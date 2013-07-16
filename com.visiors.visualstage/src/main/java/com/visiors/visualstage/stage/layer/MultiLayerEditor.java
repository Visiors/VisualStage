package com.visiors.visualstage.stage.layer;

public interface MultiLayerEditor {

	public void selectLayer(int id);

	public Layer getSelectedLayer();

	public Layer addLayer(int id, VisualGraph graphViewr);

	public Layer getLayer(int id);

	public void removeLayer(int id);

	public Layer[] getLayers();
}
