package com.visiors.visualstage.graph.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.exception.DuplicateIdentifierException;
import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.EdgeModelListener;
import com.visiors.visualstage.graph.model.NodeModel;

public class DefaultEdgeModel extends AbstractGraphObject implements EdgeModel {
	protected NodeModel sourceNode;
	protected NodeModel targetNode;

	/**
	 * Creates a new edge. A unique id will be assigned automatically.
	 */
	public DefaultEdgeModel() {
		this(-1);
	}

	/**
	 * Creates a new edge with the given <code>id</code>. The specified
	 * <code>id</code> must be unique within the entire <code>graph</code>
	 * hierarchy<br>
	 * An {@link DuplicateIdentifierException} will be thrown if the given
	 * <code>id</code> is not unique.
	 * 
	 * @param id
	 *            a unique identifier for the node, or -1 if a unique id should
	 *            be assigned automatically
	 */
	public DefaultEdgeModel(long id) {
		super(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.EdgeModel#getSourceNode()
	 */
	@Override
	public NodeModel getSourceNode() {

		return sourceNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.EdgeModel#getTargetNode()
	 */
	@Override
	public NodeModel getTargetNode() {
		return targetNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.EdgeModel#setSourceNode(com.visiors
	 * .visualstage.graph.model.NodeModel)
	 */
	@Override
	public void setSourceNode(NodeModel node) {

		NodeModel currentSourceNode = sourceNode;
		if (currentSourceNode != null) {
			currentSourceNode.preDisconnectAsSource(this);
		}
		if (node != null) {
			node.preConnectAsSource(this);
		}
		sourceNode = node;

		postEdgeSourceNodeChanged(currentSourceNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.EdgeModel#setTargetNode(com.visiors
	 * .visualstage.graph.model.NodeModel)
	 */
	@Override
	public void setTargetNode(NodeModel node) {

		NodeModel currentTargetNode = targetNode;
		if (currentTargetNode != null) {
			currentTargetNode.preDisconnectAsTarget(this);
		}
		if (node != null) {
			node.preConnectAsTarget(this);
		}
		targetNode = node;

		postEdgeSourceNodeChanged(currentTargetNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphObjectModel#deepCopy()
	 */
	@Override
	public EdgeModel deepCopy() {
		DefaultEdgeModel e = new DefaultEdgeModel(-1);
		e.sourceNode = sourceNode;
		e.targetNode = targetNode;
		e.parentGraph = parentGraph;
		e.customObject = customObject.deepCopy();
		return e;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("Edge (").append("id= ").append(getID()).append(", source node= ")
				.append(getSourceNode() != null ? String.valueOf(getSourceNode().getID()) : "null")
				.append(", target node= ")
				.append(getTargetNode() != null ? String.valueOf(getTargetNode().getID()) : "null")
				.append(" ]");
		return sb.toString();
	}

	// /////////////////////////////////////////////////////////////////
	// sending graph events

	protected List<EdgeModelListener> edgeModelListener = new ArrayList<EdgeModelListener>();

	@Override
	public void addEdgeModelListener(EdgeModelListener listener) {

		if (!edgeModelListener.contains(listener)) {
			edgeModelListener.add(listener);
		}
	}

	@Override
	public void removeEdgeModelListener(EdgeModelListener listener) {

		edgeModelListener.remove(listener);
	}

	protected void postEdgeSourceNodeChanged(NodeModel oldNode) {

		for (EdgeModelListener l : edgeModelListener) {
			l.sourceNodeChanged(this, oldNode);
		}
	}

	protected void postEdgeTargetNodeChanged(NodeModel oldNode) {

		for (EdgeModelListener l : edgeModelListener) {
			l.targetNodeChanged(this, oldNode);
		}
	}
}
