package com.visiors.visualstage.property.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.listener.PropertyListener;

/**
 * <p>
 * Private class used by {@link PropertyBinder}.
 * </p>
 * <p>
 * This class makes use of <code>Java Reflection API</code> to invoke
 * getter/setter-methods in order to keep a given {@link PropertyUnit} and the
 * associated class member in sync.
 * </p>
 * <p>
 * Since this class register itself as {@link PropertyListener}, the caller must
 * call the method {@link #unbind()} when the instance of this class is not
 * needed.
 * </p>
 * <p>
 * <b>Important Note:</b> This class avoids unnecessary calls by comparing the
 * current property's value with the associated class member; To enhance
 * performance and prevent redundant calls, objects must implement the method
 * {@link #equals(Object)}
 * </p>
 * 
 * @author Shane
 * 
 */
class PropertyUnitBinder implements PropertyListener {

	private PropertyUnit propertyUnit;
	private Method getter;
	private Method setter;
	private Object host;
	private boolean internallyUpdatingMember;
	private boolean internallyUpdatingProperty;

	PropertyUnitBinder(Object handlerClass, PropertyUnit propertyUnit) {

		this.propertyUnit = propertyUnit;
		host = handlerClass;
		propertyUnit.addPropertyListener(this);

		final String propertyName = propertyUnit.getName();
		final String getterMethodName = "get" + capitalizeFirstLetter(propertyName);
		getter = findDeclaredMethod(handlerClass.getClass(), getterMethodName, new Class[0]);
		if (getter == null) {
			System.err.println("Information: the attribute '" + propertyUnit.getFullName()
					+ "' is not binded.  The getter method definition' " + getterMethodName
					+ "()' was missing in the class '" + "'" + handlerClass.getClass().getName());
		}

		final String setterMethodName = "set" + capitalizeFirstLetter(propertyName);
		final Class<?> propertyType = propertyUnit.getType().getTypeClass();
		setter = findDeclaredMethod(handlerClass.getClass(), setterMethodName, new Class[] { propertyType });
		if (setter == null) {
			System.err.println("Information: the attribute '" + propertyUnit.getFullName()
					+ "' is not binded. The setter method definition' " + setterMethodName
					+ "(...)' was missing  in the class '" + "'" + handlerClass.getClass().getName());
		}
	}

	private Method findDeclaredMethod(Class clazz, String method, Class<?>... parameters) {

		try {
			Method declaredMethod = clazz.getDeclaredMethod(method, parameters);
			if (!Modifier.isPublic(declaredMethod.getModifiers())) {
				declaredMethod.setAccessible(true);
			}
			return declaredMethod;
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		return null;
	}

	private String capitalizeFirstLetter(String name) {

		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	synchronized void updateProperty() {

		if (getter == null) {
			return;
		}
		if (internallyUpdatingMember) {
			return;
		}
		try {
			internallyUpdatingProperty = true;
			final Object value = getter.invoke(host, new Class[] {});
			if (!equalValues(value)) {
				propertyUnit.setValue(value);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			internallyUpdatingProperty = false;

		}
	}

	synchronized void updateMember() {

		try {
			internallyUpdatingMember = true;
			if (setter != null) {
				Object value = null;
				if (getter != null) {
					value = getter.invoke(host, new Class[] {});
				}
				if (!equalValues(value)) {
					setter.invoke(host, propertyUnit.getValue());
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			internallyUpdatingMember = false;
		}
	}

	private boolean equalValues(Object value) {

		if (value == null && propertyUnit.getValue() == null) {
			return true;
		}
		return value != null && value.equals(propertyUnit.getValue());
	}

	@Override
	public void propertyChanged(List<PropertyList> propertyList, PropertyUnit propertyUnit) {

		if (!internallyUpdatingProperty) {
			updateMember();
		}
	}

	public void unbind() {

		if (propertyUnit != null) {
			propertyUnit.removePropertyListener(this);
			host = null;
			propertyUnit = null;
			getter = null;
			setter = null;
		}

	}

	@Override
	public String toString() {

		if (propertyUnit != null) {
			return "property: " + propertyUnit.getFullName() + ", Implementation: " + host.getClass() ;
		}
		return super.toString();
	}
}