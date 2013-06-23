package com.visiors.visualstage.graph.model;

import java.util.List;

import com.visiors.visualstage.model.listener.GraphModelListener;

/**
 * This interface defines the requirements for an graph without any notion of a
 * visualization. This class can be used on its own for applying graph
 * algorithms.
 */

// TODO mention what events will be fired for each action
public interface GraphModel extends NodeModel {

	/**
	 * Adds the given node to the graph.
	 */
	public void addNode(NodeModel node);

	/**
	 * removes the specified node from the graph
	 */
	public void removeNode(NodeModel node);

	/**
	 * Adds the given edge to the graph
	 */
	public void addEdge(EdgeModel edge);

	/**
	 * Removes the specified edge from the graph
	 */
	public void removeEdge(EdgeModel edge);

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

	/**
	 * Returns all nodes and edges in the graph
	 */
	public List<GraphObjectModel> getGraphObjects();

	/**
	 * Connects the given edge to <code>sourceNode</code>
	 */
	public void connectEdgeToSourceNode(EdgeModel edge, NodeModel sourceNode);

	/**
	 * Connects the given edge to <code>targetNode</code>
	 */
	public void connectEdgetoTargetNode(EdgeModel edge, NodeModel targetNode);

	/**
	 * Checks if the the given node is a member of the graph
	 */
	public boolean existsNode(NodeModel node);

	/**
	 * 
	 * Checks if the the given edge is a member of the graph
	 */
	public boolean existsEdge(EdgeModel edge);

	/**
	 * Graphs can be nested. This method returns the depth of a graph in the
	 * graph hierarchy tree. The graph on the root of hierarchy tree has the
	 * depth 0
	 */
	public int getDepth();

	public void addGraphListener(GraphModelListener listener);

	public void removeGraphListener(GraphModelListener listener);
}
