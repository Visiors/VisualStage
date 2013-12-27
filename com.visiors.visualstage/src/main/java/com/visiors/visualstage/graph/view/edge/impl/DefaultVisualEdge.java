package com.visiors.visualstage.graph.view.edge.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.visiors.visualstage.attribute.Attribute;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.graph.view.Constants;
import com.visiors.visualstage.graph.view.DefaultVisualGraphObject;
import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.Path;
import com.visiors.visualstage.graph.view.edge.PathChangeListener;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.listener.EdgeViewListener;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.Port;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.graph.view.node.listener.NodeViewAdapter;
import com.visiors.visualstage.graph.view.node.listener.VisualNodeListener;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.pool.FormatDefinitionCollection;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.property.impl.PropertyBinder;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.svg.SVGDescriptor;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.util.PropertyUtil;
import com.visiors.visualstage.validation.Validator;

public class DefaultVisualEdge extends DefaultVisualGraphObject implements VisualEdge, PathChangeListener/*
 * ,
 * PropertyListener
 */{

	protected VisualNode sourceNode;
	protected VisualNode targetNode;
	protected int sourcePortId;
	protected int targetPortId;
	protected Path path;
	protected boolean floatingPorts = false;
	private int manipulationID;
	protected Attribute arrtribute;
	protected PropertyList properties;
	protected SVGDescriptor svgLineDef;
	protected SVGDescriptor svgSelDef;
	protected String styleID = FormatDefinitionCollection.DEFAULT_STYLE;
	protected String presentationID = FormatDefinitionCollection.DEFAULT_EDGE_PRESENTATION;
	protected Rectangle boundaryExt = new Rectangle();

	private PropertyBinder propertyBinder;

	@Inject
	private FormatDefinitionCollection formatDefinitionCollection;
	@Inject
	protected UndoRedoHandler undoRedoHandler;
	@Inject
	protected Validator validator;

	public DefaultVisualEdge() {

		super();

		DI.injectMembers(this);


		sourcePortId = DefaultVisualGraphObject.NONE;
		targetPortId = DefaultVisualGraphObject.NONE;
		path = new DefaultPath();
		path.getStart().setPoint(new Point(0, 100));
		path.getEnd().setPoint(new Point(100, 0));
		path.addPathListener(this);

		initProperties();
	}

	protected DefaultVisualEdge(VisualEdge edge) {

		this();

		connect(edge.getSourceNode(), edge.getSourcePortId(), edge.getTargetNode(), edge.getTargetPortId());
		setProperties(edge.getProperties());
		setPath(edge.getPath().deepCopy());
		SetAttributes(edge.getAttributes().deepCopy());
		setStyleID(edge.getStyleID());
		setPresentationID(edge.getPresentationID());
		if (edge.getCustomData() != null) {
			setCustomData(edge.getCustomData().deepCopy());
		}
	}

	protected void initProperties() {

		// create the property definition
		PropertyList properties = new DefaultPropertyList(PropertyConstants.EDGE_PROPERTY_PREFIX);
		properties.add(new DefaultPropertyUnit(PropertyConstants.EDGE_PROPERTY_ID, getID()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.EDGE_PROPERTY_NAME, getName()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.EDGE_PROPERTY_PRESENTATION, getPresentationID()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.EDGE_PROPERTY_STYLE, getStyleID()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.EDGE_PROPERTY_PARENT_ID,
				getParentGraph() != null ? getParentGraph().getID() : NONE));

		propertyBinder = new PropertyBinder(this);

		//		setProperties(properties);
	}

	@Override
	public void setParentGraph(VisualGraph graph) {

		super.setParentGraph(graph);
		propertyBinder.save(PropertyConstants.EDGE_PROPERTY_PARENT_ID);
	}

	@Override
	public VisualNode getSourceNode() {

		return sourceNode;
	}

	@Override
	public VisualNode getTargetNode() {

		return targetNode;
	}

	@Override
	public void connect(VisualNode source, int sourcePortId, VisualNode target, int targetPortId) {

		if (Objects.equal(sourceNode, source) && Objects.equal(this.sourcePortId, sourcePortId)
				&& Objects.equal(targetNode, target) && Objects.equal(this.targetPortId, targetPortId)) {
			return;
		}

		if (!validator.permitConnection(source, sourcePortId, this, target, targetPortId)) {
			return;
		}

		final VisualNode oldSourceNode = sourceNode;
		final int oldSourcePort = sourcePortId;
		final VisualNode oldTargetNode = targetNode;
		final int oldTargetPort = targetPortId;
		/* update source */
		if (sourceNode == null || !sourceNode.equals(source)) {
			if (sourceNode != null) {
				// detach from the current edge
				sourceNode.postDisconnected(this, false);
				sourceNode.removeNodeViewListener(nodeListener);
			}
			if (source != null) {
				// attach to the new source node
				source.postConnected(this, target, false);
				source.addNodeViewListener(nodeListener);
			}
		}
		this.sourceNode = source;
		this.sourcePortId = sourcePortId;

		/* update target */
		if (targetNode == null || !targetNode.equals(target)) {
			if (targetNode != null) {
				// detach target node from the current edge
				targetNode.postDisconnected(this, true);
				targetNode.removeNodeViewListener(nodeListener);
			}
			if (target != null) {
				// attach to the new target node
				target.postConnected(this, target, true);
				target.addNodeViewListener(nodeListener);
			}
		}
		this.targetNode = target;
		this.targetPortId = targetPortId;

		updatePath(true, true);
		fireEdgeReconnected(oldSourceNode, oldSourcePort, oldTargetNode, oldTargetPort);
	}

	@Override
	public int getSourcePortId() {

		return sourcePortId;
	}

	@Override
	public int getTargetPortId() {

		return targetPortId;
	}

	@Override
	public Object deepCopy() {

		return new DefaultVisualEdge(this);
	}

	// @Override
	// public Image getPreview(DrawingContext ctx, final ImageObserver
	// observer) {
	//
	// // if(cachedImage != null)
	// // return cachedImage;
	//
	// /* create preview immediately */
	// cachedImage = previewCreator.createPreview(ctx);
	// return cachedImage;
	// }
	//
	// protected void invalidatePreview() {
	//
	// cachedImage = null;
	// }

	@Override
	public String getViewDescriptor(Resolution resolution, DrawingSubject subject) {

		if (subject == DrawingSubject.OBJECT) {

			return styledLineDescriptor();
		}
		if (subject == DrawingSubject.SELECTION_INDICATORS && resolution == Resolution.SCREEN) {

			return getSelectionDescriptor();
		}
		return new String();
	}

	private String styledLineDescriptor() {

		final String desc = getLineDescriptor();
		if (styleID == null) {
			return desc;
		}
		final StringBuffer sb = new StringBuffer();
		if (styleID.indexOf(':') == -1) {
			sb.append("<g class='" + styleID + "'>");
		} else {
			sb.append("<g " + styleID + ">");
		}
		sb.append(desc);
		sb.append("</g>");
		return sb.toString();
	}

	protected String getSelectionDescriptor() {

		if (!selected) {
			return null;
		}
		if (svgSelDef != null) {
			final EdgePoint[] points = path.getPoints();
			final StringBuffer sb = new StringBuffer();
			final double sx = transform.getScaleX();
			final double sy = transform.getScaleY();
			for (EdgePoint edgePoint : points) {
				final Point point = edgePoint.getPoint();
				createSelectionMarkDescriotor(sb, (point.x * sx) - svgSelDef.width / 2, (point.y * sy)
						- svgSelDef.height / 2);
			}
			return sb.toString();
		}
		return null;
	}

	private final void createSelectionMarkDescriotor(StringBuffer svg, double x, double y) {

		svg.append("\n<use xlink:href='#");
		svg.append(Constants.DEFAULT_EDGE_SELECTION_MARKER);
		svg.append("' x='");
		svg.append(x);
		svg.append("' y='");
		svg.append(y);
		svg.append("'/>");
	}

	protected String getLineDescriptor() {

		if (svgLineDef != null) {
			final StringBuffer svg = new StringBuffer();
			svg.append(updatedSVGDescription(svgLineDef.definition));
			return svg.toString();
		}
		return null;
	}

	private String updatedSVGDescription(String description) {

		int idx1 = description.indexOf(" x1");
		int idx2 = description.indexOf("\"", idx1);
		final StringBuffer sb = new StringBuffer();
		sb.append("<g transform='scale(");
		sb.append(transform.getScaleX());
		sb.append(",");
		sb.append(transform.getScaleY());
		sb.append(")'> ");
		if (idx1 != -1) {
			idx1 += 2;
			final char ch = description.charAt(idx1 + 1);
			idx2 = description.indexOf(ch, idx1 + 2);
			sb.append(description.substring(0, idx1 + 2));
			sb.append(getPathDescription());
			sb.append(description.substring(idx2));
		} else {
			idx1 = description.indexOf("line");
			if (idx1 == -1) {
				// TODO error
				System.err.println(" edge descrption must define a 'line' element!");
				return null;
			}
			idx1 += 5;
			sb.append(description.substring(0, idx1));
			sb.append(getPathDescription());
			sb.append(' ');
			sb.append(description.substring(idx1));
		}
		sb.append("</g>");
		return sb.toString();
	}

	private String getPathDescription() {

		final EdgePoint[] points = path.getPoints();
		final Point pt1 = points[0].getPoint();
		final Point pt2 = points[1].getPoint();
		final StringBuffer sb = new StringBuffer();
		sb.append("x1='");
		sb.append(pt1.x);
		sb.append("' y1='");
		sb.append(pt1.y);
		sb.append("' x2='");
		sb.append(pt2.x);
		sb.append("' y2='");
		sb.append(pt2.y);
		sb.append("'");
		return sb.toString();
	}

	@Override
	public Path getPath() {

		return path;
	}

	@Override
	public void setPath(Path path) {

		this.path = path;
	}

	// ///////////////////////////////////////////////////////////////////////
	// implementation of the interface PropertyOwner
	// @Override
	@Override
	public void setProperties(PropertyList propertyList) {

		super.setProperties(propertyList);

		propertyBinder.loadAll();


		//TEST
		presentationID = PropertyUtil.getProperty(propertyList, "presentation", "");
		styleID = PropertyUtil.getProperty(propertyList, "style", "");


		svgLineDef = formatDefinitionCollection.get(presentationID);
		//		svgSelDef = SVGDefinitionPool.get(Constants.DEFAULT_EDGE_SELECTION_MARKER);
	}



	private void updateBoundaries() {

		int x1 = Integer.MAX_VALUE;
		int y1 = Integer.MAX_VALUE;
		int x2 = Integer.MIN_VALUE;
		int y2 = Integer.MIN_VALUE;
		final EdgePoint[] points = path.getPoints();
		for (final EdgePoint point : points) {
			final Point pt = point.getPoint();
			x1 = Math.min(x1, pt.x);
			y1 = Math.min(y1, pt.y);
			x2 = Math.max(x2, pt.x);
			y2 = Math.max(y2, pt.y);
		}
		boundary.setBounds(x1, y1, x2 - x1, y2 - y1);

		boundaryExt.setBounds(boundary);
		// TODO solve this in a better way!!		
		boundaryExt.grow(20, 20);
	}
	// ///////////////////////////////////////////////////////////////////////
	// implementation of VisualObject interface


	@Override
	public Rectangle getExtendedBoundary() {

		return boundaryExt;
	}

	@Override
	public void setSelected(boolean selected) {

		if (this.selected != selected) {
			this.selected = selected;
			fireEdgeSelectionChanged();
		}
	}

	@Override
	public boolean isSelected() {

		return selected && attributes.isSelectable();
	}

	@Override
	public void setHighlighted(boolean highlighted) {

		super.setHighlighted(highlighted);
		fireEdgeHighlightingChanged();
	}

	@Override
	public boolean isHit(Point pt) {

		return path.getHitSegmentIndex(pt) != DefaultVisualGraphObject.NONE;
	}

	// //////////////////////////////////////////////////////////////////////////
	// path updating and manipulating
	protected void updatePath(boolean updateSource, boolean updateTarget) {

		// if (floatingPorts || (updateTarget && getTargetPortId() == NONE)
		// || (updateSource && getSourcePortId() == NONE)) {
		// Point p1 = path.getStart().getPoint();
		// Point p2 = path.getEnd().getPoint();
		// int x1 = (int) (updateSource ? sourceNode.getBounds().getCenterX() :
		// p1.x);
		// int x2 = (int) (updateTarget ? targetNode.getBounds().getCenterX() :
		// p2.x);
		// int y1 = (int) (updateSource ? sourceNode.getBounds().getCenterY() :
		// p1.y);
		// int y2 = (int) (updateTarget ? targetNode.getBounds().getCenterY() :
		// p2.y);
		// double angle = Math.toDegrees(Math.atan2(-y2 + y1, x2 - x1));
		// /*
		// * select an appropriate port if the option floatingPorts is been
		// * used, or if the port id is not specified yet
		// */
		// if (updateSource && (floatingPorts || sourcePortId == NONE)) {
		// setSourcePortId(sourceNode.getPortSet().getPortByAngle(angle));
		// p1 = sourceNode.getPortSet().getPortByID(sourcePortId).getPosition();
		// path.setPointAt(0, p1);
		// }
		// if (updateTarget && (floatingPorts || targetPortId == NONE)) {
		// setTargetPortId(targetNode.getPortByAngle(180.0 + angle));
		// p2 = sourceNode.getPortSet().getPortByID(targetPortId).getPosition();
		// path.setPointAt(path.size() - 1, p1);
		// }
		// }
		Point p1 = null;
		Point p2 = null;
		if (updateSource) {
			if (getSourcePortId() != DefaultVisualGraphObject.NONE) {
				Port port = sourceNode.getPortSet().getPortByID(getSourcePortId());
				if (port != null) {
					p1 = port.getPosition();
				}
			}
			if (p1 == null) {
				final Rectangle r = sourceNode.getBounds();
				p1 = new Point((int) r.getCenterX(), (int) r.getCenterY());
			}

			movePoint(0, p1);
		}
		if (updateTarget) {
			if (getTargetPortId() != DefaultVisualGraphObject.NONE) {
				Port port = targetNode.getPortSet().getPortByID(getTargetPortId());
				if (port != null) {
					p2 = port.getPosition();
				}
			}
			if (p2 == null) {
				final Rectangle r = targetNode.getBounds();
				p2 = new Point((int) r.getCenterX(), (int) r.getCenterY());
			}

			movePoint(path.getSize() - 1, p2);
		}
	}

	protected void movePoint(int index, Point target) {

		path.setPointAt(index, new EdgePoint(target));
		updateBoundaries();
		setModified(true);
	}

	@Override
	public void move(int dx, int dy) {

		if (dx != 0 || dy != 0 && attributes.isMovable()) {
			if (attributes.isMovable()) {
				final EdgePoint[] newPoints = path.getPoints();
				for (final EdgePoint newPoint : newPoints) {
					newPoint.getPoint().translate(dx, dy);
				}
				updateBoundaries();
			}
		}
	}

	@Override
	public void startManipulating() {

		// System.err.println("start manipulating: " + getName());
		path.startManipulating();
	}

	@Override
	public void endManipulating() {

		// System.err.println("end manipulating: " + getName());
		path.endManipulating();
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		manipulationID = DefaultVisualGraphObject.NONE;
		if (attributes.isResizable() && isSelected()) {
			final int index = path.getHitPointIndex(pt);
			if (index != DefaultVisualGraphObject.NONE) {
				manipulationID = index;
			}
		}
		return manipulationID != DefaultVisualGraphObject.NONE;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (!isInteracting()) {
			return false;
		}
		if (manipulationID == 0) {
			getPath().setPointAt(0, new EdgePoint(pt));
			return false;
		} else if (manipulationID == path.getSize() - 1) {
			getPath().setPointAt(path.getSize() - 1, new EdgePoint(pt));
			return false;
		}
		return true;
	}

	@Override
	public boolean isInteracting() {

		return manipulationID != DefaultVisualGraphObject.NONE;
	}

	@Override
	public int getPreferredCursor() {

		if (isInteracting()) {
			return Interactable.CURSOR_CROSSHAIR;
		}
		return Interactable.CURSOR_DEFAULT;
	}

	@Override
	public void setPresentationID(String presentationID) {

		this.presentationID = presentationID;

		propertyBinder.save(PropertyConstants.EDGE_PROPERTY_PRESENTATION);
	}

	@Override
	public String getPresentationID() {

		return presentationID;
	}

	@Override
	public String getStyleID() {

		return styleID;
	}

	@Override
	public void setStyleID(String styleID) {

		this.styleID = styleID;
		propertyBinder.save(PropertyConstants.EDGE_PROPERTY_STYLE);

	}

	@Override
	public String toString() {

		final StringBuffer sb = new StringBuffer();
		sb.append("EdgeView (").append("id= ").append(getID()).append(", source node= ")
		.append(getSourceNode() != null ? String.valueOf(getSourceNode().getID()) : "Nil")
		.append(", target node= ")
		.append(getTargetNode() != null ? String.valueOf(getTargetNode().getID()) : "Nil").append(" ]");
		return sb.toString();
	}

	// ===================================================
	// sending notification to listener
	protected List<EdgeViewListener> edgeViewlistener = new ArrayList<EdgeViewListener>();
	protected boolean fireEvents = true;

	@Override
	public void addEdgeViewListener(EdgeViewListener listener) {

		if (!edgeViewlistener.contains(listener)) {
			edgeViewlistener.add(listener);
		}
	}

	@Override
	public void removeEdgeViewListener(EdgeViewListener listener) {

		edgeViewlistener.remove(listener);
	}

	// TODO check why this was needed?
	// @Override
	// public void fireEvents(boolean enable) {
	//
	// fireEvents = enable;
	// path.fireEvents(enable);
	// }
	//
	// @Override
	// public boolean isFiringEvents() {
	//
	// return fireEvents;
	// }

	protected void fireEdgeReconnected(VisualNode oldSourceNode, int oldSourcePortID, VisualNode oldTagetNode,
			int oldTargetPortID) {

		if (!fireEvents) {
			return;
		}
		for (final EdgeViewListener l : edgeViewlistener) {
			l.edgeReconnected(this, oldSourceNode, oldSourcePortID, oldTagetNode, oldTargetPortID);
		}
	}

	protected void fireEdgeSelectionChanged() {

		if (!fireEvents) {
			return;
		}
		for (final EdgeViewListener l : edgeViewlistener) {
			l.edgeSelectionChanged(this);
		}
	}

	protected void fireEdgeHighlightingChanged() {

		if (!fireEvents) {
			return;
		}
		for (final EdgeViewListener l : edgeViewlistener) {
			l.edgeHighlightingChanged(this);
		}
	}

	// ///////////////////////////////
	// events coming from path
	@Override
	public void startChangingPath() {

		for (final EdgeViewListener l : edgeViewlistener) {
			l.edgeStartChangingPath(this);
		}
	}

	@Override
	public void pathChanging() {

		for (final EdgeViewListener l : edgeViewlistener) {
			l.edgePathChanging(this);
		}
	}

	@Override
	public void stoppedChangingPath(EdgePoint[] oldPath) {

		// registerPathChange(oldPath, path.getPoints());
		for (final EdgeViewListener l : edgeViewlistener) {
			l.edgeStoppedChangingPath(this, oldPath);
		}
		updateBoundaries();
		setModified(true);
	}

	// ///////////////////////////////
	// events coming from connected nodes
	VisualNodeListener nodeListener = new NodeViewAdapter() {

		@Override
		public void nodeBoundaryChangning(VisualNode node) {

			final boolean sourceChanged = sourceNode != null && sourceNode.getID() == node.getID();
			updatePath(sourceChanged, !sourceChanged);
		}
	};
	// ===================================================
	// redo/undo handling
	//
	// private static final String EDGE_PATH_CHANGED = "path changed";
	// private static final String SOURCE_PORT_CHANGED = "source port changed";
	// private static final String SOURCE_NODE_CHANGED = "source node changed";
	// private static final String TARGET_PORT_CHANGED = "target port changed";
	// private static final String TARGET_NODE_CHANGED = "target node changed";
	//
	// @Override
	// public void undo(Object data) {
	//
	// internalRedoUndo(data, true);
	// }
	//
	// @Override
	// public void redo(Object data) {
	//
	// internalRedoUndo(data, false);
	// }
	//
	// private void internalRedoUndo(Object data, boolean undo) {
	//
	// final PropertyList pl = (PropertyList) data;
	// final PropertyUnit p = (PropertyUnit) pl.get(undo ? "old" : "new");
	// if (pl.getName().equals(DefaultEdgeView.EDGE_PATH_CHANGED)) {
	// restorePath(p);
	// } else if (pl.getName().equals(DefaultEdgeView.SOURCE_PORT_CHANGED)) {
	// restoreSourcePort(p);
	// } else if (pl.getName().equals(DefaultEdgeView.SOURCE_NODE_CHANGED)) {
	// restoreSourceNode(p);
	// } else if (pl.getName().equals(DefaultEdgeView.TARGET_PORT_CHANGED)) {
	// restoreTargetPort(p);
	// } else if (pl.getName().equals(DefaultEdgeView.TARGET_NODE_CHANGED)) {
	// restoreTargetNode(p);
	// }
	// }
	//
	// private void registerSourcePortChange(int oldPortID, int newPortID) {
	//
	// if (undoRedoHandler.undoRedoInProcess() || parent == null) {
	// return;
	// }
	//
	// final PropertyList data = new
	// DefaultPropertyList(DefaultEdgeView.SOURCE_PORT_CHANGED);
	//
	// // store both old and new ports
	// data.add(new DefaultPropertyUnit("old", oldPortID));
	// data.add(new DefaultPropertyUnit("new", newPortID));
	//
	// undoRedoHandler.registerAction(this, data);
	// }
	//
	// private void registerSourceNodeChange(int oldSourceNodeID, int
	// newSourceNodeID) {
	//
	// if (undoRedoHandler.undoRedoInProcess() || parent == null) {
	// return;
	// }
	//
	// final PropertyList data = new
	// DefaultPropertyList(DefaultEdgeView.SOURCE_NODE_CHANGED);
	//
	// // store both old and new ports
	// data.add(new DefaultPropertyUnit("old", oldSourceNodeID));
	// data.add(new DefaultPropertyUnit("new", newSourceNodeID));
	//
	// undoRedoHandler.registerAction(this, data);
	// }
	//
	// private void registerTargetPortChange(int oldPortID, int newPortID) {
	//
	// if (undoRedoHandler.undoRedoInProcess() || parent == null) {
	// return;
	// }
	//
	// final PropertyList data = new
	// DefaultPropertyList(DefaultEdgeView.TARGET_PORT_CHANGED);
	//
	// // store both old and new ports
	// data.add(new DefaultPropertyUnit("old", oldPortID));
	// data.add(new DefaultPropertyUnit("new", newPortID));
	//
	// undoRedoHandler.registerAction(this, data);
	// }
	//
	// private void registerTargegtNodeChange(int oldTargetNodeID, int
	// newTargetNodeID) {
	//
	// if (undoRedoHandler.undoRedoInProcess() || parent == null) {
	// return;
	// }
	//
	// final PropertyList data = new
	// DefaultPropertyList(DefaultEdgeView.TARGET_NODE_CHANGED);
	//
	// // store both old and new ports
	// data.add(new DefaultPropertyUnit("old", oldTargetNodeID));
	// data.add(new DefaultPropertyUnit("new", newTargetNodeID));
	//
	// undoRedoHandler.registerAction(this, data);
	// }
	//
	// private void registerPathChange(Point[] oldPath, Point[] newPath) {
	//
	// if (undoRedoHandler.undoRedoInProcess() || parent == null) {
	// return;
	// }
	//
	// if (oldPath == null) {
	// return;
	// }
	//
	// if (pathEqual(oldPath, newPath)) {
	// return;
	// }
	//
	// System.err.println("UNDO: registerPathChange, size: ");
	//
	// final PropertyList data = new
	// DefaultPropertyList(DefaultEdgeView.EDGE_PATH_CHANGED);
	//
	// // store both old and new coordinates
	// String oldCoords = "";
	// for (final Point pt : oldPath) {
	// oldCoords += pt.x + " " + pt.y + " ";
	// }
	// data.add(new DefaultPropertyUnit("old", oldCoords));
	//
	// String newCoords = "";
	// for (final Point pt : newPath) {
	// newCoords += pt.x + " " + pt.y + " ";
	// }
	// data.add(new DefaultPropertyUnit("new", newCoords));
	//
	// undoRedoHandler.registerAction(this, data);
	// }
	//
	// private boolean pathEqual(Point[] p1, Point[] p2) {
	//
	// if (p1 == null || p2 == null) {
	// return false;
	// }
	// if (p1.length != p2.length) {
	// return false;
	// }
	// for (int i = 0; i < p2.length; i++) {
	// if (!p1[i].equals(p2[i])) {
	// return false;
	// }
	// }
	//
	// return true;
	// }
	//
	// private void restoreTargetNode(PropertyUnit p) {
	//
	// final int id = ConvertUtil.object2int(p.getValue());
	// targetNode = parent.getNode(id);
	// }
	//
	// private void restoreTargetPort(PropertyUnit p) {
	//
	// targetPortId = ConvertUtil.object2int(p.getValue());
	// }
	//
	// private void restoreSourceNode(PropertyUnit p) {
	//
	// final int id = ConvertUtil.object2int(p.getValue());
	// sourceNode = parent.getNode(id);
	// }
	//
	// private void restoreSourcePort(PropertyUnit p) {
	//
	// sourcePortId = ConvertUtil.object2int(p.getValue());
	// }
	//
	// private void restorePath(PropertyUnit p) {
	//
	// final String[] tstr = p.getValue().toString().split(" ");
	// final Point[] points = new Point[tstr.length / 2];
	// for (int i = 0, n = 0; i < tstr.length - 1; i += 2, n++) {
	// final int x = Integer.parseInt(tstr[i]);
	// final int y = Integer.parseInt(tstr[i + 1]);
	// points[n] = new Point(x, y);
	// }
	// setPoints(points);
	// }
}
