package com.visiors.visualstage.graph.model;


public interface EdgeModelListener {

	public void sourceNodeChanged(EdgeModel edge, NodeModel oldSourceNode);

	public void targetNodeChanged(EdgeModel edge, NodeModel oldtargetNode);
}
