package com.visiors.visualstage.graph.view.node;

import java.awt.Point;
import java.util.List;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.listener.VisualNodeListener;

/**
 * This interface defines the functionality of an <code>edge</code> in context
 * of {@link VisualGraph}
 * 
 * @author Shane
 * 
 */
public interface VisualNode extends VisualGraphObject/*
 * , LayoutableNode, Undoable,
 * DockingBase
 */{
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
	public List<VisualEdge> getOutgoingEdges();

	/**
	 * returns the list of all incoming edges
	 */
	public List<VisualEdge> getIncomingEdges();

	/**
	 * returns the list of all connected edges
	 */
	public List<VisualEdge> getConnectedEdges();

	/**
	 * Returns the node's {@link PortSet}. 
	 */
	public void setPortSet(PortSet portSetView);

	/**
	 * Returns the node's {@link PortSet}.
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
	 * @see #preConnect(VisualEdge)
	 */
	public void postConnected(VisualEdge edge, VisualNode opositeNode, boolean incomingConnection);

	/**
	 * This method will be called to notify the node about disconnection from
	 * the specified <code>edge</code>
	 * @param incomingConnection TODO
	 * 
	 * @see #preDisconnect(VisualEdge)
	 */
	public void postDisconnected(VisualEdge edge, boolean incomingConnection);

	public void addNodeViewListener(VisualNodeListener listener);

	public void removeNodeViewListener(VisualNodeListener listener);

}
