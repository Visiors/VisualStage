package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.listener.GraphDocumentAdapter;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.ComponentOffScreenRenderer;
import com.visiors.visualstage.renderer.DefaultComponentOfflineRenderer;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.impl.BaseTool;
import com.visiors.visualstage.transform.Transform;

public class ScrollBar extends BaseTool{

	public int size = StageStyleConstants.scrollbar_defaultSize;
	private int min;
	private int max;
	private int value;
	private final boolean isHorizontal;
	private final DragHelper dragHelper;
	private final Rectangle rectPlusButton = new Rectangle();
	private final Rectangle rectMinusButton = new Rectangle();
	private final Rectangle rectScrollBar = new Rectangle();
	private final Rectangle rectPageArea = new Rectangle();
	private final Rectangle rectThumb = new Rectangle();
	private Rectangle graphBoundary = new Rectangle();
	private Rectangle canvasBoundary;
	private Point mouseCurrentlyAt;
	// private final Rectangle rectCornderSquare = new Rectangle();
	private boolean armed;
	private boolean interacting;
	private int thumbPos;
	private int thumbExpansion;
	private int hitPage = -1;
	private boolean zoomMinusArmed;
	private boolean zoomPlusArmed;
	private final ComponentOffScreenRenderer thumbRenderer;
	private final ComponentOffScreenRenderer minusButtonRenderer;
	private final ComponentOffScreenRenderer plusButtonRenderer;
	private final ComponentOffScreenRenderer backgroundRenderer;
	private final ComponentOffScreenRenderer pageRenderer;

	public ScrollBar(boolean horizontal, String name) {

		super(name );
		this.isHorizontal = horizontal;
		this.dragHelper = new DragHelper(this);
		this.backgroundRenderer = new DefaultComponentOfflineRenderer(new ScrollBarBackgroundPainter(this));
		this.minusButtonRenderer = new DefaultComponentOfflineRenderer(new ScrollBarButtonPainter(this, true));
		this.plusButtonRenderer = new DefaultComponentOfflineRenderer(new ScrollBarButtonPainter(this, false));
		this.thumbRenderer = new DefaultComponentOfflineRenderer(new ScrollBarThumbPainter(this));
		this.pageRenderer = new DefaultComponentOfflineRenderer(new ScrollBarPagePainter(this));
	}

	public void setScop( final GraphDocument graphDocument) {

		super.setScope(graphDocument);
		this.graphDocument = graphDocument;
		this.value = isHorizontal ? graphDocument.getViewportPos().x : graphDocument.getViewportPos().y;

		resetInteractionValues();
		graphDocument.addGraphDocumentListener(new GraphDocumentAdapter() {

			@Override
			public void graphExpansionChanged() {

				final Rectangle r = visualGraph.getExtendedBoundary();
				if (!r.equals(graphBoundary)) {
					graphBoundary = r;
					thumbRenderer.invalidate();
					backgroundRenderer.invalidate();
					pageRenderer.invalidate();
					minusButtonRenderer.invalidate();
					plusButtonRenderer.invalidate();
					update();
				}
			}
		});
	}

	public void setMin(int min) {

		final Transform xform = graphDocument.getTransformer();
		min -= isHorizontal ? xform.getViewX() : xform.getViewY();
		this.min = Math.min(min, 0);
	}

	public void setMax(int max) {

		final Transform xform = graphDocument.getTransformer();
		max -= isHorizontal ? xform.getViewX() : xform.getViewY();
		this.max = Math.max(max, 0);
	}

	public int getMin() {

		return min;
	}

	public int getMax() {

		return max;
	}

