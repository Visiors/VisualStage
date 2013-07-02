package com.visiors.visualstage.stage.interaction.impl.nodecreateion;

import java.awt.Point;

import com.visiors.visualstage.constants.GraphStageConstants;
import com.visiors.visualstage.stage.interaction.impl.BaseInteractionHandler;

public class NodeCreationMode extends BaseInteractionHandler {

	public NodeCreationMode() {

		super();
	}

	@Override
	public String getName() {

		return GraphStageConstants.MODE_NODE_CREATION;
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

		return GraphStageConstants.CURSOR_DEFAULT;
	}
}
