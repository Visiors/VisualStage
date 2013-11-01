package com.visiors.visualstage.tool.impl;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.listener.GraphDocumentAdapter;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.impl.scrollbar.DefaultOfflineRenderer;
import com.visiors.visualstage.tool.impl.scrollbar.DragHelper;
import com.visiors.visualstage.tool.impl.scrollbar.OfflineRenderer;
import com.visiors.visualstage.tool.impl.scrollbar.ScrollBarBackgroundPainter;
import com.visiors.visualstage.tool.impl.scrollbar.ScrollBarButtonPainter;
import com.visiors.visualstage.tool.impl.scrollbar.ScrollBarCornerSquarePainter;
import com.visiors.visualstage.tool.impl.scrollbar.ScrollBarThumbPainter;
import com.visiors.visualstage.tool.impl.scrollbar.ScrollbarStyle;
import com.visiors.visualstage.transform.Transform;

public class ScrollBar implements Interactable {

	private int min;
	private int max;
	private final int unitIncrement = 20;
	private int value;
	private final boolean isHorizontal;
	private final DragHelper dragHelper;
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
	private final OfflineRenderer thumbRenderer;
	private final OfflineRenderer minusButtonRenderer;
	private final OfflineRenderer plusButtonRenderer;
	private final OfflineRenderer cornerSquareRenderer;
	private final OfflineRenderer backgroundRenderer;

	public ScrollBar(boolean horizontal) {

		this.isHorizontal = horizontal;
		this.dragHelper = new DragHelper(this);
		this.backgroundRenderer = new DefaultOfflineRenderer(new ScrollBarBackgroundPainter(this));
		this.minusButtonRenderer = new DefaultOfflineRenderer(new ScrollBarButtonPainter(this, true));
		this.plusButtonRenderer = new DefaultOfflineRenderer(new ScrollBarButtonPainter(this, false));
		this.thumbRenderer = new DefaultOfflineRenderer(new ScrollBarThumbPainter(this));
		this.cornerSquareRenderer = new DefaultOfflineRenderer(new ScrollBarCornerSquarePainter(this));
	}

