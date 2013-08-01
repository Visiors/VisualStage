package com.visiors.visualstage.graph.view;

import java.awt.Image;

import com.visiors.visualstage.attribute.Attribute;
import com.visiors.visualstage.attribute.DefaultAttribute;
import com.visiors.visualstage.graph.CustomData;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.shape.impl.BaseCompositeShape;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.VisualObjectSnapshotGenerator;
import com.visiors.visualstage.renderer.cache.DefaultViewCache;
import com.visiors.visualstage.renderer.cache.GraphObjectImageProvider;
import com.visiors.visualstage.renderer.cache.ViewCache;
import com.visiors.visualstage.validation.Validator;

/**
 * This class implements features that are common in all graph objects (edges,
 * nodes, sub-graphs).
 * 
 */
public abstract class DefaultVisualGraphObject extends BaseCompositeShape implements VisualGraphObject,
GraphObjectImageProvider {

	protected static final int NONE = -1;
	protected long id;
	protected VisualGraph parent;
	protected CustomData customData;
	protected Validator validator;
	protected Attribute attributes;
	protected PropertyList properties;
	protected boolean modified;
	protected final ViewCache viewCache;

	// @Inject
	// protected UndoRedoHandler undoRedoHandler;

	protected DefaultVisualGraphObject() {

		super();

		this.properties = new DefaultPropertyList();
		this.attributes = new DefaultAttribute();
		this.viewCache = new DefaultViewCache(this);
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
	public void setModified(boolean modified) {

		this.modified = modified;
	}

	@Override
	public boolean isModified() {

		return modified;
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

	@Override
	public void draw(Canvas canvas, DrawingContext context, DrawingSubject subject) {

		Image image = viewCache.get(context, subject);
		if (image != null) {
			canvas.draw(0, 0, image);
		}
	}

	@Override
	public Image provide(DrawingContext context, DrawingSubject subject) {

		return VisualObjectSnapshotGenerator.createSnapshot(this, context, subject);
	}

}
