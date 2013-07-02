package com.visiors.visualstage.graph.view.node;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.visiors.visualstage.graph.view.GraphObjectView;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.node.listener.NodeViewListener;

/**
 * This interface defines the functionality of an <code>edge</code> in context
 * of {@link GraphView}
 * 
 * @author Shane
 * 
 */
public interface NodeView extends GraphObjectView/*
 * , LayoutableNode, Undoable,
 * DockingBase
 */{

	/**
	 * Sets the node's bounding-box
	 */
	public void setBounds(Rectangle r);

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
	public List<EdgeView> getOutgoingEdges();

	/**
	 * returns the list of all incoming edges
	 */
	public List<EdgeView> getIncomingEdges();

	/**
	 * returns the list of all connected edges
	 */
	public List<EdgeView> getConnectedEdges();

	// port
	/**
	 * Returns the node's {@link PortSet}. Ports are usually located on the
	 * node's border and define where locations that edges can be connected to
	 */
	public void setPortSet(PortSet portSetView);

	/**
	 * Sets the {@link PortSet} for the node instance
	 */
	public PortSet getPortSet();

	/**
	 * Ports might be invisible. Illuminating is way to visual them (mostly only
	 * temporarily) so that they can be seen by.
	 * 
	 */
	public void illuminatePorts(boolean open);

	/**
	 * Indicates whether the ports are illuminated.
	 */
	public boolean arePortsilluminated();

	/**
	 * Returns the preferred port for the specified coordinate. The preferred
	 * port is usually the nearest port to the given point.
	 */
	public int getPreferredPort(Point pt);

	/**
	 * Highlighting of a port is a visual way to indicate that an edge,
	 * requesting for connection, is close enough to be accepted by this port.
	 */
	public void highlightPort(int portID, boolean on);



	/**
	 * This method will be called to notify the node about the connect to the
	 * specified <code>opositeNode</code> and <code>edge</code>
	 * 
	 * @see #preConnect(EdgeView)
	 */
	public void postConnected(EdgeView edge, NodeView opositeNode, boolean incomingConnection);

	// /**
	// * This method will be called to request the disconnection from the
	// current
	// * edge. The node can reject the request by returning <code>false</code>.
	// <br>
	// * Note: Accepting the request is no guaranty for actual disconnection
	// since
	// * this requires that all involved parties accept the action in which case
	// * the invocation of the method {@link #postDisconnected(EdgeView)} will
	// * follow.
	// *
	// * @param edge
	// * the <code>EdgeView</code> that is used for the connection.
	// */
	// public boolean preDisconnect(EdgeView edge);

	/**
	 * This method will be called to notify the node about disconnection from
	 * the specified <code>edge</code>
	 * 
	 * @see #preDisconnect(EdgeView)
	 */
	public void postDisconnected(EdgeView edge);

	public void addNodeViewListener(NodeViewListener listener);

	public void removeNodeViewListener(NodeViewListener listener);

}
