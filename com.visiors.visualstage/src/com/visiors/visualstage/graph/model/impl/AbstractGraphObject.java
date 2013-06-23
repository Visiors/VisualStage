package com.visiors.visualstage.model.impl;

import com.visiors.visualstage.IDGenerator;
import com.visiors.visualstage.model.GraphModel;

public class BaseGraphElement implements Cloneable {
    GraphModel graph;
    final long id;
    Object     customData;

    public BaseGraphElement() {

        this(-1);
    }

    public BaseGraphElement(long id) {

        if (id == -1) {
            id = IDGenerator.getNextID();
        } else {
            IDGenerator.considerExistingID(id);
        }

        this.id = id;
    }

    public long getID() {

        return id;
    }

    public void setCustomData(Object data) {

        customData = data;
    }

    public Object getCustomData() {

        return customData;
    }

    // ////////////////////////////////////////////////////////////////////
    // /// For internal use only
    public GraphModel getGraphModel() {

        return graph;
    }

    public void setGraphModel(GraphModel graph) {

        this.graph = graph;
    }

}
