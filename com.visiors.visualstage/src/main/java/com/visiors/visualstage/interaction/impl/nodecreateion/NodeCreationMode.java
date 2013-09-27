package com.visiors.visualstage.interaction.impl.nodecreateion;

import java.awt.Point;

import com.visiors.visualstage.constants.Interactable;
import com.visiors.visualstage.tool.impl.BaseTool;

public class NodeCreationMode extends BaseTool {

	public NodeCreationMode(String name) {

		super(name);
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
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public int getPreferredCursor() {

		return Interactable.CURSOR_DEFAULT;
	}
}
