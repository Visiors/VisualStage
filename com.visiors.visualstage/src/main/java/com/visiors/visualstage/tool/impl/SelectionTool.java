package com.visiors.visualstage.tool.impl;

import java.awt.Point;
import java.util.List;

import com.visiors.visualstage.graph.view.VisualGraphObject;

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
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (hitOnMouseDown != null) {
			if (deselectOnMoueRelease) {
				if(mouseDownPos.equals( pt)){ // don't remove the selection if objects were moved
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
			hitOnMouseDown = null;
			deselectOnMoueRelease = false;
		}
		return false;
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
				hitOnMouseDown.setSelected(!hitOnMouseDown.isSelected());
				needUpdate = true;
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

	private VisualGraphObject getTopHitObject(Point pt) {

		final List<VisualGraphObject> hitObjects = visualGraph.getHitObjects(pt);
		if (!hitObjects.isEmpty()) {
			return hitObjects.get(0);
		}
		return null;

	}
}
