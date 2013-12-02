package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.DrawClient;

public class ScrollBarThumbPainter implements DrawClient {

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
			drawActiveThumg(gfx, r);
			drawZoomBar(gfx, r);
		} else {
			drawInactiveThumg(gfx, r);
		}
	}

	private void drawActiveThumg(Graphics2D gfx, Rectangle r) {

		GradientPaint p;
		if (scrollBar.isHorizontal()) {
			p = new GradientPaint(0, r.y, ScrollbarStyle.thumbArmedColor2, 0, r.y + r.height,
					ScrollbarStyle.thumbArmedColor1);
			gfx.setPaint(p);
			gfx.fillRect(r.x + 1, r.y, r.width - 1, r.height);
			p = new GradientPaint(0, r.y + r.height / 2, ScrollbarStyle.thumbArmedColor1, 0, r.y + r.height,
					ScrollbarStyle.thumbArmedColor2);
			gfx.setPaint(p);
			gfx.fillRect(r.x + 1, r.y + r.height / 2 + 1, r.width - 1, r.height / 2);
			gfx.setColor(ScrollbarStyle.thumbArmedFrameColor);
			gfx.drawRoundRect(r.x + 1, r.y, r.width - 2, r.height, ScrollbarStyle.thumbRounding,
					ScrollbarStyle.thumbRounding);
		} else {
			p = new GradientPaint(r.x, 0, ScrollbarStyle.thumbArmedColor2, r.x + r.width, 0,
					ScrollbarStyle.thumbArmedColor1);
			gfx.setPaint(p);
			gfx.fillRect(r.x, r.y + 1, r.width, r.height - 1);
			p = new GradientPaint(r.x + r.width / 2, 0, ScrollbarStyle.thumbArmedColor1, r.x + r.width, 0,
					ScrollbarStyle.thumbArmedColor2);
			gfx.setPaint(p);
			gfx.fillRect(r.x + r.width / 2 + 1, r.y + 1, r.width / 2, r.height - 1);
			gfx.setColor(ScrollbarStyle.thumbArmedFrameColor);
			gfx.drawRoundRect(r.x, r.y + 1, r.width, r.height - 2, ScrollbarStyle.thumbRounding,
					ScrollbarStyle.thumbRounding);
		}
	}

	private void drawInactiveThumg(Graphics2D gfx, Rectangle r) {

		if (scrollBar.isHorizontal()) {
			gfx.setColor(ScrollbarStyle.thumbColor);
			gfx.fillRect(r.x + 1, r.y + 1, r.width, r.height - 2);
			gfx.setColor(ScrollbarStyle.thumbFrameColor);
			gfx.drawRoundRect(r.x + 1, r.y, r.width - 2, r.height, ScrollbarStyle.thumbRounding,
					ScrollbarStyle.thumbRounding);
		} else {
			gfx.setColor(ScrollbarStyle.thumbColor);
			gfx.fillRect(r.x + 1, r.y + 1, r.width - 2, r.height);
			gfx.setColor(ScrollbarStyle.thumbFrameColor);
			gfx.drawRoundRect(r.x, r.y + 1, r.width, r.height - 2, ScrollbarStyle.thumbRounding,
					ScrollbarStyle.thumbRounding);
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
		gfx.setColor(ScrollbarStyle.zoomBarColor);
		if (scrollBar.isHorizontal()) {
			gfx.drawLine(r.x + 1, r.y + 4, r.x + 1, r.y + size - 5);
			gfx.drawLine(r.x + r.width - 1, r.y + 4, r.x + r.width - 1, r.y + size - 5);
		} else {
			gfx.drawLine(r.x + 4, r.y + 1, r.x + size - 4, r.y + 1);
			gfx.drawLine(r.x + 4, r.y + r.height - 1, r.x + size - 4, r.y + r.height - 1);
		}
	}

	private void drawActiveZoomBar(Graphics2D gfx, Rectangle r) {

		final int size = scrollBar.getSize();
		if (scrollBar.isHorizontal()) {
			if (scrollBar.isZoomMinusArmed()) {
				final GradientPaint p = new GradientPaint(r.x - 5, r.y, ScrollbarStyle.thumbArmedColor2, r.x + 5, r.y
						+ r.height, ScrollbarStyle.zoomBarArmedColor);
				gfx.setPaint(p);
				gfx.fillRect(r.x + 2, r.y + 1, 3, size - 1);
			} else if (scrollBar.isZoomPlusArmed()) {
				final GradientPaint p = new GradientPaint(r.x + r.width - 5, r.y + r.height,
						ScrollbarStyle.zoomBarArmedColor, r.x + r.width + 5, r.y, ScrollbarStyle.thumbArmedColor2);
				gfx.setPaint(p);
				gfx.fillRect(r.x + r.width - 4, r.y + 1, 3, size - 1);
			}
		} else {
			if (scrollBar.isZoomMinusArmed()) {
				final GradientPaint p = new GradientPaint(r.x, r.y - 5, ScrollbarStyle.thumbArmedColor2, r.x + size,
						r.y + 5, ScrollbarStyle.zoomBarArmedColor);
				gfx.setPaint(p);
				gfx.fillRect(r.x + 1, r.y + 2, size - 1, 3);
			} else if (scrollBar.isZoomPlusArmed()) {
				final GradientPaint p = new GradientPaint(r.x, r.y + r.height - 5, ScrollbarStyle.zoomBarArmedColor,
						r.x + size, r.y + r.height + 5, ScrollbarStyle.thumbArmedColor2);
				gfx.setPaint(p);
				gfx.fillRect(r.x + 1, r.y + r.height - 4, size - 1, 3);
			}
		}
	}
}
