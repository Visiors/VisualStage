package com.visiors.visualstage.tool.impl;

import java.awt.Point;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.impl.scrollbar.ScrollBar;
import com.visiors.visualstage.tool.impl.scrollbar.ScrollBarCornerButton;

/**
 * This tool must be installed on the top of mouse / keyboard processing chain.
 * By consuming all mouse and keyboard events this class ensures that all other
 * interaction are disabled while this view is visible.
 * 
 * @author Shane
 * 
 */
public class ScrollTool extends BaseTool {

	private int size;
	private final ScrollBar hScrollBar;
	private final ScrollBar vScrollBar;
	private final ScrollBarCornerButton scrButton;

	public ScrollTool() {

		super("SCROLLBAR");
		hScrollBar = new ScrollBar(true);
		vScrollBar = new ScrollBar(false);
		scrButton = new ScrollBarCornerButton();
	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		super.setScope(graphDocument);
		hScrollBar.setGraphDocument(graphDocument);
		vScrollBar.setGraphDocument(graphDocument);
		scrButton.setGraphDocument(graphDocument);
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		return scrButton.mousePressed(pt, button, functionKey) || hScrollBar.mousePressed(pt, button, functionKey)
				|| vScrollBar.mousePressed(pt, button, functionKey);
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return scrButton.mouseReleased(pt, button, functionKey) || hScrollBar.mouseReleased(pt, button, functionKey)
				|| vScrollBar.mouseReleased(pt, button, functionKey);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return scrButton.mouseMoved(pt, button, functionKey) || hScrollBar.mouseMoved(pt, button, functionKey)
				|| vScrollBar.mouseMoved(pt, button, functionKey);
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return scrButton.mouseDoubleClicked(pt, button, functionKey)
				|| hScrollBar.mouseDoubleClicked(pt, button, functionKey)
				|| vScrollBar.mouseDoubleClicked(pt, button, functionKey);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		return scrButton.mouseDragged(pt, button, functionKey) || hScrollBar.mouseDragged(pt, button, functionKey)
				|| vScrollBar.mouseDragged(pt, button, functionKey);
	}

	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		return scrButton.mouseEntered(pt, button, functionKey) || hScrollBar.mouseEntered(pt, button, functionKey)
				|| vScrollBar.mouseEntered(pt, button, functionKey);
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		return scrButton.mouseExited(pt, button, functionKey) || hScrollBar.mouseExited(pt, button, functionKey)
				|| vScrollBar.mouseExited(pt, button, functionKey);
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
	public int getPreferredCursor() {

		int hCursor = hScrollBar.getPreferredCursor();
		int vCursor = vScrollBar.getPreferredCursor();
		return (hCursor != Interactable.CURSOR_DEFAULT ? hCursor : vCursor);
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (onTop) {
			hScrollBar.draw(awtCanvas);
			vScrollBar.draw(awtCanvas);
			scrButton.draw(awtCanvas);
		}
	}

	public void setSize(int size) {

		this.size = size;
		hScrollBar.setSize(size);
		vScrollBar.setSize(size);
		scrButton.setSize(size);
	}

	public int getSize() {

		return size;
	}
}
