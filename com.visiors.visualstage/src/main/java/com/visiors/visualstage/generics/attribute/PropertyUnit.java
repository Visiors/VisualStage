package com.visiors.visualstage.generics.attribute;

public interface PropertyUnit extends Property {

	@Override
	public String getName();

	@Override
	public void setName(String name);

	public Object getValue();

	public void setValue(Object value);

	public PropertyType getType();

	public void setType(PropertyType type);

	public void setValueRangeExpression(String rangeExpression);

	public String getValueRangeExpression();

	/**
	 * A set of data is discrete if the values belonging to it are distinct and
	 * separate, i.e. they can be counted. Examples (1,2,3,....); (male,
	 * female); (Bold, Italic, Normal).
	 * 
	 * @return true if the range is defined by a discrete data set; false if it
	 *         is defined by a continuous data set.
	 */
	public boolean hasDiscreteValueRange();

	public Object[] getDiscreteValueRange();

	public boolean isValid(Object value);

	@Override
	public PropertyList getParent();

	@Override
	public void setParent(PropertyList parent);

	@Override
	public PropertyUnit deepCopy();

}
