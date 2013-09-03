package com.visiors.visualstage.interaction.impl.snap;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.node.Port;
import com.visiors.visualstage.graph.view.node.PortSet;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.interaction.impl.BaseTool;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.GraphInteractionUtil;

public class AutoSnapTool extends BaseTool {

	private static final int EDGE_STRAIGHT_RANGE = 15;

	private boolean snapToGrid = false;
	private boolean moveToStraightEdge = true;
	private boolean showPositionLines = false;
	private Point cursor;

	private boolean ignoreEvents;
	private VisualNode hitObject;
	@Inject
	SystemUnit systemUnit;

	public AutoSnapTool(String name) {

		super(name);

		setMoveToStraightEdge(true);
	}


	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (!active) {
			return false;
		}

		hitObject = GraphInteractionUtil.getFirstHitNodeAt(visualGraph, pt);

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
			final Transform transform = visualGraph.getTransformer();
			cursor = transform.transformToScreen(pt);
			graphDocument.update();
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
			final Transform transform = visualGraph.getTransformer();
			cursor = transform.transformToScreen(pt);
			graphDocument.update();
		}
		return false;
	}

	private void snapToGrid(Point pt) {

		if (hitObject != null) {
			if (hitObject.isSelected() && visualGraph.getSelection().size() == 1) {
				snapToGrid(hitObject);
			}
		}
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (onTop && showPositionLines && cursor != null) {
			paintPositionLines(awtCanvas, context.getVisibleBounds());
		}
	}

	private void paintPositionLines(AWTCanvas canvas, Rectangle r) {

		canvas.gfx.setColor(Color.red);
		canvas.gfx.drawLine(r.x, cursor.y, r.x + r.width, cursor.y);
		canvas.gfx.drawLine(cursor.x, r.y, cursor.x, r.y + r.height);
	}

	void snapToGrid(VisualNode node) {

		if (ignoreEvents || !isSnapToGrid()) {
			return;
		}
		ignoreEvents = true;
		final int unit = (int) systemUnit.getPixelsPerUnit();
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
		for (final VisualEdge edge : edges) {
			adjustSourceNode(edge);
		}
		edges = node.getIncomingEdges();
		for (final VisualEdge edge : edges) {
			adjustTargetNode(edge);
		}
	}

	private void adjustSourceNode(VisualEdge edge) {

		final VisualNode sourceNode = edge.getSourceNode();
		final VisualNode targetNode = edge.getTargetNode();
		if (sourceNode == null || targetNode == null) {
			return;
		}

		final int sourcePortId = edge.getSourcePortId();
		final int targetPortId = edge.getTargetPortId();
		final Point ptStart = getPortPosition(sourceNode, sourcePortId);
		final Point ptEnd = getPortPosition(targetNode, targetPortId);

		int dx = ptStart.x - ptEnd.x;
		int dy = ptStart.y - ptEnd.y;
		if (Math.abs(dx) > AutoSnapTool.EDGE_STRAIGHT_RANGE) {
			dx = 0;
		}
		if (Math.abs(dy) > AutoSnapTool.EDGE_STRAIGHT_RANGE) {
			dy = 0;
		}
		if (dx != 0 || dy != 0) {
			sourceNode.move(-dx, -dy);
		}
	}

	private Point getPortPosition(VisualNode node, int portId) {

		if (node != null && portId != -1) {
			final PortSet ps = node.getPortSet();
			if (ps != null) {
				final Port port = ps.getPortByID(portId);
				if (port != null) {
					return port.getPosition();
				}
			}
		}
		return new Point();
	}

	private void adjustTargetNode(VisualEdge edge) {

		final VisualNode sourceNode = edge.getSourceNode();
		final VisualNode targetNode = edge.getTargetNode();
		if (sourceNode == null || targetNode == null) {
			return;
		}

		final int sourcePortId = edge.getSourcePortId();
		final int targetPortId = edge.getTargetPortId();
		final Point ptStart = getPortPosition(sourceNode, sourcePortId);
		final Point ptEnd = getPortPosition(targetNode, targetPortId);

		int dx = ptStart.x - ptEnd.x;
		int dy = ptStart.y - ptEnd.y;
		if (Math.abs(dx) > AutoSnapTool.EDGE_STRAIGHT_RANGE) {
			dx = 0;
		}
		if (Math.abs(dy) > AutoSnapTool.EDGE_STRAIGHT_RANGE) {
			dy = 0;
		}
		if (dx != 0 || dy != 0) {
			targetNode.move(dx, dy);
		}
	}

}
