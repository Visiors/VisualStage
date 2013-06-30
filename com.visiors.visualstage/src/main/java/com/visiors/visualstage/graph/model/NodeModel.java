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

	/**
	 * This method will be called to inform the node instance that it is about
	 * to be attached as a source-node.
	 * 
	 * @param edge
	 *            the <code>EdgeModel</code> that is going to be used for this
	 *            connection.
	 * 
	 */
	public void preConnectAsSource(EdgeModel edge);

	/**
	 * This method will be called to inform the node instance that it is about
	 * to be detached as a source-node.
	 * 
	 * @param edge
	 *            the <code>EdgeModel</code> that has been used for this
	 *            connection.
	 * 
	 */
	public void preDisconnectAsSource(EdgeModel edge);

	/**
	 * This method will be called to inform the node instance that it is about
	 * to be attached as a target-node.
	 * 
	 * @param edge
	 *            the <code>EdgeModel</code> that is going to be used for this
	 *            connection.
	 * 
	 */
	public void preConnectAsTarget(EdgeModel edge);

	/**
	 * This method will be called to inform the node instance that it is about
	 * to be detached as a target-node.
	 * 
	 * @param edge
	 *            the <code>EdgeModel</code> that has been used for this
	 *            connection.
	 * 
	 */
	public void preDisconnectAsTarget(EdgeModel edge);

}
