package com.visiors.visualstage.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class AWTCanvas {

	public final Graphics2D gfx;
	public BufferedImage image;

	public AWTCanvas(int width, int height) {

		image = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB_PRE);

		gfx = (Graphics2D) image.getGraphics();
	}
}
