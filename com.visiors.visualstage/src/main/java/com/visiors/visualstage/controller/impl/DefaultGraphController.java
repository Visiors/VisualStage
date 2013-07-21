package com.visiors.visualstage.controller.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.controller.GraphController;
import com.visiors.visualstage.editor.GraphEditor;
import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.GraphModel;
import com.visiors.visualstage.graph.model.impl.DefaultEdgeModel;
import com.visiors.visualstage.graph.model.impl.DefaultGraphModel;
import com.visiors.visualstage.graph.model.impl.DefaultNodeModel;
import com.visiors.visualstage.graph.view.edge.EdgePoint;
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
	@Inject
	protected GraphEditor graphEditor;
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

		final List<VisualNode> vNodes = visualGraph.getNodes();
		final List<VisualEdge> vEdges = visualGraph.getEdges();

		if (graphModel == null) {
			graphModel = new DefaultGraphModel();
		} else {
			graphModel.clear();
		}

		for (final VisualNode visualNode : vNodes) {
			graphModel.add(new DefaultNodeModel(visualNode.getID()));
		}

		for (final VisualEdge visualEdge : vEdges) {

			final VisualNode sn = visualEdge.getSourceNode();
			final VisualNode tn = visualEdge.getTargetNode();
			final EdgeModel e = new DefaultEdgeModel(visualEdge.getID());
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
		final PropertyList graphProperties = (PropertyList) properties.get("Graph");
		p = (PropertyUnit) graphProperties.get("id");
		id = ConvertUtil.object2long(p.getValue());
		p = (PropertyUnit) graphProperties.get("name");
		name = ConvertUtil.object2string(p.getValue());
		final VisualGraph gv = createVisualGraph(id, name);
		gv.setProperties(graphProperties);

		/* creating of the nodes... */
		final PropertyList nodesProperties = (PropertyList) graphProperties.get("nodes");
		for (int i = 0; i < nodesProperties.size(); i++) {
			final PropertyList nodeProperties = (PropertyList) nodesProperties.get(i);
			p = (PropertyUnit) nodeProperties.get("id");
			id = ConvertUtil.object2long(p.getValue());
			p = (PropertyUnit) nodeProperties.get("name");
			name = ConvertUtil.object2string(p.getValue());
			final VisualNode nv = createVisualNode(id, name);
			gv.add(nv);
			// set node's properties
			nv.setProperties((PropertyList) nodeProperties.get("Setting"));

			// PortSet
			final PropertyList PortsProperties = (PropertyList) graphProperties.get("Ports");
			if (PortsProperties != null) {
				final PortSet psv = createPortSet();
				psv.setProperties(PortsProperties);
				nv.setPortSet(psv);
			}
		}

		/* creating of the edges... */
		final PropertyList edgesProperties = (PropertyList) graphProperties.get("edges");
		for (int i = 0; i < edgesProperties.size(); i++) {
			final PropertyList edgeProperties = (PropertyList) edgesProperties.get(i);
			p = (PropertyUnit) edgeProperties.get("id");
			id = ConvertUtil.object2long(p.getValue());
			p = (PropertyUnit) edgeProperties.get("name");
			name = ConvertUtil.object2string(p.getValue());

			final VisualEdge ev = createVisualEdge(id, name);
			gv.add(ev);
			ev.setProperties((PropertyList) edgeProperties.get("Setting"));

			final PropertyList pathProperties = (PropertyList) edgeProperties.get("path");
			if (pathProperties != null) {
				ev.setProperties(pathProperties);
			}
		}

		// gv.enableAutoLayout(true);
		// gv.getUndoService().setEnabled(false);

		return gv;
	}

	private PropertyList getGraphViewProperties(VisualGraph visualGraph) {

		final PropertyList all = new DefaultPropertyList("GraphView");

		// graph id
		final long id = visualGraph.getID();
		all.add(new DefaultPropertyUnit("id", new Long(id)));

		// graph view properties
		final PropertyList graphProperties = new DefaultPropertyList("Graph");
		final PropertyList gp = visualGraph.getProperties();
		if (gp != null) {
			graphProperties.add(gp);
		}
		all.add(graphProperties);

		// edges properties
		final PropertyList edgesProperties = new DefaultPropertyList("edges");
		graphProperties.add(edgesProperties);

		final List<VisualEdge> edges = visualGraph.getEdges();
		for (final VisualEdge edge : edges) {
			final PropertyList edgeProperties = new DefaultPropertyList("Edge");
			edgeProperties.add(new DefaultPropertyUnit("id", new Long(edge.getID())));
			final PropertyList ep = edge.getProperties();
			if (ep != null) {
				edgeProperties.add(ep);
			}

			final PropertyList pathProperties = new DefaultPropertyList("path");
			edgeProperties.add(pathProperties);
			final PropertyList epp = edge.getProperties();
			if (ep != null) {
				pathProperties.add(epp);
			}

			edgesProperties.add(edgeProperties);
		}

		// nodes properties
		final PropertyList nodesProperties = new DefaultPropertyList("nodes");
		graphProperties.add(nodesProperties);

		final List<VisualNode> nodes = visualGraph.getNodes();
		for (final VisualNode node : nodes) {
			final PropertyList nodeProperties = new DefaultPropertyList("Node");
			nodeProperties.add(new DefaultPropertyUnit("id", new Long(node.getID())));
			// node's properties
			final PropertyList np = node.getProperties();
			if (np != null) {
				nodeProperties.add(np);
			}
			// ports
			final PortSet ps = node.getPortSet();
			if (ps != null) {

				final PropertyList psp = new DefaultPropertyList("Ports");
				if (psp != null) {
					nodeProperties.add(psp);
				}
			}

			nodesProperties.add(nodeProperties);
		}

		return all;
	}

	protected VisualGraph createVisualGraph(long id, String name) {

		final VisualGraph gv = graphEditor.createContainer(id, name);
		gv.setBounds(new Rectangle(0, 0, 100, 100));
		return gv;
	}

	protected VisualNode createVisualNode(long id, String name) {

		final VisualNode nv = graphEditor.createNode(id, name);
		nv.setBounds(new Rectangle(0, 0, 100, 100));
		nv.setPortSet(createPortSet());
		return nv;
	}

	protected VisualEdge createVisualEdge(long id, String name) {

		final VisualEdge visualEdge = graphEditor.createEdge(id, name);
		final EdgePoint[] edgePoints = new EdgePoint[2];
		edgePoints[0] = new EdgePoint(new Point( 0, 0));
		edgePoints[1] = new EdgePoint(new Point( 100, 100));
		visualEdge.getPath().setPoints(edgePoints, false);
		return visualEdge;
	}

	protected PortSet createPortSet() {

		final PortSet ps = new DefaultPortSet();
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

		for (final GraphModelChangeListener l : graphModelChangelistener) {
			l.graphModelChanged();
		}

	}

}
