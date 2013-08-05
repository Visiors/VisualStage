package com.visiors.visualstage.controller;

import com.visiors.visualstage.graph.view.graph.VisualGraph;

public interface GraphController {


	// public graphView createGraphView();

	void refreshGraphModel(VisualGraph visualGraph);

	void refreshVisualGraph(VisualGraph visualGraph);

	void deleteVisualGraph(VisualGraph visualGraph);

	void addGraphModelChangeListener(GraphModelChangeListener listener);

	void removeGraphModelChangeListener(GraphModelChangeListener listener);

}
