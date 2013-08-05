package com.visiors.visualstage.handler.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.constants.XMLConstants;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.util.PropertyUtil;

public class DefaultClipboardHandler implements ClipboardHandler {

	private static final int PASTE_OFFSET_STEP = 10;
	private int localPasteOffset;
	protected static String clipboardContent;

	// @Inject
	// EventBus eventbus;

	private GraphDocument graphDocument;

	@Inject
	private UndoRedoHandler undoRedoHandler;

	@Inject
	public DefaultClipboardHandler() {

		// eventbus.register(this);
	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		this.graphDocument = graphDocument;

	}

	// @Subscribe
	// public void handleLayerChange(LayerChangedEvent event) {
	//
	// selectionHandler =
	// event.getLayer().getGraphViewer().getSelectionHandler();
	// }

	@Override
	public void clear() {

		DefaultClipboardHandler.clipboardContent = null;
	}

	@Override
	public boolean canCopy() {

		final List<VisualGraphObject> selection = graphDocument.getGraph().getSelection();
		return selection.size() != 0;
	}

	@Override
	public boolean canPaste() {

		return DefaultClipboardHandler.clipboardContent != null;
	}

	@Override
	public void copySelection() {

		final List<VisualGraphObject> selection = graphDocument.getGraph().getSelection();
		if (selection.size() == 0) {
			return;
		} else if (selection.size() > 0) {
			PropertyList properties = new DefaultPropertyList();
			visualObjects2ProperyList(selection, properties);

			DefaultClipboardHandler.clipboardContent = PropertyUtil.propertyList2XML(properties, true);
		}
		localPasteOffset = DefaultClipboardHandler.PASTE_OFFSET_STEP;
	}

	@Override
	public void paste() {

		if (DefaultClipboardHandler.clipboardContent != null) {
			final PropertyList properties = PropertyUtil.XML2PropertyList(DefaultClipboardHandler.clipboardContent);
			if (properties != null) {
				try {
					undoRedoHandler.stratOfGroupAction();
					graphDocument.getGraph().clearSelection();

					Rectangle rGraph = graphDocument.getGraph().getBounds();

					List<VisualGraphObject> newGraphObjects = graphDocument.getGraph().createGraphObjects(properties);
					Rectangle r = getObjectsArea(newGraphObjects);
					if (!r.union(rGraph).equals(rGraph)) {
						r.x = rGraph.x;
						r.y = rGraph.y;
					}
					Point at = r.getLocation();
					at.translate(localPasteOffset, localPasteOffset);
					moveObjectsToTargetLocation(newGraphObjects, at);

					graphDocument.getGraph().setSelection(newGraphObjects);

					localPasteOffset += DefaultClipboardHandler.PASTE_OFFSET_STEP;

				} finally {
					undoRedoHandler.endOfGroupAction();
				}
			}
		}
	}

	// get the original objects area
	private Rectangle getObjectsArea(List<VisualGraphObject> objects) {

		Rectangle r = new Rectangle();

		for (int i = 0; i < objects.size(); i++) {
			VisualGraphObject o = objects.get(i);
			if (i == 0) {
				r = o.getBounds();
			} else {
				r = r.union(o.getBounds());
			}
		}
		return r;
	}

	private void moveObjectsToTargetLocation(List<VisualGraphObject> objects, Point at) {

		// get the original objects area
		Rectangle r = getObjectsArea(objects);

		// move the edges
		for (int i = 0; i < objects.size(); i++) {
			VisualGraphObject o = objects.get(i);
			if (o instanceof VisualEdge) {
				VisualEdge e = (VisualEdge) o;
				int dx = -r.x + at.x;
				int dy = -r.y + at.y;
				e.move(dx, dy);
			}
		}
		// move the nodes
		for (int i = 0; i < objects.size(); i++) {
			VisualGraphObject o = objects.get(i);
			if (o instanceof VisualNode) {
				VisualNode n = (VisualNode) o;
				int dx = -r.x + at.x;
				int dy = -r.y + at.y;
				n.move(dx, dy);
			}
		}
	}

	private void visualObjects2ProperyList(List<VisualGraphObject> objects, PropertyList properties) {

		final PropertyList edgesProperties = new DefaultPropertyList(XMLConstants.EDGES_SECTION_TAG);
		final PropertyList nodesProperties = new DefaultPropertyList(XMLConstants.NODES_SECTION_TAG);
		final PropertyList subgraphProperties = new DefaultPropertyList(XMLConstants.SUBGRAPHS_SECTION_TAG);

		for (final VisualGraphObject vgo : objects) {
			if (vgo instanceof VisualGraph) {
				visualObjects2ProperyList(((VisualGraph) vgo).getGraphObjects(), vgo.getProperties());
				subgraphProperties.add(subgraphProperties);
			} else if (vgo instanceof VisualNode) {

				nodesProperties.add(vgo.getProperties());
			} else if (vgo instanceof VisualEdge) {
				edgesProperties.add(vgo.getProperties());
			}
		}
		if (edgesProperties.size() != 0) {
			properties.add(edgesProperties);
		}
		if (nodesProperties.size() != 0) {
			properties.add(nodesProperties);
		}
		if (subgraphProperties.size() != 0) {
			properties.add(subgraphProperties);
		}
	}

}
