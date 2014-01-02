package com.visiors.visualstage.renderer.cache;

import java.awt.Image;
import java.awt.Rectangle;

import com.google.inject.Inject;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.ImageFactory;
import com.visiors.visualstage.svg.SVGDocumentBuilder;
import com.visiors.visualstage.transform.Transform;

public class DefaultShapeRenderer implements ShapeRenderer {

	private final ImageFactory snapshotGenerator;

	@Inject
	protected SVGDocumentBuilder svgDocumentBuilder;

	private final VisualGraphObject vgo;

	public DefaultShapeRenderer(VisualGraphObject vgo) {

		this.vgo = vgo;
		this.snapshotGenerator = new ImageFactory(svgDocumentBuilder);
	}

	@Override
	public Rectangle getBounds() {

		return vgo.getExtendedBoundary();
	}

	@Override
	public Image getSnapshot(DrawingContext context, DrawingSubject subject) {

		return snapshotGenerator.createSnapshot(vgo, context, subject);
	}

	@Override
	public void draw(AWTCanvas awtCanvas, Image image) {

		final Transform transform = vgo.getTransformer();
		final Rectangle boundary = transform.transformToScreen(vgo.getBounds());
		final Rectangle extendedBoundary = transform.transformToScreen(vgo.getExtendedBoundary());
		boundary.grow((extendedBoundary.width - boundary.width) / 2, (extendedBoundary.height - boundary.height) / 2);
		awtCanvas.gfx.drawImage(image, boundary.x, boundary.y, null);
		//		awtCanvas.gfx.setColor(Color.orange);
		//		awtCanvas.gfx.drawRect(boundary.x, boundary.y, boundary.width,
		//				boundary.height);
	}

}
