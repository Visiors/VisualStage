package com.visiors.visualstage.document.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.ViewListener;
import com.visiors.visualstage.document.layer.Layer;
import com.visiors.visualstage.document.layer.LayerManager;
import com.visiors.visualstage.document.listener.GraphDocumentListener;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.editor.Editor;
import com.visiors.visualstage.exception.InvalidLayerRemovalException;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewAdapter;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.PropertyBinder;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.stage.StageDesigner;
import com.visiors.visualstage.svg.DocumentConfig;
import com.visiors.visualstage.svg.SVGDocumentBuilder;
import com.visiors.visualstage.svg.SVGDocumentConfig;
import com.visiors.visualstage.svg.SVGUtil;
import com.visiors.visualstage.tool.ToolManager;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.validation.Validator;

/**
 * This class contains all components that are shared between all layers
 * 
 * 
 * 
 */
public class DefaultGraphDocument implements GraphDocument {

	// private boolean fireEvents = true;

	private String title;
	private boolean doDrawing = true;
	private PropertyList properties;
	private boolean useImageCaching = true;

	private PropertyBinder propertyBinder;
	protected LayerManager layerManager;
	@Inject
	protected StageDesigner stageDesigner;
	@Inject
	protected ToolManager toolManager;
	@Inject
	SVGDocumentBuilder svgDocumentBuilder;
	@Inject
	protected UndoRedoHandler undoRedoHandler;
	@Inject
	protected Validator validator;

	protected Transform transform;

	private String svgBackground;
	private String svgFilter;
	private String svgTransformation;
	private Editor editor;
	private final Dimension viewPortSize = new Dimension();

	@Inject
	public DefaultGraphDocument(LayerManager layerManager, Transform transform) {

		this.layerManager = layerManager;
		this.transform = transform;
		DI.injectMembers(this);
		addLayer(0);
		stageDesigner.addViewListener(viewListener);
	}

	@Override
	public String getTitle() {

		return title;
	}

	@Override
	public void setTitle(String title) {

		this.title = title;
	}

	@Override
	public void setEditor(Editor editor) {

		this.editor = editor;
	}

	@Override
	public Editor getEditor() {

		return editor;
	}

	@PostConstruct
	protected void initProperties() {

		// create the properties definition
		final PropertyList properties = new DefaultPropertyList(PropertyConstants.DOCUMENT_PROPERTY_PREFIX);
		// properties.add(new
		// DefaultPropertyUnit(PropertyConstants.DOCUMENT_PROPERTY_ZOOM,
		// getZoom()));
		// properties.add(new
		// DefaultPropertyUnit(PropertyConstants.DOCUMENT_PROPERTY_X_SCROLL,
		// getXScrollPosition()));
		// properties.add(new
		// DefaultPropertyUnit(PropertyConstants.DOCUMENT_PROPERTY_Y_SCROLL,
		// getYScrollPosition()));

		propertyBinder = new PropertyBinder(this);
	}

	@Override
	public PropertyList getProperties() {

		return properties;
	}

	@Override
	public void setProperties(PropertyList properties) {

		this.properties = properties;
	}

	@Override
	public double getZoom() {

		return transform.getScale();
	}

	@Override
	public void setZoom(double value) {

		if (transform.getScale() != value) {
			transform.setScale(Math.min(10.0, Math.max(0.05, value)), true);
			fireViewInvalid();
		}
	}

	@Override
	public void setViewportPos(int x, int y) {

		if (transform.getXTranslate() != x || transform.getYTranslate() != y) {
			transform.setXTranslate(x);
			transform.setYTranslate(y);
			fireViewInvalid();
		}
	}

	@Override
	public Point getViewportPos() {

		return new Point((int) transform.getXTranslate(), (int) transform.getYTranslate());
	}

	@Override
	public void setViewportSize(int w, int h) {

		if (viewPortSize.width != w || viewPortSize.height != h) {
			viewPortSize.width = w;
			viewPortSize.height = h;
			invalidate();
		}
	}

	@Override
	public Dimension getViewportSize() {

		return new Dimension(viewPortSize);
	}

