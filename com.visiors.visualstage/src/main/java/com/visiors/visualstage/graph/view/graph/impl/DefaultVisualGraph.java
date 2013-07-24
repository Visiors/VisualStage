package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.visiors.visualstage.constants.PropertyConstants;
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
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.pool.ShapeTemplatePool;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.property.impl.PropertyBinder;
import com.visiors.visualstage.renderer.RenderingContext;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.util.PropertyUtil;

public class DefaultVisualGraph extends DefaultVisualNode implements VisualGraph, VisualNodeListener, EdgeViewListener

{

	private final Depot depot;
	// private final GraphViewUndoHelper graphViewUndoHelper;
	private boolean movingContent;
	private final SubgraphEventMediator eventMediator;

	protected boolean fitToContent = true;
	protected boolean contentSelectable = true;
	protected boolean contentMovable = true;
	protected boolean contentDeletable = true;;
	private PropertyBinder propertyBinder;

	@Inject
	ShapeTemplatePool graphObjectPool;
	@Inject
	protected Provider<VisualNode> visualNodeProvider;
	@Inject
	protected Provider<VisualEdge> visualEdgeProvider;
	@Inject
	protected Provider<VisualGraph> visualGraphProvider;
	@Inject
	protected UndoRedoHandler undoRedoHandler;

	// private SVGDocumentBuilder svgDoc;

	// private Validator validator;

	protected DefaultVisualGraph() {

		this(-1);
	}

	protected DefaultVisualGraph(long id) {

		super(id);

		depot = new Depot(this);
		// graphViewUndoHelper = new GraphViewUndoHelper(this);
		eventMediator = new SubgraphEventMediator(this);
		new GraphContentManager(this);
	}

	protected DefaultVisualGraph(VisualGraph visualGraph, long id) {

		this(id);

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

		for (final VisualGraphObject go : graphObjects) {
			if (go instanceof VisualNode) {
				addNode((VisualNode) go);
			}
		}
		for (final VisualGraphObject go : graphObjects) {
			if (go instanceof VisualEdge) {
				addEdge((VisualEdge) go);
			}
		}
	}

	@Override
	public void remove(VisualGraphObject... graphObjects) {

		// undoRedoHandler.stratOfGroupAction();

		for (final VisualGraphObject go : graphObjects) {
			if (go instanceof VisualNode) {
				removeNode((VisualNode) go);
			}
		}
		for (final VisualGraphObject go : graphObjects) {
			if (go instanceof VisualEdge) {
				removeEdge((VisualEdge) go);
			}
		}
		// undoRedoHandler.endOfGroupAction();
	}

