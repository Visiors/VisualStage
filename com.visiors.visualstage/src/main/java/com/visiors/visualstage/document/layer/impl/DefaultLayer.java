package com.visiors.visualstage.document.layer.impl;

import java.awt.Color;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.visiors.visualstage.document.layer.Layer;
import com.visiors.visualstage.graph.view.graph.VisualGraph;

public class DefaultLayer implements Layer {

	private int id;
	private boolean visible = true;
	private Color bkColor;
	private int order;
	protected final VisualGraph visualGraph;

	@Inject
	protected Provider<VisualGraph> visualGraphProvider;


	public DefaultLayer(int id, int order) {

		this.id = id;
		this.order = order;
		this.visualGraph =  visualGraphProvider.get();
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
	public VisualGraph getVisualGraph() {

		return visualGraph;
	}
}
