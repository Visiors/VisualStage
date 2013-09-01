package com.visiors.visualstage.interaction.impl;

import java.awt.Point;

import com.visiors.visualstage.constants.InteractionConstants;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.interaction.Interactable;
import com.visiors.visualstage.interaction.Tool;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;

public abstract class BaseTool implements Tool {

	protected boolean active;
	protected GraphDocument graphDocument;
	protected VisualGraph visualGraph;

	protected BaseTool() {

	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		this.graphDocument = graphDocument;
		this.visualGraph = graphDocument.getGraph();

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

		return InteractionConstants.CURSOR_DEFAULT;
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
	public void paintOnBackground(AWTCanvas awtCanvas, DrawingContext context) {

	}

	@Override
	public void paintOnTop(AWTCanvas awtCanvas, DrawingContext context) {

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
