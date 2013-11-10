package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.DrawClient;
import com.visiors.visualstage.tool.impl.ScrollBar;

public class ScrollBarCornerSquarePainter implements DrawClient {

	private final ScrollBar scrollBar;

	public ScrollBarCornerSquarePainter(ScrollBar scrollBar) {

		super();
		this.scrollBar = scrollBar;
	}

	@Override
	public Rectangle getBounds() {

		return scrollBar.getRectCornderSquare();
	}

	@Override
	public void draw(Graphics2D gfx) {

		final Rectangle r = getBounds();
		final Paint p;
		if (scrollBar.isCornderButtonHovered()) {
			p = new GradientPaint(r.x , r.y, ScrollbarStyle.cornderSquareArmedColor1, r.x + r.width, r.y + r.height,
					ScrollbarStyle.cornderSquareArmedColor2);
		}else{
			p = new GradientPaint(r.x, r.y, ScrollbarStyle.cornderSquareColor1, r.x + r.width, r.y + r.height,
					ScrollbarStyle.cornderSquareColor2);
		}
		gfx.setPaint(p);
		gfx.fillRect(r.x , r.y , r.width , r.height);
		gfx.setColor(ScrollbarStyle.cornderSquareFrameColor);
		gfx.drawRect(r.x , r.y , r.width , r.height);
		if (scrollBar.isCornderButtonHovered()) {
			gfx.setColor(ScrollbarStyle.cornderSquareFrameHoeveredColor);
			//			gfx.drawRect(r.x +r.width/3, r.y +r.height/3, r.width /2, r.height/2);
		}
	}
}
