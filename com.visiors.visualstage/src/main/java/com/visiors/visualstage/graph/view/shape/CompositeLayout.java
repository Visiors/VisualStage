package com.visiors.visualstage.graph.view.shape;

import java.util.List;


public interface CompositeLayout {

	boolean layout(CompositeShape container, List<Shape> shapes, boolean adjustContainerSize);

}
