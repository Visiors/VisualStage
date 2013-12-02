package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.base.Objects;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DefaultDrawingContext;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.impl.BaseTool;
import com.visiors.visualstage.transform.Transform;

/**
 * This tool must be installed on the top of mouse / keyboard processing chain.
 * By consuming all mouse and keyboard events this class ensures that all other
 * interaction are disabled while this view is visible.
 * 
 * @author Shane
 * 
 */
public class Navigator extends BaseTool {

	private static Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 1, 3 }, 0);
	private static Stroke continuedStroke = new BasicStroke(1.0f);
	private Point mousePressedAt;
	private Rectangle rHitSection;

	private boolean autoClose;
	private final ScrollBar hScrollBar;
	private final ScrollBar vScrollBar;
	private Point mouseCurrentPos;
	private final DragHelper hDragHelper;
	private final DragHelper vDragHelper;
	private double transiteLcoalScale;
	private final Point transiteOffset = new Point();

	public Navigator(ScrollBar hScrollBar, ScrollBar vScrollBar) {

		super("NAVIGATOR");
		this.hScrollBar = hScrollBar;
		this.vScrollBar = vScrollBar;
		this.hDragHelper = new DragHelper(hScrollBar);
		this.vDragHelper = new DragHelper(vScrollBar);
		setAutoClose(true);
	}

	public void setAutoClose(boolean autoClose) {

		this.autoClose = autoClose;
	}

	public boolean isAutoClose() {

		return autoClose;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		final Transform xform = graphDocument.getTransformer();
		final Point ptScreen = xform.transformToScreen(pt);
		if (hitVistaWindow(ptScreen)) {
			mousePressedAt = ptScreen;
			hDragHelper.registerMousePressed(pt);
			vDragHelper.registerMousePressed(pt);
		} else if (rHitSection != null) {
			hDragHelper.registerMousePressed(pt);
			vDragHelper.registerMousePressed(pt);
			gotoHitSection();
		}
		return hitClientArea(ptScreen);
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		mouseCurrentPos = null;
		mousePressedAt = null;

		final Transform xform = graphDocument.getTransformer();
		final Point ptScreen = xform.transformToScreen(pt);
		if (hitClientArea(ptScreen)) {
			if (autoClose) {
				setActive(false);
			}
			graphDocument.invalidate();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		final Transform xform = graphDocument.getTransformer();
		final Point ptScreen = xform.transformToScreen(pt);
		Rectangle r = null;
		if (hitScrollableArea(ptScreen) && !hitVistaWindow(ptScreen)) {
			r = getHitSection(ptScreen);
		}
		if (!Objects.equal(r, rHitSection)) {
			rHitSection = r;
			graphDocument.invalidate();
		}
		return hitClientArea(ptScreen);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		final Transform xform = graphDocument.getTransformer();
		final Point ptScreen = xform.transformToScreen(pt);
		if (mousePressedAt != null && hitClientArea(ptScreen)) {
			if (isDraggingVista() || hitVistaWindow(ptScreen)) {
				mouseCurrentPos = ptScreen;
				onMovedVistaView();
				graphDocument.invalidate();
			}
			return true;
		}
		return false;
	}

	@Override
	public int getPreferredCursor() {

		return Interactable.CURSOR_DEFAULT;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		return active;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		return active;
	}

	@Override
	public void setActive(boolean activated) {

		if (activated != isActive()) {
			if (activated) {
				fadeIn();
			} else {
				fadeOut();
			}
			rHitSection = null;
			transiteLcoalScale = 0.;
			transiteOffset.setLocation(0, 0);
		}
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (onTop && active) {
			try {
				awtCanvas.gfx.translate(-transiteOffset.x, -transiteOffset.y);
				darwComponents(awtCanvas.gfx);
			} finally {
				awtCanvas.gfx.translate(transiteOffset.x, transiteOffset.y);
			}
		}
	}

	private void darwComponents(Graphics2D gfx) {

		drawCanvas(gfx);
		drawHightlightedSection(gfx);
		drawVistaWindow(gfx);
		drawSections(gfx);
		drawGraph(gfx);
	}

	private void drawCanvas(Graphics2D gfx) {

		final Rectangle canvasBoundary = getCanvasBoundary();
		final Rectangle scrollableArea = getScrollableArea();
		gfx.setColor(new Color(236, 239, 249));
		gfx.fillRect(canvasBoundary.x, canvasBoundary.y, canvasBoundary.width + transiteOffset.x, canvasBoundary.height
				+ transiteOffset.y);
		gfx.setColor(new Color(243, 245, 251));
		gfx.fillRect(scrollableArea.x, scrollableArea.y, scrollableArea.width + transiteOffset.x, scrollableArea.height
				+ transiteOffset.y);
	}

	private Rectangle getCanvasBoundary() {

		return hScrollBar.getCanvasBoundary();
	}

	private Rectangle getScrollableArea() {

		final Rectangle hPageArea = hScrollBar.getPageAreaRect();
		final Rectangle vPageArea = vScrollBar.getPageAreaRect();
		return new Rectangle(hPageArea.x, vPageArea.y, hPageArea.width, vPageArea.height);
	}

	private boolean hitClientArea(Point pt) {

		if (!active) {
			return false;
		}
		return getCanvasBoundary().contains(pt);
	}

	private boolean hitScrollableArea(Point pt) {

		if (!active) {
			return false;
		}
		return getScrollableArea().contains(pt);
	}

	private boolean hitVistaWindow(Point pt) {

		if (!active) {
			return false;
		}
		return getVistaWindowRect().contains(pt);
	}

	private Rectangle getVistaWindowRect() {

		final Rectangle scrollableArea = getScrollableArea();
		final Rectangle rVista = new Rectangle();
		final double localScale = computeLocalZoom();
		final Rectangle canvas = getCanvasBoundary();
		final Rectangle rClient = graphDocument.getClientBoundary();
		final int dx = rClient.x - hScrollBar.getMin();
		final int dy = rClient.y - vScrollBar.getMin();
		rVista.x = (int) (canvas.x + (dx + scrollableArea.x) * localScale);
		rVista.y = (int) (canvas.y + (dy + scrollableArea.y) * localScale);
		rVista.width = (int) (canvas.width * localScale);
		rVista.height = (int) (canvas.height * localScale);
		return rVista;
	}

	private void onMovedVistaView() {

		if (isDraggingVista()) {
			final int dx = mouseCurrentPos.x - mousePressedAt.x;
			final int dy = mouseCurrentPos.y - mousePressedAt.y;
			hScrollBar.moveThumb(dx, hDragHelper.initialValue,
					(hDragHelper.initialThumbPos + hDragHelper.initialThumbExpansion), -hDragHelper.initialThumbPos);
			vScrollBar.moveThumb(dy, vDragHelper.initialValue,
					(vDragHelper.initialThumbPos + vDragHelper.initialThumbExpansion), -vDragHelper.initialThumbPos);
		}
	}

	private boolean isDraggingVista() {

		return mouseCurrentPos != null;

	}

	private void drawVistaWindow(Graphics2D g) {

		final Rectangle rVista = getVistaWindowRect();
		g.setColor(new Color(255, 255, 255));
		g.fillRoundRect(rVista.x, rVista.y, rVista.width, rVista.height, 6, 6);
		g.setColor(new Color(135, 145, 155));
		g.drawRoundRect(rVista.x, rVista.y, rVista.width, rVista.height, 6, 6);
	}

	private void drawGraph(Graphics2D gfx) {

		final Transform xform = graphDocument.getTransformer();
		final Rectangle graphExBoundary = graphDocument.getGraph().getExtendedBoundary();

		final double origScale = xform.getScale();
		try {
			final double localScale = computeLocalZoom();
			final Rectangle scrollableArea = getScrollableArea();
			final int dx = graphExBoundary.x - hScrollBar.getMin();
			final int dy = graphExBoundary.y - vScrollBar.getMin();
			final int localXPos = (int) ((dx + scrollableArea.x) * localScale);
			final int localYPos = (int) ((dy + scrollableArea.y) * localScale);

			xform.setScale(localScale);

			final DrawingSubject[] subjects = new DrawingSubject[] { DrawingSubject.OBJECT };
			final DrawingContext ctx = new DefaultDrawingContext(Resolution.SCREEN_LOW_DETAIL, subjects);

			final Image img = graphDocument.getImage(ctx);
			gfx.drawImage(img, localXPos, localYPos, null);
		} finally {
			xform.setScale(origScale);
		}
	}

	private void drawHightlightedSection(Graphics2D g) {

		if (rHitSection != null) {
			g.setColor(new Color(255, 255, 255, 120));
			g.fillRect(rHitSection.x + 1, rHitSection.y + 1, rHitSection.width - 2, rHitSection.height - 2);
		}
	}

	private void gotoHitSection() {

		if (rHitSection != null) {

			final Rectangle rVista = getVistaWindowRect();
			final int dx = rHitSection.x - rVista.x;
			final int dy = rHitSection.y - rVista.y;
			hScrollBar.moveThumb(dx, hDragHelper.initialValue,
					(hDragHelper.initialThumbPos + hDragHelper.initialThumbExpansion), -hDragHelper.initialThumbPos);
			vScrollBar.moveThumb(dy, vDragHelper.initialValue,
					(vDragHelper.initialThumbPos + vDragHelper.initialThumbExpansion), -vDragHelper.initialThumbPos);
		}
	}

	private double computeLocalZoom() {

		// compute the required scale
		final int hMin = hScrollBar.getMin();
		final int hMax = hScrollBar.getMax();
		final int vMin = vScrollBar.getMin();
		final int vMax = vScrollBar.getMax();

		final Rectangle scrollableArea = getScrollableArea();
		final double dx = (double) scrollableArea.width / (hMax - hMin);
		final double dy = (double) scrollableArea.height / (vMax - vMin);
		return Math.min(dx, dy) + transiteLcoalScale;
	}

	private void drawSections(Graphics2D g) {

		final Rectangle scrollableArea = getScrollableArea();
		final Rectangle rVista = getVistaWindowRect();

		g.setColor(new Color(156, 180, 200));
		g.setStroke(dashedStroke);
		for (int x = scrollableArea.x; x < scrollableArea.x + scrollableArea.width + 1; x += rVista.width) {
			g.drawLine(x, scrollableArea.y, x, scrollableArea.y + scrollableArea.height);
		}
		for (int y = scrollableArea.y; y < scrollableArea.y + scrollableArea.height + 1; y += rVista.height) {
			g.drawLine(scrollableArea.x, y, scrollableArea.x + scrollableArea.width, y);
		}
		g.setStroke(continuedStroke);
	}

	private Rectangle getHitSection(Point pt) {

		final Rectangle scrollableArea = getScrollableArea();
		final Rectangle rVista = getVistaWindowRect();
		final int col = (pt.x - scrollableArea.x) / (rVista.width);
		final int row = (pt.y - scrollableArea.y) / (rVista.height);

		return new Rectangle(scrollableArea.x + col * rVista.width, scrollableArea.y + row * rVista.height,
				rVista.width, rVista.height);
	}

	private void fadeIn() {

		super.setActive(true);
	}

	private void fadeOut() {

		final Timer timer = new Timer();
		final long startTime = System.currentTimeMillis();
		final long timeOut = 3000;
		final Rectangle rVista = getVistaWindowRect();
		final Rectangle rClient = getCanvasBoundary();
		final int dx = rVista.x - rClient.x;
		final int dy = rVista.y - rClient.y;
		final double alpha = Math.atan2(dy, dx);
		final double distanceToOrigin = Math.sqrt(dx * dx + dy * dy);
		final double totalSteps = 20;//distanceToOrigin / 15;
		final double deltaDist = distanceToOrigin / totalSteps;
		final double initialLocalZoom = computeLocalZoom();
		final double deltaZoom = (graphDocument.getZoom() - initialLocalZoom) / totalSteps;

		timer.schedule(new TimerTask() {

			private double currentStep;

			@Override
			public void run() {

				final long currentTime = System.currentTimeMillis();
				final boolean finalize = currentTime > startTime + timeOut || currentStep++ >= totalSteps;
				if (finalize) {
					Navigator.super.setActive(false);
					timer.cancel();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				} else {
					transiteLcoalScale = currentStep * deltaZoom;
					double rescaleCorrection = (initialLocalZoom + transiteLcoalScale) / initialLocalZoom;
					transiteOffset.x = (int) (currentStep * deltaDist * Math.cos(alpha));
					transiteOffset.y = (int) (currentStep * deltaDist * Math.sin(alpha));
					transiteOffset.x += (rescaleCorrection - 1) * dx;
					transiteOffset.y += (rescaleCorrection - 1) * dy;
				}
				graphDocument.invalidate();
			}
		}, 100, 10);
	}
}
