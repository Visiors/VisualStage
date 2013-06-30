package com.visiors.visualstage.generics.attribute;

public enum PropertyType {

	STRING, INTEGER, LONG, DOUBLE, FLOAT, FONT, COLOR, BOOLEAN, OBJECT;

	public static Object parseObject(PropertyType attributeType, String str) {

		if (attributeType == PropertyType.INTEGER) {
			return new Integer(str);
		}
		if (attributeType == PropertyType.DOUBLE) {
			return new Double(str);
		}
		if (attributeType == PropertyType.FLOAT) {
			return new Float(str);
		}
		if (attributeType == PropertyType.BOOLEAN) {
			return new Boolean(str);
		}
		return str;
	}
}
