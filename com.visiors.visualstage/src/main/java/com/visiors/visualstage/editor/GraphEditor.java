package com.visiors.visualstage.editor;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.constants.XMLConstants;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.listener.GraphDocumentListener;
import com.visiors.visualstage.exception.DocumentExistsException;
import com.visiors.visualstage.exception.DocumentNotFoundException;
import com.visiors.visualstage.exception.DocumentSaveException;
import com.visiors.visualstage.export.XMLService;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.GroupingHandler;
import com.visiors.visualstage.handler.SelectionHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.pool.FormatCollection;
import com.visiors.visualstage.pool.ShapeCollection;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.stage.StageDesigner;
import com.visiors.visualstage.stage.StageDesigner.ViewMode;
import com.visiors.visualstage.tool.ToolManager;
import com.visiors.visualstage.util.PropertyUtil;

public class GraphEditor implements Editor, GraphDocumentListener {

	protected GraphDocument activeDocument;
	protected final Injector injector;
	protected final ToolManager toolManager;
	protected final UndoRedoHandler undoRedoHandler;
	protected final SelectionHandler selectionHandler;
	protected final GroupingHandler groupingHandler;
	protected final StageDesigner stageDesigner;
	protected final ClipboardHandler clipboardHandler;
	protected final ShapeCollection shapeCollection;
	protected final FormatCollection formatCollection;
	private final Map<String, GraphDocument> documents = Maps.newTreeMap();
	private final Set<Canvas> canvases = Sets.newHashSet();



	public GraphEditor() {

		this(new GraphBindingModule());
	}

	public GraphEditor(BindingModule module) {

		this.injector = Guice.createInjector(module);
		this.toolManager = injector.getInstance(ToolManager.class);
		this.stageDesigner = injector.getInstance(StageDesigner.class);
		this.undoRedoHandler = injector.getInstance(UndoRedoHandler.class);
		this.selectionHandler = injector.getInstance(SelectionHandler.class);
		this.groupingHandler = injector.getInstance(GroupingHandler.class);
		this.clipboardHandler = injector.getInstance(ClipboardHandler.class);
		this.shapeCollection = injector.getInstance(ShapeCollection.class);
		this.formatCollection = injector.getInstance(FormatCollection.class);

		injector.injectMembers(toolManager);
	}

	@Override
	public GraphDocument newDocument(String title) throws DocumentExistsException {

		if (documents.containsKey(title)) {
			throw new DocumentExistsException("A document with the name '" + title + "' already exists!");
		}
		return  createDocumentInstance(title);
	}

	@Override
	public GraphDocument loadDocument(String content) {

		final XMLService xmlService = new XMLService();
		final PropertyList properties = xmlService.XML2PropertyList(content);
		final PropertyList documentProperties = (PropertyList) properties
				.get(PropertyConstants.DOCUMENT_PROPERTY_SETTING);
		final PropertyList graphObjectProperties = (PropertyList) properties
				.get(PropertyConstants.DOCUMENT_PROPERTY_GRAPH);
		String name = (String) PropertyUtil.getProperty(documentProperties, PropertyConstants.NODE_PROPERTY_NAME);
		if (documents.containsKey(name)) {
			throw new DocumentExistsException("A document with the name '" + name + "' already exists!");
		}
		final GraphDocument document = createDocumentInstance(name);		
		document.setProperties(documentProperties);
		document.getGraph().setProperties(graphObjectProperties);
		return document;

	}

	GraphDocument createDocumentInstance(String title){
		final GraphDocument document = injector.getInstance(GraphDocument.class);
		document.setCanvasSet(canvases);
		applyDefaultConfiguration(document);
		document.addGraphDocumentListener(this);
		document.setTitle(title);
		documents.put(document.getTitle(), document);
		setActiveDocument(title);
		return document;
	}

	protected void applyDefaultConfiguration(GraphDocument document) {

		stageDesigner.setViewMode(ViewMode.page);
		stageDesigner.showGrid(false);
		stageDesigner.showRuler(false);
	}

	@Override
	public boolean closeDocument(String title) {
		if (!documents.containsKey(title)) {
			throw new DocumentExistsException("A document with the name '" + title + "' could not be found!");
		}
		GraphDocument document;
		if(documents.size() == 1){
			setActiveDocument(null);
			document = documents.remove(title);
		}else{
			List<GraphDocument> existingDocuments = getDocuments();
			document = documents.remove(title);
			int docIndex = existingDocuments.indexOf(document);
			if(docIndex < existingDocuments.size()-1) {
				setActiveDocument(existingDocuments.get(docIndex+1).getTitle());
			} else {
				setActiveDocument(existingDocuments.get(docIndex-1).getTitle());
			}

		}
		document.removeGraphDocumentListener(this);
		return true;
	}

