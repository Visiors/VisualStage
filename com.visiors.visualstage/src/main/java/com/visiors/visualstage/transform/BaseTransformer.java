package com.visiors.visualstage.transform;

import java.awt.Point;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

class BaseTransformer implements Cloneable {

	static final long   serialVersionUID = 1330973210523860834L;

	static final double ZERO             = 1E-10;

	double              m00;
	double              m10;
	double              m01;
	double              m11;
	double              m02;
	double              m12;

	private double      rotation;

	BaseTransformer() {

		m00 = m11 = 1.0;
		m10 = m01 = m02 = m12 = 0.0;
		rotation = 0;
	}

	BaseTransformer(BaseTransformer t) {

		m00 = t.m00;
		m10 = t.m10;
		m01 = t.m01;
		m11 = t.m11;
		m02 = t.m02;
		m12 = t.m12;
		rotation = t.rotation;
	}

	BaseTransformer(double m00, double m10, double m01, double m11, double m02, double m12, double rotation) {

		this.m00 = m00;
		this.m10 = m10;
		this.m01 = m01;
		this.m11 = m11;
		this.m02 = m02;
		this.m12 = m12;
		this.rotation = rotation;
	}

	double getScaleX() {

		return m00;
	}

	double getScaleY() {

		return m11;
	}

	double getShearX() {

		return m01;
	}

	double getShearY() {

		return m10;
	}

	double getXTranslate() {

		return m02;
	}

	double getYTranslate() {

		return m12;
	}

	void setScaleX(double sx) {

		m00 = sx;
	}

	void setScaleY(double sy) {

		m11 = sy;
	}

	void setShearX(double sx) {

		m01 = sx;
	}

	void setShearY(double sy) {

		m10 = sy;
	}

	void setXTranslate(double tx) {

		m02 = tx;
	}

	void setYTranslate(double ty) {

		m12 = ty;
	}

	void getMatrix(double[] matrix) {

		matrix[0] = m00;
		matrix[1] = m10;
		matrix[2] = m01;
		matrix[3] = m11;
		if (matrix.length > 4) {
			matrix[4] = m02;
			matrix[5] = m12;
		}
	}

	void setTransform(double m00, double m10, double m01, double m11, double m02, double m12, double rotation) {

		this.m00 = m00;
		this.m10 = m10;
		this.m01 = m01;
		this.m11 = m11;
		this.m02 = m02;
		this.m12 = m12;
		this.rotation = rotation;
	}

	void setTransform(BaseTransformer t) {

		setTransform(t.m00, t.m10, t.m01, t.m11, t.m02, t.m12, t.rotation);
	}

	void reset() {

		m00 = m11 = 1.0;
		m10 = m01 = m02 = m12 = 0.0;
	}

