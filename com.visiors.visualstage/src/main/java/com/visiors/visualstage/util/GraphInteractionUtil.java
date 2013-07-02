package com.visiors.visualstage.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visiors.visualstage.stage.edge.EdgeView;
import com.visiors.visualstage.stage.graph.GraphView;
import com.visiors.visualstage.stage.node.NodeView;
import com.visiors.visualstage.stage.node.Port;
import com.visiors.visualstage.stage.node.PortSet;
import com.visiors.visualstage.view.xxx.GraphObjectView;

public class GraphInteractionUtil {

    public static NodeView getFirstHitNodeAt(GraphView rootGraphView, Point pt) {

        List<GraphObjectView> hitObjects = getHitObjects(rootGraphView, pt, false, false, true);
        if (hitObjects.size() > 0) {
            return (NodeView) hitObjects.get(0);
        }
        return null;
    }

    public static EdgeView getFirstHitEdgeAt(GraphView rootGraphView, Point pt) {

        List<GraphObjectView> hitObjects = getHitObjects(rootGraphView, pt, true, true, false);
        if (hitObjects.size() > 0) {
            return (EdgeView) hitObjects.get(0);
        }
        return null;
    }

    public static GraphView getFirstHitGroupAt(GraphView rootGraphView, Point pt) {

        List<GraphObjectView> hitObjects = getHitObjects(rootGraphView, pt, false, true, true);
        if (hitObjects.size() > 0) {
            return (GraphView) hitObjects.get(0);
        }
        return null;
    }

    public static GraphView getLastGroupHitByObjects(GraphView rootGraphView, List<GraphObjectView> objects) {

        for (GraphObjectView vgo : objects) {
            GraphView gv = getLastGroupHitByObjects(rootGraphView, vgo);
            if (gv != null) {
                return gv;
            }
        }
        return null;
    }

    public static GraphView getLastGroupHitByObjects(GraphView rootGraphView, GraphObjectView vgo) {

        GraphView deepestGroup = null;
        Rectangle r = vgo.getBounds();
        if (r == null) {
            return null;
        }
        List<GraphObjectView> hitObjects;
        List<GraphObjectView> allHitObjects = new ArrayList<GraphObjectView>();

        hitObjects = getHitObjects(rootGraphView, new Point(r.x, r.y), false, true, true);
        for (GraphObjectView gv : hitObjects) {
            if (gv != vgo && !allHitObjects.contains(gv)) {
                allHitObjects.add(gv);
            }
        }
        hitObjects = getHitObjects(rootGraphView, new Point(r.x + r.width, r.y), false, true, true);
        for (GraphObjectView gv : hitObjects) {
            if (gv != vgo && !allHitObjects.contains(gv)) {
                allHitObjects.add(gv);
            }
        }
        hitObjects = getHitObjects(rootGraphView, new Point(r.x, r.y + r.height), false, true, true);
        for (GraphObjectView gv : hitObjects) {
            if (gv != vgo && !allHitObjects.contains(gv)) {
                allHitObjects.add(gv);
            }
        }
        hitObjects = getHitObjects(rootGraphView, new Point(r.x + r.width, r.y + r.height), false, true, true);
        for (GraphObjectView gv : hitObjects) {
            if (gv != vgo && !allHitObjects.contains(gv)) {
                allHitObjects.add(gv);
            }
        }

        if (allHitObjects.size() != 0) {
            for (GraphObjectView gv : allHitObjects) {
                if (deepestGroup == null || deepestGroup.getDepth() < ((GraphView) gv).getDepth()) {
                    deepestGroup = ((GraphView) gv);
                }
            }
        }

        return deepestGroup;
    }

    private static Rectangle calcSelectionBoundary(List<GraphObjectView> selection) {

        Rectangle r = null;
        for (GraphObjectView vgo : selection) {
            if (r == null) {
                r = vgo.getBounds();
            } else {
                r = r.union(vgo.getBounds());
            }
        }
        return r;
    }

    public static GraphObjectView getFirstHitObjectAt(GraphView rootGraphView, Point pt) {

        List<GraphObjectView> hitObjects = getHitObjects(rootGraphView, pt, false, false, false);
        if (hitObjects.size() > 0) {
            return hitObjects.get(0);
        }
        return null;
    }

