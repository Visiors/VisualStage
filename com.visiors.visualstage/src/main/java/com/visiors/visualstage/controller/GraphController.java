package com.visiors.visualstage.controller;

import com.visiors.visualstage.graph.view.graph.VisualGraph;

public interface GraphController {

	// void loadGraphView(GraphView visualGraph, InputStream stream) throws IOException,
	// ParserConfigurationException, SAXException;

	// public GraphView createGraphView();

	// void saveGraphView(GraphView visualGraph, OutputStream stream) throws IOException;

	void refreshGraphModel(VisualGraph visualGraph);

	void refreshVisualGraph(VisualGraph visualGraph);

	void deleteVisualGraph(VisualGraph visualGraph);

	void addGraphModelChangeListener(GraphModelChangeListener listener);

	void removeGraphModelChangeListener(GraphModelChangeListener listener);

}
