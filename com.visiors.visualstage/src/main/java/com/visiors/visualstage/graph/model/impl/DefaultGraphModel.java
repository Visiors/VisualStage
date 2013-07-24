package com.visiors.visualstage.graph.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visiors.visualstage.exception.IDInvalidException;
import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.GraphModel;
import com.visiors.visualstage.graph.model.GraphModelListener;
import com.visiors.visualstage.graph.model.GraphObjectModel;
import com.visiors.visualstage.graph.model.NodeModel;

/**
 * This is a default implementation for {@link GraphModel}. This class also
 * extends the {@link DefaultGraphModel}; i.e. a graph model can be considered
 * as a node as well. This approach is especially useful in context of nested
 * graphs.
 */
public class DefaultGraphModel extends DefaultNodeModel implements GraphModel {
	protected Map<Long, NodeModel> nodeMap;
	protected Map<Long, EdgeModel> edgeMap;
	protected List<GraphModelListener> graphModelListener;

	/**
	 * Creates a new graph. An unique id will be assigned automatically.
	 */
	public DefaultGraphModel() {

		this(-1);
	}

	/**
	 * Creates a new node graph the specified <code>id</code>. The specified
	 * <code>id</code> must be unique within the entire <code>graph</code>
	 * hierarchy<br>
	 * An {@link IDInvalidException} will be thrown if the given
	 * <code>id</code> is not unique.
	 * 
	 * @param id
	 *            a unique identifier for the node, or -1 if a unique id should
	 *            be assigned automatically.
	 */
	public DefaultGraphModel(long id) {

		super(id);

		nodeMap = new HashMap<Long, NodeModel>();
		edgeMap = new HashMap<Long, EdgeModel>();
		graphModelListener = new ArrayList<GraphModelListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#add(com.visiors.visualstage
	 * .graph.model.GraphObjectModel[])
	 */
	@Override
	public void add(GraphObjectModel... graphObjects) {
		for (GraphObjectModel go : graphObjects) {
			if (go instanceof NodeModel) {
				addNode((NodeModel) go);
			}
		}
		for (GraphObjectModel go : graphObjects) {
			if (go instanceof EdgeModel) {
				addEdge((EdgeModel) go);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#remove(com.visiors.visualstage
	 * .graph.model.GraphObjectModel[])
	 */
	@Override
	public void remove(GraphObjectModel... graphObjects) {
		for (GraphObjectModel go : graphObjects) {
			if (go instanceof EdgeModel) {
				removeEdge((EdgeModel) go);
			}
		}
		for (GraphObjectModel go : graphObjects) {
			if (go instanceof NodeModel) {
				removeNode((NodeModel) go);
			}
		}
	}

	protected void addNode(NodeModel node) {

		if (nodeMap.put(node.getID(), node) != null) {
			throw new IllegalArgumentException("The edge cannot be added to graph model. "
					+ "Reason: an object with the id " + node.getID() + " already exists.");
		}

		node.setParentGraph(this);

		postNodeAdded(node);
	}

	protected void removeNode(NodeModel node) {

		final List<EdgeModel> incomingEdges = new ArrayList<EdgeModel>(node.getIncomingEdges());
		for (EdgeModel edge : incomingEdges) {
			edge.setTargetNode(null);
		}

		final List<EdgeModel> outgoingEdges = new ArrayList<EdgeModel>(node.getOutgoingEdges());
		for (EdgeModel edge : outgoingEdges) {
			edge.setSourceNode(null);
		}

		nodeMap.remove(node.getID());

		postNodeRemoved(node);
	}

	protected void addEdge(EdgeModel edge) {

		if (edgeMap.put(edge.getID(), edge) != null) {
			throw new IllegalArgumentException("The edge cannot be added to the graph model. "
					+ "Reason: an object with the id " + edge.getID() + " already exists.");
		}

		edge.setParentGraph(this);

		postEdgeAdded(edge);
	}

	protected void removeEdge(EdgeModel edge) {
		if (edge.getSourceNode() != null) {
			edge.setSourceNode(null);
		}
		if (edge.getTargetNode() != null) {
			edge.setTargetNode(null);
		}

		edgeMap.remove(edge.getID());

		postEdgeRemoved(edge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphModel#clear()
	 */
	@Override
	public void clear() {

		final List<EdgeModel> edges = getEdges();
		for (EdgeModel e : edges) {
			removeEdge(e);
		}
		final List<NodeModel> nodes = getNodes();
		for (NodeModel n : nodes) {
			removeNode(n);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphModel#getNode(long)
	 */
	@Override
	public NodeModel getNode(long id) {

		return nodeMap.get(new Long(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphModel#getEdge(long)
	 */
	@Override
	public EdgeModel getEdge(long id) {

		return edgeMap.get(new Long(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphModel#getNodes()
	 */
	@Override
	public List<NodeModel> getNodes() {

		return new ArrayList<NodeModel>(nodeMap.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphModel#getEdges()
	 */
	@Override
	public List<EdgeModel> getEdges() {

		return new ArrayList<EdgeModel>(edgeMap.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphModel#getGraphObjects()
	 */
	@Override
	public List<GraphObjectModel> getGraphObjects() {

		final List<NodeModel> nodes = getNodes();
		final List<EdgeModel> edges = getEdges();
		final List<GraphObjectModel> graphObjects = new ArrayList<GraphObjectModel>();
		graphObjects.addAll(nodes);
		graphObjects.addAll(edges);
		return graphObjects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphModel#getDepth()
	 */
	@Override
	public int getDepth() {

		GraphModel parent = getParentGraph();
		return parent == null ? 0 : parent.getDepth() + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.impl.DefaultNodeModel#deepCopy()
	 */
	@Override
	public GraphModel deepCopy() {

		final DefaultGraphModel gm = new DefaultGraphModel();
		gm.incomingEdges = new ArrayList<EdgeModel>(incomingEdges);
		gm.outgoingEdges = new ArrayList<EdgeModel>(outgoingEdges);
		gm.parentGraph = parentGraph;
		gm.customObject = customObject.deepCopy();

		for (NodeModel n : nodeMap.values()) {
			nodeMap.put(n.getID(), n);
		}
		for (EdgeModel e : edgeMap.values()) {
			edgeMap.put(e.getID(), e);
		}
		return gm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.impl.DefaultNodeModel#toString()
	 */
	@Override
	public String toString() {

		final List<NodeModel> nodes = getNodes();
		final List<EdgeModel> edges = getEdges();
		NodeModel sn, tn;
		long snid, tnid;
		StringBuffer sb = new StringBuffer();
		sb.append("AdjacentTable: \nNodes:\n ");
		for (NodeModel node : nodes) {
			sb.append(node.getID() + ", ");
		}
		sb.append("\nEdges:\n ");
		for (EdgeModel edge : edges) {
			sn = edge.getSourceNode();
			snid = (sn == null ? -1 : sn.getID());
			tn = edge.getTargetNode();
			tnid = (tn == null ? -1 : tn.getID());
			sb.append(edge.getID());
			sb.append(" [" + snid + " -> " + tnid + "]\n ");
		}
		return sb.toString();
	}

	// /////////////////////////////////////////////////////////////////
	// sending graph events

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#addGraphListener(com.visiors
	 * .visualstage.model.listener.GraphModelListener)
	 */
	@Override
	public void addGraphListener(GraphModelListener listener) {

		if (!graphModelListener.contains(listener)) {
			graphModelListener.add(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#removeGraphListener(com
	 * .visiors.visualstage.model.listener.GraphModelListener)
	 */
	@Override
	public void removeGraphListener(GraphModelListener listener) {

		graphModelListener.remove(listener);
	}

	protected void postNodeAdded(NodeModel node) {

		for (GraphModelListener l : graphModelListener) {
			l.nodeAdded(node);
		}
	}

	protected void postNodeRemoved(NodeModel node) {

		for (GraphModelListener l : graphModelListener) {
			l.nodeRemoved(node);
		}
	}

	protected void postEdgeAdded(EdgeModel edge) {

		for (GraphModelListener l : graphModelListener) {
			l.edgeAdded(edge);
		}
	}

	protected void postEdgeRemoved(EdgeModel edge) {

		for (GraphModelListener l : graphModelListener) {
			l.edgeRemoved(edge);
		}
	}

}
