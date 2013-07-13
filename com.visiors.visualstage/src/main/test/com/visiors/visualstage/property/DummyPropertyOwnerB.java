package com.visiors.visualstage.property;

import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.property.impl.PropertyBinder;

public class DummyPropertyOwnerB implements PropertyOwner {

	private String valueB = "B";
	private final DummyPropertyOwnerC propertyOwnerC ;
	private final PropertyBinder propertyBinder;



	public DummyPropertyOwnerB(PropertyBinder propertyBinder) {
		this.propertyBinder = propertyBinder;
		this.propertyOwnerC = new DummyPropertyOwnerC(propertyBinder);
	}

	@Override
	public void setProperties(PropertyList properties) {

	}

	@Override
	public PropertyList getProperties() {

		PropertyList properties = new DefaultPropertyList("branchB");
		properties.add(new DefaultPropertyUnit("valueB", getValueB()));
		properties.add(propertyOwnerC.getProperties());

		return properties;
	}

	public String getValueB() {

		return valueB;
	}

	public void setValueB(String valueB) {

		this.valueB = valueB;

		propertyBinder.save("*:branchB:valueB");
	}

	public DummyPropertyOwnerC getPropertyOwnerC() {

		return propertyOwnerC;
	}
};