package com.visiors.visualstage.graph.view.shape.impl;

import com.visiors.visualstage.graph.view.shape.CompositeShape;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;


public class RectangleShape extends BaseCompositeShape implements CompositeShape {

	protected RectangleShape() {

		super();
	}

	@Override
	public String getViewDescriptor(DrawingContext context, DrawingSubject subject) {

		return "";
	}

	@Override
	public Object deepCopy() {

		// TODO Auto-generated method stub
		return null;
	}

}
