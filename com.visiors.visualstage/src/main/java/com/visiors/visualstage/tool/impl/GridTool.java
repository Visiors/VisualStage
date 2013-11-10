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
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.transform.Transform;

public class GridTool extends BaseTool {

	public enum GridStyle {
		Line, Dot, Cross
	};

	private final Color lineColor = new Color(0xD8E5E5);
	private final Color sublineColor  = new Color(0xEBF2F2);
	private static Stroke solidStroke = new BasicStroke(1.0f);
	private final SystemUnit systemUnit;

	/* Centimetre unit */
	private static final int CROSS_SIZE = 3;


	public GridTool(String name) {

		super(name);
		this.systemUnit = DI.getInstance(SystemUnit.class);
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if(!onTop && context.getResolution() == Resolution.SCREEN){
			final Transform xform = graphDocument.getTransformer();
			final Rectangle r = graphDocument.getClientBoundary();
			r.x += xform.getXTranslate(); 
			r.y += xform.getYTranslate(); 
			draw(awtCanvas.gfx, r, systemUnit.getPixelsPerUnit(), GridStyle.Line) ;
		}
	}


	public void draw(Graphics2D gfx, Rectangle r, double pixelsPreUnit, GridStyle style) {

		final Transform transformer = graphDocument.getTransformer();

		if (transformer.getScale() < 0.01) {
			return;
		}

		double transUnit = transformer.getScale() * pixelsPreUnit;

		//		while (transUnit < pixelsPreUnit / 2) {
		//			transUnit *= 2;
		//		}
		//
		//		while (transUnit > pixelsPreUnit * 2) {
		//			transUnit /= 2;
		//		}

		final double xOffset = r.x % transUnit;
		final double yOffset = r.y % transUnit;

		gfx.setStroke(solidStroke);

		switch (style) {
		case Line:
			gfx.setColor(sublineColor);
			for (double x = xOffset + transUnit / 2; x < r.width; x += transUnit) {
				gfx.drawLine((int) x, 0, (int) x, r.height);
			}

			for (double y = yOffset + transUnit / 2; y < r.height; y += transUnit) {
				gfx.drawLine(0, (int) y, r.width, (int) y);
			}

			gfx.setColor(lineColor);
			for (double x = xOffset; x < r.width; x += transUnit) {
				gfx.drawLine((int) x, 0, (int) x, r.height);
			}

			for (double y = yOffset; y < +r.height; y += transUnit) {
				gfx.drawLine(0, (int) y, r.width, (int) y);
			}
			break;
		case Dot:
			gfx.setColor(lineColor);
			for (double y = yOffset; y < r.height; y += transUnit) {
				for (double x = xOffset; x < r.width; x += transUnit) {
					gfx.drawRect((int) x, (int) y, 1, 1);
				}
			}
			break;
		case Cross:
			gfx.setColor(lineColor);
			for (double x = xOffset; x < r.width; x += transUnit) {
				for (double y = yOffset; y < r.height; y += transUnit) {
					gfx.drawLine((int) x, (int) y - CROSS_SIZE, (int) x, (int) y + CROSS_SIZE);
				}
			}
			for (double y = yOffset; y < r.height; y += transUnit) {
				for (double x = xOffset; x < r.width; x += transUnit) {
					gfx.drawLine((int) x - CROSS_SIZE, (int) y, (int) x + CROSS_SIZE, (int) y);
				}
			}
		}

		gfx.setColor(lineColor);
		gfx.drawRect(0, 0, r.width, r.height);
	}

	private void drawLineStyle() {

		// TODO Auto-generated method stub

	}

}
