package com.visiors.visualstage.graph.view.shape;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.transform.Transformer;
import com.visiors.visualstage.property.PropertyOwner;
import com.visiors.visualstage.renderer.RenderingContext;
import com.visiors.visualstage.stage.interaction.Interactable;

public interface Shape extends Interactable, PropertyOwner {

	enum Unit {
		PIXEL, PERCENT
	}

	public long getID();

	public String getName();

	public void setPresentationID(String presentationID);

	public String getPresentationID();

	void setStyleID(String styleID);

	public String getStyleID();

	public void setTransformer(Transformer transform);

	public Transformer getTransformer();

	public String getViewDescriptor(RenderingContext context, boolean standalone);

	public void setBounds(Rectangle bounds);

	public Rectangle getBounds();

	public Rectangle getExtendedBoundary();

	public boolean isHit(Point pt);

	public void setSelected(boolean selected);

	public boolean isSelected();

	public void setHighlighted(boolean highlighted);

	public boolean isHighlighted();

	void setPreferredWidth(int w);

	public int getPreferredWidth();

	void setPreferredHeight(int h);

	public int getPreferredHeight();

	public Unit getPreferredWidthUnit();

	public Unit getPreferredHeightUnit();

	public Insets getMargin();

	public void setMargin(Insets margin);

	public LayoutData getLayoutData();

	public void setLayoutData(LayoutData data);
}
