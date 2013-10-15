package com.visiors.visualstage.tool.impl;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.listener.GraphDocumentAdapter;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.tool.Interactable;

public class ScrollBar implements Interactable {

	private static final int size = 16;
	private int min;
	private int max;
	private int value;
	private final boolean horizontal;
	private int initialValue;
	private Point mousePressedAt;
	private GraphDocument graphDocument;
	private final Rectangle rectPlusButton = new Rectangle();
	private final Rectangle rectMinusButton = new Rectangle();
	private final Rectangle rectScrollBar = new Rectangle();
	private final Rectangle rectThumb = new Rectangle();
	private boolean armed;
	private Point mouseCurrentlyAt;
	private Rectangle graphBoundary;
	private Rectangle viewBoundary;
	private boolean interacting;
	private int thumbPos;
	private int thumbExpansion;
	private int hitPage = -1;

	public ScrollBar(boolean horizontal) {

		this.horizontal = horizontal;
	}

	public void setGraphDocument(final GraphDocument graphDocument) {

		this.graphDocument = graphDocument;
		this.value = horizontal ? graphDocument.getViewportPos().x : graphDocument.getViewportPos().y;
		this.hitPage = -1;
		graphDocument.addGraphDocumentListener(new GraphDocumentAdapter() {

			@Override
			public void graphExpansionChanged() {

				graphBoundary = graphDocument.getGraph().getExtendedBoundary();
				update();
			}
		});
	}

	public void setMin(int min) {

		this.min = min;
	}

	public void setMax(int max) {

		this.max = max;
	}

	public void setValue(int value) {

		this.value = value;
		final Point vp = graphDocument.getViewportPos();
		graphDocument.setViewportPos(horizontal ? -value : vp.x, !horizontal ? -value : vp.y);
	}

