package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class DefaultOfflineRenderer implements OfflineRenderer {

	private Image image;
	private final DrawClient drawClient;
	private boolean invalidated;

	public DefaultOfflineRenderer(DrawClient drawClient) {

		this.drawClient = drawClient;
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

		final Rectangle r = drawClient.getBounds();
		if (image == null || isInvalidated()) {
			image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB_PRE);
			final Graphics2D g = (Graphics2D) image.getGraphics();
			g.translate(-r.x, -r.y);
			drawClient.draw(g);
			g.translate(r.x, r.y);
			invalidate();
			System.err.println("re-creating image");
		}
		gfx.drawImage(image, r.x, r.y, null);
	}
}
