package com.visiors.visualstage.interaction.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.util.GraphInteractionUtil;

/**
 * 
 * Coordinating the interactions. - MAking sure that objects receive mouse/key
 * events - Ensures that manipulable object receive relevant information so that
 * they can react on user interaction. on user user action - Taking care of top
 * level actions like: + moving objects + connecting / reconnecting edges + copy
 * on move
 * 
 * @version $Id: $
 */
public class MoveObjectTool extends BaseTool {


	private Point mousePressedPos;
	private Rectangle hitObjectPos;
	private VisualGraphObject hitObject;
	private boolean manipulatingNotified;



	protected MoveObjectTool(String name) {

		super(name);
	}



	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		hitObject = GraphInteractionUtil.getFirstHitObjectAt(visualGraph, pt);

		if (hitObject != null ) {
			mousePressedPos = pt;
			hitObjectPos = hitObject.getBounds();
		}

		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {


		if(hitObject != null){
			notifyActionEnd();
			mousePressedPos = null;
			hitObjectPos = null;
			hitObject = null;
			manipulatingNotified = false;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (hitObject != null) {
			if (!wasManipulationNotified()) {
				notifyActionBegin();
			}
			moveSelection(pt);
			return true;
		}
		return false;
	}

	private final boolean wasManipulationNotified() {

		return manipulatingNotified;
	}

	private final void notifyActionBegin() {

		//		undoRedoHandler.stratOfGroupAction();
		// notify object about immediate actions
		final List<VisualGraphObject> selection = visualGraph.getSelection();
		for (final VisualGraphObject vgo : selection) {
			vgo.startManipulating();
		}
		manipulatingNotified = true;
	}
	private final void notifyActionEnd() {

		//		undoRedoHandler.endOfGroupAction();
		// notify object about immediate actions
		final List<VisualGraphObject> selection = visualGraph.getSelection();
		for (final VisualGraphObject vgo : selection) {
			vgo.endManipulating();
		}
		manipulatingNotified = true;
	}

	@Override
	public int getPreferredCursor() {

		if (hitObject != null) {
			return Interactable.CURSOR_MOVE;
		}
		return Interactable.CURSOR_DEFAULT;
	}



	private synchronized void moveSelection(Point pt) {

		if (hitObject != null) {

			final Point currentPos = hitObject.getBounds().getLocation();
			final int dx = mousePressedPos.x - pt.x + currentPos.x - hitObjectPos.x;
			final int dy = mousePressedPos.y - pt.y + currentPos.y - hitObjectPos.y;

			final List<VisualGraphObject> selection = visualGraph.getSelection();

			moveEdgesWithSelectedNodes(selection, dx, dy);
			moveEdges(selection, dx, dy);
			moveNodes(selection, dx, dy);

		}
	}

	private void moveEdgesWithSelectedNodes(List<VisualGraphObject> selection, int dx, int dy) {

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
			edge.move(-dx, -dy);
		}
	}

	private void moveEdges(List<VisualGraphObject> selection, int dx, int dy) {

		for (final VisualGraphObject vgo : selection) {
			if (vgo instanceof VisualEdge) {
				vgo.move(-dx, -dy);
			}
		}
	}

	private void moveNodes(List<VisualGraphObject> selection, int dx, int dy) {

		for (final VisualGraphObject vgo : selection) {
			if (vgo instanceof VisualNode) {
				vgo.move(-dx, -dy);
			}
		}
	}

}
