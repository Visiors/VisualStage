package com.visiors.visualstage.tool.impl;

import java.awt.Color;
import java.awt.Font;
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
import com.visiors.visualstage.transform.Transform;

public class ScrollBar implements Interactable {

	private static Color background1 = new Color(0xE1E6F6);
	private static Color background2 = new Color(0xFFFFFF);
	private static Color frame = new Color(0xC0C0C4);
	private static Color pageFrame = new Color(0xffffff);
	private static Color pageFontColor = new Color(0xaaaaaa);
	private static Font pageFont = new Font ("Verdana", Font.PLAIN , 9);
	private static Color pageHeightlighted = new Color(200, 200, 225);

	private static Color thumbColor1 = new Color(0xCACACD);
	private static Color thumbColor2 = new Color(0xFFFFFF);
	private static Color thumbFrame = new Color(0x9098AC);

	private static Color arrow = new Color(0x9098AC);
	private static Color arrowArmed = new Color(0x565F7D);

	private static Color button1 = new Color(0x9098AC);
	private static Color button2 = new Color(0xFFFFFF);

	private static final int size = 16;
	private static final int ZOOM_BAR_REGION = 7;
	private int min;
	private int max;
	private final int unitIncrement = 20;
	private int value;
	private final boolean horizontal;
	private int initialValue;
	private int initialThumbExpansion;
	private double initialZoom;
	private int initialGraphStartXPos;
	private int initialGraphEndXPos;
	private int initialGraphStartYPos;
	private int initialGraphEndYPos;
	private Point mousePressedAt;
	private GraphDocument graphDocument;
	private final Rectangle rectPlusButton = new Rectangle();
	private final Rectangle rectMinusButton = new Rectangle();
	private final Rectangle rectScrollBar = new Rectangle();
	private final Rectangle rectThumb = new Rectangle();
	private boolean armed;
	private Point mouseCurrentlyAt;
	private Rectangle graphBoundary;
	private Rectangle canvasBoundary;
	private boolean interacting;
	private int thumbPos;
	private int thumbExpansion;
	private int hitPage = -1;
	private boolean zoomMinusArmed;
	private boolean zoomPlusArmed;
	private int initialThumbPos;


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

