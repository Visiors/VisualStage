package com.visiors.visualstage.graph.view.graph;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.visiors.visualstage.graph.view.GraphObjectView;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.NodeView;

public interface GraphView extends NodeView/* , LayoutableGraph */{
	/**
	 * Removes all nodes and edges from the graph
	 */
	public void clear();

	/**
	 * Adds the given graph-objects (edges, nodes) to the graph.
	 */
	public void add(GraphObjectView... graphObject);

	/**
	 * removes the specified graph-objects (edges, nodes) from the graph
	 */
	public void remove(GraphObjectView... graphObject);

	/**
	 * Returns the node specified by <code>id</code>
	 */
	public NodeView getNode(long id);

	/**
	 * Returns the edge specified by <code>id</code>
	 */
	public EdgeView getEdge(long id);

	/**
	 * Returns all nodes in the graph
	 */
	public List<NodeView> getNodes();

	/**
	 * Returns all edges in the graph
	 */
	public List<EdgeView> getEdges();

	/**
	 * Returns all graph-objects (edges, nodes) in the graph
	 */
	public List<GraphObjectView> getGraphObjects();

	/**
	 * Returns all graph-objects inside the specified rectangle
	 */
	public List<GraphObjectView> getGraphObjects(Rectangle rect);

	/**
	 * Returns the graph-object at the given position
	 */
	public List<GraphObjectView> getGraphObjectsAt(Point pt);

	/**
	 * Returns the graph-object at the given position
	 */
	public List<GraphObjectView> getHitObjects(Point pt);

	/**
	 * Graphs can be nested. This method returns the depth of a graph in the
	 * graph hierarchy tree. The graph on the root of hierarchy tree has the
	 * depth <code>0</code>
	 */
	public int getDepth();

	/**
	 * Sends the given graph-object to the front of the drawing order.
	 */
	public void toFront(GraphObjectView gov);

	/**
	 * Sends the given graph-object to the back of the drawing order.
	 */
	public void toBack(GraphObjectView gov);

	/**
	 * Sends the given graph-object one step higher in the drawing order.
	 */
	public void moveForward(GraphObjectView gov);

	/**
	 * Sends the given graph-object one step deeper in the drawing order.
	 */
	public void moveBackward(GraphObjectView gov);

	public List<GraphObjectView> getSelection();

	public void setSelection(List<GraphObjectView> selection);

	public void setSelection(GraphObjectView selection);

	public void clearSelection();

	public void addGraphViewListener(GraphViewListener listener);

	public void removeGraphViewListener(GraphViewListener listener);

}