	@Override
	public void addCanvas(Canvas canvas) {

		this.canvases .add(canvas);

	}

	@Override
	public String saveDocument(String title) {

		final GraphDocument document = getDocument(title);
		try {
			final XMLService xmlService = new XMLService();
			final PropertyList properties = new DefaultPropertyList(XMLConstants.DOCUMENT_PROPERTY_DOCUMENT);
			final PropertyList documentProperties = document.getProperties();
			final PropertyList graphProperties = new DefaultPropertyList(PropertyConstants.DOCUMENT_PROPERTY_GRAPH);
			graphProperties.add(document.getGraph().getProperties(true));
			properties.add(documentProperties);
			properties.add(graphProperties);
			return xmlService.propertyList2XML(documentProperties, true);
		} catch (IOException e) {
			throw new DocumentSaveException("Document could not be saved!", e);
		}
	}

	@Override
	public void setActiveDocument(String title) {

		activeDocument = title == null ? null: getDocument(title);
		if(activeDocument != null){ // TODO when activeDocument == null (last document closed) scope must be set on null
			toolManager.setScope(activeDocument);
			clipboardHandler.setScope(activeDocument);
			selectionHandler.setScope(activeDocument);
			groupingHandler.setScope(activeDocument);
			stageDesigner.setScope(activeDocument);
		}
	}

	@Override
	public GraphDocument getDocument(String title) {

		if (!documents.containsKey(title)) {
			throw new DocumentNotFoundException("A document with the name '" + title + "' could not be found!");
		}
		return documents.get(title);
	}


	@Override
	public void printDocument(String title) {

		final GraphDocument document = getDocument(title);
		//document.print(canvas, rPage, transform);
	}

	@Override
	public void renameDocument(String currentTitle, String newTitle) throws DocumentExistsException {

		final GraphDocument document = getDocument(currentTitle);
		document.setTitle(currentTitle);
	}


	@Override
	public void update() {
		GraphDocument doc = getActiveDocument();
		if(doc != null) {
			doc.update();
		}	
	}

	@Override
	public GraphDocument getActiveDocument() {

		return activeDocument;
	}

	public List<GraphDocument> getDocuments() {

		return new ArrayList<GraphDocument>(documents.values());
	}


	@Override
	public ShapeCollection getShapesCollection() {

		return shapeCollection;
	}

	@Override
	public FormatCollection getFormatsCollection() {

		return formatCollection;
	}


	@Override
	public ClipboardHandler getClipboardHandler() {

		return clipboardHandler;
	}

	@Override
	public SelectionHandler getSelectionHandler() {

		return selectionHandler;
	}

	@Override
	public StageDesigner getStageDesigner() {

		return stageDesigner;
	}

	@Override
	public GroupingHandler getGroupingHandler() {

		return groupingHandler;
	}

	@Override
	public UndoRedoHandler getUndoRedoHandler() {

		return undoRedoHandler;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		throw new CloneNotSupportedException();
	}


	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		return toolManager.keyPressed(keyChar, keyCode);

	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		return toolManager.keyReleased(keyChar, keyCode);
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return toolManager.mouseDoubleClicked(pt, button, functionKey);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		return toolManager.mouseDragged(pt, button, functionKey);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return toolManager.mouseMoved(pt, button, functionKey);
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		return toolManager.mousePressed(pt, button, functionKey);
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return toolManager.mouseReleased(pt, button, functionKey);
	}

	@Override
	public boolean isInteracting() {

		return toolManager.isInteracting();
	}

	@Override
	public void cancelInteraction() {

		toolManager.cancelInteraction();
	}

	@Override
	public void terminateInteraction() {

		toolManager.cancelInteraction();
	}

	@Override
	public int getPreferredCursor() {

		return toolManager.getPreferredCursor();
	}


	@Override
	public void undoStackModified() {

		// TODO Auto-generated method stub

	}


	@Override
	public void graphExpansionChanged(Rectangle newBoundary) {

		fireBoundaryChanged(newBoundary);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	///// Listener
	Set<EditorListener> listeners = new HashSet<EditorListener>();
	@Override
	public void addEditorListener(EditorListener editorListener) {

		listeners.add(editorListener);
	}

	@Override
	public void removeEditorListener(EditorListener editorListener) {

		listeners.remove(editorListener);		
	}


	protected void fireBoundaryChanged(Rectangle newBoundary) {

		for (EditorListener listener : listeners) {
			listener.boundaryChangedListener(newBoundary);
		}
	}


}
