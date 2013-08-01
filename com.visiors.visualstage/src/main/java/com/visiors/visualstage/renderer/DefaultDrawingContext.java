package com.visiors.visualstage.renderer;

import java.awt.Rectangle;

import com.google.common.base.Objects;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;

public class DefaultDrawingContext implements DrawingContext {

	private Resolution resolution;
	private Rectangle bounds;
	private Transform transform;

	//
	// public DefaultDrawingContext(Canvas canvas) {
	//
	// this(canvas.getResolution(), canvas.getCanvasBounds(),
	// canvas.getTransform());
	// }

	public DefaultDrawingContext(Resolution resolution, Rectangle bounds, Transform transform) {

		this.resolution = resolution;
		this.bounds = new Rectangle(bounds);
		this.transform = new DefaultTransformer((DefaultTransformer) transform);

	}

	@Override
	public Resolution getResolution() {

		return resolution;
	}

	@Override
	public void setResolution(Resolution resolution) {

		this.resolution = resolution;

	}

	@Override
	public Rectangle getBounds() {

		return bounds;
	}

	@Override
	public void setBounds(Rectangle bounds) {

		this.bounds = bounds;

	}

	@Override
	public Transform getTransform() {

		return transform;
	}

	@Override
	public void setTransform(Transform transform) {

		this.transform = transform;

	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof DefaultDrawingContext) {
			final DefaultDrawingContext other = (DefaultDrawingContext) obj;
			return Objects.equal(resolution, other.resolution) && Objects.equal(transform, other.transform)
					&& Objects.equal(bounds, other.bounds);
		}
		return false;
	}

	@Override
	public int hashCode() {

		return Objects.hashCode(resolution, bounds, transform);
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
	// subject, new Rectangle(bounds),
	// new DefaultTransformer((DefaultTransformer) transform));
	// copy.setImage(outputImage);
	// return copy;
	// }
}
