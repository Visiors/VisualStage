package com.visiors.visualstage.property.impl;

import com.visiors.visualstage.exception.AttributeException;
import com.visiors.visualstage.property.ValueRange;


public class DefaultValueRange implements ValueRange {


	private static final String LIST_SEPARATOR = ",";
	private static final String EQUAL          = "==";
	private static final String NOTEQUAL       = "!=";
	private static final String GREATER        = ">";   
	private static final String GREATEROREQUAL = ">=";
	private static final String LESS           = "<";
	private static final String LESSOREQUAL    = "<=";

	private final PropertyType type;
	private String valueExpression;
	private boolean isDescrete;
	private Object[] descreteDataSet;

	public DefaultValueRange(PropertyType type, String valueExpression) {
		this.type = type;
		setExpression(valueExpression);
	}


	private boolean isDescrete() {
		if(valueExpression == null) {
			return false;
		}

		//		
		//		char c;
		//		int sepIndex = -1;
		//		String operator = "><=!&";
		//		for (int i = 0, len = valueExpression.length(); i < len;i++) {
		//			c = valueExpression.charAt(i);
		//			if(c == '|') {
		//				if(i == 0 || i == len-1)  // | at the very first and last position not accepted
		//					return false;
		//				if(i == sepIndex +1 ) // || not accepted
		//					return false;
		//			}
		//			if(operator.indexOf(c) != -1 ) // the list should have no other operator 
		//				return false;
		//		}
		//		
		//		return sepIndex != -1;

		return valueExpression.indexOf(LIST_SEPARATOR) != -1;
	}

	@Override
	public ValueRange deepCopy() {
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public Object[] getDescreteDataSet() {
		if(isDescrete && descreteDataSet == null) {
			descreteDataSet = parseValueSet();
		}
		return descreteDataSet;
	}




	private boolean isValidateType(Object value) {

		if(type == PropertyType.INTEGER) {
			return value instanceof Integer;
		}
		if(type == PropertyType.DOUBLE) {
			return value instanceof Double;
		}
		if(type == PropertyType.FLOAT) {
			return value instanceof Float;
		}
		if(type == PropertyType.BOOLEAN) {
			return value instanceof Boolean;
		}
		if(type == PropertyType.STRING) {
			return value instanceof String;
		}
		return false;
	}


	private Object[] parseValueSet() {
		String[] values = valueExpression.split(LIST_SEPARATOR);
		descreteDataSet = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			descreteDataSet[i] = PropertyType.parseObject(type, values[i].trim());
		}
		return descreteDataSet;
	}

	@Override
	public boolean isValueValid(Object value) {
		if(valueExpression == null) {
			return true;
		}

		if(!isValidateType(value)) {
			throw new AttributeException("Value must be from type " + type.toString());
		}

		if(isDescrete) {
			Object[] set = getDescreteDataSet();
			if(set != null) {
				for (Object element : set) {
					if(element.equals(value)) {
						return true;
					}
				}
			}
			return false;
		}

		return RangeValidator.isValid(value, valueExpression);
	}


	@Override
	public void setExpression(String rangeExpression) {
		this.valueExpression = rangeExpression;
		this.isDescrete = isDescrete();
	}




	@Override
	public String getExpression() {
		return valueExpression;
	}




	@Override
	public boolean discreteDataSet() {
		return isDescrete;
	}



}
