package com.visiors.visualstage.property.listener;

import java.util.List;

import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;

public interface PropertyListener {

	void propertyChanged(List<PropertyList> path, PropertyUnit property);

}
