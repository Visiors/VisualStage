package com.visiors.visualstage.graph.view.shape.impl;

import com.visiors.visualstage.graph.view.shape.CompositeShape;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;


public class RectangleShape extends BaseCompositeShape implements CompositeShape {

	protected RectangleShape() {

		super();
	}

	@Override
	public String getViewDescriptor(Resolution resolution, DrawingSubject subject) {

		return "";
	}

	@Override
	public Object deepCopy() {

		// TODO Auto-generated method stub
		return null;
	}

}
