package com.visiors.visualstage.tool.impl;

import java.awt.Point;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.tool.Interactable;

/**
 * 
 * This class simply passes the incoming mouse- and key-events to the graph objects at the mouse position so that
 * graph objects can interact with user. 
 * 
 * @version $Id: $
 */
public class ObjectEditTool extends BaseTool {

	private VisualGraphObject hitObject;

	public ObjectEditTool(String name) {

		super(name);
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		if (hitObject != null) {
			if (hitObject.keyPressed(keyChar, keyCode)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		if (hitObject != null) {
			if (hitObject.keyReleased(keyChar, keyCode)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (hitObject != null) {
			if (hitObject.mousePressed(pt, button, functionKey)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (hitObject != null) {
			if (hitObject.mouseReleased(pt, button, functionKey)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		if (hitObject != null) {
			if (hitObject.mouseDoubleClicked(pt, button, functionKey)) {
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		if (hitObject != null) {
			if (hitObject.mouseMoved(pt, button, functionKey)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (hitObject != null) {
			if (hitObject.mouseDragged(pt, button, functionKey)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getPreferredCursor() {

		if (hitObject != null) {
			return hitObject.getPreferredCursor();
		}
		return Interactable.CURSOR_DEFAULT;
	}
}
