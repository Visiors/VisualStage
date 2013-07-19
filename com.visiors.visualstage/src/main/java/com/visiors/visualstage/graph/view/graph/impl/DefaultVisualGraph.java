package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.visiors.visualstage.graph.view.Constants;
import com.visiors.visualstage.graph.view.DefaultVisualGraphObject;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.listener.EdgeViewListener;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.graph.view.node.impl.DefaultVisualNode;
import com.visiors.visualstage.graph.view.node.listener.VisualNodeListener;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.renderer.RenderingContext;

public class DefaultVisualGraph extends DefaultVisualNode implements VisualGraph, VisualNodeListener, EdgeViewListener

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

	protected DefaultVisualGraph(String name) {

		super(name);
	}

	protected DefaultVisualGraph(String name, long id) {

		super(name, id);

		depot = new Depot(this);
		// graphViewUndoHelper = new GraphViewUndoHelper(this);
		eventMediator = new SubgraphEventMediator(this);
		new GraphContentManager(this);
	}

	protected DefaultVisualGraph(VisualGraph visualGraph, long id) {

		super(visualGraph.getName(), id);

		this.setBounds(visualGraph.getBounds());
		this.SetAttributes(visualGraph.getAttributes());
		this.incomingEdges = new ArrayList<VisualEdge>(visualGraph.getIncomingEdges());
		this.outgoingEdges = new ArrayList<VisualEdge>(visualGraph.getOutgoingEdges());
		this.setProperties(visualGraph.getProperties());
		this.setStyleID(visualGraph.getStyleID());
		this.setPresentationID(visualGraph.getPresentationID());
		/* this.setFormID(node.getFormID()); */
		if (visualGraph.getPortSet() != null) {
			portSet = visualGraph.getPortSet();
		}
		if (visualGraph.getCustomData() != null) {
			setCustomData(visualGraph.getCustomData().deepCopy());
		}
	}

	@Override
	public void add(VisualGraphObject... graphObjects) {

		for (VisualGraphObject go : graphObjects) {
			if (go instanceof VisualNode) {
				addNode((VisualNode) go);
			}
		}
		for (VisualGraphObject go : graphObjects) {
			if (go instanceof VisualEdge) {
				addEdge((VisualEdge) go);
			}
		}
	}

	@Override
	public void remove(VisualGraphObject... graphObjects) {

		// undoRedoHandler.stratOfGroupAction();

		for (VisualGraphObject go : graphObjects) {
			if (go instanceof VisualNode) {
				removeNode((VisualNode) go);
			}
		}
		for (VisualGraphObject go : graphObjects) {
			if (go instanceof VisualEdge) {
				removeEdge((VisualEdge) go);
			}
		}
		// undoRedoHandler.endOfGroupAction();
	}

	protected void addNode(VisualNode node) {

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
	public void setParentGraph(VisualGraph parentgraph) {

		super.setParentGraph(parentgraph);

		if (parentgraph instanceof DefaultVisualGraph) {
			eventMediator.setParentView((DefaultVisualGraph) parentgraph);
		} else {
			eventMediator.setParentView(null);
		}

		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.GRAPH_PROPERTY_PARENT_ID,
		// (parentgraph != null ? parentgraph.getID() : -1));
	}

	protected void removeNode(VisualNode node) {

		depot.remove(node);
		node.removeNodeViewListener(this);
		final List<VisualEdge> ies = new ArrayList<VisualEdge>(node.getIncomingEdges());
		for (VisualEdge e : ies) {
			e.connect(e.getSourceNode(), e.getSourcePortId(), null, DefaultVisualGraphObject.NONE);
		}
		final List<VisualEdge> oes = new ArrayList<VisualEdge>(node.getOutgoingEdges());
		for (VisualEdge e : oes) {
			e.connect(null, DefaultVisualGraphObject.NONE, e.getTargetNode(), e.getTargetPortId());
		}
		fireNodeRemoved(node);

		// graphViewUndoHelper.registerNodeRemoval(node);
	}

	protected void addEdge(VisualEdge edge) {

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

	protected void removeEdge(VisualEdge edge) {

		edge.removeEdgeViewListener(this);
		VisualNode n = edge.getSourceNode();
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
	public VisualEdge getEdge(long id) {

		return (VisualEdge) depot.getObject(id);
	}

	@Override
	public VisualNode getNode(long id) {

		return (VisualNode) depot.getObject(id);
	}

	@Override
	public List<VisualGraphObject> getHitObjects(Point pt) {

		return Arrays.asList(depot.getHitObjects(pt));
	}

	@Override
	public List<VisualEdge> getEdges() {

		return Arrays.asList(depot.getEdges());
	}

	@Override
	public List<VisualNode> getNodes() {

		return Arrays.asList(depot.getNodes());
	}

	@Override
	public List<VisualGraphObject> getGraphObjects(Rectangle rect) {

		return Arrays.asList(depot.getGraphObjects(rect));
	}

	@Override
	public List<VisualGraphObject> getGraphObjects() {

		return Arrays.asList(depot.getObjects());
	}

	@Override
	public void clear() {

		VisualGraphObject[] objects = depot.getObjects();
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
	public List<VisualGraphObject> getSelection() {

		List<VisualGraphObject> selection = new ArrayList<VisualGraphObject>();
		for (VisualGraphObject vo : getGraphObjects()) {
			if (vo.isSelected()) {
				selection.add(vo);
			}
		}

		return selection;
	}

	@Override
	public void setSelection(List<VisualGraphObject> selection) {

		for (VisualGraphObject vo : getGraphObjects()) {
			vo.setSelected(selection.contains(vo));
		}
	}

	@Override
	public void setSelection(VisualGraphObject selection) {

		for (VisualGraphObject vo : getGraphObjects()) {
			vo.setSelected(vo.equals(selection));
		}
	}

	@Override
	public void clearSelection() {

		for (VisualGraphObject vo : getGraphObjects()) {
			vo.setSelected(false);
		}
	}

	void moveContent(int dx, int dy) {

		// move content
		if (dx != 0 || dy != 0 && !movingContent) {
			movingContent = true;
			VisualGraphObject[] objects = depot.getObjects();

			for (VisualGraphObject vgo : objects) {
				if (!(vgo instanceof VisualNode)) {
					vgo.move(dx, dy);
				}
			}
			for (VisualGraphObject vgo : objects) {
				if ((vgo instanceof VisualNode)) {
					vgo.move(dx, dy);
				}
			}

			movingContent = false;
		}
	}

	@Override
	public void startManipulating() {

		super.startManipulating();
		VisualGraphObject[] objects = depot.getObjects();
		for (VisualGraphObject vgo : objects) {
			vgo.startManipulating();
		}
	}

	@Override
	public void endManipulating() {

		VisualGraphObject[] objects = depot.getObjects();
		for (VisualGraphObject vgo : objects) {
			vgo.endManipulating();
		}
		super.endManipulating();
	}

	@Override
	public List<VisualGraphObject> getGraphObjectsAt(Point pt) {

		return Arrays.asList(depot.getHitObjects(pt));
	}

	@Override
	public void toFront(VisualGraphObject gov) {

		if (gov.getParentGraph() == this) {
			depot.toFront(gov);
			updateView();
		} else {
			gov.getParentGraph().toFront(gov);
		}
	}

	@Override
	public void toBack(VisualGraphObject gov) {

		if (gov.getParentGraph() == this) {
			depot.toBack(gov);
			updateView();
		} else {
			gov.getParentGraph().toBack(gov);
		}
	}

	@Override
	public void moveForward(VisualGraphObject gov) {

		if (gov.getParentGraph() == this) {
			depot.moveForward(gov);
			updateView();
		} else {
			gov.getParentGraph().moveForward(gov);
		}
	}

	@Override
	public void moveBackward(VisualGraphObject gov) {

		if (gov.getParentGraph() == this) {
			depot.moveBackward(gov);
			updateView();
		} else {
			gov.getParentGraph().moveBackward(gov);
		}
	}

	@Override
	public int getDepth() {

		VisualGraph parent = getParentGraph();
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
	public void setBounds(Rectangle bounds) {

		if (parent == null) {
			boundary = new Rectangle(bounds);
		} else {
			super.setBounds(bounds);
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

		sb.append("GraphView (").append("id = ").append(getID()).append(", level = ").append(getDepth())
		.append(", objects = ").append(String.valueOf(getGraphObjects().size())).append(" ]");
		return sb.toString();

	}

	@Override
	public VisualGraphObject deepCopy(long id) {

		return new DefaultVisualGraph(this, id);
	}

	@Override
	public Image getPreview(RenderingContext ctx, ImageObserver observer) {

		Image preview = super.getPreview(ctx, observer);

		return preview;
	}

	@Override
	public String getViewDescriptor(RenderingContext context/*
	 * , boolean
	 * useEmbeddedImage
	 */, boolean standalone) {

		VisualGraphObject[] objects = depot.getObjects();
		if (objects.length == 0) {
			return super.getViewDescriptor(context/* , useEmbeddedImage */, true);
		}

		final StringBuffer svg = new StringBuffer();
		String subgraphDesc = super.getViewDescriptor(context/*
		 * ,
		 * useEmbeddedImage
		 */, true);
		if (subgraphDesc != null) {
			svg.append(subgraphDesc);
		}

		svg.append("\n<g>\n");

		for (VisualGraphObject vgo : objects) {

			String description = vgo.getViewDescriptor(context/*
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

	//
	// private void example() {
	//
	// final List<GraphObjectView> allObjects = new
	// ArrayList<GraphObjectView>();
	// GraphVisitor visitor = new GraphVisitor() {
	//
	// public void visit(GraphView subgraph, int level) {
	//
	// allObjects.add(subgraph.getGraphObjects(false));
	// }
	// };
	// GraphUtil.visitSubgraphs(visualGraph, visitor, true, 0);
	//
	// }

	@Override
	public void visitNodes(GraphNodeVisitor visitor, boolean preOrder) {

		visitNodes(this, visitor, preOrder);
	}


	private boolean visitNodes(VisualGraph visualGraph, GraphNodeVisitor visitor, boolean preOrder) {

		List<VisualNode> nodes = visualGraph.getNodes();
		if (preOrder) {
			for (VisualGraphObject node : nodes) {
				if (node instanceof VisualGraph == false) {
					if (!visitor.visit((VisualNode) node)) {
						return false;
					}
				}
			}
			for (VisualGraphObject node : nodes) {
				if (node instanceof VisualGraph) {
					if (!visitNodes((VisualGraph) node, visitor, preOrder)) {
						return false;
					}
				}
			}
		} else {
			for (VisualGraphObject node : nodes) {
				if (node instanceof VisualGraph) {
					if (!visitNodes((VisualGraph) node, visitor, preOrder)) {
						return false;
					}
				}
			}
			for (VisualGraphObject node : nodes) {
				if (node instanceof VisualGraph == false) {
					if (!visitor.visit((VisualNode) node)) {
						return false;
					}
				}
			}
		}
		return true;
	}


	//	@Override
	//	public void visitSubgraphs(GraphVisitor visitor, boolean preOrder) {
	//
	//		visitSubgraphs(this, visitor, preOrder);
	//	}
	//	
	//	private boolean visitSubgraphs(VisualGraph visualGraph, GraphVisitor visitor, boolean preOrder) {
	//
	//		List<VisualNode> nodes = visualGraph.getNodes();
	//		for (VisualGraphObject node : nodes) {
	//			if (node instanceof VisualGraph) {
	//				VisualGraph subgraph = (VisualGraph) node;
	//				if (preOrder) {
	//					if (!visitor.visit(subgraph)) {
	//						return false;
	//					}
	//					if(!visitSubgraphs(subgraph, visitor, preOrder)) {
	//						return false;
	//					}
	//				} else {
	//					if(!visitSubgraphs(subgraph, visitor, preOrder)) {
	//						return false;
	//					}
	//					if (!visitor.visit(subgraph)) {
	//						return false;
	//					}
	//				}
	//			}
	//		}
	//		return true;
	//	}

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

	protected void fireNodeAdded(VisualNode n) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeAdded(n);
		}
		fireGraphManipulated();
	}

	protected void fireNodeRemoved(VisualNode n) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeRemoved(n);
		}
		fireGraphManipulated();
	}

	protected void fireEdgeAdded(VisualEdge e) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeAdded(e);
		}
		fireGraphManipulated();
	}

	protected void fireEdgeRemoved(VisualEdge e) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeRemoved(e);
		}
		fireGraphManipulated();
	}

	protected void fireStartGrouping(VisualGraph group) {

		for (GraphViewListener l : graphViewListener) {
			l.startGrouping(group);
		}
	}

	protected void fireEndGrouping(VisualGraph group) {

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
	public void nodeStartedChangingBoundary(VisualNode node) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeStartedChangingBoundary(node);
		}
	}

	@Override
	public void nodeBoundaryChangning(VisualNode node) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.nodeBoundaryChangning(node);
		}
		fireGraphManipulated();
	}

	@Override
	public void nodeStoppedChangingBoundary(VisualNode node, Rectangle oldBoundary) {

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
	public void nodeSelectionChanged(VisualNode node) {

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
	public void nodeHighlightingChanged(VisualNode node) {

		updateView();
	}

	// ///////////////////////////////
	// events coming from edges

	@Override
	public void edgeStartChangingPath(VisualEdge edge) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeStartedChangingPath(edge);
		}
	}

	@Override
	public void edgePathChanging(VisualEdge edge) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgePathChanging(edge);
		}
		fireGraphManipulated();
	}

	@Override
	public void edgeStoppedChangingPath(VisualEdge edge, EdgePoint[] oldPath) {

		if (!fireEvents) {
			return;
		}
		for (GraphViewListener l : graphViewListener) {
			l.edgeStoppedChangingPath(edge, oldPath);
		}
		fireGraphManipulated();
	}

	@Override
	public void edgeReconnected(VisualEdge edge, VisualNode oldSourceNode, int oldSourcePortID,
			VisualNode oldTagetNode, int oldTargetPortID) {

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
	public void edgeSelectionChanged(VisualEdge edge) {

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
	public void edgeHighlightingChanged(VisualEdge edge) {

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