	@Override
	public VisualNode createNode(long id, String type) {

		if (!graphObjectPool.contains(type)) {
			throw new IllegalArgumentException("The node '" + type
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the node's descriptor, or registere the associated node.");
		}

		return createNode(graphObjectPool.get(type));
	}

	@Override
	public VisualNode createNode(PropertyList properties) {

		final VisualNode visualNode = visualNodeProvider.get();
		visualNode.setProperties(properties);
		addNode(visualNode);
		return visualNode;
	}

	@Override
	public VisualEdge createEdge(long id, String type) {

		if (!graphObjectPool.contains(type)) {
			throw new IllegalArgumentException("The edge '" + type
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the edge's descriptor, or registere the associated edge.");
		}
		return createEdge(graphObjectPool.get(type));
	}

	@Override
	public VisualEdge createEdge(PropertyList properties) {

		final VisualEdge visualEdge = visualEdgeProvider.get();
		visualEdge.setProperties(properties);
		addEdge(visualEdge);
		return visualEdge;
	}

	@Override
	public VisualGraph createSubgraph(long id, String type) {

		if (!graphObjectPool.contains(type)) {
			throw new IllegalArgumentException("The subgraph '" + type
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the subgraph's descriptor, or registere the associated object.");
		}

		return createSubgraph(graphObjectPool.get(type));
	}

	@Override
	public VisualGraph createSubgraph(PropertyList properties) {

		final VisualGraph visualGraph = visualGraphProvider.get();
		visualGraph.setProperties(properties);
		addNode(visualGraph);
		return visualGraph;
	}

	@Override
	public void createGraphObjects(PropertyList properties) {

		/*
		 * The Objects' id might change if the stored ids are in use. This map
		 * keeps track of changed stored ids and the real current ids so the
		 * edges can find their connections
		 */
		final Map<Long, Long> idMappings = new HashMap<Long, Long>();

		final PropertyList edgesProperties = (PropertyList) properties.get(PropertyConstants.EDGE_SECTION_TAG);
		final PropertyList nodesProperties = (PropertyList) properties.get(PropertyConstants.NODE_SECTION_TAG);
		final PropertyList groupsProperties = (PropertyList) properties.get(PropertyConstants.SUBGRAPH_SECTION_TAG);

		if (groupsProperties != null) {
			for (int i = 0; i < groupsProperties.size(); i++) {
				final PropertyList graphProperties = (PropertyList) groupsProperties.get(i);
				final VisualGraph graph = createSubgraph(graphProperties);
				final long previousID = PropertyUtil.getProperty(graphProperties, PropertyConstants.NODE_PROPERTY_ID,
						-1L);
				idMappings.put(previousID, graph.getID());
			}
		}
		if (nodesProperties != null) {
			for (int i = 0; i < nodesProperties.size(); i++) {
				final PropertyList nodeProperties = (PropertyList) nodesProperties.get(i);
				final VisualNode node = createNode(nodeProperties);
				final long previousID = PropertyUtil.getProperty(nodeProperties, PropertyConstants.NODE_PROPERTY_ID,
						-1L);
				idMappings.put(previousID, node.getID());
			}
		}
		if (edgesProperties != null) {
			for (int i = 0; i < edgesProperties.size(); i++) {
				final PropertyList edgeProperties = (PropertyList) edgesProperties.get(i);
				final VisualEdge edge = createEdge(edgeProperties);
				// connect edge
				final PropertyUnit source = PropertyUtil.findPropertyUnit(edgeProperties,
						PropertyConstants.EDGE_PROPERTY_SOURCE);
				final PropertyUnit target = PropertyUtil.findPropertyUnit(edgeProperties,
						PropertyConstants.EDGE_PROPERTY_TARGET);
				final int sourcePort = PropertyUtil.getProperty(edgeProperties,
						PropertyConstants.EDGE_PROPERTY_SOURCE_PORT, NONE);
				final int targetPort = PropertyUtil.getProperty(edgeProperties,
						PropertyConstants.EDGE_PROPERTY_TARGET_PORT, NONE);
				VisualNode sourceNode = null;
				VisualNode targetNode = null;
				if (source != null) {
					sourceNode = getNode(idMappings.get(source));
				}
				if (target != null) {
					targetNode = getNode(idMappings.get(target));
				}
				edge.connect(sourceNode, sourcePort, targetNode, targetPort);
			}
		}
	}

	protected void addNode(VisualNode node) {

		if (depot.getObject(node.getID()) != null) {
			throw new IllegalArgumentException("The node cannot be added into the graph. "
					+ "Reason: an object with the id " + node.getID() + " already exists.");
		}
		depot.add(node);
		node.setParentGraph(this);
		node.setTransformer(transformer);
		node.addNodeViewListener(this);
		// node.fireEvents(fireEvents);
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
	}

	protected void removeNode(VisualNode node) {

		depot.remove(node);
		node.removeNodeViewListener(this);
		final List<VisualEdge> ies = new ArrayList<VisualEdge>(node.getIncomingEdges());
		for (final VisualEdge e : ies) {
			e.connect(e.getSourceNode(), e.getSourcePortId(), null, DefaultVisualGraphObject.NONE);
		}
		final List<VisualEdge> oes = new ArrayList<VisualEdge>(node.getOutgoingEdges());
		for (final VisualEdge e : oes) {
			e.connect(null, DefaultVisualGraphObject.NONE, e.getTargetNode(), e.getTargetPortId());
		}
		fireNodeRemoved(node);
		// graphViewUndoHelper.registerNodeRemoval(node);
	}

	protected void addEdge(VisualEdge edge) {

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

		final VisualGraphObject[] objects = depot.getObjects();
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

		final List<VisualGraphObject> selection = new ArrayList<VisualGraphObject>();
		for (final VisualGraphObject vo : getGraphObjects()) {
			if (vo.isSelected()) {
				selection.add(vo);
			}
		}

		return selection;
	}

	@Override
	public void setSelection(List<VisualGraphObject> selection) {

		for (final VisualGraphObject vo : getGraphObjects()) {
			vo.setSelected(selection.contains(vo));
		}
	}

	@Override
	public void setSelection(VisualGraphObject selection) {

		for (final VisualGraphObject vo : getGraphObjects()) {
			vo.setSelected(vo.equals(selection));
		}
	}

	@Override
	public void clearSelection() {

		for (final VisualGraphObject vo : getGraphObjects()) {
			vo.setSelected(false);
		}
	}

	void moveContent(int dx, int dy) {

		// move content
		if (dx != 0 || dy != 0 && !movingContent) {
			movingContent = true;
			final VisualGraphObject[] objects = depot.getObjects();
			for (final VisualGraphObject vgo : objects) {
				if (!(vgo instanceof VisualNode)) {
					vgo.move(dx, dy);
				}
			}
			for (final VisualGraphObject vgo : objects) {
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
		final VisualGraphObject[] objects = depot.getObjects();
		for (final VisualGraphObject vgo : objects) {
			vgo.startManipulating();
		}
	}

	@Override
	public void endManipulating() {

		final VisualGraphObject[] objects = depot.getObjects();
		for (final VisualGraphObject vgo : objects) {
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

		final VisualGraph parent = getParentGraph();
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
	protected void initPropertyList() {

		// create the properties definition
		PropertyList properties = new DefaultPropertyList(PropertyConstants.GRAPH_PROPERTY_PREFIX);
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_ID, getID()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_NAME, getName()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_X, getX()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_Y, getY()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_WIDTH, getWidth()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_HEIGHT, getHeight()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_PRESENTATION, presentationID));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_PARENT_ID, getParentGraphID()));

		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_FIT_TO_CONTENT, fitToContent));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_CONTENT_SELECTABEL, contentSelectable));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_CONTENT_MOVABLE, contentMovable));
		properties.add(new DefaultPropertyUnit(PropertyConstants.GRAPH_PROPERTY_CONTENT_DELETABLE, contentDeletable));

		PropertyList portsProperties = portSet.getProperties();
		properties.add(portsProperties);

		propertyBinder = new PropertyBinder(this);
		propertyBinder.setHandler(portsProperties.getName(), portSet);

		setProperties(properties);
	}

	public void setFitToContent(boolean fitToContent) {

		this.fitToContent = fitToContent;
	}

	public boolean getFitToContent() {

		return fitToContent;
	}

	public void setContentMovable(boolean contentMovable) {

		this.contentMovable = contentMovable;
	}

	public boolean getContentMovable(boolean contentMovable) {

		return this.contentMovable;
	}

	public void setContentSelectable(boolean contentSelectable) {

		this.contentSelectable = contentSelectable;
	}

	public boolean getContentSelectable() {

		return this.contentSelectable;
	}

	public void setContentDeletable(boolean contentDeletable) {

		this.contentDeletable = contentDeletable;
	}

	public boolean getContentDeletable() {

		return this.contentDeletable;
	}


	@Override
	public PropertyList getProperties(boolean childrenIncluded) {

		final PropertyList graphProperties = getProperties();

		if (childrenIncluded) {
			appendChildrenProperties(this, graphProperties);
		}
		return graphProperties;
	}

	private void appendChildrenProperties(VisualGraph visualGraph, PropertyList properties) {

		final PropertyList edgesProperties = new DefaultPropertyList(PropertyConstants.EDGE_SECTION_TAG);
		final PropertyList nodesProperties = new DefaultPropertyList(PropertyConstants.NODE_SECTION_TAG);
		final PropertyList subgraphProperties = new DefaultPropertyList(PropertyConstants.SUBGRAPH_SECTION_TAG);

		for (final VisualGraphObject vgo : visualGraph.getGraphObjects()) {
			if (vgo instanceof VisualGraph) {
				appendChildrenProperties((VisualGraph) vgo, vgo.getProperties());
				subgraphProperties.add(subgraphProperties);
			} else if (vgo instanceof VisualNode) {

				nodesProperties.add(vgo.getProperties());
			} else if (vgo instanceof VisualEdge) {
				edgesProperties.add(vgo.getProperties());
			}
		}
		if (edgesProperties.size() != 0) {
			properties.add(edgesProperties);
		}
		if (nodesProperties.size() != 0) {
			properties.add(nodesProperties);
		}
		if (subgraphProperties.size() != 0) {
			properties.add(subgraphProperties);
		}
	}

	@Override
	public void setProperties(PropertyList propertyList) {

		super.setProperties(properties);
		propertyBinder.loadAll();

		createGraphObjects(properties);
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

		final StringBuffer sb = new StringBuffer();

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

		final Image preview = super.getPreview(ctx, observer);

		return preview;
	}

	@Override
	public String getViewDescriptor(RenderingContext context, Resolution resolution) {

		final VisualGraphObject[] objects = depot.getObjects();
		if (objects.length == 0) {
			return super.getViewDescriptor(context, resolution);
		}

		final StringBuffer svg = new StringBuffer();
		final String subgraphDesc = super.getViewDescriptor(context, resolution);
		if (subgraphDesc != null) {
			svg.append(subgraphDesc);
		}

		svg.append("\n<g>\n");

		for (final VisualGraphObject vgo : objects) {

			final String description = vgo.getViewDescriptor(context, resolution);
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

		final List<VisualNode> nodes = visualGraph.getNodes();
		if (preOrder) {
			for (final VisualGraphObject node : nodes) {
				if (node instanceof VisualGraph == false) {
					if (!visitor.visit((VisualNode) node)) {
						return false;
					}
				}
			}
			for (final VisualGraphObject node : nodes) {
				if (node instanceof VisualGraph) {
					if (!visitNodes((VisualGraph) node, visitor, preOrder)) {
						return false;
					}
				}
			}
		} else {
			for (final VisualGraphObject node : nodes) {
				if (node instanceof VisualGraph) {
					if (!visitNodes((VisualGraph) node, visitor, preOrder)) {
						return false;
					}
				}
			}
			for (final VisualGraphObject node : nodes) {
				if (node instanceof VisualGraph == false) {
					if (!visitor.visit((VisualNode) node)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// @Override
	// public void visitSubgraphs(GraphVisitor visitor, boolean preOrder) {
	//
	// visitSubgraphs(this, visitor, preOrder);
	// }
	//
	// private boolean visitSubgraphs(VisualGraph visualGraph, GraphVisitor
	// visitor, boolean preOrder) {
	//
	// List<VisualNode> nodes = visualGraph.getNodes();
	// for (VisualGraphObject node : nodes) {
	// if (node instanceof VisualGraph) {
	// VisualGraph subgraph = (VisualGraph) node;
	// if (preOrder) {
	// if (!visitor.visit(subgraph)) {
	// return false;
	// }
	// if(!visitSubgraphs(subgraph, visitor, preOrder)) {
	// return false;
	// }
	// } else {
	// if(!visitSubgraphs(subgraph, visitor, preOrder)) {
	// return false;
	// }
	// if (!visitor.visit(subgraph)) {
	// return false;
	// }
	// }
	// }
	// }
	// return true;
	// }

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
		for (final GraphViewListener l : graphViewListener) {
			l.nodeAdded(n);
		}
		fireGraphManipulated();
	}

	protected void fireNodeRemoved(VisualNode n) {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.nodeRemoved(n);
		}
		fireGraphManipulated();
	}

	protected void fireEdgeAdded(VisualEdge e) {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.edgeAdded(e);
		}
		fireGraphManipulated();
	}

	protected void fireEdgeRemoved(VisualEdge e) {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.edgeRemoved(e);
		}
		fireGraphManipulated();
	}

	protected void fireStartGrouping(VisualGraph group) {

		for (final GraphViewListener l : graphViewListener) {
			l.startGrouping(group);
		}
	}

	protected void fireEndGrouping(VisualGraph group) {

		for (final GraphViewListener l : graphViewListener) {
			l.endGrouping(group);
		}
	}

	protected void fireGraphManipulated() {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.graphManipulated(this);
		}
		updateView();
	}

	// @Override
	public void updateView() {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.viewChanged(this);
		}
	}

	protected void fireGraphExpansionChanged(Rectangle newExpansion) {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
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
		for (final GraphViewListener l : graphViewListener) {
			l.nodeStartedChangingBoundary(node);
		}
	}

	@Override
	public void nodeBoundaryChangning(VisualNode node) {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.nodeBoundaryChangning(node);
		}
		fireGraphManipulated();
	}

	@Override
	public void nodeStoppedChangingBoundary(VisualNode node, Rectangle previousBoundary) {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.nodeStoppedChangingBoundary(node, previousBoundary);
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
		for (final GraphViewListener l : graphViewListener) {
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
		for (final GraphViewListener l : graphViewListener) {
			l.edgeStartedChangingPath(edge);
		}
	}

	@Override
	public void edgePathChanging(VisualEdge edge) {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.edgePathChanging(edge);
		}
		fireGraphManipulated();
	}

	@Override
	public void edgeStoppedChangingPath(VisualEdge edge, EdgePoint[] previousPath) {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.edgeStoppedChangingPath(edge, previousPath);
		}
		fireGraphManipulated();
	}

	@Override
	public void edgeReconnected(VisualEdge edge, VisualNode previousSourceNode, int previousSourcePort,
			VisualNode previousTagetNode, int previousTargetPort) {

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
			l.edgeReassigned(edge, previousSourceNode, previousSourcePort, previousTagetNode, previousTargetPort);
		}
		// graphViewUndoHandler.registerPortReassigned(edge, previousPort,
		// sourceNodeChanged);
		fireGraphManipulated();
	}

	@Override
	public void edgeSelectionChanged(VisualEdge edge) {

		// graphViewUndoHelper.registerSelectionChange(edge);

		if (!fireEvents) {
			return;
		}
		for (final GraphViewListener l : graphViewListener) {
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