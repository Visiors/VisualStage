package com.visiors.visualstage.interaction.impl.marquee;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.constants.InteractionConstants;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.interaction.impl.BaseInteractionHandler;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.transform.Transform;

public class MarqueeSelectionMode extends BaseInteractionHandler {

	private final Rectangle marqueeRect = new Rectangle();
	private final Color lineColor;
	private final List<VisualGraphObject> objectInMarquee = new ArrayList<VisualGraphObject>();
	private Point mousePressedPos;

	@Inject
	private UndoRedoHandler undoRedoHandler;

	@Inject
	public MarqueeSelectionMode() {

		super();

		lineColor = new Color(0x9999aa); // UIManager.getColor("MinuetLnF.Marquee.Color");
	}

	@Override
	public String getName() {

		return InteractionConstants.MODE_MARQUEE_SELECTION;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		undoRedoHandler.stratOfGroupAction();

		VisualGraphObject[] hit = visualGraph.getHitObjects(pt);
		if (hit.length == 0) {
			final Transform transformer = visualGraph.getTransform();
			mousePressedPos = transformer.transformToScreen(pt);
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		undoRedoHandler.endOfGroupAction();
		mousePressedPos = null;
		if (!isEmpty()) {
			empty();
			visualGraph.updateView();
		}
		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (mousePressedPos != null) {
			final Transform transformer = visualGraph.getTransform();
			setMarqueeRect(mousePressedPos, transformer.transformToScreen(pt));
			visualGraph.updateView();
		}
		return false;
	}

	void setMarqueeRect(Point pt1, Point pt2) {

		marqueeRect.x = Math.min(pt1.x, pt2.x);
		marqueeRect.y = Math.min(pt1.y, pt2.y);
		marqueeRect.width = Math.abs(pt2.x - pt1.x);
		marqueeRect.height = Math.abs(pt2.y - pt1.y);
		updateObjectSelectionState();
	}

	/**
	 * This method looks for all objects in the marquee area regardless their
	 * selection state. It uses the internal list "allObjectInMarquee" to keep
	 * track on the objects. An internal list is required since in
	 * multi-selection mode we have to leave objects in their original state if
	 * the user make the marquee boundary smaller.
	 */
	private void updateObjectSelectionState() {

		VisualGraphObject vobj;
		Rectangle robj;
		VisualGraphObject[] objects = visualGraph.getGraphObjects();

		final Transform transformer = visualGraph.getTransform();
		for (VisualGraphObject object : objects) {
			vobj = object;
			robj = transformer.transformToScreen(vobj.getBounds());

			/*
			 * add the object into the list if it is within the marquee bounding
			 * box
			 */
			if (marqueeRect.contains(robj)) {
				if (!objectInMarquee.contains(vobj)) {
					vobj.setSelected(!vobj.isSelected());
					objectInMarquee.add(vobj);
				}
			}
			/*
			 * if an objects is not in the list anymore invert its selection
			 * state in order to recover its original selection state and remove
			 * it from the list
			 */
			else {
				if (objectInMarquee.contains(vobj)) {
					vobj.setSelected(!vobj.isSelected());
					objectInMarquee.remove(vobj);
				}
			}
		}
	}

	boolean isEmpty() {

		return marqueeRect.isEmpty();
	}

	void empty() {

		marqueeRect.setSize(0, 0);
		objectInMarquee.clear();
	}

	@Override
	public void paintOnBackground(Canvas canvas, DrawingContext r) {

		if (!marqueeRect.isEmpty()) {

			canvas.setStroke(1.0f, new float[] { 3, 1 });
			canvas.setColor(lineColor);
			// canvas.setXORMode(new Color(200, 220, 255));
			canvas.drawRect(marqueeRect.x, marqueeRect.y, marqueeRect.width - 1,
					marqueeRect.height - 1);
			// canvas.setPaintMode();
		}
	}
}
