package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.graph.view.GraphObjectView;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.listener.GraphViewAdapter;
import com.visiors.visualstage.graph.view.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.NodeView;

public class Depot extends GraphViewAdapter implements GraphViewListener {

	private final DepotObjectContainer container;
	private int edges;
	private int nodes;
	private int subgraphs;

	// private final boolean selectionOnTop = true;
	private final DefaultGraphView graphView;
	private final Rectangle expansion = new Rectangle();;

	Depot(DefaultGraphView graphView) {

		graphView.addGraphViewListener(this);
		this.graphView = graphView;
		container = new DepotObjectContainer();
		clear();
	}

	void add(GraphObjectView vgo) {

		container.add(vgo);
		increaseObjectCounter(vgo);
		checkExpansionAndSendNotification();
	}

	void remove(GraphObjectView vgo) {

		container.delete(vgo);
		decreaseObjectCounter(vgo);
		checkExpansionAndSendNotification();
	}

	GraphObjectView getObject(long id) {

		return container.getObject(id);
	}

	NodeView[] getNodes() {

		int i = 0;
		NodeView[] ns = new NodeView[nodes];
		if (nodes > 0) {
			GraphObjectView[] objects = container.getObjects();
			for (GraphObjectView n : objects) {
				if (n instanceof NodeView) {
					ns[i++] = (NodeView) n;
				}
			}
		}
		return ns;
	}

	EdgeView[] getEdges() {

		int i = 0;
		EdgeView[] es = new EdgeView[edges];
		if (edges > 0) {
			GraphObjectView[] objects = container.getObjects();
			for (GraphObjectView e : objects) {
				if (e instanceof EdgeView) {
					es[i++] = (EdgeView) e;
				}
			}
		}
		return es;
	}

	GraphObjectView[] getObjects() {

		return container.getObjects();
		// GraphObjectView[] objects = container.getObjects();
		// if (!includingNestedObjects)
		// return objects;
		// return objects;
		//
		// List<GraphObjectView> all = new
		// ArrayList<GraphObjectView>(objects.length * 2);
		// for (int i = 0; i < objects.length; i++) {
		// all.add(objects[i]);
		// if (objects[i] instanceof GraphView) {
		// all = digUpSubgraphObjects((GraphView) objects[i], all);
		// }
		// }
		// return all.toArray(new GraphObjectView[all.size()]);
	}

	void clear() {

		container.clear();

		edges = 0;
		nodes = 0;
		subgraphs = 0;
	}

	GraphObjectView[] getHitObjects(Point pt) {

		// first pick objects of with the bounding box is hit
		GraphObjectView[] candidates = container.getObjectToDraw();

		// consider only objects that consider themselves as hit
		List<GraphObjectView> hitObjects = new ArrayList<GraphObjectView>();
		for (GraphObjectView candidate : candidates) {
			if (candidate.isHit(pt)) {
				hitObjects.add(candidate);
				if (candidate instanceof GraphView) {
					List<GraphObjectView> hitNestedOject = ((GraphView) candidate)
							.getGraphObjectsAt(pt);
					for (GraphObjectView element : hitNestedOject) {
						hitObjects.add(element);
					}
				}
			}
		}
		return hitObjects.toArray(new GraphObjectView[hitObjects.size()]);
	}

	GraphObjectView[] getGraphObjects(Rectangle r) {

		return container.getObjectToDraw();

	}

	int getEdgeCount() {

		return edges;
	}

	int getNodeCount() {

		return nodes;
	}

	int getSubgraphCount() {

		return subgraphs;
	}

	Rectangle getTotalExpansion() {

		return expansion;

	}

	private void increaseObjectCounter(GraphObjectView vgo) {

		if (vgo instanceof NodeView) {
			nodes++;
			if (vgo instanceof GraphView) {
				subgraphs++;
			}
		} else {
			edges++;
		}
	}

	private void decreaseObjectCounter(GraphObjectView vgo) {

		if (vgo instanceof NodeView) {
			nodes--;
			if (vgo instanceof GraphView) {
				subgraphs--;
			}
		} else {
			edges--;
		}
	}

	// private List<GraphObjectView> digUpSubgraphObjects(GraphView subgraph,
	// List<GraphObjectView> all) {
	//
	// GraphObjectView[] objects = subgraph.getGraphObjects();
	// for (int i = 0; i < objects.length; i++) {
	// all.add(objects[i]);
	// if (objects[i] instanceof GraphView)
	// all = digUpSubgraphObjects((GraphView) objects[i], all);
	// }
	// return all;
	// }
	//
	// private List<GraphObjectView> digUpSubgraphObjects(Rectangle r, GraphView
	// subgraph,
	// List<GraphObjectView> all) {
	//
	// GraphObjectView[] objects = subgraph.getGraphObjects(r, false);
	// for (int i = 0; i < objects.length; i++) {
	// all.add(objects[i]);
	// if (objects[i] instanceof GraphView)
	// all = digUpSubgraphObjects(r, (GraphView) objects[i], all);
	// }
	// return all;
	// }

	void toFront(GraphObjectView gov) {

		container.toFront(gov);
	}

	void toBack(GraphObjectView gov) {

		container.toBack(gov);
	}

	void moveForward(GraphObjectView gov) {

		container.moveForward(gov);
	}

	void moveBackward(GraphObjectView gov) {

		container.moveBackward(gov);
	}

	private void fireGraphExpansionChanged() {

		graphView.fireGraphExpansionChanged(expansion);
	}

	private void checkExpansionAndSendNotification() {

		Rectangle currentExpansion = container.getTotalExpansion();
		if (!expansion.equals(currentExpansion)) {
			expansion.setBounds(currentExpansion);
			fireGraphExpansionChanged();
		}
	}

	Rectangle getExpansion() {

		return new Rectangle(container.getTotalExpansion());
	}

	// //////////////////////////////////////////////////////////////
	// // GraphView events

	// @Override
	// public void nodeBoundaryChangning(NodeView node) {
	// container.setObjectBoundaryChanged(node);
	// checkExpansionAndSendNotification();
	// }
	//
	// @Override
	// public void edgePathChanging(EdgeView edge) {
	// container.setObjectBoundaryChanged(edge);
	// checkExpansionAndSendNotification();
	// }

	@Override
	public void nodeStoppedChangingBoundary(NodeView node, Rectangle oldBoundary) {

		container.setObjectBoundaryChanged(node);
		checkExpansionAndSendNotification();
	}

	@Override
	public void edgeStoppedChangingPath(EdgeView edge, Point[] oldPath) {

		container.setObjectBoundaryChanged(edge);
		checkExpansionAndSendNotification();
	}

	@Override
	public void edgeRemoved(EdgeView edge) {

		container.setObjectBoundaryChanged(edge);
		checkExpansionAndSendNotification();
	}

	@Override
	public void nodeRemoved(NodeView node) {

		container.setObjectBoundaryChanged(node);
		checkExpansionAndSendNotification();
	}

	@Override
	public void edgeAdded(EdgeView edge) {

		container.setObjectBoundaryChanged(edge);
		checkExpansionAndSendNotification();
	}

	@Override
	public void nodeAdded(NodeView node) {

		container.setObjectBoundaryChanged(node);
		checkExpansionAndSendNotification();
	}
}