	@Override
	public Rectangle getClientBoundary() {

		return new Rectangle(transform.getViewX(), transform.getViewY(), transform.getViewWidth(),
				transform.getViewHeight());
	}

	@Override
	public void setActiveLayer(int id) {

		final Layer previousActiveLayer = layerManager.getSelectedLayer();
		if (previousActiveLayer != null) {
			final VisualGraph visualGraph = previousActiveLayer.getVisualGraph();
			visualGraph.removeGraphViewListener(graphViewListener);
		}
		final Layer newActivelayer = layerManager.selectLayer(id);
		final VisualGraph visualGraph = newActivelayer.getVisualGraph();
		visualGraph.setTransformer(transform);
		visualGraph.addGraphViewListener(graphViewListener);
	}

	@Override
	public Layer getCurrentLayer() {

		return layerManager.getSelectedLayer();
	}

	@Override
	public Layer addLayer(int id) {

		final Layer newLayer = layerManager.addLayer(id);
		setActiveLayer(id);
		return newLayer;
	}

	@Override
	public void removeLayer(int id) {

		if (layerManager.getLayerCount() == 0) {
			throw new InvalidLayerRemovalException("No layer available!");
		}
		if (layerManager.getLayerCount() == 1) {
			throw new InvalidLayerRemovalException(
					"The layer cannot be removed because the document must have at least one layer!");
		}

		final Layer layerToRemove = layerManager.getLayer(id);
		final List<Layer> allLayers = layerManager.getLayers();

		for (int i = 0, len = allLayers.size(); i < len; i++) {
			if (allLayers.get(i).getID() == layerToRemove.getID()) {
				if (i < len - 1) {
					setActiveLayer(allLayers.get(i + 1).getID());
				} else {
					setActiveLayer(allLayers.get(i - 1).getID());
				}
			}
			layerManager.removeLayer(id);
		}
	}

	@Override
	public List<Layer> getLayers() {

		return layerManager.getLayers();
	}

	@Override
	public Layer getLayer(int id) {

		return layerManager.getLayer(id);
	}

	@Override
	public int getLayerCount() {

		return layerManager.getLayerCount();
	}

	@Override
	public ToolManager getToolManager() {

		return toolManager;
	}

	@Override
	public VisualGraph getGraph() {

		final Layer currentLayer = getCurrentLayer();
		return currentLayer.getVisualGraph();
	}

	@Override
	public void useImageBuffering(boolean enable) {

		useImageCaching = enable;
	}

	@Override
	public void enableDrawing(boolean doDrawing) {

		this.doDrawing = doDrawing;
		if (doDrawing) {
			invalidate();
		}
	}

	@Override
	public void invalidate() {

		final int rulerSize = stageDesigner.getRulerSize();
		final int scrollBarSize = stageDesigner.getScrollBarSize();
		transform.setViewportX(rulerSize);
		transform.setViewportY(rulerSize);
		transform.setViewWidth(viewPortSize.width - rulerSize - scrollBarSize);
		transform.setViewHeight(viewPortSize.height - rulerSize - scrollBarSize);

		fireViewInvalid();
	}

	@Override
	public void setTransformer(Transform transform) {

		this.transform = transform;

	}

	@Override
	public Transform getTransformer() {

		return transform;
	}

	// /////////////////////////////////////////////////

	@Override
	public synchronized Image getScreen(DrawingContext context) {

		final Rectangle rClient = getClientBoundary();

		if (rClient.isEmpty()) {
			System.err.println("Warning: view port is empty!");
			return new AWTCanvas(1, 1).image;
		}

		final AWTCanvas awtCanvas = createCanvas();
		// - keep always the main graph view fit to the screen
		resizeGraph(rClient);
		// - paint the background items behind the graph
		stageDesigner.drawHints(awtCanvas, context, false);
		toolManager.drawHints(awtCanvas, context, false);
		// - paint the graph
		drawGraph(awtCanvas, context);
		// - paint items on the top of graph
		toolManager.drawHints(awtCanvas, context, true);
		stageDesigner.drawHints(awtCanvas, context, true);
		// - draw frame
		drawFrame(awtCanvas.gfx);
		return awtCanvas.image;
	}

