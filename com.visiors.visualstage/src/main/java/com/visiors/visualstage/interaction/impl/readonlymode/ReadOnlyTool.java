package com.visiors.visualstage.interaction.impl.readonlymode;

import java.awt.Point;

import com.visiors.visualstage.interaction.impl.BaseTool;

public class ReadOnlyTool extends BaseTool {

	public ReadOnlyTool(String name) {

		super(name);
	}



	@Override
	public void setActive(boolean activated) {

		super.setActive(activated);

		unselectGraph();
	}

	private void unselectGraph() {

		if(graphDocument != null) {
			graphDocument.getGraph().clearSelection();
		}
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

}
