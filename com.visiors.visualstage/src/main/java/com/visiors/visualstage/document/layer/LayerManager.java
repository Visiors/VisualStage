package com.visiors.visualstage.document.layer;

import java.util.List;

public interface LayerManager {

	public Layer selectLayer(int id);

	public Layer getSelectedLayer();

	public Layer addLayer(int id);

	public Layer getLayer(int id);

	public void removeLayer(int id);

	public List<Layer> getLayers();

	public int getLayerCount();
}
