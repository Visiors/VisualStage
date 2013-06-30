package com.visiors.visualstage.view.transform;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class VSTransformer extends SimpleTransformer implements Transformer {

    public VSTransformer() {

        super();
    }

    public VSTransformer(VSTransformer t) {

        super(t);
        listener = t.listener;
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#setTransform(com.visiors.visualstage.view.transform.
     * DefaultTransformer)
     */
    @Override
    public void setTransform(VSTransformer t) {

        double s = getScaleX();
        super.setTransform(t);
        listener = t.listener;
        if (s != getScaleX()) {
            fireScaleValueChanged();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#reset()
     */
    @Override
    public void reset() {

        super.reset();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#setScale(double)
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
     * @see com.visiors.visualstage.view.transform.Transformer#setShear(double)
     */
    @Override
    public void setShear(double sx) {

        super.setShearX(sx);
        super.setShearY(sx);
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#setShearX(double)
     */
    @Override
    public void setShearX(double sx) {

        super.setShearX(sx);
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#setShearY(double)
     */
    @Override
    public void setShearY(double sy) {

        super.setShearY(sy);
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#setTranslateX(double)
     */
    @Override
    public void setTranslateX(double tx) {

        super.setTranslateX(tx);
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#setTranslateY(double)
     */
    @Override
    public void setTranslateY(double ty) {

        super.setTranslateY(ty);
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#setRotation(double)
     */
    @Override
    public void setRotation(double alpha) {

        super.setToRotation(alpha);
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#getScaleX()
     */
    @Override
    public double getScaleX() {

        return super.getScaleX();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#getScale()
     */
    @Override
    public double getScale() {

        return super.getScaleX();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#getScaleY()
     */
    @Override
    public double getScaleY() {

        return super.getScaleY();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#getShear()
     */
    @Override
    public double getShear() {

        return super.getShearX();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#getShearX()
     */
    @Override
    public double getShearX() {

        return super.getShearX();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#getShearY()
     */
    @Override
    public double getShearY() {

        return super.getShearY();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#getTranslateX()
     */
    @Override
    public double getTranslateX() {

        return super.getTranslateX();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#getTranslateY()
     */
    @Override
    public double getTranslateY() {

        return super.getTranslateY();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#getRotation()
     */
    @Override
    public double getRotation() {

        return super.getRotation();
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#transformToScreen(java.awt.Point)
     */
    @Override
    public final Point transformToScreen(Point ptGraph) {

        return super.transform(ptGraph, new Point());
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#transformToGraph(java.awt.Point)
     */
    @Override
    public final Point transformToGraph(Point ptScreen) {

        return createInverse().transform(ptScreen, new Point());
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#transformToScreen(java.awt.Rectangle)
     */
    @Override
    public final Rectangle transformToScreen(Rectangle rGraph) {

        Point newLocation = transformToScreen(rGraph.getLocation());
        return new Rectangle(newLocation.x, newLocation.y, (int) (rGraph.width * getScaleX()),
                (int) (rGraph.height * getScaleY()));
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#transformToGraph(java.awt.Rectangle)
     */
    @Override
    public final Rectangle transformToGraph(Rectangle rScreen) {

        Point newLocation = transformToScreen(rScreen.getLocation());
        return new Rectangle(newLocation.x, newLocation.y, (int) (rScreen.width * getScaleX()),
                (int) (rScreen.height * getScaleY()));

    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#transformToScreenX(int)
     */
    @Override
    public int transformToScreenX(int x) {

        return transformX(x);
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#transformToScreenY(int)
     */
    @Override
    public int transformToScreenY(int y) {

        return transformY(y);
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#transformToGraphX(int)
     */
    @Override
    public int transformToGraphX(int x) {

        return createInverse().transformX(x);
    }

    /*
     * (non-Javadoc)
     * @see com.visiors.visualstage.view.transform.Transformer#transformToGraphY(int)
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
     * @see com.visiors.visualstage.view.transform.Transformer#addListener(com.visiors.visualstage.view.transform.
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
     * @see com.visiors.visualstage.view.transform.Transformer#removeListener(com.visiors.visualstage.view.transform.
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
