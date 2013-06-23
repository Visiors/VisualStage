package com.visiors.visualstage.model;

public interface GraphElement {

    public long getID();

    public void setCustomData(Object data);

    public Object getCustomData();

    public GraphElement deepCopy();

    // /////////////////////////////////////////////////
    // //// for internal use only

    public GraphModel getGraphModel();

    public void setGraphModel(GraphModel graph);
}
