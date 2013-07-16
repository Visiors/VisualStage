package com.visiors.visualstage.validation;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.Path;
import com.visiors.visualstage.graph.view.node.VisualNode;


public interface Validator{

	public boolean permitAddingNode(VisualNode node);
	public boolean permitDeletingNode(VisualNode node);
	public boolean permitAddingEdge(VisualEdge edge);
	public boolean permitDeletingEdge(VisualEdge edge);
	public boolean permitMovingEdge(VisualEdge edge, int dx, int dy);
	public boolean permitResizingEdge(VisualEdge edge, Path newPath);
	public boolean permitMovingNode(VisualNode node, int dx, int dy);
	public boolean permitResizingNode(VisualNode node, Rectangle targetBoundary);

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
	public boolean permitConnection(VisualNode sourceNode, int sourcePortId, VisualEdge edge, VisualNode targetNode, int targetPortId);
}
