package com.visiors.visualstage.model;


import com.visiors.visualstage.model.listener.GraphModelListener;



public interface GraphModel extends NodeModel
{
    
    public void addNode(NodeModel node);
    public void removeNode(NodeModel node);
    public void addEdge(EdgeModel edge);
    public void removeEdge(EdgeModel edge);
    public void clear();
    public NodeModel getNode(long id);
    public EdgeModel getEdge(long id);
    public NodeModel[] getNodes();
    public EdgeModel[] getEdges();
    public GraphElement[] getGraphElements();
    public void setEdgeSourceNode(EdgeModel edge, NodeModel node);
    public void setEdgeTargetNode(EdgeModel edge, NodeModel node);
    public boolean existsNode(NodeModel node);
    public boolean existsEdge(EdgeModel edge);
    public int getLevel();
    public GraphModel deepCopy();
    
    public void addGraphListener(GraphModelListener listener);
    public void removeGraphListener(GraphModelListener listener);
}
