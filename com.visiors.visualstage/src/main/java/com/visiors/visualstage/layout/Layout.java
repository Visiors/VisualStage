package com.visiors.visualstage.layout;

import com.visiors.visualstage.property.PropertyList;

public interface Layout
{
    
    public String getLayoutName();
    public void setLayoutProperty(PropertyList properties);
    public PropertyList getLayoutProperty();
    public boolean setGraph(LayoutableGraph graph);
    public void layout();

}
