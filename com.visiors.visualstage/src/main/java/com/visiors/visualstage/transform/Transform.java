package com.visiors.visualstage.transform;

import java.awt.Point;
import java.awt.Rectangle;

public interface Transform {


	public void reset();

	public void setScale(double s);

	public void setShear(double sx);

	public void setShearX(double sx);

	public void setShearY(double sy);

	public void setXTranslate(double tx);

	public void setYTranslate(double ty);

	public void setRotation(double alpha);

	public double getScaleX();

	public double getScale();

	public double getScaleY();

	public double getShear();

	public double getShearX();

	public double getShearY();

	public double getXTranslate();

	public double getYTranslate();

	public double getRotation();

	public Point transformToScreen(Point ptGraph);

	public Point transformToGraph(Point ptScreen);

	public Rectangle transformToScreen(Rectangle rGraph);

	public Rectangle transformToGraph(Rectangle rScreen);

	public int transformToScreenX(int x);

	public int transformToScreenY(int y);

	public int transformToGraphX(int x);

	public int transformToGraphY(int y);

	public void addListener(TransformValueChangeListener l);

	public void removeListener(TransformValueChangeListener l);

	public Object clone();

	public int getViewportWidth();

	public void setViewportWidth(int w);

	public int getViewportHeight();

	public void setViewportHeight(int h);

	public Rectangle getCanvasBoundary();

	public void setCanvasBoundary(Rectangle r);
}
