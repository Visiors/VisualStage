package com.visiors.visualstage.model.listener;

import java.awt.Rectangle;

import com.visiors.visualstage.model.EdgeModel;
import com.visiors.visualstage.model.NodeModel;

public interface GraphModelListener {

    public void nodeAdded(NodeModel node);

    public void nodeRemoved(NodeModel node);

    public void nodeBoundaryChanged(NodeModel node, Rectangle oldBoundary);

    public void edgeAdded(EdgeModel edge);

    public void edgeRemoved(EdgeModel edge);

    public void edgeReconnected(EdgeModel edge, NodeModel node, boolean sourceNode);
}
