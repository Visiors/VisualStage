package com.visiors.visualstage.stage.interaction.impl.attachnemt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.visiors.visualstage.constants.GraphStageConstants;
import com.visiors.visualstage.form.FormItem;
import com.visiors.visualstage.stage.interaction.impl.BaseInteractionHandler;
import com.visiors.visualstage.util.GraphInteractionUtil;

public class FormComposeMode extends BaseInteractionHandler {

	private FormItem hitComponent;
	private VisualNode hitNode;
	private Point mousePressedPt;
	private final int margin = 3;
	private int dx;
	private int dy;

	public FormComposeMode() {

		super();
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		mousePressedPt = pt;
		hitComponent = getComponentAt(pt);
		if (hitComponent != null) {
			final Point location = hitComponent.getLocation();
			final Dimension size = hitComponent.getSize();
			dx = pt.x - location.x;
			dy = pt.y - location.y - size.height;
		}

		return hitComponent != null;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		hitComponent = null;
		visualGraph.updateView();
		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (hitComponent != null) {

			adjustFormSetting(pt);

			hitNode.getForm().invalidate();
			visualGraph.updateView();
		}
		return hitComponent != null;
	}

	private void adjustFormSetting(Point pt) {

		final String newSlot = getClosestSlot(pt);
		final Point slotPosition = hitNode.getSlotLocation(newSlot);
		final Anchor orientation = getSlotDirection(slotPosition, pt);

		pt.x -= dx;
		pt.y -= dy;
		final Point offset = getOffsetToSlot(slotPosition, pt);

		hitComponent.setSlot(newSlot);
		hitComponent.setAnchor(orientation);

		hitComponent.setOffset(offset);
	}

	private Anchor getSlotDirection(Point slotPosition, Point location) {

		if (location.y < slotPosition.y - margin) { // northern

			if (location.x < slotPosition.x - margin) {
				return Anchor.NorthWest;
			} else if (location.x > slotPosition.x + margin) {
				return Anchor.NorthEast;
			} else {
				return Anchor.North;
			}

		} else if (location.y > slotPosition.y + margin) { // southern
			if (location.x < slotPosition.x - margin) {
				return Anchor.SouthWest;
			} else if (location.x > slotPosition.x + margin) {
				return Anchor.SouthEast;
			} else {
				return Anchor.South;
			}
		} else { // center
			if (location.x < slotPosition.x - margin) {
				return Anchor.West;
			} else if (location.x > slotPosition.x + margin) {
				return Anchor.East;
			} else {
				return Anchor.Center;
			}
		}
	}

