package com.visiors.visualstage.interaction.impl.edgecreation;

import java.awt.Point;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.visiors.visualstage.constants.InteractionConstants;
import com.visiors.visualstage.editor.GraphEditor;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.orthogonal.OrthogonalEdgeRouter;
import com.visiors.visualstage.graph.view.node.Port;
import com.visiors.visualstage.graph.view.node.PortSet;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.interaction.impl.BaseTool;
import com.visiors.visualstage.util.GraphInteractionUtil;

public class EdgeCreationMode extends BaseTool {

	private static final int range = 40;
	private static final int alignmentRange = 30;
	private static final int minDistance = 60;
	private static final int DELAY = 400;

	private VisualNode draggingNode;
	private VisualNode nodeToMate;
	private VisualEdge createdEdge;
	private Timer timer;
	boolean forward;
	private Point lastDraggedPoint;
	private int keyPressedInProcess;

	public EdgeCreationMode() {

		super();
	}

	@Override
	public String getName() {

		return InteractionConstants.MODE_EDGE_CREATION;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		if (keyPressedInProcess == keyCode) {
			return false;
		}
		keyPressedInProcess = keyCode;
		reverse(isControlKeyPressed(keyCode));
		cancel(isShiftKeyPressed(keyCode));
		return false;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		keyPressedInProcess = 0;
		forward = true;
		reverse(false);
		return false;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (draggingNode == null) {
			draggingNode = GraphInteractionUtil.getFirstHitNodeAt(visualGraph, pt);
		}

		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (createdEdge != null) {
			alignNode();
		}
		cancelTimer();
		highlightTargetPorts(null, false, false);
		draggingNode = null;
		nodeToMate = null;
		createdEdge = null;

		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (!pt.equals(lastDraggedPoint)) {
			lastDraggedPoint = pt;

			if (createdEdge != null) {
				createdEdge.getParentGraph().remove(createdEdge);
				createdEdge = null;
			}

			if (isShiftKeyPressed(functionKey)) {
				return false;
			}

			// Determination of the hit node must be redone here because the
			// node might be
			// duplicated in a "copy on move" action
			if (draggingNode != null) {
				draggingNode = GraphInteractionUtil.getFirstHitNodeAt(visualGraph, pt);
			}

			if (draggingNode != null) {
				highlightTargetPorts(null, false, false);
				forward = !isControlKeyPressed(functionKey);
				scheduleTimer();
				// return true;
			}
		}
		return false;
	}

	private void reverse(boolean controlKeyPressed) {

		forward = !controlKeyPressed;
		if (createdEdge != null) {
			// reverse edge
			VisualNode s = createdEdge.getSourceNode();
			int sp = createdEdge.getSourcePortId();
			VisualNode t = createdEdge.getTargetNode();
			int tp = createdEdge.getTargetPortId();
			createdEdge.connect(null, -1, null, -1);
			createdEdge.connect(s, sp, t, tp);
			graphDocument.update();
		}
	}

	private void cancel(boolean cancel) {

		if (cancel && createdEdge != null) {
			createdEdge.getParentGraph().remove(createdEdge);
			createdEdge = null;
			highlightTargetPorts(null, false, false);
		}
	}

	private void highlightTargetPorts(Port[] ports, boolean openPort, boolean highlight) {

		if (draggingNode != null) {
			draggingNode.illuminatePorts(ports != null && openPort);
			if (ports != null && highlight) {
				draggingNode.highlightPort(ports[0].getID(), true);
			}
		}

		if (nodeToMate != null) {
			nodeToMate.illuminatePorts(ports != null && openPort);
			if (ports != null && highlight) {
				nodeToMate.highlightPort(ports[1].getID(), true);
			}
		}
	}

