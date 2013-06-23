package com.visiors.visualstage.model;

public interface EdgeModel extends GraphElement {

    public NodeModel getSourceNode();

    public NodeModel getTargetNode();

    public void setSourceNode(NodeModel node);

    public void setTargetNode(NodeModel node);
}