	private final double getDistance(Point pt1, Point pt2) {

		final int dx = pt2.x - pt1.x;
		final int dy = pt2.y - pt1.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	private Point getOffsetToSlot(Point slotPosition, Point location) {

		Point offset = new Point(location.x - slotPosition.x, location.y - slotPosition.y);
		// snap
		if (Math.abs(offset.x) < 10) {
			offset.x = 0;
		}
		if (Math.abs(offset.y) < 10) {
			offset.y = 0;
		}
		return offset;
	}

	private String getClosestSlot(Point location) {

		String[] slots = hitNode.getSlots();
		int minIndex = 0;
		double distance;
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < slots.length; i++) {
			Point pt = hitNode.getSlotLocation(slots[i]);
			distance = getDistance(pt, location);
			if (distance < minDistance) {
				minDistance = distance;
				minIndex = i;
			}
		}

		return slots[minIndex];
		// return slots[0];
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInteracting() {

		// TODO Auto-generated method stub
		return hitComponent != null;
	}

	@Override
	public void cancelInteraction() {

		// TODO Auto-generated method stub

	}

	@Override
	public void terminateInteraction() {

		// TODO Auto-generated method stub

	}

	@Override
	public int getPreferredCursor() {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {

		return GraphStageConstants.MODE_FORM_COMPOSE;
	}

	@Override
	public void setActive(boolean activated) {

		if (activated) {
			visualGraph.clearSelection();

		}
	}

	@Override
	public boolean isActive() {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void paintOnBackground(Device device, Rectangle visibleScreenRect) {

		// TODO Auto-generated method stub

	}

	@Override
	public void paintOnTop(Device device, Rectangle visibleScreenRect) {

		final VisualNode[] nodes = visualGraph.getNodes();
		final Transformer xform = visualGraph.getTransform();
		boolean slotUsed;
		Point pt;
		for (VisualNode node : nodes) {
			String[] slots = node.getSlots();
			for (String slot : slots) {
				pt = node.getSlotLocation(slot);
				pt = xform.transformToScreen(pt);
				slotUsed = slotUsed(node, slot);
				drawSlot(device, pt, slotUsed);
				if (hitComponent != null && slotUsed) {
					drawOrientations(device, pt);
				}
			}
		}

		if (hitComponent != null) {
			drawInfo(device);
		}
	}

	private boolean slotUsed(VisualNode node, String slot) {

		Form form = node.getForm();
		if (form != null) {
			List<FormItem> units = form.getItems();
			for (FormItem formComponentUnit : units) {
				if (formComponentUnit.getSlot().equals(slot)) {
					return true;
				}
			}
		}
		return false;
	}

	private void drawOrientations(Device device, Point pt) {

		final int r = 10;

		device.setColor(new Color(0x8fff0000, true));

		device.drawLine(pt.x + margin, pt.y, pt.x + r, pt.y);
		device.drawLine(pt.x + r - 1, pt.y - 1, pt.x + r - 1, pt.y + 1);

		device.drawLine(pt.x - r, pt.y, pt.x - margin, pt.y);
		device.drawLine(pt.x - r + 1, pt.y - 1, pt.x - r + 1, pt.y + 1);

		device.drawLine(pt.x, pt.y - r, pt.x, pt.y - margin);
		device.drawLine(pt.x - 1, pt.y - r + 1, pt.x + 1, pt.y - r + 1);

		device.drawLine(pt.x, pt.y + margin, pt.x, pt.y + r);
		device.drawLine(pt.x - 1, pt.y + r - 1, pt.x + 1, pt.y + r - 1);

		device.drawLine(pt.x + margin, pt.y + margin, pt.x + r, pt.y + r);
		device.drawLine(pt.x + r - 1, pt.y + r, pt.x + r, pt.y + r - 1);
		device.drawLine(pt.x + r - 2, pt.y + r, pt.x + r, pt.y + r - 2);

		device.drawLine(pt.x - r, pt.y - r, pt.x - margin, pt.y - margin);
		device.drawLine(pt.x - r + 1, pt.y - r, pt.x - r, pt.y - r + 1);
		device.drawLine(pt.x - r + 2, pt.y - r, pt.x - r, pt.y - r + 2);

		device.drawLine(pt.x - r, pt.y + r, pt.x - margin, pt.y + margin);
		device.drawLine(pt.x - r, pt.y + r - 1, pt.x - r + 1, pt.y + r);
		device.drawLine(pt.x - r, pt.y + r - 2, pt.x - r + 2, pt.y + r);

		device.drawLine(pt.x + margin, pt.y - margin, pt.x + r, pt.y - r);
		device.drawLine(pt.x + r - 1, pt.y - r, pt.x + r, pt.y - r + 1);
		device.drawLine(pt.x + r - 2, pt.y - r, pt.x + r, pt.y - r + 2);
	}

	private void drawSlot(Device device, Point pt, boolean highlighted) {

		device.setColor(new Color(0x8fff0000, true));
		if (highlighted) {
			device.fillRect(pt.x - margin, pt.y - margin, margin * 2, margin * 2);
		}
		device.drawRect(pt.x - margin, pt.y - margin, margin * 2, margin * 2);
	}

	private void drawInfo(Device device) {

		final String slot = hitComponent.getSlot();
		final Dimension size = hitComponent.getSize();
		final Point offset = hitComponent.getOffset();
		Transformer xform = visualGraph.getTransform();
		final Rectangle b = xform.transformToScreen(hitNode.getBounds());
		final Point pt = new Point(b.x, b.y - size.height - 50);

		pt.x -= 40;
		pt.y -= 10;

		device.setColor(Color.black);
		device.drawRect(pt.x - 20, pt.y - 20, 220, 60);
		device.setColor(new Color(0xb0ffffff, true));
		device.fillRect(pt.x - 20, pt.y - 20, 220, 60);

		device.setColor(Color.black);
		device.drawString("Slot to attach: " + slot, pt.x, pt.y);
		device.drawString("Expanding direction: " + hitComponent.getAnchor(), pt.x, pt.y + 15);
		if (offset.x != 0 || offset.y != 0) {
			device.drawString("Additional offset to slot : x=" + offset.x + ", y=" + offset.y,
					pt.x, pt.y + 30);
		}

	}

	private FormItem getComponentAt(Point pt) {

		hitNode = GraphInteractionUtil.getFirstHitNodeAt(visualGraph, pt);

		if (hitNode != null) {
			Form form = hitNode.getForm();
			if (form != null) {
				FormItem unit = form.getHitItem(pt);
				if (unit != null) {
					return unit;
				}
			}
		}

		return null;
	}

}
