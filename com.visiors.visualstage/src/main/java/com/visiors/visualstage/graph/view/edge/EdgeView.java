package com.visiors.visualstage.graph.view.edge;

import com.visiors.visualstage.graph.view.GraphObjectView;
import com.visiors.visualstage.graph.view.listener.EdgeViewListener;
import com.visiors.visualstage.graph.view.node.NodeView;

public interface EdgeView extends GraphObjectView/*
												  * LayoutableEdge, Undoable,
												  * DockingBase,PropertyOwner
												  */{

	public void setPath(Path path);

	public Path getPath();

	public boolean preConnect(NodeView source, int sourcePortId, NodeView target, int targetPortId);

	/**
	 * Connects the specified source- and target-node. This methods calls
	 * {@link EdgeView#preConnect(NodeView, int, NodeView, int)}, and
	 * {@link NodeView#preConnect(EdgeView, NodeView, boolean)} for both source-
	 * and target-node internally to make sure that all involved parties accept
	 * the connection.
	 * <p>
	 * This method makes sures that exiting nodes are detached correctly from
	 * this edges and the new nodes are informed about this connection.
	 * </p>
	 * <p>
	 * To disconnect currently attached nodes pass <code>null</code> as argument
	 * </p>
	 * 
	 */
	public void connect(NodeView source, int sourcePortId, NodeView target, int targetPortId);

	public NodeView getTargetNode();

	public int getTargetPortId();

	public NodeView getSourceNode();

	public int getSourcePortId();

	// public boolean preReassignSource(NodeView newSourceNode, int port);
	//
	// public boolean preReassignTarget(NodeView newTargetNode, int port);

	public void addEdgeViewListener(EdgeViewListener listener);

	public void removeEdgeViewListener(EdgeViewListener listener);

}