	private void scheduleTimer() {

		cancelTimer();
		timer = new Timer();

		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				if (draggingNode != null) {

					Port[] posrts = new Port[2];
					nodeToMate = (VisualNode) GraphInteractionUtil.getClosestNode(visualGraph,
							draggingNode, EdgeCreationMode.range, true, posrts);

					final boolean connect = nodeToMate != null && draggingNode != null
							&& !nodeConnected(nodeToMate, draggingNode);
					if (connect) {
						highlightTargetPorts(posrts, true, true);
						createEdge(forward);
						graphDocument.update();
					}
				}
			}

		}, EdgeCreationMode.DELAY);
	}

	private void cancelTimer() {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private boolean nodeConnected(VisualNode node1, VisualNode node2) {

		List<VisualEdge> edges = node1.getIncomingEdges();
		for (VisualEdge edge : edges) {
			if (edge.getSourceNode() != null) {
				if (node2.getID() == edge.getSourceNode().getID()) {
					return true;
				}
			}
		}
		edges = node1.getOutgoingEdges();
		for (VisualEdge edge : edges) {
			if (edge.getTargetNode() != null) {
				if (node2.getID() == edge.getTargetNode().getID()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void cancelInteraction() {

		cancelTimer();
		if (createdEdge != null) {
			createdEdge.getParentGraph().remove(createdEdge);
			graphDocument.update();
		}
		highlightTargetPorts(null, false, false);
		draggingNode = null;
		nodeToMate = null;
		createdEdge = null;
	}

	private void createEdge(boolean forward) {

		Port refPort = getHighlightedPort(nodeToMate);
		Port alignPort = getHighlightedPort(draggingNode);

		if (refPort != null && alignPort != null) {
			int p1 = refPort.getID();
			int p2 = alignPort.getID();
			GraphEditor f = GraphEditor.instance();
			createdEdge = f.createDefaultEdge();
			if (forward) {

				createdEdge.connect(nodeToMate, p1,draggingNode, p2);

			} else {
				createdEdge.connect(draggingNode, p2,nodeToMate, p1);
			}
			nodeToMate.getParentGraph().add(createdEdge);
			GraphInteractionUtil.moveEdgeToAppropriateGraphView(createdEdge);
		}
		keyPressedInProcess = 0;
		forward = true;
	}

	private void adaptEdge(VisualEdge edge, Point start, Point end) {

		Point[] points = edge.getPoints();
		// createdEdge.move(start.x - points[0].x, start.y - points[0].y);
		points = edge.getPoints();
		//
		// double w2 = end.x - start.x;
		// double h2 = end.y - start.y;
		// double w1 = points[points.length-1].x - points[0].x;
		// double h1 = points[points.length-1].y - points[0].y;
		// double dx;
		// double dy;
		//
		// for (int i = 0; i < points.length; i++) {
		// dx = (points[i].x - points[0].x);
		// dy = (points[i].y - points[0].y);
		// points[i].x -= (int) (dx / Math.round(w1/w2)) ;
		// points[i].y -= (int) (dy / Math.round(h1/h2)) ;
		// }

		edge.setPoints(OrthogonalEdgeRouter.routeEdge(edge, points, 4));

	}

	private Port getHighlightedPort(VisualNode node) {

		if (node != null && node.arePortsilluminated()) {
			PortSet ports = node.getPortSet();
			for (Port port : ports.getPorts()) {
				if (port.isHighlighted()) {
					return port;
				}
			}
		}
		return null;
	}

	private void alignNode() {

		Port refPort = getHighlightedPort(draggingNode);
		Port alignPort = getHighlightedPort(nodeToMate);
		if (refPort == null || alignPort == null) {
			return;
		}

		Point p1 = refPort.getPosition();
		Point p2 = alignPort.getPosition();

		int dx = 0;
		int dy = 0;

		if (alignPort.acceptsDirection(90) || alignPort.acceptsDirection(270)) {
			dx = p2.x - p1.x;
			if (Math.abs(dx) < EdgeCreationMode.alignmentRange) {
				dy = p1.y > p2.y ? (p2.y - p1.y) + EdgeCreationMode.minDistance : (p2.y - p1.y)
						- EdgeCreationMode.minDistance;
				draggingNode.move(dx, dy);
			}
		} else if (alignPort.acceptsDirection(0) || alignPort.acceptsDirection(180)) {
			dy = p2.y - p1.y;
			if (Math.abs(dy) < EdgeCreationMode.alignmentRange) {
				dx = p1.x > p2.x ? (p2.x - p1.x) + EdgeCreationMode.minDistance : (p2.x - p1.x)
						- EdgeCreationMode.minDistance;
				draggingNode.move(dx, dy);
			}
		}
	}
}
