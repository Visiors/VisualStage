package com.visiors.visualstage.graph.view.shape;

import java.util.List;

public interface CompositeShape extends Shape {

	public List<Shape> getChildren();

	public void setChildren(List<Shape> children);

	public void addChild(Shape shape);

	public void removeChild(long shapeId);

	public void setLayout(CompositeLayout layout);

	public CompositeLayout getLayout();


}