	public void setValue(int value) {

		this.value = value;
		final Point vp = graphDocument.getViewportPos();
		graphDocument.setViewportPos(isHorizontal ? -value : vp.x, !isHorizontal ? -value : vp.y);
		graphBoundary = visualGraph.getExtendedBoundary();
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

		final Point ptScreen = graphDocument.getTransformer().transformToScreen(pt);

		final boolean hitScrollbar = hitScrollbar(ptScreen);
		if (armed != hitScrollbar) {
			armed = hitScrollbar;
			minusButtonRenderer.invalidate();
			plusButtonRenderer.invalidate();
			thumbRenderer.invalidate();
			pageRenderer.invalidate();
			hitPage = -1;
			update();
		}
		if (armed) {
			boolean hitZoom = hitZoomMinus(ptScreen);
			if (hitZoom != zoomMinusArmed) {
				zoomMinusArmed = hitZoom;
				thumbRenderer.invalidate();
			}
			hitZoom = hitZoomPlus(ptScreen);
			if (hitZoom != zoomPlusArmed) {
				zoomPlusArmed = hitZoom;
				thumbRenderer.invalidate();
			}

			int newHitPage = -1;
			if (!hitTumb(ptScreen) && !hitMinusButton(ptScreen) && !hitPlusButton(ptScreen)) {
				newHitPage = getHitPage(ptScreen);
			}
			if (newHitPage != hitPage) {
				hitPage = newHitPage;
				pageRenderer.invalidate();
			}
			update();
		}

		return armed;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		final Point ptScreen = graphDocument.getTransformer().transformToScreen(pt);
		if (interacting) {
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
		return hitScrollbar(ptScreen);
	}

	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		if (!isInteracting()) {
			if ((isArmed())) {
				resetInteractionValues();
				minusButtonRenderer.invalidate();
				plusButtonRenderer.invalidate();
				thumbRenderer.invalidate();
				pageRenderer.invalidate();
				update();
			}
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

	private void update() {

		graphDocument.invalidate();
	}

	private void onMinusZoomChanged() {

		final int dx = -mouseCurrentlyAt.x + dragHelper.mousePressedAt.x;
		mouseCurrentlyAt.x = Math.max(dragHelper.initialThumbPos, mouseCurrentlyAt.x);

		double newZoomValue = dragHelper.initialZoom * dragHelper.initialThumbExpansion
				/ (dragHelper.initialThumbExpansion + dx);
		newZoomValue = Math.max(Math.min(newZoomValue, 10), 0.1);
		final Transform xform = graphDocument.getTransformer();

		final int xlog1 = xform.transformToGraphX(canvasBoundary.x + canvasBoundary.width);
		// graphDocument.setZoom(newZoomValue);
		graphDocument.getTransformer().setScale(newZoomValue);
		final int xlog2 = xform.transformToGraphX(canvasBoundary.x + canvasBoundary.width);
		final int delta = xform.transformToScreenX(xlog2) - xform.transformToScreenX(xlog1);

		graphDocument.getTransformer().setXTranslate(graphDocument.getTransformer().getXTranslate() + delta);
	}

	private void onPlusZoomChanged() {

		final int dxMouse = Math.max(dragHelper.initialThumbPos, mouseCurrentlyAt.x) - dragHelper.initialThumbPos
				- dragHelper.initialThumbExpansion;
		final double thumbChange = (double) (dragHelper.initialThumbExpansion + dxMouse)
				/ dragHelper.initialThumbExpansion;
		double newZoom = dragHelper.initialZoom / thumbChange;
		newZoom = Math.max(newZoom, 0.1);
		newZoom = Math.min(newZoom, 10);
		final double xtranslate = scrollbarPageAreaToDocument(dragHelper.initialThumbPos);
		setValue((int) xtranslate);
		graphDocument.getTransformer().setScale(newZoom);
	}

	private void onThumbMoved() {

		int offset;
		if (isHorizontal) {
			offset = mouseCurrentlyAt.x - dragHelper.mousePressedAt.x;
		} else {
			offset = mouseCurrentlyAt.y - dragHelper.mousePressedAt.y;
		}
		moveThumb(offset, dragHelper.initialValue, (dragHelper.initialThumbPos + dragHelper.initialThumbExpansion),
				-dragHelper.initialThumbPos);
	}

	void moveThumb(int offset, int ancore, int max, int min) {

		offset = Math.max(offset, min);
		offset = Math.min(offset, scrollbarPageExpansion() - max);
		final int newViewportPos = (int) (ancore + offset * documentScrollableRatio());
		setValue(newViewportPos);
	}

	private void onButtonClick(boolean minus) {

		final int newValue = getValue() + StageStyleConstants.scrollbar_unitIncrement * (minus ? -1 : 1);
		setValue(newValue);
	}

	private void computeHScrollBarGeometry() {

		this.canvasBoundary = graphDocument.getClientBoundary();

		computeScrollBarRect();
		computeMinusButtonRect();
		computePlusButtonRect();
		computeMinMaxValues();
		computeThumbExpansion();
		computeThumbPos();
		computeThumbRect();
		computePageAreaRect();
	}

	private int documentExpansion() {

		return max - min;// Math.min(getValue(), min);
	}

	private int getViewPortExpansion() {

		return isHorizontal ? canvasBoundary.width : canvasBoundary.height;
	}

	private int scrollbarPageExpansion() {

		if (isHorizontal) {
			return rectPlusButton.x - rectMinusButton.x - rectMinusButton.width;
		} else {
			return rectPlusButton.y - rectMinusButton.y - rectMinusButton.height;
		}
	}

	private void computeMinMaxValues() {

		if (isHorizontal) {
			final int mergin = canvasBoundary.width;
			setMin(graphBoundary.x - mergin);
			final int maxValue = graphBoundary.x + graphBoundary.width;
			final int pages = (graphBoundary.x + graphBoundary.width - min) / canvasBoundary.width + 1;
			final int diffToFullPage = pages * canvasBoundary.width - (maxValue - min);
			setMax(maxValue + diffToFullPage + mergin);
		} else {
			final int mergin = canvasBoundary.height;
			setMin(graphBoundary.y - mergin);
			final int maxValue = graphBoundary.y + graphBoundary.height;
			final int pages = (graphBoundary.y + graphBoundary.height - min) / canvasBoundary.height + 1;
			final int diffToFullPage = pages * canvasBoundary.height - (maxValue - min);
			setMax(maxValue + diffToFullPage + mergin);
		}
	}

	public double documentViewportRatio() {

		return (double) documentExpansion() / getViewPortExpansion();
	}

	private double documentScrollableRatio() {

		return (double) documentExpansion() / scrollbarPageExpansion();
	}

	private double documentPageAreaRatio() {

		if (isHorizontal) {
			return canvasBoundary.width / documentScrollableRatio();
		} else {
			return canvasBoundary.height / documentScrollableRatio();
		}
	}

	private double scrollbarPageAreaToDocument(double pos) {

		return pos * documentScrollableRatio() + min + getValue();
	}

	private double convertDocumentToScrollbarPageArea(double pos) {

		return pos / documentScrollableRatio();
	}

	private void computeThumbExpansion() {

		thumbExpansion = (int) documentPageAreaRatio();
	}

	private void computeThumbPos() {

		// if (!zoomMinusArmed && !zoomPlusArmed)
		{
			thumbPos = (int) convertDocumentToScrollbarPageArea(-min);
			thumbPos = Math.min(Math.max(thumbPos, 0), scrollbarPageExpansion() - thumbExpansion);
		}
	}

	private void computeThumbRect() {

		if (isHorizontal) {
			rectThumb.setBounds(canvasBoundary.x + size + thumbPos, canvasBoundary.y + canvasBoundary.height,
					thumbExpansion, size);
		} else {
			rectThumb.setBounds(canvasBoundary.x + canvasBoundary.width, canvasBoundary.y + size + thumbPos, size,
					thumbExpansion);
		}
	}

	public void computePageAreaRect() {

		if (isHorizontal) {
			rectPageArea.setBounds(rectMinusButton.x + rectMinusButton.width, rectScrollBar.y, rectPlusButton.x
					- rectMinusButton.x - rectMinusButton.width, rectScrollBar.height);

		} else {
			rectPageArea.setBounds(rectScrollBar.x, rectMinusButton.y + rectMinusButton.height, rectScrollBar.width,
					rectPlusButton.y - rectMinusButton.y - rectMinusButton.height);
		}
	}

	private void computeScrollBarRect() {

		if (isHorizontal) {
			rectScrollBar.setBounds(canvasBoundary.x, canvasBoundary.y + canvasBoundary.height, canvasBoundary.width,
					size);
		} else {
			rectScrollBar.setBounds(canvasBoundary.x + canvasBoundary.width, canvasBoundary.y, size,
					canvasBoundary.height);
		}
	}

	private void computeMinusButtonRect() {

		rectMinusButton.setBounds(rectScrollBar.x, rectScrollBar.y, size, size);
	}

	private void computePlusButtonRect() {

		if (isHorizontal) {
			rectPlusButton.setBounds(rectScrollBar.x + rectScrollBar.width - size, rectScrollBar.y
					+ rectScrollBar.height - size, size, size);
		} else {
			rectPlusButton.setBounds(rectScrollBar.x + rectScrollBar.width - size, rectScrollBar.y
					+ rectScrollBar.height - size, size, size);
		}
	}

	public void draw(AWTCanvas awtCanvas) {

		if (graphDocument != null) {
			// TODO move this
			computeHScrollBarGeometry();

			final Graphics2D g = awtCanvas.gfx;
			backgroundRenderer.render(g);
			pageRenderer.render(g);
			thumbRenderer.render(g);
			minusButtonRenderer.render(g);
			plusButtonRenderer.render(g);
		}
	}

	private int getHitPage(Point pt) {

		final double pageNumber = documentViewportRatio();
		if (pageNumber == 0) {
			return -1;
		}
		final double localPageWidth = getPageWidth();
		if (isHorizontal) {
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
				return pt.x < rectThumb.x + StageStyleConstants.scrollbar_zoomBarDomain;
			} else {
				return pt.y < rectThumb.y + StageStyleConstants.scrollbar_zoomBarDomain;
			}
		}
		return false;
	}

	private boolean hitZoomPlus(Point pt) {

		if (hitTumb(pt)) {
			if (isHorizontal) {
				return pt.x > rectThumb.x + rectThumb.width - StageStyleConstants.scrollbar_zoomBarDomain;
			} else {
				return pt.y > rectThumb.y + rectThumb.height - StageStyleConstants.scrollbar_zoomBarDomain;
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

	public double getPageWidth() {

		return thumbExpansion;
	}

	public double getTotalePageNumber() {

		return documentViewportRatio();
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

	public Rectangle getRectPageArea() {

		return rectPageArea;
	}

	public boolean isZoomMinusArmed() {

		return zoomMinusArmed;
	}

	public boolean isZoomPlusArmed() {

		return zoomPlusArmed;
	}

	public int getHighlightedPage() {

		return hitPage;
	}

	public int getSize() {

		return size;
	}

	public void setSize(int size) {

		this.size = size;
		update();
	}

	Rectangle getPlusButtonRect() {

		return new Rectangle(rectPlusButton);
	}

	Rectangle getMinusButtonRect() {

		return new Rectangle(rectMinusButton);
	}

	Rectangle getScrollBarRect() {

		return rectScrollBar;
	}

	Rectangle getPageAreaRect() {

		return new Rectangle(rectPageArea);
	}

	Rectangle getThumbRect() {

		return new Rectangle(rectThumb);
	}

	Rectangle getCanvasBoundary() {

		return new Rectangle(canvasBoundary);
	}

}
