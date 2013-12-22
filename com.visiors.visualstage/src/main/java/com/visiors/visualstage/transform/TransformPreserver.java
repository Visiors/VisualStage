package com.visiors.visualstage.transform;

public class TransformPreserver {

	private final Transform xform;
	private double xTranslate;
	private double yTranslate;
	private double scale;
	private int xViewport;
	private int yViewport;

	public TransformPreserver(Transform xform) {

		this.xform = xform;
	}

	public TransformPreserver store() {

		this.xTranslate = xform.getXTranslate();
		this.yTranslate = xform.getYTranslate();
		this.scale = xform.getScale();
		this.xViewport = xform.getViewX();
		this.yViewport = xform.getViewY();
		return this;
	}

	public TransformPreserver reset() {

		resetTranslate();
		resetScale();
		resetViewport();
		return this;
	}

	public TransformPreserver resetTranslate() {

		xform.setXTranslate(0);
		xform.setYTranslate(0);
		return this;
	}

	public TransformPreserver resetScale() {

		xform.setScale(1.0);
		return this;
	}

	public TransformPreserver resetViewport() {

		xform.setViewportX(0);
		xform.setViewportY(0);
		return this;
	}

	public void restore() {

		xform.setXTranslate(this.xTranslate);
		xform.setYTranslate(this.yTranslate);
		xform.setViewportX(xViewport);
		xform.setViewportY(yViewport);
		xform.setScale(this.scale);
	}

	public void setScale(double scale) {

		xform.setScale(scale);		
	}

	public TransformPreserver setTranslate(int dx, int dy) {

		xform.setXTranslate(dx);
		xform.setYTranslate(dy);
		return this;
	}
}
