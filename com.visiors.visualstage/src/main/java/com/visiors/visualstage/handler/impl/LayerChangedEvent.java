package com.visiors.visualstage.handler.impl;

import com.visiors.visualstage.document.layer.Layer;

public class LayerChangedEvent {

    private final Layer selectedLayer;

    public LayerChangedEvent(Layer selectedLayer) {

        this.selectedLayer = selectedLayer;
    }

    public Layer getLayer() {

        return selectedLayer;
    }

}
