package com.visiors.visualstage.renderer;

import java.awt.Rectangle;

import com.visiors.visualstage.transform.Transform;

public interface DrawingContext {

	public Resolution getResolution();

	//	public void setResolution(Resolution resolution);

	public Rectangle getBounds();

	//	public void setBounds(Rectangle rectangle);

	public Transform getTransform();

	//	public void setTransform(Transform transform);


}