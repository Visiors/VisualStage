package com.visiors.visualstage.document;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.graph.view.node.impl.DefaultVisualNode;

public class VisualNodeProvider implements Provider<VisualNode> {

	private String type;

	@Inject
	public VisualNodeProvider() {

	}

	public void setNodeType(String type) {

		this.type = type;
	}

	@Override
	public VisualNode get() {

		System.err.println("Type: " + type);
		return new DefaultVisualNode();
	}
}