package com.visiors.visualstage.graph.view;

import java.awt.Image;
import java.awt.Rectangle;

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
import com.visiors.visualstage.renderer.cache.DefaultViewCache;
import com.visiors.visualstage.renderer.cache.GraphObjectImageProvider;
import com.visiors.visualstage.renderer.cache.ViewCache;
import com.visiors.visualstage.svg.SVGDocumentBuilder;

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
	protected Attribute attributes;
	protected PropertyList properties;
	protected boolean modified;
	protected final ViewCache offlineRenderer;
	@Inject
	protected SVGDocumentBuilder svgDocumentBuilder;
	protected ImageFactory snapshotGenerator;

	protected DefaultVisualGraphObject() {

		super();

		this.properties = new DefaultPropertyList();
		this.attributes = new DefaultAttribute();
		this.offlineRenderer = new DefaultViewCache(this);

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

		Image image = offlineRenderer.get(context, subject);
		if (image != null) {
			final Rectangle boundary = transform.transformToScreen(getBounds());
			final Rectangle extendedBoundary = transform.transformToScreen(getExtendedBoundary());
			boundary.grow((extendedBoundary.width - boundary.width) / 2,
					(extendedBoundary.height - boundary.height) / 2);
			//			awtCanvas.gfx.setColor(Color.blue);
			//			awtCanvas.gfx.drawRect(boundary.x, boundary.y, boundary.width, boundary.height);
			awtCanvas.gfx.drawImage(image, boundary.x, boundary.y, null);
		}
	}

	@Override
	public Image provideImage(DrawingContext context, DrawingSubject subject) {

		setModified(false);
		if (snapshotGenerator == null) {
			this.snapshotGenerator = new ImageFactory(svgDocumentBuilder);
		}
		return snapshotGenerator.createSnapshot(this, context, subject);
	}

	private Object VisualObjectSnapshotGenerator() {

		// TODO Auto-generated method stub
		return null;
	}

}
