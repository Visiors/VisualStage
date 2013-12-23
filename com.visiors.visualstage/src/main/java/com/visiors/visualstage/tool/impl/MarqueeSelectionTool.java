package com.visiors.visualstage.tool.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.transform.Transform;

public class MarqueeSelectionTool extends BaseTool {

	private static Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 3, 1 }, 0);
	private final Rectangle marqueeRect = new Rectangle();
	private final Color lineColor = new Color(0x9999aa);;
	private List<VisualGraphObject> existingSelection;
	private Point mousePressedPos;
	private Point mouseCurrentPos;

	@Inject
	private UndoRedoHandler undoRedoHandler;

	public MarqueeSelectionTool(String name) {

		super(name);
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		// undoRedoHandler.stratOfGroupAction();
		final List<VisualGraphObject> hit = visualGraph.getHitObjects(pt);
		if (hit.isEmpty()) {
			mousePressedPos = pt;
			if (isControlKeyPressed(functionKey)) {
				existingSelection = visualGraph.getSelection();
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		// undoRedoHandler.endOfGroupAction();
		mousePressedPos = null;
		if (!marqueeRect.isEmpty()) {
			empty();
			graphDocument.invalidate();
		}
		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (mousePressedPos != null) {
			mouseCurrentPos = pt;
			updateMarqueeRect();
			updateObjectSelectionState(isControlKeyPressed(functionKey));
			graphDocument.invalidate();
		}
		return false;
	}

	void updateMarqueeRect() {

		if (mousePressedPos != null && mouseCurrentPos != null) {

			final Transform transformer = visualGraph.getTransformer();
			final Point pt1Screen = transformer.transformToScreen(mousePressedPos);
			final Point pt2Screen = transformer.transformToScreen(mouseCurrentPos);
			marqueeRect.x = Math.min(pt1Screen.x, pt2Screen.x);
			marqueeRect.y = Math.min(pt1Screen.y, pt2Screen.y);
			marqueeRect.width = Math.abs(pt2Screen.x - pt1Screen.x);
			marqueeRect.height = Math.abs(pt2Screen.y - pt1Screen.y);
		}
	}

	private void updateObjectSelectionState(boolean xorSelectionMode) {

		final List<VisualGraphObject> objects = visualGraph.getGraphObjects();
		final Transform transformer = visualGraph.getTransformer();

		// -select only objects inside the selection rectangle
		if (!xorSelectionMode) {
			visualGraph.clearSelection();
			for (final VisualGraphObject vobj : objects) {
				if (marqueeRect.contains(transformer.transformToScreen(vobj.getBounds()))) {
					vobj.setSelected(true);
				}
			}
		} else {
			// -keep the selection state of objects outside the marquee
			// -Invert the selection state of objects inside
			for (final VisualGraphObject vobj : objects) {
				if (marqueeRect.contains(transformer.transformToScreen(vobj.getBounds()))) {
					vobj.setSelected(existingSelection == null || !existingSelection.contains(vobj));
				}
			}
		}
	}

	void empty() {

		marqueeRect.setSize(0, 0);
		if (existingSelection != null) {
			existingSelection = null;
		}
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (onTop) {

			if (!marqueeRect.isEmpty()) {
				awtCanvas.gfx.setStroke(dashedStroke);
				awtCanvas.gfx.setColor(lineColor);
				final Rectangle rClient = graphDocument.getClientBoundary();
				awtCanvas.gfx.setClip(rClient);
				awtCanvas.gfx.drawRect(marqueeRect.x, marqueeRect.y, marqueeRect.width - 1, marqueeRect.height - 1);
				awtCanvas.gfx.setClip(null);
			}
		}
	}
}
