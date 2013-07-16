package com.visiors.visualstage.graph.view;

import com.visiors.visualstage.attribute.Attribute;
import com.visiors.visualstage.attribute.DefaultAttribute;
import com.visiors.visualstage.graph.CustomData;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.shape.impl.BaseCompositeShape;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.validation.Validator;

/**
 * This class implements features that are common in all graph objects (edges,
 * nodes, sub-graphs).
 * 
 */
public abstract class DefaultVisualGraphObject extends BaseCompositeShape implements VisualGraphObject {

	protected static final int NONE = -1;
	protected long id;
	protected VisualGraph parent;
	protected CustomData customData;
	protected Validator validator;
	protected Attribute attributes;
	protected PropertyList properties;

	// @Inject
	// protected UndoRedoHandler undoRedoHandler;

	protected DefaultVisualGraphObject(String name) {

		this(name, -1);
	}

	protected DefaultVisualGraphObject(String name, long id) {

		super(name, id);
		this.properties = new DefaultPropertyList();
		this.attributes = new DefaultAttribute();
	}

	@Override
	public void setCustomData(CustomData data) {

		customData = data;
	}

	@Override
	public CustomData getCustomData() {

		return customData;
	}

	@Override
	public VisualGraph getParentGraph() {

		return parent;
	}

	@Override
	public void setParentGraph(VisualGraph graph) {

		parent = graph;
	}

	@Override
	public Attribute getAttributes() {

		return attributes;
	}

	@Override
	public void SetAttributes(Attribute attributes) {

		this.attributes = attributes;
	}

	public Validator getValidator() {

		return validator;
	}

	public void setValidator(Validator validator) {

		this.validator = validator;
	}

}
