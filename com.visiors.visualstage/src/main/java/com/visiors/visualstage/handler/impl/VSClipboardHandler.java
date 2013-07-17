package com.visiors.visualstage.handler.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.io.GraphBuilder;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.stage.edge.EdgeView;
import com.visiors.visualstage.stage.graph.GraphObjectView;
import com.visiors.visualstage.stage.graph.GraphView;
import com.visiors.visualstage.stage.node.NodeView;
import com.visiors.visualstage.view.xxx.GraphObjectView;

public class VSClipboardHandler implements ClipboardHandler {

    private static final int PASTE_OFFSET_STEP = 10;
    private int localPasteOffset;
    protected static String clipboardContent;

    // @Inject
    // EventBus eventbus;

    private VisualGraph visualGraph;

    @Inject
    private UndoRedoHandler undoRedoHandler;

    @Inject
    public VSClipboardHandler() {

        // eventbus.register(this);
    }

    @Override
    public void setScope(VisualGraph visualGraph) {

        this.visualGraph = visualGraph;

    }

    // @Subscribe
    // public void handleLayerChange(LayerChangedEvent event) {
    //
    // selectionHandler = event.getLayer().getGraphViewer().getSelectionHandler();
    // }

    @Override
    public void clear() {

        VSClipboardHandler.clipboardContent = null;
    }

    @Override
    public boolean canCopy() {

        final List<VisualGraphObject> selection = visualGraph.getSelection();
        return selection.size() != 0;
    }

    @Override
    public boolean canPaste() {

        return VSClipboardHandler.clipboardContent != null;
    }

    @Override
    public void copySelection() {

        final List<VisualGraphObject> selection = visualGraph.getSelection();
        if (selection.size() == 0) {
            return;
        } else if (selection.size() > 0) {
            VisualGraphObject[] objectToCopy = selection.toArray(new VisualGraphObject[0]);
            PropertyList properties = new DefaultPropertyList();
            GraphBuilder.visualObjects2ProperyList(objectToCopy, properties);
            VSClipboardHandler.clipboardContent = GraphBuilder.propertyList2XML(properties, true);
        }
        localPasteOffset = VSClipboardHandler.PASTE_OFFSET_STEP;
    }

    @Override
    public void paste() {

        if (VSClipboardHandler.clipboardContent != null) {
            PropertyList propertes = GraphBuilder.XML2PropertyList(VSClipboardHandler.clipboardContent);
            if (propertes != null) {
                try {
                    undoRedoHandler.stratOfGroupAction();
                    visualGraph.clearSelection();

                    Rectangle rGraph = visualGraph.getBounds();
                    List<VisualGraphObject> objects = GraphBuilder.createGraphObjects(propertes, visualGraph, true);
                    Rectangle r = getObjectsArea(objects);
                    if (!r.union(rGraph).equals(rGraph)) {
                        r.x = rGraph.x;
                        r.y = rGraph.y;
                    }
                    Point at = r.getLocation();
                    at.translate(localPasteOffset, localPasteOffset);
                    moveObjectsToTargetLocation(objects, at);

                    visualGraph.setSelection(objects);

                    localPasteOffset += VSClipboardHandler.PASTE_OFFSET_STEP;

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

}
