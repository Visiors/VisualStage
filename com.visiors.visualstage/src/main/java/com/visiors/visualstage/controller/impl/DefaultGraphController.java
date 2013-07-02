package com.visiors.visualstage.controller.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.controller.GraphController;
import com.visiors.visualstage.graph.mode.impl.DefaultEdgeModel;
import com.visiors.visualstage.graph.mode.impl.DefaultGraphModel;
import com.visiors.visualstage.graph.mode.impl.DefaultNodeModel;
import com.visiors.visualstage.graph.mode.listener.GraphModelChangeListener;
import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.GraphModel;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.stage.GraphFactory;
import com.visiors.visualstage.stage.edge.EdgeView;
import com.visiors.visualstage.stage.graph.GraphView;
import com.visiors.visualstage.stage.node.NodeView;
import com.visiors.visualstage.stage.node.PortSet;
import com.visiors.visualstage.stage.node.impl.DefaultPortSet;
import com.visiors.visualstage.util.ConvertUtil;

public class DefaultGraphController implements GraphController {

    protected GraphModel          graphModel;
    private final List<GraphView> graphViews = new ArrayList<GraphView>();

    public DefaultGraphController(GraphModel graphModel) {

        this.graphModel = graphModel.deepCopy();
    }

    // @Override
    // public GraphView createGraphView() {
    // GraphView graphView = createGraphView(graphModel.getID(), graphModel.getNodeType());
    // graphViews.add(graphView);
    // createViewBasedOnModel(graphView);
    // return graphView;
    // }

    @Override
    public void deleteGraphView(GraphView graphview) {

        graphViews.remove(graphview);
    }

    /* update view based on current graph model */
    @Override
    public void refreshGraphView(GraphView graphview) {

        graphview.clear();

        // createViewBasedOnModel(graphview);
    }

    /*
     * Write back changes to the data model
     */
    @Override
    public void refreshGraphModel(GraphView graphview) {

        // create the graph model from the current view
        createModelBasedOnView(graphview);
    }

    private void createModelBasedOnView(GraphView graphView) {

        NodeView[] vNodes = graphView.getNodes();
        EdgeView[] vEdges = graphView.getEdges();

        if (graphModel == null) {
            graphModel = new DefaultGraphModel();
        } else {
            graphModel.clear();
        }

        for (int i = 0; i < vNodes.length; i++) {
            graphModel.addNode(new DefaultNodeModel(vNodes[i].getID()));
        }

        for (int i = 0; i < vEdges.length; i++) {
            NodeView sn = vEdges[i].getSourceNode();
            NodeView tn = vEdges[i].getTargetNode();
            EdgeModel e = new DefaultEdgeModel(vEdges[i].getID());
            graphModel.addEdge(e);
            if (sn != null) {
                e.setSourceNode(graphModel.getNode(sn.getID()));
            }
            if (tn != null) {
                e.setTargetNode(graphModel.getNode(tn.getID()));
            }
        }
        fireGraphModelChanged();
    }

    // private void createViewBasedOnModel(GraphView graphView) {
    // Node[] nodes = graphModel.getNodes();
    // Edge[] edges = graphModel.getEdges();
    //
    // EdgeView vedge;
    // NodeView vnode;
    //
    // try {
    // // graphView.enableAutoLayout(false);
    // // graphView.enableEvents(false);
    //
    // for (int i = 0; i < nodes.length; i++) {
    // vnode = createNodeView(nodes[i].getID(), nodes[i].getType());
    // graphView.addNode(vnode);
    // }
    //
    // for (int i = 0; i < edges.length; i++) {
    // vedge = createEdgeView(edges[i].getID(), edges[i].getType());
    //
    // graphView.addEdge(vedge);
    //
    // Node sn = edges[i].getSourceNode();
    // Node tn = edges[i].getTargetNode();
    // if (sn != null)
    // vedge.setSourceNode(graphView.getNode(sn.getID()));
    //
    // if (tn != null)
    // vedge.setTargetNode(graphView.getNode(tn.getID()));
    //
    // }
    // } finally {
    // // graphView.enableAutoLayout(true);
    // // graphView.enableEvents(true);
    // }
    // }