		this.min = Math.min(min, 0);
	}

	public void setMax(int max) {

		this.max = Math.max(max, canvasBoundary.width);
	}

	public void setValue(int value) {

		this.value = Math.max(min,  Math.min(value, max));
		final Point vp = graphDocument.getViewportPos();
		graphDocument.setViewportPos(horizontal ? -value : vp.x, !horizontal ? -value : vp.y);
		graphBoundary = graphDocument.getGraph().getExtendedBoundary();
	}

	public int getValue() {

		return value;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (armed) {
			Transform xform = graphDocument.getTransformer();
			final Point ptScreen = xform.transformToScreen(pt);
			if (hitMinusButton(ptScreen)) {
				onButtonClick(true);
			} else if (hitPlusButton(ptScreen)) {
				onButtonClick(false);
			} else if (zoomMinusArmed || zoomPlusArmed || hitTumb(ptScreen)) {
				interacting = true;
				mousePressedAt = ptScreen;
				initialValue = value;
				initialThumbPos = thumbPos;
				initialThumbExpansion = thumbExpansion;
				initialZoom = graphDocument.getTransformer().getScale();
				initialGraphStartXPos =  xform.transformToGraphX(0);
				initialGraphEndXPos = xform.transformToGraphX(canvasBoundary.x + canvasBoundary.width  );
				initialGraphStartYPos = xform.transformToGraphY(0);
				initialGraphEndYPos = xform.transformToGraphY(canvasBoundary.y + canvasBoundary.height);
			} else {
				goToPage(hitPage);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (interacting) {
			interacting = false;
			update();
		}
		return armed;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return mousePressed(pt, button, functionKey);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		final Point ptScreen = graphDocument.getTransformer().transformToScreen(pt);
		zoomMinusArmed = false;
		zoomPlusArmed = false;
		hitPage = -1;
		boolean hit = hitScrollbar(ptScreen);
		boolean modified = hit != armed;
		armed = hit;
		if (hit) {
			final boolean hitPageArea = hitPageArea(ptScreen);
			if (hitPageArea) {
				if (hitZoomMinus(ptScreen)) {
					zoomMinusArmed = true;
				} else if (hitZoomPlus(ptScreen)) {
					zoomPlusArmed = true;
				} else if (!hitTumb(ptScreen)) {
					hitPage = getHitPage(ptScreen);
				}
				modified = true;
			}
		}
		if(modified) {
			update();
		}
		return armed;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (interacting) {
			final Point ptScreen = graphDocument.getTransformer().transformToScreen(pt);
			mouseCurrentlyAt = ptScreen;
			if (zoomMinusArmed) {
				onMinusZoomChanged();
			} else if (zoomPlusArmed) {
				onPlusZoomChanged();
			} else {
				onThumbMoved();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		if (armed && !interacting) {
			armed = false;
			zoomMinusArmed = false;
			zoomPlusArmed = false;
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

		if (zoomMinusArmed || zoomPlusArmed) {
			return (horizontal ? Interactable.CURSOR_W_RESIZE : Interactable.CURSOR_N_RESIZE);
		}
		return Interactable.CURSOR_DEFAULT;
	}

	private int cornerSquareSize() {

		return size;
	}

	private void update() {

		graphDocument.invalidate();
	}

	private void onMinusZoomChanged() {

		double oldZoomValue = graphDocument.getZoom();


		int dx = mouseCurrentlyAt.x - mousePressedAt.x;
		double newZoomValue = initialZoom * initialThumbExpansion  / (initialThumbExpansion - dx);
		newZoomValue = Math.max(Math.min(newZoomValue, 10), 0.1);
		graphDocument.setZoom(newZoomValue);

		//		onThumbMoved();

		final Transform xform = graphDocument.getTransformer();
		final int gxCurrent = xform.transformToGraphX(canvasBoundary.width);
		final int gyCurrent = xform.transformToGraphY(canvasBoundary.height / 2);
		//		xform.setYTranslate(xform.getYTranslate() - initialGraphStartYPos  -initialGraphEndYPos/2 + gyCurrent);
		//		setValue(getValue() + initialGraphEndXPos  - gxCurrent);
		//		System.err.println(getValue() -initialGraphEndXPos  * (oldZoomValue  - newZoomValue));
	}

	private void onPlusZoomChanged() {

		int dx = mouseCurrentlyAt.x - mousePressedAt.x;		
		mouseCurrentlyAt.x = Math.max(initialThumbPos + 520, mouseCurrentlyAt.x);
		double newZoomValue = initialZoom * initialThumbExpansion  / (initialThumbExpansion + dx);
		newZoomValue = Math.max(Math.min(newZoomValue, 10), 0.1);
		graphDocument.setZoom(newZoomValue);

		//		
		final Transform xform = graphDocument.getTransformer();
		final int gxCurrent = xform.transformToGraphX(0);
		final int gyCurrent = xform.transformToGraphY(canvasBoundary.height / 2);
		//		xform.setYTranslate(xform.getYTranslate() - initialGraphStartYPos  -initialGraphEndYPos/2 + gyCurrent);
		//moveThumb( -gxCurrent + initialGraphEndXPos);

		dx = graphDocument.getGraph().getExtendedBoundary().x - initialGraphStartXPos;


		System.err.println(dx);
		//		xform.setXTranslate(initialValue+ dx);

	}

	private void onThumbMoved() {

		int offset;
		if (horizontal) {
			offset = mouseCurrentlyAt.x - mousePressedAt.x;
		} else {
			offset = mouseCurrentlyAt.y - mousePressedAt.y;
		}
		moveThumb(offset);
	}

	private void moveThumb(int offset){

		offset = Math.max(offset, -initialThumbPos);
		offset = Math.min(offset, scrollbarPageExpansion() - (initialThumbPos + initialThumbExpansion));
		int newViewportPos = (int) (initialValue + offset * documentScrollableRatio());
		setValue(newViewportPos);
	}

	private void onButtonClick(boolean minus) {

		int old = getValue();
		int newValue = getValue() + (minus ? -unitIncrement : unitIncrement);
		setValue(newValue);

		// System.err.println("view port chagned: " + old + " -> " + getValue()
		// + ", min: "+ min + ", max: " + max);
	}

	private void computeHScrollBarGeometry() {

		this.canvasBoundary = graphDocument.getCanvasBoundary();
		computeScrollBarRect();
		computeMinusButtonRect();
		computePlusButtonRect();
		computeMinMaxValues();
		computeThumbExpansion();
		computeThumbPos();
		computeThumbRect();
	}

	private int documentExpansion() {

		return max - min;// Math.min(getValue(), min);
	}

	private int getViewPortExpansion() {

		return horizontal ? canvasBoundary.width : canvasBoundary.height;
	}

	private int scrollbarPageExpansion() {

		if (horizontal) {
			return rectScrollBar.width - rectMinusButton.width - rectPlusButton.width;
		}
		return rectScrollBar.height - rectMinusButton.height - rectPlusButton.height;
	}

	private void computeMinMaxValues() {

		int mergin = canvasBoundary.width ;
		if (horizontal) {
			setMin(graphBoundary.x - mergin);
			setMax(graphBoundary.x + graphBoundary.width + mergin);
		} else {
			setMin(graphBoundary.y - mergin);
			setMax(graphBoundary.y + graphBoundary.height + mergin);
		}
	}

	private double documentViewportRatio() {

		return (double) documentExpansion() / getViewPortExpansion();
	}

	private double documentScrollableRatio() {

		return (double) documentExpansion() / scrollbarPageExpansion();
	}

	private double convertViewToGraphPos(int viewPos) {

		return viewPos / documentScrollableRatio();
	}



	private double documentScrollbarPageAreaRatio() {

		return canvasBoundary.width / documentScrollableRatio();
	}

	private double scrollbarPageAreaToDocument(double pos) {

		return pos * documentScrollableRatio() + min + getValue();
	}

	private double convertDocumentToScrollbarPageArea(double pos) {

		return pos / documentScrollableRatio();
	}

	private void computeThumbExpansion() {

		thumbExpansion = (int) documentScrollbarPageAreaRatio();
		//		System.err.println("min: " + min);
		//		System.err.println("max: " + max);
		//		System.err.println("docWidth: " + graphBoundary.width);
		//		System.err.println("viewWidth: " + canvasBoundary.width);
		//		System.err.println("ratio: " + documentScrollbarPageAreaRatio() );
		//		System.err.println("thumbExpansion: " + thumbExpansion );
		//		System.err.println("value: " + value );
	}

	private void computeThumbPos() {

		thumbPos = (int) convertDocumentToScrollbarPageArea(-min);
		if (horizontal) {
			// thumbPos = Math.min(Math.max(thumbPos, 0),
			// scrollbarPageExpansion() - thumbExpansion);
		} else {

		}
	}

	private void computeThumbRect() {

		if (horizontal) {
			rectThumb.setBounds(canvasBoundary.x + size + thumbPos, canvasBoundary.y + canvasBoundary.height - size,
					thumbExpansion, size);

		} else {
			rectThumb.setBounds(canvasBoundary.x + canvasBoundary.width - size - 1, canvasBoundary.y + size + thumbPos,
					size, thumbExpansion);
		}
	}

	private void computeScrollBarRect() {

		if (horizontal) {
			rectScrollBar.setBounds(canvasBoundary.x, canvasBoundary.y + canvasBoundary.height - size,
					canvasBoundary.width - cornerSquareSize(), size);

		} else {
			rectScrollBar.setBounds(canvasBoundary.x + canvasBoundary.width - size, canvasBoundary.y, size,
					canvasBoundary.height - cornerSquareSize());
		}
	}

	private void computeMinusButtonRect() {

		if (horizontal) {
			rectMinusButton.setBounds(canvasBoundary.x, canvasBoundary.y + canvasBoundary.height - size, size, size);
		} else {
			rectMinusButton.setBounds(canvasBoundary.x + canvasBoundary.width - size, canvasBoundary.y, size, size);
		}
	}

	private void computePlusButtonRect() {

		if (horizontal) {
			rectPlusButton.setBounds(canvasBoundary.x + canvasBoundary.width - size - cornerSquareSize(),
					canvasBoundary.y + canvasBoundary.height - size, size, size);
		} else {
			rectPlusButton.setBounds(canvasBoundary.x + canvasBoundary.width - size, canvasBoundary.y
					+ canvasBoundary.height - size - cornerSquareSize(), size, size);
		}
	}

	public void draw(AWTCanvas awtCanvas) {

		if (graphDocument != null) {
			computeHScrollBarGeometry();
			final Rectangle viewport = graphDocument.getCanvasBoundary();
			drawHScrollBar(awtCanvas.gfx, viewport);
		}

	}

	private void drawHScrollBar(Graphics2D gfx, Rectangle viewport) {

		drawBackground(gfx);
		drawPages(gfx);
		drawThumb(gfx);
		drawButtonMinus(gfx);
		drawButtonPlus(gfx);
		drawCornerSquare(gfx);
	}

	private void drawBackground(Graphics2D gfx) {

		if (horizontal) {
			final Paint p = new GradientPaint(0, rectScrollBar.y, background1, 0, rectScrollBar.y
					+ rectScrollBar.height, background2);
			gfx.setPaint(p);
		} else {
			final Paint p = new GradientPaint(rectScrollBar.x, 0, background1, rectScrollBar.x + rectScrollBar.width,
					0, background2);
			gfx.setPaint(p);
		}
		// background
		gfx.fillRect(rectScrollBar.x, rectScrollBar.y, rectScrollBar.width, rectScrollBar.height);
		gfx.setColor(frame);
		gfx.drawLine(rectScrollBar.x, rectScrollBar.y, rectScrollBar.x, rectScrollBar.y + rectScrollBar.height);
		gfx.drawLine(rectScrollBar.x, rectScrollBar.y, rectScrollBar.x + rectScrollBar.width, rectScrollBar.y);
	}

	private void drawPages(Graphics2D gfx) {

		frame = new Color(210, 210, 230);
		pageFrame = new Color(210, 210, 245);
		pageFontColor = new Color(170, 170, 200);
		pageHeightlighted = new Color(220, 220, 225);

		final double pageNumber = documentViewportRatio();
		if (pageNumber == 0) {
			return;
		}
		final double localPageWidth = getPageWidth();

		gfx.setFont(pageFont);
		if (horizontal) {
			final int fontHeight = 6;
			final int start = canvasBoundary.x + size;
			final int end = canvasBoundary.x + canvasBoundary.width - size - cornerSquareSize();
			for (int page = 0, x; page < pageNumber + 1; page++) {
				x = (int) (localPageWidth * page) + start;
				if (localPageWidth > 5) {
					if (!interacting && page == hitPage) {
						gfx.setColor(pageHeightlighted);
						gfx.fillRect(x, rectScrollBar.y + 2,
								(int) Math.min(localPageWidth, canvasBoundary.width - size - x),
								rectScrollBar.height - 2);
					}
					if (page > 0) {
						gfx.setColor(pageFrame);
						gfx.drawLine(x, rectScrollBar.y + 1, x, rectScrollBar.y + rectScrollBar.height - 2);
					}
				}
				if (x + 0.5 * localPageWidth < end && localPageWidth > 12) {
					gfx.setColor(pageFontColor);
					gfx.drawString(Integer.toString(page + 1), (int) (x + localPageWidth / 2), rectScrollBar.y
							+ rectScrollBar.height - fontHeight / 2);
				}
			}

			gfx.setColor(Color.WHITE);
		} else {
			final int fontWidth = 6;
			final int start = canvasBoundary.y + size;
			final int end = canvasBoundary.height - size - cornerSquareSize();
			final double localPageHeight = (end - start) / pageNumber;
			for (int page = 0, y; page < pageNumber; page++) {
				y = (int) (localPageHeight * page) + start;
				if (localPageHeight > 5) {
					if (!interacting && page == hitPage) {
						gfx.setColor(pageHeightlighted);
						gfx.fillRect(rectScrollBar.x + 2, y, rectScrollBar.width - 2,
								(int) Math.min(localPageHeight, canvasBoundary.height - size - y));
					}
					if (page > 0) {
						gfx.setColor(pageFrame);
						gfx.drawLine(rectScrollBar.x, y, rectScrollBar.x + rectScrollBar.width, y);
					}
				}
				if (y + 0.5 * localPageHeight < end && localPageHeight > 12) {
					gfx.setColor(pageFontColor);
					gfx.drawString(Integer.toString(page + 1), rectScrollBar.x + rectScrollBar.width / 2 - fontWidth
							/ 2, (int) (y + localPageHeight / 2));
				}
			}
		}
	}

	private int getHitPage(Point pt) {

		final double pageNumber = documentViewportRatio();
		if (pageNumber == 0) {
			return -1;
		}
		final double localPageWidth = getPageWidth();
		if (horizontal) {
			final int start = canvasBoundary.x + size;
			return (int) Math.floor((pt.x - start) / localPageWidth);
		} else {
			final int start = canvasBoundary.y + size;
			return (int) Math.floor((pt.y - start) / localPageWidth);
		}
	}

	private boolean hitScrollbar(Point pt) {

		return rectScrollBar.contains(pt.x, pt.y);
	}
	private boolean hitPageArea(Point pt) {

		if(rectScrollBar.contains(pt.x, pt.y)){
			if (horizontal){
				return pt.x > rectMinusButton.x + rectMinusButton.width &&  pt.x < rectPlusButton.x ;
			} else {
				return pt.y > rectMinusButton.y + rectMinusButton.height &&  pt.y < rectPlusButton.y ;
			}
		}
		return false;
	}

	private boolean hitTumb(Point pt) {

		return rectThumb.contains(pt.x, pt.y);
	}

	private boolean hitZoomMinus(Point pt) {

		if (hitTumb(pt)) {
			if (horizontal) {
				return pt.x < rectThumb.x + ZOOM_BAR_REGION;
			} else {
				return pt.y < rectThumb.y + ZOOM_BAR_REGION;
			}
		}
		return false;
	}

	private boolean hitZoomPlus(Point pt) {

		if (hitTumb(pt)) {
			if (horizontal) {
				return pt.x > rectThumb.x + rectThumb.width - ZOOM_BAR_REGION;
			} else {
				return pt.y > rectThumb.y + rectThumb.height - ZOOM_BAR_REGION;
			}
		}
		return false;
	}

	private boolean hitMinusButton(Point pt) {

		return rectMinusButton.contains(pt.x, pt.y);
	}

	private boolean hitPlusButton(Point pt) {

		return rectPlusButton.contains(pt.x, pt.y);
	}


	private void goToPage(int page) {

		if (page < 0) {
			return;
		}
		final double pageWidth = getPageWidth();
		final double pageStart = page * pageWidth;
		setValue((int)scrollbarPageAreaToDocument(pageStart));   
	}

	private double getPageWidth() {

		return scrollbarPageExpansion() / documentViewportRatio();
	}

	//	private void drawThumb(Graphics2D gfx) {
	//
	//		if (!armed) {
	//			button1 = new Color(150, 150, 205, 50);
	//			button2 = new Color(255, 255, 255, 100);
	//		} else {
	//			button1 = new Color(150, 150, 150, 200);
	//			button2 = new Color(255, 255, 255, 100);
	//		}
	//		Rectangle r = rectThumb;
	//		GradientPaint paint = new GradientPaint(0, r.y, button2, 0, r.y + r.height, button1);
	//		gfx.setPaint(paint);
	//		gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height - 2);
	//		paint = new GradientPaint(0, r.y + r.height / 2 - 5, button1, 0, r.y + r.height, button2);
	//		gfx.setPaint(paint);
	//		gfx.fillRect(r.x + 1, r.y + r.height / 2, r.width - 1, r.height / 2);
	//
	//		// frame
	//		if (!armed) {
	//			gfx.setColor(new Color(180, 190, 210));
	//		} else {
	//			gfx.setColor(new Color(180, 180, 200));
	//		}
	//		gfx.drawRoundRect(rectThumb.x + 1, rectThumb.y, rectThumb.width - 2, rectThumb.height, 4, 5);
	//
	//		if (armed) {
	//			drawZoomBar(gfx);
	//		}
	//	}

	private void drawThumb(Graphics2D gfx) {

		if (!armed) {
			gfx.setColor(new Color(200, 220, 250, 100));
			gfx.fillRect(rectThumb.x + 2, rectThumb.y + 1, rectThumb.width - 4, rectThumb.height - 2);
		} else {

			button1 = new Color(150, 150, 150, 100);
			final Rectangle r = rectThumb;
			GradientPaint paint = new GradientPaint(0, r.y, button2, 0, r.y + r.height, button1);
			gfx.setPaint(paint);
			gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height - 2);
			paint = new GradientPaint(0, r.y + r.height / 2 - 5, button1, 0, r.y + r.height, button2);
			gfx.setPaint(paint);
			gfx.fillRect(r.x + 1, r.y + r.height / 2, r.width - 1, r.height / 2);
		}

		// frame
		if (!armed) {
			gfx.setColor(new Color(170, 180, 200));
		} else {
			gfx.setColor(new Color(180, 180, 200));
		}
		gfx.drawRoundRect(rectThumb.x + 1, rectThumb.y, rectThumb.width - 2, rectThumb.height, 4, 4);

		if (armed) {
			drawZoomBar(gfx);
		}
	}


	private void drawZoomBar(Graphics2D gfx) {

		int zoombarThickness = size / 2;
		int magnifierSize = zoombarThickness - 2;
		if (zoomMinusArmed) {
			gfx.setColor(new Color(255, 150, 50));
			gfx.drawLine(rectThumb.x + 1, rectThumb.y + 3, rectThumb.x + 1, rectThumb.y + size - 3);
			gfx.setColor(new Color(255, 150, 50));
			gfx.drawLine(rectThumb.x + 2, rectThumb.y + 2, rectThumb.x + 2, rectThumb.y + size - 2);
			gfx.setColor(new Color(255, 210, 50));
			gfx.drawLine(rectThumb.x + 3, rectThumb.y + 1, rectThumb.x + 3, rectThumb.y + size - 1);
		} else {
			gfx.setColor(new Color(255, 150, 100));
			gfx.drawLine(rectThumb.x + 1, rectThumb.y + 5, rectThumb.x + 1, rectThumb.y + size - 5);

			gfx.drawLine(rectThumb.x + rectThumb.width - 1, rectThumb.y + 5, rectThumb.x + rectThumb.width - 1,
					rectThumb.y + size - 5);
		}

		/*
		 * if (horizontal) { int x = rectThumb.x + zoombarThickness; int y =
		 * rectThumb.y + rectThumb.height / 2; drawMagnifier(gfx, x, y,
		 * magnifierSize); x = rectThumb.x + rectThumb.width - zoombarThickness
		 * - 3; y = rectThumb.y + rectThumb.height / 2 - 1; drawMagnifier(gfx,
		 * x, y, magnifierSize); } else { int x = rectThumb.x + zoombarThickness
		 * - 1; int y = rectThumb.y + zoombarThickness + 1; drawMagnifier(gfx,
		 * x, y, magnifierSize); x = rectThumb.x + zoombarThickness - 1; y =
		 * rectThumb.y + rectThumb.height - zoombarThickness - 2;
		 * drawMagnifier(gfx, x, y, magnifierSize); }
		 */
	}

	private void drawMagnifier(Graphics2D gfx, int x, int y, int size) {

		gfx.drawLine(x, y - size / 4, x, y + size / 4);
		gfx.drawLine(x - size / 4, y, x + size / 4, y);
		gfx.drawOval(x - size / 2, y - size / 2, size, size);
		// gfx.drawLine(x + size / 2, y + size / 2, x + 4, y + 4);

		// gfx.setColor(new Color(160, 160, 200));
		// y = y-4;
		// x-=3;
		// y++;
		// gfx.drawPolygon(new Polygon(new int[] { x, x + size, x + size }, new
		// int[] { y + size / 2, y, y + size },
		// 3));
		// gfx.setColor(new Color(255, 255, 255));
		// x--;
		// y--;
		// gfx.drawPolygon(new Polygon(new int[] { x, x + size, x + size }, new
		// int[] { y + size / 2, y, y + size },
		// 3));

	}

	private void drawButtonMinus(Graphics2D gfx) {

		drawButton(gfx, rectMinusButton, true);
		gfx.setColor(armed ? arrowArmed : arrow);

		if (horizontal) {
			drawArrowToWest(gfx, rectMinusButton.x + size / 3, rectMinusButton.y + size / 3, size / 2, size / 2);
		} else {
			drawArrowToNorth(gfx, rectMinusButton.x + size / 3, rectMinusButton.y + size / 3, size / 2, size / 2);
		}
	}

	private void drawButtonPlus(Graphics2D gfx) {

		drawButton(gfx, rectPlusButton, false);
		gfx.setColor(armed ? arrowArmed : arrow);
		if (horizontal) {
			drawArrowToEast(gfx, rectPlusButton.x + size / 3, rectPlusButton.y + size / 3, size / 2, size / 2);
		} else {
			drawArrowToSouth(gfx, rectPlusButton.x + size / 3, rectPlusButton.y + size / 3, size / 2, size / 2);
		}
	}

	private void drawCornerSquare(Graphics2D gfx) {

		if (!horizontal) {
			gfx.setColor(new Color(0xF9B104));
			gfx.fillRoundRect(rectScrollBar.x + 1, rectScrollBar.y + rectScrollBar.height + 1, cornerSquareSize() - 2,
					cornerSquareSize() - 2, 0, 0);
			gfx.setColor(background1);
			gfx.drawRoundRect(rectScrollBar.x + 3, rectScrollBar.y + rectScrollBar.height + 3, cornerSquareSize() - 7,
					cornerSquareSize() - 7, 4, 4);
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

	private void drawButton(Graphics2D gfx, Rectangle r, boolean minus) {

		if (armed) {
			if (horizontal) {
				GradientPaint paint = new GradientPaint(0, r.y, button2, 0, r.y + r.height, button1);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height - 2);
				paint = new GradientPaint(0, r.y + r.height / 2 - 5, button1, 0, r.y + r.height, button2);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + 1, r.y + r.height / 2, r.width - 1, r.height / 2);
				gfx.setColor(button1);
				if (minus) {
					gfx.drawLine(r.x + r.width, r.y + r.height - size + 1, r.x + r.width, r.y + r.height);
				} else {
					gfx.drawLine(r.x, r.y + r.height - size + 1, r.x, r.y + r.height);
				}
			} else {
				GradientPaint paint = new GradientPaint(r.x, 0, button2, r.x + r.width, 0, button1);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + 1, r.y + 1, r.width - 1, r.height - 2);
				paint = new GradientPaint(r.x + r.width / 2 - 5, 0, button1, r.x + r.width, 0, button2);
				gfx.setPaint(paint);
				gfx.fillRect(r.x + r.width / 2, r.y, r.width / 2, r.height);
				gfx.setColor(button1);
				if (minus) {
					gfx.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height);
				} else {
					gfx.drawLine(r.x, r.y, r.x + r.width, r.y);
				}
			}
		}
	}
}
