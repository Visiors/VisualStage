package com.visiors.visualstage.graph.model;


public interface GraphModelListener {

	public void nodeAdded(NodeModel node);

	public void nodeRemoved(NodeModel node);

	public void edgeAdded(EdgeModel edge);

	public void edgeRemoved(EdgeModel edge);

}
