package com.visiors.visualstage.controller;

import com.visiors.visualstage.graph.mode.listener.GraphModelChangeListener;
import com.visiors.visualstage.stage.graph.GraphView;

public interface GraphController {

    // void loadGraphView(GraphView graphView, InputStream stream) throws IOException,
    // ParserConfigurationException, SAXException;

    // public GraphView createGraphView();

    // void saveGraphView(GraphView graphView, OutputStream stream) throws IOException;

    void refreshGraphModel(GraphView graphview);

    void refreshGraphView(GraphView graphView);

    void deleteGraphView(GraphView graphView);

    void addGraphModelChangeListener(GraphModelChangeListener listener);

    void removeGraphModelChangeListener(GraphModelChangeListener listener);

}
