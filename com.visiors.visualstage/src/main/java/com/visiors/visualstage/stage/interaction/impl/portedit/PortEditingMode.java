package com.visiors.visualstage.stage.interaction.impl.portedit;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.visiors.visualstage.constants.GraphStageConstants;
import com.visiors.visualstage.stage.cache.GraphObjectView;
import com.visiors.visualstage.stage.interaction.impl.BaseInteractionHandler;

public class PortEditingMode extends BaseInteractionHandler {

	private VisualNode subject;
	private PortSet originalPortSet;
	private int portID = -1;
	private final Rectangle angleEndHandle;
	private final Rectangle angleStartHandle;
	private int currentCursor;
	private Rectangle handle;

	public PortEditingMode() {

		super();

		angleEndHandle = new Rectangle();
		angleStartHandle = new Rectangle();
	}

	@Override
	public String getName() {

		return GraphStageConstants.MODE_PORT_EDIT;
	}

	@Override
	public void setActive(boolean activated) {

		if (activated) {
			if (graphView.getSelection().size() == 1) {
				List<VisualGraphObject> selection = graphView.getSelection();
				VisualGraphObject vgo = selection.get(0);
				if (vgo instanceof VisualNode) {
					subject = (VisualNode) vgo;
					subject.openPorts(true);
					// subject.setSelected(false);
					originalPortSet = subject.getPortSet();
					super.setActive(activated);
				}
			}
		} else {
			terminateInteraction();
		}
	}

	@Override
	public void cancelInteraction() {

		if (subject != null) {
			subject.openPorts(false);
			subject.setPortSet(originalPortSet);
			subject.highlightPort(portID, false);
			subject = null;
			portID = -1;
		}
		super.cancelInteraction();
	}

	@Override
	public void terminateInteraction() {

		if (subject != null) {
			subject.openPorts(false);
			subject.highlightPort(portID, false);
			portID = -1;
			subject = null;
		}
		super.terminateInteraction();
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (subject == null) {
			return true;
		}

		handle = null;
		final Point spt = graphView.getTransform().transformToScreen(pt);
		if (angleStartHandle.contains(spt) || angleEndHandle.contains(spt)) {
			handle = angleStartHandle.contains(spt) ? angleStartHandle : angleEndHandle;
			return true;
		}
		subject.highlightPort(portID, false);
		portID = getHitPort(pt);
		if (isControlKeyPressed(functionKey)) {

			if (portID != -1) {
				removePort(portID);
				portID = -1;
			} else {
				insertPort(pt);
			}
		} else if (portID != -1) {
			subject.highlightPort(portID, true);
		}

		graphView.updateView();

		return isActive();
	}

	private void removePort(int portId) {

		Port[] ports = subject.getPortSet().getPorts();
		Port[] newPorts = new Port[ports.length - 1];

		for (int i = 0, n = 0; i < ports.length; i++) {
			if (portId != ports[i].getID()) {
				newPorts[n++] = ports[i];
			}
		}
		subject.setPortSet(new DefaultPortSet(newPorts));
	}

	private void insertPort(Point pt) {

		Rectangle b = subject.getBounds();
		double xratio = ((double) (pt.x - b.x) / b.width);
		double yratio = ((double) (pt.y - b.y) / b.height);
		Port[] ports = subject.getPortSet().getPorts();

		Port[] newPorts = new Port[ports.length + 1];
		portID = -1;
		for (int i = 0; i < ports.length; i++) {
			newPorts[i] = ports[i];
			portID = Math.max(portID, ports[i].getID());
		}
		portID++;
		int[] acceptedInterval = new int[] { 0, 360 };

		newPorts[ports.length] = new DefaultPort(portID, xratio, yratio, acceptedInterval);
		subject.setPortSet(new DefaultPortSet(newPorts));
		subject.highlightPort(portID, true);
	}

	private int getHitPort(Point pt) {

		PortSet ps = subject.getPortSet();
		Port p = ps.getPortAt(pt);
		return p != null ? p.getID() : -1;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		handle = null;

		return isActive();
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (portID != -1) {

			if (handle != null) {
				adjustInterval(pt);
			} else {
				movePort(portID, pt);
			}

			graphView.updateView();
		}
		return isActive();
	}

	private void adjustInterval(Point pt) {

		Port port = subject.getPortSet().getPortByID(portID);
		Point pos = port.getPosition();
		int dx = pos.x - pt.x;
		int dy = pos.y - pt.y;
		int[] interval = port.getAcceptedInterval();
		if (handle.equals(angleStartHandle)) {
			interval[0] = (int) Math.toDegrees(Math.atan2(dy, -dx));
		} else {
			interval[1] = (int) Math.toDegrees(Math.atan2(dy, -dx));
		}

		if (interval[0] < 0) {
			interval[0] += 360;
		}
		if (interval[1] < 0) {
			interval[1] += 360;
		}

		port.setAcceptedInterval(interval);
	}

	// private int snapAngle(int angle, int snap, int tolerance) {
	//
	// if(angle < snap + tolerance && angle > snap - tolerance )
	// return snap;
	// return angle;
	// }

