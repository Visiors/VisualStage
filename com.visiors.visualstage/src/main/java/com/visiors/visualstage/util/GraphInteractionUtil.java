package com.visiors.visualstage.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.Port;
import com.visiors.visualstage.graph.view.node.PortSet;
import com.visiors.visualstage.graph.view.node.VisualNode;

public class GraphInteractionUtil {

	public static VisualNode getFirstHitNodeAt(VisualGraph rootGraphView, Point pt) {

		List<VisualGraphObject> hitObjects = getHitObjects(rootGraphView, pt, false, false, true);
		if (hitObjects.size() > 0) {
			return (VisualNode) hitObjects.get(0);
		}
		return null;
	}

	public static VisualEdge getFirstHitEdgeAt(VisualGraph rootGraphView, Point pt) {

		List<VisualGraphObject> hitObjects = getHitObjects(rootGraphView, pt, true, true, false);
		if (hitObjects.size() > 0) {
			return (VisualEdge) hitObjects.get(0);
		}
		return null;
	}

	public static VisualGraph getFirstHitGroupAt(VisualGraph rootGraphView, Point pt) {

		List<VisualGraphObject> hitObjects = getHitObjects(rootGraphView, pt, false, true, true);
		if (hitObjects.size() > 0) {
			return (VisualGraph) hitObjects.get(0);
		}
		return null;
	}

	public static VisualGraph getLastGroupHitByObjects(VisualGraph rootGraphView, List<VisualGraphObject> objects) {

		for (VisualGraphObject vgo : objects) {
			VisualGraph gv = getLastGroupHitByObjects(rootGraphView, vgo);
			if (gv != null) {
				return gv;
			}
		}
		return null;
	}

	public static VisualGraph getLastGroupHitByObjects(VisualGraph rootGraphView, VisualGraphObject vgo) {

		VisualGraph deepestGroup = null;
		Rectangle r = vgo.getBounds();
		if (r == null) {
			return null;
		}
		List<VisualGraphObject> hitObjects;
		List<VisualGraphObject> allHitObjects = new ArrayList<VisualGraphObject>();

		hitObjects = getHitObjects(rootGraphView, new Point(r.x, r.y), false, true, true);
		for (VisualGraphObject gv : hitObjects) {
			if (gv != vgo && !allHitObjects.contains(gv)) {
				allHitObjects.add(gv);
			}
		}
		hitObjects = getHitObjects(rootGraphView, new Point(r.x + r.width, r.y), false, true, true);
		for (VisualGraphObject gv : hitObjects) {
			if (gv != vgo && !allHitObjects.contains(gv)) {
				allHitObjects.add(gv);
			}
		}
		hitObjects = getHitObjects(rootGraphView, new Point(r.x, r.y + r.height), false, true, true);
		for (VisualGraphObject gv : hitObjects) {
			if (gv != vgo && !allHitObjects.contains(gv)) {
				allHitObjects.add(gv);
			}
		}
		hitObjects = getHitObjects(rootGraphView, new Point(r.x + r.width, r.y + r.height), false, true, true);
		for (VisualGraphObject gv : hitObjects) {
			if (gv != vgo && !allHitObjects.contains(gv)) {
				allHitObjects.add(gv);
			}
		}

		if (allHitObjects.size() != 0) {
			for (VisualGraphObject gv : allHitObjects) {
				if (deepestGroup == null || deepestGroup.getDepth() < ((VisualGraph) gv).getDepth()) {
					deepestGroup = ((VisualGraph) gv);
				}
			}
		}

		return deepestGroup;
	}

	private static Rectangle calcSelectionBoundary(List<VisualGraphObject> selection) {

		Rectangle r = null;
		for (VisualGraphObject vgo : selection) {
			if (r == null) {
				r = vgo.getBounds();
			} else {
				r = r.union(vgo.getBounds());
			}
		}
		return r;
	}

	public static VisualGraphObject getFirstHitObjectAt(VisualGraph rootGraphView, Point pt) {

		List<VisualGraphObject> hitObjects = getHitObjects(rootGraphView, pt, false, false, false);
		if (hitObjects.size() > 0) {
			return hitObjects.get(0);
		}
		return null;
	}

