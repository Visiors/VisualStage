package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import com.google.common.base.Objects;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DefaultDrawingContext;
import com.visiors.visualstage.renderer.DefaultOfflineRenderer;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.OffScreenRenderer;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.renderer.effect.BlendEffect;
import com.visiors.visualstage.renderer.effect.DefaultEffectBatchProcessor;
import com.visiors.visualstage.renderer.effect.Effect;
import com.visiors.visualstage.renderer.effect.EffectBatchProcessor;
import com.visiors.visualstage.renderer.effect.EffectListener;
import com.visiors.visualstage.renderer.effect.TransformEffect;
import com.visiors.visualstage.renderer.effect.ViewProvider;
import com.visiors.visualstage.stage.StageDesigner;
import com.visiors.visualstage.stage.StageDesigner.ViewMode;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.impl.BaseTool;
import com.visiors.visualstage.tool.impl.PageLayoutViewTool;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.transform.TransformPreserver;

/**
 * This tool must be installed on the top of mouse / keyboard processing chain.
 * By consuming all mouse and keyboard events this class ensures that all other
 * interaction are disabled while this view is visible.
 * 
 * @author Shane
 * 
 */
public class Navigator extends BaseTool implements ViewProvider {

	private static Stroke sectionDividerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 1, 3 }, 0);
	private static Stroke continuedStroke = new BasicStroke(1.0f);
	private static Stroke vistaFrameStroke = new BasicStroke(1.0f);
	private static Stroke vistaFrameShadowStroke = new BasicStroke(4.0f);
	private Point mousePressedAt;
	private Rectangle rHitSection;

	private boolean autoClose;
	private final ScrollBar hScrollBar;
	private final ScrollBar vScrollBar;
	private Point mouseCurrentPos;
	private final DragHelper hDragHelper;
	private final DragHelper vDragHelper;
	protected Image canvasImage;
	private boolean hoverVista;
	private double transitionCurrentScale;
	private final Point transitionCurrentOffset = new Point();
	private boolean takeSnapshot;
	private DrawingContext context;
	private boolean activated;
	private final OffScreenRenderer graphRenderer;

	public Navigator(ScrollBar hScrollBar, ScrollBar vScrollBar) {

		super("NAVIGATOR");
		this.hScrollBar = hScrollBar;
		this.vScrollBar = vScrollBar;
		this.hDragHelper = new DragHelper(hScrollBar);
		this.vDragHelper = new DragHelper(vScrollBar);
		this.graphRenderer = new DefaultOfflineRenderer(new NavigatorGraphPainter(this));
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
			if (isAutoClose()) {
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
		hoverVista = false;
		if (hitVistaWindow(ptScreen)) {
			hoverVista = true;
		} else if (hitScrollableArea(ptScreen)) {
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

		return hoverVista ? Interactable.CURSOR_MOVE : Interactable.CURSOR_DEFAULT;
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
	public synchronized void setActive(boolean active) {

		if (active != activated) {
			if (active) {
				activated = true;
				fadeIn();
			} else {
				activated = false;
				fadeOut();
			}
		}
	}

	Rectangle getCanvasBoundary() {

		return hScrollBar.getCanvasBoundary();
	}

	private Rectangle getScrollableArea() {

		final Rectangle hPageArea = hScrollBar.getPageAreaRect();
		final Rectangle vPageArea = vScrollBar.getPageAreaRect();

		final int hMin = hScrollBar.getMin();
		final int hMax = hScrollBar.getMax();
		final int vMin = vScrollBar.getMin();
		final int vMax = vScrollBar.getMax();

		final double dxf = (double) hPageArea.width / (hMax - hMin);
		final double dyf = (double) vPageArea.height / (vMax - vMin);
		final double f = dxf / dyf;

		if (f < 1.0) {
			vPageArea.y += vPageArea.height / 2 * (1.0 - f);
			vPageArea.height *= f;

		} else if (f > 1.0) {
			vPageArea.x += vPageArea.width / 2 * (1.0 - f);
			vPageArea.width *= f;
		}

		return new Rectangle(hPageArea.x, vPageArea.y, hPageArea.width, vPageArea.height);
	}

	private double[] computeTranslationFactor() {

		final Rectangle hPageArea = hScrollBar.getPageAreaRect();
		final Rectangle vPageArea = vScrollBar.getPageAreaRect();
		final Rectangle rLocal = getScrollableArea();
		return new double[] { (double) hPageArea.width / rLocal.width, (double) vPageArea.height / rLocal.height };
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
		return computeVistaWindowRect().contains(pt);
	}

	private Rectangle computeVistaWindowRect() {

		final Transform xform = graphDocument.getTransformer();
		final double localScale = computeLocalZoom() + transitionCurrentScale;
		final Point ptNavigator = viewToNavigator(new Point(0, 0));
		final Rectangle rVista = new Rectangle();
		rVista.setLocation(ptNavigator);
		final Rectangle canvas = getCanvasBoundary();
		rVista.width = (int) (canvas.width * localScale / xform.getScale());
		rVista.height = (int) (canvas.height * localScale / xform.getScale());
		return rVista;
	}

	private void onMovedVistaView() {

		if (isDraggingVista()) {
			final double[] fxTranslation = computeTranslationFactor();
			final double dx = (mouseCurrentPos.x - mousePressedAt.x) * fxTranslation[0];
			final double dy = (mouseCurrentPos.y - mousePressedAt.y) * fxTranslation[1];
			hScrollBar.moveThumb((int) dx, hDragHelper.initialValue,
					(hDragHelper.initialThumbPos + hDragHelper.initialThumbExpansion), -hDragHelper.initialThumbPos);
			vScrollBar.moveThumb((int) dy, vDragHelper.initialValue,
					(vDragHelper.initialThumbPos + vDragHelper.initialThumbExpansion), -vDragHelper.initialThumbPos);
		}
	}

	private void gotoHitSection() {

		if (rHitSection != null) {
			final double[] fxTranslation = computeTranslationFactor();
			final Rectangle rVista = computeVistaWindowRect();
			final int dx = (int) ((rHitSection.x - rVista.x) * fxTranslation[0]);
			final int dy = (int) ((rHitSection.y - rVista.y) * fxTranslation[1]);

			hScrollBar.moveThumb(dx, hDragHelper.initialValue,
					(hDragHelper.initialThumbPos + hDragHelper.initialThumbExpansion), -hDragHelper.initialThumbPos);
			vScrollBar.moveThumb(dy, vDragHelper.initialValue,
					(vDragHelper.initialThumbPos + vDragHelper.initialThumbExpansion), -vDragHelper.initialThumbPos);
		}
	}

	private double computeLocalZoom() {

		final Transform xform = graphDocument.getTransformer();
		// compute the required scale
		final int hMin = hScrollBar.getMin();
		final int hMax = hScrollBar.getMax();
		final int vMin = vScrollBar.getMin();
		final int vMax = vScrollBar.getMax();
		final Rectangle scrollableArea = getScrollableArea();
		final double dx = (double) scrollableArea.width / (hMax - hMin);
		final double dy = (double) scrollableArea.height / (vMax - vMin);
		return Math.min(dx, dy) * xform.getScale();
	}

	private boolean isDraggingVista() {

		return mouseCurrentPos != null;
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		this.context = context;

		if (onTop && active && !takeSnapshot) {
			final Rectangle clip = awtCanvas.gfx.getClipBounds();
			try {
				final Rectangle canvasBoundary = getCanvasBoundary();
				awtCanvas.gfx.setClip(canvasBoundary);
				if (canvasImage != null) {
					awtCanvas.gfx.drawImage(canvasImage, 0, 0, null);
				} else {
					drawComponents(awtCanvas.gfx, true);
				}
			} finally {
				awtCanvas.gfx.setClip(clip);
			}
		}
	}

	private void drawComponents(Graphics2D gfx, boolean useCache) {

		try {
			gfx.translate(-transitionCurrentOffset.x, -transitionCurrentOffset.y);
			drawCanvas(gfx);
			drawSections(gfx);
			if (useCache) {
				graphRenderer.render(gfx);
			} else {
				drawGraph(gfx);
				graphRenderer.invalidate();
			}
			drawFilter(gfx);
			// drawHightlightedSection(gfx);
			drawVistaWindowFrame(gfx);
		} finally {
			gfx.translate(transitionCurrentOffset.x, transitionCurrentOffset.y);
		}
	}

	private void drawFilter(Graphics2D gfx) {

		final Rectangle rClient = getCanvasBoundary();
		final Rectangle rVista = computeVistaWindowRect();
		gfx.setColor(StageStyleConstants.navigator_filterColorOutsideViewport);
		gfx.fillRect(rClient.x, rClient.y, rVista.x - rClient.x, rClient.height);
		gfx.fillRect(rVista.x + rVista.width, rClient.y, rClient.x + rClient.width - rVista.x - rClient.x,
				rClient.height);
		gfx.fillRect(rVista.x, rClient.y, rVista.width, rVista.y - rClient.y);
		gfx.fillRect(rVista.x, rVista.y + rVista.height, rVista.width, rClient.y + rClient.height - rVista.y
				- rVista.height);
	}

	private void drawCanvas(Graphics2D gfx) {

		final Rectangle canvasBoundary = getCanvasBoundary();
		final Rectangle scrollableArea = getScrollableArea();
		canvasBoundary.translate(transitionCurrentOffset.x, transitionCurrentOffset.y);
		scrollableArea.translate(transitionCurrentOffset.x, transitionCurrentOffset.y);
		// TODO graphDocument.getEditor().getStageDesigner().getBackground
		gfx.setColor(new Color(206, 216, 231));

		gfx.fillRect(canvasBoundary.x, canvasBoundary.y, canvasBoundary.width, canvasBoundary.height);
		// gfx.setColor(new Color(241, 243, 250));
		// gfx.fillRect(scrollableArea.x, scrollableArea.y,
		// scrollableArea.width, scrollableArea.height);
	}

	private void drawVistaWindowFrame(Graphics2D g) {

		final Rectangle rVista = computeVistaWindowRect();

		if (hoverVista) {
			g.setColor(StageStyleConstants.navigator_vistaWindowArmedFrameShadowColor);
			g.setStroke(vistaFrameShadowStroke);
			g.drawRoundRect(rVista.x - 1, rVista.y - 1, rVista.width + 2, rVista.height + 2, 3, 3);
			g.setColor(StageStyleConstants.navigator_vistaWindowArmedFrameColor);
			g.setStroke(vistaFrameStroke);
			g.drawRoundRect(rVista.x, rVista.y, rVista.width, rVista.height, 3, 3);
			g.setStroke(continuedStroke);
		} else {
			g.setColor(StageStyleConstants.navigator_vistaWindowFrameShadowColor);
			g.setStroke(vistaFrameShadowStroke);
			g.drawRoundRect(rVista.x - 2, rVista.y - 2, rVista.width + 4, rVista.height + 4, 3, 3);
			g.setColor(StageStyleConstants.navigator_vistaWindowFrameColor);
			g.setStroke(vistaFrameStroke);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setStroke(new BasicStroke(1.0f));
			g.drawRect(rVista.x, rVista.y, rVista.width, rVista.height);
			g.setStroke(continuedStroke);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}

	void drawGraph(Graphics2D gfx) {

		final Image imgGraph;
		final Transform xform = graphDocument.getTransformer();
		final double localScale = computeLocalZoom() + transitionCurrentScale;
		final DrawingSubject[] subjects = new DrawingSubject[] { DrawingSubject.OBJECT };
		final DrawingContext ctx = new DefaultDrawingContext(Resolution.SCREEN_LOW_DETAIL, subjects);

		// calculate the position of the graph on the navigator view
		final Rectangle rClient = getCanvasBoundary();
		final Rectangle graphExBoundary = graphDocument.getGraph().getExtendedBoundary();
		final Point ptGraph = graphExBoundary.getLocation();
		ptGraph.translate(-rClient.x, -rClient.y);
		final Point ptGraphTopLeft = viewToNavigator(ptGraph);

		// calculate the position of the pages on the navigator view
		final PageLayoutViewTool pageLayoutPainter = new PageLayoutViewTool();
		pageLayoutPainter.setScope(graphDocument);
		final Rectangle rPage = pageLayoutPainter.computePageRect(xform);
		final Point ptPage = rPage.getLocation();
		ptPage.translate(-rClient.x, -rClient.y);
		final Point ptPageTopLeft = viewToNavigator(ptPage);

		// adjust zoom
		final TransformPreserver preserver = new TransformPreserver(xform);
		preserver.store().resetTranslate().setScale(localScale);
		try {
			// - create graph snapshot
			imgGraph = graphDocument.getImage(ctx);
			// - draw print page layout
			final StageDesigner sd = graphDocument.getEditor().getStageDesigner();
			if (sd.getViewMode() == ViewMode.draft) {
				// compute the page size for the current zoom
				final Rectangle rPagePreview = pageLayoutPainter.computePageRect(xform);
				final AWTCanvas canvas = new AWTCanvas(rPagePreview.width + rClient.x + 5, rPagePreview.height
						+ rClient.y + 5);
				preserver.setTranslate(-rPagePreview.x + rClient.x, -rPagePreview.y + rClient.y);
				pageLayoutPainter.drawHints(canvas, ctx, false);
				gfx.drawImage(canvas.getImage(), ptPageTopLeft.x - rClient.x, ptPageTopLeft.y - rClient.y, null);
			}
		} finally {
			preserver.restore();
		}
		gfx.drawImage(imgGraph, ptGraphTopLeft.x - rClient.x, ptGraphTopLeft.y - rClient.y, null);
	}

	private Point viewToNavigator(Point ptView) {

		final Transform xform = graphDocument.getTransformer();
		final Rectangle scrollableArea = getScrollableArea();
		final double localScale = computeLocalZoom();
		int x = (int) (scrollableArea.x + (ptView.x - hScrollBar.getMin()) * localScale / xform.getScale());
		int y = (int) (scrollableArea.y + (ptView.y - vScrollBar.getMin()) * localScale / xform.getScale());
		x += (int) (ptView.x * (transitionCurrentScale) / xform.getScale());
		y += (int) (ptView.y * (transitionCurrentScale) / xform.getScale());
		return new Point(x, y);
	}

	private void drawHightlightedSection(Graphics2D g) {

		if (rHitSection != null) {
			final Rectangle scrollableArea = getScrollableArea();
			g.setColor(new Color(255, 255, 255, 60));
			final Rectangle r = scrollableArea.intersection(rHitSection);
			g.fillRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
		}
	}

	private void drawSections(Graphics2D g) {

		final Rectangle scrollableArea = getScrollableArea();
		final Rectangle rVista = computeVistaWindowRect();

		g.setColor(new Color(150, 150, 200));
		g.setStroke(sectionDividerStroke);
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
		final Rectangle rVista = computeVistaWindowRect();
		final int col = (pt.x - scrollableArea.x) / (rVista.width);
		final int row = (pt.y - scrollableArea.y) / (rVista.height);

		return new Rectangle(scrollableArea.x + col * rVista.width, scrollableArea.y + row * rVista.height,
				rVista.width, rVista.height);
	}

	private void fadeIn() {

		final int imageNum = 10;
		final TransformEffect transformEffect = new TransformEffect(this);
		final BufferedImage[] slides = createTransitionSlides(imageNum, false);
		transformEffect.setSlides(slides);
		transformEffect.setTimeInterval(30);

		final BlendEffect blendEffect = new BlendEffect(this);
		blendEffect.setOverlayImage(getBackgroundSnapshot());
		blendEffect.setBackgroundImage(slides[0]);
		blendEffect.setStartWeight(0.0);
		blendEffect.setEndWeight(1.0);
		blendEffect.setSteps(15);
		blendEffect.setTimeInterval(20);

		final EffectBatchProcessor effectProcessor = new DefaultEffectBatchProcessor();
		effectProcessor.perform(blendEffect, transformEffect);
		effectProcessor.addListener(new EffectListener() {

			@Override
			public void startProcessing(Effect effect) {

			}

			@Override
			public void finishedProcessing(Effect effect) {

				if (effect instanceof TransformEffect) {
					canvasImage = null;
					transitionCurrentScale = 0;
					transitionCurrentOffset.setLocation(0, 0);
					canvasImage = null;
					rHitSection = null;
					graphDocument.invalidate();
				}
			}
		});
		super.setActive(true);
	}

	private void fadeOut() {

		final int imageNum = 10;
		final TransformEffect transformEffect = new TransformEffect(this);
		final BufferedImage[] slides = createTransitionSlides(imageNum, true);
		transformEffect.setSlides(slides);
		transformEffect.setTimeInterval(30);

		final BlendEffect blendEffect = new BlendEffect(this);
		blendEffect.setOverlayImage(slides[imageNum]);
		blendEffect.setBackgroundImage(getBackgroundSnapshot());
		blendEffect.setStartWeight(0.0);
		blendEffect.setEndWeight(1.0);
		blendEffect.setSteps(15);
		blendEffect.setTimeInterval(30);

		final EffectBatchProcessor effectProcessor = new DefaultEffectBatchProcessor();
		effectProcessor.perform(transformEffect, blendEffect);
		effectProcessor.addListener(new EffectListener() {

			@Override
			public void startProcessing(Effect effect) {

			}

			@Override
			public void finishedProcessing(Effect effect) {

				if (effect instanceof BlendEffect) {
					Navigator.super.setActive(false);
					canvasImage = null;
					transitionCurrentScale = 0;
					transitionCurrentOffset.setLocation(0, 0);
					canvasImage = null;
					rHitSection = null;
					graphDocument.invalidate();
				}
			}
		});
	}

	private BufferedImage[] createTransitionSlides(int totalIteration, boolean zoomOut) {

		final Rectangle rVista = computeVistaWindowRect();
		final Rectangle rClient = getCanvasBoundary();
		final double dx = rVista.x - rClient.x;
		final double dy = rVista.y - rClient.y;
		final double alpha = Math.atan2(dy, dx);
		final double distanceToOrigin = Math.sqrt(dx * dx + dy * dy);
		final double distChangePerIteration = distanceToOrigin / totalIteration;
		final double initialLocalZoom = computeLocalZoom();
		final double zoomChangePerIteration = (graphDocument.getZoom() - initialLocalZoom) / totalIteration;
		final BufferedImage[] images = new BufferedImage[totalIteration + 1];

		for (int it = 0; it <= totalIteration; it++) {

			final int step = zoomOut ? it : totalIteration - it;
			// calculate and the transition scale
			transitionCurrentScale = step * zoomChangePerIteration;
			// calculate transition offset
			final double distanceToOrig = step * distChangePerIteration;
			final double xDistanceToOrig = distanceToOrig * Math.cos(alpha);
			final double yDistanceToOrig = distanceToOrig * Math.sin(alpha);
			transitionCurrentOffset.x = (int) Math.round(xDistanceToOrig);
			transitionCurrentOffset.y = (int) Math.round(yDistanceToOrig);
			// create image of view for this step
			images[it] = new BufferedImage(rClient.x + rClient.width, rClient.y + rClient.height,
					BufferedImage.TYPE_INT_ARGB_PRE);
			final Graphics2D gfxImage = (Graphics2D) images[it].getGraphics();
			drawComponents(gfxImage, false);
		}
		return images;
	}

	private Image getBackgroundSnapshot() {

		takeSnapshot = true;
		try {
			return graphDocument.getScreen(context);
		} finally {
			takeSnapshot = false;
		}
	}

	@Override
	public void paintScreen(Image image) {

		canvasImage = image;
		graphDocument.invalidate();
	}
}