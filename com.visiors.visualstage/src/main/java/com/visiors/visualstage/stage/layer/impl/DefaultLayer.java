package com.visiors.visualstage.stage.layer.impl;

import java.awt.Color;

import com.visiors.visualstage.stage.graph.GraphView;
import com.visiors.visualstage.stage.layer.Layer;

public class DefaultLayer implements Layer {

    private int id;
    private boolean visible;
    private Color bkColor;
    private int order;
    private final GraphView graphView;

    public DefaultLayer(int id, int order, GraphView graphView) {

        this.id = id;
        this.order = order;
        this.graphView = graphView;
        visible = true;
    }

    @Override
    public int getID() {

        return id;
    }

    @Override
    public void setID(int id) {

        this.id = id;
    }

    @Override
    public boolean isVisible() {

        return visible;
    }

    @Override
    public void setVisible(boolean visible) {

        this.visible = visible;
    }

    @Override
    public Color getBackgroundColor() {

        return bkColor;
    }

    @Override
    public void setBackgroundColor(Color bkc) {

        bkColor = bkc;
    }

    @Override
    public int getOrder() {

        return order;
    }

    @Override
    public void setOrder(int order) {

        this.order = order;
    }

    @Override
    public GraphView getGraphView() {

        return graphView;
    }
}
