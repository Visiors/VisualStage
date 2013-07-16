package com.visiors.visualstage.controller;

import com.visiors.visualstage.graph.view.graph.VisualGraph;

public interface GraphController {

	// void loadGraphView(GraphView graphView, InputStream stream) throws IOException,
	// ParserConfigurationException, SAXException;

	// public GraphView createGraphView();

	// void saveGraphView(GraphView graphView, OutputStream stream) throws IOException;

	void refreshGraphModel(VisualGraph visualGraph);

	void refreshVisualGraph(VisualGraph graphView);

	void deleteVisualGraph(VisualGraph graphView);

	void addGraphModelChangeListener(GraphModelChangeListener listener);

	void removeGraphModelChangeListener(GraphModelChangeListener listener);

}
