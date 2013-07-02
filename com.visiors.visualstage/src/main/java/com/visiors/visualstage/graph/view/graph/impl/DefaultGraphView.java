package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.visiors.visualstage.graph.view.Constants;
import com.visiors.visualstage.graph.view.DefaultGraphObjectView;
import com.visiors.visualstage.graph.view.GraphObjectView;
import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.edge.listener.EdgeViewListener;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.NodeView;
import com.visiors.visualstage.graph.view.node.impl.DefaultNodeView;
import com.visiors.visualstage.graph.view.node.listener.NodeViewListener;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.renderer.RenderingContext;

public class DefaultGraphView extends DefaultNodeView implements GraphView, NodeViewListener,
EdgeViewListener

{

	private final Depot depot;
	// private final GraphViewUndoHelper graphViewUndoHelper;
	private boolean movingContent;
	// private PropertyList properties;
	private final SubgraphEventMediator eventMediator;

	protected int margin = 4;
	protected boolean fitToContent = true;
	protected boolean contentSelectable = true;
	protected boolean contentMovable = true;
	protected boolean contentDeletable = true;;

	// private UndoRedoHandler undoRedoHandler;
	// private SVGDocumentBuilder svgDoc;

	// private Validator validator;

	protected DefaultGraphView(String name) {

		super(name);
	}

	protected DefaultGraphView(String name, long id) {

		super(name, id);

		depot = new Depot(this);
		// graphViewUndoHelper = new GraphViewUndoHelper(this);
		eventMediator = new SubgraphEventMediator(this);
		new GraphContentManager(this);
	}

	protected DefaultGraphView(GraphView graphView, long id) {

		super(graphView.getName(), id);

		this.setBounds(graphView.getBounds());
		this.SetAttributes(graphView.getAttributes());
		this.incomingEdges = new ArrayList<EdgeView>(graphView.getIncomingEdges());
		this.outgoingEdges = new ArrayList<EdgeView>(graphView.getOutgoingEdges());
		this.setProperties(graphView.getProperties());
		this.setStyleID(graphView.getStyleID());
		this.setPresentationID(graphView.getPresentationID());
		/* this.setFormID(node.getFormID()); */
		if (graphView.getPortSet() != null) {
			portSet = graphView.getPortSet();
		}
		if (graphView.getCustomData() != null) {
			setCustomData(graphView.getCustomData().deepCopy());
		}
	}

	@Override
	public void add(GraphObjectView... graphObjects) {

		for (GraphObjectView go : graphObjects) {
			if (go instanceof NodeView) {
				addNode((NodeView) go);
			}
		}
		for (GraphObjectView go : graphObjects) {
			if (go instanceof EdgeView) {
				addEdge((EdgeView) go);
			}
		}
	}

	@Override
	public void remove(GraphObjectView... graphObjects) {

		// undoRedoHandler.stratOfGroupAction();

		for (GraphObjectView go : graphObjects) {
			if (go instanceof NodeView) {
				removeNode((NodeView) go);
			}
		}
		for (GraphObjectView go : graphObjects) {
			if (go instanceof EdgeView) {
				removeEdge((EdgeView) go);
			}
		}
		// undoRedoHandler.endOfGroupAction();
	}

	protected void addNode(NodeView node) {

		if (depot.getObject(node.getID()) != null) {
			throw new IllegalArgumentException("The node cannot be added into the graph. "
					+ "Reason: an object with the id " + node.getID() + " already exists.");
		}

		depot.add(node);

		node.setParentGraph(this);

		node.setTransformer(transformer);

		// node.fireEvents(fireEvents);

		node.addNodeViewListener(this);

		fireNodeAdded(node);

		// graphViewUndoHelper.registerNodeCreation(node);
	}

	@Override
	public void setParentGraph(GraphView parentgraph) {

		super.setParentGraph(parentgraph);

		if (parentgraph instanceof DefaultGraphView) {
			eventMediator.setParentView((DefaultGraphView) parentgraph);
		} else {
			eventMediator.setParentView(null);
		}

		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_PARENT_ID,
		// (parentgraph != null ? parentgraph.getID() : -1));
	}

	protected void removeNode(NodeView node) {

		depot.remove(node);
		node.removeNodeViewListener(this);
		final List<EdgeView> ies = new ArrayList<EdgeView>(node.getIncomingEdges());
		for (EdgeView e : ies) {
			e.connect(e.getSourceNode(), e.getSourcePortId(), null, DefaultGraphObjectView.NONE);
		}
		final List<EdgeView> oes = new ArrayList<EdgeView>(node.getOutgoingEdges());
		for (EdgeView e : oes) {
			e.connect(null, DefaultGraphObjectView.NONE, e.getTargetNode(), e.getTargetPortId());
		}
		fireNodeRemoved(node);

		// graphViewUndoHelper.registerNodeRemoval(node);
	}

	protected void addEdge(EdgeView edge) {

		if (depot.getObject(edge.getID()) != null) {
			throw new IllegalArgumentException("The edge cannot be added into the graph. "
					+ "Reason: an object with the id " + edge.getID() + " already exists.");
		}

		depot.add(edge);
		edge.setParentGraph(this);
		edge.setTransformer(transformer);
		// edge.fireEvents(fireEvents);
		edge.addEdgeViewListener(this);
		fireEdgeAdded(edge);
		// graphViewUndoHelper.registerEdgeCreation(edge);
	}

	protected void removeEdge(EdgeView edge) {

		edge.removeEdgeViewListener(this);
		NodeView n = edge.getSourceNode();
		if (n != null) {
			n.postDisconnected(edge);
		}
		n = edge.getTargetNode();
		if (n != null) {
			n.postDisconnected(edge);
		}
		depot.remove(edge);
		// graphViewUndoHelper.registerEdgeRemoval(edge);
		fireEdgeRemoved(edge);
	}

	@Override
	public EdgeView getEdge(long id) {

		return (EdgeView) depot.getObject(id);
	}

	@Override
	public NodeView getNode(long id) {

		return (NodeView) depot.getObject(id);
	}

	@Override
	public List<GraphObjectView> getHitObjects(Point pt) {

		return Arrays.asList(depot.getHitObjects(pt));
	}

	@Override
	public List<EdgeView> getEdges() {

		return Arrays.asList(depot.getEdges());
	}

	@Override
	public List<NodeView> getNodes() {

		return Arrays.asList(depot.getNodes());
	}

	@Override
	public List<GraphObjectView> getGraphObjects(Rectangle rect) {

		return Arrays.asList(depot.getGraphObjects(rect));
	}

	@Override
	public List<GraphObjectView> getGraphObjects() {

		return Arrays.asList(depot.getObjects());
	}

	@Override
	public void clear() {

		GraphObjectView[] objects = depot.getObjects();
		if (objects != null) {
			remove(objects);
		}
		fireGraphExpansionChanged(new Rectangle());
	}

	@Override
	public void move(int dx, int dy) {

		super.move(dx, dy);
		moveContent(dx, dy);
	}

	@Override
	public List<GraphObjectView> getSelection() {

		List<GraphObjectView> selection = new ArrayList<GraphObjectView>();
		for (GraphObjectView vo : getGraphObjects()) {
			if (vo.isSelected()) {
				selection.add(vo);
			}
		}

		return selection;
	}

	@Override
	public void setSelection(List<GraphObjectView> selection) {

		for (GraphObjectView vo : getGraphObjects()) {
			vo.setSelected(selection.contains(vo));
		}
	}

	@Override
	public void setSelection(GraphObjectView selection) {

		for (GraphObjectView vo : getGraphObjects()) {
			vo.setSelected(vo.equals(selection));
		}
	}

	@Override
	public void clearSelection() {

		for (GraphObjectView vo : getGraphObjects()) {
			vo.setSelected(false);
		}
	}

	void moveContent(int dx, int dy) {

		// move content
		if (dx != 0 || dy != 0 && !movingContent) {
			movingContent = true;
			GraphObjectView[] objects = depot.getObjects();

			for (GraphObjectView vgo : objects) {
				if (!(vgo instanceof NodeView)) {
					vgo.move(dx, dy);
				}
			}
			for (GraphObjectView vgo : objects) {
				if ((vgo instanceof NodeView)) {
					vgo.move(dx, dy);
				}
			}

			movingContent = false;
		}
	}

	@Override
	public void startManipulating() {

		super.startManipulating();
		GraphObjectView[] objects = depot.getObjects();
		for (GraphObjectView vgo : objects) {
			vgo.startManipulating();
		}
	}

	@Override
	public void endManipulating() {

		GraphObjectView[] objects = depot.getObjects();
		for (GraphObjectView vgo : objects) {
			vgo.endManipulating();
		}
		super.endManipulating();
	}

	@Override
	public List<GraphObjectView> getGraphObjectsAt(Point pt) {

		return Arrays.asList(depot.getHitObjects(pt));
	}

	@Override
	public void toFront(GraphObjectView gov) {

		if (gov.getParentGraph() == this) {
			depot.toFront(gov);
			updateView();
		} else {
			gov.getParentGraph().toFront(gov);
		}
	}

	@Override
	public void toBack(GraphObjectView gov) {

		if (gov.getParentGraph() == this) {
			depot.toBack(gov);
			updateView();
		} else {
			gov.getParentGraph().toBack(gov);
		}
	}

	@Override
	public void moveForward(GraphObjectView gov) {

		if (gov.getParentGraph() == this) {
			depot.moveForward(gov);
			updateView();
		} else {
			gov.getParentGraph().moveForward(gov);
		}
	}

	@Override
	public void moveBackward(GraphObjectView gov) {

		if (gov.getParentGraph() == this) {
			depot.moveBackward(gov);
			updateView();
		} else {
			gov.getParentGraph().moveBackward(gov);
		}
	}

	@Override
	public int getDepth() {

		GraphView parent = getParentGraph();
		return parent == null ? 0 : parent.getDepth() + 1;
	}

	@Override
	public Rectangle getExtendedBoundary() {

		if (parent == null) {
			return depot.getExpansion();
		}

		return super.getExtendedBoundary();
	}

	@Override
	public void setBounds(Rectangle r) {

		if (parent == null) {
			boundary = new Rectangle(r);
		} else {
			super.setBounds(r);
		}
	}

	@Override
	public PropertyList getProperties() {

		// if (properties == null) {
		// return null;
		// }
		//
		// if (properties.get(PropertyConstants.PORTS_PROPERTY) == null) {
		// PropertyList portsProperties = portSet.getProperties();
		// if (portsProperties != null) {
		// properties.add(portsProperties);
		// }
		// }
		//
		// return properties.deepCopy();
	}

	@Override
	protected void init() {

		super.init();

		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_ID, id);
		// PropertyUtil.makeEditable(properties,
		// PropertyConstants.GRAPH_PROPERTY_ID, false);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_NAME,
		// getName());
		// PropertyUtil.makeEditable(properties,
		// PropertyConstants.GRAPH_PROPERTY_NAME, false);
		//
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_X,
		// boundary.x);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_Y,
		// boundary.y);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_WIDTH,
		// boundary.width);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_HEIGHT,
		// boundary.height);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_ID, id);
		//
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_INNER_MARGIN, 4);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_FIT_TO_CONTENT, true);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_CONTENT_SELECTABEL, true);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_CONTENT_MOVABLE, true);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_CONTENT_DELETABLE, true);
		//
		// properties.addPropertyListener(this);
	}

	@Override
	public void propertyChanged(List<PropertyList> path, PropertyUnit property) {

		// try {
		// super.propertyChanged(path, property);
		// String str = PropertyUtil.toString(path, property);
		//
		// if (PropertyConstants.GRAPH_PROPERTY_INNER_MARGIN.equals(str)) {
		// margin = ConvertUtil.object2int(property.getValue());
		// } else if
		// (PropertyConstants.GRAPH_PROPERTY_FIT_TO_CONTENT.equals(str)) {
		// fitToContent = ConvertUtil.object2boolean(property.getValue());
		// } else if
		// (PropertyConstants.GRAPH_PROPERTY_CONTENT_SELECTABEL.equals(str)) {
		// contentSelectable = ConvertUtil.object2boolean(property.getValue());
		// } else if
		// (PropertyConstants.GRAPH_PROPERTY_CONTENT_MOVABLE.equals(str)) {
		// contentMovable = ConvertUtil.object2boolean(property.getValue());
		// } else if
		// (PropertyConstants.GRAPH_PROPERTY_CONTENT_DELETABLE.equals(str)) {
		// contentDeletable = ConvertUtil.object2boolean(property.getValue());
		// } else if (PropertyConstants.GRAPH_PROPERTY_PRESENTATION.equals(str))
		// {
		// presentationID = ConvertUtil.object2string(property.getValue());
		// }
		// } catch (Exception e) {
		// System.err.println("Error. The value " + property.getValue()
		// + "is incompatible with the property " + property.getName());
		// }
	}

	@Override
	public void setProperties(PropertyList propertyList) {

		// properties = propertyList.deepCopy();
		// Rectangle r = new Rectangle();
		// r.x = PropertyUtil.getProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_X, 0);
		// r.y = PropertyUtil.getProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_Y, 0);
		// r.width = PropertyUtil.getProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_WIDTH, 50);
		// r.height = PropertyUtil
		// .getProperty(properties, PropertyConstants.GRAPH_PROPERTY_HEIGHT,
		// 50);
		// setBounds(r);
		//
		// margin = PropertyUtil.getProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_INNER_MARGIN, 0);
		// fitToContent = PropertyUtil.getProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_FIT_TO_CONTENT, true);
		// contentSelectable = PropertyUtil.getProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_CONTENT_SELECTABEL, true);
		// contentMovable = PropertyUtil.getProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_CONTENT_MOVABLE, true);
		// contentDeletable = PropertyUtil.getProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_CONTENT_DELETABLE, true);
		//
		// presentationID = PropertyUtil.getProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_PRESENTATION, null);
		//
		// PropertyList portsProperties =
		// PropertyUtil.getPropertyList(properties,
		// PropertyConstants.PORTS_PROPERTY);
		// if (portsProperties != null) {
		// portSet.setProperties(portsProperties);
		// // updatePortPosition();
		// }
		// // never accept the given id and name
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_ID, id);
		// PropertyUtil.makeEditable(properties,
		// PropertyConstants.GRAPH_PROPERTY_ID, false);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_PARENT_ID, (parent != null ?
		// parent.getID() : -1));
		// PropertyUtil.makeEditable(properties,
		// PropertyConstants.GRAPH_PROPERTY_PARENT_ID, false);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_NAME,
		// getName());
		// PropertyUtil.makeEditable(properties,
		// PropertyConstants.GRAPH_PROPERTY_NAME, false);

	}

	@Override
	protected void updateBoundaryProperties() {

		// PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_X, boundary.x);
		// PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_Y, boundary.y);
		// PropertyUtil
		// .setProperty(properties, PropertyConstants.GRAPH_PROPERTY_WIDTH,
		// boundary.width);
		// PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_HEIGHT,
		// boundary.height);
	}

	@Override
	public void setPresentationID(String presentationID) {

		this.presentationID = presentationID;

		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_PRESENTATION, presentationID);
	}

	@Override
	protected String getSelectionDesriptorID() {

		return Constants.DEFAULT_SUBGRAPH_SELECTION_MARKER;
	}

	@Override
	protected String getPortHighlighingDesriptorID() {

		return Constants.DEFAULT_SUBGRAPH_PORT_HIGHLIGHT_INDICATOR;
	}

	@Override
	protected String getPortDesriptorID() {

		return Constants.DEFAULT_SUBGRAPH_PORT_INDICATOR;
	}

	@Override
	protected String getSelectionMarkerDesriptorID() {

		return Constants.DEFAULT_SUBGRAPH_SELECTION_MARKER;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append("GraphView (").append("id = ").append(getID()).append(", level = ")
		.append(getDepth()).append(", objects = ")
		.append(String.valueOf(getGraphObjects().size())).append(" ]");
		return sb.toString();

	}

	@Override
	public GraphObjectView deepCopy(long id) {

		return new DefaultGraphView(this, id);
	}

	@Override
	public Image getPreview(RenderingContext ctx, ImageObserver observer) {

		Image preview = super.getPreview(ctx, observer);

		return preview;
	}

	@Override
	public String getViewDescription(RenderingContext context/*
	 * , boolean
	 * useEmbeddedImage
	 */, boolean standalone) {

		GraphObjectView[] objects = depot.getObjects();
		if (objects.length == 0) {
			return super.getViewDescription(context/* , useEmbeddedImage */, true);
		}

		final StringBuffer svg = new StringBuffer();
		String subgraphDesc = super.getViewDescription(context/*
		 * ,
		 * useEmbeddedImage
		 */, true);
		if (subgraphDesc != null) {
			svg.append(subgraphDesc);
		}

		svg.append("\n<g>\n");

		for (GraphObjectView vgo : objects) {

			String description = vgo.getViewDescription(context/*
			 * ,
			 * useEmbeddedImage
			 */, true);
			if (description != null && !description.isEmpty()) {
				svg.append(description);
			}
		}
		svg.append("\n</g>");

		return svg.toString();
	}

	// //
	// ///////////////////////////////////////////////////////////////////////
	// // implementation of the interface LayoutableNode
	// @Override
	// public LayoutableNode[] nodes() {
	//
	// return depot.getNodes();
	// }
	//
	// @Override
	// public LayoutableEdge[] edges() {
	//
	// return depot.getEdges();
	// }

	// ===================================================
	// sending notification to listener

	List<GraphViewListener> graphViewListener = new ArrayList<GraphViewListener>();

	@Override
	public void addGraphViewListener(GraphViewListener listener) {

		if (!graphViewListener.contains(listener)) {
			graphViewListener.add(listener);
		}
	}

	@Override
	public void removeGraphViewListener(GraphViewListener listener) {

		graphViewListener.remove(listener);
	}

	// @Override
	// public void fireEvents(boolean enable) {
	//
	// super.fireEvents(enable);
	// GraphObjectView[] members = depot.getObjects();
	// for (int i = 0; i < members.length; i++) {
	// members[i].fireEvents(enable);
	// }
	// }

	protected void fireNodeAdded(NodeView n) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeAdded(n);
		}
		fireGraphManipulated();
	}

	protected void fireNodeRemoved(NodeView n) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeRemoved(n);
		}
		fireGraphManipulated();
	}

	protected void fireEdgeAdded(EdgeView e) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeAdded(e);
		}
		fireGraphManipulated();
	}

	protected void fireEdgeRemoved(EdgeView e) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeRemoved(e);
		}
		fireGraphManipulated();
	}

	protected void fireStartGrouping(GraphView group) {

		for (GraphViewListener l : graphViewListener) {
			l.startGrouping(group);
		}
	}

	protected void fireEndGrouping(GraphView group) {

		for (GraphViewListener l : graphViewListener) {
			l.endGrouping(group);
		}
	}

	protected void fireGraphManipulated() {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.graphManipulated(this);
		}
		updateView();
	}

	// @Override
	public void updateView() {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.viewChanged(this);
		}
	}

	protected void fireGraphExpansionChanged(Rectangle newExpansion) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.graphExpansionChanged(this, newExpansion);
		}
	}

	// ///////////////////////////////
	// events coming from nodes\

	@Override
	public void nodeStartedChangingBoundary(NodeView node) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeStartedChangingBoundary(node);
		}
	}

	@Override
	public void nodeBoundaryChangning(NodeView node) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeBoundaryChangning(node);
		}
		fireGraphManipulated();
	}

	@Override
	public void nodeStoppedChangingBoundary(NodeView node, Rectangle oldBoundary) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeStoppedChangingBoundary(node, oldBoundary);
		}
		fireGraphManipulated();
	}

	@Override
	public void nodeManipulated() {

		fireGraphManipulated();
	}

	//
	// @Override
	// public void nodeMoving(NodeView nodeView, int dx, int dy) {
	// if(!fireEvents)
	// return;
	// for (GraphViewListener l : graphViewListener) {
	// l.nodeMoved(nodeView, dx, dy);
	// }
	// fireGraphManipulated();
	// }

	@Override
	public void nodeSelectionChanged(NodeView node) {

		// graphViewUndoHelper.registerSelectionChange(node);

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeSelectionChanged(node);
		}
		updateView();
	}

	@Override
	public void nodeHighlightingChanged(NodeView node) {

		updateView();
	}

	// ///////////////////////////////
	// events coming from edges

	@Override
	public void edgeStartChangingPath(EdgeView edge) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeStartedChangingPath(edge);
		}
	}

	@Override
	public void edgePathChanging(EdgeView edge) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgePathChanging(edge);
		}
		fireGraphManipulated();
	}

	@Override
	public void edgeStoppedChangingPath(EdgeView edge, EdgePoint[] oldPath) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeStoppedChangingPath(edge, oldPath);
		}
		fireGraphManipulated();
	}

	@Override
	public void edgeReconnected(EdgeView edge, NodeView oldSourceNode, int oldSourcePortID,
			NodeView oldTagetNode, int oldTargetPortID) {


		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeReassigned(edge, oldConnecedNode, oldPortID, sourceNodeChanged);
		}
		// graphViewUndoHandler.registerPortReassigned(edge, oldPortID,
		// sourceNodeChanged);
		fireGraphManipulated();
	}

	@Override
	public void edgeSelectionChanged(EdgeView edge) {

		// graphViewUndoHelper.registerSelectionChange(edge);

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeSelectionChanged(edge);
		}
		updateView();
	}

	@Override
	public void edgeHighlightingChanged(EdgeView edge) {

		updateView();
	}

	// // ===================================================
	// // implementation of UndoClient
	//
	// @Override
	// public void redo(Object data) {
	//
	// graphViewUndoHelper.redo(data);
	// }
	//
	// @Override
	// public void undo(Object data) {
	//
	// graphViewUndoHelper.undo(data);
	// }

}
