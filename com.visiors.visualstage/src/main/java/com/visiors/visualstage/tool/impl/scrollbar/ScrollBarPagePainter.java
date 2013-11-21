package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.DrawClient;

public class ScrollBarPagePainter implements DrawClient {

	private final ScrollBar scrollBar;
	private final double minDisplayablePageWidth = 5;

	public ScrollBarPagePainter(ScrollBar scrollBar) {

		super();
		this.scrollBar = scrollBar;
	}

	@Override
	public Rectangle getBounds() {

		return scrollBar.getRectScrollBar();
	}

	@Override
	public void draw(Graphics2D gfx) {

		final double totalPageNumber = scrollBar.getTotalePageNumber();
		for (int pageNumber = 0; pageNumber < totalPageNumber + 1; pageNumber++) {
			drawPage(gfx, pageNumber);
		}
	}

	private void drawPage(Graphics2D gfx, int pageNumber) {

		final Rectangle rPage = getPageBoundary(pageNumber);
		if (pageDisplayable(pageNumber)) {
			drawPageBackground(gfx, pageNumber, rPage);
			drawPageDivider(gfx, pageNumber, rPage);
			drawPageNumber(gfx, pageNumber, rPage);
		}
	}

	private void drawPageNumber(Graphics2D gfx, int pageNumber, Rectangle rPage) {

		final int fontSize = ScrollbarStyle.pageFont.getSize();
		final boolean pageHightlighted = isHightlighted(pageNumber);
		gfx.setFont(ScrollbarStyle.pageFont);
		gfx.setColor(pageHightlighted ? ScrollbarStyle.pageActiveFontColor : ScrollbarStyle.pageInactiveFontColor);

		final String text = Integer.toString(pageNumber + 1);
		if (scrollBar.isHorizontal()) {
			if (rPage.width > fontSize * 2) {
				gfx.drawString(text, rPage.x + rPage.width / 2, rPage.y + rPage.height - fontSize / 2);
			}
		} else {
			if (rPage.height > fontSize * 2) {
				gfx.drawString(text, rPage.x + rPage.width / 2 - fontSize / 2, rPage.y + rPage.height / 2);
			}
		}
	}

	private void drawPageDivider(Graphics2D gfx, int pageNumber, Rectangle rPage) {

		if (pageNumber > 0) {
			gfx.setColor(ScrollbarStyle.pageFrameColor);
			if (scrollBar.isHorizontal()) {
				gfx.drawLine(rPage.x, rPage.y + 1, rPage.x, rPage.y + rPage.height - 2);
			} else {
				gfx.drawLine(rPage.x + 1, rPage.y, rPage.x + rPage.width - 2, rPage.y);
			}
		}
	}

	private void drawPageBackground(Graphics2D gfx, int pageNumber, Rectangle rPage) {

		if (isHightlighted(pageNumber)) {
			gfx.setColor(ScrollbarStyle.pageHeightlightedColor);
			if (scrollBar.isHorizontal()) {
				gfx.fillRect(rPage.x, rPage.y + 2, rPage.width - 1, rPage.height - 3);
			} else {
				gfx.fillRect(rPage.x + 2, rPage.y, rPage.width - 3, rPage.height - 1);
			}
		}
	}

	private Rectangle getPageBoundary(int pageNumber) {

		final Rectangle r = getBounds();
		final double pageWidth = scrollBar.getPageWidth();
		final Rectangle rectPageArea = scrollBar.getRectPageArea();
		if (scrollBar.isHorizontal()) {
			final int xPageStart = (int) ((pageWidth * pageNumber) + rectPageArea.x);
			return new Rectangle(xPageStart, r.y, Math.min((int) pageWidth, r.x + r.width), r.height);
		} else {
			final int yPageStart = (int) ((pageWidth * pageNumber) + rectPageArea.y);
			return new Rectangle(r.x, yPageStart, r.width, Math.min((int) pageWidth, r.y + r.height));
		}
	}

	private boolean pageDisplayable(int pageNumber) {

		final double pageWidth = scrollBar.getPageWidth();
		return (pageWidth > minDisplayablePageWidth);
	}

	private boolean isHightlighted(int pageNumber) {

		return pageNumber == scrollBar.getHighlightedPage();
	}

}