	public static VisualGraphObject getClosestObject(VisualGraph visualGraph, VisualGraphObject refObject,
			boolean ignoreGroups, boolean ignoreNodes, boolean ignoreEdges, boolean ignoreOverlaps) {

		double minDist = Double.MAX_VALUE;
		VisualGraphObject bestmatch = null;

		if (!ignoreNodes || !ignoreGroups) {
			Rectangle b, bref;
			int dx, dy;
			double r;

			bref = refObject.getBounds();
			Point cpt = new Point(bref.x + bref.width / 2, bref.y + bref.height / 2);

			List<VisualNode> nodes = visualGraph.getNodes();
			for (VisualNode node : nodes) {

				if (node.getID() == refObject.getID()) {
					continue;
				}

				if ((node instanceof VisualGraph && !ignoreGroups) || !ignoreNodes) {
					b = node.getBounds();
					if (ignoreOverlaps && bref.intersects(b)) {
						continue;
					}

					dx = Math.abs(b.x + b.width / 2 - cpt.x);
					dy = Math.abs(b.y + b.height / 2 - cpt.y);
					r = Math.sqrt(dx * dx + dy * dy);
					if (r < minDist) {
						minDist = r;
						;
						bestmatch = node;
					}
				}
			}
		}
		if (!ignoreEdges) {
			// TODO
			// EdgeView[] edges = visualGraph.getEdges();
			// for (int i = 0; i < edges.length; i++) {
			//
			// }
		}
		return bestmatch;
	}

	public static VisualGraphObject getClosestNode(VisualGraph visualGraph, VisualGraphObject refObject, int maxDistance,
			boolean ignoreOverlaps, Port[] posrts) {

		double minDist = Double.MAX_VALUE;
		VisualGraphObject bestmatch = null;
		Rectangle b;
		double r;
		Rectangle bref = refObject.getBounds();

		List<VisualNode> nodes = visualGraph.getNodes();
		for (VisualNode node : nodes) {


			if (node.getID() == refObject.getID()) {
				continue;
			}

			if (node instanceof VisualGraph) {
				VisualGraphObject no = getClosestNode((VisualGraph) node, refObject, maxDistance, ignoreOverlaps,
						posrts);
				if (no != null) {
					return no;
				}
			}

			b = node.getBounds();
			if (!ignoreOverlaps) {
				if (bref.intersects(b)) {
					continue;
				}
			}

			double alpha = getAngle(bref, b);
			Port pRef = getAvailableport((VisualNode) refObject, alpha);
			if (pRef == null) {
				continue;
			}
			// find the port on the opposite side
			int beta = portMidAngle(pRef);
			Port pNode = getAvailableport(node, beta + 180);

			if (pNode == null) {
				continue;
			}

			Point pt1 = pRef.getPosition();
			Point pt2 = pNode.getPosition();
			r = distance(pt1.x, pt1.y, pt2.x, pt2.y);
			if (r < maxDistance && r < minDist) {
				minDist = r;
				bestmatch = node;
				if (posrts != null) {
					posrts[0] = pRef;
					posrts[1] = pNode;
				}
			}
		}
		return bestmatch;
	}

	private static int portMidAngle(Port p) {

		int[] aa = p.getAcceptedInterval();

		if (aa == null) {
			return -1;
		}

		if (aa[1] < aa[0]) {
			aa[1] += 360;
		}
		return aa[0] + (aa[1] - aa[0]) / 2;
	}

