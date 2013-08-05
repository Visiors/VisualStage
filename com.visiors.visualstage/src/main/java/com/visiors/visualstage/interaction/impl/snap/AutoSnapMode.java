package com.visiors.visualstage.interaction.impl.snap;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.constants.InteractionConstants;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.interaction.impl.BaseInteractionHandler;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.GraphInteractionUtil;

public class AutoSnapMode extends BaseInteractionHandler {

	private static final int EDGE_STRAIGHT_RANGE = 15;

	private boolean snapToGrid = false;
	private boolean moveToStraightEdge = true;
	private boolean showPositionLines = false;
	private Point cursor;

	private boolean ignoreEvents;
	private VisualNode hitObject;
	@Inject
	SystemUnit systemUnit;

	public AutoSnapMode() {

		super();

		setMoveToStraightEdge(true);
	}

	@Override
	public String getName() {

		return InteractionConstants.MODE_AUTO_ALIGNMENT;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (!active) {
			return false;
		}

		hitObject = GraphInteractionUtil.getFirstHitNodeAt(graphDocument.getGraph(), pt);

		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (!active) {
			return false;
		}

		if (hitObject != null) {
			if (moveToStraightEdge) {
				adjustNodePos(hitObject);
			}

		}
		hitObject = null;
		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (!active) {
			return false;
		}

		if (isShowPositionLines()) {
			final Transform transform = graphDocument.getGraph().getTransform();
			cursor = transform.transformToScreen(pt);
			graphDocument.getGraph().updateView();
		}

		if (isSnapToGrid()) {
			snapToGrid(pt);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		if (!active) {
			return false;
		}

		if (isShowPositionLines()) {
			final Transform transform = graphDocument.getGraph().getTransformer();
			cursor = transform.transformToScreen(pt);
			graphDocument.getGraph().updateView();
		}
		return false;
	}

	private void snapToGrid(Point pt) {

		if (hitObject != null) {
			if (hitObject.isSelected() && graphDocument.getGraph().getSelection().size() == 1) {
				snapToGrid(hitObject);
			}
		}

	}

	@Override
	public void paintOnBackground(Canvas canvas, DrawingContext r) {

	}

	@Override
	public void paintOnTop(Canvas canvas, DrawingContext r) {

		if (showPositionLines && cursor != null) {
			paintPositionLines(canvas, r);
		}
	}

	private void paintPositionLines(Canvas canvas, Rectangle r) {

		canvas.setColor(Color.red);
		canvas.drawLine(r.x, cursor.y, r.x + r.width, cursor.y);
		canvas.drawLine(cursor.x, r.y, cursor.x, r.y + r.height);
	}

	void snapToGrid(VisualNode node) {

		if (ignoreEvents || !isSnapToGrid()) {
			return;
		}
		ignoreEvents = true;
		int unit = (int) systemUnit.getPixelsPerUnit();
		final int snapRegion = 10;
		final Rectangle b = node.getBounds();
		int x = (b.x / unit) * unit;

		if (b.x - x < snapRegion) {
			b.x = x;
		} else if (x + unit - b.x < snapRegion) {
			b.x = x + unit;
		} else {
			x = ((b.x + b.width) / unit) * unit;
			if (b.x + b.width - x < snapRegion) {
				b.x = x - b.width;
			} else if (x + unit - b.x - b.width < snapRegion) {
				b.x = x + unit - b.width;
			}
		}

		int y = (b.y / unit) * unit;
		if (b.y - y < snapRegion) {
			b.y = y;
		} else if (y + unit - b.y < snapRegion) {
			b.y = y + unit;
		} else {
			y = ((b.y + b.height) / unit) * unit;
			if (b.y + b.height - y < snapRegion) {
				b.y = y - b.height;
			} else if (y + unit - b.y - b.height < snapRegion) {
				b.y = y + unit - b.height;
			}
		}
		node.setBounds(b);
		ignoreEvents = false;
	}

	public boolean isShowPositionLines() {

		return showPositionLines;
	}

	public void setShowPositionLines(boolean showPositionLines) {

		this.showPositionLines = showPositionLines;
	}

	public boolean isSnapToGrid() {

		return snapToGrid;
	}

	public void snapToGrid(boolean snapToGrid) {

		this.snapToGrid = snapToGrid;
	}

	public boolean isMoveToStraightEdge() {

		return moveToStraightEdge;
	}

	public void setMoveToStraightEdge(boolean moveToStraightEdge) {

		this.moveToStraightEdge = moveToStraightEdge;
	}

	private void adjustNodePos(VisualNode node) {

		List<VisualEdge> edges = node.getOutgoingEdges();
		for (VisualEdge edge : edges) {
			adjustSourceNode(edge);
		}
		edges = node.getIncomingEdges();
		for (VisualEdge edge : edges) {
			adjustTargetNode(edge);
		}
	}

	private void adjustSourceNode(VisualEdge edge) {

		final Point[] points = edge.getPoints();
		VisualNode sourceNode = edge.getSourceNode();
		VisualNode targetNode = edge.getTargetNode();
		if (sourceNode == null || targetNode == null) {
			return;
		}

		int sourcePortId = edge.getSourcePortId();
		int targetPortId = edge.getTargetPortId();
		final Point ptStart = sourcePortId == -1 ? sourceNode.getPortPosition(sourcePortId)
				: points[0];
		final Point ptEnd = sourcePortId == -1 ? targetNode.getPortPosition(targetPortId)
				: points[points.length - 1];

		int dx = ptStart.x - ptEnd.x;
		int dy = ptStart.y - ptEnd.y;
		if (Math.abs(dx) > AutoSnapMode.EDGE_STRAIGHT_RANGE) {
			dx = 0;
		}
		if (Math.abs(dy) > AutoSnapMode.EDGE_STRAIGHT_RANGE) {
			dy = 0;
		}
		if (dx != 0 || dy != 0) {
			sourceNode.move(-dx, -dy);
		}
	}

	private void adjustTargetNode(VisualEdge edge) {

		final Point[] points = edge.getPoints();
		VisualNode sourceNode = edge.getSourceNode();
		VisualNode targetNode = edge.getTargetNode();
		if (sourceNode == null || targetNode == null) {
			return;
		}

		int sourcePortId = edge.getSourcePortId();
		int targetPortId = edge.getTargetPortId();
		final Point ptStart = sourcePortId == -1 ? sourceNode.getPortPosition(sourcePortId)
				: points[0];
		final Point ptEnd = sourcePortId == -1 ? targetNode.getPortPosition(targetPortId)
				: points[points.length - 1];

		int dx = ptStart.x - ptEnd.x;
		int dy = ptStart.y - ptEnd.y;
		if (Math.abs(dx) > AutoSnapMode.EDGE_STRAIGHT_RANGE) {
			dx = 0;
		}
		if (Math.abs(dy) > AutoSnapMode.EDGE_STRAIGHT_RANGE) {
			dy = 0;
		}
		if (dx != 0 || dy != 0) {
			targetNode.move(dx, dy);
		}
	}

}
