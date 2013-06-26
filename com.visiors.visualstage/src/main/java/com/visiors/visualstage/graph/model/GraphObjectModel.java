package com.visiors.visualstage.graph.model;

import com.visiors.visualstage.graph.CustomData;

/**
 * 
 * <p>
 * This interface defines methods that are common to all graph elements; i.e.
 * nodes, edges and sub-graphs.
 * </p>
 * <p>
 * <code>GraphObjects</code> have an identifiable nature. Every
 * <code>GraphObject</code> can be identified by an its unique id.
 * </p>
 */

public interface GraphObjectModel {

	/**
	 * Returns the object's global identifier
	 */
	public long getID();

	/**
	 * Allows clients to attach an object to a this {@link GraphObjectModel} .
	 * This is a convenient way for client applications to store additional
	 * information to this graph element. <br>
	 * Note: The custom data needs to implement the interface
	 * {@link GraphObjectModel} in order to be cloned and store properly.
	 */
	// TODOL object must implement attributable in order to be persistable
	public void setCustomObject(CustomData data);

	/**
	 * returns the custom object attached to this graph element
	 */
	public CustomData getCustomObject();

	/**
	 * This method returns the {@link GraphModel} in which this
	 * <code>GraphObject</code> instance is currently hosted
	 */
	public GraphModel getParentGraph();

	/**
	 * Informs the <code>graph object</code> about the graph in which it is
	 * hosted. This method will be called when ever the
	 * <code>graph object</code> in inserted into a graph, or when it is moved
	 * from one graph into another
	 */
	public void setParentGraph(GraphModel parentGraph);

	/**
	 * Returns a deep copy of the <code>GraphObject</code>.
	 */
	public GraphObjectModel deepCopy();

}
