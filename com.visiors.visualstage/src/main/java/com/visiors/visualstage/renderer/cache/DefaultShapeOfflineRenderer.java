package com.visiors.visualstage.renderer.cache;

import java.awt.Image;

import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;

public class DefaultShapeOfflineRenderer implements ShapeOffScreenRenderer {

	private final ShapeRenderer shapeRenderer;
	private final ImageRepository cache = new ImageRepository();

	public DefaultShapeOfflineRenderer(ShapeRenderer shapeRenderer) {

		this.shapeRenderer = shapeRenderer;
	}

	@Override
	public void render(AWTCanvas awtCanvas, DrawingContext context, DrawingSubject subject) {

		Image image = cache.getImage(context.getResolution(), subject);
		if (image == null) {
			image = shapeRenderer.getSnapshot(context, subject);
			if (image != null) {
				cache.storeImage(image, context.getResolution(), subject);
			}
		}
		if (image != null) {
			shapeRenderer.draw(awtCanvas, image);
		}
	}

	@Override
	public void invalidate() {

		cache.clear();
	}
}
