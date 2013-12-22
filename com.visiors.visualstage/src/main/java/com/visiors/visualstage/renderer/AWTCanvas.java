package com.visiors.visualstage.renderer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;


public class AWTCanvas {

	public Graphics2D gfx;
	public BufferedImage image;

	public AWTCanvas(int width, int height) {

		image = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB_PRE);
		gfx = (Graphics2D) image.getGraphics();
		//		gfx.setColor(Color.white);
		//		gfx.fillRect(0, 0, width, height);
		gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public void setImage(Image img){
		image = (BufferedImage) img;
		gfx = (Graphics2D) image.getGraphics();
	}

	public Image getImage(){
		return image;
	}

}
