package com.visiors.visualstage.validation;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.edge.Path;
import com.visiors.visualstage.graph.view.node.NodeView;


public interface Validator{

	public boolean permitAddingNode(NodeView node);
	public boolean permitDeletingNode(NodeView node);
	public boolean permitAddingEdge(EdgeView edge);
	public boolean permitDeletingEdge(EdgeView edge);
	public boolean permitMovingEdge(EdgeView edge, int dx, int dy);
	public boolean permitResizingEdge(EdgeView edge, Path newPath);
	public boolean permitMovingNode(NodeView node, int dx, int dy);
	public boolean permitResizingNode(NodeView node, Rectangle targetBoundary);

	/**
	 * This method is called to ask for permission for a new connection. 
	 * To reject the request return<code>false</code>; otherwise <code>true</code>
	 *
	 * @param sourceNode the new source-node
	 * @param sourcePortId the new source port
	 * @param edge the current edge.  
	 * @param targetNode the new target-node
	 * @param targetPortId the new target port
	 * @return true to <code>allow</code> the new connection; otherwise <code>false</code>
	 */
	public boolean permitConnection(NodeView sourceNode, int sourcePortId, EdgeView edge, NodeView targetNode, int targetPortId);
}