	private void movePort(int portId, Point pt) {

		Port[] ports = subject.getPortSet().getPorts();
		for (int i = 0; i < ports.length; i++) {

			Port port = ports[i];
			if (port.getID() == portId) {

				Rectangle b = subject.getBounds();
				double xratio = ((double) (pt.x - b.x) / b.width);
				double yratio = ((double) (pt.y - b.y) / b.height);

				// reduce the number of decimals
				xratio = ((int) (xratio * 100)) / 100.0;
				yratio = ((int) (yratio * 100)) / 100.0;

				// snap to most useful locations
				xratio = snapRatio(xratio, 0.5, 0.1);
				xratio = snapRatio(xratio, 0.0, 0.1);
				xratio = snapRatio(xratio, 1.0, 0.1);

				yratio = snapRatio(yratio, 0.5, 0.1);
				yratio = snapRatio(yratio, 0.0, 0.1);
				yratio = snapRatio(yratio, 1.0, 0.1);

				int[] interval = suggestInterval(port.getAcceptedInterval(), xratio, yratio);

				ports[i] = new DefaultPort(portId, xratio, yratio, interval);
				subject.setPortSet(new DefaultPortSet(ports));
				subject.highlightPort(port.getID(), true);
				break;
			}
		}
	}

	private int[] suggestInterval(int[] interval, double xratio, double yratio) {

		if (xratio > 0.80) {
			interval[0] = 315;
			interval[1] = 45;
		} else if (xratio < 0.2) {
			interval[0] = 135;
			interval[1] = 225;
		} else if (yratio <= 0.2) {
			interval[0] = 45;
			interval[1] = 135;
		} else if (yratio >= 0.8) {
			interval[0] = 225;
			interval[1] = 315;
		} else {
			interval[0] = 0;
			interval[1] = 360;
		}

		return interval;
	}

	private double snapRatio(double ratio, double snapAt, double tolerance) {

		if (ratio < snapAt + tolerance && ratio > snapAt - tolerance) {
			return snapAt;
		}
		return ratio;
	}

	private int snapAngle(int angle, int snap, int tolerance) {

		if (angle < snap + tolerance && angle > snap - tolerance) {
			return snap;
		}
		return angle;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		currentCursor = GraphStageConstants.CURSOR_DEFAULT;
		if (subject != null) {
			pt = graphView.getTransform().transformToScreen(pt);
			if (angleStartHandle.contains(pt) || angleEndHandle.contains(pt)) {
				currentCursor = GraphStageConstants.CURSOR_CROSSHAIR;
			}
		}
		return isActive();
	}

	@Override
	public int getPreferredCursor() {

		return currentCursor;
	}

	@Override
	public void paintOnBackground(Device device, Rectangle r) {

		if (subject == null || portID == -1) {
			return;
		}

		// Guide lines
		device.setColor(Color.lightGray);

		Rectangle b = subject.getBounds();
		b = graphView.getTransform().transformToScreen(b);
		// device.drawLine(b.x - 50,
		// b.y + b.height / 2,
		// b.x + b.width + 50,
		// b.y + b.height / 2);
		// device.drawLine(b.x + b.width /2,
		// b.y - 50,
		// b.x + b.width / 2,
		// b.y + b.height + 50);

		// write ratio
		device.setColor(Color.black);
		Port port = subject.getPortSet().getPortByID(portID);
		device.drawString("x-ratio: " + port.getXRatio() + "%", b.x + b.width / 2 + 10, b.y
				+ b.height + 50);
		device.drawString("y-ratio: " + port.getYRatio() + "%", b.x + b.width / 2 + 10, b.y
				+ b.height + 60);
	}

	@Override
	public void paintOnTop(Device device, Rectangle r) {

		drawPortsAcceptingInterval(device);

	}

	private void drawPortsAcceptingInterval(Device device) {

		if (subject == null) {
			return;
		}
		device.setColor(Color.gray);
		Port[] ports = subject.getPortSet().getPorts();
		double r = 50;
		for (int i = 0; i < ports.length; i++) {
			Port port = ports[i];
			if (!port.isHighlighted()) {
				continue;
			}
			int[] angles = port.getAcceptedInterval();
			double start = Math.toRadians(angles[0]);
			double end = Math.toRadians(angles[1]);
			Point pt = graphView.getTransform().transformToScreen(port.getPosition());
			device.setColor(Color.black);

			int dx1 = (int) (6 * Math.cos(start));
			int dy1 = (int) (6 * Math.sin(start));
			int dx2 = (int) (r * Math.cos(start));
			int dy2 = (int) (r * Math.sin(start));
			device.drawLine(pt.x + dx1, pt.y - dy1, pt.x + dx2, pt.y - dy2);
			angleStartHandle.setBounds(pt.x + dx2 - 4, pt.y - dy2 - 4, 7, 7);
			device.fillOval(angleStartHandle.x, angleStartHandle.y, angleStartHandle.width,
					angleStartHandle.height);

			dx1 = (int) (6 * Math.cos(end));
			dy1 = (int) (6 * Math.sin(end));
			dx2 = (int) (r * Math.cos(end));
			dy2 = (int) (r * Math.sin(end));

			device.drawLine(pt.x + dx1, pt.y - dy1, pt.x + dx2, pt.y - dy2);

			angleEndHandle.setBounds(pt.x + dx2 - 4, pt.y - dy2 - 4, 7, 7);
			device.fillOval(angleEndHandle.x, angleEndHandle.y, angleEndHandle.width,
					angleEndHandle.height);
			dx2 = (int) ((r + 15) * Math.cos(start));
			dy2 = (int) ((r + 15) * Math.sin(start));
			device.drawString("start: " + angles[0] + "°", pt.x + dx2, pt.y - dy2);
			int offset = 0;
			if ((angles[0] == 0 && angles[1] == 360)) {
				offset -= 12;
			}
			dx2 = (int) ((r + 15) * Math.cos(end));
			dy2 = (int) ((r + 15) * Math.sin(end)) + offset;
			device.drawString("end: " + angles[1] + "°", pt.x + dx2, pt.y - dy2);

		}
	}
}
