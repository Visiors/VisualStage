package com.visiors.visualstage.model.listener;

import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.NodeModel;

public interface GraphModelListener {

	public void nodeAdded(NodeModel node);

	public void nodeRemoved(NodeModel node);

	public void edgeAdded(EdgeModel edge);

	public void edgeRemoved(EdgeModel edge);

	public void edgeReconnected(EdgeModel edge, NodeModel node, boolean sourceNode);
}