	public void setGraphDocument(final GraphDocument graphDocument) {

		this.graphDocument = graphDocument;
		this.value = isHorizontal ? graphDocument.getViewportPos().x : graphDocument.getViewportPos().y;

		resetInteractionValues();
		graphDocument.addGraphDocumentListener(new GraphDocumentAdapter() {

			@Override
			public void graphExpansionChanged() {

				Rectangle r = graphDocument.getGraph().getExtendedBoundary();
				if (!r.equals(graphBoundary)) {
					graphBoundary = r;
					thumbRenderer.invalidate();
					update();
				}
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

		this.value = Math.max(min, Math.min(value, max));
		final Point vp = graphDocument.getViewportPos();
		graphDocument.setViewportPos(isHorizontal ? -value : vp.x, !isHorizontal ? -value : vp.y);
		graphBoundary = graphDocument.getGraph().getExtendedBoundary();
	}

	public int getValue() {

		return value;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (armed) {
			final Transform xform = graphDocument.getTransformer();
			final Point ptScreen = xform.transformToScreen(pt);
			if (hitMinusButton(ptScreen)) {
				onButtonClick(true);
			} else if (hitPlusButton(ptScreen)) {
				onButtonClick(false);
			} else if (zoomMinusArmed || zoomPlusArmed || hitTumb(ptScreen)) {
				interacting = true;
				dragHelper.registerMousePressed(pt);
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

		hitPage = -1;
		zoomMinusArmed = false;
		zoomPlusArmed = false;

		final Point ptScreen = graphDocument.getTransformer().transformToScreen(pt);
		final boolean hit = hitScrollbar(ptScreen);
		boolean modified = hit != armed;
		if (armed != hit) {
			armed = hit;
			minusButtonRenderer.invalidate();
			plusButtonRenderer.invalidate();
			thumbRenderer.invalidate();
		}
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
		if (modified) {
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
			resetInteractionValues();
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

	private void resetInteractionValues() {

		armed = false;
		zoomMinusArmed = false;
		zoomPlusArmed = false;
		hitPage = -1;
	}

	@Override
	public int getPreferredCursor() {

		if (zoomMinusArmed || zoomPlusArmed) {
			return (isHorizontal ? Interactable.CURSOR_W_RESIZE : Interactable.CURSOR_N_RESIZE);
		}
		return Interactable.CURSOR_DEFAULT;
	}

	private int cornerSquareSize() {

		return ScrollbarStyle.size;
	}

	private void update() {

		graphDocument.invalidate();
	}

	private void onMinusZoomChanged() {

		final double oldZoomValue = graphDocument.getZoom();

		final int dx = mouseCurrentlyAt.x - dragHelper.mousePressedAt.x;
		double newZoomValue = dragHelper.initialZoom * dragHelper.initialThumbExpansion
				/ (dragHelper.initialThumbExpansion - dx);
		newZoomValue = Math.max(Math.min(newZoomValue, 10), 0.1);
		graphDocument.setZoom(newZoomValue);

		// onThumbMoved();

		final Transform xform = graphDocument.getTransformer();
		final int gxCurrent = xform.transformToGraphX(canvasBoundary.width);
		final int gyCurrent = xform.transformToGraphY(canvasBoundary.height / 2);
		// xform.setYTranslate(xform.getYTranslate() - initialGraphStartYPos
		// -initialGraphEndYPos/2 + gyCurrent);
		// setValue(getValue() + initialGraphEndXPos - gxCurrent);
		// System.err.println(getValue() -initialGraphEndXPos * (oldZoomValue -
		// newZoomValue));
	}

	private void onPlusZoomChanged() {

		int dx = mouseCurrentlyAt.x - dragHelper.mousePressedAt.x;
		mouseCurrentlyAt.x = Math.max(dragHelper.initialThumbPos, mouseCurrentlyAt.x);
		double newZoomValue = dragHelper.initialZoom * dragHelper.initialThumbExpansion
				/ (dragHelper.initialThumbExpansion + dx);
		newZoomValue = Math.max(Math.min(newZoomValue, 10), 0.1);
		graphDocument.setZoom(newZoomValue);

		//
		final Transform xform = graphDocument.getTransformer();
		final int gxCurrent = xform.transformToGraphX(0);
		final int gyCurrent = xform.transformToGraphY(canvasBoundary.height / 2);
		// xform.setYTranslate(xform.getYTranslate() - initialGraphStartYPos
		// -initialGraphEndYPos/2 + gyCurrent);
		// moveThumb( -gxCurrent + initialGraphEndXPos);

		dx = graphDocument.getGraph().getExtendedBoundary().x - dragHelper.initialGraphStartXPos;

		System.err.println(dx);
		// xform.setXTranslate(initialValue+ dx);

	}

	private void onThumbMoved() {

		int offset;
		if (isHorizontal) {
			offset = mouseCurrentlyAt.x - dragHelper.mousePressedAt.x;
		} else {
			offset = mouseCurrentlyAt.y - dragHelper.mousePressedAt.y;
		}
		moveThumb(offset);
	}

	private void moveThumb(int offset) {

		offset = Math.max(offset, -dragHelper.initialThumbPos);
		offset = Math.min(offset, scrollbarPageExpansion()
				- (dragHelper.initialThumbPos + dragHelper.initialThumbExpansion));
		final int newViewportPos = (int) (dragHelper.initialValue + offset * documentScrollableRatio());
		setValue(newViewportPos);
	}

	private void onButtonClick(boolean minus) {

		final int old = getValue();
		final int newValue = getValue() + (minus ? -unitIncrement : unitIncrement);
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

		return isHorizontal ? canvasBoundary.width : canvasBoundary.height;
	}

	private int scrollbarPageExpansion() {

		if (isHorizontal) {
			return rectScrollBar.width - rectMinusButton.width - rectPlusButton.width;
		}
		return rectScrollBar.height - rectMinusButton.height - rectPlusButton.height;
	}

	private void computeMinMaxValues() {

		final int mergin = canvasBoundary.width;
		if (isHorizontal) {
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
	}

	private void computeThumbPos() {

		thumbPos = (int) convertDocumentToScrollbarPageArea(-min);
		thumbPos = Math.min(Math.max(thumbPos, 0), scrollbarPageExpansion() - thumbExpansion);
	}

	private void computeThumbRect() {

		if (isHorizontal) {
			rectThumb.setBounds(canvasBoundary.x + ScrollbarStyle.size + thumbPos, canvasBoundary.y
					+ canvasBoundary.height - ScrollbarStyle.size, thumbExpansion, ScrollbarStyle.size);

		} else {
			rectThumb.setBounds(canvasBoundary.x + canvasBoundary.width - ScrollbarStyle.size, canvasBoundary.y
					+ ScrollbarStyle.size + thumbPos, ScrollbarStyle.size, thumbExpansion);
		}
	}

	private void computeScrollBarRect() {

		if (isHorizontal) {
			rectScrollBar.setBounds(canvasBoundary.x, canvasBoundary.y + canvasBoundary.height - ScrollbarStyle.size,
					canvasBoundary.width - cornerSquareSize(), ScrollbarStyle.size);

		} else {
			rectScrollBar.setBounds(canvasBoundary.x + canvasBoundary.width - ScrollbarStyle.size, canvasBoundary.y,
					ScrollbarStyle.size, canvasBoundary.height - cornerSquareSize());
		}
	}

	private void computeMinusButtonRect() {

		if (isHorizontal) {
			rectMinusButton.setBounds(canvasBoundary.x, canvasBoundary.y + canvasBoundary.height - ScrollbarStyle.size,
					ScrollbarStyle.size, ScrollbarStyle.size);
		} else {
			rectMinusButton.setBounds(canvasBoundary.x + canvasBoundary.width - ScrollbarStyle.size, canvasBoundary.y,
					ScrollbarStyle.size, ScrollbarStyle.size);
		}
	}

	private void computePlusButtonRect() {

		if (isHorizontal) {
			rectPlusButton.setBounds(
					canvasBoundary.x + canvasBoundary.width - ScrollbarStyle.size - cornerSquareSize(),
					canvasBoundary.y + canvasBoundary.height - ScrollbarStyle.size, ScrollbarStyle.size,
					ScrollbarStyle.size);
		} else {
			rectPlusButton.setBounds(canvasBoundary.x + canvasBoundary.width - ScrollbarStyle.size, canvasBoundary.y
					+ canvasBoundary.height - ScrollbarStyle.size - cornerSquareSize(), ScrollbarStyle.size,
					ScrollbarStyle.size);
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

		backgroundRenderer.render(gfx);
		thumbRenderer.render(gfx);
		minusButtonRenderer.render(gfx);
		plusButtonRenderer.render(gfx);
		cornerSquareRenderer.render(gfx);
	}

	private int getHitPage(Point pt) {

		final double pageNumber = documentViewportRatio();
		if (pageNumber == 0) {
			return -1;
		}
		final double localPageWidth = getPageWidth();
		if (isHorizontal) {
			final int start = canvasBoundary.x + ScrollbarStyle.size;
			return (int) Math.floor((pt.x - start) / localPageWidth);
		} else {
			final int start = canvasBoundary.y + ScrollbarStyle.size;
			return (int) Math.floor((pt.y - start) / localPageWidth);
		}
	}

	private boolean hitScrollbar(Point pt) {

		return rectScrollBar.contains(pt.x, pt.y);
	}

	private boolean hitPageArea(Point pt) {

		if (rectScrollBar.contains(pt.x, pt.y)) {
			if (isHorizontal) {
				return pt.x > rectMinusButton.x + rectMinusButton.width && pt.x < rectPlusButton.x;
			} else {
				return pt.y > rectMinusButton.y + rectMinusButton.height && pt.y < rectPlusButton.y;
			}
		}
		return false;
	}

	private boolean hitTumb(Point pt) {

		return rectThumb.contains(pt.x, pt.y);
	}

	private boolean hitZoomMinus(Point pt) {

		if (hitTumb(pt)) {
			if (isHorizontal) {
				return pt.x < rectThumb.x + ScrollbarStyle.ZOOM_BAR_REGION;
			} else {
				return pt.y < rectThumb.y + ScrollbarStyle.ZOOM_BAR_REGION;
			}
		}
		return false;
	}

	private boolean hitZoomPlus(Point pt) {

		if (hitTumb(pt)) {
			if (isHorizontal) {
				return pt.x > rectThumb.x + rectThumb.width - ScrollbarStyle.ZOOM_BAR_REGION;
			} else {
				return pt.y > rectThumb.y + rectThumb.height - ScrollbarStyle.ZOOM_BAR_REGION;
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
		setValue((int) scrollbarPageAreaToDocument(pageStart));
	}

	private double getPageWidth() {

		return scrollbarPageExpansion() / documentViewportRatio();
	}

	public GraphDocument getGraphDocument() {

		return graphDocument;
	}

	public int getThumbPos() {

		return thumbPos;
	}

	public int getThumbExpansion() {

		return thumbExpansion;
	}

	public boolean isHorizontal() {

		return isHorizontal;
	}

	public boolean isArmed() {

		return armed;
	}

	public Rectangle getRectMinusButton() {

		return rectMinusButton;
	}

	public Rectangle getRectPlusButton() {

		return rectPlusButton;
	}

	public Rectangle getRectScrollBar() {

		return rectScrollBar;
	}

	public Rectangle getRectThumb() {

		return rectThumb;
	}

	public boolean isZoomMinusArmed() {

		return zoomMinusArmed;
	}

	public boolean isZoomPlusArmed() {

		return zoomPlusArmed;
	}
}
