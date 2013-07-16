package com.visiors.visualstage.graph.view.node;

import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.property.PropertyOwner;

public interface PortSet extends PropertyOwner
{

	public enum Positioning {FIXED, FLOATING};

	public Port[] getPorts();

	public PortSet deepCopy();

	public void updatePosition(Rectangle r);

	public Port getPortByID(int portID);

	public Port getPortAt(Point pt);

	public int getPortNextTo(Point pt);

	public int getPortByAngle(double angle);

	public void createDefaultFourPortSet();

	public void createDefaultEightPortSet();

	public Positioning getPositioning();

	public void setPositioning(Positioning positioning);

}
