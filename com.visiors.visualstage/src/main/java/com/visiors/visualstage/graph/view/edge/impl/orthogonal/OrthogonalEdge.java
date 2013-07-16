package com.visiors.visualstage.graph.view.edge.impl.orthogonal;

import java.awt.Point;

import org.apache.batik.ext.awt.image.codec.util.PropertyUtil;

import com.visiors.visualstage.generics.attribute.PropertyList;
import com.visiors.visualstage.generics.interaction.Interactable;
import com.visiors.visualstage.graph.view.DefaultVisualGraphObject;
import com.visiors.visualstage.graph.view.ViewConstants;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.curved.polyline.PolygonalEdgeView;
import com.visiors.visualstage.resource.SVGDefinition;
import com.visiors.visualstage.resource.SVGDefinitionPool;

public class OrthogonalEdge extends PolygonalEdgeView {

	private static final int FIRST_EDGE_POINT_INDEX = 0X000000;
	private static final int LAST_EDGE_POINT_INDEX = 0X000001;
	private static final int CORNER_POINT_START_INDEX = 0X0000FF;
	private static final int SEGMENT_START_INDEX = 0X000FFF;
	private static final int SEGMENT_CENTER_START_INDEX = 0X00FFFF;

	private Point start;
	private Point end;
	private final int distanceToNode = 10;
	private int outputDirection;
	private int inputDirection;
	private int manipulationID;
	protected SVGDefinition svgManDef;

	public OrthogonalEdge(String name) {

		super(name);
	}

	protected OrthogonalEdge(String name, long id) {

		super(name, id);
	}

	protected OrthogonalEdge(VisualEdge edge, long id) {

		super(edge, id);
	}

	@Override
	protected void init() {

		super.init();
		svgManDef = SVGDefinitionPool.get(Constants.DEFAULT_EDGE_MANIPULATION_HANDEL);

		properties = PropertyUtil.setProperty(properties, PropertyConstants.EDGE_PROPERTY_TYPE,
				PropertyConstants.EDGE_PROPERTY_TYPE_ORTHOGONAL);
		PropertyUtil.makeEditable(properties, PropertyConstants.EDGE_PROPERTY_TYPE, false);
	}

	@Override
	public VisualEdge deepCopy(long id) {

		return new OrthogonalEdge(this, id);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		manipulationID = DefaultVisualGraphObject.NONE;
		boolean altKeyPressed = (functionKey & Interactable.KEY_ALT) != 0;

		if (isSelected()) {
			if (AttributeUtil.isResizable(this)) {

				int size = path.getSize();
				/* hit point? */
				int index = path.getHitPointIndex(pt);
				if (index != DefaultVisualGraphObject.NONE) {
					if (index == 0) {
						manipulationID = OrthogonalEdge.FIRST_EDGE_POINT_INDEX; /*
																				 * start
																				 * point
																				 * hit
																				 */
					} else if (index == size - 1) {
						manipulationID = OrthogonalEdge.LAST_EDGE_POINT_INDEX; /*
																			    * end
																			    * point
																			    * hit
																			    */
					} else {
						manipulationID = OrthogonalEdge.CORNER_POINT_START_INDEX + index; /*
																						   * one
																						   * of
																						   * the
																						   * corner
																						   * points
																						   * hit
																						   */
					}
				}

				/* hit segment? */
				int hitSegmentindex = path.getHitSegmentIndex(pt);
				if (hitSegmentindex != DefaultVisualGraphObject.NONE) {

					if (altKeyPressed) { /*
										  * make sure we always hit a segment
										  * while inserting new points
										  */
						manipulationID = OrthogonalEdge.SEGMENT_START_INDEX + hitSegmentindex;
					} else if (hitSegmentindex > 0 && hitSegmentindex < size - 2) {
						Point pt1 = path.getPointAt(hitSegmentindex);
						Point pt2 = path.getPointAt(hitSegmentindex + 1);
						Point ptCenter = new Point(pt1.x + (pt2.x - pt1.x) / 2, pt1.y
								+ (pt2.y - pt1.y) / 2);
						if (pointHit(ptCenter, pt)) {
							manipulationID = OrthogonalEdge.SEGMENT_CENTER_START_INDEX
									+ hitSegmentindex;
							/*
							 * allow moving edges by dragging segments else
							 * return SEGMENT_START_INDEX + hitSegmentindex;
							 */
						}
					}
				}
			}
		}
		return manipulationID != DefaultVisualGraphObject.NONE;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (manipulationID == DefaultVisualGraphObject.NONE) {
			return false;
		}
		boolean altKeyPressed = (functionKey & Interactable.KEY_ALT) != 0;

		startManipulating();
		if (altKeyPressed) {

			if (hitCornerPoint(manipulationID)) {
				removePointsAt(pt);
			} else if (hitSegment(manipulationID)) {
				// UndoService.getInstance().stratOfGroupAction();
				int index = IDToIndex(manipulationID);
				Point[] points = path.getPoints();
				if (points[index].x == points[index + 1].x) {
					pt.x = points[index].x;
				} else {
					pt.y = points[index].y;
				}

				addPoint(pt, index);
				setSelected(true);
			}
		}
		return true;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (manipulationID != DefaultVisualGraphObject.NONE) {
			endManipulating();
		}

		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (manipulationID == DefaultVisualGraphObject.NONE) {
			return false;
		}

		int index = IDToIndex(manipulationID);
		/* manipulating the start-point */
		if (hitStartPoint(manipulationID) || hitEndPoint(manipulationID)) {
			// tempResizingPath = path.getPoints();
			movePoint(index, pt);
			return false;
		}
		/* manipulating the corner points */
		else if (hitCornerPoint(manipulationID)) {
			moveCornerPoint(index, pt);
			return true;
		}
		/* manipulating the corner points */
		else if (hitSegmentCenterPoint(manipulationID)) {
			/* move segment. find the real index */
			shiftSegment(index, pt);
			return true;
		}
		return true;
	}

