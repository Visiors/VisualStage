package com.visiors.visualstage.graph.view.edge.impl.polyline;

import java.awt.Point;

import com.visiors.visualstage.constants.InteractionConstants;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.graph.view.DefaultVisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.DefaultVisualEdge;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.interaction.Interactable;
import com.visiors.visualstage.svg.SVGDescriptor;
import com.visiors.visualstage.svg.SVGUtil;
import com.visiors.visualstage.util.PropertyUtil;

public class PolygonalEdgeView extends DefaultVisualEdge {

	private static final int SEGMENT_START_INDEX = 0X000FFF;
	protected int manipulationID;
	protected boolean pinned;

	public PolygonalEdgeView() {

		this(-1);
	}

	protected PolygonalEdgeView(long id) {

		super(id);
	}

	protected PolygonalEdgeView(VisualEdge edge, long id) {

		super(edge, id);
	}

	@Override
	protected void initProperties() {

		super.initProperties();
		properties = PropertyUtil.setProperty(properties, PropertyConstants.EDGE_PROPERTY_TYPE,
				PropertyConstants.EDGE_PROPERTY_TYPE_POLYGONAL);
		PropertyUtil.makeEditable(properties, PropertyConstants.EDGE_PROPERTY_TYPE, false);
	}

	@Override
	public VisualEdge deepCopy(long id) {

		return new PolygonalEdgeView(this, id);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		manipulationID = DefaultVisualGraphObject.NONE;
		boolean altKeyPressed = (functionKey & Interactable.KEY_ALT) != 0;

		if (isSelected()) {
			if (AttributeUtil.isResizable(this)) {
				/* hit point? */
				int index = path.getHitPointIndex(pt);
				if (index != DefaultVisualGraphObject.NONE) {
					manipulationID = index;
					setPinned(true);
				}

				/* hit segment? */
				int hitSegmentindex = path.getHitSegmentIndex(pt);
				if (hitSegmentindex != DefaultVisualGraphObject.NONE) {

					if (altKeyPressed) { /* make sure we always hit a segment while inserting new points */
						manipulationID = PolygonalEdgeView.SEGMENT_START_INDEX + hitSegmentindex;
					}
				}
			}
		}
		return manipulationID != DefaultVisualGraphObject.NONE;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (manipulationID == DefaultVisualGraphObject.NONE) {
			return false;
		}

		int index = IDToIndex(manipulationID);
		/* manipulating the start-point */
		if (hitPoint(manipulationID)) {
			movePoint(index, pt);
			return false;
		}
		return true;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (manipulationID == DefaultVisualGraphObject.NONE) {
			return false;
		}

		startManipulating();
		boolean altKeyPressed = (functionKey & Interactable.KEY_ALT) != 0;

		if ((altKeyPressed)) {

			if (hitSegment(manipulationID)) {
				int index = IDToIndex(manipulationID);
				addPoint(pt, index);
				setSelected(true);
			} else if (hitPoint(manipulationID)) {
				removePointsAt(pt);
			}
		}

		return true;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (manipulationID != DefaultVisualGraphObject.NONE) {
			endManipulating();
			int index = IDToIndex(manipulationID);
			if (index != 0 && index != path.getSize() - 1) {
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return manipulationID != DefaultVisualGraphObject.NONE;
	}

	@Override
	public int getPreferredCursor() {

		if (manipulationID != DefaultVisualGraphObject.NONE) {
			return InteractionConstants.CURSOR_CROSSHAIR;
		}

		return InteractionConstants.CURSOR_DEFAULT;
	}

	public void setPinned(boolean pinned) {

		this.pinned = pinned;
	}

	public boolean isPinned() {

		return pinned;
	}

	protected void addPoint(Point target, int segIndex) {

		undoRedoHandler.stratOfGroupAction();
		path.insertPointAt(segIndex, target);
		undoRedoHandler.endOfGroupAction();
		setPinned(true);
	}

	protected void removePointsAt(final Point target) {

		Point[] points = path.getPoints();

		int idx = path.getHitPointIndex(target);
		// removing the end-points not allowed
		if (idx > 0 && idx < points.length - 1) {
			points = removeFromPointList(points, idx);
			path.setPoints(points, true);
			setPinned(true);
		}
	}

	private Point[] removeFromPointList(Point[] points, int idx) {

		Point[] newPoints = new Point[points.length - 1];
		System.arraycopy(points, 0, newPoints, 0, idx);
		System.arraycopy(points, idx + 1, newPoints, idx, points.length - idx - 1);
		return newPoints;
	}

	@Override
	protected void updatePath(boolean updateSource, boolean updateTarget) {

		super.updatePath(updateSource, updateTarget);
		if (!isPinned()) {
			setPoints(PolylineEdgeRouter.routeEdge(this));
		}
	}

	// protected void movePoint(int index, Point target) {
	//
	// if(index != 0 && index != path.size()-1) {
	// path.setPointAt(index , target);
	// return;
	// }
	// Point[] points = path.getPoints();
	//
	// if(tempResizingPath == null)
	// tempResizingPath = points;
	//
	// double dx = target.x - points[index].x;
	// double dy = target.y - points[index].y;
	// double w1 = tempResizingPath[points.length-1].x - tempResizingPath[0].x;
	// double h1 = tempResizingPath[points.length-1].y - tempResizingPath[0].y;
	// double w2;
	// double h2;
	// for (int i = 1; i < points.length-1; i++) {
	// w2 = tempResizingPath[i].x - tempResizingPath[0].x;
	// h2 = tempResizingPath[i].y - tempResizingPath[0].y;
	// points[i].x += Math.round(w2/w1 *dx) ;
	// points[i].y += Math.round(h2/h1 *dy) ;
	// }
	// points[index] = target;
	// path.setPoints(points);
	// }

	private int IDToIndex(int ID) {

		if (ID >= PolygonalEdgeView.SEGMENT_START_INDEX) {
			return ID - PolygonalEdgeView.SEGMENT_START_INDEX;
		}

		return ID;
	}

	private boolean hitPoint(int manipulationID) {

		return manipulationID < PolygonalEdgeView.SEGMENT_START_INDEX;
	}

	private boolean hitSegment(int manipulationID) {

		return manipulationID >= PolygonalEdgeView.SEGMENT_START_INDEX;
	}

	@Override
	protected void fireEdgeCoonectionChanged(VisualNode oldConnectedNode, int oldPortID, boolean sourceNodeChanged) {

		setPinned(false);
		super.fireEdgeReconnected(oldConnectedNode, oldPortID, sourceNodeChanged);
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

	protected String updatedSVGDescription(String description) {

		final String strPoints = getPathDescription();
		return SVGUtil.setElementAttribute(description, "polyline", "points", strPoints);
	}

	protected String getPathDescription() {

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

}
