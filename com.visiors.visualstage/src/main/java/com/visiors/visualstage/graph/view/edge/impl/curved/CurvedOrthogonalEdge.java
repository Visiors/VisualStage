package com.visiors.visualstage.graph.view.edge.impl.curved;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import org.apache.batik.ext.awt.image.codec.util.PropertyUtil;

import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.orthogonal.OrthogonalEdge;
import com.visiors.visualstage.svg.SVGDescriptor;
import com.visiors.visualstage.svg.SVGUtil;

public class CurvedOrthogonalEdge extends OrthogonalEdge {

	private SVGDescriptor baselineDef;


	public CurvedOrthogonalEdge(String name) {

		super(name);
	}

	protected CurvedOrthogonalEdge(String name, long id) {

		super(name, id);
	}

	protected CurvedOrthogonalEdge(VisualEdge edge, long id) {

		super(edge, id);
	}

	@Override
	protected void init() {

		super.init();
		baselineDef = svgDescriptorPool.get(Constants.DEFAULT_EDGE_BASELINE);

		properties = PropertyUtil.setProperty(properties, PropertyConstants.EDGE_PROPERTY_TYPE,
				PropertyConstants.EDGE_PROPERTY_TYPE_CURVED_ORTHOGONAL);
		PropertyUtil.makeEditable(properties, PropertyConstants.EDGE_PROPERTY_TYPE, false);
	}

	@Override
	public VisualEdge deepCopy(long id) {

		return new CurvedOrthogonalEdge(this, id);
	}

	@Override
	protected String getLineDescriptor() {

		if (svgLineDef != null) {
			StringBuffer svg = new StringBuffer();
			svg.append("<g transform='scale(");
			svg.append(transform.getScaleX());
			svg.append(",");
			svg.append(transform.getScaleY());
			svg.append(")'> ");
			svg.append(svgLineDef.definition);

			svg.append("</g>");
			return updatedSVGDescription(svg.toString());
		}
		return null;
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
		GeneralPath genPath = new GeneralPath();

		genPath.moveTo(points[0].x, points[0].y);
		int len = points.length;
		if (points.length == 2) {
			genPath.lineTo(points[1].x, points[1].y);
		} else {

			for (int i = 1; i < len - 2; i++) {

				x1 = points[i].x;
				y1 = points[i].y;
				x2 = points[i].x + (points[i + 1].x - points[i].x) / 2;
				y2 = points[i].y + (points[i + 1].y - points[i].y) / 2;
				genPath.quadTo(x1, y1, x2, y2);
			}

			x1 = points[len - 2].x;
			y1 = points[len - 2].y;
			x2 = points[len - 1].x;
			y2 = points[len - 1].y;
			genPath.quadTo(x1, y1, x2, y2);
		}
		return genPath;
	}
}
