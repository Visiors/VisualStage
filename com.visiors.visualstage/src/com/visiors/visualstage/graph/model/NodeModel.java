package com.visiors.visualstage.model;

import java.util.List;

public interface NodeModel extends GraphElement {

    public int getDegree();

    public int getIndegree();

    public int getOutdegree();

    public List<EdgeModel> getOutgoingEdges();

    public List<EdgeModel> getIncomingEdges();

    public EdgeModel[] getConnectedEdges();

    // /////////////////////////////////////////////////
    // //// for internal use only

    void registerIncomingEdge(EdgeModel edge);

    void registerOutgoingEdge(EdgeModel edge);

    void unregisterIncomingEdge(EdgeModel edge);

    void unregisterOutgoingEdge(EdgeModel edge);
}
