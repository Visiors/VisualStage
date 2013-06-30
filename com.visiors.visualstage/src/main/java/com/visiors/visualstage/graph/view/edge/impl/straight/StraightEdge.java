package com.visiors.visualstage.view.edge.impl.straight;

import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.util.PropertyUtil;
import com.visiors.visualstage.view.edge.EdgeView;
import com.visiors.visualstage.view.edge.impl.DefaultEdgeView;

public class StraightEdge extends DefaultEdgeView {

    public StraightEdge(String name) {

        super(name);
    }

    protected StraightEdge(String name, long id) {

        super(name, id);
    }

    protected StraightEdge(EdgeView edge, long id) {

        super(edge, id);
    }

    @Override
    protected void init() {

        super.init();
        properties = PropertyUtil.setProperty(properties, PropertyConstants.EDGE_PROPERTY_TYPE,
                PropertyConstants.EDGE_PROPERTY_TYPE_STRAIGHT);
        PropertyUtil.makeEditable(properties, PropertyConstants.EDGE_PROPERTY_TYPE, false);
    }

    @Override
    public EdgeView deepCopy(long id) {

        return new StraightEdge(this, id);
    }

}
