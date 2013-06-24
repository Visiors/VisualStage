package com.visiors.visualstage.graph.listener;

import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.NodeModel;

public interface EdgeModelListener {

	public void sourceNodeChanged(EdgeModel edge, NodeModel oldSourceNode);

	public void targetNodeChanged(EdgeModel edge, NodeModel oldtargetNode);
}
