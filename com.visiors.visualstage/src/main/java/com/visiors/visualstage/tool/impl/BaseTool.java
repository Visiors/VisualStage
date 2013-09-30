package com.visiors.visualstage.tool.impl;

import java.awt.Point;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.Tool;

public class BaseTool implements Tool {

	protected boolean active;
	protected GraphDocument graphDocument;
	protected VisualGraph visualGraph;
	private final String name;

	protected BaseTool(String name) {

		this.name = name;

	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		this.graphDocument = graphDocument;
		this.visualGraph = graphDocument.getGraph();

	}

	@Override
	public String getName() {

		return this.name;
	}

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

		return Interactable.CURSOR_DEFAULT;
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
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

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

	protected boolean isFunctionKeyPressed(int key) {
		return key != 0;
	}

}
