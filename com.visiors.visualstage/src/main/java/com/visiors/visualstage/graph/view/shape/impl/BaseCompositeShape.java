package com.visiors.visualstage.graph.view.shape.impl;

import java.util.List;

import com.visiors.visualstage.graph.view.shape.CompositeLayout;
import com.visiors.visualstage.graph.view.shape.CompositeShape;
import com.visiors.visualstage.graph.view.shape.Shape;


public abstract class BaseCompositeShape extends BaseShape implements CompositeShape {

	protected List<Shape> children;
	protected CompositeLayout compositeLayout;

	protected BaseCompositeShape() {

		super(-1);
	}

	protected BaseCompositeShape( long id) {
		super( id);
	}


	@Override
	public void setChildren(List<Shape> children) {

		this.children = children;		
	}

	@Override
	public List<Shape> getChildren() {

		return this.children;
	}

	@Override
	public void addChild(Shape shape) {

		children.add(shape);
	}

	@Override
	public void removeChild(long shapeId) {

		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getID() == shapeId) {
				children.remove(i);
				break;
			}
		}
	}

	@Override
	public void setLayout(CompositeLayout layout) {

		this.compositeLayout = layout;
	}

	@Override
	public CompositeLayout getLayout() {

		return compositeLayout;
	}

}
