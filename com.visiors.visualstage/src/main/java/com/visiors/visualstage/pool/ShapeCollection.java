package com.visiors.visualstage.pool;

import com.visiors.visualstage.property.PropertyList;

public interface ShapeCollection {

	public void add(String key, PropertyList properties);

	public PropertyList remove(String key);

	public boolean contains(String key);

	public PropertyList get(String key);

	public void loadAndPool(String xmlContent);

}