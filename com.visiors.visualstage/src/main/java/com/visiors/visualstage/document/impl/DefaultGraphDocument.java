package com.visiors.visualstage.document.impl;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.google.inject.Inject;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.ViewListener;
import com.visiors.visualstage.document.listener.GraphDocumentListener;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewAdapter;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.renderer.Device;
import com.visiors.visualstage.renderer.RenderingContext;
import com.visiors.visualstage.renderer.RenderingContext.Resolution;
import com.visiors.visualstage.renderer.RenderingContext.Subject;
import com.visiors.visualstage.stage.layer.Layer;
import com.visiors.visualstage.stage.layer.impl.DefaultMultiLayerEditor;
import com.visiors.visualstage.stage.ruler.StageDesigner;
import com.visiors.visualstage.stage.ruler.StageDesigner.ViewMode;
import com.visiors.visualstage.transform.Transformer;
import com.visiors.visualstage.util.PropertyUtil;
import com.visiors.visualstage.validation.Validator;

/**
 * This class contains all componets that are shared between all layers
 * 

 * 
 */
public class DefaultGraphDocument implements GraphDocument {

	private boolean fireEvents = true;
	private boolean updateView;
	private PropertyList properties;
	private boolean enableImageBuffering;



	protected DefaultMultiLayerEditor layerManager;

	private  String title;


	@Inject
	protected StageDesigner stageDesigner;
	@Inject
	protected UndoRedoHandler undoRedoHandler;
	@Inject
	protected Validator validator;



	public DefaultGraphDocument(String title) {


		this.title = title;
		layerManager = new DefaultMultiLayerEditor();
		stageDesigner.addViewListener(viewListener);


		// create components shared by all graph editors
		createNewLayer(0);

		updateView = true;
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

		// PropertyList pl = interactionHandler.getProperties();
		// pl = PropertyUtil.setProperty(pl,
		// PropertyConstants.GRAPH_PROPERTY_INNER_MARGIN, 2);
		// pl = PropertyUtil.setProperty(pl,
		// PropertyConstants.GRAPH_PROPERTY_FIT_TO_CONTENT, false);
		// pl = PropertyUtil.setProperty(pl,
		// PropertyConstants.GRAPH_PROPERTY_CONTENT_SELECTABEL, true);
		// pl = PropertyUtil.setProperty(pl,
		// PropertyConstants.GRAPH_PROPERTY_CONTENT_MOVABLE, true);
		// PropertyUtil.makeVisible(pl,
		// PropertyConstants.GRAPH_PROPERTY_FIT_TO_CONTENT, false);
		// interactionHandler.setProperties(pl);
	}

	@Override
	public PropertyList getProperties() {

		properties = PropertyUtil.setProperty(properties, PropertyConstants.DOCUMENT_PROPERTY_ZOOM, 100);
		properties = PropertyUtil.setProperty(properties, PropertyConstants.DOCUMENT_PROPERTY_X_SCROLL, 0);
		properties = PropertyUtil.setProperty(properties, PropertyConstants.DOCUMENT_PROPERTY_Y_SCROLL, 0);
		return properties.deepCopy();
	}

	@Override
	public void setProperties(PropertyList properties) {

		this.properties = properties;
	}

	//
	// @Override
	// public long getID() {
	//
	// return ??;
	// }

	@Override
	public void selectLayer(int id) {

		layerManager.selectLayer(id);
	}

	@Override
	public Layer getCurrentLayer() {

		return layerManager.getSelectedLayer();
	}

	@Override
	public Layer createNewLayer(int id) {

		VisualGraph graphViewer = injector.getInstance(VisualGraph.class);

		graphViewer.addGraphViewListener(graphViewListener);

		graphViewer.fireEvents(fireEvents);

		return layerManager.addLayer(id, graphViewer);
	}

	@Override
	public Layer getLayer(int id) {

		return layerManager.getLayer(id);
	}

	@Override
	public void removeLayer(int id) {

		layerManager.removeLayer(id);
	}

	@Override
	public Layer[] getLayers() {

		return layerManager.getLayers();
	}

	@Override
	public VisualGraph getGraph() {

		final Layer currentLayer = getCurrentLayer();
		return currentLayer.getGraphView();
	}

	@Override
	public void enableImageBuffering(boolean enable) {

		enableImageBuffering = enable;
	}





