package com.visiors.visualstage.graph.view.node.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.graph.view.Constants;
import com.visiors.visualstage.graph.view.DefaultVisualGraphObject;
import com.visiors.visualstage.graph.view.ViewConstants;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.Port;
import com.visiors.visualstage.graph.view.node.PortSet;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.graph.view.node.listener.VisualNodeListener;
import com.visiors.visualstage.graph.view.shape.Shape;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.pool.FormatCollection;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.property.impl.PropertyBinder;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.svg.SVGDescriptor;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.util.PropertyUtil;

public class DefaultVisualNode extends DefaultVisualGraphObject implements VisualNode {

	protected static final int RESIZE_WEST = 1;
	protected static final int RESIZE_SOUTH = 2;
	protected static final int RESIZE_EAST = 3;
	protected static final int RESIZE_NORTH = 4;
	protected static final int RESIZE_NORTH_WEST = 5;
	protected static final int RESIZE_SOUTH_WEST = 6;
	protected static final int RESIZE_NORTH_EAST = 7;
	protected static final int RESIZE_SOUTH_EAST = 8;
	protected static final int PROCESSING_BY_ATTACHMENT = 9;

	protected List<VisualEdge> incomingEdges;
	protected List<VisualEdge> outgoingEdges;
	protected PortSet portSet;
	private Rectangle oldRect;
	private boolean illuminatePorts;
	private int actionMode = DefaultVisualGraphObject.NONE;
	protected SVGDescriptor selDef;
	protected SVGDescriptor portDef;
	protected SVGDescriptor portHLDef;
	protected SVGDescriptor svgDef;
	private PropertyBinder propertyBinder;
	@Inject
	private FormatCollection formatCollection;
	@Inject
	protected UndoRedoHandler undoRedoHandler;
	// private final VisualObjectSnapshotGenerator previewCreator;
	// private final List<CachedImage> cachedImage;

	public DefaultVisualNode() {

		super();

		/* this.name = name; */
		this.portSet = new DefaultPortSet();
		this.incomingEdges = new ArrayList<VisualEdge>();
		this.outgoingEdges = new ArrayList<VisualEdge>();
		this.boundary = new Rectangle(0, 0, 80, 50); 

		// previewCreator = new VisualObjectSnapshotGenerator(this);
		// cachedImage = new ArrayList<CachedImage>();

		styleID = FormatCollection.DEFAULT_STYLE;
		presentationID = FormatCollection.DEFAULT_NODE_PRESENTATION;

		initPropertyList();
	}

	protected DefaultVisualNode(VisualNode node) {

		this();

		this.setBounds(node.getBounds()); // TODO deep copy
		this.SetAttributes(node.getAttributes());
		this.incomingEdges = new ArrayList<VisualEdge>(node.getIncomingEdges());
		this.outgoingEdges = new ArrayList<VisualEdge>(node.getOutgoingEdges());
		this.setProperties(node.getProperties()); // TODO deep copy
		this.setStyleID(node.getStyleID());
		this.setPresentationID(node.getPresentationID());
		this.setPortSet(node.getPortSet()); // TODO deep copy
		this.setChildren(node.getChildren()); // TODO deep copy

		if (node.getCustomData() != null) {
			setCustomData(node.getCustomData().deepCopy());
		}
	}


	protected void initPropertyList() {

		// create the property definition
		final PropertyList properties = new DefaultPropertyList(PropertyConstants.NODE_PROPERTY_PREFIX);
		properties.add(new DefaultPropertyUnit(PropertyConstants.NODE_PROPERTY_ID, getID()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.NODE_PROPERTY_NAME, getName()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.NODE_PROPERTY_X, getX()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.NODE_PROPERTY_Y, getY()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.NODE_PROPERTY_WIDTH, getWidth()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.NODE_PROPERTY_HEIGHT, getHeight()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.NODE_PROPERTY_PRESENTATION, presentationID));
		properties.add(new DefaultPropertyUnit(PropertyConstants.NODE_PROPERTY_STYLE, getStyleID()));
		properties.add(new DefaultPropertyUnit(PropertyConstants.NODE_PROPERTY_PARENT_ID, getParentGraphID()));

		// TODO add layout

		// add posts properties
		final PropertyList portsProperties = portSet.getProperties();
		properties.add(portsProperties);

		propertyBinder = new PropertyBinder(this);
		propertyBinder.setHandler(portsProperties.getName(), portSet);

		propertyBinder.bind(properties);
	}

	@Override
	public void setParentGraph(VisualGraph graph) {

		super.setParentGraph(graph);
		propertyBinder.save(PropertyConstants.NODE_PROPERTY_PARENT_ID);
	}

	// @Override
	// public void setTransform(Transform transform) {
	// if(this.transformer != transform) {
	// if(this.transformer != null)
	// this.transformer.removeListener(this);
	// super.setTransform(transform);
	// if(this.transformer != null)
	// this.transformer.addListener(this);
	// }
	// }
	//
	// @Override
	// public void scaleValuesChanged() {
	//
	// markPreviewAsInvalid();
	// }

	@Override
	public int getDegree() {

		return getIndegree() + getOutdegree();
	}

