package com.visiors.visualstage.graph.view.edge.impl.orthogonal.rounded;

import java.awt.Point;
import java.util.List;

import org.apache.batik.ext.awt.image.codec.util.PropertyUtil;

import com.visiors.visualstage.generics.attribute.PropertyList;
import com.visiors.visualstage.generics.attribute.PropertyUnit;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.orthogonal.OrthogonalEdge;

public class RoundedOrthogonalEdge extends OrthogonalEdge {

	private int rounding = 6;

	public RoundedOrthogonalEdge(String name) {

		super(name);
	}

	protected RoundedOrthogonalEdge(String name, long id) {

		super(name, id);
	}

	protected RoundedOrthogonalEdge(VisualEdge edge, long id) {

		super(edge, id);
	}

	@Override
	protected void init() {

		super.init();
		properties = PropertyUtil.setProperty(properties, PropertyConstants.EDGE_PROPERTY_TYPE,
				PropertyConstants.EDGE_PROPERTY_TYPE_CURVED_ORTHOGONAL);
		PropertyUtil.makeEditable(properties, PropertyConstants.EDGE_PROPERTY_TYPE, false);
		properties = PropertyUtil.setProperty(properties, PropertyConstants.EDGE_PROPERTY_ROUNGING,
				rounding);
	}

	@Override
	public void setProperties(PropertyList properties) {

		super.setProperties(properties);
		rounding = PropertyUtil
				.getProperty(properties, PropertyConstants.EDGE_PROPERTY_ROUNGING, 0);
	}

	@Override
	public void propertyChanged(List<PropertyList> path, PropertyUnit property) {

		super.propertyChanged(path, property);
		String str = PropertyUtil.toString(path, property);
		if (PropertyConstants.EDGE_PROPERTY_ROUNGING.equals(str)) {
			rounding = ConvertUtil.object2int(property.getValue());
		}
	}

	@Override
	public VisualEdge deepCopy(long id) {

		return new RoundedOrthogonalEdge(this, id);
	}

	@Override
	protected String getLineDescriptor() {

		if (svgLineDef != null) {
			StringBuffer svg = new StringBuffer();

			svg.append("<g transformer='scale(");
			svg.append(transformer.getScaleX());
			svg.append(",");
			svg.append(transformer.getScaleY());
			svg.append(")'> ");
			svg.append(svgLineDef.definition);
			svg.append("</g>");
			return updatedSVGDescription(svg.toString());
		}
		return null;
	}

	@Override
	protected String updatedSVGDescription(String description) {

		String strPoints = getPathDescription();
		return SVGUtil.setElementAttribute(description, "path", "d", strPoints);
	}

	@Override
	protected String getPathDescription() {

		if (rounding == 0) {
			return super.getPathDescription();
		}

		int radius = rounding;
		final Point[] points = path.getPoints();
		final StringBuffer sb = new StringBuffer();

		Point p0;
		Point p1;
		Point p2;

		SVGUtil.moveTo(sb, points[0].x, points[0].y);
		for (int i = 1; i < points.length - 1; i++) {
			p0 = points[i - 1];
			p1 = points[i];
			p2 = points[i + 1];
			if (p0.x == p1.x && p1.x != p2.x) {
				if (p0.y < p1.y) // //// -> South
				{
					radius = Math.min(radius, (p1.y - p0.y) / 2);
					if (p1.x < p2.x) // East.
					{
						radius = Math.min(radius, (p2.x - p1.x) / 2);
						SVGUtil.lineTo(sb, p1.x, p1.y - radius);
						SVGUtil.curveEllipticalTo(sb, p1.x + radius, p1.y, radius, false);

					} else if (p1.x > p2.x)// West.
					{
						radius = Math.min(radius, (p1.x - p2.x) / 2);
						SVGUtil.lineTo(sb, p1.x, p1.y - radius);
						SVGUtil.curveEllipticalTo(sb, p1.x - radius, p1.y, radius, true);
					}
				} else if (p0.y > p1.y) // //// -> North
				{
					radius = Math.min(radius, (p0.y - p1.y) / 2);
					if (p1.x < p2.x) // East.
					{
						radius = Math.min(radius, (p2.x - p1.x) / 2);
						SVGUtil.lineTo(sb, p1.x, p1.y + radius);
						SVGUtil.curveEllipticalTo(sb, p1.x + radius, p1.y, radius, true);
					} else if (p1.x > p2.x)// West
					{
						radius = Math.min(radius, (p1.x - p2.x) / 2);
						SVGUtil.lineTo(sb, p1.x, p1.y + radius);
						SVGUtil.curveEllipticalTo(sb, p1.x - radius, p1.y, radius, false);
					}
				}
			} else if (p0.y == p1.y && p1.y != p2.y) {
				if (p0.x < p1.x) // //// -> East
				{
					if (p1.y < p2.y) // South.
					{
						radius = Math.min(radius, (p2.y - p1.y) / 2);
						SVGUtil.lineTo(sb, p1.x - radius, p1.y);
						SVGUtil.curveEllipticalTo(sb, p1.x, p1.y + radius, radius, true);
					} else if (p1.y > p2.y)// North.
					{
						radius = Math.min(radius, (p1.y - p2.y) / 2);
						SVGUtil.lineTo(sb, p1.x - radius, p1.y);
						SVGUtil.curveEllipticalTo(sb, p1.x, p1.y - radius, radius, false);
					}
				} else if (p0.x > p1.x) // //// -> West.
				{
					radius = Math.min(radius, (p0.x - p1.x) / 2);
					if (p1.y < p2.y) // South.
					{
						radius = Math.min(radius, (p2.y - p1.y) / 2);
						SVGUtil.lineTo(sb, p1.x + radius, p1.y);
						SVGUtil.curveEllipticalTo(sb, p1.x, p1.y + radius, radius, false);
					} else if (p1.y > p2.y) // North.
					{
						radius = Math.min(radius, (p1.y - p2.y) / 2);
						SVGUtil.lineTo(sb, p1.x + radius, p1.y);
						SVGUtil.curveEllipticalTo(sb, p1.x, p1.y - radius, radius, true);
					}
				}
			} else {
				// System.err.println("Warning: Bend rounding has failed because of "
				// + "an non-orthogonal edge!");
			}
		}
		SVGUtil.lineTo(sb, points[points.length - 1].x, points[points.length - 1].y);

		return sb.toString();
	}

}
