package com.visiors.visualstage.stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import com.visiors.visualstage.transform.Transform;

public class Grid {

	public enum GridStyle {
		Line, Dot, Cross
	};

	private static Stroke solidStroke = new BasicStroke(1.0f);

	/* Grid line color */
	private final Color lineColor;
	private final Color sublineColor;

	/* Centimetre unit */
	private static final int CROSS_SIZE = 4;
	private final Transform transformer;

	public Grid(Transform transformer) {

		this.transformer = transformer;
		lineColor = new Color(0xD8E5E5);// UIManager.getColor("MinuetLnF.Ruler.Line.Color");
		sublineColor = new Color(0xEBF2F2); // UIManager.getColor("MinuetLnF.Ruler.Subline.Color");
	}

	public void draw( Graphics2D gfx, Rectangle r, double pixelsPreUnit, GridStyle style) {


		double transUnit = transformer.getScale() * pixelsPreUnit;

		while (transUnit < pixelsPreUnit / 2) {
			transUnit *= 2;
		}

		while (transUnit > pixelsPreUnit * 2) {
			transUnit /= 2;
		}

		double xOffset = Math.abs(transformer.getTranslateX() - r.x) % transUnit - transUnit;
		double yOffset = Math.abs(transformer.getTranslateY() - r.y) % transUnit - transUnit;

		if ((int) xOffset < 0) {
			xOffset += transUnit;
		}
		if ((int) yOffset < 0) {
			yOffset += transUnit;
		}

		gfx.setStroke(solidStroke);
		switch (style) {
		case Line:

			gfx.setColor(sublineColor);
			for (double x = r.x + xOffset + transUnit / 2; x < r.x + r.width; x += transUnit) {
				gfx.drawLine((int) x, r.y, (int) x, r.y + r.height);
			}

			for (double y = r.y + yOffset + transUnit / 2; y < r.y + r.height; y += transUnit) {
				gfx.drawLine(r.x, (int) y, r.x + r.width, (int) y);
			}

			gfx.setColor(lineColor);
			int test = 0;
			for (double x = r.x + xOffset; x < r.x + r.width; x += transUnit, test++) {
				gfx.drawLine((int) x, r.y, (int) x, r.y + r.height);
				// System.err.println("Number of drawn lines: " + test);
			}

			for (double y = r.y + yOffset; y < r.y + r.height; y += transUnit) {
				gfx.drawLine(r.x, (int) y, r.x + r.width, (int) y);
			}

			break;
		case Dot:
			gfx.setColor(lineColor);
			for (double y = r.y + yOffset; y < r.y + r.height; y += transUnit) {
				for (double x = r.x + xOffset; x < r.x + r.width; x += transUnit) {
					gfx.drawRect((int) x, (int) y, 1, 1);
				}
			}
			break;
		case Cross:
			gfx.setColor(lineColor);
			for (double x = r.x + xOffset; x < r.x + r.width; x += transUnit) {
				for (double y = r.y + yOffset - Grid.CROSS_SIZE; y < r.y + r.height; y += transUnit) {
					gfx.drawLine((int) x, (int) y - Grid.CROSS_SIZE, (int) x, (int) y + Grid.CROSS_SIZE);
				}
			}
			for (double y = r.y + yOffset - Grid.CROSS_SIZE; y < r.y + r.height; y += transUnit) {
				for (double x = r.x + xOffset; x < r.x + r.width; x += transUnit) {
					gfx.drawLine((int) x - Grid.CROSS_SIZE, (int) y, (int) x + Grid.CROSS_SIZE, (int) y);
				}
			}
		}

		gfx.setColor(lineColor);
		gfx.drawRect(r.x, r.y, r.width, r.height);
	}

}
