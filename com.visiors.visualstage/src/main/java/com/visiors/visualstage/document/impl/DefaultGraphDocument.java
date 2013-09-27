package com.visiors.visualstage.document.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.google.inject.Inject;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.ViewListener;
import com.visiors.visualstage.document.layer.Layer;
import com.visiors.visualstage.document.layer.LayerManager;
import com.visiors.visualstage.document.listener.GraphDocumentListener;
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
import com.visiors.visualstage.stage.StageDesigner.ViewMode;
import com.visiors.visualstage.svg.DocumentConfig;
import com.visiors.visualstage.svg.SVGDocumentBuilder;
import com.visiors.visualstage.svg.SVGDocumentConfig;
import com.visiors.visualstage.svg.SVGUtil;
import com.visiors.visualstage.tool.ToolManager;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.validation.Validator;

/**
 * This class contains all componets that are shared between all layers
 * 
 * 
 * 
 */
public class DefaultGraphDocument implements GraphDocument {

	// private boolean fireEvents = true;

	private String title;
	private boolean doDrawing = true;
	private PropertyList properties;
	private boolean useImageCaching;

	protected LayerManager layerManager;

	private PropertyBinder propertyBinder;
	protected StageDesigner stageDesigner;
	protected final ToolManager toolManager;
	@Inject
	SVGDocumentBuilder svgDocumentBuilder;
	protected UndoRedoHandler undoRedoHandler;

	protected Validator validator;

	private String svgBackground;
	private String svgFilter;
	private String svgTransformation;

	@Inject
	public DefaultGraphDocument(LayerManager layerManager, StageDesigner stageDesigner, ToolManager toolManager,
			Validator validator) {

		this.stageDesigner = stageDesigner;
		this.toolManager = toolManager;
		this.validator = validator;
		this.layerManager = layerManager;

		stageDesigner.addViewListener(viewListener);
		addLayer(0);
	}

	@Override
	public String getTitle() {

		return title;
	}

	@Override
	public void setTitle(String title) {

		this.title = title;
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
	public void setActiveLayer(int id) {

		final Layer previousActiveLayer = layerManager.getSelectedLayer();
		if (previousActiveLayer != null) {
			final VisualGraph visualGraph = previousActiveLayer.getVisualGraph();
			visualGraph.removeGraphViewListener(graphViewListener);
		}
		final Layer newActivelayer = layerManager.selectLayer(id);
		final VisualGraph visualGraph = newActivelayer.getVisualGraph();
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

		fireViewInvalid();
	}

	// /////////////////////////////////////////////////

	@Override
	public synchronized Image getScreen(DrawingContext context) {

		final Rectangle viewport = context.getViewport();
		final AWTCanvas awtCanvas = new AWTCanvas((int) viewport.getWidth(), (int) viewport.getHeight());

		if (viewport == null || viewport.isEmpty()) {
			System.err.println("Warning: Cannot draw on the canvas with zero width/heigt!");
			return awtCanvas.image;
		}

		final VisualGraph graph = getGraph();
		// keep always the main graph view fit to the screen
		graph.setBounds(viewport);

		toolManager.drawHints(awtCanvas, context, false);
		stageDesigner.paintBehind(awtCanvas, context);
		if (useImageCaching) {
			graph.draw(awtCanvas, context, DrawingSubject.OBJECT);
			graph.draw(awtCanvas, context, DrawingSubject.SELECTION_INDICATORS);
			graph.draw(awtCanvas, context, DrawingSubject.PORTS);
		} else {
			awtCanvas.gfx.drawImage(getImage(context, null), 0, 0, null);
		}
		stageDesigner.paintOver(awtCanvas, context);
		toolManager.drawHints(awtCanvas, context, true);

		awtCanvas.gfx.setStroke(new BasicStroke(1f));
		awtCanvas.gfx.setColor(Color.blue);
		awtCanvas.gfx.drawRect(2, 2, (int) viewport.getWidth() - 4, (int) viewport.getHeight() - 4);
		return awtCanvas.image;
	}

	@Override
	public Image getImage(DrawingContext context) {

		final DocumentConfig config = new SVGDocumentConfig(svgBackground, svgFilter, svgTransformation);
		return getImage(context, config);
	}

	private Image getImage(DrawingContext context, DocumentConfig config) {

		final DrawingSubject[] subjects = new DrawingSubject[] { DrawingSubject.OBJECT,
				DrawingSubject.SELECTION_INDICATORS, DrawingSubject.PORTS };
		final String svgDocument = getSVGDocument(context, config, subjects);
		final Image image = SVGUtil.svgToImage(svgDocument);
		return image;
	}

	@Override
	public Rectangle getDocumentBoundary() {

		Rectangle r;
		if (stageDesigner.getViewMode() == ViewMode.page) {
			r = stageDesigner.getDocumentBoundary();
			r.grow(0, 50);
		} else {
			r = getGraph().getExtendedBoundary();
		}

		// r.grow(MARGIN, MARGIN);

		if (stageDesigner.isRulerVisible()) {
			final int rulerSize = stageDesigner.getRulerSize();
			r.x -= rulerSize;
			r.y -= rulerSize;
			r.width += rulerSize;
			r.height += rulerSize;
		}
		return r;
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

	protected String getSVGDocument(DrawingContext context, DocumentConfig config, DrawingSubject... subject) {

		final VisualGraph graph = getGraph();
		Rectangle boundary = graph.getBounds();
		final Rectangle viewport = new Rectangle(-context.getViewport().x, -context.getViewport().y, boundary.width,
				boundary.height);
		Transform xform = graph.getTransformer();

		svgDocumentBuilder.createEmptyDocument(viewport, xform, config);

		for (DrawingSubject drawingSubject : subject) {
			svgDocumentBuilder.addContent(graph.getViewDescriptor(context.getResolution(), drawingSubject));
		}
		svgDocumentBuilder.finlaizeDocument();
		return svgDocumentBuilder.getDocument();
	}

	@Override
	public String getSVGDocument(DrawingContext context) {

		final DocumentConfig config = new SVGDocumentConfig(svgBackground, svgFilter, svgTransformation);
		return getSVGDocument(context, config, DrawingSubject.OBJECT);
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
		public void graphExpansionChanged(VisualGraph graph, Rectangle newExpansion) {

			fireGraphExpansionChanged(newExpansion);
		}

	};

	ViewListener viewListener = new ViewListener() {

		@Override
		public void viewModeChanged() {

			fireGraphExpansionChanged(getDocumentBoundary());
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

	protected void fireGraphExpansionChanged(Rectangle r) {

		for (final GraphDocumentListener l : graphDocumentListener) {
			l.graphExpansionChanged(r);
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
