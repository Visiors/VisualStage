package com.visiors.visualstage.transform;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class DefaultTransformer extends BaseTransformer implements Transform {

	private int viewportWidth;
	private int viewportHeight;
	private int viewportX;
	private int viewportY;

	public DefaultTransformer() {

		super();
	}

	public DefaultTransformer(DefaultTransformer t) {

		super(t);
		listener = t.listener;
	}


	@Override
	public void setScale(double s) {

		setScale(s, false);
	}


	@Override
	public void setXTranslate(double tx) {

		setXTranslate(tx, false);

	}


	@Override
	public void setYTranslate(double ty) {

		setYTranslate(ty, false);

	}
	@Override
	public void setScale(double s, boolean 	suppressEvent) {

		if (super.getScaleX() != s) {
			setScaleX(s);
			setScaleY(s);
			if(!suppressEvent) {
				fireScaleChanged();
			}
		}
	}


	@Override
	public void setXTranslate(double tx, boolean 	suppressEvent) {

		super.setXTranslate(tx);
		if(!suppressEvent) {
			fireXTraslateChanged();
		}
	}


	@Override
	public void setYTranslate(double ty, boolean 	suppressEvent) {

		super.setYTranslate(ty);
		if(!suppressEvent) {
			fireYTraslateChanged();
		}
	}


	@Override
	public double getScale() {

		return super.getScaleX();
	}


	@Override
	public double getXTranslate() {

		return super.getXTranslate();
	}


	@Override
	public double getYTranslate() {

		return super.getYTranslate();
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
	public void setViewportX(int viewportX) {

		this.viewportX = viewportX;
	}


	@Override
	public int getViewX() {

		return viewportX;
	}

	@Override
	public int getViewY() {

		return viewportY;
	}


	@Override
	public void setViewportY(int viewportY) {

		this.viewportY = viewportY;
	}

	@Override
	public final Point transformToScreen(Point ptGraph) {

		Point screen = super.transform(new Point(ptGraph.x, ptGraph.y), new Point());
		screen.translate(viewportX, viewportY);
		return screen;
	}

	@Override
	public final Point transformToGraph(Point ptScreen) {

		final Point pt = new Point(ptScreen.x - viewportX, ptScreen.y - viewportY);
		return createInverse().transform(pt, new Point());
	}

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

	@Override
	public int transformToScreenX(int x) {

		return transformX(x) + viewportX;
	}

	@Override
	public int transformToScreenY(int y) {

		return transformY(y) + viewportY;
	}

	@Override
	public int transformToGraphX(int x) {

		return createInverse().transformX(x + viewportX);
	}

	@Override
	public int transformToGraphY(int y) {

		return createInverse().transformY(y + viewportY);
	}

	@Override
	public int transformToGraphDX(int w) {

		return (int) (w / getScale());
	}

	@Override
	public int transformToGraphDY(int h) {

		return (int) (h / getScale());
	}

	@Override
	public int transformToScreenDX(int w) {

		return (int) (w * getScale());
	}

	@Override
	public int transformToScreenDY(int h) {

		return (int) (h * getScale());
	}

	// ////////////////////////////////////////////////////////
	// Listener

	protected List<TransformListener> listener = new ArrayList<TransformListener>();

	@Override
	public void addListener(TransformListener l) {

		if (!listener.contains(l)) {
			listener.add(l);
		}

	}

	@Override
	public void removeListener(TransformListener l) {

		listener.remove(l);
	}

	protected void fireScaleChanged() {

		for (TransformListener l : listener) {
			l.scaleChanged();
		}
	}

	protected void  fireXTraslateChanged() {
		for (TransformListener l : listener) {
			l.xTranslateChanged();
		}
	}

	protected void  fireYTraslateChanged() {
		for (TransformListener l : listener) {
			l.yTranslateChanged();
		}
	}
}

