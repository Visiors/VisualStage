package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DefaultOfflineRenderer;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.OffScreenRenderer;
import com.visiors.visualstage.tool.Interactable;

public class ScrollBarCornerButton implements Interactable {

	private final Rectangle bounds = new Rectangle();
	private final OffScreenRenderer offlineRenderer;
	private final Rectangle canvasBoundary = new Rectangle();
	private GraphDocument graphDocument;
	private boolean hit;
	private int size = 16;
	private final Navigator navigator;
	private boolean isButtonToggle;

	public ScrollBarCornerButton(ScrollBar hScrollBar, ScrollBar vScrollBar) {

		this.offlineRenderer = new DefaultOfflineRenderer(new ScrollBarCornerButtonPainter(this));
		this.navigator = new Navigator(hScrollBar, vScrollBar);
	}

	public void setGraphDocument(final GraphDocument graphDocument) {

		this.navigator.setScope(graphDocument);
		this.graphDocument = graphDocument;
		this.navigator.setActive(false);
		this.isButtonToggle = false;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (hit) {
			setToggled(!isToggled());
		}
		return hit || navigator.mousePressed(pt, button, functionKey);
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return hit || navigator.mouseReleased(pt, button, functionKey);
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return hit || navigator.mouseDoubleClicked(pt, button, functionKey);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		return hit || navigator.mouseDragged(pt, button, functionKey);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		final Point ptScreen = graphDocument.getTransformer().transformToScreen(pt);
		final boolean hit = isHit(ptScreen);
		if (this.hit != hit) {
			this.hit = hit;
			offlineRenderer.invalidate();
			graphDocument.invalidate();
		}
		return hit || navigator.mouseMoved(pt, button, functionKey);
	}

	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		return navigator.mouseEntered(pt, button, functionKey);
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		if (hit) {
			hit = false;
			offlineRenderer.invalidate();
			graphDocument.invalidate();
			return true;
		}
		return navigator.mouseExited(pt, button, functionKey);
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		return navigator.keyPressed(keyChar, keyCode);
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		return navigator.keyReleased(keyChar, keyCode);
	}

	@Override
	public boolean isInteracting() {

		return hit || navigator.isInteracting();
	}

	@Override
	public void cancelInteraction() {

		navigator.cancelInteraction();
	}

	@Override
	public void terminateInteraction() {

		navigator.terminateInteraction();
	}

	@Override
	public int getPreferredCursor() {

		return navigator.isActive() ?  navigator.getPreferredCursor() : Interactable.CURSOR_DEFAULT;
	}

	public Rectangle getBounds() {

		return bounds;
	}

	private boolean isHit(Point pt) {

		return bounds.contains(pt);
	}

	public void draw(AWTCanvas awtCanvas, DrawingContext context) {

		if (graphDocument != null) {
			if (isButtonToggle != isToggled()) {
				isButtonToggle = isToggled();
				offlineRenderer.invalidate();
			}
			updateBounds();
			offlineRenderer.render(awtCanvas.gfx);
			navigator.drawHints(awtCanvas, context, true);
		}
	}

	private void updateBounds() {

		final Rectangle r = graphDocument.getClientBoundary();
		if (!r.equals(canvasBoundary)) {
			canvasBoundary.setBounds(r);
			bounds.setBounds(canvasBoundary.x + canvasBoundary.width, canvasBoundary.y + canvasBoundary.height, size,
					size);
			offlineRenderer.invalidate();
		}
	}

	public boolean isHovered() {

		return hit;
	}

	public void setSize(int size) {

		this.size = size;

	}

	private void setToggled(boolean b) {

		if (this.navigator.isActive() != b) {
			this.navigator.setActive(b);
			offlineRenderer.invalidate();
			graphDocument.invalidate();
		}
	}

	public boolean isToggled() {

		return this.navigator.isActive();
	}

}
