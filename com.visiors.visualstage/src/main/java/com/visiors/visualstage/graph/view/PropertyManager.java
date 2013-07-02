package com.visiors.visualstage.graph.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.listener.PropertyListener;
import com.visiors.visualstage.util.PropertyUtil;

public class PropertyManager implements PropertyListener {

	private PropertyList properties;
	private final GraphObjectView graphObjectView;
	private final Map<String, Class> property2type = new HashMap<String, Class>();
	private final Map<String, Method> property2getter = new HashMap<String, Method>();
	private final Map<String, Method> property2setter = new HashMap<String, Method>();

	public PropertyManager(GraphObjectView graphObjectView) {

		this.graphObjectView = graphObjectView;
		this.properties = graphObjectView.getProperties();
	}

	public void bind(String propertyPath, String memberName, Class memberType, boolean readOnly) {

		String methodName = "get" + capitalizeFirstLetter(memberName);
		final Method getter = findMember(methodName, null);
		if (getter == null) {
			throw new IllegalArgumentException("Missing the getter method '" + methodName + "'");
		}

		methodName = "set" + capitalizeFirstLetter(memberName);
		final Method setter = findMember(methodName, memberType);
		if (setter == null) {
			throw new IllegalArgumentException("Missing the setter method '" + methodName + "'");
		}

		property2getter.put(propertyPath, getter);
		property2setter.put(propertyPath, setter);
		property2type.put(memberName, memberType);
		PropertyUtil.makeEditable(properties, propertyPath, !readOnly);
		updateProperty(propertyPath);
	}

	public void updateProperty(String propertyPath) {

		final Method getter = property2getter.get(propertyPath);
		try {
			final Object value = getter.invoke(graphObjectView, new Class[] {});
			properties = PropertyUtil.setProperty(properties, propertyPath, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updatePropertyOwner(String propertyPath) {

		final Method setter = property2setter.get(propertyPath);
		try {
			final PropertyUnit p = PropertyUtil.findPropertyUnit(properties, propertyPath);
			if (p == null) {
				// throw exception
			}
			final Object value = p.getValue();
			setter.invoke(graphObjectView, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void propertyChanged(List<PropertyList> propertyList, PropertyUnit property) {

		String propertyPath = PropertyUtil.toString(propertyList, property);
		updatePropertyOwner(propertyPath);
	}

	private String capitalizeFirstLetter(String name) {

		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	private Method findMember(String memberName, Class type) {

		try {
			return GraphObjectView.class.getDeclaredMethod(memberName, new Class[] { type });
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
