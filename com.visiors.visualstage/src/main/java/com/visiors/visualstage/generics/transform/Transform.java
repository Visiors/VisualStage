package com.visiors.visualstage.generics.transform;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Transform extends AffineTransform {

	public Transform() {
		super();
	}

	public Transform(Transform t) {
		super(t);
		listener = t.listener;
	}

	public void setTransform(Transform t) {

		double s = getScaleX();
		super.setTransform(t);
		listener = t.listener;
		if (s != getScaleX()) {
			fireScaleValueChanged();
		}
	}

	@Override
	public void reset() {
		super.reset();
	}

	public void setScale(double s) {
		if (super.getScaleX() != s) {
			setScaleX(s);
			setScaleY(s);
			fireScaleValueChanged();
		}
	}

	// @Override
	// public void setScaleX(double sx) {
	// super.setScaleX(sx);
	// }
	//
	//
	// @Override
	// public void setScaleY(double sy) {
	// super.setScaleY(sy);
	// }

	public void setShear(double sx) {
		super.setShearX(sx);
		super.setShearY(sx);
	}

	@Override
	public void setShearX(double sx) {
		super.setShearX(sx);
	}

	@Override
	public void setShearY(double sy) {
		super.setShearY(sy);
	}

	@Override
	public void setTranslateX(double tx) {
		super.setTranslateX(tx);
	}

	@Override
	public void setTranslateY(double ty) {
		super.setTranslateY(ty);
	}

	public void setRotation(double alpha) {
		super.setToRotation(alpha);
	}

	@Override
	public double getScaleX() {
		return super.getScaleX();
	}

	public double getScale() {
		return super.getScaleX();
	}

	@Override
	public double getScaleY() {
		return super.getScaleY();
	}

	public double getShear() {
		return super.getShearX();
	}

	@Override
	public double getShearX() {
		return super.getShearX();
	}

	@Override
	public double getShearY() {
		return super.getShearY();
	}

	@Override
	public double getTranslateX() {
		return super.getTranslateX();
	}

	@Override
	public double getTranslateY() {
		return super.getTranslateY();
	}

	@Override
	public double getRotation() {
		return super.getRotation();
	}

	public final Point transformToScreen(Point ptGraph) {
		return super.transform(ptGraph, new Point());
	}

	public final Point transformToGraph(Point ptScreen) {
		return createInverse().transform(ptScreen, new Point());
	}

	public final Rectangle transformToScreen(Rectangle rGraph) {

		Point newLocation = transformToScreen(rGraph.getLocation());
		return new Rectangle(newLocation.x, newLocation.y, (int) (rGraph.width * getScaleX()),
				(int) (rGraph.height * getScaleY()));
	}

	public final Rectangle transformToGraph(Rectangle rScreen) {

		Point newLocation = transformToScreen(rScreen.getLocation());
		return new Rectangle(newLocation.x, newLocation.y, (int) (rScreen.width * getScaleX()),
				(int) (rScreen.height * getScaleY()));

	}

	public int transformToScreenX(int x) {
		return transformX(x);
	}

	public int transformToScreenY(int y) {
		return transformY(y);
	}

	public int transformToGraphX(int x) {
		return createInverse().transformX(x);
	}

	public int transformToGraphY(int y) {
		return createInverse().transformY(y);
	}

	// ////////////////////////////////////////////////////////
	// Listener

	protected List<TransformValueChangeListener> listener = new ArrayList<TransformValueChangeListener>();

	public void addListener(TransformValueChangeListener l) {
		if (!listener.contains(l)) {
			this.listener.add(l);
		}
	}

	public void removeListener(TransformValueChangeListener l) {
		this.listener.remove(l);
	}

	protected void fireScaleValueChanged() {

		for (TransformValueChangeListener l : listener) {
			l.scaleValuesChanged();
		}
	}

}
