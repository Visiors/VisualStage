package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

import com.visiors.visualstage.tool.impl.ScrollBar;

public class ScrollBarBackgroundPainter implements DrawClient {

	private final ScrollBar scrollBar;


	public ScrollBarBackgroundPainter(ScrollBar scrollBar) {

		super();
		this.scrollBar = scrollBar;
	}


	@Override
	public Rectangle getBounds() {

		return scrollBar.getRectScrollBar();
	}

	@Override
	public void draw(Graphics2D gfx) {

		final Rectangle r = scrollBar.getRectScrollBar();
		final Paint p;
		if (scrollBar.isHorizontal()) {
			p = new GradientPaint(0, r.y, ScrollbarStyle.background1, 0, r.y + r.height,
					ScrollbarStyle.background2);
		} else {
			p = new GradientPaint(r.x, 0, ScrollbarStyle.background1, r.x + r.width, 0,
					ScrollbarStyle.background2);
		}
		gfx.setPaint(p);
		gfx.fillRect(r.x, r.y, r.width, r.height);
		gfx.setColor(ScrollbarStyle.frame);
		gfx.drawLine(r.x, r.y, r.x, r.y + r.height);
		gfx.drawLine(r.x, r.y, r.x + r.width, r.y);
	}
}