    /* creating graph view from properties... */
    private GraphView createGraphViewFromProperties(PropertyList properties) {

        PropertyUnit p;
        long id;
        String name;
        // gv.enableAutoLayout(false);
        // gv.getUndoService().setEnabled(false);

        // set graph properties
        PropertyList graphProperties = (PropertyList) properties.get("Graph");
        p = (PropertyUnit) graphProperties.get("id");
        id = ConvertUtil.object2long(p.getValue());
        p = (PropertyUnit) graphProperties.get("name");
        name = ConvertUtil.object2string(p.getValue());
        GraphView gv = createGraphView(id, name);
        gv.setProperties(graphProperties);

        /* creating of the nodes... */
        PropertyList nodesProperties = (PropertyList) graphProperties.get("nodes");
        for (int i = 0; i < nodesProperties.size(); i++) {
            PropertyList nodeProperties = (PropertyList) nodesProperties.get(i);
            p = (PropertyUnit) nodeProperties.get("id");
            id = ConvertUtil.object2long(p.getValue());
            p = (PropertyUnit) nodeProperties.get("name");
            name = ConvertUtil.object2string(p.getValue());
            NodeView nv = createNodeView(id, name);
            gv.addGraphObject(nv);
            // set node's properties
            nv.setProperties((PropertyList) nodeProperties.get("Setting"));

            // PortSet
            PropertyList PortsProperties = (PropertyList) graphProperties.get("Ports");
            if (PortsProperties != null) {
                PortSet psv = createPortSet();
                psv.setProperties(PortsProperties);
                nv.setPortSet(psv);
            }
        }

        /* creating of the edges... */
        PropertyList edgesProperties = (PropertyList) graphProperties.get("edges");
        for (int i = 0; i < edgesProperties.size(); i++) {
            PropertyList edgeProperties = (PropertyList) edgesProperties.get(i);
            p = (PropertyUnit) edgeProperties.get("id");
            id = ConvertUtil.object2long(p.getValue());
            p = (PropertyUnit) edgeProperties.get("name");
            name = ConvertUtil.object2string(p.getValue());

            EdgeView ev = createEdgeView(id, name);
            gv.addGraphObject(ev);
            ev.setProperties((PropertyList) edgeProperties.get("Setting"));

            PropertyList pathProperties = (PropertyList) edgeProperties.get("path");
            if (pathProperties != null) {
                ev.setProperties(pathProperties);
            }
        }

        // gv.enableAutoLayout(true);
        // gv.getUndoService().setEnabled(false);

        return gv;
    }

    private PropertyList getGraphViewProperties(GraphView graphview) {

        PropertyList all = new DefaultPropertyList("GraphView");

        // graph id
        long id = graphview.getID();
        all.add(new DefaultPropertyUnit("id", new Long(id)));

        // graph view properties
        PropertyList graphProperties = new DefaultPropertyList("Graph");
        PropertyList gp = graphview.getProperties();
        if (gp != null) {
            graphProperties.add(gp);
        }
        all.add(graphProperties);

        // edges properties
        PropertyList edgesProperties = new DefaultPropertyList("edges");
        graphProperties.add(edgesProperties);

        EdgeView[] edges = graphview.getEdges();
        for (int i = 0; i < edges.length; i++) {
            PropertyList edgeProperties = new DefaultPropertyList("Edge");
            edgeProperties.add(new DefaultPropertyUnit("id", new Long(edges[i].getID())));
            PropertyList ep = edges[i].getProperties();
            if (ep != null) {
                edgeProperties.add(ep);
            }

            PropertyList pathProperties = new DefaultPropertyList("path");
            edgeProperties.add(pathProperties);
            PropertyList epp = edges[i].getProperties();
            if (ep != null) {
                pathProperties.add(epp);
            }

            edgesProperties.add(edgeProperties);
        }

        // nodes properties
        PropertyList nodesProperties = new DefaultPropertyList("nodes");
        graphProperties.add(nodesProperties);

        NodeView[] nodes = graphview.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            PropertyList nodeProperties = new DefaultPropertyList("Node");
            nodeProperties.add(new DefaultPropertyUnit("id", new Long(nodes[i].getID())));
            // node's properties
            PropertyList np = nodes[i].getProperties();
            if (np != null) {
                nodeProperties.add(np);
            }
            // ports
            PortSet ps = nodes[i].getPortSet();
            if (ps != null) {

                PropertyList psp = new DefaultPropertyList("Ports");
                if (psp != null) {
                    nodeProperties.add(psp);
                }
            }

            nodesProperties.add(nodeProperties);
        }

        return all;
    }

    protected GraphView createGraphView(long id, String name) {

        GraphView gv = GraphFactory.instance().createContainer(id, name);
        gv.setBounds(new Rectangle(0, 0, 100, 100));
        return gv;
    }

    protected NodeView createNodeView(long id, String name) {

        NodeView nv = GraphFactory.instance().createNode(id, name);
        nv.setBounds(new Rectangle(0, 0, 100, 100));
        nv.setPortSet(createPortSet());
        return nv;
    }

    protected EdgeView createEdgeView(long id, String name) {

        EdgeView ev = GraphFactory.instance().createEdge(id, name);

        Point[] edgePoints = new Point[2];
        edgePoints[0] = new Point(0, 0);
        edgePoints[1] = new Point(100, 100);
        ev.setPoints(edgePoints);
        return ev;
    }

    protected PortSet createPortSet() {

        PortSet ps = new DefaultPortSet();
        ps.createDefaultFourPortSet();
        return ps;
    }

    // ===================================================
    // sending notification to listener
    protected List<GraphModelChangeListener> graphModelChangelistener = new ArrayList<GraphModelChangeListener>();

    @Override
    public void addGraphModelChangeListener(GraphModelChangeListener listener) {

        if (!graphModelChangelistener.contains(listener)) {
            graphModelChangelistener.add(listener);
        }
    }

    @Override
    public void removeGraphModelChangeListener(GraphModelChangeListener listener) {

        graphModelChangelistener.remove(listener);
    }

    protected void fireGraphModelChanged() {

        for (GraphModelChangeListener l : graphModelChangelistener) {
            l.graphModelChanged();
        }

    }

}
