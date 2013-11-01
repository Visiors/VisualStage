package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

import com.visiors.visualstage.tool.impl.ScrollBar;

public class ScrollBarButtonPainter implements DrawClient {

	private final ScrollBar scrollBar;
	private final boolean minusButton;

	public ScrollBarButtonPainter(ScrollBar scrollBar, boolean minusButton) {

		super();
		this.scrollBar = scrollBar;
		this.minusButton = minusButton;
	}



	@Override
	public Rectangle getBounds() {

		if (minusButton) {
			return scrollBar.getRectMinusButton();
		} else {
			return scrollBar.getRectPlusButton();
		}
	}

	@Override
	public void draw(Graphics2D gfx) {

		if (minusButton) {
			drawButtonMinus(gfx);
		} else {
			drawButtonPlus(gfx);
		}

	}

	private void drawButtonMinus(Graphics2D gfx) {

		final Rectangle r = scrollBar.getRectMinusButton();
		drawButton(gfx, r, true);
		gfx.setColor(scrollBar.isArmed() ? ScrollbarStyle.arrowArmed : ScrollbarStyle.arrow);

		if (scrollBar.isHorizontal()) {
			drawArrowToWest(gfx, r.x + ScrollbarStyle.size / 3, r.y + ScrollbarStyle.size / 3, ScrollbarStyle.size / 2,
					ScrollbarStyle.size / 2);
		} else {
			drawArrowToNorth(gfx, r.x + ScrollbarStyle.size / 3, r.y + ScrollbarStyle.size / 3,
					ScrollbarStyle.size / 2, ScrollbarStyle.size / 2);
		}
	}

	private void drawButtonPlus(Graphics2D gfx) {

		final Rectangle r = scrollBar.getRectPlusButton();
		drawButton(gfx, r, false);
		gfx.setColor(scrollBar.isArmed() ? ScrollbarStyle.arrowArmed : ScrollbarStyle.arrow);
		if (scrollBar.isHorizontal()) {
			drawArrowToEast(gfx, r.x + ScrollbarStyle.size / 3, r.y + ScrollbarStyle.size / 3, ScrollbarStyle.size / 2,
					ScrollbarStyle.size / 2);
		} else {
			drawArrowToSouth(gfx, r.x + ScrollbarStyle.size / 3, r.y + ScrollbarStyle.size / 3,
					ScrollbarStyle.size / 2, ScrollbarStyle.size / 2);
		}
	}

	private void drawButton(Graphics2D gfx, Rectangle r, boolean minus) {

		if (scrollBar.isArmed()) {
			if (scrollBar.isHorizontal()) {
				GradientPaint paint = new GradientPaint(0, r.y, ScrollbarStyle.button2, 0, r.y + r.height,
						ScrollbarStyle.button1);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height - 2);
				paint = new GradientPaint(0, r.y + r.height / 2 - 5, ScrollbarStyle.button1, 0, r.y + r.height,
						ScrollbarStyle.button2);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + 1, r.y + r.height / 2, r.width - 1, r.height / 2);
				gfx.setColor(ScrollbarStyle.button1);
				if (minus) {
					gfx.drawLine(r.x + r.width, r.y + r.height - ScrollbarStyle.size + 1, r.x + r.width, r.y + r.height);
				} else {
					gfx.drawLine(r.x, r.y + r.height - ScrollbarStyle.size + 1, r.x, r.y + r.height);
				}
			} else {
				GradientPaint paint = new GradientPaint(r.x, 0, ScrollbarStyle.button2, r.x + r.width, 0,
						ScrollbarStyle.button1);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height - 2);
				paint = new GradientPaint(r.x + r.width / 2 - 5, 0, ScrollbarStyle.button1, r.x + r.width, 0,
						ScrollbarStyle.button2);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + r.width / 2, r.y, r.width / 2, r.height);
				gfx.setColor(ScrollbarStyle.button1);
				if (minus) {
					gfx.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height);
				} else {
					gfx.drawLine(r.x, r.y, r.x + r.width, r.y);
				}
			}
		}
	}

	private void drawArrowToEast(Graphics2D gfx, int x, int y, int w, int h) {

		gfx.fillPolygon(new Polygon(new int[] { x, x, x + w }, new int[] { y, y + h, y + h / 2 }, 3));
	}

	private void drawArrowToWest(Graphics2D gfx, int x, int y, int w, int h) {

		gfx.fillPolygon(new Polygon(new int[] { x, x + w, x + w }, new int[] { y + h / 2, y, y + h }, 3));
	}

	private void drawArrowToNorth(Graphics2D gfx, int x, int y, int w, int h) {

		gfx.fillPolygon(new Polygon(new int[] { x + w / 2, x, x + w }, new int[] { y, y + h, y + h }, 3));
	}

	private void drawArrowToSouth(Graphics2D gfx, int x, int y, int w, int h) {

		gfx.fillPolygon(new Polygon(new int[] { x, x + w, x + w / 2 }, new int[] { y, y, y + h }, 3));
	}

}