	void setToRotation(double angle) {

		rotation = angle;
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);
		if (Math.abs(cos) < BaseTransformer.ZERO) {
			cos = 0.0;
			sin = sin > 0.0 ? 1.0 : -1.0;
		} else if (Math.abs(sin) < BaseTransformer.ZERO) {
			sin = 0.0;
			cos = cos > 0.0 ? 1.0 : -1.0;
		}
		m00 = m11 = cos;
		m01 = -sin;
		m10 = sin;
		m02 = m12 = 0.0;
	}

	void setToRotation(double angle, double px, double py) {

		setToRotation(angle);
		m02 = px * (1.0 - m00) + py * m10;
		m12 = py * (1.0 - m00) - px * m10;
	}

	double getRotation() {

		return rotation;
	}

	double getDeterminant() {

		return m00 * m11 - m01 * m10;
	}

	BaseTransformer createInverse() {

		double det = getDeterminant();
		return new BaseTransformer(m11 / det, // m00
				-m10 / det, // m10
				-m01 / det, // m01
				m00 / det, // m11
				(m01 * m12 - m11 * m02) / det, // m02
				(m10 * m02 - m00 * m12) / det, // m12
				rotation);
	}

	Point2D transform(Point2D src, Point2D dst) {

		if (dst == null) {
			if (src instanceof Point2D.Double) {
				dst = new Point2D.Double();
			} else {
				dst = new Point2D.Float();
			}
		}

		double x = src.getX();
		double y = src.getY();

		dst.setLocation(Math.round(x * m00) + Math.round(y * m01 + m02), Math.round(x * m10 + y * m11 + m12));
		return dst;
	}

	Point transform(Point src, Point dst) {

		dst.setLocation(Math.round(src.x * m00 + src.y * m01 + m02), Math.round(src.x * m10 + src.y * m11 + m12));
		return dst;
	}

	int transformX(int srcX) {

		return (int) (srcX * m00 + +m02);
	}

	int transformY(int srcY) {

		return (int) (srcY * m11 + m12);
	}

	void transform(Point2D[] src, int srcOff, Point2D[] dst, int dstOff, int length) {

		while (--length >= 0) {
			Point2D srcPoint = src[srcOff++];
			double x = srcPoint.getX();
			double y = srcPoint.getY();
			Point2D dstPoint = dst[dstOff];
			if (dstPoint == null) {
				if (srcPoint instanceof Point2D.Double) {
					dstPoint = new Point2D.Double();
				} else {
					dstPoint = new Point2D.Float();
				}
			}
			dstPoint.setLocation(x * m00 + y * m01 + m02, x * m10 + y * m11 + m12);
			dst[dstOff++] = dstPoint;
		}
	}

	void transform(double[] src, int srcOff, double[] dst, int dstOff, int length) {

		int step = 2;
		if (src == dst && srcOff < dstOff && dstOff < srcOff + length * 2) {
			srcOff = srcOff + length * 2 - 2;
			dstOff = dstOff + length * 2 - 2;
			step = -2;
		}
		while (--length >= 0) {
			double x = src[srcOff + 0];
			double y = src[srcOff + 1];
			dst[dstOff + 0] = x * m00 + y * m01 + m02;
			dst[dstOff + 1] = x * m10 + y * m11 + m12;
			srcOff += step;
			dstOff += step;
		}
	}

	void transform(double[] src, int srcOff, float[] dst, int dstOff, int length) {

		while (--length >= 0) {
			double x = src[srcOff++];
			double y = src[srcOff++];
			dst[dstOff++] = (float) (x * m00 + y * m01 + m02);
			dst[dstOff++] = (float) (x * m10 + y * m11 + m12);
		}
	}

	Point2D deltaTransform(Point2D src, Point2D dst) {

		if (dst == null) {
			if (src instanceof Point2D.Double) {
				dst = new Point2D.Double();
			} else {
				dst = new Point2D.Float();
			}
		}

		double x = src.getX();
		double y = src.getY();

		dst.setLocation(x * m00 + y * m01, x * m10 + y * m11);
		return dst;
	}

	void deltaTransform(double[] src, int srcOff, double[] dst, int dstOff, int length) {

		while (--length >= 0) {
			double x = src[srcOff++];
			double y = src[srcOff++];
			dst[dstOff++] = x * m00 + y * m01;
			dst[dstOff++] = x * m10 + y * m11;
		}
	}

	Point2D inverseTransform(Point2D src, Point2D dst) throws NoninvertibleTransformException {

		double det = getDeterminant();

		if (dst == null) {
			if (src instanceof Point2D.Double) {
				dst = new Point2D.Double();
			} else {
				dst = new Point2D.Float();
			}
		}

		double x = src.getX() - m02;
		double y = src.getY() - m12;

		dst.setLocation((x * m11 - y * m01) / det, (y * m00 - x * m10) / det);
		return dst;
	}

	void inverseTransform(double[] src, int srcOff, double[] dst, int dstOff, int length)
			throws NoninvertibleTransformException {

		double det = getDeterminant();
		while (--length >= 0) {
			double x = src[srcOff++] - m02;
			double y = src[srcOff++] - m12;
			dst[dstOff++] = (x * m11 - y * m01) / det;
			dst[dstOff++] = (y * m00 - x * m10) / det;
		}
	}

	@Override
	public String toString() {

		return getClass().getName() + "[[" + m00 + ", " + m01 + ", " + m02 + "], [" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ m10 + ", " + m11 + ", " + m12 + "]]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public Object clone() {

		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}
		if (obj instanceof BaseTransformer) {
			BaseTransformer t = (BaseTransformer) obj;
			return m00 == t.m00 && m01 == t.m01 && m02 == t.m02 && m10 == t.m10 && m11 == t.m11 && m12 == t.m12;
		}
		return false;
	}

}
