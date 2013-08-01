package com.visiors.visualstage.graph.view.shape.impl;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.graph.UIDGen;
import com.visiors.visualstage.graph.view.shape.LayoutData;
import com.visiors.visualstage.graph.view.shape.Shape;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.PropertyUtil;

public abstract class BaseShape implements Shape {

	protected static final int NONE = -1;
	protected long id;
	protected String name;
	protected boolean selected;
	protected boolean highlighted;
	protected Transform transformer;
	protected Rectangle boundary;
	protected Insets margin;
	protected final Unit widthUnit = Unit.PERCENT;
	protected final Unit heightUnit = Unit.PERCENT;
	protected int preferredWidth;
	protected int preferredHeight;
	protected String styleID;
	protected String presentationID;
	protected PropertyList properties;
	private LayoutData layoutData;

	protected BaseShape() {

		this.id = UIDGen.getInstance().getNextId();
		this.boundary = new Rectangle();
	}

	@Override
	public long getID() {

		return id;
	}

	@Override
	public String getName() {

		return (String) PropertyUtil.getProperty(properties, "name");
	}

	@Override
	public void setPresentationID(String presentationID) {

		this.presentationID = presentationID;

	}

	@Override
	public String getPresentationID() {

		return presentationID;
	}

	@Override
	public void setStyleID(String styleID) {

		this.styleID = styleID;
	}

	@Override
	public String getStyleID() {

		return styleID;
	}

	@Override
	public Transform getTransformer() {

		return transformer;
	}

	@Override
	public void setTransformer(Transform transform) {

		this.transformer = transform;
	}

	@Override
	public void setSelected(boolean selected) {

		this.selected = selected;
	}

	@Override
	public boolean isSelected() {

		return selected;
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
	public int getPreferredWidth() {

		return 0;
	}

	@Override
	public int getPreferredHeight() {

		return 0;
	}

	@Override
	public void setPreferredWidth(int w) {

		preferredWidth = w;
	}

	@Override
	public void setPreferredHeight(int h) {

		preferredHeight = h;
	}

	@Override
	public Unit getPreferredWidthUnit() {

		return widthUnit;
	}

	@Override
	public Unit getPreferredHeightUnit() {

		return heightUnit;
	}



	@Override
	public LayoutData getLayoutData() {

		return layoutData;
	}

	@Override
	public void setLayoutData(LayoutData layoutData) {

		this.layoutData = layoutData;
	}

	@Override
	public Insets getMargin() {

		return margin;
	}

	@Override
	public void setMargin(Insets margin) {

		this.margin = margin;
	}

	@Override
	public Rectangle getBounds() {

		return boundary;
	}

	@Override
	public void setBounds(Rectangle bounds) {

		this.boundary = bounds;
	}

	@Override
	public Rectangle getExtendedBoundary() {

		return new Rectangle(boundary.x - margin.left, boundary.y - margin.top, boundary.width - margin.left
				- margin.right, boundary.height - margin.top - margin.bottom);
	}

	@Override
	public PropertyList getProperties() {

		return properties;
	}

	@Override
	public void setProperties(PropertyList properties) {

		this.properties = properties;
	}

	@Override
	public boolean isHit(Point pt) {

		return getExtendedBoundary().contains(pt);
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
	public boolean isInteracting() {

		return false;
	}

	@Override
	public int getPreferredCursor() {

		return 0;
	}

	@Override
	public void cancelInteraction() {

	}

	@Override
	public void terminateInteraction() {

	}
}