	@Override
	protected void addPoint(Point target, int segIndex) {

		if (segIndex != DefaultVisualGraphObject.NONE) {
			// add 2 points
			undoRedoHandler.stratOfGroupAction();
			path.insertPointAt(segIndex, target);
			path.insertPointAt(segIndex, target);
			undoRedoHandler.endOfGroupAction();
			setPinned(true);
		}
	}

	@Override
	protected void removePointsAt(Point target) {

		Point[] points = path.getPoints();

		int idx = path.getHitPointIndex(target);
		// removing the end-points not allowed
		if (idx < 1 || idx > points.length - 2 || points.length < 6) {
			return;
		}

		if (idx == 1) {
			// remove points at idx and idx+1
			points = removeFromPointList(points, idx);
			points = removeFromPointList(points, idx);
			// ensure that the edge remains orthogonal
			boolean segBeforeHorizontal = points[idx + 1].y == points[idx + 2].y;
			if (segBeforeHorizontal) {
				points[idx + 1].x = points[idx].x;
			} else {
				points[idx + 1].y = points[idx].y;
			}
		} else {
			if (idx == points.length - 2) {
				--idx;
			}
			boolean segBeforeHorizontal = points[idx - 1].y == points[idx - 2].y;
			// remove points at idx and idx+1
			points = removeFromPointList(points, idx);
			points = removeFromPointList(points, idx);
			// ensure that the edge remains orthogonal
			if (segBeforeHorizontal) {
				points[idx - 1].x = points[idx].x;
			} else {
				points[idx - 1].y = points[idx].y;
			}
		}

		path.setPoints(points, true);
		setPinned(true);
	}

	private Point[] removeFromPointList(Point[] points, int idx) {

		Point[] newPoints = new Point[points.length - 1];
		System.arraycopy(points, 0, newPoints, 0, idx);
		System.arraycopy(points, idx + 1, newPoints, idx, points.length - idx - 1);
		return newPoints;
	}

	private void shiftSegment(int index, Point target) {

		Point[] points = path.getPoints();

		/* Number of points can be decreased during the interaction */

		if (index <= 0 || index >= points.length - 1) {
			return;
		}

		if (points[index].x == points[index + 1].x) { // vertical
			points[index].x = target.x;
			points[index + 1].x = target.x;
		} else {
			points[index].y = target.y;
			points[index + 1].y = target.y;
		}
		setPoints(points);
		setPinned(true);
		invalidatePreview();
	}

