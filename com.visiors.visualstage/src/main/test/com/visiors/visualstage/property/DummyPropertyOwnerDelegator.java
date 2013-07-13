package com.visiors.visualstage.property;

import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.property.impl.PropertyBinder;

public class DummyPropertyOwnerDelegator implements PropertyOwner {

	private PropertyList properties;

	private final PropertyBinder propertyBinder;
	private String valueA = "A";
	private final DummyPropertyOwnerB propertyOwnerB;

	public DummyPropertyOwnerDelegator() {

		propertyBinder = new PropertyBinder(this);
		propertyOwnerB = new DummyPropertyOwnerB(propertyBinder);

		PropertyList properties = new DefaultPropertyList("root");
		properties.add(new DefaultPropertyUnit("valueA", getValueA()));

		properties.add(propertyOwnerB.getProperties());

		// delegate synchronisation work to other class
		propertyBinder.setHandler("root:branchB", propertyOwnerB);
		propertyBinder.setHandler("root:branchB:branchC", propertyOwnerB.getPropertyOwnerC());


		setProperties(properties);
	}

	@Override
	public void setProperties(PropertyList properties) {

		this.properties = properties;
		propertyBinder.bind(properties);
		propertyBinder.loadAll();
	}


	public DummyPropertyOwnerB getPropertyOwnerB() {

		return propertyOwnerB;
	}

	@Override
	public PropertyList getProperties() {

		return properties;
	}

	public String getValueA() {

		return valueA;
	}

	public void setValueA(String valueA) {

		this.valueA = valueA;
		propertyBinder.save("root:valueA");
	}


}