    public static GraphObjectView getClosestObject(GraphView graphView, GraphObjectView refObject,
            boolean ignoreGroups, boolean ignoreNodes, boolean ignoreEdges, boolean ignoreOverlaps) {

        double minDist = Double.MAX_VALUE;
        GraphObjectView bestmatch = null;

        if (!ignoreNodes || !ignoreGroups) {
            Rectangle b, bref;
            int dx, dy;
            double r;

            bref = refObject.getBounds();
            Point cpt = new Point(bref.x + bref.width / 2, bref.y + bref.height / 2);

            NodeView[] nodes = graphView.getNodes();
            for (int i = 0; i < nodes.length; i++) {

                if (nodes[i].getID() == refObject.getID()) {
                    continue;
                }

                if ((nodes[i] instanceof GraphView && !ignoreGroups) || !ignoreNodes) {
                    b = nodes[i].getBounds();
                    if (ignoreOverlaps && bref.intersects(b)) {
                        continue;
                    }

                    dx = Math.abs(b.x + b.width / 2 - cpt.x);
                    dy = Math.abs(b.y + b.height / 2 - cpt.y);
                    r = Math.sqrt(dx * dx + dy * dy);
                    if (r < minDist) {
                        minDist = r;
                        ;
                        bestmatch = nodes[i];
                    }
                }
            }
        }
        if (!ignoreEdges) {
            // TODO
            // EdgeView[] edges = graphView.getEdges();
            // for (int i = 0; i < edges.length; i++) {
            //
            // }
        }
        return bestmatch;
    }

