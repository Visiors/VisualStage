package com.visiors.visualstage.graph.view.graph.impl;

import java.util.List;

import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewAdapter;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.VisualNode;

/**
 * This class passes events coming from a subgraph to its parent graph
 */
class SubgraphEventMediator {

	List<GraphViewListener> parentListener;
	private final VisualGraph subgraph;

	SubgraphEventMediator(VisualGraph subgraph) {

		this.subgraph = subgraph;
	}

	void setParentView(DefaultVisualGraph parentView) {

		if (parentView == null) {
			subgraph.removeGraphViewListener(subgraphEventListener);
		} else {
			subgraph.addGraphViewListener(subgraphEventListener);
		}

		parentListener = parentView.graphViewListener;
	}

	GraphViewListener subgraphEventListener = new GraphViewAdapter() {

		@Override
		public void graphManipulated(VisualGraph graph) {

			for (GraphViewListener l : parentListener) {
				l.graphManipulated(graph);
			}
		}

		@Override
		public void viewInvalid(VisualGraph graph) {

			for (GraphViewListener l : parentListener) {
				l.viewInvalid(graph);
			}
		}

		@Override
		public void nodeSelectionChanged(VisualNode node) {

			for (GraphViewListener l : parentListener) {
				l.nodeSelectionChanged(node);
			}
		}

		@Override
		public void edgeSelectionChanged(VisualEdge edge) {

			for (GraphViewListener l : parentListener) {
				l.edgeSelectionChanged(edge);
			}
		}
	};
}
