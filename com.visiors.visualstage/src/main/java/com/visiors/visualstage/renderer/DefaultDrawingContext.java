package com.visiors.visualstage.renderer;

import java.awt.Rectangle;

import com.google.common.base.Objects;

public class DefaultDrawingContext implements DrawingContext {

	private Resolution resolution = Resolution.SCREEN;
	private Rectangle visibleBounds = new Rectangle();
	private DrawingSubject[] subjects = new DrawingSubject[] { DrawingSubject.OBJECT, DrawingSubject.PORTS,
			DrawingSubject.SELECTION_INDICATORS };

	public DefaultDrawingContext(Resolution resolution, Rectangle visibleBounds, DrawingSubject... subjects) {

		this.resolution = resolution;
		this.subjects = subjects;
		this.visibleBounds = new Rectangle(visibleBounds);
	}

	@Override
	public Resolution getResolution() {

		return resolution;
	}

	@Override
	public DrawingSubject[] getDrawingSubject() {

		return subjects;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof DefaultDrawingContext) {
			final DefaultDrawingContext other = (DefaultDrawingContext) obj;
			return Objects.equal(resolution, other.resolution) && Objects.equal(visibleBounds, other.visibleBounds);
		}
		return false;
	}

	@Override
	public int hashCode() {

		return Objects.hashCode(resolution, visibleBounds);
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
