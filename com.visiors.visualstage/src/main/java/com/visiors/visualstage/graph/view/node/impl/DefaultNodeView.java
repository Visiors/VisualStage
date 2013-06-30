package com.visiors.visualstage.graph.view.node.impl;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.xerces.impl.dv.util.Base64;

import com.visiors.visualstage.generics.attribute.DefaultAttribute;
import com.visiors.visualstage.generics.attribute.PropertyList;
import com.visiors.visualstage.generics.attribute.PropertyListener;
import com.visiors.visualstage.generics.interaction.Interactable;
import com.visiors.visualstage.generics.renderer.RenderingContext;
import com.visiors.visualstage.generics.renderer.RenderingContext.Resolution;
import com.visiors.visualstage.generics.renderer.resource.svg.SVGDefinition;
import com.visiors.visualstage.generics.renderer.resource.svg.SVGDefinitionPool;
import com.visiors.visualstage.graph.view.Constants;
import com.visiors.visualstage.graph.view.DefaultGraphObjectView;
import com.visiors.visualstage.graph.view.GraphObjectView;
import com.visiors.visualstage.graph.view.ViewConstants;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.listener.NodeViewListener;
import com.visiors.visualstage.graph.view.node.NodeView;
import com.visiors.visualstage.graph.view.node.Port;
import com.visiors.visualstage.graph.view.node.PortSet;

public class DefaultNodeView extends DefaultGraphObjectView implements NodeView, PropertyListener {

	protected static final int RESIZE_WEST = 1;
	protected static final int RESIZE_SOUTH = 2;
	protected static final int RESIZE_EAST = 3;
	protected static final int RESIZE_NORTH = 4;
	protected static final int RESIZE_NORTH_WEST = 5;
	protected static final int RESIZE_SOUTH_WEST = 6;
	protected static final int RESIZE_NORTH_EAST = 7;
	protected static final int RESIZE_SOUTH_EAST = 8;

	protected static final int PROCESSING_BY_ATTACHMENT = 9;

	public static final String[] dockingSlots = new String[] { "North", "NorthEast", "East",
			"SouthEast", "South", "SouthWest", "West", "NorthWest", "Center" };

	protected List<EdgeView> incomingEdges;
	protected List<EdgeView> outgoingEdges;
	protected PortSet portSet;
	protected Rectangle boundary;

	private Rectangle oldRect;
	private boolean illuminatePorts;
	// private Form form;
	private int manipulationID = DefaultGraphObjectView.NONE;
	// private final String name;
	private PropertyList properties;
	protected SVGDefinition selDef;
	protected SVGDefinition portDef;
	protected SVGDefinition portHLDef;
	protected SVGDefinition svgDef;
	protected String presentationID;
	protected String styleID;
	protected String formID;

	// private final VisualObjectPreviewGenerator previewCreator;
	// private final List<CachedImage> cachedImage;

	protected DefaultNodeView(String name) {

		super(name);
	}

	protected DefaultNodeView(String name, long id) {

		super(name, id);

		/* this.name = name; */
		this.portSet = new DefaultPortSet();
		this.incomingEdges = new ArrayList<EdgeView>();
		this.outgoingEdges = new ArrayList<EdgeView>();
		this.boundary = new Rectangle(0, 0, 80, 50);
		this.attributes = new DefaultAttribute();
		this.attributes = new DefaultAttribute();

		// previewCreator = new VisualObjectPreviewGenerator(this);
		// cachedImage = new ArrayList<CachedImage>();
	}

	protected DefaultNodeView(NodeView node, long id) {

		this(node.getName(), id);

		this.setBounds(node.getBounds());
		this.SetAttributes(node.getAttributes());
		this.incomingEdges = new ArrayList<EdgeView>(node.getIncomingEdges());
		this.outgoingEdges = new ArrayList<EdgeView>(node.getOutgoingEdges());
		this.setProperties(node.getProperties());
		this.setStyleID(node.getStyleID());
		this.setPresentationID(node.getPresentationID());
		/* this.setFormID(node.getFormID()); */
		if (node.getPortSet() != null) {
			portSet = node.getPortSet();
		}
		if (node.getCustomData() != null) {
			setCustomData(node.getCustomData().deepCopy());
		}
	}

