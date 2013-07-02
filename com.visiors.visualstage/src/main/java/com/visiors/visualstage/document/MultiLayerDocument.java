package com.visiors.visualstage.document;

import com.visiors.visualstage.stage.layer.Layer;

public interface MultiLayerDocument {

    public Layer getCurrentLayer();

    public void selectLayer(int id);

    public Layer createNewLayer(int id);

    public void removeLayer(int id);

    public Layer getLayer(int id);

    public Layer[] getLayers();
}
