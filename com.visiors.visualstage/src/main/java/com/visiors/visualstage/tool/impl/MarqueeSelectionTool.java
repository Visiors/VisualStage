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

	private final Rectangle marqueeRect = new Rectangle();
	private final Color lineColor;
	private List<VisualGraphObject> existingSelection;
	private Point mousePressedPos;
	private static Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 3, 1 }, 0);

	@Inject
	private UndoRedoHandler undoRedoHandler;


	public MarqueeSelectionTool(String name) {

		super(name);

		lineColor = new Color(0x9999aa); // UIManager.getColor("MinuetLnF.Marquee.Color");
	}



	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		//		undoRedoHandler.stratOfGroupAction();
		final List<VisualGraphObject> hit = visualGraph.getHitObjects(pt);
		if (hit.isEmpty()) {
			final Transform transformer = visualGraph.getTransformer();
			mousePressedPos = transformer.transformToScreen(pt);

			if(isControlKeyPressed(functionKey)) {
				existingSelection = visualGraph.getSelection();
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		//		undoRedoHandler.endOfGroupAction();
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
			final Transform transformer = visualGraph.getTransformer();
			adjustMarqueeRect(mousePressedPos, transformer.transformToScreen(pt));
			updateObjectSelectionState(isControlKeyPressed(functionKey));
			graphDocument.invalidate();
		}
		return false;
	}

	void adjustMarqueeRect(Point pt1, Point pt2) {

		marqueeRect.x = Math.min(pt1.x, pt2.x);
		marqueeRect.y = Math.min(pt1.y, pt2.y);
		marqueeRect.width = Math.abs(pt2.x - pt1.x);
		marqueeRect.height = Math.abs(pt2.y - pt1.y);

	}

	private void updateObjectSelectionState(boolean xorSelectionMode) {

		final List<VisualGraphObject> objects = visualGraph.getGraphObjects();
		final Transform transformer = visualGraph.getTransformer();
		Rectangle r;

		// -select only objects inside the selection rectangle
		if(!xorSelectionMode) {
			visualGraph.clearSelection();
			for (VisualGraphObject vobj : objects) {
				r = transformer.transformToScreen(vobj.getBounds());
				if (marqueeRect.contains(r) ) {
					vobj.setSelected(true);
				}
			}			
		}else{
			// -keep the selection state of objects outside the marquee. 
			// -Invert the selection state of objects inside the marquee
			for (VisualGraphObject vobj : objects) {
				r = transformer.transformToScreen(vobj.getBounds());
				if (marqueeRect.contains(r) ) {
					vobj.setSelected(existingSelection == null || !existingSelection.contains(vobj));
				}
			}
		}
	}

	void empty() {

		marqueeRect.setSize(0, 0);
		if(existingSelection != null) {
			existingSelection = null;
		}
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (onTop && !marqueeRect.isEmpty()) {
			awtCanvas.gfx.setStroke(dashedStroke);
			awtCanvas.gfx.setColor(lineColor);
			//			awtCanvas.gfx.setXORMode(new Color(200, 220, 255));
			int x = (int) context.getViewport().getX();
			int y = (int) context.getViewport().getY();
			awtCanvas.gfx.drawRect(x + marqueeRect.x, y + marqueeRect.y, marqueeRect.width - 1, marqueeRect.height - 1);
			//			awtCanvas.gfx.setPaintMode();
		}
	}
}
