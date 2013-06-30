package com.visiors.visualstage.graph.view.node.impl;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.ViewConstants;
import com.visiors.visualstage.graph.view.node.Port;

public class DefaultPort implements Port {

	protected Color color = Color.white;
	protected Color selectionColor = Color.orange;
	protected boolean highlighted;
	protected Point position = new Point();

	protected int id;
	protected double xRatio;
	protected double yRatio;
	// protected int range = ViewConstants.MARKER_SIZE;
	protected int[] accseptingInterval = { 0, 0 };

	public DefaultPort() {

	}

	public DefaultPort(int id, double xRatio, double yRatio, int[] angle) {

		this.id = id;
		this.xRatio = xRatio;
		this.yRatio = yRatio;
		setAcceptedInterval(angle);
	}

	public DefaultPort(DefaultPort pt) {

		id = pt.id;
		xRatio = pt.xRatio;
		yRatio = pt.yRatio;
		position = pt.getPosition();
		highlighted = pt.isHighlighted();

		setAcceptedInterval(pt.accseptingInterval);
	}

	@Override
	public int getID() {

		return id;
	}

	// //////////////////////////////////////////////////////////////////////
	// Position and size
	@Override
	public double getXRatio() {

		return xRatio;
	}

	@Override
	public double getYRatio() {

		return yRatio;
	}

	// //////////////////////////////////////////////////////////////////////
	// interval

	@Override
	public int[] getAcceptedInterval() {

		return accseptingInterval;
	}

	@Override
	public void setAcceptedInterval(int[] interval) {

		accseptingInterval = interval;

		/* normalizing interval */

		// -make angles positive
		if (accseptingInterval[0] < 0) {
			accseptingInterval[0] += 360.0;
		}
		if (accseptingInterval[1] < 0) {
			accseptingInterval[1] += 360.0;
		}

		// -make smaller than 360
		if (accseptingInterval[0] > 360.0) {
			accseptingInterval[0] -= 360.0;
		}
		if (accseptingInterval[1] > 360.0) {
			accseptingInterval[1] -= 360.0;
		}

	}

	@Override
	public Port deepCopy() {

		return new DefaultPort(this);
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append("Port [").append("id= ").append(id).append(", interval= ")
				.append(accseptingInterval[0]).append(" - ").append(accseptingInterval[1])
				.append(", position= ").append(xRatio).append(" , ").append(yRatio).append(" ]");

		return sb.toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// // Format
	//
	// public void setColor(Color c)
	// {
	// this.color = c;
	// }
	//
	// public Color getColor()
	// {
	// return this.color;
	// }
	//
	// public void setSelectionColor(Color c)
	// {
	// this.selectionColor = c;
	// }
	//
	// public Color getSelectionColor()
	// {
	// return this.selectionColor;
	// }

	@Override
	public void updatePosition(Rectangle r) {

		position.x = (int) (r.x + xRatio * r.width);
		position.y = (int) (r.y + yRatio * r.height);
	}

	@Override
	public Point getPosition() {

		return new Point(position);
	}

	// //
	// ///////////////////////////////////////////////////////////////////////
	// // implementation of Attributable interface
	// @Override
	// public void setProperties(PropertyList properties) {
	//
	// id = PropertyUtil.getProperty(properties, "id", -1);
	// xRatio = PropertyUtil.getProperty(properties, "xRatio", 0.0);
	// yRatio = PropertyUtil.getProperty(properties, "yRatio", 0.0);
	// accseptingInterval[0] = PropertyUtil.getProperty(properties, "from", 0);
	// accseptingInterval[1] = PropertyUtil.getProperty(properties, "to", 0);
	// }
	//
	// @Override
	// public PropertyList getProperties() {
	//
	// PropertyList pl = new
	// DefaultPropertyList(PropertyConstants.PORT_PROPERTY);
	// pl.add(new DefaultPropertyUnit("id", new Integer(id)));
	// pl.add(new DefaultPropertyUnit("xRatio", new Double(xRatio)));
	// pl.add(new DefaultPropertyUnit("yRatio", new Double(yRatio)));
	// pl.add(new DefaultPropertyUnit("from", new
	// Integer(accseptingInterval[0])));
	// pl.add(new DefaultPropertyUnit("to", new
	// Integer(accseptingInterval[1])));
	// return pl;
	// }

	// ///////////////////////////////////////////////////////////////////////
	// implementation of VisualObject interface

	// public void draw(Graphics g, int porpose)
	// {
	// g.setColor(selected ? selectionColor : color);
	//
	// int x = transformer.scaleX(position.x);
	// int y = transformer.scaleY(position.y);
	//
	// g.fillRect(x - range/2, y - range/2, range, range);
	// g.setColor(Color.darkGray);
	// g.drawRect(x - range/2, y - range/2, range, range);
	// }

	@Override
	public boolean isHit(Point pt) {

		int range = ViewConstants.PORT_SIZE;
		return pt.x >= position.x - range / 2 && pt.x <= position.x + range / 2
				&& pt.y >= position.y - range / 2 && pt.y <= position.y + range / 2;
	}

	@Override
	public void setHighlighted(boolean highlighted) {

		this.highlighted = highlighted;
	}

	@Override
	public boolean isHighlighted() {

		return highlighted;
	}

	@Override
	public boolean acceptsDirection(double angle) {

		double int1 = accseptingInterval[0];
		double int2 = accseptingInterval[1];

		if (angle < 0) {
			angle += 360;
		}
		if (angle > 360) {
			angle %= 360;
		}

		if (int2 > 360 && int1 < 360) {
			angle += 360;
		}

		if (int1 > int2) {
			return (angle >= int1 && angle <= 360) || (angle >= 0 && angle <= int2);
		}

		return angle >= int1 && angle <= int2;
	}

}
