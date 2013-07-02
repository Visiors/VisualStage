package com.visiors.visualstage.graph.view;

import java.awt.Point;

import com.visiors.visualstage.attribute.Attribute;
import com.visiors.visualstage.graph.CustomData;
import com.visiors.visualstage.graph.UIDGen;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.transform.Transformer;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.validation.Validator;

/**
 * This class implements features that are common in all graph objects (edges,
 * nodes, sub-graphs).
 * 
 */
public abstract class DefaultGraphObjectView implements GraphObjectView {

	protected static final int NONE = -1;
	protected long id;
	protected Attribute attributes;
	protected GraphView parent;
	protected boolean selected;
	protected boolean highlighted;
	protected CustomData customData;
	protected Validator validator;
	protected PropertyList properties;

	// @Inject
	// protected UndoRedoHandler undoRedoHandler;

	protected Transformer transformer;
	protected final String name;

	protected DefaultGraphObjectView(String name) {

		this(name, -1);
	}

	protected DefaultGraphObjectView(String name, long id) {

		if (id == -1) {
			id = UIDGen.getInstance().getNextId();
		} else {
			UIDGen.getInstance().considerExternalId(id);
		}
		this.id = id;
		this.name = name;
		this.properties = new DefaultPropertyList();
	}

	@Override
	public long getID() {

		return id;
	}

	@Override
	public String getName() {

		return name;
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
	public Transformer getTransformer() {

		return transformer;
	}

	@Override
	public void setTransformer(Transformer transform) {

		this.transformer = transform;
	}

	@Override
	public GraphView getParentGraph() {

		return parent;
	}

	@Override
	public void setParentGraph(GraphView graph) {

		parent = graph;
	}

	@Override
	public void setHighlighted(boolean highlighted) {

		this.highlighted = highlighted;
	}

	@Override
	public boolean isHighlighted() {

		return highlighted;
	}

	@Override
	public Attribute getAttributes() {

		return attributes;
	}

	@Override
	public void SetAttributes(Attribute attributes) {

		this.attributes = attributes;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		return false;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		return false;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public void cancelInteraction() {

	}

	@Override
	public void terminateInteraction() {

	}

	public Validator getValidator() {

		return validator;
	}

	public void setValidator(Validator validator) {

		this.validator = validator;
	}
}
