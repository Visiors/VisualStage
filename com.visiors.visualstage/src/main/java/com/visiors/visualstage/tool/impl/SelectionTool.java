package com.visiors.visualstage.tool.impl;

import java.awt.Point;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.util.GraphInteractionUtil;

/**
 * This tool listens to mouse event and selects graph objects accordingly. Both
 * single selection mode and multi-selection mode are supported. The
 * multi-selection mode is activated when the control key is hold down.
 */
public class SelectionTool extends BaseTool {

	private VisualGraphObject hitOnMouseDown;
	private boolean deselectOnMoueRelease;
	private Point mouseDownPos;

	public SelectionTool(String name) {

		super(name);
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		final boolean xorMode = isControlKeyPressed(functionKey);
		mouseDownPos = pt;
		boolean needUpdate = false;
		hitOnMouseDown = getTopHitObject(pt);
		if (hitOnMouseDown == null) {
			if (!xorMode) {
				if (!visualGraph.getSelection().isEmpty()) {
					visualGraph.clearSelection();
					needUpdate = true;
				}
			}
		} else {
			if (xorMode) {
				if (!hitOnMouseDown.isSelected()) {
					hitOnMouseDown.setSelected(!hitOnMouseDown.isSelected());
					needUpdate = true;
				} else {
					deselectOnMoueRelease = true;
				}
			} else {
				deselectOnMoueRelease = hitOnMouseDown.isSelected() && visualGraph.getSelection().size() > 1;
				for (final VisualGraphObject vgo : visualGraph.getGraphObjects()) {
					if (vgo.equals(hitOnMouseDown)) {
						if (!vgo.isSelected()) {
							vgo.setSelected(true);
							needUpdate = true;
						}
					} else if (!deselectOnMoueRelease) {
						if (vgo.isSelected()) {
							vgo.setSelected(false);
							needUpdate = true;
						}
					}
				}
			}
		}
		if (needUpdate) {
			graphDocument.invalidate();
		}

		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (hitOnMouseDown != null) {
			final boolean xorMode = isControlKeyPressed(functionKey);
			if (mouseDownPos.equals(pt)) { // don't change the selection if
										   // objects were moved
				if (deselectOnMoueRelease) {
					if (xorMode) {
						hitOnMouseDown.setSelected(false);
					} else {
						boolean needUpdate = false;
						for (final VisualGraphObject vgo : visualGraph.getGraphObjects()) {
							if (vgo.isSelected() && !vgo.equals(hitOnMouseDown)) {
								vgo.setSelected(false);
								needUpdate = true;
							}
						}
						if (needUpdate) {
							graphDocument.invalidate();
						}
					}
				}
			}
			hitOnMouseDown = null;
			deselectOnMoueRelease = false;
		}
		return false;
	}

	private VisualGraphObject getTopHitObject(Point pt) {

		return GraphInteractionUtil.getFirstHitObjectAt(visualGraph, pt);

	}
}
