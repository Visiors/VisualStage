package com.visiors.visualstage.document.layer;

import java.awt.Color;

import com.visiors.visualstage.graph.view.graph.VisualGraph;

public interface Layer {

	public int getID();

	public boolean isVisible();

	public void setVisible(boolean visible);

	public Color getBackgroundColor();

	public void setBackgroundColor(Color bkc);

	public int getOrder();

	public void setOrder(int order);

	public VisualGraph getVisualGraph();
}
