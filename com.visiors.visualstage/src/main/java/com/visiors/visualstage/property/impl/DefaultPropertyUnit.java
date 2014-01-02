package com.visiors.visualstage.property.impl;

import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.exception.AttributeException;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.ValueRange;
import com.visiors.visualstage.property.listener.PropertyListener;
import com.visiors.visualstage.util.DeepCopy;

public class DefaultPropertyUnit extends BaseProperty implements PropertyUnit {


	private Object value;

	private final ValueRange range;
	private PropertyType type;


	public DefaultPropertyUnit() {

		this(null, null);
	}

	public DefaultPropertyUnit(String name, Object value) {

		this(name, value, PropertyType.typeOf(value));
	}

	public DefaultPropertyUnit(String name, Object value, PropertyType type) {

		this(name, value, type, null);
	}

	public DefaultPropertyUnit(String name, Object value, PropertyType type, String range) {

		this.range = new DefaultValueRange(type, range);
		setName(name);
		setValue(value);
		setType(type);

	}


	@Override
	public Object getValue() {

		return this.value;
	}

	@Override
	public void setValue(Object value) {

		if (this.value == null || !this.value.equals(value)) {
			if (!range.isValueValid(value)) {
				throw new AttributeException("Value not in expected range! Valid value range: "
						+ range.getExpression());
			}
			this.value = value;
			firePropertyChanged();
		}
	}

	@Override
	public PropertyType getType() {

		return type;
	}

	@Override
	public void setType(PropertyType type) {

		this.type = type;
	}


	@Override
	public PropertyUnit deepCopy() {

		DefaultPropertyUnit punit = new DefaultPropertyUnit(name, DeepCopy.copy(value), type,
				range.getExpression());
		punit.setReadOnly(isReadOnly());
		punit.setVisible(isVisible());

		return punit;
	}


	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(getName()).append("=").append(getValue());
		return sb.toString();
	}

	@Override
	public void setValueRangeExpression(String expression) {

		this.range.setExpression(expression);
	}

	@Override
	public String getValueRangeExpression() {

		return range.getExpression();
	}

	@Override
	public boolean hasDiscreteValueRange() {

		return range.discreteDataSet();
	}

	@Override
	public boolean isValid(Object value) {

		return range.isValueValid(value);
	}

	@Override
	public Object[] getDiscreteValueRange() {

		return range.getDescreteDataSet();
	}

	// //////////////////////////////////////////////////////////////////////////
	// Notifications - sending notification to listener

	protected List<PropertyListener> propertyListener = new ArrayList<PropertyListener>();

	protected void firePropertyChanged() {

		List<PropertyList> path = new ArrayList<PropertyList>();

		for (PropertyListener l : propertyListener) {
			l.propertyChanged(path, this);
		}
	}

	@Override
	public void addPropertyListener(PropertyListener listener) {

		if (!propertyListener.contains(listener)) {
			this.propertyListener.add(listener);
		}
	}

	@Override
	public void removePropertyListener(PropertyListener listener) {

		if (!propertyListener.contains(listener)) {
			this.propertyListener.remove(listener);
		}
	}
}