	private void resizeGraph(Rectangle rClientBounds) {

		final VisualGraph graph = getGraph();
		graph.setBounds(rClientBounds);
	}

	private void drawGraph(AWTCanvas awtCanvas, DrawingContext context) {

		final VisualGraph graph = getGraph();
		if (useImageCaching) {
			graph.draw(awtCanvas, context, DrawingSubject.OBJECT);
			graph.draw(awtCanvas, context, DrawingSubject.SELECTION_INDICATORS);
			graph.draw(awtCanvas, context, DrawingSubject.PORTS);
		} 
		else
		{
			final Image img = getImage(context, null, computeClipRect());
			awtCanvas.gfx.drawImage(img, 0, 0, null);
		}
	}

	private AWTCanvas createCanvas() {

		final Dimension canvasSize = getViewportSize();
		return new AWTCanvas(canvasSize.width, canvasSize.height);
	}

	private void drawFrame(Graphics2D gfx) {

		// awtCanvas.gfx.setStroke(new BasicStroke(1f));
		// awtCanvas.gfx.setColor(Color.orange);
		// awtCanvas.gfx.drawRect(rClientBounds.x, rClientBounds.y,
		// rClientBounds.width-1, rClientBounds.height-1);
		// awtCanvas.gfx.setColor(Color.red);
		// awtCanvas.gfx.drawRect(0, 0, canvasSize.width-1,
		// canvasSize.height-1);
	}

	private Rectangle computeDocumentBoundary() {

		final Transform xform = getTransformer();
		final Rectangle clipRect = getGraph().getExtendedBoundary();
		final Rectangle clientBounds = getClientBoundary();
		clipRect.x -= xform.getXTranslate() + clientBounds.x;
		clipRect.y -= xform.getYTranslate() + clientBounds.y;
		clipRect.width += clientBounds.x;
		clipRect.height += clientBounds.y;
		return clipRect;
	}

	private Rectangle computeClipRect() {

		final Rectangle clientArea = getClientBoundary();
		final Transform xform = getTransformer();

		clientArea.width += clientArea.x;
		clientArea.height += clientArea.y;
		clientArea.x = (int) (-xform.getXTranslate());
		clientArea.y = (int) (-xform.getYTranslate());
		return clientArea;
	}

	@Override
	public Image getImage(DrawingContext context) {

		final DocumentConfig config = new SVGDocumentConfig(svgBackground, svgFilter, svgTransformation);
		return getImage(context, config, computeDocumentBoundary());
	}

	private Image getImage(DrawingContext context, DocumentConfig config, Rectangle clipRect) {

		// pack everything in context. getSVGDocument will have only one
		// parameter
		final String svgDocument = getSVGDocument(context, config, clipRect, context.getDrawingSubject());
		return SVGUtil.svgToImage(svgDocument);
	}

	@Override
	public void print(Canvas canvas, Rectangle rPage, Transform printTransformer) {

		// final VisualGraph visualGraph = getGraph();
		// final Transform viewTransform = visualGraph.getTransformer();
		// visualGraph.setTransformer(printTransformer);
		// try {
		// final DrawingContext context = new
		// DrawingContext(DrawingContext.Resolution.SCREEN, Subject.OBJECT,
		// false);
		// context.subject = Subject.OBJECT;
		//
		// // context.clippingArea = transform.toGraph(rPage);
		//
		// visualGraph.drawContent(canvas, context);
		//
		// } finally {
		//
		// visualGraph.setTransformer(viewTransform);
		// }

	}

	protected String getSVGDocument(DrawingContext context, DocumentConfig config, Rectangle clippingRect,
			DrawingSubject... subject) {

		final VisualGraph graph = getGraph();

		svgDocumentBuilder.createEmptyDocument(clippingRect, null, config);
		for (final DrawingSubject drawingSubject : subject) {
			String description = graph.getViewDescriptor(context.getResolution(), drawingSubject);
			if(!Strings.isNullOrEmpty(description)) {
				svgDocumentBuilder.addContent(description);
			}
		}
		svgDocumentBuilder.finlaizeDocument();
		return svgDocumentBuilder.getDocument();
	}

