package com.visiors.visualstage.stage.layer.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.visiors.visualstage.handler.impl.LayerChangedEvent;
import com.visiors.visualstage.stage.graph.GraphView;
import com.visiors.visualstage.stage.layer.Layer;
import com.visiors.visualstage.stage.layer.MultiLayerEditor;

public class DefaultMultiLayerEditor implements MultiLayerEditor {

    @Inject
    EventBus eventbus;

    private final Map<Integer, Layer> layers;

    private Layer currentLayer;

    public DefaultMultiLayerEditor() {

        layers = new HashMap<Integer, Layer>();
    }

    @Override
    public Layer getLayer(int id) {

        Integer key = new Integer(id);
        if (!layers.containsKey(key)) {
            throw new RuntimeException("the id '" + id + "' referes to a non exiting layer!");
        }
        return layers.get(key);
    }

    @Override
    public void selectLayer(int id) {

        currentLayer = getLayer(id);
        eventbus.post(new LayerChangedEvent(currentLayer));
    }

    @Override
    public Layer getSelectedLayer() {

        if (currentLayer == null) {
            throw new RuntimeException("No layer was selected!");
        }
        return currentLayer;
    }

    @Override
    public Layer addLayer(int id, GraphView graphView) {

        Layer layer = new DefaultLayer(id, layers.size(), graphView);
        layers.put(new Integer(id), layer);
        return layer;
    }

    @Override
    public void removeLayer(int id) {

        Layer layer = getLayer(id);
        layers.remove(layer);
    }

    @Override
    public Layer[] getLayers() {

        return layers.values().toArray(new Layer[0]);
    }

}
