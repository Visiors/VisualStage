package com.visiors.visualstage.layout;

import java.awt.Point;


public interface LayoutableEdge
{
    public long getID();
    public LayoutableNode getSourceNode();    
    public LayoutableNode getTargetNode();
    public Point[] getPoints();
    public void setPoints(Point[] points);
    public boolean isSelected();
    
    public void setSourcePortId(int id);
    public void setTargetPortId(int id);
    public int getSourcePortId();
    public int getTargetPortId();
    
}
