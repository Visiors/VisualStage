package com.visiors.visualstage.graph.view.edge.impl.curved;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import org.apache.batik.ext.awt.image.codec.util.PropertyUtil;

import com.visiors.visualstage.constants.Constants;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.polyline.PolygonalEdgeView;
import com.visiors.visualstage.svg.SVGDescriptor;
import com.visiors.visualstage.svg.SVGUtil;

/* The quadratic Bézier curve  */
public class CurvedPolygonalEdge extends PolygonalEdgeView {

	private SVGDescriptor baselineDef;

	public CurvedPolygonalEdge() {

		super(-1);
	}

	protected CurvedPolygonalEdge(long id) {

		super(id);
	}

	protected CurvedPolygonalEdge(VisualEdge edge, long id) {

		super(edge, id);
	}

	@Override
	protected void init() {

		super.init();
		baselineDef = svgDescriptorPool.get(Constants.DEFAULT_EDGE_BASELINE);

		properties = PropertyUtil.setProperty(properties, PropertyConstants.EDGE_PROPERTY_TYPE,
				PropertyConstants.EDGE_PROPERTY_TYPE_CURVED_POLYGONAL);
		PropertyUtil.makeEditable(properties, PropertyConstants.EDGE_PROPERTY_TYPE, false);

	}

	@Override
	public VisualEdge deepCopy(long id) {

		return new CurvedPolygonalEdge(this, id);
	}

	@Override
	protected String getLineDescriptor() {

		if (presentationID != null) {
			SVGDescriptor def = svgDescriptorPool.get(presentationID);
			if (def != null) {
				StringBuffer svg = new StringBuffer();
				svg.append("<g transform='scale(");
				svg.append(transform.getScaleX());
				svg.append(",");
				svg.append(transform.getScaleY());
				svg.append(")'> ");
				svg.append(def.definition);
				svg.append("</g>");
				return updatedSVGDescription(svg.toString());
			}
		}
		return null;
	}

	@Override
	protected String updatedSVGDescription(String description) {

		final StringBuffer sb = new StringBuffer();

		String curvedpath = getPathDescription();
		sb.append(SVGUtil.setElementAttribute(description, "path", "d", curvedpath));

		return sb.toString();
	}

	@Override
	protected String getPathDescription() {

		final Point[] points = path.getPoints();
		final StringBuffer sb = new StringBuffer();
		int cx, cy, x, y;

		SVGUtil.moveTo(sb, points[0].x, points[0].y);
		int len = points.length;
		if (points.length == 2) {
			SVGUtil.lineTo(sb, points[1].x, points[1].y);
		} else {

			for (int i = 1; i < len - 2; i++) {
				cx = points[i].x;
				cy = points[i].y;
				x = points[i].x + (points[i + 1].x - points[i].x) / 2;
				y = points[i].y + (points[i + 1].y - points[i].y) / 2;
				SVGUtil.quadraticBezierTo(sb, x, y, cx, cy);
			}

			cx = points[len - 2].x;
			cy = points[len - 2].y;
			x = points[len - 1].x;
			y = points[len - 1].y;

			SVGUtil.quadraticBezierTo(sb, x, y, cx, cy);
		}
		return sb.toString();
	}

	@Override
	protected String getSelectionDescriptor() {

		String desc = super.getSelectionDescriptor();

		if (desc != null) {
			String polypath = getBaselineDescription();
			String baseline = "";
			baseline += ("<g transform='scale(");
			baseline += (transform.getScaleX());
			baseline += (",");
			baseline += (transform.getScaleY());
			baseline += (")'> ");
			baseline += (baselineDef.definition);
			baseline += ("</g>");
			desc += SVGUtil.setElementAttribute(baseline, "polyline", "points", polypath);
		}

		return desc;
	}

	protected String getBaselineDescription() {

		final Point[] points = path.getPoints();
		final StringBuffer sb = new StringBuffer();

		for (Point point : points) {
			sb.append(' ');
			sb.append(point.x);
			sb.append(',');
			sb.append(point.y);
		}
		return sb.toString();
	}

	@Override
	public boolean isHit(Point pt) {

		if (selected && super.isHit(pt)) {
			return true;
		}

		Path2D curvedpath = getSplinePath(path.getPoints());
		return curvedpath.intersects(pt.x - 2, pt.y - 2, 4, 4);
	}

	public Path2D getSplinePath(Point[] points) {

		int x1, y1, x2, y2;
		GeneralPath path = new GeneralPath();

		path.moveTo(points[0].x, points[0].y);
		int len = points.length;
		if (points.length == 2) {
			path.lineTo(points[1].x, points[1].y);
		} else {

			for (int i = 1; i < len - 2; i++) {

				x1 = points[i].x;
				y1 = points[i].y;
				x2 = points[i].x + (points[i + 1].x - points[i].x) / 2;
				y2 = points[i].y + (points[i + 1].y - points[i].y) / 2;
				path.quadTo(x1, y1, x2, y2);
			}

			x1 = points[len - 2].x;
			y1 = points[len - 2].y;
			x2 = points[len - 1].x;
			y2 = points[len - 1].y;
			path.quadTo(x1, y1, x2, y2);
		}
		return path;
	}
}
