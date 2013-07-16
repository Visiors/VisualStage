package com.visiors.visualstage.stage.interaction.impl.readonlymode;

import java.awt.Point;

import com.visiors.visualstage.constants.GraphStageConstants;
import com.visiors.visualstage.stage.cache.GraphObjectView;
import com.visiors.visualstage.stage.interaction.impl.BaseInteractionHandler;

public class ReadOnlyMode extends BaseInteractionHandler {

	public ReadOnlyMode() {

		super();
	}

	@Override
	public String getName() {

		return GraphStageConstants.MODE_READ_ONLY;
	}

	@Override
	public void setActive(boolean activated) {

		super.setActive(activated);

		unselectGraph(graphView);
	}

	private void unselectGraph(VisualGraph gv) {

		VisualGraphObject[] objects = gv.getGraphObjects();
		for (int i = 0; i < objects.length; i++) {
			objects[i].setSelected(false);
			if (objects[i] instanceof VisualGraph) {
				unselectGraph((VisualGraph) objects[i]);
			}
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