	public int getValue() {

		return value;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (armed) {
			if (hitTumb(pt)) {
				interacting = true;
				mousePressedAt = pt;
				initialValue = value;
			} else {
				goToPage(hitPage);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {
		return armed;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return mousePressed(pt, button, functionKey);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (interacting) {
			mouseCurrentlyAt = pt;
			updateViewport();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		Point vp = graphDocument.getViewportPos();
		pt.translate(vp.x, vp.y);
		boolean hit = rectScrollBar.contains(pt);

		int page = hitPage;
		hitPage = -1;
		if (hit) {
			hitPage = getHitPage(pt);
		}
		if (hit != armed || page != hitPage) {
			armed = hit;
			if(!hit) {
				interacting = false;
			}
			update();
		}
		return armed;
	}

	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		if (armed) {
			armed = false;
			hitPage = -1;
			update();
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		return false;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		return false;
	}

	@Override
	public boolean isInteracting() {

		return interacting;
	}

	@Override
	public void cancelInteraction() {

	}

	@Override
	public void terminateInteraction() {

	}

	@Override
	public int getPreferredCursor() {

		return Interactable.CURSOR_DEFAULT;
	}

	private int getCornerSquareSize() {

		return size;
	}

	private void update() {

		graphDocument.invalidate();
	}

	private void updateViewport() {

		int newViewportPos;
		if (horizontal) {
			int dx = (mouseCurrentlyAt.x - getValue()) - (mousePressedAt.x - initialValue);
			newViewportPos = initialValue + (int) convertViewToGraphPos(dx);
		} else {
			int dy = (mouseCurrentlyAt.y - getValue()) - (mousePressedAt.y - initialValue);
			newViewportPos = initialValue + (int) convertViewToGraphPos(dy);
		}
		setValue(normalizeViewportValue(newViewportPos));
	}

	private int normalizeViewportValue(int value) {

		if (horizontal) {
			return Math.max(Math.min(value, max - (viewBoundary.x + viewBoundary.width)), min);
		} else {
			return Math.max(Math.min(value, max - (viewBoundary.y + viewBoundary.height)), min);
		}
	}

	private void computeHScrollBarGeometry() {

		this.viewBoundary = graphDocument.getViewBoundary();
		computeMinMaxValues();
		computeThumbPos();
		computeThumbSize();
		computeScrollBarRect();
		computeThumbRect();
		computeMinusButtonRect();
		computePlusButtonRect();
	}

	private void computeMinMaxValues() {

		if (horizontal) {

			this.min = graphBoundary.x - getCornerSquareSize()/*- viewBoundary.width*/;
			this.max = graphBoundary.x + graphBoundary.width + getCornerSquareSize()/*
			 * +
			 * viewBoundary
			 * .
			 * width
			 */;

		} else {
			this.min = graphBoundary.y - getCornerSquareSize();
			this.max = graphBoundary.y + graphBoundary.height + getCornerSquareSize();
		}
	}

	private void computeThumbSize() {

		final int start;
		final int end;
		final double pageNumber = 1.0 / projectionRatio();
		if (pageNumber == 0) {
			thumbExpansion = 0;
			return;
		}
		if (horizontal) {
			start = viewBoundary.x + size;
			end = viewBoundary.x + viewBoundary.width - size - getCornerSquareSize();
		} else {
			start = viewBoundary.y + size;
			end = viewBoundary.y + viewBoundary.height - size - getCornerSquareSize();
		}
		thumbExpansion = Math.min((int) ((end - start) / pageNumber), end - start - thumbPos);
	}

	private double projectionRatio() {

		if (horizontal) {
			return (viewBoundary.width - getCornerSquareSize()) / (double) (max - min);
		} else {
			return (viewBoundary.height - getCornerSquareSize()) / (double) (max - min);
		}
	}

	private double convertGraphToViewPos(int graphPos) {

		return (int) ((graphPos - min) * projectionRatio());
	}

	private double convertViewToGraphPos(int viewPos) {

		return (int) (viewPos / projectionRatio());
	}


	private void computeThumbPos() {

		thumbPos = (int) convertGraphToViewPos(getValue());
		thumbPos = Math.max(thumbPos, 0);
	}

	private void computeThumbRect() {

		if (horizontal) {
			rectThumb.setBounds(viewBoundary.x + size + thumbPos, viewBoundary.y + viewBoundary.height - size,
					thumbExpansion, size);

		} else {
			rectThumb.setBounds(viewBoundary.x + viewBoundary.width - size, viewBoundary.y + size, size, thumbExpansion);
		}

	}

	private void computeScrollBarRect() {

		if (horizontal) {
			rectScrollBar.setBounds(viewBoundary.x, viewBoundary.y + viewBoundary.height - size, viewBoundary.width
					- getCornerSquareSize(), size);

		} else {
			rectScrollBar.setBounds(viewBoundary.x + viewBoundary.width - size, viewBoundary.y, size,
					viewBoundary.height - getCornerSquareSize());
		}
	}

	private void computeMinusButtonRect() {

		if (horizontal) {
			rectMinusButton.setBounds(viewBoundary.x, viewBoundary.y + viewBoundary.height - size, size, size);
		} else {
			rectMinusButton.setBounds(viewBoundary.x + viewBoundary.width - size, viewBoundary.y, size, size);
		}
	}

	private void computePlusButtonRect() {

		if (horizontal) {
			rectPlusButton.setBounds(viewBoundary.x + viewBoundary.width - size - getCornerSquareSize(), viewBoundary.y
					+ viewBoundary.height - size, size, size);
		} else {
			rectPlusButton.setBounds(viewBoundary.x + viewBoundary.width - size, viewBoundary.y + viewBoundary.height
					- size - getCornerSquareSize(), size, size);
		}
	}

	public void draw(AWTCanvas awtCanvas) {

		if (graphDocument != null) {
			computeHScrollBarGeometry();
			final Rectangle viewport = graphDocument.getViewBoundary();
			drawHScrollBar(awtCanvas.gfx, viewport);
		}

	}

	private void drawHScrollBar(Graphics2D gfx, Rectangle viewport) {

		drawBackground(gfx);
		drawPages(gfx);
		drawThumb(gfx);
		drawButtonMinus(gfx);
		drawButtonPlus(gfx);
	}

	private void drawBackground(Graphics2D gfx) {

		if (horizontal) {
			Paint p = new GradientPaint(0, rectScrollBar.y, new Color(0xE1E6F6), 0, rectScrollBar.y
					+ rectScrollBar.height, new Color(0xFFFFFF));
			gfx.setPaint(p);
		} else {
			Paint p = new GradientPaint(rectScrollBar.x, 0, new Color(0xE1E6F6), rectScrollBar.x + rectScrollBar.width,
					0, new Color(0xFFFFFF));
			gfx.setPaint(p);
		}
		// background
		gfx.fillRect(rectScrollBar.x, rectScrollBar.y, rectScrollBar.width, rectScrollBar.height);
		gfx.setColor(new Color(0xC0C0C4));
		gfx.drawLine(rectScrollBar.x, rectScrollBar.y, rectScrollBar.x, rectScrollBar.y + rectScrollBar.height);
		gfx.drawLine(rectScrollBar.x, rectScrollBar.y, rectScrollBar.x + rectScrollBar.width, rectScrollBar.y);
	}

	private void drawPages(Graphics2D gfx) {

		double pageNumber = 1.0 / projectionRatio();
		if (pageNumber == 0) {
			return;
		}
		final double localPageWidth = getScrollbarPageWidth();
		if (horizontal) {
			final int fontHeight = 6;
			final int start = viewBoundary.x + size;
			final int end = viewBoundary.x + viewBoundary.width - size - getCornerSquareSize();
			for (int page = 0, x; page < pageNumber + 1; page++) {
				x = (int) (localPageWidth * page) + start;
				if (localPageWidth > 5) {
					if (!interacting && page == hitPage) {
						gfx.setColor(new Color(230, 230, 255));
						gfx.fillRect(x, rectScrollBar.y + 2,
								(int) Math.min(localPageWidth, viewBoundary.width - size - x), rectScrollBar.height - 2);
						gfx.setColor(new Color(0xC0C0C4));
					}
					if (page > 0) {
						gfx.drawLine(x, rectScrollBar.y, x, rectScrollBar.y + rectScrollBar.height);
					}
				}
				if (x + 0.5 * localPageWidth < end && localPageWidth > 12) {
					gfx.drawString(Integer.toString(page + 1), (int) (x + localPageWidth / 2), rectScrollBar.y
							+ rectScrollBar.height - fontHeight / 2);
				}
			}
		} else {
			final int fontWidth = 6;
			final int start = viewBoundary.y + size;
			final int end = viewBoundary.height - size - getCornerSquareSize();
			final double localPageHeight = (end - start) / pageNumber;
			for (int page = 0, y; page < pageNumber; page++) {
				y = (int) (localPageHeight * page) + start;
				if (localPageHeight > 5) {
					if (!interacting && page == hitPage) {
						gfx.setColor(new Color(230, 230, 255));
						gfx.fillRect(rectScrollBar.x + 2, y, rectScrollBar.width - 2,
								(int) Math.min(localPageHeight, viewBoundary.height - size - y));
						gfx.setColor(new Color(0xC0C0C4));
					}
					if (page > 0) {
						gfx.drawLine(rectScrollBar.x, y, rectScrollBar.x + rectScrollBar.width, y);
					}
				}
				if (y + 0.5 * localPageHeight < end && localPageHeight > 12) {
					gfx.drawString(Integer.toString(page + 1), rectScrollBar.x + rectScrollBar.width / 2 - fontWidth
							/ 2, (int) (y + localPageHeight / 2));
				}
			}
		}
	}

	private int getHitPage(Point pt) {

		final double pageNumber = 1.0 / projectionRatio();
		if (pageNumber == 0) {
			return -1;
		}
		final double localPageWidth = getScrollbarPageWidth();
		if (horizontal) {
			final int start = viewBoundary.x + size;
			return (int) Math.floor((pt.x - start) / localPageWidth);
		} else {
			final int start = viewBoundary.y + size;
			return (int) Math.floor((pt.x - start) / localPageWidth);
		}
	}

	private boolean hitTumb(Point pt) {

		final Point vp = graphDocument.getViewportPos();
		return rectThumb.contains(pt.x + vp.x, pt.y + vp.y);
	}

	private void goToPage(int page) {

		if (page < 0 ) {
			return;
		}
		final double localPageWidth = getScrollbarPageWidth();
		if (horizontal) {
			double pointOnScrollPageArea = page * localPageWidth;
			double v = -viewBoundary.x + pointOnScrollPageArea
					* ((double) (max - min) / (viewBoundary.width - getCornerSquareSize()));
			setValue((int) v);
		} else {
			double pointOnScrollPageArea = page * localPageWidth;
			double v = -viewBoundary.y + pointOnScrollPageArea
					* ((double) (max - min) / (viewBoundary.height - getCornerSquareSize()));
			setValue((int) v);
		}
	}

	private double getScrollbarPageWidth() {

		final double pageNumber = 1.0 / projectionRatio();
		final int start;
		final int end;
		if (horizontal) {
			start = viewBoundary.x + size;
			end = viewBoundary.x + viewBoundary.width - size - getCornerSquareSize();
		} else {
			start = viewBoundary.y + size;
			end = viewBoundary.y + viewBoundary.height - size - getCornerSquareSize();
		}
		return (end - start) / pageNumber;

	}

	private void drawThumb(Graphics2D gfx) {

		if (horizontal) {
			Paint p = new GradientPaint(0, rectThumb.y, new Color(0xF9F9F9), 0, rectThumb.y + rectThumb.height,
					new Color(0xB8B8B8));
			gfx.setPaint(p);

		} else {
			Paint p = new GradientPaint(rectThumb.x, 0, new Color(0xFFFFFF), rectThumb.x + rectThumb.width, 0,
					new Color(0xCACACD));
			gfx.setPaint(p);
		}
		// thumb
		gfx.fillRoundRect(rectThumb.x + 2, rectThumb.y + 2, rectThumb.width - 4, rectThumb.height - 4, 11, 11);
		gfx.setColor(new Color(0x9098AC));
		gfx.drawRoundRect(rectThumb.x + 2, rectThumb.y + 2, rectThumb.width - 4, rectThumb.height - 4, 11, 11);
	}

	private void drawButtonMinus(Graphics2D gfx) {

		drawButton(gfx, rectMinusButton);
		if (armed) {
			gfx.setColor(new Color(0x0));
		}
		if (horizontal) {
			drawArrowToWest(gfx, rectMinusButton.x + size / 3, rectMinusButton.y + size / 3, size / 2, size / 2);
		} else {
			drawArrowToNorth(gfx, rectMinusButton.x + size / 3, rectMinusButton.y + size / 3, size / 2, size / 2);
		}
	}

	private void drawButtonPlus(Graphics2D gfx) {

		drawButton(gfx, rectPlusButton);
		if (armed) {
			gfx.setColor(new Color(0x0));
		}

		if (horizontal) {
			drawArrowToEast(gfx, rectPlusButton.x + size / 3, rectPlusButton.y + size / 3, size / 2, size / 2);
		} else {
			drawArrowToSouth(gfx, rectPlusButton.x + size / 3, rectPlusButton.y + size / 3, size / 2, size / 2);
		}
	}

	private void drawArrowToEast(Graphics2D gfx, int x, int y, int width, int height) {

		gfx.fillPolygon(new Polygon(new int[] { x, x, x + width }, new int[] { y, y + height, y + height / 2 }, 3));

	}

	private void drawArrowToWest(Graphics2D gfx, int x, int y, int width, int height) {

		gfx.fillPolygon(new Polygon(new int[] { x, x + width, x + width }, new int[] { y + height / 2, y, y + height },
				3));

	}

	private void drawArrowToNorth(Graphics2D gfx, int x, int y, int width, int height) {

		gfx.fillPolygon(new Polygon(new int[] { x + width / 2, x, x + width }, new int[] { y, y + height, y + height },
				3));

	}

	private void drawArrowToSouth(Graphics2D gfx, int x, int y, int width, int height) {

		gfx.fillPolygon(new Polygon(new int[] { x, x + width, x + width / 2 }, new int[] { y, y, y + height }, 3));

	}

	private void drawButton(Graphics2D gfx, Rectangle r) {

		if (!armed) {
			return;
		}

		if (horizontal) {
			Paint p = new GradientPaint(0, r.y, new Color(0xF9F9F9), 0, r.y + r.height, new Color(0xB8B8B8));
			gfx.setPaint(p);

		} else {
			Paint p = new GradientPaint(r.x, 0, new Color(0xFFFFFF), r.x + r.width, 0, new Color(0xCACACD));
			gfx.setPaint(p);
		}

		// buttons
		// gfx.fillRoundRect(r.x , r.y , r.width, r.height, 12, 12);
		// buttons shadow
		// gfx.setColor(new Color(0x9098AC));
		// gfx.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);
	}
}
