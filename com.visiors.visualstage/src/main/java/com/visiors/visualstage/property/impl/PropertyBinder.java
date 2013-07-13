package com.visiors.visualstage.property.impl;

import java.util.HashMap;
import java.util.Map;

import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.listener.PropertyListener;

/**
 * <p>
 * This helper class allows binding a {@link PropertyList} to any given java
 * class. Whenever the method {@link #bind(PropertyList)} is invoked, this class
 * looks for all {@link PropertyUnit}s contained in the given
 * {@link PropertyList} and map them to the associated methods of the given
 * class. This also means that invocation of {@link #bind(PropertyList)} is also
 * required after any structural changes to {@link PropertyList}.
 * </p>
 * <p>
 * Once the method {@link #bind(PropertyList)} is called, the client can use
 * {@link #loadAll()} to update the class members based on the values of the
 * given {@link PropertyList} or call {@link #saveAll()} to overwrite the
 * properties' values with the current value of the class members.
 * </p>
 * <p>
 * While Updating the class members will be carried out automatically (by
 * listening to the {@link PropertyListener}), updating the properties can only
 * be done explicitly by calling one of the methods {@link #load(String)} or
 * {@link #loadAll()}
 * </p>
 * <p>
 * To make binding work a class needs to define getter/setter methods for each
 * {@link PropertyUnit}; for instance if a class wants to bind the property
 * "globeId" to one of its members, it has to define the methods getGlobeId()
 * and setGlobeId() which internally will get/set the value of the member
 * variable "globeId". The getter method must not have any parameters; the
 * setter method must have exactly one parameter. No exception will be thrown
 * for missing method definitions.
 * </p>
 * <p>
 * It is possible to define a different handler-classes for each
 * {@link PropertyList} contained in the main property list by using the method
 * {@link #setHandler(String, Object)}.
 * 
 * </p>
 * 
 * @author Shane
 * 
 */
public class PropertyBinder {

	private final Object defaultHandler;
	private final Map<String, PropertyUnitBinder> bindings = new HashMap<String, PropertyUnitBinder>();
	private final Map<String, Object> hanlders = new HashMap<String, Object>();

	public PropertyBinder(Object propertyOwnerClass) {

		this.defaultHandler = propertyOwnerClass;
	}

	/**
	 * This method can be used to Specify the class that handles synchronization
	 * for a specific {@link PropertyList}. <code>propertyOwnerClass</code>
	 * which was passed in constructor will be used as default handler for all
	 * {@link PropertyList} without a specified handler.
	 * 
	 * @param propertyList
	 *            the path to the property-list
	 * @param handlerClass
	 *            the class that provides getter/setter methods
	 */
	public void setHandler(String propertyListPath, Object handlerClass) {

		hanlders.put(propertyListPath, handlerClass);
	}

	/**
	 * Invokes the associated getter method for the given property and overwrite
	 * the property's value accordingly.
	 * 
	 * @param attributePath
	 *            the property's path. The path can begin with the wildcard sign
	 *            "*"
	 */
	public void save(String attributePath) {

		PropertyUnitBinder binding = null;
		if (attributePath.startsWith("*")) {
			String relPath = attributePath.substring(attributePath.charAt(1) == ':' ? 2 : 1);
			for (String key : bindings.keySet()) {
				if (key.endsWith(relPath)) {
					binding = bindings.get(key);
					break;
				}
			}
		} else {
			binding = bindings.get(attributePath);
		}
		if (binding == null) {
			throw new RuntimeException("Invalid binding: " + attributePath);
		}
		binding.updateProperty();
	}

	/**
	 * Invokes all associated getter methods for the {@link PropertyUnit}s of
	 * the given {@link PropertyList} and updates the property's values
	 * accordingly.
	 * 
	 */
	public void saveAll() {

		for (PropertyUnitBinder binding : bindings.values()) {
			binding.updateProperty();
		}
	}

	/**
	 * Invokes the associated setter methods to ensure that class member is in
	 * sync with the property specified by <code>attributePath</code>
	 * 
	 * @param attributePath
	 *            the property's path
	 */
	public void load(String attributePath) {

		PropertyUnitBinder binding = bindings.get(attributePath);
		if (binding == null) {
			throw new RuntimeException("Invalid binding: " + attributePath);
		}
		binding.updateMember();
	}

	/**
	 * Invokes all associated setter methods to ensure that class members are in
	 * sync with the {@link PropertyList}
	 * 
	 */
	public void loadAll() {

		// update the members
		for (PropertyUnitBinder binding : bindings.values()) {
			binding.updateMember();
		}
	}

	/**
	 * Scans the given <code>propertyList</code> to find all contained
	 * {@link PropertyUnit}s to create internal maps between the
	 * {@link PropertyUnit}s and the class members. No Synchronisation will
	 * happen before this method is called. Please note that this method must
	 * also be called after any structural changes to {@link PropertyList}.
	 * 
	 * @param propertyList
	 *            the properties
	 */
	public void bind(PropertyList propertyList) {

		// un-bind old bindings
		unbindAllPropertyUnits();

		// bind all properties and class members
		bindAllPropertyUnits(propertyList, propertyList.getName());
	}

	private void bindAllPropertyUnits(PropertyList properties, String path) {

		for (int i = 0; i < properties.size(); i++) {
			Property p = properties.get(i);
			if (p instanceof PropertyList) {
				PropertyList pl = (PropertyList) p;
				String relPath = path + ":" + pl.getName();
				bindAllPropertyUnits(pl, relPath);
			} else if (p instanceof PropertyUnit) {
				PropertyUnit pu = (PropertyUnit) p;
				String relPath = path + ":" + pu.getName();
				Object handler = getHandlerForPropertyList(path);
				bindings.put(relPath, new PropertyUnitBinder(handler, pu, relPath));
			}
		}
	}

	/**
	 * Searches for a handler for the given path. If there is no exact match, it
	 * looks if there in a handler for the parent {@link PropertyList}. It
	 * returns the default handler if no match could be found.
	 */
	private Object getHandlerForPropertyList(String path) {

		// check if there is a exact match
		Object handler = hanlders.get(path);
		if (handler != null) {
			return handler;
		}

		// look for a match in the hierarchy tree
		Map.Entry<String, Object> bestMatch = null;
		for (Map.Entry<String, Object> entry : hanlders.entrySet()) {
			if (path.startsWith(entry.getKey())) {
				if (bestMatch == null || bestMatch.getKey().length() < entry.getKey().length()) {
					bestMatch = entry;
				}
			}
		}
		if (bestMatch != null) {
			return bestMatch.getValue();
		}

		// return default handler
		return defaultHandler;
	}

	private void unbindAllPropertyUnits() {

		for (PropertyUnitBinder binder : bindings.values()) {
			binder.unbind();
		}
		bindings.clear();
	}

}
