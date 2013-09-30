package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewAdapter;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.VisualNode;

public class Depot extends GraphViewAdapter implements GraphViewListener {

	private final DepotObjectContainer container;
	private int edges;
	private int nodes;
	private int subgraphs;

	// private final boolean selectionOnTop = true;
	private final DefaultVisualGraph visualGraph;
	private final Rectangle expansion = new Rectangle();;

	Depot(DefaultVisualGraph visualGraph) {

		visualGraph.addGraphViewListener(this);
		this.visualGraph = visualGraph;
		container = new DepotObjectContainer();
		clear();
	}

	void add(VisualGraphObject vgo) {

		container.add(vgo);
		increaseObjectCounter(vgo);
		checkExpansionAndSendNotification();
	}

	void remove(VisualGraphObject vgo) {

		container.delete(vgo);
		decreaseObjectCounter(vgo);
		checkExpansionAndSendNotification();
	}

	VisualGraphObject getObject(long id) {

		return container.getObject(id);
	}

	VisualNode[] getNodes() {

		int i = 0;
		final VisualNode[] ns = new VisualNode[nodes];
		if (nodes > 0) {
			final VisualGraphObject[] objects = container.getObjects();
			for (final VisualGraphObject n : objects) {
				if (n instanceof VisualNode) {
					ns[i++] = (VisualNode) n;
				}
			}
		}
		return ns;
	}

	VisualEdge[] getEdges() {

		int i = 0;
		final VisualEdge[] es = new VisualEdge[edges];
		if (edges > 0) {
			final VisualGraphObject[] objects = container.getObjects();
			for (final VisualGraphObject e : objects) {
				if (e instanceof VisualEdge) {
					es[i++] = (VisualEdge) e;
				}
			}
		}
		return es;
	}

	VisualGraphObject[] getObjects() {

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
		// if (objects[i] instanceof graphView) {
		// all = digUpSubgraphObjects((graphView) objects[i], all);
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

	VisualGraphObject[] getHitObjects(Point pt) {

		// first pick objects of with the bounding box is hit
		final VisualGraphObject[] candidates = container.getObjectToDraw();

		// consider only objects that consider themselves as hit
		final List<VisualGraphObject> hitObjects = new ArrayList<VisualGraphObject>();
		for (final VisualGraphObject candidate : candidates) {
			if (candidate.isHit(pt)) {
				hitObjects.add(candidate);
				if (candidate instanceof VisualGraph) {
					final List<VisualGraphObject> hitNestedOject = ((VisualGraph) candidate).getGraphObjectsAt(pt);
					for (final VisualGraphObject element : hitNestedOject) {
						hitObjects.add(element);
					}
				}
			}
		}
		return hitObjects.toArray(new VisualGraphObject[hitObjects.size()]);
	}

	VisualGraphObject[] getGraphObjects(Rectangle r) {

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

	private void increaseObjectCounter(VisualGraphObject vgo) {

		if (vgo instanceof VisualNode) {
			nodes++;
			if (vgo instanceof VisualGraph) {
				subgraphs++;
			}
		} else {
			edges++;
		}
	}

	private void decreaseObjectCounter(VisualGraphObject vgo) {

		if (vgo instanceof VisualNode) {
			nodes--;
			if (vgo instanceof VisualGraph) {
				subgraphs--;
			}
		} else {
			edges--;
		}
	}

	// private List<GraphObjectView> digUpSubgraphObjects(graphView subgraph,
	// List<GraphObjectView> all) {
	//
	// GraphObjectView[] objects = subgraph.getGraphObjects();
	// for (int i = 0; i < objects.length; i++) {
	// all.add(objects[i]);
	// if (objects[i] instanceof graphView)
	// all = digUpSubgraphObjects((graphView) objects[i], all);
	// }
	// return all;
	// }
	//
	// private List<GraphObjectView> digUpSubgraphObjects(Rectangle r, graphView
	// subgraph,
	// List<GraphObjectView> all) {
	//
	// GraphObjectView[] objects = subgraph.getGraphObjects(r, false);
	// for (int i = 0; i < objects.length; i++) {
	// all.add(objects[i]);
	// if (objects[i] instanceof graphView)
	// all = digUpSubgraphObjects(r, (graphView) objects[i], all);
	// }
	// return all;
	// }

	void toFront(VisualGraphObject gov) {

		container.toFront(gov);
	}

	void toBack(VisualGraphObject gov) {

		container.toBack(gov);
	}

	void moveForward(VisualGraphObject gov) {

		container.moveForward(gov);
	}

	void moveBackward(VisualGraphObject gov) {

		container.moveBackward(gov);
	}

	private void fireGraphExpansionChanged() {

		visualGraph.fireGraphExpansionChanged();
	}

	private void checkExpansionAndSendNotification() {

		final Rectangle currentExpansion = container.getTotalExpansion();
		if (!expansion.equals(currentExpansion)) {
			expansion.setBounds(currentExpansion);
			fireGraphExpansionChanged();
		}
	}

	Rectangle getExpansion() {

		return new Rectangle(container.getTotalExpansion());
	}

	// //////////////////////////////////////////////////////////////
	// // graphView events

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
	public void nodeStoppedChangingBoundary(VisualNode node, Rectangle oldBoundary) {

		container.setObjectBoundaryChanged(node);
		checkExpansionAndSendNotification();
	}

	@Override
	public void edgeStoppedChangingPath(VisualEdge edge, EdgePoint[] oldPath) {

		container.setObjectBoundaryChanged(edge);
		checkExpansionAndSendNotification();
	}

	@Override
	public void edgeRemoved(VisualEdge edge) {

		container.setObjectBoundaryChanged(edge);
		checkExpansionAndSendNotification();
	}

	@Override
	public void nodeRemoved(VisualNode node) {

		container.setObjectBoundaryChanged(node);
		checkExpansionAndSendNotification();
	}

	@Override
	public void edgeAdded(VisualEdge edge) {

		container.setObjectBoundaryChanged(edge);
		checkExpansionAndSendNotification();
	}

	@Override
	public void nodeAdded(VisualNode node) {

		container.setObjectBoundaryChanged(node);
		checkExpansionAndSendNotification();
	}
}