	public final static double distance(int x1, int y1, int x2, int y2) {

		final double dx = Math.abs(x2 - x1);
		final double dy = Math.abs(y2 - y1);
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static Port[] getClosestPortPair(Port[] ports1, Port[] ports2) {

		Port[] res = new Port[2];
		double minDist = Double.MAX_VALUE;
		Point pt1;
		Point pt2;
		double dx;
		double dy;
		double d;
		for (Port port1 : ports1) {

			pt1 = port1.getPosition();
			for (Port port2 : ports2) {
				pt2 = port2.getPosition();
				dx = Math.abs(pt2.x - pt1.x);
				dy = Math.abs(pt2.y - pt1.y);
				d = Math.sqrt(dx * dx + dy * dy);
				if (d < minDist) {
					minDist = d;
					res[0] = port1;
					res[1] = port2;
				}
			}
		}
		return res;
	}

	private static double getAngle(Rectangle b1, Rectangle b2) {

		return Math.toDegrees(Math.atan2(b1.y + b1.height / 2 - b2.y - b2.height / 2, b2.x + b2.width / 2 - b1.x
				- b1.width / 2));
	}

	private static Port getAvailableport(VisualNode node, double alph) {

		final PortSet ps = node.getPortSet();
		final int portID = ps.getPortByAngle(alph);

		return ps.getPortByID(portID);
	}

	public static List<VisualGraphObject> getHitObjects(VisualGraph visualGraph, Point pt, boolean ignoreGroups,
			boolean ignoreNodes, boolean ignoreEdges) {

		List<VisualGraphObject> result = new ArrayList<VisualGraphObject>();

		recursiveSearchForHitObject(visualGraph, pt, ignoreGroups, ignoreNodes, ignoreEdges, result);

		return result;
	}

	private static List<VisualGraphObject> recursiveSearchForHitObject(VisualGraph visualGraph, Point pt,
			boolean ignoreGroups, boolean ignoreNodes, boolean ignoreEdges, List<VisualGraphObject> hitList) {

		List<VisualGraphObject> hitObjects = visualGraph.getHitObjects(pt);

		for (int i = hitObjects.size() - 1; i >= 0; i--) {
			VisualGraphObject vgo = hitObjects.get(i);
			if (vgo != null) {

				if (vgo instanceof VisualEdge && !ignoreEdges) {
					hitList.add(vgo);
				} else if (vgo instanceof VisualGraph) {
					if (!ignoreGroups) {
						hitList.add(vgo);
					}
					recursiveSearchForHitObject((VisualGraph) vgo, pt, ignoreGroups, ignoreNodes, ignoreEdges, hitList);
				} else if (vgo instanceof VisualNode && !ignoreNodes) {
					hitList.add(vgo);
				}
			}
		}

		return hitList;
	}

	/*
	 * private static GraphObjectView getFirstHitObjectInGroup( graphView visualGraph, Point pt, boolean ignoreGroups,
	 * boolean ignoreNodes, boolean ignoreEdges) { GraphObjectView hitObjects[] = visualGraph.getHitObjects(pt); for
	 * (int i = hitObjects.length - 1; i >= 0 ; i--) { GraphObjectView vgo = hitObjects[i]; if(vgo != null) {
	 * if(!ignoreGroups && !ignoreNodes && !ignoreEdges ) return vgo; if(vgo instanceof EdgeView && !ignoreEdges) return
	 * vgo; else if(vgo instanceof graphView && !ignoreGroups) return vgo; else if(vgo instanceof NodeView &&
	 * !ignoreNodes) return vgo; } } return ignoreGroups || visualGraph.getLevel() == 0 ? null : visualGraph; } private
	 * static GraphObjectView getLastHitObjectInGroup( graphView visualGraph, Point pt, boolean ignoreGroups, boolean
	 * ignoreNodes, boolean ignoreEdges) { GraphObjectView hitObjects[] = visualGraph.getHitObjects(pt); for (int i = 0;
	 * i < hitObjects.length ; i++) { GraphObjectView vgo = hitObjects[i]; if(vgo != null) { if(!ignoreGroups &&
	 * !ignoreNodes && !ignoreEdges ) return vgo; if(vgo instanceof EdgeView && !ignoreEdges) return vgo; else if(vgo
	 * instanceof graphView && !ignoreGroups) return vgo; else if(vgo instanceof NodeView && !ignoreNodes) return vgo; }
	 * } return ignoreGroups|| visualGraph.getLevel() == 0 ? null : visualGraph; }
	 */

	/**
	 * checks if graphView2 is nested in the graphView1
	 * 
	 * @param graphView2
	 * @param graphView1
	 * @return
	 */
	private static boolean nested(VisualGraph graphView1, VisualGraph graphView2) {

		int l2 = graphView2.getDepth();
		VisualGraph gv = graphView2.getParentGraph();
		while (gv.getDepth() > l2) {
			gv = graphView2.getParentGraph();
		}
		return gv == graphView1;
	}

	/**
	 * This method ensures that the edges specified by "edge" is placed in the right group. The right position for an
	 * edges is the deepest group that contains both source- and target-node.
	 * 
	 * @param edge
	 * @param sourcenode
	 * @param targetnode
	 */
	public static final VisualGraph determinOptimumGraphViewForEdge(VisualGraph rootGraphView, VisualEdge edge) {

		VisualGraph graphViewSource = null;
		VisualGraph graphViewTarget = null;

		VisualNode source = edge.getSourceNode();
		VisualNode target = edge.getTargetNode();

		VisualGraph targetGraph = null;
		if (source != null) {
			graphViewSource = source.getParentGraph();
		}
		if (target != null) {
			graphViewTarget = target.getParentGraph();
		}

		if (graphViewSource == null && graphViewTarget != null) {
			targetGraph = graphViewTarget.getParentGraph();
		} else if (graphViewSource != null && graphViewTarget == null) {
			targetGraph = graphViewSource.getParentGraph();
		} else if (graphViewSource != null && graphViewTarget != null) {
			if (graphViewSource.getDepth() < graphViewTarget.getDepth()) {
				if (nested(graphViewSource, graphViewTarget)) {
					return graphViewSource;
				} else {
					graphViewSource.getParentGraph();
				}
			} else if (graphViewSource.getDepth() > graphViewTarget.getDepth()) {

				if (nested(graphViewTarget, graphViewSource)) {
					return graphViewTarget;
				} else {
					graphViewTarget.getParentGraph();
				}
			} else {
				if (graphViewSource == graphViewTarget) {
					return graphViewSource;
				} else {
					return findSharedParent(graphViewSource, graphViewTarget);
				}
			}
		}

		return targetGraph == null ? rootGraphView : targetGraph;
	}

	private static VisualGraph findSharedParent(VisualGraph graphViewSource, VisualGraph graphViewTarget) {

		VisualGraph parentSource = graphViewSource;
		VisualGraph parenttarget = graphViewTarget;
		while (parentSource != parenttarget) {
			parentSource = parentSource.getParentGraph();
			parenttarget = parenttarget.getParentGraph();
		}

		return parentSource;
	}

	public static void moveEdgeToAppropriateGraphView(VisualEdge edge) {

		VisualGraph currentView = edge.getParentGraph();
		VisualGraph parent = currentView;
		if (parent == null) {
			return;
		}
		VisualGraph topLevelView = parent;
		while (topLevelView.getDepth() != 0) {
			topLevelView = topLevelView.getParentGraph();
		}

		VisualGraph targetView = GraphInteractionUtil.determinOptimumGraphViewForEdge(topLevelView, edge);

		// System.err.println("target level for edge: " + targetView.getLevel());
		if (targetView != currentView) {
			GraphInteractionUtil.relocateObject(edge, targetView);
		}
	}

	public static final void relocateObject(List<VisualGraphObject> objects, VisualGraph targetView) {

		for (VisualGraphObject vgo : objects) {
			relocateObject(vgo, targetView);
		}
	}

	public static final void relocateObject(VisualGraphObject vgo, VisualGraph targetView) {

		VisualGraph currentView = vgo.getParentGraph();
		if (currentView != targetView) {

			if (vgo instanceof VisualEdge) {
				relocateEdge((VisualEdge) vgo, currentView, targetView);
			} else if (vgo instanceof VisualGraph) {
				relocateGroup((VisualGraph) vgo, currentView, targetView);
			} else {
				relocateNode((VisualNode) vgo, currentView, targetView);
			}
		}
	}

	private static void relocateNode(VisualNode node, VisualGraph sourceView, VisualGraph targetView) {

		Map<VisualEdge, Integer> sourcePortMap = new HashMap<VisualEdge, Integer>();
		Map<VisualEdge, Integer> targetPortMap = new HashMap<VisualEdge, Integer>();

		List<VisualEdge> incomingEdges = new ArrayList<VisualEdge>(node.getIncomingEdges());
		for (VisualEdge edgeView : incomingEdges) {
			targetPortMap.put(edgeView, new Integer(edgeView.getTargetPortId()));
		}
		List<VisualEdge> outgoingEdges = new ArrayList<VisualEdge>(node.getOutgoingEdges());
		for (VisualEdge edgeView : outgoingEdges) {
			sourcePortMap.put(edgeView, new Integer(edgeView.getSourcePortId()));
		}
		sourceView.remove(node);
		targetView.add(node);

		for (VisualEdge edge : outgoingEdges) {
			Integer portID = sourcePortMap.get(edge);
			edge.connect(node, portID.intValue(), edge.getTargetNode(), edge.getTargetPortId());
			moveEdgeToAppropriateGraphView(edge);
		}
		for (VisualEdge edge : incomingEdges) {
			Integer portID = targetPortMap.get(edge);
			edge.connect(edge.getSourceNode(), edge.getSourcePortId(), node, portID.intValue());
			moveEdgeToAppropriateGraphView(edge);
		}
	}

	private static void relocateEdge(VisualEdge edge, VisualGraph sourceView, VisualGraph targetView) {

		VisualNode sn = edge.getSourceNode();
		int sp = edge.getSourcePortId();
		VisualNode tn = edge.getTargetNode();
		int tp = edge.getTargetPortId();

		sourceView.remove(edge);
		targetView.add(edge);

		edge.connect(sn, sp, tn, tp);
		System.err.println("Edge moved to graphView: " + edge.getParentGraph().getID() + " in level: "
				+ edge.getParentGraph().getDepth());

	}

	private static void relocateGroup(VisualGraph group, VisualGraph sourceView, VisualGraph targetView) {

		sourceView.remove(group);
		targetView.add(group);
	}


}
