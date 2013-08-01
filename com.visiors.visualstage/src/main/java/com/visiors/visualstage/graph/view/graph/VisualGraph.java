package com.visiors.visualstage.graph.view.graph;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.impl.GraphNodeVisitor;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.property.PropertyList;

public interface VisualGraph extends VisualNode/* , LayoutableGraph */{

	/** Traverse through all nodes in the current graph and contained subgraphs */
	public void visitNodes(GraphNodeVisitor visitor, boolean preOrder);

	/**
	 * Removes all nodes and edges from the graph
	 */
	public void clear();

	/**
	 * Creates a new {@link VisualNode} and add it to the graph. by cloning the
	 * master-node identified by the given <code>name</code>.
	 * 
	 * @param type
	 *            Identifies the node that si to be used as a template for
	 *            creating the new node.ly .
	 * @return The created instance of {@link VisualNode}.
	 */
	public VisualNode createNode(String type);

	public VisualEdge createEdge(String type);

	public VisualGraph createSubgraph(String type);

	public VisualNode createNode(PropertyList properties);

	public VisualEdge createEdge(PropertyList properties);

	public VisualGraph createSubgraph(PropertyList properties);

	public List<VisualGraphObject> createGraphObjects(PropertyList properties);

	/**
	 * Adds the given graph-objects (edges, nodes) to the graph.
	 */
	public void add(VisualGraphObject... graphObject);

	/**
	 * removes the specified graph-objects (edges, nodes) from the graph
	 */
	public void remove(VisualGraphObject... graphObject);

	/**
	 * Returns the node specified by <code>id</code>
	 */
	public VisualNode getNode(long id);

	/**
	 * Returns the edge specified by <code>id</code>
	 */
	public VisualEdge getEdge(long id);

	/**
	 * Returns all nodes in the graph
	 */
	public List<VisualNode> getNodes();

	/**
	 * Returns all edges in the graph
	 */
	public List<VisualEdge> getEdges();

	/**
	 * Returns all graph-objects (edges, nodes) in the graph
	 */
	public List<VisualGraphObject> getGraphObjects();

	/**
	 * Returns all graph-objects inside the specified rectangle
	 */
	public List<VisualGraphObject> getGraphObjects(Rectangle rect);

	/**
	 * Returns the graph-object at the given position
	 */
	public List<VisualGraphObject> getGraphObjectsAt(Point pt);

	/**
	 * Returns the graph-object at the given position
	 */
	public List<VisualGraphObject> getHitObjects(Point pt);

	/**
	 * Graphs can be nested. This method returns the depth of a graph in the
	 * graph hierarchy tree. The graph on the root of hierarchy tree has the
	 * depth <code>0</code>
	 */
	public int getDepth();

	/**
	 * Sends the given graph-object to the front of the drawing order.
	 */
	public void toFront(VisualGraphObject gov);

	/**
	 * Sends the given graph-object to the back of the drawing order.
	 */
	public void toBack(VisualGraphObject gov);

	/**
	 * Sends the given graph-object one step higher in the drawing order.
	 */
	public void moveForward(VisualGraphObject gov);

	/**
	 * Sends the given graph-object one step deeper in the drawing order.
	 */
	public void moveBackward(VisualGraphObject gov);

	public List<VisualGraphObject> getSelection();

	public void setSelection(List<VisualGraphObject> selection);

	public void setSelection(VisualGraphObject selection);

	public void clearSelection();

	public void addGraphViewListener(GraphViewListener listener);

	public void removeGraphViewListener(GraphViewListener listener);

	public PropertyList getProperties(boolean childrenIncluded);

}
