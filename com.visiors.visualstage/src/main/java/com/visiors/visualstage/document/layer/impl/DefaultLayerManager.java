package com.visiors.visualstage.document.layer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.visiors.visualstage.document.layer.Layer;
import com.visiors.visualstage.document.layer.LayerManager;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.handler.impl.LayerChangedEvent;

public class DefaultLayerManager implements LayerManager {

	@Inject
	protected Injector injector;

	private final Map<Integer, Layer> layers = new HashMap<Integer, Layer>();;

	private Layer currentLayer;

	@Inject
	private EventBus eventbus;


	public DefaultLayerManager() {

	}

	@Override
	public Layer getLayer(int id) {

		final Integer key = new Integer(id);
		if (!layers.containsKey(key)) {
			throw new RuntimeException("the id '" + id + "' referes to a non exiting layer!");
		}
		return layers.get(key);
	}

	@Override
	public Layer selectLayer(int id) {

		currentLayer = getLayer(id);
		currentLayer.getVisualGraph();
		eventbus.post(new LayerChangedEvent(currentLayer));
		return currentLayer;
	}

	@Override
	public Layer getSelectedLayer() {

		// if (currentLayer == null) {
		// throw new RuntimeException("No layer was selected!");
		// }
		return currentLayer;
	}

	@Override
	public Layer addLayer(int id) {
		VisualGraph visualGraph = injector.getInstance(VisualGraph.class);
		injector.injectMembers(visualGraph);

		final Layer layer = new DefaultLayer(id, layers.size(), visualGraph);
		layers.put(new Integer(id), layer);
		return layer;
	}

	@Override
	public void removeLayer(int id) {

		layers.remove(id);
	}

	@Override
	public List<Layer> getLayers() {

		return sort(layers.values());
	}

	@Override
	public int getLayerCount() {

		return layers.size();
	}

	private List<Layer> sort(Collection<Layer> collection) {

		final List<Layer> list = new ArrayList<Layer>(collection);
		Collections.sort(list, new Comparator<Layer>() {

			@Override
			public int compare(Layer l1, Layer l2) {

				if (l1.getOrder() < l2.getOrder()) {
					return -1;
				}
				if (l1.getOrder() > l2.getOrder()) {
					return 1;
				}
				return 0;
			};
		});
		return list;
	}
}