	private void moveCornerPoint(int index, Point target) {

		// prevent moving the first and last segment
		if (index < path.getSize() - 2) {
			shiftSegment(index, target);
		}
		if (index > 0) {
			shiftSegment(index - 1, target);
		}
		invalidatePreview();
	}

	private int IDToIndex(int ID) {

		if (ID > OrthogonalEdge.SEGMENT_CENTER_START_INDEX) {
			return ID - OrthogonalEdge.SEGMENT_CENTER_START_INDEX;
		}
		if (ID > OrthogonalEdge.SEGMENT_START_INDEX) {
			return ID - OrthogonalEdge.SEGMENT_START_INDEX;
		}
		if (ID > OrthogonalEdge.CORNER_POINT_START_INDEX) {
			return ID - OrthogonalEdge.CORNER_POINT_START_INDEX;
		}
		if (ID > OrthogonalEdge.FIRST_EDGE_POINT_INDEX) {
			return path.getSize() - 1;
		}
		return 0;
	}

	private boolean hitStartPoint(int manipulationID) {

		return manipulationID == OrthogonalEdge.FIRST_EDGE_POINT_INDEX;
	}

	private boolean hitEndPoint(int manipulationID) {

		return manipulationID == OrthogonalEdge.LAST_EDGE_POINT_INDEX;
	}

	private boolean hitCornerPoint(int manipulationID) {

		return manipulationID > OrthogonalEdge.CORNER_POINT_START_INDEX
				&& manipulationID < OrthogonalEdge.SEGMENT_START_INDEX;
	}

	private boolean hitSegment(int manipulationID) {

		return manipulationID > OrthogonalEdge.SEGMENT_START_INDEX
				&& manipulationID < OrthogonalEdge.SEGMENT_CENTER_START_INDEX;
	}

	private boolean hitSegmentCenterPoint(int manipulationID) {

		return manipulationID > OrthogonalEdge.SEGMENT_CENTER_START_INDEX;
	}

	@Override
	public void stoppedChangingPath(Point[] oldPath) {

		// if(isEdgeEdited())
		// path.setPoints(removeRedundats(path.getPoints()));
		super.stoppedChangingPath(oldPath);
	}

	private final boolean pointHit(Point pt, Point hit) {

		int ms = ViewConstants.RESIZING_MARKER_SNAP_RADIUS;
		return hit.x >= pt.x - ms && hit.x <= pt.x + ms && hit.y >= pt.y - ms && hit.y <= pt.y + ms;
	}

	@Override
	protected void updatePath(boolean updateSource, boolean updateTarget) {

		super.updatePath(updateSource, updateTarget);
		if (!isPinned()) {
			setPoints(OrthogonalEdgeRouter.routeEdge(this, getPoints(), distanceToNode));
		}
	}

	@Override
	public int getPreferredCursor() {

		if (manipulationID == DefaultVisualGraphObject.NONE) {
			return GraphStageConstants.CURSOR_DEFAULT;
		}

		if (hitSegmentCenterPoint(manipulationID)) {
			int index = IDToIndex(manipulationID);
			Point[] points = path.getPoints();
			if (points[index].x == points[index + 1].x) {
				return GraphStageConstants.CURSOR_W_RESIZE;
			} else {
				return GraphStageConstants.CURSOR_N_RESIZE;
			}
		}
		if (hitSegment(manipulationID)) {
			return GraphStageConstants.CURSOR_CROSSHAIR;
		}
		if (hitCornerPoint(manipulationID)) {
			return GraphStageConstants.CURSOR_CROSSHAIR;
		}
		if (hitStartPoint(manipulationID) || hitEndPoint(manipulationID)) {
			return GraphStageConstants.CURSOR_CROSSHAIR;
		}

		return GraphStageConstants.CURSOR_DEFAULT;
	}

