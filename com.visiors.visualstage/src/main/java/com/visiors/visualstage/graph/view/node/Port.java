package com.visiors.visualstage.graph.view.node;

import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.property.PropertyOwner;

public interface Port  extends PropertyOwner {

	public int getID();

	public double getXRatio();

	public double getYRatio();

	public int[] getAcceptedInterval();

	public void setAcceptedInterval(int[] interval);

	public void updatePosition(Rectangle r);

	public boolean acceptsDirection(double direction);

	public Point getPosition();

	public void setHighlighted(boolean highlighted);

	public boolean isHighlighted();

	public boolean isHit(Point pt);

	public Port deepCopy();

}
