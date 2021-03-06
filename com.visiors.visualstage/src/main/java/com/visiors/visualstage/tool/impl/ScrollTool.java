package com.visiors.visualstage.tool.impl;

import java.awt.Point;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.impl.scrollbar.MouseScroller;
import com.visiors.visualstage.tool.impl.scrollbar.ScrollBar;
import com.visiors.visualstage.tool.impl.scrollbar.ScrollBarCornerButton;
import com.visiors.visualstage.tool.impl.scrollbar.StageStyleConstants;

/**
 * This tool must be installed on the top of mouse / keyboard processing chain.
 * By consuming all mouse and keyboard events this class ensures that all other
 * interaction are disabled while this view is visible.
 * 
 * @author Shane
 * 
 */
public class ScrollTool extends BaseTool {

	private int size = StageStyleConstants.scrollbar_defaultSize;
	private final ScrollBar hScrollBar;
	private final ScrollBar vScrollBar;
	private final ScrollBarCornerButton scrButton;
	private final MouseScroller autoScroller;

	public ScrollTool() {

		super("SCROLLBAR");
		hScrollBar = new ScrollBar(true, "HSCROLLBAR");
		vScrollBar = new ScrollBar(false, "VSCROLLBAR");
		scrButton = new ScrollBarCornerButton(hScrollBar, vScrollBar, "CORNERBUTTON");
		autoScroller = new MouseScroller(hScrollBar, vScrollBar);

		autoScroller.setActive(true);
	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		super.setScope(graphDocument);
		hScrollBar.setScope(graphDocument);
		vScrollBar.setScope(graphDocument);
		scrButton.setScope(graphDocument);
		autoScroller.setScope(graphDocument);
	}

	public void setAutoMouseScroll(boolean active) {

		autoScroller.setActive(active);
	}

	public boolean isAutoMouseScroll() {

		return autoScroller.isActive();
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		return scrButton.mousePressed(pt, button, functionKey) || hScrollBar.mousePressed(pt, button, functionKey)
				|| vScrollBar.mousePressed(pt, button, functionKey)
				|| autoScroller.mousePressed(pt, button, functionKey);
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return scrButton.mouseReleased(pt, button, functionKey) || hScrollBar.mouseReleased(pt, button, functionKey)
				|| vScrollBar.mouseReleased(pt, button, functionKey)
				|| autoScroller.mouseReleased(pt, button, functionKey);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return scrButton.mouseMoved(pt, button, functionKey) || hScrollBar.mouseMoved(pt, button, functionKey)
				|| vScrollBar.mouseMoved(pt, button, functionKey) || autoScroller.mouseMoved(pt, button, functionKey);
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return scrButton.mouseDoubleClicked(pt, button, functionKey)
				|| hScrollBar.mouseDoubleClicked(pt, button, functionKey)
				|| vScrollBar.mouseDoubleClicked(pt, button, functionKey)
				|| autoScroller.mouseDoubleClicked(pt, button, functionKey);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		return scrButton.mouseDragged(pt, button, functionKey) || hScrollBar.mouseDragged(pt, button, functionKey)
				|| vScrollBar.mouseDragged(pt, button, functionKey)
				|| autoScroller.mouseDragged(pt, button, functionKey);
	}

	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		return scrButton.mouseEntered(pt, button, functionKey) || hScrollBar.mouseEntered(pt, button, functionKey)
				|| vScrollBar.mouseEntered(pt, button, functionKey)
				|| autoScroller.mouseEntered(pt, button, functionKey);
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		return scrButton.mouseExited(pt, button, functionKey) || hScrollBar.mouseExited(pt, button, functionKey)
				|| vScrollBar.mouseExited(pt, button, functionKey) || autoScroller.mouseExited(pt, button, functionKey);
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		return scrButton.keyPressed(keyChar, keyCode) || hScrollBar.keyPressed(keyChar, keyCode)
				|| vScrollBar.keyPressed(keyChar, keyCode) || autoScroller.keyPressed(keyChar, keyCode);
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		return scrButton.keyReleased(keyChar, keyCode) || hScrollBar.keyReleased(keyChar, keyCode)
				|| vScrollBar.keyReleased(keyChar, keyCode) || autoScroller.keyReleased(keyChar, keyCode);
	}

	@Override
	public int getPreferredCursor() {

		int cursor = hScrollBar.getPreferredCursor();
		if (cursor != Interactable.CURSOR_DEFAULT) {
			return cursor;
		}
		cursor = vScrollBar.getPreferredCursor();
		if (cursor != Interactable.CURSOR_DEFAULT) {
			return cursor;
		}
		cursor = scrButton.getPreferredCursor();
		if (cursor != Interactable.CURSOR_DEFAULT) {
			return cursor;
		}

		return Interactable.CURSOR_DEFAULT;
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (onTop) {
			hScrollBar.draw(awtCanvas);
			vScrollBar.draw(awtCanvas);
			scrButton.draw(awtCanvas, context);
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
