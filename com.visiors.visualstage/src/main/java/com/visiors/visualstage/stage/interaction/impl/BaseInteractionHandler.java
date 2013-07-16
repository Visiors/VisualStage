package com.visiors.visualstage.stage.interaction.impl;

import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.constants.GraphStageConstants;
import com.visiors.visualstage.stage.interaction.Interactable;
import com.visiors.visualstage.stage.interaction.InteractionMode;

public abstract class BaseInteractionHandler implements InteractionMode {

	protected boolean active;
	protected VisualGraph graphView;

	protected BaseInteractionHandler() {

	}

	@Override
	public void setScope(VisualGraph graphView) {

		this.graphView = graphView;

	}

	@Override
	public abstract String getName();

	@Override
	public void setActive(boolean activated) {

		active = activated;
	}

	@Override
	public boolean isActive() {

		return active;
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

	@Override
	public boolean isInteracting() {

		return false;
	}

	@Override
	public void cancelInteraction() {

	}

	@Override
	public void terminateInteraction() {

	}

	@Override
	public void paintOnBackground(Device device, Rectangle visibleScreenRect) {

	}

	@Override
	public void paintOnTop(Device device, Rectangle visibleScreenRect) {

	}

	protected boolean isControlKeyPressed(int key) {

		return (key & Interactable.KEY_CONTROL) != 0;
	}

	protected boolean isShiftKeyPressed(int key) {

		return (key & Interactable.KEY_SHIFT) != 0;
	}

	protected boolean isAltKeyPressed(int key) {

		return (key & Interactable.KEY_ALT) != 0;
	}

}
