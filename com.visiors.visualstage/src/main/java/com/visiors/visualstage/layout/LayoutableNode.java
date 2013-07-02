package com.visiors.visualstage.layout;

import java.awt.Point;
import java.awt.Rectangle;

public interface LayoutableNode
{
    public long getID();
    public Rectangle getBounds();
    public void setBounds(Rectangle r);
    public int getIndegree();
    public int getOutdegree();
    public LayoutableEdge[] incomingEdges();
    public LayoutableEdge[] outgoingEdges();
    public boolean isSelected();
    
    
    int[] getPortAcceptedInterval(int portId);
    public int getPortNextTo(Point pt);
    public int getPortByAngle(double angle);
    public Point getPortPosition(int portID);
   
}
