package com.visiors.visualstage.controller.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.controller.GraphController;
import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.GraphModel;
import com.visiors.visualstage.graph.model.impl.DefaultEdgeModel;
import com.visiors.visualstage.graph.model.impl.DefaultGraphModel;
import com.visiors.visualstage.graph.model.impl.DefaultNodeModel;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.PortSet;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.graph.view.node.impl.DefaultPortSet;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.util.ConvertUtil;

public class DefaultGraphController implements GraphController {

	protected GraphModel graphModel;
	private final List<VisualGraph> visualGraphs = new ArrayList<VisualGraph>();

	public DefaultGraphController(GraphModel graphModel) {

		this.graphModel = (GraphModel) graphModel.deepCopy();
	}

	// @Override
	// public GraphView createGraphView() {
	// GraphView visualGraph = createGraphView(graphModel.getID(),
	// graphModel.getNodeType());
	// visualGraphs.add(visualGraph);
	// createViewBasedOnModel(visualGraph);
	// return visualGraph;
	// }

	@Override
	public void deleteVisualGraph(VisualGraph visualGraph) {

		visualGraphs.remove(visualGraph);
	}

	/* update view based on current graph model */
	@Override
	public void refreshVisualGraph(VisualGraph visualGraph) {

		visualGraph.clear();

		// createViewBasedOnModel(visualGraph);
	}

	/*
	 * Write back changes to the data model
	 */
	@Override
	public void refreshGraphModel(VisualGraph visualGraph) {

		// create the graph model from the current view
		createModelBasedOnView(visualGraph);
	}

	private void createModelBasedOnView(VisualGraph visualGraph) {

		List<VisualNode> vNodes = visualGraph.getNodes();
		List<VisualEdge> vEdges = visualGraph.getEdges();

		if (graphModel == null) {
			graphModel = new DefaultGraphModel();
		} else {
			graphModel.clear();
		}

		for (VisualNode visualNode : vNodes) {
			graphModel.add(new DefaultNodeModel(visualNode.getID()));
		}

		for (VisualEdge visualEdge : vEdges) {

			VisualNode sn = visualEdge.getSourceNode();
			VisualNode tn = visualEdge.getTargetNode();
			EdgeModel e = new DefaultEdgeModel(visualEdge.getID());
			graphModel.add(e);
			if (sn != null) {
				e.setSourceNode(graphModel.getNode(sn.getID()));
			}
			if (tn != null) {
				e.setTargetNode(graphModel.getNode(tn.getID()));
			}
		}
		fireGraphModelChanged();
	}

	// private void createViewBasedOnModel(GraphView visualGraph) {
	// Node[] nodes = graphModel.getNodes();
	// Edge[] edges = graphModel.getEdges();
	//
	// EdgeView vedge;
	// NodeView vnode;
	//
	// try {
	// // visualGraph.enableAutoLayout(false);
	// // visualGraph.enableEvents(false);
	//
	// for (int i = 0; i < nodes.length; i++) {
	// vnode = createNodeView(nodes[i].getID(), nodes[i].getType());
	// visualGraph.addNode(vnode);
	// }
	//
	// for (int i = 0; i < edges.length; i++) {
	// vedge = createEdgeView(edges[i].getID(), edges[i].getType());
	//
	// visualGraph.addEdge(vedge);
	//
	// Node sn = edges[i].getSourceNode();
	// Node tn = edges[i].getTargetNode();
	// if (sn != null)
	// vedge.setSourceNode(visualGraph.getNode(sn.getID()));
	//
	// if (tn != null)
	// vedge.setTargetNode(visualGraph.getNode(tn.getID()));
	//
	// }
	// } finally {
	// // visualGraph.enableAutoLayout(true);
	// // visualGraph.enableEvents(true);
	// }
	// }

	/* creating graph view from properties... */
	private VisualGraph createGraphViewFromProperties(PropertyList properties) {

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
		VisualGraph gv = createVisualGraph(id, name);
		gv.setProperties(graphProperties);

		/* creating of the nodes... */
		PropertyList nodesProperties = (PropertyList) graphProperties.get("nodes");
		for (int i = 0; i < nodesProperties.size(); i++) {
			PropertyList nodeProperties = (PropertyList) nodesProperties.get(i);
			p = (PropertyUnit) nodeProperties.get("id");
			id = ConvertUtil.object2long(p.getValue());
			p = (PropertyUnit) nodeProperties.get("name");
			name = ConvertUtil.object2string(p.getValue());
			VisualNode nv = createVisualNode(id, name);
			gv.add(nv);
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

			VisualEdge ev = createVisualEdge(id, name);
			gv.add(ev);
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

	private PropertyList getGraphViewProperties(VisualGraph visualGraph) {

		PropertyList all = new DefaultPropertyList("GraphView");

		// graph id
		long id = visualGraph.getID();
		all.add(new DefaultPropertyUnit("id", new Long(id)));

		// graph view properties
		PropertyList graphProperties = new DefaultPropertyList("Graph");
		PropertyList gp = visualGraph.getProperties();
		if (gp != null) {
			graphProperties.add(gp);
		}
		all.add(graphProperties);

		// edges properties
		PropertyList edgesProperties = new DefaultPropertyList("edges");
		graphProperties.add(edgesProperties);

		List<VisualEdge> edges = visualGraph.getEdges();
		for (VisualEdge edge : edges) {
			PropertyList edgeProperties = new DefaultPropertyList("Edge");
			edgeProperties.add(new DefaultPropertyUnit("id", new Long(edge.getID())));
			PropertyList ep = edge.getProperties();
			if (ep != null) {
				edgeProperties.add(ep);
			}

			PropertyList pathProperties = new DefaultPropertyList("path");
			edgeProperties.add(pathProperties);
			PropertyList epp = edge.getProperties();
			if (ep != null) {
				pathProperties.add(epp);
			}

			edgesProperties.add(edgeProperties);
		}

		// nodes properties
		PropertyList nodesProperties = new DefaultPropertyList("nodes");
		graphProperties.add(nodesProperties);

		List<VisualNode> nodes = visualGraph.getNodes();
		for (VisualNode node : nodes) {
			PropertyList nodeProperties = new DefaultPropertyList("Node");
			nodeProperties.add(new DefaultPropertyUnit("id", new Long(node.getID())));
			// node's properties
			PropertyList np = node.getProperties();
			if (np != null) {
				nodeProperties.add(np);
			}
			// ports
			PortSet ps = node.getPortSet();
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

	protected VisualGraph createVisualGraph(long id, String name) {

		VisualGraph gv = GraphFactory.instance().createContainer(id, name);
		gv.setBounds(new Rectangle(0, 0, 100, 100));
		return gv;
	}

	protected VisualNode createVisualNode(long id, String name) {

		VisualNode nv = GraphFactory.instance().createNode(id, name);
		nv.setBounds(new Rectangle(0, 0, 100, 100));
		nv.setPortSet(createPortSet());
		return nv;
	}

	protected VisualEdge createVisualEdge(long id, String name) {

		VisualEdge ev = GraphFactory.instance().createEdge(id, name);

		Point[] edgePoints = new Point[2];
		edgePoints[0] = new Point(0, 0);
		edgePoints[1] = new Point(100, 100);
		ev.getPath().setPoints(edgePoints);
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
