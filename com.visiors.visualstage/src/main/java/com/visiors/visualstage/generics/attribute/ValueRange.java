package com.visiors.visualstage.generics.attribute;

public interface ValueRange {

	public void setExpression(String rangeExpression);

	public String getExpression();

	public boolean isValueValid(Object value);

	public boolean discreteDataSet();

	public Object[] getDescreteDataSet();

	public ValueRange deepCopy();

}
