package com.visiors.visualstage.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.visiors.visualstage.model.EdgeModel;
import com.visiors.visualstage.model.GraphElement;
import com.visiors.visualstage.model.GraphModel;
import com.visiors.visualstage.model.NodeModel;
import com.visiors.visualstage.model.listener.GraphModelListener;

public class DefaultGraphModel extends DefaultNodeModel implements GraphModel {

    protected Map<Long, NodeModel> nodeMap = new TreeMap<Long, NodeModel>();
    protected Map<Long, EdgeModel> edgeMap = new TreeMap<Long, EdgeModel>();
    protected List<GraphModelListener> graphModelListener = new ArrayList<GraphModelListener>();

    public DefaultGraphModel() {

        super();
    }

    public DefaultGraphModel(long id) {

        super(id);
    }

    /**
     * @param node
     */
    @Override
    public void addNode(NodeModel node) {

        if (nodeMap.put(node.getID(), node) != null) {
            throw new IllegalArgumentException("The edge cannot be added to graph model. "
                    + "Reason: an object with the id " + node.getID() + " already exists.");
        }

        node.setGraphModel(this);

        postNodeAdded(node);
    }

    /**
     * @param node
     */
    @Override
    public void removeNode(NodeModel node) {

        List<EdgeModel> incomingEdges = node.getIncomingEdges();
        for (EdgeModel edge : incomingEdges) {
            setEdgeTargetNode(edge, null);
        }

        List<EdgeModel> outgoingEdges = node.getOutgoingEdges();
        for (EdgeModel edge : outgoingEdges) {
            setEdgeSourceNode(edge, null);
        }

        nodeMap.remove(node.getID());

        postNodeRemoved(node);
    }

    /**
     * @param edge
     */
    @Override
    public void addEdge(EdgeModel edge) {

        if (edgeMap.put(edge.getID(), edge) != null) {
            throw new IllegalArgumentException("The edge cannot be added to the graph model. "
                    + "Reason: an object with the id " + edge.getID() + " already exists.");
        }

        edge.setGraphModel(this);

        postEdgeAdded(edge);
    }

    /**
     * @param edge
     */
    @Override
    public void removeEdge(EdgeModel edge) {

        if (edge.getSourceNode() != null) {
            setEdgeSourceNode(edge, null);
        }
        if (edge.getTargetNode() != null) {
            setEdgeTargetNode(edge, null);
        }

        edgeMap.remove(edge.getID());

        postEdgeRemoved(edge);
    }

    /**
     * 
     */
    @Override
    public void clear() {

        EdgeModel[] edges = getEdges();
        for (EdgeModel e : edges) {
            removeEdge(e);
        }
        NodeModel[] nodes = getNodes();
        for (NodeModel n : nodes) {
            removeNode(n);
        }
    }

    /**
     * @param id
     * @return
     */
    @Override
    public NodeModel getNode(long id) {

        return nodeMap.get(new Long(id));
    }

    /**
     * @param id
     * @return
     */
    @Override
    public EdgeModel getEdge(long id) {

        return edgeMap.get(new Long(id));
    }

    /**
     * @return
     */
    @Override
    public NodeModel[] getNodes() {

        return nodeMap.values().toArray(new NodeModel[nodeMap.size()]);
    }

    /**
     * @return
     */
    @Override
    public EdgeModel[] getEdges() {

        return edgeMap.values().toArray(new EdgeModel[edgeMap.size()]);
    }

    @Override
    public GraphElement[] getGraphElements() {

        NodeModel[] nodeList = getNodes();
        EdgeModel[] edgeList = getEdges();

        int nNum = nodeList != null ? nodeList.length : 0;
        int eNum = edgeList != null ? edgeList.length : 0;

        GraphElement[] allGraphObjects = new GraphElement[nNum + eNum];

        if (nodeList != null) {
            System.arraycopy(nodeList, 0, allGraphObjects, 0, nNum);
        }
        if (edgeList != null) {
            System.arraycopy(edgeList, 0, allGraphObjects, nNum, eNum);
        }

        return allGraphObjects;
    }

