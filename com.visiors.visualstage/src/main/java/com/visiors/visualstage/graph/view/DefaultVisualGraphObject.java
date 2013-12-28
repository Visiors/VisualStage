package com.visiors.visualstage.graph.view;

import com.google.inject.Inject;
import com.visiors.visualstage.attribute.Attribute;
import com.visiors.visualstage.attribute.DefaultAttribute;
import com.visiors.visualstage.graph.CustomData;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.shape.impl.BaseCompositeShape;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.ImageFactory;
import com.visiors.visualstage.renderer.cache.DefaultShapeOfflineRenderer;
import com.visiors.visualstage.renderer.cache.DefaultShapeRenderer;
import com.visiors.visualstage.renderer.cache.ShapeOffScreenRenderer;
import com.visiors.visualstage.svg.SVGDocumentBuilder;

/**
 * This class implements features that are common in all graph objects (edges,
 * nodes, sub-graphs).
 * 
 */
public abstract class DefaultVisualGraphObject extends BaseCompositeShape implements VisualGraphObject, Cacheable {

	protected static final int NONE = -1;
	protected long id;
	protected VisualGraph parent;
	protected CustomData customData;
	protected Attribute attributes;
	protected PropertyList properties;
	protected final ShapeOffScreenRenderer offScreenRenderer;
	@Inject
	protected SVGDocumentBuilder svgDocumentBuilder;
	protected ImageFactory snapshotGenerator;

	protected DefaultVisualGraphObject() {

		super();

		this.properties = new DefaultPropertyList();
		this.attributes = new DefaultAttribute();
		this.offScreenRenderer = new DefaultShapeOfflineRenderer(new DefaultShapeRenderer(this));

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
	public void invalidate() {

		offScreenRenderer.invalidate();
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

	// public Validator getValidator() {
	//
	// return validator;
	// }
	//
	// public void setValidator(Validator validator) {
	//
	// this.validator = validator;
	// }

	@Override
	public void draw(AWTCanvas awtCanvas, DrawingContext context, DrawingSubject subject) {

		if(subject != DrawingSubject.SELECTION_INDICATORS || isSelected()) {
			offScreenRenderer.render(awtCanvas, context, subject);
		}
	}
}