    public static GraphObjectView getClosestNode(GraphView graphView, GraphObjectView refObject, int maxDistance,
            boolean ignoreOverlaps, Port[] posrts) {

        double minDist = Double.MAX_VALUE;
        GraphObjectView bestmatch = null;
        Rectangle b;
        double r;
        Rectangle bref = refObject.getBounds();

        NodeView[] nodes = graphView.getNodes();
        for (int i = 0; i < nodes.length; i++) {

            if (nodes[i].getID() == refObject.getID()) {
                continue;
            }

            if (nodes[i] instanceof GraphView) {
                GraphObjectView no = getClosestNode((GraphView) nodes[i], refObject, maxDistance, ignoreOverlaps,
                        posrts);
                if (no != null) {
                    return no;
                }
            }

            b = nodes[i].getBounds();
            if (!ignoreOverlaps) {
                if (bref.intersects(b)) {
                    continue;
                }
            }

            double alpha = getAngle(bref, b);
            Port pRef = getAvailableport((NodeView) refObject, alpha);
            if (pRef == null) {
                continue;
            }
            // find the port on the opposite side
            int beta = portMidAngle(pRef);
            Port pNode = getAvailableport(nodes[i], beta + 180);

            if (pNode == null) {
                continue;
            }

            Point pt1 = pRef.getPosition();
            Point pt2 = pNode.getPosition();
            r = distance(pt1.x, pt1.y, pt2.x, pt2.y);
            if (r < maxDistance && r < minDist) {
                minDist = r;
                bestmatch = nodes[i];
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

    private static Port getAvailableport(NodeView node, double alph) {

        final PortSet ps = node.getPortSet();
        final int portID = ps.getPortByAngle(alph);

        return ps.getPortByID(portID);
    }

    public static List<GraphObjectView> getHitObjects(GraphView graphView, Point pt, boolean ignoreGroups,
            boolean ignoreNodes, boolean ignoreEdges) {

        List<GraphObjectView> result = new ArrayList<GraphObjectView>();

        recursiveSearchForHitObject(graphView, pt, ignoreGroups, ignoreNodes, ignoreEdges, result);

        return result;
    }

    private static List<GraphObjectView> recursiveSearchForHitObject(GraphView graphView, Point pt,
            boolean ignoreGroups, boolean ignoreNodes, boolean ignoreEdges, List<GraphObjectView> hitList) {

        GraphObjectView hitObjects[] = graphView.getHitObjects(pt);

        for (int i = hitObjects.length - 1; i >= 0; i--) {
            GraphObjectView vgo = hitObjects[i];
            if (vgo != null) {

                if (vgo instanceof EdgeView && !ignoreEdges) {
                    hitList.add(vgo);
                } else if (vgo instanceof GraphView) {
                    if (!ignoreGroups) {
                        hitList.add(vgo);
                    }
                    recursiveSearchForHitObject((GraphView) vgo, pt, ignoreGroups, ignoreNodes, ignoreEdges, hitList);
                } else if (vgo instanceof NodeView && !ignoreNodes) {
                    hitList.add(vgo);
                }
            }
        }

        return hitList;
    }

    /*
     * private static GraphObjectView getFirstHitObjectInGroup( GraphView graphView, Point pt, boolean ignoreGroups,
     * boolean ignoreNodes, boolean ignoreEdges) { GraphObjectView hitObjects[] = graphView.getHitObjects(pt); for
     * (int i = hitObjects.length - 1; i >= 0 ; i--) { GraphObjectView vgo = hitObjects[i]; if(vgo != null) {
     * if(!ignoreGroups && !ignoreNodes && !ignoreEdges ) return vgo; if(vgo instanceof EdgeView && !ignoreEdges) return
     * vgo; else if(vgo instanceof GraphView && !ignoreGroups) return vgo; else if(vgo instanceof NodeView &&
     * !ignoreNodes) return vgo; } } return ignoreGroups || graphView.getLevel() == 0 ? null : graphView; } private
     * static GraphObjectView getLastHitObjectInGroup( GraphView graphView, Point pt, boolean ignoreGroups, boolean
     * ignoreNodes, boolean ignoreEdges) { GraphObjectView hitObjects[] = graphView.getHitObjects(pt); for (int i = 0;
     * i < hitObjects.length ; i++) { GraphObjectView vgo = hitObjects[i]; if(vgo != null) { if(!ignoreGroups &&
     * !ignoreNodes && !ignoreEdges ) return vgo; if(vgo instanceof EdgeView && !ignoreEdges) return vgo; else if(vgo
     * instanceof GraphView && !ignoreGroups) return vgo; else if(vgo instanceof NodeView && !ignoreNodes) return vgo; }
     * } return ignoreGroups|| graphView.getLevel() == 0 ? null : graphView; }
     */

    /**
     * checks if graphView2 is nested in the graphView1
     * 
     * @param graphView2
     * @param graphView1
     * @return
     */
    private static boolean nested(GraphView graphView1, GraphView graphView2) {

        int l2 = graphView2.getDepth();
        GraphView gv = graphView2.getParent();
        while (gv.getDepth() > l2) {
            gv = graphView2.getParent();
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
    public static final GraphView determinOptimumGraphViewForEdge(GraphView rootGraphView, EdgeView edge) {

        GraphView graphViewSource = null;
        GraphView graphViewTarget = null;

        NodeView source = edge.getSourceNode();
        NodeView target = edge.getTargetNode();

        GraphView targetGraph = null;
        if (source != null) {
            graphViewSource = source.getParent();
        }
        if (target != null) {
            graphViewTarget = target.getParent();
        }

        if (graphViewSource == null && graphViewTarget != null) {
            targetGraph = graphViewTarget.getParent();
        } else if (graphViewSource != null && graphViewTarget == null) {
            targetGraph = graphViewSource.getParent();
        } else if (graphViewSource != null && graphViewTarget != null) {
            if (graphViewSource.getDepth() < graphViewTarget.getDepth()) {
                if (nested(graphViewSource, graphViewTarget)) {
                    return graphViewSource;
                } else {
                    graphViewSource.getParent();
                }
            } else if (graphViewSource.getDepth() > graphViewTarget.getDepth()) {

                if (nested(graphViewTarget, graphViewSource)) {
                    return graphViewTarget;
                } else {
                    graphViewTarget.getParent();
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

    private static GraphView findSharedParent(GraphView graphViewSource, GraphView graphViewTarget) {

        GraphView parentSource = graphViewSource;
        GraphView parenttarget = graphViewTarget;
        while (parentSource != parenttarget) {
            parentSource = parentSource.getParent();
            parenttarget = parenttarget.getParent();
        }

        return parentSource;
    }

    public static void moveEdgeToAppropriateGraphView(EdgeView edge) {

        GraphView currentView = edge.getParent();
        GraphView parent = currentView;
        if (parent == null) {
            return;
        }
        GraphView topLevelView = parent;
        while (topLevelView.getDepth() != 0) {
            topLevelView = topLevelView.getParent();
        }

        GraphView targetView = GraphInteractionUtil.determinOptimumGraphViewForEdge(topLevelView, edge);

        // System.err.println("target level for edge: " + targetView.getLevel());
        if (targetView != currentView) {
            GraphInteractionUtil.relocateObject(edge, targetView);
        }
    }

    public static final void relocateObject(List<GraphObjectView> objects, GraphView targetView) {

        for (GraphObjectView vgo : objects) {
            relocateObject(vgo, targetView);
        }
    }

    public static final void relocateObject(GraphObjectView vgo, GraphView targetView) {

        GraphView currentView = vgo.getParent();
        if (currentView != targetView) {

            if (vgo instanceof EdgeView) {
                relocateEdge((EdgeView) vgo, currentView, targetView);
            } else if (vgo instanceof GraphView) {
                relocateGroup((GraphView) vgo, currentView, targetView);
            } else {
                relocateNode((NodeView) vgo, currentView, targetView);
            }
        }
    }

    private static void relocateNode(NodeView node, GraphView sourceView, GraphView targetView) {

        Map<EdgeView, Integer> sourcePortMap = new HashMap<EdgeView, Integer>();
        Map<EdgeView, Integer> targetPortMap = new HashMap<EdgeView, Integer>();

        List<EdgeView> incomingEdges = new ArrayList<EdgeView>(node.getIncomingEdges());
        for (EdgeView edgeView : incomingEdges) {
            targetPortMap.put(edgeView, new Integer(edgeView.getTargetPortId()));
        }
        List<EdgeView> outgoingEdges = new ArrayList<EdgeView>(node.getOutgoingEdges());
        for (EdgeView edgeView : outgoingEdges) {
            sourcePortMap.put(edgeView, new Integer(edgeView.getSourcePortId()));
        }
        sourceView.deleteGraphObject(node);
        targetView.addGraphObject(node);

        for (EdgeView edge : outgoingEdges) {
            Integer portID = sourcePortMap.get(edge);
            edge.setSourceNode(node, portID.intValue());
            moveEdgeToAppropriateGraphView(edge);
        }
        for (EdgeView edge : incomingEdges) {
            Integer portID = targetPortMap.get(edge);
            edge.setTargetNode(node, portID.intValue());
            moveEdgeToAppropriateGraphView(edge);
        }
    }

    private static void relocateEdge(EdgeView edge, GraphView sourceView, GraphView targetView) {

        NodeView sn = edge.getSourceNode();
        int sp = edge.getSourcePortId();
        NodeView tn = edge.getTargetNode();
        int tp = edge.getTargetPortId();

        sourceView.deleteGraphObject(edge);
        targetView.addGraphObject(edge);

        edge.setSourceNode(sn);
        edge.setSourcePortId(sp);
        edge.setTargetNode(tn);
        edge.setTargetPortId(tp);
        System.err.println("Edge moved to GraphView: " + edge.getParent().getID() + " in level: "
                + edge.getParent().getDepth());

    }

    private static void relocateGroup(GraphView group, GraphView sourceView, GraphView targetView) {

        sourceView.deleteGraphObject(group);
        targetView.addGraphObject(group);
    }

    /*
     * example final List<GraphObjectView> allObjects = new ArrayList<GraphObjectView>(); GraphVisitor visitor = new
     * GraphVisitor () { public void visit(GraphView subgraph, int level) {
     * allObjects.add(subgraph.getGraphObjects(false)); } }; GraphUtil.visitSubgraphs(graphView, visitor, true, 0);
     */

    public static final void visitSubgraphs(GraphView graphView, GraphVisitor visitor, boolean preOrder,
            int currentLevel) {

        GraphObjectView[] nodes = graphView.getNodes();
        for (int i = 0; i < nodes.length; i++) {

            if (nodes[i] instanceof GraphView) {
                GraphView subgraph = (GraphView) nodes[i];
                if (preOrder) {
                    visitor.visit(subgraph, currentLevel);
                    visitSubgraphs(subgraph, visitor, preOrder, currentLevel + 1);
                } else {
                    visitSubgraphs(subgraph, visitor, preOrder, currentLevel + 1);
                    visitor.visit(subgraph, currentLevel);
                }
            }
        }
    }

}
