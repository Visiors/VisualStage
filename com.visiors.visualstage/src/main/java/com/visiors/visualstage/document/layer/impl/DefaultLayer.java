package com.visiors.visualstage.document.layer.impl;

import java.awt.Color;

import com.google.common.base.Objects;
import com.visiors.visualstage.document.layer.Layer;
import com.visiors.visualstage.graph.view.graph.VisualGraph;

public class DefaultLayer implements Layer {

	private final int id;
	private boolean visible = true;
	private Color bkColor;
	private int order;
	protected VisualGraph visualGraph;

	public DefaultLayer(int id, int order, VisualGraph visualGraph) {

		this.id = id;
		this.order = order;
		this.visualGraph = visualGraph;
	}

	@Override
	public int getID() {

		return id;
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

	@Override
	public boolean equals(Object obj) {

		return Objects.equal(this, obj);
	}

	@Override
	public int hashCode() {

		return Objects.hashCode(id);
	}

	@Override
	public String toString() {

		return "id: " + id + ", order: "+ order + "("+(isVisible() ? " visible": " invisible"  )+ ")";
	}
}
