package com.visiors.visualstage.property.impl;

import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.exception.AttributeException;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.ValueRange;
import com.visiors.visualstage.property.listener.PropertyListener;
import com.visiors.visualstage.util.DeepCopy;

public class DefaultPropertyUnit implements PropertyUnit {

	private String name;
	private Object value;
	private PropertyList parent;
	private int attributes;
	private final ValueRange range;
	private PropertyType type;

	public DefaultPropertyUnit() {

		this(null, null);
	}

	public DefaultPropertyUnit(String name, Object value) {

		this(name, value, PropertyType.OBJECT);
	}

	public DefaultPropertyUnit(String name, Object value, PropertyType type) {

		this(name, value, type, VISIBLE | EDITABLE | PERSISTENT, null);
	}

	public DefaultPropertyUnit(String name, Object value, PropertyType type, String range) {

		this(name, value, type, VISIBLE | EDITABLE | PERSISTENT, range);
	}

	public DefaultPropertyUnit(String name, Object value, PropertyType type, int attributes,
			String range) {

		this.name = name;
		this.value = value;
		this.attributes = attributes;
		this.type = type != PropertyType.OBJECT ? type : findType(value);

		this.range = new DefaultValueRange(type, range);
	}

	private PropertyType findType(Object value) {

		if (value instanceof Long) {
			return PropertyType.LONG;
		}
		if (value instanceof Integer) {
			return PropertyType.INTEGER;
		}
		if (value instanceof Double) {
			return PropertyType.DOUBLE;
		}
		if (value instanceof Float) {
			return PropertyType.FLOAT;
		}
		if (value instanceof Boolean) {
			return PropertyType.BOOLEAN;
		}
		if (value instanceof String) {
			return PropertyType.STRING;
		}

		return PropertyType.OBJECT;
	}

	public DefaultPropertyUnit(PropertyUnit a) {

		this(a.getName(), a.getValue(), a.getType(), a.getAttribute(), a.getValueRangeExpression());
	}

	@Override
	public String getName() {

		return this.name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
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
	public PropertyList getParent() {

		return this.parent;
	}

	@Override
	public void setParent(PropertyList parent) {

		this.parent = parent;
	}

	@Override
	public int getAttribute() {

		return attributes;
	}

	@Override
	public void setAttribute(int a) {

		this.attributes = a;
	}

	@Override
	public PropertyUnit deepCopy() {

		DefaultPropertyUnit punit = new DefaultPropertyUnit(name, DeepCopy.copy(value), type,
				attributes, range.getExpression());

		// for (PropertyListener l : propertyListener) {
		// punit.addPropertyListener(l);
		// }

		return punit;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(getName()).append("=").append(getValue());
		return sb.toString();
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

}
