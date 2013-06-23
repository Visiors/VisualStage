package com.visiors.visualstage.graph.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.graph.model.Copyable;
import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.NodeModel;

/**
 * This is a default implementation for {@link NodeModel}.
 * 
 * 
 */
public class DefaultNodeModel extends AbstractGraphObject implements NodeModel {

	protected List<EdgeModel> incomingEdges;
	protected List<EdgeModel> outgoingEdges;

	/**
	 * Creates a new node. An unique id will be assigned automatically.
	 */
	public DefaultNodeModel() {
		this(-1);
	}

	/**
	 * Creates a new node with the specified id.
	 * 
	 * @param id
	 *            a unique identifier for the node, or -1 if a unique id should
	 *            be assigned automatically
	 */
	public DefaultNodeModel(long id) {
		super(id);
		incomingEdges = new ArrayList<EdgeModel>();
		outgoingEdges = new ArrayList<EdgeModel>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.NodeModel#getDegree()
	 */
	@Override
	public int getDegree() {
		return getIndegree() + getOutdegree();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.NodeModel#getIndegree()
	 */
	@Override
	public int getIndegree() {
		return incomingEdges.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.NodeModel#getOutdegree()
	 */
	@Override
	public int getOutdegree() {
		return outgoingEdges.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.NodeModel#getOutgoingEdges()
	 */
	@Override
	public List<EdgeModel> getOutgoingEdges() {
		return outgoingEdges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.NodeModel#getIncomingEdges()
	 */
	@Override
	public List<EdgeModel> getIncomingEdges() {
		return incomingEdges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.NodeModel#getConnectedEdges()
	 */
	@Override
	public List<EdgeModel> getConnectedEdges() {
		final List<EdgeModel> edges = getIncomingEdges();
		edges.addAll(getOutgoingEdges());
		return edges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphObjectModel#deepCopy()
	 */
	@Override
	public NodeModel deepCopy() {
		final DefaultNodeModel n = new DefaultNodeModel(-1);
		n.incomingEdges = new ArrayList<EdgeModel>(incomingEdges);
		n.outgoingEdges = new ArrayList<EdgeModel>(outgoingEdges);
		n.parentGraph = parentGraph;
		if (customObject instanceof Copyable) {
			n.customObject = ((Copyable) customObject).deepCopy();
		} else {
			n.customObject = customObject;
		}
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		return sb.append("Node (").append("id= ").append(getID()).append(" ]").toString();
	}

	// //////////////////////////////////////////////////////////////////////////
	// Internal access only

	@Override
	public boolean connectToIncomingEdge(EdgeModel edge) {
		incomingEdges.add(edge);
		return true;
	}

	@Override
	public boolean connectToOutgoingEdge(EdgeModel edge) {
		outgoingEdges.add(edge);
		return true;
	}

	@Override
	public boolean disconnectFromIncomingEdge(EdgeModel edge) {
		incomingEdges.remove(edge);
		return true;
	}

	@Override
	public boolean disconnectFromOutgoingEdge(EdgeModel edge) {
		outgoingEdges.remove(edge);
		return true;
	}

}
