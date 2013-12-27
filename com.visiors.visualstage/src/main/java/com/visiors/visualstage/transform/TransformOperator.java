package com.visiors.visualstage.transform;

public class TransformOperator {

	private final Transform xform;
	private double xTranslate;
	private double yTranslate;
	private double scale;
	private int xViewport;
	private int yViewport;
	private boolean triggerEvents;

	public TransformOperator(Transform xform) {

		this.xform = xform;
	}

	public TransformOperator triggerEvents(boolean triggerEvents) {

		this.triggerEvents = triggerEvents;
		return this;
	}

	public TransformOperator store() {

		this.xTranslate = xform.getXTranslate();
		this.yTranslate = xform.getYTranslate();
		this.scale = xform.getScale();
		this.xViewport = xform.getViewX();
		this.yViewport = xform.getViewY();
		return this;
	}

	public TransformOperator reset() {

		resetTranslate();
		resetScale();
		resetViewport();
		return this;
	}

	public TransformOperator resetTranslate() {

		xform.setXTranslate(0, triggerEvents);
		xform.setYTranslate(0, triggerEvents);
		return this;
	}

	public TransformOperator resetScale() {

		xform.setScale(1.0);
		return this;
	}

	public TransformOperator resetViewport() {

		xform.setViewportX(0);
		xform.setViewportY(0);
		return this;
	}

	public void restore() {

		xform.setXTranslate(this.xTranslate, triggerEvents);
		xform.setYTranslate(this.yTranslate, triggerEvents);
		xform.setViewportX(xViewport);
		xform.setViewportY(yViewport);
		xform.setScale(this.scale, triggerEvents);
	}

	public void setScale(double scale) {

		xform.setScale(scale, triggerEvents);
	}

	public TransformOperator setTranslate(int dx, int dy) {

		xform.setXTranslate(dx, triggerEvents);
		xform.setYTranslate(dy, triggerEvents);
		return this;
	}
}
