package com.visiors.visualstage.renderer;

import java.awt.Rectangle;

import com.google.common.base.Objects;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;

public class DefaultDrawingContext implements DrawingContext {

	private final Resolution resolution;
	private final Rectangle visibleBounds;
	private final Transform transform;

	//
	// public DefaultDrawingContext(Canvas canvas) {
	//
	// this(canvas.getResolution(), canvas.getCanvasBounds(),
	// canvas.getTransform());
	// }

	public DefaultDrawingContext(Resolution resolution, Rectangle visibleBounds, Transform transform) {

		this.resolution = resolution;
		this.visibleBounds = new Rectangle(visibleBounds);
		this.transform = new DefaultTransformer((DefaultTransformer) transform);

	}

	@Override
	public Resolution getResolution() {

		return resolution;
	}



	@Override
	public Rectangle getViewport() {

		return visibleBounds;
	}


	@Override
	public Transform getTransform() {

		return transform;
	}



	@Override
	public boolean equals(Object obj) {

		if (obj instanceof DefaultDrawingContext) {
			final DefaultDrawingContext other = (DefaultDrawingContext) obj;
			return Objects.equal(resolution, other.resolution) && Objects.equal(transform, other.transform)
					&& Objects.equal(visibleBounds, other.visibleBounds);
		}
		return false;
	}

	@Override
	public int hashCode() {

		return Objects.hashCode(resolution, visibleBounds, transform);
	}

	@Override
	public String toString() {

		return "Resoluton: " + resolution.toString();
	}
	//
	// @Override
	// protected Object clone() {
	//
	// final DefaultDrawingContext copy = new DefaultDrawingContext(resolution,
	// subject, new Rectangle(visibleBounds),
	// new DefaultTransformer((DefaultTransformer) transform));
	// copy.setImage(outputImage);
	// return copy;
	// }
}
