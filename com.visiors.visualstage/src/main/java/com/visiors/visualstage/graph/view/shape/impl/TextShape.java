package com.visiors.visualstage.graph.view.shape.impl;

import com.visiors.visualstage.graph.view.shape.Shape;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;

public class TextShape extends BaseShape implements Shape {

	protected TextShape() {

		super(-1);
	}

	@Override
	public String getViewDescriptor(DrawingContext context, DrawingSubject subject) {

		return "";
	}

}