	// ///////////////////////////////
	// notifiying interaction listener
	//
	// protected List<InteractionClientListener> interactionListener = new
	// ArrayList<InteractionClientListener>();
	//
	//
	// @Override
	// public void addInteractionListener(InteractionClientListener listener) {
	// if (!interactionListener.contains(listener))
	// interactionListener.add(listener);
	// }
	// @Override
	// public void removeInteractionListener(InteractionClientListener listener)
	// {
	//
	// interactionListener.remove(listener);
	// }

	//
	// @Override
	// public void interactionModeChanged(int oldMode, int newMode) {
	// for (InteractionClientListener l : interactionListener) {
	// l.interactionModeChanged(oldMode, newMode);
	// }
	// fireViewChanged();
	// }

	// /////////////////////////////////////////////////

	@Override
	public void render(Device device, Rectangle visibleScreenRect, Resolution resolution) {

		if (!updateView) {
			return;
		}

		// context.clippingArea = transform.toGraph(visibleScreenRect);

		stageDesigner.paintBackground(device, visibleScreenRect, resolution);

		final VisualGraph gv = getGraph();
		// keep always the main graph view fit to the screen
		gv.setBounds(visibleScreenRect);
		gv.enableImageBuffering(enableImageBuffering);


		RenderingContext context = new RenderingContext(resolution, Subject.OBJECT, true);
		gv.drawContent(device, context);

		stageDesigner.paintOnTop(device, visibleScreenRect, resolution);
	}

	@Override
	public String getSVGDocument(Device device, RenderingContext context, double scale) {

		context.subject = Subject.OBJECT;
		return getGraph().getSVGDocument(device, context, false, scale);
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
			int rulerSize = stageDesigner.getRulerSize();
			r.x -= rulerSize;
			r.y -= rulerSize;
			r.width += rulerSize;
			r.height += rulerSize;
		}
		return r;
	}

	@Override
	public void print(Device device, Rectangle rPage, Transformer t) {

		Transformer currentTransform = getGraph().getTransform();
		getGraph().setTransform(t);
		try {
			RenderingContext context = new RenderingContext(RenderingContext.Resolution.SCREEN, Subject.OBJECT, false);
			context.subject = Subject.OBJECT;

			// context.clippingArea = transform.toGraph(rPage);

			getGraph().drawContent(device, context);

		} finally {

			getGraph().setTransform(currentTransform);
		}

	}

	@Override
	public StageDesigner getStageDesigner() {

		return stageDesigner;
	}

	@Override
	public void registerInplaceTextEditor(InplaceTextditor editor) {

		InplaceEdtiorService.registerEditor(editor);
	}

	@Override
	public void setBackground(String svgBackgroundID) {

		getGraph().setBackground(svgBackgroundID);
		fireViewChanged();
	}

	@Override
	public void setFilter(String svgFilterID) {

		interactionHandler.setFilter(svgFilterID);
		fireViewChanged();
	}

	@Override
	public void setTransformation(String svgTransformID) {

		interactionHandler.svgTransformID(svgTransformID);
		fireViewChanged();
	}






	// //////////////////////////////////////////////////////////////
	// ////// Events

	@Override
	public void fireEvents(boolean enabled) {

		fireEvents = enabled;
		getGraph().fireEvents(enabled);
	}

	@Override
	public boolean isFiringEvents() {

		return fireEvents;
	}

	@Override
	public void setUpdateView(boolean update) {

		updateView = update;
		if (update) {
			fireViewChanged();
		}
	}

	@Override
	public boolean getUpdateView() {

		return updateView;
	}

	GraphViewListener graphViewListener = new GraphViewAdapter() {

		@Override
		public void graphManipulated(VisualGraph graph) {

			fireGraphManipulated();
		}

		@Override
		public void viewChanged(VisualGraph graph) {

			fireViewChanged();
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

	protected void fireViewChanged() {

		if (updateView) {
			for (GraphDocumentListener l : graphDocumentListener) {
				l.viewChanged();
			}
		}
	}

	protected void fireGraphManipulated() {

		if (updateView) {
			for (GraphDocumentListener l : graphDocumentListener) {
				l.graphManipulated();
			}
		}
	}

	protected void fireGraphExpansionChanged(Rectangle r) {

		for (GraphDocumentListener l : graphDocumentListener) {
			l.graphExpansionChanged(r);
		}
	}

	@Override
	public void interactionModeChanged(String previousHandler, String currnetHandler) {

		fireViewChanged();
	}

	@Override
	public Validator getValidator() {

		// TODO Auto-generated method stub
		return null;
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
