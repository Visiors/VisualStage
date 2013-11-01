package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.visiors.visualstage.tool.impl.ScrollBar;

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

		Rectangle r = getBounds();
		if (!scrollBar.isArmed()) {
			gfx.setColor(new Color(200, 220, 250, 100));
			gfx.fillRect(r.x + 2, r.y + 1, r.width - 4, r.height - 2);
		} else {

			GradientPaint paint = new GradientPaint(0, r.y, ScrollbarStyle.button2, 0, r.y + r.height,
					ScrollbarStyle.button1);
			gfx.setPaint(paint);
			gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height - 2);
			paint = new GradientPaint(0, r.y + r.height / 2 - 5, ScrollbarStyle.button1, 0, r.y + r.height,
					ScrollbarStyle.button2);
			gfx.setPaint(paint);
			gfx.fillRect(r.x + 1, r.y + r.height / 2, r.width - 1, r.height / 2);
		}

		// frame
		if (!scrollBar.isArmed()) {
			gfx.setColor(new Color(170, 180, 200));
		} else {
			gfx.setColor(new Color(180, 180, 200));
		}
		gfx.drawRoundRect(r.x + 1, r.y, r.width - 2, r.height, 4, 4);

		if (scrollBar.isArmed()) {
			drawZoomBar(gfx, r);
		}
	}

	private void drawZoomBar(Graphics2D gfx, Rectangle r) {

		if (scrollBar.isZoomMinusArmed()) {
			gfx.setColor(new Color(255, 150, 50));
			gfx.drawLine(r.x + 1, r.y + 3, r.x + 1, r.y + ScrollbarStyle.size - 3);
			gfx.setColor(new Color(255, 150, 50));
			gfx.drawLine(r.x + 2, r.y + 2, r.x + 2, r.y + ScrollbarStyle.size - 2);
			gfx.setColor(new Color(255, 210, 50));
			gfx.drawLine(r.x + 3, r.y + 1, r.x + 3, r.y + ScrollbarStyle.size - 1);
		} else {
			gfx.setColor(new Color(255, 150, 100));
			gfx.drawLine(r.x + 1, r.y + 5, r.x + 1, r.y + ScrollbarStyle.size - 5);

			gfx.drawLine(r.x + r.width - 1, r.y + 5, r.x + r.width - 1,
					r.y + ScrollbarStyle.size - 5);
		}




	}


}
