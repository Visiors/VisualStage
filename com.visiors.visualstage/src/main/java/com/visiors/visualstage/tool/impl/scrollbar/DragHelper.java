package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Point;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.transform.Transform;

public class DragHelper {

	public final ScrollBar scrollBar;
	public int initialValue;
	public int initialThumbExpansion;
	public double initialZoom;
	public int initialThumbPos;
	public Point mousePressedAt;
	public int initialGraphAtScreenLeft;

	public DragHelper(ScrollBar scrollBar) {

		this.scrollBar = scrollBar;
	}

	public void registerMousePressed(Point pt) {

		final GraphDocument graphDocument = scrollBar.getGraphDocument();
		final Transform xform = graphDocument.getTransformer();
		mousePressedAt = xform.transformToScreen(pt);
		initialValue = scrollBar.getValue();
		initialThumbPos = scrollBar.getThumbPos();
		initialThumbExpansion = scrollBar.getThumbExpansion();
		initialZoom = graphDocument.getTransformer().getScale();
		initialGraphAtScreenLeft = xform.transformToGraphX(0);
	}
}
