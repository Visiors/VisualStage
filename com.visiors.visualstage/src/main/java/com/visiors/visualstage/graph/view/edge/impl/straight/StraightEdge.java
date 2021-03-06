package com.visiors.visualstage.graph.view.edge.impl.straight;

import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.DefaultVisualEdge;
import com.visiors.visualstage.util.PropertyUtil;

public class StraightEdge extends DefaultVisualEdge {

	public StraightEdge() {

		super();
	}



	protected StraightEdge(VisualEdge edge) {

		super(edge);
	}

	@Override
	protected void initProperties() {

		super.initProperties();
		properties = PropertyUtil.setProperty(properties, PropertyConstants.EDGE_PROPERTY_TYPE,
				PropertyConstants.EDGE_PROPERTY_TYPE_STRAIGHT);
	}

	@Override
	public VisualEdge deepCopy() {

		return new StraightEdge(this);
	}

}
