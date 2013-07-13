package com.visiors.visualstage.property;

import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.property.impl.PropertyBinder;

public class DummyPropertyOwnerC implements PropertyOwner {

	private String valueC = "C";
	private final PropertyBinder propertyBinder;

	public DummyPropertyOwnerC(PropertyBinder propertyBinder) {
		this.propertyBinder = propertyBinder;
	}

	@Override
	public void setProperties(PropertyList properties) {
	}

	@Override
	public PropertyList getProperties() {

		PropertyList properties = new DefaultPropertyList("branchC");
		properties.add(new DefaultPropertyUnit("valueC", getValueC()));
		return properties;
	}

	public String getValueC() {

		return valueC;
	}

	public void setValueC(String valueC) {

		this.valueC = valueC;
		propertyBinder.save("*:branchC:valueC");
	}

};