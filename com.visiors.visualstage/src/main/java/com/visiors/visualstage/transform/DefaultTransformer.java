package com.visiors.visualstage.transform;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class DefaultTransformer extends SimpleTransformer implements Transform {

	public DefaultTransformer() {

		super();
	}

	public DefaultTransformer(DefaultTransformer t) {

		super(t);
		listener = t.listener;
	}

	//    /*
	//     * (non-Javadoc)
	//     * @see com.visiors.visualstage.view.transform.Transform#setTransform(com.visiors.visualstage.view.transform.
	//     * DefaultTransformer)
	//     */
	//    @Override
	//    public void setTransform(DefaultTransformer t) {
	//
	//        double s = getScaleX();
	//        super.setTransform(t);
	//        listener = t.listener;
	//        if (s != getScaleX()) {
	//            fireScaleValueChanged();
	//        }
	//    }

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#reset()
	 */
	@Override
	public void reset() {

		super.reset();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#setScale(double)
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#setShear(double)
	 */
	@Override
	public void setShear(double sx) {

		super.setShearX(sx);
		super.setShearY(sx);
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#setShearX(double)
	 */
	@Override
	public void setShearX(double sx) {

		super.setShearX(sx);
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#setShearY(double)
	 */
	@Override
	public void setShearY(double sy) {

		super.setShearY(sy);
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#setTranslateX(double)
	 */
	@Override
	public void setTranslateX(double tx) {

		super.setTranslateX(tx);
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#setTranslateY(double)
	 */
	@Override
	public void setTranslateY(double ty) {

		super.setTranslateY(ty);
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#setRotation(double)
	 */
	@Override
	public void setRotation(double alpha) {

		super.setToRotation(alpha);
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#getScaleX()
	 */
	@Override
	public double getScaleX() {

		return super.getScaleX();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#getScale()
	 */
	@Override
	public double getScale() {

		return super.getScaleX();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#getScaleY()
	 */
	@Override
	public double getScaleY() {

		return super.getScaleY();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#getShear()
	 */
	@Override
	public double getShear() {

		return super.getShearX();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#getShearX()
	 */
	@Override
	public double getShearX() {

		return super.getShearX();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#getShearY()
	 */
	@Override
	public double getShearY() {

		return super.getShearY();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#getTranslateX()
	 */
	@Override
	public double getTranslateX() {

		return super.getTranslateX();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#getTranslateY()
	 */
	@Override
	public double getTranslateY() {

		return super.getTranslateY();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#getRotation()
	 */
	@Override
	public double getRotation() {

		return super.getRotation();
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#transformToScreen(java.awt.Point)
	 */
	@Override
	public final Point transformToScreen(Point ptGraph) {

		return super.transform(ptGraph, new Point());
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#transformToGraph(java.awt.Point)
	 */
	@Override
	public final Point transformToGraph(Point ptScreen) {

		return createInverse().transform(ptScreen, new Point());
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#transformToScreen(java.awt.Rectangle)
	 */
	@Override
	public final Rectangle transformToScreen(Rectangle rGraph) {

		Point newLocation = transformToScreen(rGraph.getLocation());
		return new Rectangle(newLocation.x, newLocation.y, (int) (rGraph.width * getScaleX()),
				(int) (rGraph.height * getScaleY()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#transformToGraph(java.awt.Rectangle)
	 */
	@Override
	public final Rectangle transformToGraph(Rectangle rScreen) {

		Point newLocation = transformToScreen(rScreen.getLocation());
		return new Rectangle(newLocation.x, newLocation.y, (int) (rScreen.width * getScaleX()),
				(int) (rScreen.height * getScaleY()));

	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#transformToScreenX(int)
	 */
	@Override
	public int transformToScreenX(int x) {

		return transformX(x);
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#transformToScreenY(int)
	 */
	@Override
	public int transformToScreenY(int y) {

		return transformY(y);
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#transformToGraphX(int)
	 */
	@Override
	public int transformToGraphX(int x) {

		return createInverse().transformX(x);
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#transformToGraphY(int)
	 */
	@Override
	public int transformToGraphY(int y) {

		return createInverse().transformY(y);
	}

	// ////////////////////////////////////////////////////////
	// Listener

	protected List<TransformValueChangeListener> listener = new ArrayList<TransformValueChangeListener>();

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#addListener(com.visiors.visualstage.view.transform.
	 * TransformValueChangeListener)
	 */
	@Override
	public void addListener(TransformValueChangeListener l) {

		if (!listener.contains(l)) {
			listener.add(l);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.visiors.visualstage.view.transform.Transform#removeListener(com.visiors.visualstage.view.transform.
	 * TransformValueChangeListener)
	 */
	@Override
	public void removeListener(TransformValueChangeListener l) {

		listener.remove(l);
	}

	protected void fireScaleValueChanged() {

		for (TransformValueChangeListener l : listener) {
			l.scaleValuesChanged();
		}
	}

}
