package com.visiors.visualstage.graph.view.shape;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.property.PropertyOwner;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.transform.Transform;

public interface Shape extends Interactable, PropertyOwner, Duplicatable {

	enum Unit {
		PIXEL, PERCENT
	}

	public long getID();

	public String getName();

	public void setPresentation(String presentationId);

	public String getPresentation();

	void setStyle(String styleId);

	public String getStyle();

	public void setTransformer(Transform transform);

	public Transform getTransformer();

	public String getViewDescriptor(Resolution resolution, DrawingSubject subject);

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
