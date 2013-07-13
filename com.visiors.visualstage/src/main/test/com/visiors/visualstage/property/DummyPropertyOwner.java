package com.visiors.visualstage.property;

import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.property.impl.PropertyBinder;

public class DummyPropertyOwner implements PropertyOwner {

	private PropertyList properties;

	private final PropertyBinder propertyBinder;
	private String valueA = "A";
	private int valueB = 1;
	private boolean valueC = false;

	public DummyPropertyOwner() {

		PropertyList properties = new DefaultPropertyList("root");
		properties.add(new DefaultPropertyUnit("valueA", getValueA()));
		properties.add(new DefaultPropertyUnit("valueB", getValueB()));
		propertyBinder = new PropertyBinder(this);
		setProperties(properties);
	}

	@Override
	public void setProperties(PropertyList properties) {

		this.properties = properties;
		propertyBinder.bind(properties);
	}

	public void loadAll() {

		propertyBinder.loadAll();
	}

	public void saveAll() {

		propertyBinder.saveAll();
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

	public int getValueB() {

		return valueB;
	}

	public void setValueB(int valueB) {

		this.valueB = valueB;
		propertyBinder.save("root:valueB");
	}

	public boolean getValueC() {

		return valueC;
	}

	public void setValueC(boolean valueC) {

		this.valueC = valueC;
		propertyBinder.save("root:valueC");
	}
}
