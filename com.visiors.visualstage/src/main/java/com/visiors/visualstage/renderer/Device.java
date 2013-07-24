package com.visiors.visualstage.renderer;

import java.awt.Image;
import java.awt.Rectangle;




public interface Device{ //TODO rename to Canvas

	public void drawImage(int x, int y, Image image);
	public Resolution getResolution();
	public Rectangle getCanvasBounds(); 
	public double getScale(); 
}