	@Override
	public String getSVGDocument(DrawingContext context) {

		final DocumentConfig config = new SVGDocumentConfig(svgBackground, svgFilter, svgTransformation);
		return getSVGDocument(context, config, computeDocumentBoundary(), DrawingSubject.OBJECT);
	}

	@Override
	public void setSvgBackground(String svgBackgroundId) {

		this.svgBackground = svgBackgroundId;
		// getGraph().setBackground(svgBackgroundId);
		invalidate();
	}

	@Override
	public void setSvgFilter(String svgFilterId) {

		this.svgFilter = svgFilterId;
		// interactionHandler.setFilter(svgFilterId);
		invalidate();
	}

	@Override
	public void setSvgTransformation(String svgTransformId) {

		this.svgTransformation = svgTransformId;
		// interactionHandler.svgTransformID(svgTransformId);
		invalidate();
	}

	@Override
	public String toString() {

		return getTitle();
	}

	// //////////////////////////////////////////////////////////////
	// ////// Events

	@Override
	public boolean isDrawingEnabled() {

		return doDrawing;
	}

	GraphViewListener graphViewListener = new GraphViewAdapter() {

		@Override
		public void viewInvalid(VisualGraph graph) {

			invalidate();
		}

		@Override
		public void graphExpansionChanged(VisualGraph graph) {

			fireGraphExpansionChanged();
		}

	};

	ViewListener viewListener = new ViewListener() {

		@Override
		public void viewModeChanged() {

			fireGraphExpansionChanged();
		}
	};

	@Override
	public void addGraphViewListener(GraphViewListener listener) {

		getGraph().addGraphViewListener(listener);
	}

	@Override
	public void removeGraphViewListener(GraphViewListener listener) {

		getGraph().removeGraphViewListener(listener);
	}

	protected List<GraphDocumentListener> graphDocumentListener = new ArrayList<GraphDocumentListener>();

	@Override
	public void addGraphDocumentListener(GraphDocumentListener listener) {

		if (!graphDocumentListener.contains(listener)) {
			graphDocumentListener.add(listener);
		}
	}

	@Override
	public void removeGraphDocumentListener(GraphDocumentListener listener) {

		graphDocumentListener.remove(listener);
	}

	protected void fireViewInvalid() {

		for (final GraphDocumentListener l : graphDocumentListener) {
			l.viewInvalid(this);
		}
	}

	protected void fireGraphExpansionChanged() {

		for (final GraphDocumentListener l : graphDocumentListener) {
			l.graphExpansionChanged();
		}
	}

	@Override
	public Validator getValidator() {

		return validator;
	}

	// /////////////////////////////////////////////////////////////////
	// // Redo / Undo

	// private static final String SELECTION_CHANGED = "selection changed";
	// private static final String GROUP = "group";
	// private static final String UNGROUP = "ungroup";

	@Override
	public void undo(Object data) {

		// PropertyList pl = (PropertyList) data;
		//
		// if (GROUP.equals(pl.getName())) {
		// groupSelection(pl, true);
		// } else if (UNGROUP.equals(pl.getName())) {
		// }

	}

	@Override
	public void redo(Object data) {

		// PropertyList pl = (PropertyList) data;
		//
		// if (GROUP.equals(pl.getName())) {
		// groupSelection(pl, false);
		// } else if (UNGROUP.equals(pl.getName())) {
		// }
	}

	//
	// void registerGroupingSelection(String graphviewToUse){
	// if (undoService.undoRedoInProcess())
	// return;
	// PropertyList data = new DefaultPropertyList(GROUP);
	// data.add(new DefaultPropertyUnit("name", graphviewToUse));
	// undoService.registerAction(this, data);
	// }
	//
	// private void groupSelection(PropertyList pl, boolean undo) {
	// if(undo){
	// ungroupSelsection();
	// }
	// else{
	// PropertyUnit p = (PropertyUnit) pl.get("name");
	// String graphviewToUse = ConvertUtil.object2string(p.getValue());
	// groupSelection(graphviewToUse);
	// }
	// }

}
