package com.visiors.visualstage.tool.impl;

import java.awt.Point;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.editor.Editor;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.pool.GraphBuilder;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.transform.Transform;

/**
 * This tool must be installed on the top of mouse / keyboard processing chain.
 * By consuming all mouse and keyboard events this class ensures that all other
 * interaction are disabled while this view is visible.
 * 
 * @author Shane
 * 
 */

public class DragAndDropTool extends BaseTool {

	private static final String SEPARATOR = "#";
	private VisualGraphObject vgo;
	@Inject
	protected UndoRedoHandler undoRedoHandler;
	private int bookmarkID;
	private boolean activateAutoMouseScroller;
	private boolean objectDropped;

	public DragAndDropTool() {

		super("DRAGANDDROP");
		DI.injectMembers(this);
	}

	@Override
	public boolean onDragEntered(Point pt, String data) {

		boolean consume = false;
		if (isValidDragObject(data)) {
			bookmarkID = undoRedoHandler.setBookmark();
			undoRedoHandler.stratOfGroupAction();
			vgo = createDraggedObject(pt, data);
			graphDocument.getEditor().mousePressed(getGripPoint(vgo), BUTTON_LEFT, 0);
			consume = true;
		}
		return consume;
	}

	@Override
	public boolean onDragExited(Point pt, String data) {

		if (vgo != null) {
			final Editor editor = graphDocument.getEditor();
			editor.mouseReleased(pt, BUTTON_LEFT, 0);
			undoRedoHandler.endOfGroupAction();
			if (!objectDropped) {
				visualGraph.remove(vgo);
				undoRedoHandler.clearHistory(bookmarkID);
			}
			editor.getStageDesigner().setAutoMouseScroll(activateAutoMouseScroller);
			vgo = null;
			activateAutoMouseScroller = false;
			objectDropped = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean onDragOver(Point pt, String data) {

		final boolean consume = vgo != null;
		if (consume) {
			final Editor editor = graphDocument.getEditor();
			if (editor.getStageDesigner().isAutoMouseScroll()) {
				editor.getStageDesigner().setAutoMouseScroll(false);
				activateAutoMouseScroller = true;
			}
			editor.mouseDragged(pt, BUTTON_LEFT, 0);
		}
		return consume;
	}

	@Override
	public boolean onDragDropped(Point pt, String data) {

		final boolean consume = vgo != null;
		if (consume) {
			objectDropped = true;
		}
		return consume;
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

	}

	private boolean isValidDragObject(String data) {

		if (!Strings.isNullOrEmpty(data)) {
			final String segments[] = data.split(SEPARATOR);
			return segments.length == 2;
		}
		return false;
	}

	private VisualGraphObject createDraggedObject(Point pt, String data) {

		VisualGraphObject graphObject = null;
		if (!Strings.isNullOrEmpty(data)) {
			final String segments[] = data.split(SEPARATOR);
			final String shapeName = segments[0];
			final String type = segments[1];
			if (type.equalsIgnoreCase(GraphBuilder.GraphObjectType.edge.name())) {
				graphObject = visualGraph.createEdge(shapeName);
			} else if (type.equalsIgnoreCase(GraphBuilder.GraphObjectType.node.name())) {
				graphObject = visualGraph.createNode(shapeName);
			} else if (type.equalsIgnoreCase(GraphBuilder.GraphObjectType.subgraph.name())) {
				graphObject = visualGraph.createSubgraph(shapeName);
			}
			if (graphObject != null) {
				final Transform xform = graphDocument.getTransformer();
				final Point ptGraph = xform.transformToGraph(pt);
				graphObject.move(ptGraph.x, ptGraph.y);
			}
		}
		return graphObject;
	}

	private Point getGripPoint(VisualGraphObject vgo) {

		final Transform xform = graphDocument.getTransformer();
		return xform.transformToScreen(vgo.getPreferredGripPoint());
	}
}
