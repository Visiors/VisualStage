package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.visiors.visualstage.tool.impl.ScrollBar;

public class ScrollBarPagePainter implements DrawClient {

	private final ScrollBar scrollBar;

	public ScrollBarPagePainter(ScrollBar scrollBar) {

		super();
		this.scrollBar = scrollBar;
	}


	@Override
	public Rectangle getBounds() {

		return 
	}

	@Override
	public void draw(Graphics2D gfx) {

		Rectangle r = getBounds();

		if (!horizontal) {
			gfx.setColor(new Color(0xF9B104));
			gfx.fillRoundRect(rectScrollBar.x + 1, rectScrollBar.y + rectScrollBar.height + 1, cornerSquareSize() - 2,
					cornerSquareSize() - 2, 0, 0);
			gfx.setColor(ScrollbarStyle.background1);
			gfx.drawRoundRect(rectScrollBar.x + 3, rectScrollBar.y + rectScrollBar.height + 3, cornerSquareSize() - 7,
					cornerSquareSize() - 7, 4, 4);
		}
	}

}
