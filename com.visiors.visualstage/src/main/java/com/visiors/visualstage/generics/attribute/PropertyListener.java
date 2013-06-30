package com.visiors.visualstage.generics.attribute;

import java.util.List;

public interface PropertyListener {

	void propertyChanged(List<PropertyList> path, PropertyUnit property);

}
