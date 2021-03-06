package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.ComponentRenderer;

public class ScrollBarThumbPainter implements ComponentRenderer {

	private final ScrollBar scrollBar;

	public ScrollBarThumbPainter(ScrollBar scrollBar) {

		super();
		this.scrollBar = scrollBar;
	}

	@Override
	public Rectangle getBounds() {

		return scrollBar.getRectThumb();
	}

	@Override
	public void draw(Graphics2D gfx) {

		final Rectangle r = getBounds();
		if (scrollBar.isArmed()) {
			drawActiveThum(gfx, r);
			drawZoomBar(gfx, r);
		} else {
			drawInactiveThum(gfx, r);
		}
		drawHandle(gfx, r);
	}

	private void drawActiveThum(Graphics2D gfx, Rectangle r) {

		GradientPaint p;
		if (scrollBar.isHorizontal()) {
			p = new GradientPaint(0, r.y, StageStyleConstants.scrollbar_thumbArmedColor2, 0, r.y + r.height,
					StageStyleConstants.scrollbar_thumbArmedColor1);
			gfx.setPaint(p);
			gfx.fillRect(r.x + 1, r.y, r.width - 1, r.height);
			p = new GradientPaint(0, r.y + r.height / 2, StageStyleConstants.scrollbar_thumbArmedColor1, 0, r.y + r.height,
					StageStyleConstants.scrollbar_thumbArmedColor2);
			gfx.setPaint(p);
			gfx.fillRect(r.x + 1, r.y + r.height / 2 + 1, r.width - 1, r.height / 2);
			gfx.setColor(StageStyleConstants.scrollbar_thumbArmedFrameColor);
			gfx.drawRoundRect(r.x + 1, r.y, r.width - 2, r.height, StageStyleConstants.scrollbar_thumbFrameRounding,
					StageStyleConstants.scrollbar_thumbFrameRounding);
		} else {

			p = new GradientPaint(r.x + 2, 0, StageStyleConstants.scrollbar_backgroundColor2, r.x + r.width, 0,
					StageStyleConstants.scrollbar_thumbArmedColor1);
			gfx.setPaint(p);
			gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height);
			gfx.setColor(StageStyleConstants.scrollbar_thumbFrameColor);
			gfx.drawRoundRect(r.x, r.y + 1, r.width, r.height - 2, StageStyleConstants.scrollbar_thumbFrameRounding,
					StageStyleConstants.scrollbar_thumbFrameRounding);

		}
	}

	private void drawInactiveThum(Graphics2D gfx, Rectangle r) {

		if (scrollBar.isHorizontal()) {

			final GradientPaint p = new GradientPaint(0, r.y, StageStyleConstants.scrollbar_thumbInactiveColor2, 0, r.y
					+ r.height, StageStyleConstants.scrollbar_thumbInactiveColor1);
			gfx.setPaint(p);
			gfx.fillRect(r.x + 1, r.y + 1, r.width, r.height - 2);

			gfx.setColor(StageStyleConstants.scrollbar_thumbFrameColor);
			gfx.drawRoundRect(r.x + 1, r.y, r.width - 2, r.height, StageStyleConstants.scrollbar_thumbFrameRounding,
					StageStyleConstants.scrollbar_thumbFrameRounding);
		} else {

			final GradientPaint p = new GradientPaint(r.x, 0, StageStyleConstants.scrollbar_thumbInactiveColor2, r.x
					+ r.width, 0, StageStyleConstants.scrollbar_thumbInactiveColor1);
			gfx.setPaint(p);
			gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height);
			gfx.setColor(StageStyleConstants.scrollbar_thumbFrameColor);
			gfx.drawRoundRect(r.x, r.y + 1, r.width, r.height - 2, StageStyleConstants.scrollbar_thumbFrameRounding,
					StageStyleConstants.scrollbar_thumbFrameRounding);
		}
	}

	private void drawHandle(Graphics2D gfx, Rectangle r) {

		gfx.setColor(StageStyleConstants.scrollbar_thumbHandleColor);

		final int x = r.x + r.width / 2;
		final int y = r.y + r.height / 2;
		if (scrollBar.isHorizontal()) {
			gfx.drawLine(x - 3, y - 3, x - 3, y + 3);
			gfx.drawLine(x - 1, y - 3, x - 1, y + 3);
			gfx.drawLine(x + 1, y - 3, x + 1, y + 3);
			gfx.drawLine(x + 3, y - 3, x + 3, y + 3);

		} else {
			gfx.drawLine(x - 3, y - 3, x + 4, y - 3);
			gfx.drawLine(x - 3, y - 1, x + 4, y - 1);
			gfx.drawLine(x - 3, y + 1, x + 4, y + 1);
			gfx.drawLine(x - 3, y + 3, x + 4, y + 3);
		}
	}

	private void drawZoomBar(Graphics2D gfx, Rectangle r) {

		drawInactiveZoomBar(gfx, r);
		if (scrollBar.isZoomMinusArmed() || scrollBar.isZoomPlusArmed()) {
			drawActiveZoomBar(gfx, r);
		}
	}

	private void drawInactiveZoomBar(Graphics2D gfx, Rectangle r) {

		final int size = scrollBar.getSize();
		gfx.setColor(StageStyleConstants.scrollbar_zoomBarColor);
		if (scrollBar.isHorizontal()) {
			gfx.drawLine(r.x + 1, r.y + 4, r.x + 1, r.y + size - 4);
			gfx.drawLine(r.x + r.width - 1, r.y + 4, r.x + r.width - 1, r.y + size - 4);
		} else {
			gfx.drawLine(r.x + 4, r.y + 1, r.x + size - 4, r.y + 1);
			gfx.drawLine(r.x + 4, r.y + r.height - 1, r.x + size - 4, r.y + r.height - 1);
		}
	}

	private void drawActiveZoomBar(Graphics2D gfx, Rectangle r) {

		final int size = scrollBar.getSize();
		if (scrollBar.isHorizontal()) {
			if (scrollBar.isZoomMinusArmed()) {
				final GradientPaint p = new GradientPaint(r.x - 5, r.y, StageStyleConstants.scrollbar_thumbArmedColor2,
						r.x + 5, r.y + r.height, StageStyleConstants.scrollbar_zoomBarArmedColor);
				gfx.setPaint(p);
				gfx.fillRect(r.x + 2, r.y + 2, 3, size - 2);
			} else if (scrollBar.isZoomPlusArmed()) {
				final GradientPaint p = new GradientPaint(r.x + r.width - 5, r.y + r.height,
						StageStyleConstants.scrollbar_zoomBarArmedColor, r.x + r.width + 5, r.y,
						StageStyleConstants.scrollbar_thumbArmedColor2);
				gfx.setPaint(p);
				gfx.fillRect(r.x + r.width - 3, r.y + 2, 3, size - 2);
			}
		} else {
			if (scrollBar.isZoomMinusArmed()) {
				final GradientPaint p = new GradientPaint(r.x, r.y - 5, StageStyleConstants.scrollbar_thumbArmedColor2, r.x
						+ size, r.y + 5, StageStyleConstants.scrollbar_zoomBarArmedColor);
				gfx.setPaint(p);
				gfx.fillRect(r.x + 1, r.y + 2, size - 1, 3);
			} else if (scrollBar.isZoomPlusArmed()) {
				final GradientPaint p = new GradientPaint(r.x, r.y + r.height - 5,
						StageStyleConstants.scrollbar_zoomBarArmedColor, r.x + size, r.y + r.height + 5,
						StageStyleConstants.scrollbar_thumbArmedColor2);
				gfx.setPaint(p);
				gfx.fillRect(r.x + 1, r.y + r.height - 3, size - 1, 3);
			}
		}
	}
}
