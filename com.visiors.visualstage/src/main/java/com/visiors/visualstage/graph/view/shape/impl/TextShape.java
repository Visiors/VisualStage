package com.visiors.visualstage.graph.view.shape.impl;

import com.visiors.visualstage.graph.view.shape.Shape;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;

public class TextShape extends BaseShape implements Shape {

	protected TextShape() {

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