	// private Point[] removeRedundats(Point[] points) {
	// int redundats = 0;
	// for (int i = 2; i < points.length; i++) {
	// if ((points[i].x == points[i - 1].x && points[i].x == points[i - 2].x)
	// || (points[i].y == points[i - 1].y && points[i].y == points[i - 2].y))
	// ++redundats;
	// }
	// if (redundats != 0) {
	// Point[] tmp = new Point[points.length - redundats];
	// tmp[0] = points[0];
	// for (int i = 1, j = 1; i < points.length - 1; i++) {
	// if ((tmp[j - 1].x == points[i].x && points[i].x == points[i + 1].x)
	// || (tmp[j - 1].y == points[i].y && points[i].y == points[i + 1].y))
	// continue;
	// tmp[j++] = points[i];
	// }
	//
	// tmp[tmp.length - 1] = points[points.length - 1];
	// return tmp;
	// }
	// return points;
	// }

	@Override
	public void pathChanging() {

		boolean b = isFiringEvents();
		fireEvents(false);

		Point[] points = getPoints();
		if (isPinned() && points.length > 2) {
			points = OrthogonalEdgeRouter.ensureOrthogonality(this, points);
		} else {
			points = OrthogonalEdgeRouter.routeEdge(this, points, distanceToNode);
		}
		path.setPoints(points, true);

		fireEvents(b);

		super.pathChanging();
	}

	@Override
	protected String getSelectionDescriptor() {

		String sel = super.getSelectionDescriptor();
		if (sel == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer(sel);
		Point[] points = path.getPoints();

		Point pt;
		Point pt1;
		Point pt2;

		if (svgManDef != null) {
			final double sx = transform.getScaleX();
			final double sy = transform.getScaleY();
			for (int i = 1; i < points.length - 2; i += 1) {
				pt1 = points[i];
				pt2 = points[i + 1];
				pt = pt1;

				if (pt1.x == pt2.x) {
					if (Math.abs(pt2.y - pt1.y) < svgManDef.height) {
						continue;
					}
					pt.y = pt1.y + (pt2.y - pt1.y) / 2;
				} else if (pt1.y == pt2.y) {
					if (Math.abs(pt2.x - pt1.x) < svgManDef.width) {
						continue;
					}
					pt.x = pt1.x + (pt2.x - pt1.x) / 2;
				}
				createManipulationMarkDescriotor(sb, sx * pt.x - svgManDef.width / 2 + 2, sy * pt.y
						- svgManDef.height / 2 + 2);
			}
		}
		return sb.toString();
	}

	private final void createManipulationMarkDescriotor(StringBuffer svg, double x, double y) {

		svg.append("\n<use xlink:href='#");
		svg.append(Constants.DEFAULT_EDGE_MANIPULATION_HANDEL);
		svg.append("' x='");
		svg.append(x);
		svg.append("' y='");
		svg.append(y);
		svg.append("'/>");
	}

	/*
	 * if(svgManDef != null) { Point pt; Point pt1; Point pt2; for (int i = 1; i
	 * < points.length - 2; i += 1) { pt1 = points[i]; pt2 = points[i + 1]; pt =
	 * pt1; if (pt1.x == pt2.x && Math.abs(pt2.y - pt1.y) > svgManDef.height *
	 * 2) { pt.y = pt1.y + (pt2.y - pt1.y) / 2;
	 * createSelectionMarkDescriotor(sb, (pt.x * sx) - svgSelDef.width / 2,
	 * (pt.y * sy) - svgSelDef.height / 2); } else if (pt1.y == pt2.y &&
	 * Math.abs(pt2.x - pt1.x) > svgManDef.width * 2) { pt.x = pt1.x + (pt2.x -
	 * pt1.x) / 2; createSelectionMarkDescriotor(sb, (pt.y * sy) -
	 * svgSelDef.height / 2, (pt.x * sx) - svgSelDef.width / 2); } } }
	 */
	// ///////////////////////////////////////////////////////////////////////
	// implementation of the interface PropertyOwner

	public static final String PINNED = "Properties:Pinned";

	@Override
	public void setProperties(PropertyList properties) {

		pinned = PropertyUtil.getProperty(properties, OrthogonalEdge.PINNED, false);

		super.setProperties(properties);
	}

	@Override
	public PropertyList getProperties() {

		properties = super.getProperties();
		properties = PropertyUtil.setProperty(properties, OrthogonalEdge.PINNED, isPinned());
		PropertyUtil.makeEditable(properties, OrthogonalEdge.PINNED, false);
		return properties;
	}
}
