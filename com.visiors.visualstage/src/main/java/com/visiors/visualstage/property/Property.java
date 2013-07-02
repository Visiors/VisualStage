package com.visiors.visualstage.property;

import com.visiors.visualstage.property.listener.PropertyListener;

public interface Property {

	public static final int VISIBLE = 1 << 0;
	public static final int PERSISTENT = 1 << 1;
	public static final int EDITABLE = 1 << 2;

	public String getName();

	public void setName(String name);

	public PropertyList getParent();

	public void setParent(PropertyList parent);

	public Property deepCopy();

	public void setAttribute(int a);

	public int getAttribute();

	public void addPropertyListener(PropertyListener l);

	public void removePropertyListener(PropertyListener l);
}
