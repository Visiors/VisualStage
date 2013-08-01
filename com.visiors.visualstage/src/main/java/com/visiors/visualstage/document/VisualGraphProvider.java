package com.visiors.visualstage.document;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.impl.DefaultVisualGraph;

public class VisualGraphProvider implements Provider<VisualGraph> {


	@Inject
	public VisualGraphProvider() {
	}

	@Override
	public VisualGraph get() {

		return new DefaultVisualGraph();
	}
}