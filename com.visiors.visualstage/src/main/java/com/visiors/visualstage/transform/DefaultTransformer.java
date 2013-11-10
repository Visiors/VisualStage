package com.visiors.visualstage.transform;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class DefaultTransformer extends SimpleTransformer implements Transform {

	private int viewportWidth;
	private int viewportHeight;
	private Rectangle clientBounds = new Rectangle();

	public DefaultTransformer() {

		super();
	}

	public DefaultTransformer(DefaultTransformer t) {

		super(t);
		listener = t.listener;
	}

	// /*
	// * (non-Javadoc)
	// * @see
	// com.visiors.visualstage.view.transform.Transform#setTransform(com.visiors.visualstage.view.transform.
	// * DefaultTransformer)
	// */
	// @Override
	// public void setTransform(DefaultTransformer t) {
	//
	// double s = getScaleX();
	// super.setTransform(t);
	// listener = t.listener;
	// if (s != getScaleX()) {
	// fireScaleValueChanged();
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#reset()
	 */
	@Override
	public void reset() {

		super.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#setShear(double)
	 */
	@Override
	public void setShear(double sx) {

		super.setShearX(sx);
		super.setShearY(sx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#setShearX(double)
	 */
	@Override
	public void setShearX(double sx) {

		super.setShearX(sx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#setShearY(double)
	 */
	@Override
	public void setShearY(double sy) {

		super.setShearY(sy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#setTranslateX(double)
	 */
	@Override
	public void setXTranslate(double tx) {

		super.setXTranslate(tx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#setTranslateY(double)
	 */
	@Override
	public void setYTranslate(double ty) {

		super.setYTranslate(ty);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#setRotation(double)
	 */
	@Override
	public void setRotation(double alpha) {

		super.setToRotation(alpha);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#getScaleX()
	 */
	@Override
	public double getScaleX() {

		return super.getScaleX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#getScale()
	 */
	@Override
	public double getScale() {

		return super.getScaleX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#getScaleY()
	 */
	@Override
	public double getScaleY() {

		return super.getScaleY();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#getShear()
	 */
	@Override
	public double getShear() {

		return super.getShearX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#getShearX()
	 */
	@Override
	public double getShearX() {

		return super.getShearX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#getShearY()
	 */
	@Override
	public double getShearY() {

		return super.getShearY();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#getTranslateX()
	 */
	@Override
	public double getXTranslate() {

		return super.getXTranslate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#getTranslateY()
	 */
	@Override
	public double getYTranslate() {

		return super.getYTranslate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.view.transform.Transform#getRotation()
	 */
	@Override
	public double getRotation() {

		return super.getRotation();
	}

	@Override
	public int getViewWidth() {

		return viewportWidth;
	}

	@Override
	public void setViewWidth(int w) {

		this.viewportWidth = w;
	}

	@Override
	public int getViewHeight() {

		return viewportHeight;
	}

	@Override
	public void setViewHeight(int h) {

		this.viewportHeight = h;
	}

	@Override
	public void setClientBounds(Rectangle clientBounds) {

		this.clientBounds = clientBounds;
	}

	@Override
	public Rectangle getClientBounds() {

		return new Rectangle(clientBounds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#transformToScreen(java
	 * .awt.Point)
	 */
	@Override
	public final Point transformToScreen(Point ptGraph) {

		Point screen = super.transform(new Point(ptGraph.x, ptGraph.y), new Point());
		screen.translate(clientBounds.x, clientBounds.y);
		return screen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#transformToGraph(java
	 * .awt.Point)
	 */
	@Override
	public final Point transformToGraph(Point ptScreen) {

		final Point pt = new Point(ptScreen.x - clientBounds.x, ptScreen.y - clientBounds.y);
		return createInverse().transform(pt, new Point());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#transformToScreen(java
	 * .awt.Rectangle)
	 */
	@Override
	public final Rectangle transformToScreen(Rectangle rGraph) {

		Point newLocation = transformToScreen(rGraph.getLocation());
		return new Rectangle(newLocation.x, newLocation.y, (int) (rGraph.width * getScaleX()),
				(int) (rGraph.height * getScaleY()));
	}

	@Override
	public Rectangle transformToGraph(Rectangle rScreen) {

		Point newLocation = transformToGraph(rScreen.getLocation());
		return new Rectangle(newLocation.x, newLocation.y, (int) (rScreen.width / getScaleX()),
				(int) (rScreen.height / getScaleY()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#transformToScreenX(int)
	 */
	@Override
	public int transformToScreenX(int x) {

		return transformX(x) + clientBounds.x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#transformToScreenY(int)
	 */
	@Override
	public int transformToScreenY(int y) {

		return transformY(y) - clientBounds.y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#transformToGraphX(int)
	 */
	@Override
	public int transformToGraphX(int x) {

		return createInverse().transformX(x + clientBounds.x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#transformToGraphY(int)
	 */
	@Override
	public int transformToGraphY(int y) {

		return createInverse().transformY(y - clientBounds.y);
	}

	// ////////////////////////////////////////////////////////
	// Listener

	protected List<TransformValueChangeListener> listener = new ArrayList<TransformValueChangeListener>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#addListener(com.visiors
	 * .visualstage.view.transform. TransformValueChangeListener)
	 */
	@Override
	public void addListener(TransformValueChangeListener l) {

		if (!listener.contains(l)) {
			listener.add(l);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.view.transform.Transform#removeListener(com.visiors
	 * .visualstage.view.transform. TransformValueChangeListener)
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
