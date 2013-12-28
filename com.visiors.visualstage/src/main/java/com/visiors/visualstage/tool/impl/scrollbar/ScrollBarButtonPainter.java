package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.ComponentRenderer;

public class ScrollBarButtonPainter implements ComponentRenderer {

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
		gfx.setColor(scrollBar.isArmed() ? StageStyleConstants.scrollbar_buttonArrowArmedColor
				: StageStyleConstants.scrollbar_buttonArrowColor);
		if (scrollBar.isHorizontal()) {
			drawArrowToWest(gfx, r.x + r.width / 3, r.y + r.height / 3, 4, 8);
		} else {
			drawArrowToNorth(gfx, r.x + r.width / 3 - 1, r.y + r.height / 3, 9, 4);
		}
	}

	private void drawButtonPlus(Graphics2D gfx) {

		final Rectangle r = scrollBar.getRectPlusButton();
		drawButton(gfx, r, false);
		gfx.setColor(scrollBar.isArmed() ? StageStyleConstants.scrollbar_buttonArrowArmedColor
				: StageStyleConstants.scrollbar_buttonArrowColor);
		if (scrollBar.isHorizontal()) {
			drawArrowToEast(gfx, r.x + r.width / 3, r.y + r.height / 3, 4, 8);
		} else {
			drawArrowToSouth(gfx, r.x + r.width / 3, r.y + r.height / 3, 7, 4);
		}
	}

	private void drawButton(Graphics2D gfx, Rectangle r, boolean minus) {

		if (scrollBar.isArmed()) {
			if (scrollBar.isHorizontal()) {
				GradientPaint paint = new GradientPaint(0, r.y, StageStyleConstants.scrollbar_buttonArmedColor2, 0, r.y
						+ r.height, StageStyleConstants.scrollbar_buttonArmedColor1);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height - 2);
				paint = new GradientPaint(0, r.y + r.height / 2 - 5, StageStyleConstants.scrollbar_buttonArmedColor1, 0, r.y
						+ r.height, StageStyleConstants.scrollbar_buttonArmedColor2);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + 1, r.y + r.height / 2, r.width - 1, r.height / 2);

				gfx.setColor(StageStyleConstants.scrollbar_buttonFrameColor);
				gfx.drawRect(r.x, r.y, r.width - 1, r.height);
			} else {
				GradientPaint paint = new GradientPaint(r.x, 0, StageStyleConstants.scrollbar_buttonArmedColor2, r.x
						+ r.width, 0, StageStyleConstants.scrollbar_buttonArmedColor1);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height - 2);
				paint = new GradientPaint(r.x + r.width / 2 - 5, 0, StageStyleConstants.scrollbar_buttonArmedColor1, r.x
						+ r.width, 0, StageStyleConstants.scrollbar_buttonArmedColor2);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + r.width / 2, r.y, r.width / 2, r.height);

				gfx.setColor(StageStyleConstants.scrollbar_buttonFrameColor);
				gfx.drawRect(r.x, r.y, r.width, r.height - 1);
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

		gfx.fillPolygon(new Polygon(new int[] { x, x + w / 2, x + w }, new int[] { y + h, y, y + h }, 3));
	}

	private void drawArrowToSouth(Graphics2D gfx, int x, int y, int w, int h) {

		gfx.fillPolygon(new Polygon(new int[] { x, x + w, x + w / 2 }, new int[] { y, y, y + h }, 3));
	}
}
