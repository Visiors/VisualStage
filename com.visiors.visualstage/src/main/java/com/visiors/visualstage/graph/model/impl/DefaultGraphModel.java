package com.visiors.visualstage.graph.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.GraphModel;
import com.visiors.visualstage.graph.model.GraphObjectModel;
import com.visiors.visualstage.graph.model.NodeModel;
import com.visiors.visualstage.model.listener.GraphModelListener;

public class DefaultGraphModel extends DefaultNodeModel implements GraphModel {

	protected Map<Long, NodeModel> nodeMap;
	protected Map<Long, EdgeModel> edgeMap;
	protected List<GraphModelListener> graphModelListener;

	public DefaultGraphModel() {

		super();
	}

	public DefaultGraphModel(long id) {

		super(id);

		nodeMap = new HashMap<Long, NodeModel>();
		edgeMap = new HashMap<Long, EdgeModel>();
		graphModelListener = new ArrayList<GraphModelListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphModel#addNode(com.visiors.
	 * visualstage.graph.model.NodeModel)
	 */
	@Override
	public void addNode(NodeModel node) {

		if (nodeMap.put(node.getID(), node) != null) {
			throw new IllegalArgumentException("The edge cannot be added to graph model. "
					+ "Reason: an object with the id " + node.getID() + " already exists.");
		}

		node.setParentGraph(this);

		postNodeAdded(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#removeNode(com.visiors
	 * .visualstage.graph.model.NodeModel)
	 */
	@Override
	public void removeNode(NodeModel node) {

		final List<EdgeModel> incomingEdges = node.getIncomingEdges();
		for (EdgeModel edge : incomingEdges) {
			connectEdgetoTargetNode(edge, null);
		}

		final List<EdgeModel> outgoingEdges = node.getOutgoingEdges();
		for (EdgeModel edge : outgoingEdges) {
			connectEdgeToSourceNode(edge, null);
		}

		nodeMap.remove(node.getID());

		postNodeRemoved(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphModel#addEdge(com.visiors.
	 * visualstage.graph.model.EdgeModel)
	 */
	@Override
	public void addEdge(EdgeModel edge) {

		if (edgeMap.put(edge.getID(), edge) != null) {
			throw new IllegalArgumentException("The edge cannot be added to the graph model. "
					+ "Reason: an object with the id " + edge.getID() + " already exists.");
		}

		edge.setParentGraph(this);

		postEdgeAdded(edge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#removeEdge(com.visiors
	 * .visualstage.graph.model.EdgeModel)
	 */
	@Override
	public void removeEdge(EdgeModel edge) {

		if (edge.getSourceNode() != null) {
			connectEdgeToSourceNode(edge, null);
		}
		if (edge.getTargetNode() != null) {
			connectEdgetoTargetNode(edge, null);
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
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#connectEdgeToSourceNode
	 * (com.visiors.visualstage.graph.model.EdgeModel,
	 * com.visiors.visualstage.graph.model.NodeModel)
	 */
	@Override
	public void connectEdgeToSourceNode(EdgeModel edge, NodeModel sourceNode) {

		final NodeModel currentNode = edge.getSourceNode();
		if (edge.getSourceNode() != null) {
			if (!edge.getSourceNode().disconnectFromOutgoingEdge(edge)) {
				return;
			}
		}
		if (!sourceNode.connectToOutgoingEdge(edge)) {
			return;
		}
		edge.setSourceNode(sourceNode);

		postEdgeReconnected(edge, currentNode, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#connectEdgetoTargetNode
	 * (com.visiors.visualstage.graph.model.EdgeModel,
	 * com.visiors.visualstage.graph.model.NodeModel)
	 */
	@Override
	public void connectEdgetoTargetNode(EdgeModel edge, NodeModel targetNode) {

		final NodeModel currentNode = edge.getTargetNode();

		if (edge.getTargetNode() != null) {
			if (!edge.getTargetNode().disconnectFromIncomingEdge(edge)) {
				return;
			}
		}
		if (!targetNode.connectToIncomingEdge(edge)) {
			return;
		}
		edge.setTargetNode(targetNode);
		postEdgeReconnected(edge, currentNode, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#existsNode(com.visiors
	 * .visualstage.graph.model.NodeModel)
	 */
	@Override
	public boolean existsNode(NodeModel node) {

		return nodeMap.containsValue(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphModel#existsEdge(com.visiors
	 * .visualstage.graph.model.EdgeModel)
	 */
	@Override
	public boolean existsEdge(EdgeModel edge) {

		return edgeMap.containsValue(edge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.impl.DefaultNodeModel#deepCopy()
	 */
	@Override
	public GraphModel deepCopy() {

		final DefaultGraphModel graphModel = new DefaultGraphModel();
		for (NodeModel n : nodeMap.values()) {
			nodeMap.put(n.getID(), n);
		}
		for (EdgeModel e : edgeMap.values()) {
			edgeMap.put(e.getID(), e);
		}
		return graphModel;
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

	protected void postEdgeReconnected(EdgeModel edge, NodeModel oldNode, boolean sourceNodeChanged) {

		for (GraphModelListener l : graphModelListener) {
			l.edgeReconnected(edge, oldNode, sourceNodeChanged);
		}
	}

}