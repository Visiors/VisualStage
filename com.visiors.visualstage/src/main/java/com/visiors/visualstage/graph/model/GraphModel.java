package com.visiors.visualstage.graph.model;

import java.util.List;

/**
 * This interface defines the requirements for an graph without any notion of a
 * visualization. This class can be used on its own for applying graph
 * algorithms.
 */

// TODO mention what events will be fired for each action
public interface GraphModel extends NodeModel {

	/**
	 * Adds the given graph-objects (edges, nodes) to the graph.
	 */
	public void add(GraphObjectModel... graphObjects);

	/**
	 * removes the specified graph-objects (edges, nodes) from the graph
	 */
	public void remove(GraphObjectModel... graphObjects);

	/**
	 * Returns all graph-objects (edges, nodes) in the graph
	 */
	public List<GraphObjectModel> getGraphObjects();

	/**
	 * Removes all nodes and edges from the graph
	 */
	public void clear();

	/**
	 * Returns the node specified by <code>id</code>
	 */
	public NodeModel getNode(long id);

	/**
	 * Returns the edge specified by <code>id</code>
	 */
	public EdgeModel getEdge(long id);

	/**
	 * Returns all nodes in the graph
	 */
	public List<NodeModel> getNodes();

	/**
	 * Returns all edges in the graph
	 */
	public List<EdgeModel> getEdges();

	//
	// /**
	// * Connects the given edge to <code>sourceNode</code>
	// */
	// public void connectEdgeToSourceNode(EdgeModel edge, NodeModel node);
	//
	// /**
	// * Connects the given edge to <code>targetNode</code>
	// */
	// public void connectEdgeToTargetNode(EdgeModel edge, NodeModel node);

	/**
	 * Graphs can be nested. This method returns the depth of a graph in the
	 * graph hierarchy tree. The graph on the root of hierarchy tree has the
	 * depth <code>0</code>
	 */
	public int getDepth();

	public void addGraphListener(GraphModelListener listener);

	public void removeGraphListener(GraphModelListener listener);
}
