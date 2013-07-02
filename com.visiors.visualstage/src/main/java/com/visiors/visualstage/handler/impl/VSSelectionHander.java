package com.visiors.visualstage.handler.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.handler.SelectionHandler;
import com.visiors.visualstage.stage.adapter.GraphViewAdapter;
import com.visiors.visualstage.stage.edge.EdgeView;
import com.visiors.visualstage.stage.graph.GraphObjectView;
import com.visiors.visualstage.stage.graph.GraphView;
import com.visiors.visualstage.stage.listener.GraphViewListener;
import com.visiors.visualstage.stage.node.NodeView;
import com.visiors.visualstage.view.xxx.GraphObjectView;

public class VSSelectionHander extends GraphViewAdapter implements GraphViewListener, SelectionHandler {
    protected boolean multiselection;
    protected GraphView graphView;
    private boolean processing;
    private volatile List<GraphObjectView> listOfSelectedObjects;

    @Inject
    public VSSelectionHander() {

    }

    @Override
    public void setScope(GraphView graphView) {

        this.graphView = graphView;
        graphView.addGraphViewListener(this);
        listOfSelectedObjects = new ArrayList<GraphObjectView>();
    }

    @Override
    public void setMuliSelectionMode(boolean multiselection) {

        this.multiselection = multiselection;
    }

    @Override
    public boolean isMuliSelectionMode() {

        return multiselection;
    }

    @Override
    public int getSelectionCount() {

        return listOfSelectedObjects.size();
    }

    @Override
    public void clearSelection() {

        try {
            processing = true;
            while (listOfSelectedObjects.size() != 0) {
                GraphObjectView vgo = listOfSelectedObjects.remove(0);
                vgo.setSelected(false);
                // if(vgo instanceof GraphView) {
                // ((GraphView) vgo).getSelectionService().clearSelection();
                // }
            }
        } finally {
            processing = false;
        }
    }

    /**
     * This method inverts the selection state of the specified object. It considers the current selection mode: while
     * in multi-selection mode only the state of the specified object will be changed, in single-selection mode all
     * other objects will be unselected.
     * 
     * @param graphObject the {@link GraphObjectView} of which the selection state is to be changed
     */
    @Override
    public void invertObjectSelection(GraphObjectView graphObject) {

        GraphObjectView vobj;
        GraphObjectView[] objects = graphView.getGraphObjects();

        for (int i = 0; i < objects.length; i++) {
            vobj = objects[i];
            if (vobj == graphObject) {
                vobj.setSelected(!vobj.isSelected() || !multiselection);
            } else {
                if (!multiselection) {
                    vobj.setSelected(false);
                }
            }
        }
    }

    /**
     * This method set the select
     * 
     * @param graphObject
     * @param selected
     */
    @Override
    public void select(GraphObjectView graphObject, boolean selected) {

        GraphObjectView vobj;

        GraphObjectView[] objects = graphView.getGraphObjects();

        for (int i = 0; i < objects.length; i++) {
            vobj = objects[i];
            if (vobj == graphObject) {
                if (vobj.isSelected() != selected) {
                    vobj.setSelected(selected);
                }
                break;
            }
        }

    }

    @Override
    public void select(List<GraphObjectView> graphObject) {

        clearSelection();

        for (GraphObjectView vobj : graphObject) {
            vobj.setSelected(true);
        }

    }

    @Override
    public List<GraphObjectView> getSelection() {

        return new ArrayList<GraphObjectView>(listOfSelectedObjects);
    }

    // ////////////////////////////////////////////////////////////
    // Graph View events
    @Override
    public void nodeSelectionChanged(NodeView node) {

        if (processing) {
            return;
        }

        int index = listOfSelectedObjects.indexOf(node);
        if (index == -1 && node.isSelected()) {
            listOfSelectedObjects.add(node);
        } else if (index != -1 && !node.isSelected()) {
            listOfSelectedObjects.remove(index);
        }
    }

    @Override
    public void invalidate() {

        listOfSelectedObjects.clear();

        graphView.clearSelection();
        GraphObjectView[] vgos = graphView.getGraphObjects();

        for (GraphObjectView GraphObjectView : vgos) {

            if (GraphObjectView instanceof GraphView) {
                GraphView gv = (GraphView) GraphObjectView;
                listOfSelectedObjects.addAll(gv.getSelection());
            }
            if (GraphObjectView.isSelected()) {
                listOfSelectedObjects.add(GraphObjectView);
            }
        }

    }

    @Override
    public void nodeAdded(NodeView node) {

        if (node.isSelected() && !listOfSelectedObjects.contains(node)) {
            listOfSelectedObjects.add(node);
        }
    }

    @Override
    public void nodeRemoved(NodeView node) {

        int index = listOfSelectedObjects.indexOf(node);
        if (index != -1) {
            listOfSelectedObjects.remove(index);
        }
    }

    @Override
    public void edgeAdded(EdgeView edge) {

        if (edge.isSelected() && !listOfSelectedObjects.contains(edge)) {
            listOfSelectedObjects.add(edge);
        }
    }

    @Override
    public void edgeRemoved(EdgeView edge) {

        int index = listOfSelectedObjects.indexOf(edge);
        if (index != -1) {
            listOfSelectedObjects.remove(index);
        }
    }

    @Override
    public void edgeSelectionChanged(EdgeView edge) {

        if (processing) {
            return;
        }

        int index = listOfSelectedObjects.indexOf(edge);
        if (index == -1 && edge.isSelected()) {
            listOfSelectedObjects.add(edge);
        } else if (index != -1 && !edge.isSelected()) {
            listOfSelectedObjects.remove(index);
        }
    }

}
