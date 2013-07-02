package com.visiors.visualstage.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;




public interface Device{

	public void drawLine(int x1, int y1, int x2, int y2);
	public void drawRect(int x, int y, int width, int height);
	public void fillRect(int x, int y, int width, int height);
	public void drawImage(int x, int y, Image image);
	
	public void translate(int x, int y);
	public void scale(double sx, double sy);
	public void shear(double shx, double shy);
	public void rotate(double theta);
	public void resetTransformation();
	void setColor(int r, int g, int b);
	Object getGraphics();
	public void setColor(Color c);
	public void setFont(Font font);
	public void drawString(String text, int x, int y);
	public void drawString(String text, int x, int y, int rotation);
	public void fillOval(int i, int j, int k, int l);
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);
	void setStroke(float width, float[] dash);
	public void setStroke(float f);

}
