package com.visiors.visualstage.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.model.EdgeModel;
import com.visiors.visualstage.model.NodeModel;

public class DefaultNodeModel extends BaseGraphElement implements NodeModel {

    private List<EdgeModel> incomingEdges;
    private List<EdgeModel> outgoingEdges;

    /**
     * Creates a new node. An unique id will be assigned automatically.
     */
    public DefaultNodeModel() {
        this(-1);
    }

    /**
     * Creates a new node with the specified id.
     * 
     * @param id a unique identifier for the node, or -1 if a unique id should be assigned automatically
     */
    public DefaultNodeModel(long id) {
        super(id);
        incomingEdges = new ArrayList<EdgeModel>();
        outgoingEdges = new ArrayList<EdgeModel>();
    }

    @Override
    public int getDegree() {
        return getIndegree() + getOutdegree();
    }

    @Override
    public int getIndegree() {
        return incomingEdges.size();
    }

    @Override
    public int getOutdegree() {
        return outgoingEdges.size();
    }

    @Override
    public List<EdgeModel> getOutgoingEdges() {
        return outgoingEdges;
    }

    @Override
    public List<EdgeModel> getIncomingEdges() {
        return incomingEdges;
    }

    @Override
    public EdgeModel[] getConnectedEdges() {
        List<EdgeModel> edges = getIncomingEdges();
        edges.addAll(getOutgoingEdges());
        return edges.toArray(new EdgeModel[edges.size()]);
    }

    @Override
    public NodeModel deepCopy() {
        DefaultNodeModel n = new DefaultNodeModel(-1);
        n.incomingEdges = new ArrayList<EdgeModel>(incomingEdges);
        n.outgoingEdges = new ArrayList<EdgeModel>(outgoingEdges);
        n.graph = graph;
        n.customData = customData;
        return n;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        return sb.append("Node (").append("id= ").append(getID()).append(" ]").toString();
    }

    // //////////////////////////////////////////////////////////////////////////
    // Internal access only

    @Override
    public void registerIncomingEdge(EdgeModel edge) {
        incomingEdges.add(edge);
    }

    @Override
    public void registerOutgoingEdge(EdgeModel edge) {
        outgoingEdges.add(edge);
    }

    @Override
    public void unregisterIncomingEdge(EdgeModel edge) {
        incomingEdges.remove(edge);
    }

    @Override
    public void unregisterOutgoingEdge(EdgeModel edge) {
        outgoingEdges.remove(edge);
    }

}
