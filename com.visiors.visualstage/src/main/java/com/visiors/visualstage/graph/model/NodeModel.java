package com.visiors.visualstage.graph.model;

import java.util.List;

/**
 * <p>
 * This interface represents the functionality of a graph node.
 * </p>
 */
public interface NodeModel extends GraphObjectModel {

	/**
	 * Returns the number of all edges connected to this node
	 */
	public int getDegree();

	/**
	 * Returns the number of edges entering this node.
	 */
	public int getIndegree();

	/**
	 * Returns the number of edges leaving this node.
	 */
	public int getOutdegree();

	/**
	 * returns the list of all outgoing edges
	 */
	public List<EdgeModel> getOutgoingEdges();

	/**
	 * returns the list of all incoming edges
	 */
	public List<EdgeModel> getIncomingEdges();

	/**
	 * returns the list of all connected edges
	 */
	public List<EdgeModel> getConnectedEdges();

	// /////////////////////////////////////////////////
	// //// for internal use only
	public boolean connectToIncomingEdge(EdgeModel edge);

	public boolean connectToOutgoingEdge(EdgeModel edge);

	public boolean disconnectFromIncomingEdge(EdgeModel edge);

	public boolean disconnectFromOutgoingEdge(EdgeModel edge);
}
