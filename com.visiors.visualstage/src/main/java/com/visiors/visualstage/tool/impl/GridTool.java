package com.visiors.visualstage.tool.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.stage.StageDesigner;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.transform.Transform;

public class GridTool extends BaseTool {

	public enum GridStyle {
		Line, Dot, Cross
	};

	private final Color lineColor = new Color(0xD8E5E5);
	private final Color sublineColor = new Color(0xEBF2F2);
	private static Stroke solidStroke = new BasicStroke(1.0f);
	private final SystemUnit systemUnit;

	protected StageDesigner stageDesigner;

	/* Centimetre unit */
	private static final int CROSS_SIZE = 3;

	public GridTool(String name) {

		super(name);
		this.systemUnit = DI.getInstance(SystemUnit.class);
		this.stageDesigner = DI.getInstance(StageDesigner.class);
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (!onTop && context.getResolution() == Resolution.SCREEN) {
			//			final Rectangle r = graphDocument.getClientBoundary();
			final Rectangle r = stageDesigner.getPageBounds();

			draw(awtCanvas.gfx, r, GridStyle.Line);
		}
	}

	private double computeNormalizedUnitWidth() {

		final Transform xform = graphDocument.getTransformer();
		final double pixelsPreUnit = systemUnit.getPixelsPerUnit();
		final double transUnit = xform.getScale() * pixelsPreUnit;
		final double f = transUnit / pixelsPreUnit;
		if (f > 1.0) {
			return transUnit / Math.round(f);
		} else if (f < 1.0) {
			return  transUnit * Math.round(1.0 / f);
		}
		return transUnit;
	}

	public void draw(Graphics2D gfx, Rectangle r, GridStyle style) {

		final double transUnit = computeNormalizedUnitWidth();
		final double xOffset = r.x % transUnit;
		final double yOffset = r.y % transUnit;

		gfx.setStroke(solidStroke);
		switch (style) {
		case Line:
			gfx.setColor(sublineColor);
			for (double x = xOffset + transUnit / 2; x < r.x + r.width; x += transUnit) {
				gfx.drawLine((int) x, r.y, (int) x, r.y + r.height);
			}
			for (double y = yOffset + transUnit / 2; y < r.y + r.height; y += transUnit) {
				gfx.drawLine(r.x , (int) y,r.x +  r.width, (int) y);
			}
			gfx.setColor(lineColor);
			for (double x = xOffset + transUnit; x < r.x + r.width; x += transUnit) {
				gfx.drawLine((int) x, r.y, (int) x, r.y + r.height);
			}
			for (double y = yOffset + transUnit; y < r.y + r.height; y += transUnit) {
				gfx.drawLine(r.x, (int) y, r.x + r.width, (int) y);
			}
			break;
		case Dot:
			gfx.setColor(lineColor);
			for (double y = yOffset; y < r.y + r.height; y += transUnit) {
				for (double x = xOffset; x < r.width; x += transUnit) {
					gfx.drawRect((int) x, (int) y, 1, 1);
				}
			}
			break;
		case Cross:
			gfx.setColor(lineColor);
			for (double x = xOffset; x < r.x + r.width; x += transUnit) {
				for (double y = yOffset; y < r.height; y += transUnit) {
					gfx.drawLine((int) x, (int) y - CROSS_SIZE, (int) x, (int) y + CROSS_SIZE);
				}
			}
			for (double y = yOffset; y < r.y + r.height; y += transUnit) {
				for (double x = xOffset; x < r.width; x += transUnit) {
					gfx.drawLine((int) x - CROSS_SIZE, (int) y, (int) x + CROSS_SIZE, (int) y);
				}
			}
		}
		//		gfx.setStroke(new BasicStroke(1f));
		//		gfx.setColor(Color.red);
		//		gfx.drawRect(r.x, r.y, r.width-1, r.height-1);
	}
}