	@Override
	public int getIndegree() {

		return incomingEdges.size();
	}

	@Override
	public int getOutdegree() {

		return outgoingEdges.size();
	}

	@Override
	public List<VisualEdge> getOutgoingEdges() {

		return outgoingEdges;
	}

	@Override
	public List<VisualEdge> getIncomingEdges() {

		return incomingEdges;
	}

	@Override
	public List<VisualEdge> getConnectedEdges() {

		final List<VisualEdge> edges = new ArrayList<VisualEdge>();
		edges.addAll(incomingEdges);
		edges.addAll(outgoingEdges);
		return edges;
	}

	private final boolean pointHit(int x, int y, Point ptHit) {

		final int ms = ViewConstants.RESIZING_MARKER_SNAP_RADIUS;
		return ptHit.x >= x - ms && ptHit.x <= x + ms && ptHit.y >= y - ms && ptHit.y <= y + ms;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		actionMode = DefaultVisualGraphObject.NONE;

		if (attributes.isResizable() && isSelected()) {

			final int ms = ViewConstants.MARKER_SIZE / 2;
			final int x1 = boundary.x - ms;
			final int y1 = boundary.y - ms;
			final int cx = boundary.x + boundary.width / 2;
			final int cy = boundary.y + boundary.height / 2;
			final int x2 = boundary.x + boundary.width + ms;
			final int y2 = boundary.y + boundary.height + ms;

			if (pointHit(x1, cy - ms, pt)) {
				actionMode = DefaultVisualNode.RESIZE_WEST;
			} else if (pointHit(x1, y1, pt)) {
				actionMode = DefaultVisualNode.RESIZE_NORTH_WEST;
			} else if (pointHit(x1, y2, pt)) {
				actionMode = DefaultVisualNode.RESIZE_SOUTH_WEST;
			} else if (pointHit(x2, cy, pt)) {
				actionMode = DefaultVisualNode.RESIZE_EAST;
			} else if (pointHit(x2, y1, pt)) {
				actionMode = DefaultVisualNode.RESIZE_NORTH_EAST;
			} else if (pointHit(x2, y2, pt)) {
				actionMode = DefaultVisualNode.RESIZE_SOUTH_EAST;
			} else if (pointHit(cx, y1, pt)) {
				actionMode = DefaultVisualNode.RESIZE_NORTH;
			} else if (pointHit(cx, y2, pt)) {
				actionMode = DefaultVisualNode.RESIZE_SOUTH;
			} /*
			 * else { if (form != null) { form.mouseMoved(pt, button,
			 * functionKey); } }
			 */
		}
		return actionMode != DefaultVisualGraphObject.NONE;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (actionMode == DefaultVisualGraphObject.NONE) {
			return false;
		}
		finishInteraction();

		final Rectangle r = getBounds();

		final int dx = r.x - pt.x;
		final int dy = r.y - pt.y;
		final int dw = r.x + r.width;
		final int dh = r.y + r.height;

		switch (actionMode) {
		case RESIZE_NORTH:
			r.height += dy;
			r.y -= dy;
			break;
		case RESIZE_NORTH_WEST:
			r.height += dy;
			r.y = pt.y;
			r.width += dx;
			r.x = pt.x;
			break;
		case RESIZE_WEST:
			r.width += dx;
			r.x = pt.x;
			break;
		case RESIZE_SOUTH_WEST:
			r.width += dx;
			r.x = pt.x;
			r.height += pt.y - dh;
			break;
		case RESIZE_SOUTH:
			r.height += pt.y - dh;
			break;
		case RESIZE_SOUTH_EAST:
			r.height += pt.y - dh;
			r.width += pt.x - dw;
			break;
		case RESIZE_EAST:
			r.width += pt.x - dw;
			break;
		case RESIZE_NORTH_EAST:
			r.width += pt.x - dw;
			r.height += dy;
			r.y = pt.y;
			break;
		}
		setBounds(r);

		return true;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (actionMode == DefaultVisualGraphObject.NONE) {
			return false;
		}
		if (actionMode == DefaultVisualNode.PROCESSING_BY_ATTACHMENT) {
			/*
			 * if (form != null) { if (form.mousePressed(pt, button,
			 * functionKey)) { return true; } }
			 */
		}
		startManipulating();
		return true;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (actionMode == DefaultVisualGraphObject.NONE) {
			return false;
		}
		if (actionMode == DefaultVisualNode.PROCESSING_BY_ATTACHMENT) {
			/*
			 * if (form != null) { if (form.mouseReleased(pt, button,
			 * functionKey)) { return true; } }
			 */
		}
		endManipulating();
		return true;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		if (actionMode == DefaultVisualGraphObject.NONE) {
			/*
			 * if (form != null) { if (form.mouseDoubleClicked(pt, button,
			 * functionKey)) { manipulationID =
			 * DefaultNodeView.PROCESSING_BY_ATTACHMENT; return true; } }
			 */
		}
		return false;
	}


	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		return false;
	}
	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		if (actionMode == DefaultVisualNode.PROCESSING_BY_ATTACHMENT) {
			/*
			 * if (form != null) { if (form.keyPressed(keyChar, keyCode)) {
			 * return true; } }
			 */
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		if (actionMode == DefaultVisualNode.PROCESSING_BY_ATTACHMENT) {
			/*
			 * if (form != null) { if (form.keyReleased(keyChar, keyCode)) {
			 * return true; } }
			 */
		}
		return false;
	}

	@Override
	public int getPreferredCursor() {

		switch (actionMode) {
		case RESIZE_WEST:
			return Interactable.CURSOR_W_RESIZE;
		case RESIZE_SOUTH:
			return Interactable.CURSOR_S_RESIZE;
		case RESIZE_EAST:
			return Interactable.CURSOR_E_RESIZE;
		case RESIZE_NORTH:
			return Interactable.CURSOR_N_RESIZE;
		case RESIZE_NORTH_WEST:
			return Interactable.CURSOR_NW_RESIZE;
		case RESIZE_SOUTH_WEST:
			return Interactable.CURSOR_SW_RESIZE;
		case RESIZE_NORTH_EAST:
			return Interactable.CURSOR_NE_RESIZE;
		case RESIZE_SOUTH_EAST:
			return Interactable.CURSOR_SE_RESIZE;
		case PROCESSING_BY_ATTACHMENT:
			/*
			 * if (form != null) { return form.getPreferredCursor(); }
			 */
		default:
			return Interactable.CURSOR_DEFAULT;
		}
	}

	@Override
	public boolean isInteracting() {

		return actionMode != DefaultVisualGraphObject.NONE;
	}

	@Override
	public int getPreferredPort(Point pt) {

		return portSet.getPortNextTo(pt);
	}

	@Override
	public void postConnected(VisualEdge edge, VisualNode opositeNode, boolean incomingConnection) {

		if (incomingConnection) {
			incomingEdges.add(edge);
		} else {
			outgoingEdges.add(edge);
		}
	}

	@Override
	public void postDisconnected(VisualEdge edge, boolean incomingConnection) {

		if (incomingConnection) {
			incomingEdges.remove(edge);
		} else {
			outgoingEdges.remove(edge);
		}
	}

	@Override
	public void highlightPort(int portID, boolean on) {

		switch (portSet.getPositioning()) {
		case FIXED:
			final Port[] ports = portSet.getPorts();
			for (final Port port : ports) {
				port.setHighlighted(port.getID() == portID && on);
			}
			break;
		case FLOATING:
			// TODO highlight node's border

		}
	}

	@Override
	public String toString() {

		final StringBuffer sb = new StringBuffer();

		sb.append("NodeView (").append("id= ").append(getID()).append(", name = ").append(getName())
		.append(", indegree = ").append(String.valueOf(getIndegree())).append(", outdegree = ")
		.append(String.valueOf(getOutdegree())).append(", boundary: x = ").append(boundary.x).append(", y = ")
		.append(boundary.y).append(", width = ").append(boundary.width).append(", height = ")
		.append(boundary.height).append(" ]");
		return sb.toString();

	}

	@Override
	public void setPresentationID(String presentationID) {

		super.setPresentationID(presentationID);

		propertyBinder.save(PropertyConstants.NODE_PROPERTY_PRESENTATION);

		// invalidatePreview();
		//
		// if (parent != null) {
		// parent.updateView();
		// }
	}

	@Override
	public void setStyleID(String styleID) {

		super.setStyleID(styleID);
		propertyBinder.save(PropertyConstants.NODE_PROPERTY_STYLE);

		// invalidatePreview();

		// if (parent != null) {
		// parent.updateView();
		// }
	}

	public long getParentGraphID() {

		return parent == null ? -1 : parent.getID();
	}

	// ///////////////////////////////////////////////////////////////////////
	// implementation of the interface Attributable

	@Override
	public void setProperties(PropertyList properties) {

		super.setProperties(properties);

		propertyBinder.loadAll();

		final PropertyList portsProperty = PropertyUtil.findPropertyList(properties, PropertyConstants.PORTS_PROPERTY);
		if (portsProperty != null) {
			portSet.setProperties(portsProperty);
			portSet.updatePosition(boundary);
		}


		//TEST
		//		presentationID = PropertyUtil.getProperty(properties, "presentation", "");
		//		styleID = PropertyUtil.getProperty(properties, "style", "");
	}

	public int getX() {

		return boundary.x;
	}

	public void setX(int x) {

		setBounds(new Rectangle(x, boundary.y, boundary.width, boundary.height));
	}

	public int getY() {

		return boundary.y;
	}

	public void setY(int y) {

		setBounds(new Rectangle(boundary.x, y, boundary.width, boundary.height));
	}

	public int getWidth() {

		return boundary.width;
	}

	public void setWidth(int w) {

		setBounds(new Rectangle(boundary.x, boundary.y, w, boundary.height));
	}

	public int getHeight() {

		return boundary.height;
	}

	public void setHeight(int h) {

		setBounds(new Rectangle(boundary.x, boundary.y, boundary.width, h));
	}

	@Override
	public Rectangle getExtendedBoundary() {

		final Rectangle r = getBounds();
		if (!r.isEmpty()) {
			// considering of 1 pixel for rounding inaccuracy
			//TODO uncomment this asap
			//r.grow(ViewConstants.MARKER_SIZE + 1, ViewConstants.MARKER_SIZE + 1);

			// if (portSet != null && ViewConstants.HIGHTLIGHT_PORTS) {
			// Port[] ports = portSet.getPorts();
			// for (int i = 0; i < ports.length; i++) {
			// r = r.union(ports[i].getBounds());
			// }
			// }

			// if (form != null) {
			// Rectangle fr = form.getBoundingBox();
			// if (!fr.isEmpty()) {
			// r = r.union(form.getBoundingBox());
			// }
			// }
		}
		return r;
	}

	@Override
	public boolean isHit(Point pt) {

		final boolean hit = getExtendedBoundary().contains(pt);
		// if (!hit) {
		// if (form != null && form.getHitItem(pt) != null) {
		// return true;
		// }
		// }
		return hit;
	}

	@Override
	public void setSelected(boolean selected) {

		if (this.selected != selected) {
			if (!selected || canBeSelected()) {
				this.selected = selected;
			}
			fireNodeSelectionChanged();
			if (!selected) {
				finishInteraction();
			}
		}
	}

	protected void finishInteraction() {

		if (isInteracting()) {
			terminateInteraction();
		}
		// if (form != null) {
		// form.terminateInteraction();
		// }

	}

	private boolean canBeSelected() {

		return attributes.isSelectable();
	}

	@Override
	public boolean isSelected() {

		return selected;
	}

	@Override
	public void setHighlighted(boolean highlighted) {

		super.setHighlighted(highlighted);
		fireNodeHighlightingChanged();
	}

	@Override
	public Rectangle getBounds() {

		return new Rectangle(boundary);
	}

	@Override
	public void setBounds(Rectangle bounds) {

		if (svgDef != null && svgDef.keepRatio) {
			bounds.width = (int) (bounds.height * svgDef.ratio);
		}

		if (bounds.width < ViewConstants.MIN_NODE_WIDTH) {
			bounds.x = boundary.x;
			bounds.width = ViewConstants.MIN_NODE_WIDTH;
		}
		if (bounds.height < ViewConstants.MIN_NODE_HEIGHT) {
			bounds.y = boundary.y;
			bounds.height = ViewConstants.MIN_NODE_HEIGHT;
		}

		if (!boundary.equals(bounds)) {
			if (attributes.isResizable()) {

				final boolean isManipulationNotified = (oldRect != null);
				if (!isManipulationNotified) {
					startManipulating();
				}

				if (bounds.width != boundary.width || bounds.height != boundary.height) {
					// invalidatePreview();
				}

				boundary = new Rectangle(bounds);

				updateView();

				portSet.updatePosition(boundary);

				fireNodeBoundaryChanging();

				// System.err.println("interacting: " + getName());

				if (!isManipulationNotified) {
					endManipulating();
				}

				// portSet.updatePosition(boundary);

			}
		}
	}

	private void updateView() {

		// if (form != null) {
		// form.invalidate();
		// }

	}

	@Override
	public void startManipulating() {

		oldRect = getBounds();
		fireNodeStartedChangingBoundary();
		List<VisualEdge> edges = getIncomingEdges();
		for (final VisualEdge edgeView : edges) {
			edgeView.startManipulating();
		}
		edges = getOutgoingEdges();
		for (final VisualEdge edgeView : edges) {
			edgeView.startManipulating();
		}
		// System.err.println("Interaction Started: " + getName());
	}

	@Override
	public void endManipulating() {

		if (oldRect == null) {
			return;
		}
		// System.err.println("Interaction Ended: " + getName());
		if (boundary.equals(oldRect)) {
			return;
		}
		registerChangingBoundary(oldRect);
		fireNodeStoppedChangingBoundary(oldRect);

		updateBoundaryProperties();


		List<VisualEdge> edges = getIncomingEdges();
		for (final VisualEdge edgeView : edges) {
			edgeView.endManipulating();
		}
		edges = getOutgoingEdges();
		for (final VisualEdge edgeView : edges) {
			edgeView.endManipulating();
		}
		oldRect = null;
	}

	protected void updateBoundaryProperties() {

		// PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_X, boundary.x);
		// PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_Y, boundary.y);
		// PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_WIDTH, boundary.width);
		// PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_HEIGHT,
		// boundary.height);
	}

	@Override
	public void move(int dx, int dy) {

		if ((dx != 0 || dy != 0) && attributes.isMovable()) {
			final Rectangle r = new Rectangle(boundary);
			r.translate(dx, dy);
			setBounds(r);
		}
	}


	// private synchronized void invalidatePreview() {
	//
	// for (CachedImage cImg : cachedImage) {
	// cImg.invalidate();
	// }
	// System.err.println("cache updated..");
	// }

	// private synchronized Image getCachedImageFor(DrawingContext ctx, double
	// scale,
	// boolean validImgeOnly) {
	//
	// for (CachedImage cImg : cachedImage) {
	// if (Math.round(cImg.getScale() * 100.0) / 100.0 == Math.round(scale *
	// 100.0) / 100.0
	// /* && cImg.ctx.equals(ctx) */&& (!validImgeOnly || cImg.isValid())) {
	// return cImg.getImage();
	// }
	// }
	// return null;
	// }

	// private synchronized void updateCachedImage(DrawingContext ctx, double
	// scale, Image image) {
	//
	// for (int i = cachedImage.getSize() - 1; i >= 0; i--) {
	// if (!cachedImage.get(i).isValid()) {
	// cachedImage.remove(i);
	// }
	// }
	// cachedImage.add(new CachedImage(ctx, scale, image));
	// System.err.println("cache size: " + cachedImage.getSize());
	// }
	//
	// @Override
	// public Image getPreview(final Context ctx, final ImageObserver observer)
	// {
	//
	// // Image img = getCachedImageFor(ctx, transform.getScale(), true);
	// // if (img != null) {
	// // return img;
	// return null;
	// }

	//
	// /* preview is needed immediately */
	// if (observer == null) {
	// img = previewCreator.createPreview(ctx);
	// cachedImage.add(new CachedImage(ctx, transform.getScale(), img));
	// System.err.println("create image immediately");
	// return img;
	// }
	// /* create preview asynchrony */
	// else {
	// previewCreator.createPreview(ctx, new ImageObserver() {
	//
	// @Override
	// public boolean imageUpdate(Image image, int infoflags, int x, int y, int
	// width,
	// int height) {
	//
	// System.err.println("creating image asynchrony");
	// updateCachedImage(ctx, transform.getScale(), image);
	// observer.imageUpdate(image, infoflags, x, y, width, height);
	// return true;
	// }
	// });
	//
	// /*
	// * provide caller with a resized preview for now until the new image
	// * is ready
	// */
	// img = getCachedImageFor(ctx, transform.getScale(), false);
	// if (img != null) {
	// Rectangle r = transform.transformToScreen(getExtendedBoundary());
	// System.err.println("provide scaled image");
	// return scaledImage(img, r.width, r.height);
	// }
	// }
	// return null;
	// }
	//
	// private Image scaledImage(Image image, int width, int height) {
	//
	// if (width == 0 || height == 0) {
	// return null;
	// }
	// BufferedImage scaledImage = new BufferedImage(width, height,
	// BufferedImage.TYPE_INT_ARGB_PRE);
	// Graphics2D g = scaledImage.createGraphics();
	// double r = (double) image.getWidth(null) / image.getHeight(null);
	// int w = (int) Math.round(height * r);
	// g.drawImage(image, 0, 0, w, height, null);
	// g.dispose();
	// return scaledImage;
	// }

	@Override
	public String getViewDescriptor(Resolution resolution, DrawingSubject subject) {

		if (subject == DrawingSubject.OBJECT) {

			return getNodeViewDescriptor(resolution, subject);
		}
		if (subject == DrawingSubject.SELECTION_INDICATORS) {

			return getSelectionViewDescriptor(boundary);
		}
		if (subject == DrawingSubject.PORTS ) {

			return getPortsViewDescriptor(boundary);
		}

		throw new IllegalArgumentException("Unknown subject: " +subject);
	}

	// @Override
	// public String[][] getSVGDocumentAttributes() {
	//
	// if (svgDef == null) {
	// svgDef = formatCollection.get(presentationID);
	// }
	// if (svgDef != null) {
	// return svgDef.getDocumentAttributes();
	// }
	// return null;
	// }

	// private String ImageToString(BufferedImage img) {
	//
	// if (img != null) {
	// try {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// ImageIO.write(img, "png", baos);
	// String encodedImage = Base64.encode(baos.toByteArray());
	// return encodedImage;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }

	protected String getNodeViewDescriptor(Resolution resolution, DrawingSubject subject) {

		final boolean includeChildren = resolution != Resolution.SCREEN_LOW_DETAIL && resolution != Resolution.PREVIEW;

		final StringBuffer desc = new StringBuffer();
		if (presentationID != null) {
			if (svgDef == null) {
				svgDef = formatCollection.get(presentationID);
			}
			if (svgDef != null) {
				final Rectangle b = transform.transformToScreen(boundary);
				final double tx = transform.getXTranslate();
				final double ty = transform.getYTranslate();
				if (styleID != null) {
					if (styleID.indexOf(':') == -1) { // TODO ?
						desc.append("<g class='" + styleID + "'>");
					} else {
						desc.append("<g style='" + styleID + "'>");
					}
				}
				desc.append("<use xlink:href='#");
				desc.append(presentationID);
				desc.append("' x='");
				desc.append(b.x - tx);
				desc.append("' y='");
				desc.append(b.y - ty);
				desc.append("' width='");
				desc.append(b.width);
				desc.append("' height='");
				desc.append(b.height);
				desc.append("'/>");
				if (includeChildren) {
					appendChildrenViewDescriptor(resolution, subject, desc);
				}
				if (styleID != null) {
					desc.append("</g>");
				}
			} 
			else {
				System.err.println("presentation-ID '" + presentationID
						+ "' refes to an not existing graphical object.");
				presentationID = null;// write it only once!
			}
		}

		return desc.toString();
	}

	protected void appendChildrenViewDescriptor(Resolution resolution, DrawingSubject subject, StringBuffer desc) {

		if(children != null){
			for (final Shape shape : children) {
				appendChildViewDescriptor(resolution, subject, desc, shape);
			}
		}

	}

	protected void appendChildViewDescriptor(Resolution resolution, DrawingSubject subject, StringBuffer desc,
			Shape shape) {

		final String shapeDesc = shape.getViewDescriptor(resolution, subject);
		// TODO move to the right position

	}

	private void layout() {

		if (compositeLayout != null) {
			compositeLayout.layout(this, getChildren(), true);
		}

	}

	protected String getSelectionViewDescriptor(Rectangle boundary) {

		if (!selected || illuminatePorts) {
			return null;
		}

		final StringBuffer desc = new StringBuffer();
		if (selDef == null) {
			selDef = formatCollection.get(getSelectionDesriptorID());
		}
		if (selDef != null) {
			final Rectangle b = transform.transformToScreen(boundary);
			desc.append("<g transform='translate(");
			desc.append(-transform.getXTranslate());
			desc.append(",");
			desc.append(-transform.getYTranslate());
			desc.append(")'>");

			/*
			 * if the aspect ration is to be preserved, show only the diagonal
			 * selection markers
			 */
			if (svgDef == null) {
				svgDef = formatCollection.get(presentationID);
			}
			if (svgDef != null && !svgDef.keepRatio) {
				appendSelectionMarkDescriotor(desc, b.x + b.width / 2 - selDef.width / 2, b.y - selDef.height - 2);// N
				appendSelectionMarkDescriotor(desc, b.x + b.width / 2 - selDef.width / 2, b.y + b.height + 2);// S
				appendSelectionMarkDescriotor(desc, b.x - selDef.width - 2, b.y + b.height / 2 - selDef.height / 2);// W
				appendSelectionMarkDescriotor(desc, b.x + b.width + 2, b.y + b.height / 2 - selDef.height / 2);// E
			}

			appendSelectionMarkDescriotor(desc, b.x - selDef.width - 2, b.y - selDef.height - 2);// NW
			appendSelectionMarkDescriotor(desc, b.x - selDef.width - 2, b.y + b.height + 2);// SW
			appendSelectionMarkDescriotor(desc, b.x + b.width + 2, b.y - selDef.height - 2);// NE
			appendSelectionMarkDescriotor(desc, b.x + b.width + 2, b.y + b.height + 2);// SE

			desc.append("\n</g>");
		}

		return desc.toString();
	}

	protected String getSelectionDesriptorID() {

		return Constants.DEFAULT_NODE_SELECTION_MARKER;
	}

	protected String getPortHighlighingDesriptorID() {

		return Constants.DEFAULT_NODE_PORT_HIGHLIGHT_INDICATOR;
	}

	protected String getPortDesriptorID() {

		return Constants.DEFAULT_NODE_PORT_INDICATOR;
	}

	protected String getSelectionMarkerDesriptorID() {

		return Constants.DEFAULT_NODE_SELECTION_MARKER;
	}

	protected String getPortsViewDescriptor(Rectangle b) {

		if (illuminatePorts && portSet != null) {

			if (portDef == null) {
				portDef = formatCollection.get(getPortDesriptorID());
			}
			if (portDef == null) {
				System.err
				.println("Warning: cannot draw the ports. Reason: missing the SVG descriptor assosiate with '"
						+ getPortDesriptorID() + "'");
				return null;
			}

			final StringBuffer sb = new StringBuffer();

			portSet.updatePosition(b);

			Point pt;
			final Port[] port = portSet.getPorts();
			final double sx = transform.getScaleX();
			final double sy = transform.getScaleY();
			for (final Port element : port) {
				pt = element.getPosition();
				appendPortIndicatorDescriotor(sb, pt.x * sx, pt.y * sy, element.isHighlighted());
			}
			portSet.updatePosition(boundary);
			return sb.toString();
		}
		return null;
	}

	private void appendPortIndicatorDescriotor(StringBuffer desc, double x, double y, boolean highlighted) {

		if (portHLDef == null) {
			portHLDef = formatCollection.get(getPortHighlighingDesriptorID());
		}

		if (highlighted) {
			x -= portHLDef.width / 2;
			y -= portHLDef.height / 2;
		} else {
			x -= portDef.width / 2;
			y -= portDef.height / 2;
		}
		desc.append("\n<use xlink:href='#");
		desc.append(highlighted ? getPortHighlighingDesriptorID() : getPortDesriptorID());
		desc.append("' x='");
		desc.append(x);
		desc.append("' y='");
		desc.append(y);
		desc.append("'/>");
	}

	private final void appendSelectionMarkDescriotor(StringBuffer desc, double x, double y) {

		desc.append("\n<use xlink:href='#");
		desc.append(getSelectionMarkerDesriptorID());
		desc.append("' x='");
		desc.append(x);
		desc.append("' y='");
		desc.append(y);
		desc.append("'/>");
	}

	@Override
	public PortSet getPortSet() {

		return portSet.deepCopy();
	}

	@Override
	public void setPortSet(PortSet portSet) {

		this.portSet = portSet;
		portSet.updatePosition(boundary);
	}

	@Override
	public void illuminatePorts(boolean illuminate) {

		if (illuminatePorts != illuminate) {
			illuminatePorts = illuminate;
		}
	}

	@Override
	public boolean arePortsilluminated() {

		return illuminatePorts;
	}

	// @Override
	@Override
	public String getName() {

		return "";// name;
	}


	@Override
	public Object deepCopy() {

		return new DefaultVisualNode(this);
	}

	// ///////////////////////////////////////////////////////////////////////
	// implementation of the interface LayoutableEdge

	// @Override
	// public int getPortNextTo(Point pt) {
	//
	// if (portSet != null) {
	// return portSet.getPortNextTo(pt);
	// }
	// return 0;
	// }
	//
	// @Override
	// public int[] getPortAcceptedInterval(int portId) {
	//
	// if (portSet != null) {
	// Port port = portSet.getPortByID(portId);
	// if (port != null) {
	// return port.getAcceptedInterval();
	// }
	// }
	// return null;
	// }
	//
	// @Override
	// public int getPortByAngle(double angle) {
	//
	// if (portSet != null) {
	// return portSet.getPortByAngle(angle);
	// }
	// return 0;
	// }
	//
	// @Override
	// public Point getPortPosition(int portId) {
	//
	// if (portSet != null) {
	// Port port = portSet.getPortByID(portId);
	// if (port != null) {
	// return port.getPosition();
	// }
	// }
	// return null;
	// }
	//
	// @Override
	// public LayoutableEdge[] incomingEdges() {
	//
	// return incomingEdges.toArray(new LayoutableEdge[incomingEdges.size()]);
	// }
	//
	// @Override
	// public LayoutableEdge[] outgoingEdges() {
	//
	// return outgoingEdges.toArray(new LayoutableEdge[outgoingEdges.size()]);
	// }

	// @Override
	// public String getFormID() {
	//
	// return formID;
	// }
	//
	// @Override
	// public Form getForm() {
	//
	// return form;
	// }
	//
	// @Override
	// public void setFormID(String formID) {
	//
	// this.formID = formID;
	// properties = PropertyUtil.setProperty(properties,
	// PropertyConstants.NODE_PROPERTY_FORM,
	// formID);
	//
	// final Form f = FormPool.get(formID);
	// if (f != null) {
	// form = f.deepCopy(this);
	// form.invalidate();
	// } else {
	// form = null;
	// }
	//
	// invalidatePreview();
	//
	// if (parent != null) {
	// parent.updateView();
	// }
	// }
	//
	// @Override
	// public String[] getSlots() {
	//
	// return DefaultNodeView.dockingSlots;
	// }
	//
	// @Override
	// public Point getSlotLocation(String id) {
	//
	// if (id.equals(DefaultNodeView.dockingSlots[0])) {
	// return new Point(boundary.x + boundary.width / 2, boundary.y);
	// }
	// if (id.equals(DefaultNodeView.dockingSlots[1])) {
	// return new Point(boundary.x + boundary.width, boundary.y);
	// }
	// if (id.equals(DefaultNodeView.dockingSlots[2])) {
	// return new Point(boundary.x + boundary.width, boundary.y +
	// boundary.height / 2);
	// }
	// if (id.equals(DefaultNodeView.dockingSlots[3])) {
	// return new Point(boundary.x + boundary.width, boundary.y +
	// boundary.height);
	// }
	// if (id.equals(DefaultNodeView.dockingSlots[4])) {
	// return new Point(boundary.x + boundary.width / 2, boundary.y +
	// boundary.height);
	// }
	// if (id.equals(DefaultNodeView.dockingSlots[5])) {
	// return new Point(boundary.x, boundary.y + boundary.height);
	// }
	// if (id.equals(DefaultNodeView.dockingSlots[6])) {
	// return new Point(boundary.x, boundary.y + boundary.height / 2);
	// }
	// if (id.equals(DefaultNodeView.dockingSlots[7])) {
	// return new Point(boundary.x, boundary.y);
	// }
	//
	// return new Point(boundary.x + boundary.width / 2, boundary.y +
	// boundary.height / 2);
	//
	// }

	//
	// @Override
	// public void dockingFormBoundaryChanged(Rectangle expand) {
	//
	// Rectangle r = boundary.union(expand);
	// if(!r.equals(boundary)) {
	// setBounds(r);
	// }
	// }
	//
	// @Override
	// public void redraw() {
	// fireNodeHighlightingChanged();
	//
	// }

	// //////////////////////////////////////////////////////////////////////////
	// Notifications - sending notification to listener

	protected List<VisualNodeListener> nodeViewListener = new ArrayList<VisualNodeListener>();
	protected boolean fireEvents = true;

	@Override
	public void addNodeViewListener(VisualNodeListener listener) {

		if (!nodeViewListener.contains(listener)) {
			nodeViewListener.add(listener);
		}
	}

	@Override
	public void removeNodeViewListener(VisualNodeListener listener) {

		nodeViewListener.remove(listener);
	}

	// @Override
	// public void fireEvents(boolean enable) {
	//
	// fireEvents = enable;
	// }
	//
	// @Override
	// public boolean isFiringEvents() {
	//
	// return fireEvents;
	// }

	protected void fireNodeStartedChangingBoundary() {

		if (!fireEvents) {
			return;
		}
		for (final VisualNodeListener l : nodeViewListener) {
			l.nodeStartedChangingBoundary(this);
		}
	}

	protected void fireNodeBoundaryChanging() {

		if (!fireEvents) {
			return;
		}
		for (final VisualNodeListener l : nodeViewListener) {
			l.nodeBoundaryChangning(this);
		}

		setModified(true);
	}

	protected void fireNodeStoppedChangingBoundary(Rectangle oldBoundary) {

		if (!fireEvents) {
			return;
		}
		for (final VisualNodeListener l : nodeViewListener) {
			l.nodeStoppedChangingBoundary(this, oldBoundary);
		}
	}

	protected void fireNodeManipulated() {

		if (!fireEvents) {
			return;
		}
		for (final VisualNodeListener l : nodeViewListener) {
			l.nodeManipulated();
		}
		setModified(true);
	}

	protected void fireNodeSelectionChanged() {

		if (!fireEvents) {
			return;
		}
		for (final VisualNodeListener l : nodeViewListener) {
			l.nodeSelectionChanged(this);
		}
	}

	protected void fireNodeHighlightingChanged() {

		if (!fireEvents) {
			return;
		}
		for (final VisualNodeListener l : nodeViewListener) {
			l.nodeHighlightingChanged(this);
		}
	}

	// // ===================================================
	// // redo/undo handling
	//
	// private static final String BOUNDARY_CHANGED = "boundary changed";
	// private static final String NODE_MOVED = "node moved";
	//
	// @Override
	// public void undo(Object action) {
	//
	// internalRedoUndo(action, true);
	// }
	//
	// @Override
	// public void redo(Object action) {
	//
	// internalRedoUndo(action, false);
	// }
	//
	// private void internalRedoUndo(Object data, boolean undo) {
	//
	// PropertyList pl = (PropertyList) data;
	// PropertyUnit p;
	// if (DefaultNodeView.BOUNDARY_CHANGED.equals(pl.getName())) {
	// PropertyList rectInfo = (PropertyList) pl.get(undo ? "old" : "new");
	// p = (PropertyUnit) rectInfo.get("x");
	// int x = ConvertUtil.object2int(p.getValue());
	// p = (PropertyUnit) rectInfo.get("y");
	// int y = ConvertUtil.object2int(p.getValue());
	// p = (PropertyUnit) rectInfo.get("w");
	// int w = ConvertUtil.object2int(p.getValue());
	// p = (PropertyUnit) rectInfo.get("h");
	// int h = ConvertUtil.object2int(p.getValue());
	// Rectangle b = boundary;
	//
	// setBounds(new Rectangle(x, y, w, h));
	//
	// } /*
	// * else if (NODE_MOVED.equals(pl.getName())) { p = (PropertyUnit)
	// * pl.get("dx"); int dx = ConvertUtil.object2int(p.getValue()); p =
	// * (PropertyUnit) pl.get("dy"); int dy =
	// * ConvertUtil.object2int(p.getValue()); move(undo ? -dx : dx, undo ?
	// * -dy : dy); }
	// */
	// }
	//
	// // private void registerChangingPosition(int dx, int dy) {
	// // if (undoService.undoRedoInProcess())
	// // return;
	// //
	// // if (dx == 0 && dy == 0)
	// // return;
	// //
	// // PropertyList data = new DefaultPropertyList(NODE_MOVED);
	// // data.add(new DefaultPropertyUnit("dx", new Integer(dx)));
	// // data.add(new DefaultPropertyUnit("dy", new Integer(dy)));
	// //
	// // undoService.registerAction(this, data);
	// // }
	//
	private void registerChangingBoundary(Rectangle oldBounds) {

		// if (undoRedoHandler.undoRedoInProcess() || parent == null) {
		// return;
	}
	//
	// PropertyList data = new
	// DefaultPropertyList(DefaultNodeView.BOUNDARY_CHANGED);
	// PropertyList oldRectInfo = new DefaultPropertyList("old");
	// oldRectInfo.add(new DefaultPropertyUnit("x", new Integer(oldBounds.x)));
	// oldRectInfo.add(new DefaultPropertyUnit("y", new Integer(oldBounds.y)));
	// oldRectInfo.add(new DefaultPropertyUnit("w", new
	// Integer(oldBounds.width)));
	// oldRectInfo.add(new DefaultPropertyUnit("h", new
	// Integer(oldBounds.height)));
	//
	// PropertyList newRectInfo = new DefaultPropertyList("new");
	// newRectInfo.add(new DefaultPropertyUnit("x", new Integer(boundary.x)));
	// newRectInfo.add(new DefaultPropertyUnit("y", new Integer(boundary.y)));
	// newRectInfo.add(new DefaultPropertyUnit("w", new
	// Integer(boundary.width)));
	// newRectInfo.add(new DefaultPropertyUnit("h", new
	// Integer(boundary.height)));
	// data.add(oldRectInfo);
	// data.add(newRectInfo);
	//
	// undoRedoHandler.registerAction(this, data);
	// }


}
