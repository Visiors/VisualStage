package com.visiors.visualstage.graph.model.impl;

import com.visiors.visualstage.graph.model.Copyable;
import com.visiors.visualstage.graph.model.EdgeModel;
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
	 * Creates a new edge with the given id.
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

	@Override
	public void setSourceNode(NodeModel node) {

		sourceNode = node;
	}

	@Override
	public void setTargetNode(NodeModel node) {

		targetNode = node;
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
		if (customObject instanceof Copyable) {
			e.customObject = ((Copyable) customObject).deepCopy();
		} else {
			e.customObject = customObject;
		}
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

}
