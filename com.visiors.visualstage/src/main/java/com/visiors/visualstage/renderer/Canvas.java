package com.visiors.visualstage.renderer;

import java.awt.Image;
import java.awt.Rectangle;

import com.visiors.visualstage.transform.Transform;




public interface Canvas{

	public void draw(int x, int y, Image image);
	public Resolution getResolution();
	public Rectangle getCanvasBounds(); 
	public Transform getTransform(); 
}
