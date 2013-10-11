package com.visiors.visualstage.tool.impl;

import java.awt.Point;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;

/**
 * This tool must be installed on the top of mouse / keyboard processing chain.
 * By consuming all mouse and keyboard events this class ensures that all other
 * interaction are disabled while this view is visible.
 * 
 * @author Shane
 * 
 */
public class ScrollTool extends BaseTool {

	private final ScrollBar hScrollBar;
	private final ScrollBar vScrollBar;

	public ScrollTool(String name) {

		super(name);
		hScrollBar = new ScrollBar(true);
		vScrollBar = new ScrollBar(false);
	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		super.setScope(graphDocument);
		hScrollBar.setGraphDocument(graphDocument);
		vScrollBar.setGraphDocument(graphDocument);
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		return hScrollBar.mousePressed(pt, button, functionKey) || vScrollBar.mousePressed(pt, button, functionKey);
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return hScrollBar.mouseReleased(pt, button, functionKey) || vScrollBar.mouseReleased(pt, button, functionKey);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return hScrollBar.mouseMoved(pt, button, functionKey) || vScrollBar.mouseMoved(pt, button, functionKey);
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return hScrollBar.mouseDoubleClicked(pt, button, functionKey) || vScrollBar.mouseDoubleClicked(pt, button, functionKey);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		return hScrollBar.mouseDragged(pt, button, functionKey) || vScrollBar.mouseDragged(pt, button, functionKey);
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
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (onTop) {
			hScrollBar.draw(awtCanvas);
			vScrollBar.draw(awtCanvas);
		}
	}

}
