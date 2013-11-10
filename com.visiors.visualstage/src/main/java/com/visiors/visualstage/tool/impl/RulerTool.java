package com.visiors.visualstage.tool.impl;

import java.awt.Point;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.impl.ruler.CornerButton;
import com.visiors.visualstage.tool.impl.ruler.Ruler;

public class RulerTool extends BaseTool {

	private int size = 16;
	private final Ruler hRuler;
	private final Ruler vRuler;
	private final CornerButton cornerButton;

	public RulerTool() {

		super("RULER");
		cornerButton = new CornerButton();
		hRuler = new Ruler(true);
		vRuler = new Ruler(false);
	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		super.setScope(graphDocument);
		hRuler.setScope(graphDocument);
		vRuler.setScope(graphDocument);
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		return hRuler.mousePressed(pt, button, functionKey) || vRuler.mousePressed(pt, button, functionKey);
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return hRuler.mouseReleased(pt, button, functionKey) || vRuler.mouseReleased(pt, button, functionKey);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return hRuler.mouseMoved(pt, button, functionKey) || vRuler.mouseMoved(pt, button, functionKey);
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return hRuler.mouseDoubleClicked(pt, button, functionKey) || vRuler.mouseDoubleClicked(pt, button, functionKey);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		return hRuler.mouseDragged(pt, button, functionKey) || vRuler.mouseDragged(pt, button, functionKey);
	}

	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		return hRuler.mouseEntered(pt, button, functionKey) || vRuler.mouseEntered(pt, button, functionKey);
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		return hRuler.mouseExited(pt, button, functionKey) || vRuler.mouseExited(pt, button, functionKey);
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

		int hCursor = hRuler.getPreferredCursor();
		int vCursor = vRuler.getPreferredCursor();
		return (hCursor != Interactable.CURSOR_DEFAULT ? hCursor : vCursor);
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (onTop) {
			hRuler.draw(awtCanvas);
			vRuler.draw(awtCanvas);
			cornerButton.draw(awtCanvas);

		}

	}

	public void setSize(int size) {

		this.size = size;
		hRuler.setSize(size);
		vRuler.setSize(size);
		cornerButton.setSize(size);
	}

	public int getSize() {

		return size;
	}

}