    @Override
    public int getLevel() {

        GraphModel parent = getGraphModel();
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * @param edge
     * @param node
     */
    @Override
    public void setEdgeSourceNode(EdgeModel edge, NodeModel node) {

        NodeModel oldNode = edge.getSourceNode();
        edge.setSourceNode(node);
        postEdgeReconnected(edge, oldNode, true);
    }

    /**
     * @param edge
     * @param node
     */
    @Override
    public void setEdgeTargetNode(EdgeModel edge, NodeModel node) {

        NodeModel oldNode = edge.getTargetNode();

        edge.setTargetNode(node);

        postEdgeReconnected(edge, oldNode, false);
    }

    /**
     * @param node
     * @return
     */
    @Override
    public boolean existsNode(NodeModel node) {

        return nodeMap.containsValue(node);
    }

    /**
     * @param edge
     * @return
     */
    @Override
    public boolean existsEdge(EdgeModel edge) {

        return edgeMap.containsValue(edge);
    }

    @Override
    public GraphModel deepCopy() {

        DefaultGraphModel g = new DefaultGraphModel();

        g.nodeMap = new TreeMap<Long, NodeModel>(nodeMap);
        Collection<NodeModel> nodes = nodeMap.values();
        for (NodeModel n : nodes) {
            nodeMap.put(n.getID(), n);
        }

        Collection<EdgeModel> edges = edgeMap.values();
        for (EdgeModel e : edges) {
            edgeMap.put(e.getID(), e);
        }

        return g;
    }

    @Override
    public String toString() {

        NodeModel sn, tn;
        long snid, tnid;

        NodeModel[] nodes = getNodes();
        EdgeModel[] edges = getEdges();

        StringBuffer sb = new StringBuffer();
        sb.append("AdjacentTable: \nNodes:\n ");
        for (int i = 0; i < nodes.length; i++) {
            sb.append(nodes[i].getID() + ", ");
        }
        sb.append("\nEdges:\n ");
        for (int i = 0; i < edges.length; i++) {
            sn = edges[i].getSourceNode();
            snid = (sn == null ? -1 : sn.getID());

            tn = edges[i].getTargetNode();
            tnid = (tn == null ? -1 : tn.getID());

            sb.append(edges[i].getID());
            sb.append(" [" + snid + " -> " + tnid + "]\n ");
        }
        return sb.toString();
    }

    // /////////////////////////////////////////////////////////////////
    // sending graph events

    /**
     * @param listener
     */
    @Override
    public void addGraphListener(GraphModelListener listener) {

        if (!graphModelListener.contains(listener)) {
            graphModelListener.add(listener);
        }
    }

    /**
     * @param listener
     */
    @Override
    public void removeGraphListener(GraphModelListener listener) {

        graphModelListener.remove(listener);
    }

    protected void postNodeAdded(NodeModel node) {

        for (GraphModelListener l : graphModelListener) {
            l.nodeAdded(node);
        }
    }

    protected void postNodeRemoved(NodeModel node) {

        for (GraphModelListener l : graphModelListener) {
            l.nodeRemoved(node);
        }
    }

    protected void postEdgeAdded(EdgeModel edge) {

        for (GraphModelListener l : graphModelListener) {
            l.edgeAdded(edge);
        }
    }

    protected void postEdgeRemoved(EdgeModel edge) {

        for (GraphModelListener l : graphModelListener) {
            l.edgeRemoved(edge);
        }
    }

    protected void postEdgeReconnected(EdgeModel edge, NodeModel oldNode, boolean sourceNodeChanged) {

        for (GraphModelListener l : graphModelListener) {
            l.edgeReconnected(edge, oldNode, sourceNodeChanged);
        }
    }

}
