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

	public Rectangle transformToGraph(Rectangle ptGraph);

	public int transformToScreenX(int x);

	public int transformToScreenY(int y);

	public int transformToScreenDX(int w);

	public int transformToScreenDY(int h);

	public int transformToGraphX(int x);

	public int transformToGraphY(int y);

	public int transformToGraphDX(int w);

	public int transformToGraphDY(int h);



	public void addListener(TransformValueChangeListener l);

	public void removeListener(TransformValueChangeListener l);

	public Object clone();

	public int getViewWidth();

	public void setViewWidth(int w);

	public int getViewHeight();

	public void setViewHeight(int h);

	public int getViewX();

	public void setViewportX(int x);

	public int getViewY();

	public void setViewportY(int y);


}
