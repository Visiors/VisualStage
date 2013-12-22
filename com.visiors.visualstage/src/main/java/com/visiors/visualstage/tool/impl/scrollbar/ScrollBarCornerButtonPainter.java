package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.DrawClient;

public class ScrollBarCornerButtonPainter implements DrawClient {

	private final ScrollBarCornerButton scrollBarButton;

	public ScrollBarCornerButtonPainter(ScrollBarCornerButton scrollBarCornerButton) {

		super();
		this.scrollBarButton = scrollBarCornerButton;
	}

	@Override
	public Rectangle getBounds() {

		return scrollBarButton.getBounds();
	}

	@Override
	public void draw(Graphics2D gfx) {

		final Rectangle r = getBounds();
		final Paint p;
		if (scrollBarButton.isToggled()) {
			p = new GradientPaint(r.x, r.y, ScrollbarStyle.cornerButtonToggledColor, r.x + r.width, r.y + r.height,
					ScrollbarStyle.cornerButtonArmedColor2);

		} else {
			if (scrollBarButton.isHovered()) {
				p = new GradientPaint(r.x, r.y, ScrollbarStyle.cornerButtonArmedColor1, r.x + r.width, r.y + r.height,
						ScrollbarStyle.cornerButtonArmedColor2);
			} else {
				p = new GradientPaint(r.x, r.y, ScrollbarStyle.cornerButtonColor1, r.x + r.width, r.y + r.height,
						ScrollbarStyle.cornerButtonColor2);
			}
		}
		gfx.setPaint(p);
		gfx.fillRect(r.x, r.y, r.width, r.height);
		gfx.setColor(ScrollbarStyle.cornerButtonFrameColor);
		gfx.drawRect(r.x, r.y, r.width, r.height);
		if (scrollBarButton.isHovered()) {
			gfx.setColor(ScrollbarStyle.cornerButtonFrameHoeveredColor);
			// gfx.drawRect(r.x +r.width/3, r.y +r.height/3, r.width /2,
			// r.height/2);
		}

		gfx.setColor(new Color(130, 150, 170));
		gfx.fillRoundRect(r.x + 5, r.y + 5, r.width - 9, r.height - 9, 0, 0);
	}
}
