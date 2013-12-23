package com.visiors.visualstage.tool.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.util.GraphInteractionUtil;
import com.visiors.visualstage.validation.Validator;

/**
 * 
 * @version $Id: $
 */
public class MoveSelectionTool extends BaseTool {

	protected static final double PRESISTION_FACTOR = 20.0;
	protected Point mousePressedPos;
	protected VisualGraphObject hitObject;
	protected Rectangle hitObjectOringalPos;
	protected boolean isMoving;
	@Inject
	protected Validator validator;
	@Inject
	UndoRedoHandler undoRedoHandler;
	private Point currentMousePos;

	public MoveSelectionTool(String name) {

		super(name);

		DI.injectMembers(this);
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		if (isMoving() && isAltKeyPressed(keyCode)) {
			mousePressedPos = currentMousePos;
			hitObjectOringalPos = hitObject.getBounds();
		}
		return false;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		hitObject = GraphInteractionUtil.getFirstHitObjectAt(visualGraph, pt);
		if (hitObject != null) {
			mousePressedPos = pt;
			hitObjectOringalPos = hitObject.getBounds();
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (hitObject != null) {
			onEndMoving();
			hitObject = null;
			isMoving = false;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (hitObject != null) {
			if (!isMoving()) {
				onStartMoving();
			}
			moveSelection(pt, isAltKeyPressed(functionKey));
			return true;
		}
		return false;
	}

	private final boolean isMoving() {

		return isMoving;
	}

	private final void onStartMoving() {

		undoRedoHandler.stratOfGroupAction();
		// notify object about imminent action
		final List<VisualGraphObject> selection = visualGraph.getSelection();
		for (final VisualGraphObject vgo : selection) {
			vgo.startManipulating();
		}
		isMoving = true;
	}

	private final void onEndMoving() {

		// notify object about the end of move action
		final List<VisualGraphObject> selection = visualGraph.getSelection();
		for (final VisualGraphObject vgo : selection) {
			vgo.endManipulating();
		}
		undoRedoHandler.endOfGroupAction();
	}

	@Override
	public int getPreferredCursor() {

		return hitObject != null ? Interactable.CURSOR_MOVE : Interactable.CURSOR_DEFAULT;
	}

	private synchronized void moveSelection(Point pt, boolean fineTuning) {

		if (hitObject != null) {
			final double presistionFactor = fineTuning ? PRESISTION_FACTOR : 1.0;
			final Point hitObjectCurrentPos = hitObject.getBounds().getLocation();
			final int dx = (int) ((mousePressedPos.x - pt.x) / presistionFactor) + hitObjectCurrentPos.x
					- hitObjectOringalPos.x;
			final int dy = (int) ((mousePressedPos.y - pt.y) / presistionFactor) + hitObjectCurrentPos.y
					- hitObjectOringalPos.y;
			final List<VisualGraphObject> selection = visualGraph.getSelection();
			this.currentMousePos = pt;
			moveConnectedEdges(selection, dx, dy);
			moveEdges(selection, dx, dy);
			moveNodes(selection, dx, dy);
		}
	}

	/**
	 * Moves edges that are not selected but connected to selected nodes
	 */
	private void moveConnectedEdges(List<VisualGraphObject> selection, int dx, int dy) {

		final List<VisualEdge> edgesToMove = new ArrayList<VisualEdge>();
		VisualNode sn, tn;

		for (final VisualGraphObject vgo : selection) {
			if (vgo instanceof VisualNode) {
				final VisualNode n = (VisualNode) vgo;
				List<VisualEdge> connections = n.getOutgoingEdges();
				for (final VisualEdge edge : connections) {
					if (!edge.isSelected()) {
						tn = edge.getTargetNode();
						if (tn != null && tn.isSelected() && !edgesToMove.contains(edge)) {
							edgesToMove.add(edge);
						}
					}
				}
				connections = n.getIncomingEdges();
				for (final VisualEdge edge : connections) {
					if (!edge.isSelected()) {
						sn = edge.getSourceNode();
						if (sn != null && sn.isSelected() && !edgesToMove.contains(edge)) {
							edgesToMove.add(edge);
						}
					}
				}
			}
		}
		for (final VisualEdge edge : edgesToMove) {
			if (validator.permitMovingEdge(edge, -dx, -dy)) {
				edge.move(-dx, -dy);
			}
		}
	}

	private void moveEdges(List<VisualGraphObject> selection, int dx, int dy) {

		for (final VisualGraphObject vgo : selection) {
			if (vgo instanceof VisualEdge) {
				final VisualEdge edge = (VisualEdge) vgo;
				if (validator.permitMovingEdge(edge, -dx, -dy)) {
					edge.move(-dx, -dy);
				}
			}
		}
	}

	private void moveNodes(List<VisualGraphObject> selection, int dx, int dy) {

		for (final VisualGraphObject vgo : selection) {
			if (vgo instanceof VisualNode) {
				final VisualNode node = (VisualNode) vgo;
				if (validator.permitMovingNode(node, -dx, -dy)) {
					node.move(-dx, -dy);
				}
			}
		}
	}
}
