package com.visiors.visualstage.document;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.DefaultVisualEdge;

public class VisualEdgeProvider implements Provider<VisualEdge> {


	@Inject
	public VisualEdgeProvider() {
	}

	@Override
	public VisualEdge get() {

		return new DefaultVisualEdge();
	}
}