package com.visiors.visualstage.renderer;

import java.awt.Image;




public interface Canvas{

	public void draw(int x, int y, Image image);

	public DrawingContext getContext();
}
