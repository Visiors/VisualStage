package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.visiors.visualstage.tool.impl.ScrollBar;

public class ScrollBarCornerSquarePainter implements DrawClient {

	private final ScrollBar scrollBar;

	public ScrollBarCornerSquarePainter(ScrollBar scrollBar) {

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



		final double pageNumber = documentViewportRatio();
		if (pageNumber == 0) {
			return;
		}
		final double localPageWidth = getPageWidth();

		gfx.setFont(ScrollbarStyle.pageFont);
		if (scrollBar.isHorizontal()) {
			final int fontHeight = 6;
			final int start = canvasBoundary.x + ScrollbarStyle.size;
			final int end = canvasBoundary.x + canvasBoundary.width - ScrollbarStyle.size - cornerSquareSize();
			for (int page = 0, x; page < pageNumber + 1; page++) {
				x = (int) (localPageWidth * page) + start;
				if (localPageWidth > 5) {
					if (!interacting && page == hitPage) {
						gfx.setColor(ScrollbarStyle.pageHeightlighted);
						gfx.fillRect(x, rectScrollBar.y + 2,
								(int) Math.min(localPageWidth, canvasBoundary.width - ScrollbarStyle.size - x),
								rectScrollBar.height - 2);
					}
					if (page > 0) {
						gfx.setColor(ScrollbarStyle.pageFrame);
						gfx.drawLine(x, rectScrollBar.y + 1, x, rectScrollBar.y + rectScrollBar.height - 2);
					}
				}
				if (x + 0.5 * localPageWidth < end && localPageWidth > 12) {
					gfx.setColor(ScrollbarStyle.pageFontColor);
					gfx.drawString(Integer.toString(page + 1), (int) (x + localPageWidth / 2), rectScrollBar.y
							+ rectScrollBar.height - fontHeight / 2);
				}
			}

			gfx.setColor(Color.WHITE);
		} else {
			final int fontWidth = 6;
			final int start = canvasBoundary.y + ScrollbarStyle.size;
			final int end = canvasBoundary.height - ScrollbarStyle.size - cornerSquareSize();
			final double localPageHeight = (end - start) / pageNumber;
			for (int page = 0, y; page < pageNumber; page++) {
				y = (int) (localPageHeight * page) + start;
				if (localPageHeight > 5) {
					if (!interacting && page == hitPage) {
						gfx.setColor(ScrollbarStyle.pageHeightlighted);
						gfx.fillRect(rectScrollBar.x + 2, y, rectScrollBar.width - 2,
								(int) Math.min(localPageHeight, canvasBoundary.height - ScrollbarStyle.size - y));
					}
					if (page > 0) {
						gfx.setColor(ScrollbarStyle.pageFrame);
						gfx.drawLine(rectScrollBar.x, y, rectScrollBar.x + rectScrollBar.width, y);
					}
				}
				if (y + 0.5 * localPageHeight < end && localPageHeight > 12) {
					gfx.setColor(ScrollbarStyle.pageFontColor);
					gfx.drawString(Integer.toString(page + 1), rectScrollBar.x + rectScrollBar.width / 2 - fontWidth
							/ 2, (int) (y + localPageHeight / 2));
				}
			}
		}
	}

}
