package com.visiors.visualstage.graph.view.shape.impl;

import com.visiors.visualstage.graph.view.shape.CompositeShape;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;


public class RectangleShape extends BaseCompositeShape implements CompositeShape {

	protected RectangleShape() {

		super(-1);
	}

	@Override
	public String getViewDescriptor(DrawingContext context, DrawingSubject subject) {

		return "";
	}

}
