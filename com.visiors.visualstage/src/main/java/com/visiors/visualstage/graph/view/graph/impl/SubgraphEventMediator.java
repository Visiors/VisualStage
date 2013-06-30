package com.visiors.visualstage.graph.view.graph.impl;

import java.util.List;

import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.listener.GraphViewAdapter;
import com.visiors.visualstage.graph.view.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.NodeView;

/**
 * This class passes events coming from a subgraph to its parent graph
 */
class SubgraphEventMediator {

	List<GraphViewListener> parentListener;
	private final GraphView subgraph;

	SubgraphEventMediator(GraphView subgraph) {

		this.subgraph = subgraph;
	}

	void setParentView(DefaultGraphView parentView) {

		if (parentView == null) {
			subgraph.removeGraphViewListener(subgraphEventListener);
		} else {
			subgraph.addGraphViewListener(subgraphEventListener);
		}

		parentListener = parentView.graphViewListener;
	}

	GraphViewListener subgraphEventListener = new GraphViewAdapter() {

		@Override
		public void graphManipulated(GraphView graph) {

			for (GraphViewListener l : parentListener) {
				l.graphManipulated(graph);
			}
		}

		@Override
		public void viewChanged(GraphView graph) {

			for (GraphViewListener l : parentListener) {
				l.viewChanged(graph);
			}
		}

		@Override
		public void nodeSelectionChanged(NodeView node) {

			for (GraphViewListener l : parentListener) {
				l.nodeSelectionChanged(node);
			}
		}

		@Override
		public void edgeSelectionChanged(EdgeView edge) {

			for (GraphViewListener l : parentListener) {
				l.edgeSelectionChanged(edge);
			}
		}
	};
}
