package com.visiors.visualstage.model.impl;

import com.visiors.visualstage.model.EdgeModel;
import com.visiors.visualstage.model.NodeModel;

public class DefaultEdgeModel extends BaseGraphElement implements EdgeModel {
    protected NodeModel sourceNode;
    protected NodeModel targetNode;

    /**
     * Creates a new edge. A unique id will be assigned automatically.
     */
    public DefaultEdgeModel() {
        this(-1);
    }

    /**
     * Creates a new edge with the specified id.
     * 
     * @param id a unique identifier for the edge, or -1 if a unique id should be assigned automatically
     */
    public DefaultEdgeModel(long id) {
        super(id);
    }

    @Override
    public NodeModel getSourceNode() {
        return sourceNode;
    }

    @Override
    public NodeModel getTargetNode() {
        return targetNode;
    }

    @Override
    public void setSourceNode(NodeModel node) {
        node.registerOutgoingEdge(this);
        if (sourceNode != null) {
            sourceNode.unregisterOutgoingEdge(this);
        }
        sourceNode = node;
    }

    @Override
    public void setTargetNode(NodeModel node) {
        node.registerIncomingEdge(this);
        if (targetNode != null) {
            targetNode.unregisterIncomingEdge(this);
        }
        targetNode = node;
    }

    @Override
    public EdgeModel deepCopy() {
        DefaultEdgeModel e = new DefaultEdgeModel(-1);
        e.sourceNode = sourceNode;
        e.targetNode = targetNode;
        e.graph = graph;
        e.customData = customData;
        return e;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("DefaultEdge (").append("id= ").append(getID()).append(", source node= ")
                .append(getSourceNode() != null ? String.valueOf(getSourceNode().getID()) : "Nil")
                .append(", target node= ")
                .append(getTargetNode() != null ? String.valueOf(getTargetNode().getID()) : "Nil").append(" ]");
        return sb.toString();
    }

}
