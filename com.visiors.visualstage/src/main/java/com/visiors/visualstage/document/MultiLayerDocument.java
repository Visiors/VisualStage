package com.visiors.visualstage.document;

import java.util.List;

import com.visiors.visualstage.document.layer.Layer;

public interface MultiLayerDocument {

	public Layer getCurrentLayer();

	public void setActiveLayer(int id);

	public Layer createDrawingLayer(int id);

	public void removeLayer(int id);

	public Layer getLayer(int id);

	public List<Layer> getLayers();

	public int getLayerCount();
}
