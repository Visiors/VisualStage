package com.visiors.visualstage.transform;

import java.awt.Point;
import java.awt.Rectangle;

public interface Transform {

	public void setScale(double s, boolean suppressEvent);

	public void setXTranslate(double tx, boolean suppressEvent);

	public void setYTranslate(double ty, boolean suppressEvent);

	public void setScale(double s);

	public void setXTranslate(double tx);

	public void setYTranslate(double ty);

	public double getScale();

	public double getXTranslate();

	public double getYTranslate();

	public Point transformToScreen(Point ptGraph);

	public Point transformToGraph(Point ptScreen);

	public Rectangle transformToScreen(Rectangle rGraph);

	public Rectangle transformToGraph(Rectangle ptGraph);

	public int transformToScreenX(int x);

	public int transformToScreenY(int y);

	public int transformToScreenDX(int w);

	public int transformToScreenDY(int h);

	public int transformToGraphX(int x);

	public int transformToGraphY(int y);

	public int transformToGraphDX(int w);

	public int transformToGraphDY(int h);

	public int getViewWidth();

	public void setViewWidth(int w);

	public int getViewHeight();

	public void setViewHeight(int h);

	public int getViewX();

	public void setViewportX(int x);

	public int getViewY();

	public void setViewportY(int y);

	public Object clone();

	public void addListener(TransformListener l);

	public void removeListener(TransformListener l);

}