	@PostConstruct
	protected void init() {

		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_ID, id);
		// PropertyUtil.makeEditable(properties,
		// PropertyConstants.NODE_PROPERTY_ID, false);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_NAME,
		// name);
		// PropertyUtil.makeEditable(properties,
		// PropertyConstants.NODE_PROPERTY_NAME, false);
		//
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_X,
		// boundary.x);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_Y,
		// boundary.y);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_WIDTH,
		// boundary.width);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_HEIGHT,
		// boundary.height);
		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_ID, id);
		//
		// //
		// PropertyUtil.setPropertyRange(properties,Constants.NODE_PROPERTY_X,
		// // "5, 15, 25");
		// properties.addPropertyListener(this);
	}

	@Override
	public void setParentGraph(GraphView graph) {

		super.setParentGraph(graph);

		// properties = PropertyUtil.setProperty(properties,
		// PropertyConstants.NODE_PROPERTY_PARENT_ID, (parent != null ?
		// parent.getID() : -1));
		// PropertyUtil.makeEditable(properties,
		// PropertyConstants.NODE_PROPERTY_PARENT_ID, false);
	}

	// @Override
	// public void setTransform(Transform transformer) {
	// if(this.transformer != transformer) {
	// if(this.transformer != null)
	// this.transformer.removeListener(this);
	// super.setTransform(transformer);
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
	public List<EdgeView> getOutgoingEdges() {

		return outgoingEdges;
	}

	@Override
	public List<EdgeView> getIncomingEdges() {

		return incomingEdges;
	}

	@Override
	public List<EdgeView> getConnectedEdges() {

		List<EdgeView> edges = new ArrayList<EdgeView>();
		edges.addAll(incomingEdges);
		edges.addAll(outgoingEdges);
		return edges;
	}

	private final boolean pointHit(int x, int y, Point ptHit) {

		int ms = ViewConstants.RESIZING_MARKER_SNAP_RADIUS;
		return ptHit.x >= x - ms && ptHit.x <= x + ms && ptHit.y >= y - ms && ptHit.y <= y + ms;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		manipulationID = DefaultGraphObjectView.NONE;

		if (attributes.isResizable() && isSelected()) {

			int ms = ViewConstants.MARKER_SIZE / 2;
			int x1 = boundary.x - ms;
			int y1 = boundary.y - ms;
			int cx = boundary.x + boundary.width / 2;
			int cy = boundary.y + boundary.height / 2;
			int x2 = boundary.x + boundary.width + ms;
			int y2 = boundary.y + boundary.height + ms;

			if (pointHit(x1, cy - ms, pt)) {
				manipulationID = DefaultNodeView.RESIZE_WEST;
			} else if (pointHit(x1, y1, pt)) {
				manipulationID = DefaultNodeView.RESIZE_NORTH_WEST;
			} else if (pointHit(x1, y2, pt)) {
				manipulationID = DefaultNodeView.RESIZE_SOUTH_WEST;
			} else if (pointHit(x2, cy, pt)) {
				manipulationID = DefaultNodeView.RESIZE_EAST;
			} else if (pointHit(x2, y1, pt)) {
				manipulationID = DefaultNodeView.RESIZE_NORTH_EAST;
			} else if (pointHit(x2, y2, pt)) {
				manipulationID = DefaultNodeView.RESIZE_SOUTH_EAST;
			} else if (pointHit(cx, y1, pt)) {
				manipulationID = DefaultNodeView.RESIZE_NORTH;
			} else if (pointHit(cx, y2, pt)) {
				manipulationID = DefaultNodeView.RESIZE_SOUTH;
			} /*
			   * else { if (form != null) { form.mouseMoved(pt, button,
			   * functionKey); } }
			   */
		}
		return manipulationID != DefaultGraphObjectView.NONE;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (manipulationID == DefaultGraphObjectView.NONE) {
			return false;
		}

		finishInteraction();

		Rectangle r = getBounds();

		int dx = r.x - pt.x;
		int dy = r.y - pt.y;
		int dw = r.x + r.width;
		int dh = r.y + r.height;

		switch (manipulationID) {
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

		if (manipulationID == DefaultGraphObjectView.NONE) {
			return false;
		}
		if (manipulationID == DefaultNodeView.PROCESSING_BY_ATTACHMENT) {
			/*
			 * if (form != null) { if (form.mousePressed(pt, button,
			 * functionKey)) { return true; } }
			 */
		}
		startManipulating();
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (manipulationID == DefaultGraphObjectView.NONE) {
			return false;
		}
		if (manipulationID == DefaultNodeView.PROCESSING_BY_ATTACHMENT) {
			/*
			 * if (form != null) { if (form.mouseReleased(pt, button,
			 * functionKey)) { return true; } }
			 */
		}
		endManipulating();
		return false;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		if (manipulationID == DefaultGraphObjectView.NONE) {
			/*
			 * if (form != null) { if (form.mouseDoubleClicked(pt, button,
			 * functionKey)) { manipulationID =
			 * DefaultNodeView.PROCESSING_BY_ATTACHMENT; return true; } }
			 */
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		if (manipulationID == DefaultNodeView.PROCESSING_BY_ATTACHMENT) {
			/*
			 * if (form != null) { if (form.keyPressed(keyChar, keyCode)) {
			 * return true; } }
			 */
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		if (manipulationID == DefaultNodeView.PROCESSING_BY_ATTACHMENT) {
			/*
			 * if (form != null) { if (form.keyReleased(keyChar, keyCode)) {
			 * return true; } }
			 */
		}
		return false;
	}

	@Override
	public int getPreferredCursor() {

		switch (manipulationID) {
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

		return manipulationID != DefaultGraphObjectView.NONE;
	}

	@Override
	public int getPreferredPort(Point pt) {

		if (portSet != null) {
			return portSet.getPortNextTo(pt);
		}
		return NONE;
	}

	@Override
	public boolean preConnect(EdgeView edge, NodeView opositeNode, boolean incomingConnection) {

		return true;
	}

	//
	@Override
	public void postConnected(EdgeView edge, NodeView opositeNode, boolean incomingConnection) {

		if (incomingConnection) {
			incomingEdges.add(edge);
		} else {
			outgoingEdges.add(edge);
		}

	}

	@Override
	public void postDisconnected(EdgeView edge) {

		if (edge.getSourceNode().equals(this)) {
			outgoingEdges.remove(edge);
		} else {
			incomingEdges.remove(edge);
		}
	}

	@Override
	public void highlightPort(int portID, boolean on) {

		Port[] ports = portSet.getPorts();
		for (Port port : ports) {
			port.setHighlighted(port.getID() == portID && on);
		}
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append("NodeView (").append("id= ").append(getID()).append(", name = ")
				.append(getName()).append(", indegree = ").append(String.valueOf(getIndegree()))
				.append(", outdegree = ").append(String.valueOf(getOutdegree()))
				.append(", boundary: x = ").append(boundary.x).append(", y = ").append(boundary.y)
				.append(", width = ").append(boundary.width).append(", height = ")
				.append(boundary.height).append(" ]");
		return sb.toString();

	}

	@Override
	public void setPresentationID(String presentationID) {

		this.presentationID = presentationID;

		/*
		 * properties = PropertyUtil.setProperty(properties,
		 * PropertyConstants.NODE_PROPERTY_PRESENTATION, presentationID);
		 */

		// invalidatePreview();
		//
		// if (parent != null) {
		// parent.updateView();
		// }
	}

	@Override
	public String getPresentationID() {

		return presentationID;
	}

	@Override
	public void setStyleID(String styleID) {

		this.styleID = styleID;
		/*
		 * properties = PropertyUtil.setProperty(properties,
		 * PropertyConstants.NODE_PROPERTY_STYLE, styleID);
		 */

		// invalidatePreview();

		// if (parent != null) {
		// parent.updateView();
		// }
	}

	@Override
	public String getStyleID() {

		return styleID;
	}

	// ///////////////////////////////////////////////////////////////////////
	// implementation of the interface Attributable

	// @Override
	// public PropertyList getProperties() {
	//
	// if (properties == null) {
	// return null;
	// }
	//
	// // posts
	// if (portSet != null && properties.get(PropertyConstants.PORTS_PROPERTY)
	// == null) {
	// PropertyList portsProperties = portSet.getProperties();
	// if (portsProperties != null) {
	// properties.add(portsProperties);
	// }
	// }
	//
	// // form
	// if (form != null && properties.get(Constants.FORM_PROPERTY) == null) {
	// properties.add(form.getProperties());
	// }
	//
	// return properties.deepCopy();
	// }
	//
	// @Override
	// public void setProperties(PropertyList properties) {
	//
	// this.properties = properties;
	//
	// // adjust coordinates
	// Rectangle r = new Rectangle();
	// r.x = PropertyUtil.getProperty(properties,
	// PropertyConstants.NODE_PROPERTY_X, 0);
	// r.y = PropertyUtil.getProperty(properties,
	// PropertyConstants.NODE_PROPERTY_Y, 0);
	// r.width = PropertyUtil.getProperty(properties,
	// PropertyConstants.NODE_PROPERTY_WIDTH, 50);
	// r.height = PropertyUtil.getProperty(properties,
	// PropertyConstants.NODE_PROPERTY_HEIGHT, 50);
	// setBounds(r);
	//
	// PropertyList portsProperties = PropertyUtil.getPropertyList(properties,
	// PropertyConstants.PORTS_PROPERTY);
	// if (portsProperties != null) {
	// portSet.setProperties(portsProperties);
	// updatePortPosition();
	// }
	//
	// // PropertyList formProperties =
	// // PropertyUtil.getPropertyList(properties, Constants.FORM_PROPERTY);
	// // if(formProperties != null) {
	// // form = new DefaultForm(formProperties, this);
	// // }
	//
	// setPresentationID(PropertyUtil.getProperty(properties,
	// PropertyConstants.NODE_PROPERTY_PRESENTATION, null));
	// setStyleID(PropertyUtil
	// .getProperty(properties, PropertyConstants.NODE_PROPERTY_STYLE, null));
	// setFormID(PropertyUtil.getProperty(properties,
	// PropertyConstants.NODE_PROPERTY_FORM, null));
	//
	// // never accept the given id and name
	// properties = PropertyUtil.setProperty(properties,
	// PropertyConstants.NODE_PROPERTY_ID, id);
	// PropertyUtil.makeEditable(properties, PropertyConstants.NODE_PROPERTY_ID,
	// false);
	// properties = PropertyUtil.setProperty(properties,
	// PropertyConstants.NODE_PROPERTY_PARENT_ID, (parent != null ?
	// parent.getID() : -1));
	// PropertyUtil.makeEditable(properties,
	// PropertyConstants.NODE_PROPERTY_PARENT_ID, false);
	// PropertyUtil.makeEditable(properties,
	// PropertyConstants.NODE_PROPERTY_NAME, false);
	// properties = PropertyUtil.setProperty(properties,
	// PropertyConstants.NODE_PROPERTY_NAME,
	// name);
	// PropertyUtil.makeEditable(properties,
	// PropertyConstants.NODE_PROPERTY_NAME, false);
	// }

	// @Override
	// public void propertyChanged(List<PropertyList> path, PropertyUnit
	// property) {
	//
	// try {
	// String str = PropertyUtil.toString(path, property);
	// if (PropertyConstants.NODE_PROPERTY_X.equals(str)) {
	// propertyChanged_X(ConvertUtil.object2int(property.getValue()));
	// } else if (PropertyConstants.NODE_PROPERTY_Y.equals(str)) {
	// propertyChanged_Y(ConvertUtil.object2int(property.getValue()));
	// } else if (PropertyConstants.NODE_PROPERTY_WIDTH.equals(str)) {
	// propertyChanged_Width(ConvertUtil.object2int(property.getValue()));
	// } else if (PropertyConstants.NODE_PROPERTY_HEIGHT.equals(str)) {
	// propertyChanged_Height(ConvertUtil.object2int(property.getValue()));
	// } else if (PropertyConstants.NODE_PROPERTY_ATTACHMENT.equals(str)) {
	// propertyChanged_Panel((PropertyList) property.getValue());
	// } else if (PropertyConstants.NODE_PROPERTY_PRESENTATION.equals(str)) {
	// propertyChanged_Presenttion(ConvertUtil.object2string(property.getValue()));
	// } else if (PropertyConstants.NODE_PROPERTY_STYLE.equals(str)) {
	// propertyChanged_Style(ConvertUtil.object2string(property.getValue()));
	// } else if (PropertyConstants.NODE_PROPERTY_FORM.equals(str)) {
	// propertyChanged_Form(ConvertUtil.object2string(property.getValue()));
	// }
	//
	// } catch (Exception e) {
	// System.err.println("Error. The value " + property.getValue()
	// + "is incompatible with the property " + property.getName());
	// }
	// }
	//
	// protected void propertyChanged_X(int x) {
	//
	// if (x != boundary.x) {
	// Rectangle r = new Rectangle(boundary);
	// r.x = x;
	// setBounds(r);
	// }
	// }
	//
	// protected void propertyChanged_Y(int y) {
	//
	// if (y != boundary.y) {
	// Rectangle r = new Rectangle(boundary);
	// r.y = y;
	// setBounds(r);
	// }
	// }
	//
	// protected void propertyChanged_Width(int w) {
	//
	// if (w != boundary.width) {
	// Rectangle r = new Rectangle(boundary);
	// r.width = w;
	// setBounds(r);
	// }
	// }
	//
	// protected void propertyChanged_Height(int h) {
	//
	// if (h != boundary.height) {
	// Rectangle r = new Rectangle(boundary);
	// r.height = h;
	// setBounds(r);
	// }
	// }
	//
	// protected void propertyChanged_Panel(PropertyList propertyList) {
	//
	// if (propertyList != null && form != null) {
	// form.setProperties(propertyList);
	// }
	// }
	//
	// protected void propertyChanged_Presenttion(String id) {
	//
	// presentationID = id;
	// if (parent != null) {
	// parent.updateView();
	// }
	// }
	//
	// protected void propertyChanged_Style(String id) {
	//
	// styleID = id;
	// if (parent != null) {
	// parent.updateView();
	// }
	// }
	//
	// protected void propertyChanged_Form(String id) {
	//
	// formID = id;
	// if (parent != null) {
	// parent.updateView();
	// }
	// }

	@Override
	public Rectangle getExtendedBoundary() {

		Rectangle r = getBounds();
		if (!r.isEmpty()) {
			// considering of 1 pixel for rounding inaccuracy
			r.grow(ViewConstants.MARKER_SIZE + 1, ViewConstants.MARKER_SIZE + 1);

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

		boolean hit = getExtendedBoundary().contains(pt);
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
	public void setBounds(Rectangle r) {

		if (svgDef != null && svgDef.keepRatio) {
			r.width = (int) (r.height * svgDef.ratio);
		}

		if (r.width < ViewConstants.MIN_NODE_WIDTH) {
			r.x = boundary.x;
			r.width = ViewConstants.MIN_NODE_WIDTH;
		}
		if (r.height < ViewConstants.MIN_NODE_HEIGHT) {
			r.y = boundary.y;
			r.height = ViewConstants.MIN_NODE_HEIGHT;
		}

		if (!boundary.equals(r)) {
			if (attributes.isResizable()) {

				boolean singleMovement = oldRect == null;
				if (singleMovement) {
					startManipulating();
				}

				if (r.width != boundary.width || r.height != boundary.height) {
					// invalidatePreview();
				}

				boundary = new Rectangle(r);

				updateView();

				updatePortPosition();

				fireNodeBoundaryChanging();

				// System.err.println("interacting: " + getName());

				if (singleMovement) {
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
		List<EdgeView> edges = getIncomingEdges();
		for (EdgeView edgeView : edges) {
			edgeView.startManipulating();
		}
		edges = getOutgoingEdges();
		for (EdgeView edgeView : edges) {
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

		oldRect = null;

		List<EdgeView> edges = getIncomingEdges();
		for (EdgeView edgeView : edges) {
			edgeView.endManipulating();
		}
		edges = getOutgoingEdges();
		for (EdgeView edgeView : edges) {
			edgeView.endManipulating();
		}

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
			Rectangle r = new Rectangle(boundary);
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

	// private synchronized Image getCachedImageFor(RenderingContext ctx, double
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

	// private synchronized void updateCachedImage(RenderingContext ctx, double
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
	public Image getPreview(final RenderingContext ctx, final ImageObserver observer) {

		// Image img = getCachedImageFor(ctx, transformer.getScale(), true);
		// if (img != null) {
		// return img;
		return null;
	}

	//
	// /* preview is needed immediately */
	// if (observer == null) {
	// img = previewCreator.createPreview(ctx);
	// cachedImage.add(new CachedImage(ctx, transformer.getScale(), img));
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
	// updateCachedImage(ctx, transformer.getScale(), image);
	// observer.imageUpdate(image, infoflags, x, y, width, height);
	// return true;
	// }
	// });
	//
	// /*
	// * provide caller with a resized preview for now until the new image
	// * is ready
	// */
	// img = getCachedImageFor(ctx, transformer.getScale(), false);
	// if (img != null) {
	// Rectangle r = transformer.transformToScreen(getExtendedBoundary());
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
	public String getViewDescription(RenderingContext context) {

		switch (context.subject) {
		case OBJECT:

			if (false/* useEmbeddedImage */) {
				String imageData = ImageToString((BufferedImage) getPreview(context, null));
				if (imageData != null) {
					final Rectangle b = transformer.transformToScreen(boundary);
					final double tx = transformer.getTranslateX();
					final double ty = transformer.getTranslateY();
					final StringBuffer svg = new StringBuffer();
					svg.append("<image x='");
					svg.append(b.x - tx);
					svg.append("' y='");
					svg.append(b.y - ty);
					svg.append("' width='");
					svg.append(b.width);
					svg.append("' height='");
					svg.append(b.height);
					svg.append("' xlink:href='data:image/png;base64,");
					svg.append(imageData);
					svg.append("'/>");
					return svg.toString();
				}
			}

			return getObjectDescription(context.resolution != Resolution.SCREEN_LOW_DETAIL
					&& context.resolution != Resolution.PREVIEW, boundary);
		case SELECTION_INDICATORS:
			if (context.resolution == Resolution.SCREEN) {
				return getSelectionDescription(boundary);
			}
		case PORTS:
			if (context.resolution == Resolution.SCREEN) {
				return getPortsDescription(boundary);
			}
		}
		return null;
	}

	@Override
	public String[][] getSVGDocumentAttributes() {

		if (svgDef == null) {
			svgDef = SVGDefinitionPool.get(getPresentationID());
		}
		if (svgDef != null) {
			return svgDef.getDocumentAttributes();
		}
		return null;
	}

	private String ImageToString(BufferedImage img) {

		if (img != null) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(img, "png", baos);
				String encodedImage = Base64.encode(baos.toByteArray());
				return encodedImage;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String getObjectDescription(boolean includeForm, Rectangle boundary) {

		final StringBuffer svg = new StringBuffer();
		if (presentationID != null) {
			if (svgDef == null) {
				svgDef = SVGDefinitionPool.get(getPresentationID());
			}
			if (svgDef != null) {
				final Rectangle b = transformer.transformToScreen(boundary);
				final double tx = transformer.getTranslateX();
				final double ty = transformer.getTranslateY();
				if (styleID != null) {
					if (styleID.indexOf(':') == -1) {
						svg.append("<g class='" + styleID + "'>");
					} else {
						svg.append("<g style='" + styleID + "'>");
					}
				}
				svg.append("<use xlink:href='#");
				svg.append(presentationID);
				svg.append("' x='");
				svg.append(b.x - tx);
				svg.append("' y='");
				svg.append(b.y - ty);
				svg.append("' width='");
				svg.append(b.width);
				svg.append("' height='");
				svg.append(b.height);
				svg.append("'/>");
				if (styleID != null) {
					svg.append("</g>");
				}
			} else {
				System.err.println("presentation-ID '" + presentationID
						+ "' referes to an not existing graphical object.");
				// presentationID = null; write it only once!
			}
		}

		// if (includeForm && form != null) {
		// svg.append("<g transformer='scale(");
		// svg.append(transformer.getScaleX());
		// svg.append(",");
		// svg.append(transformer.getScaleY());
		// svg.append(")'>");
		// svg.append(form.getDescription());
		// svg.append("</g>");
		// }
		return svg.toString();
	}

	private String getSelectionDescription(Rectangle boundary) {

		if (!selected || illuminatePorts) {
			return null;
		}

		StringBuffer svg = new StringBuffer();
		if (selDef == null) {
			selDef = SVGDefinitionPool.get(getSelectionDesriptorID());
		}
		if (selDef != null) {
			Rectangle b = transformer.transformToScreen(boundary);
			svg.append("<g transformer='translate(");
			svg.append(-transformer.getTranslateX());
			svg.append(",");
			svg.append(-transformer.getTranslateY());
			svg.append(")'>");

			/*
			 * if the aspect ration is to be preserved, show only the diagonal
			 * selection markers
			 */
			if (svgDef == null) {
				svgDef = SVGDefinitionPool.get(getPresentationID());
			}
			if (svgDef != null && !svgDef.keepRatio) {
				createSelectionMarkDescriotor(svg, b.x + b.width / 2 - selDef.width / 2, b.y
						- selDef.height - 2);// N
				createSelectionMarkDescriotor(svg, b.x + b.width / 2 - selDef.width / 2, b.y
						+ b.height + 2);// S
				createSelectionMarkDescriotor(svg, b.x - selDef.width - 2, b.y + b.height / 2
						- selDef.height / 2);// W
				createSelectionMarkDescriotor(svg, b.x + b.width + 2, b.y + b.height / 2
						- selDef.height / 2);// E
			}

			createSelectionMarkDescriotor(svg, b.x - selDef.width - 2, b.y - selDef.height - 2);// NW
			createSelectionMarkDescriotor(svg, b.x - selDef.width - 2, b.y + b.height + 2);// SW
			createSelectionMarkDescriotor(svg, b.x + b.width + 2, b.y - selDef.height - 2);// NE
			createSelectionMarkDescriotor(svg, b.x + b.width + 2, b.y + b.height + 2);// SE

			svg.append("\n</g>");
		}

		return svg.toString();
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

	private String getPortsDescription(Rectangle b) {

		if (illuminatePorts && portSet != null) {

			if (portDef == null) {
				portDef = SVGDefinitionPool.get(getPortDesriptorID());
			}
			if (portDef == null) {
				System.err
						.println("Warning: cannot draw the ports. Reason: missing the SVG descriptor assosiate with '"
								+ getPortDesriptorID() + "'");
				return null;
			}

			StringBuffer sb = new StringBuffer();

			portSet.updatePosition(b);

			Point pt;
			final Port[] port = portSet.getPorts();
			final double sx = transformer.getScaleX();
			final double sy = transformer.getScaleY();
			for (Port element : port) {
				pt = element.getPosition();
				createPortIndicatorDescriotor(sb, pt.x * sx, pt.y * sy, element.isHighlighted());
			}
			portSet.updatePosition(boundary);
			return sb.toString();
		}
		return null;
	}

	private void createPortIndicatorDescriotor(StringBuffer svg, double x, double y,
			boolean highlighted) {

		if (portHLDef == null) {
			portHLDef = SVGDefinitionPool.get(getPortHighlighingDesriptorID());
		}

		if (highlighted) {
			x -= portHLDef.width / 2;
			y -= portHLDef.height / 2;
		} else {
			x -= portDef.width / 2;
			y -= portDef.height / 2;
		}
		svg.append("\n<use xlink:href='#");
		svg.append(highlighted ? getPortHighlighingDesriptorID() : getPortDesriptorID());
		svg.append("' x='");
		svg.append(x);
		svg.append("' y='");
		svg.append(y);
		svg.append("'/>");
	}

	private final void createSelectionMarkDescriotor(StringBuffer svg, double x, double y) {

		svg.append("\n<use xlink:href='#");
		svg.append(getSelectionMarkerDesriptorID());
		svg.append("' x='");
		svg.append(x);
		svg.append("' y='");
		svg.append(y);
		svg.append("'/>");
	}

	@Override
	public PortSet getPortSet() {

		return portSet.deepCopy();
	}

	@Override
	public void setPortSet(PortSet portSet) {

		this.portSet = portSet;
		updatePortPosition();
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
	public GraphObjectView deepCopy(long id) {

		return new DefaultNodeView(this, id);
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

	protected List<NodeViewListener> nodeViewListener = new ArrayList<NodeViewListener>();
	protected boolean fireEvents;

	@Override
	public void addNodeViewListener(NodeViewListener listener) {

		if (!nodeViewListener.contains(listener)) {
			nodeViewListener.add(listener);
		}
	}

	@Override
	public void removeNodeViewListener(NodeViewListener listener) {

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
		for (NodeViewListener l : nodeViewListener) {
			l.nodeStartedChangingBoundary(this);
		}
	}

	protected void fireNodeBoundaryChanging() {

		if (!fireEvents) {
			return;
		}
		for (NodeViewListener l : nodeViewListener) {
			l.nodeBoundaryChangning(this);
		}
	}

	protected void fireNodeStoppedChangingBoundary(Rectangle oldBoundary) {

		if (!fireEvents) {
			return;
		}
		for (NodeViewListener l : nodeViewListener) {
			l.nodeStoppedChangingBoundary(this, oldBoundary);
		}
	}

	protected void fireNodeManipulated() {

		if (!fireEvents) {
			return;
		}
		for (NodeViewListener l : nodeViewListener) {
			l.nodeManipulated();
		}
	}

	protected void fireNodeSelectionChanged() {

		if (!fireEvents) {
			return;
		}
		for (NodeViewListener l : nodeViewListener) {
			l.nodeSelectionChanged(this);
		}
	}

	protected void fireNodeHighlightingChanged() {

		if (!fireEvents) {
			return;
		}
		for (NodeViewListener l : nodeViewListener) {
			l.nodeHighlightingChanged(this);
		}
	}

	private void updatePortPosition() {

		if (portSet != null) {
			portSet.updatePosition(boundary);
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
