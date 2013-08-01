package com.visiors.visualstage.graph.view.shape;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.interaction.Interactable;
import com.visiors.visualstage.property.PropertyOwner;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.transform.Transform;

public interface Shape extends Interactable, PropertyOwner, Duplicatable {

	enum Unit {
		PIXEL, PERCENT
	}

	public long getID();

	public String getName();

	public void setPresentationID(String presentationID);

	public String getPresentationID();

	void setStyleID(String styleID);

	public String getStyleID();

	public void setTransformer(Transform transform);

	public Transform getTransformer();

	public String getViewDescriptor(DrawingContext context, DrawingSubject subject);

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
