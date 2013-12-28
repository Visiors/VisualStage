package com.visiors.visualstage.renderer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class DefaultComponentOfflineRenderer implements ComponentOffScreenRenderer {

	private Image image;
	private final ComponentRenderer componentRenderer;
	private boolean invalidated;

	public DefaultComponentOfflineRenderer(ComponentRenderer componentRenderer) {

		this.componentRenderer = componentRenderer;
	}

	@Override
	public void invalidate() {

		this.invalidated = true;
	}

	public boolean isInvalidated() {

		return invalidated;
	}

	@Override
	public void render(Graphics2D gfx) {

		final Rectangle r = componentRenderer.getBounds();
		if(r != null && !r.isEmpty()) {
			if ((image == null || isInvalidated()) ) 
			{
				image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB_PRE);
				final Graphics2D g = (Graphics2D) image.getGraphics();
				g.translate(-r.x, -r.y);
				componentRenderer.draw(g);
				g.translate(r.x, r.y);
				invalidated = false;
				//System.err.println("creating image");
			} else {
				//System.err.println("re-using image");
			}
			gfx.drawImage(image, r.x, r.y, null);
		}
	}
}
