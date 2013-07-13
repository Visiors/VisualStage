package com.visiors.visualstage.property.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public enum PropertyType { 


	STRING(String.class),
	INTEGER(Integer.TYPE),
	LONG(Long.TYPE),
	DOUBLE(Double.TYPE),
	FLOAT(Float.TYPE),
	BOOLEAN(Boolean.TYPE),
	OBJECT(Object.class),
	COLOR(Color.class);


	private static final Map<String, PropertyType> types = new HashMap<String, PropertyType>();
	private final Class typeClass;


	static {
		for (PropertyType type : PropertyType.values()) {
			PropertyType.types.put(type.getName(), type);
		}
	}

	private PropertyType(Class  typeClass) {
		this.typeClass = typeClass;
	}

	public final String getName() {
		return typeClass.getName();
	}

	public final Class<?> getTypeClass() {
		return typeClass;
	}


	public static PropertyType fromName(String name) {
		return PropertyType.types.get(name);
	}

	public static PropertyType typeOf(Object value) {

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

	//
	//
	//	public Object parseObject(PropertyType attributeType, String str) {
	//
	//		if(attributeType == PropertyType.INTEGER) {
	//			return new Integer(str);
	//		}
	//		if(attributeType == PropertyType.DOUBLE) {
	//			return new Double(str);
	//		}
	//		if(attributeType == PropertyType.FLOAT) {
	//			return new Float(str);
	//		}
	//		if(attributeType == PropertyType.BOOLEAN) {
	//			return new Boolean(str);
	//		}
	//		return str;
	//	}
}
