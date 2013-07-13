package com.visiors.visualstage.property;

import com.visiors.visualstage.property.listener.PropertyListener;

public interface Property {


	public String getName();

	public void setName(String name);

	public void setVisible(boolean visible);

	public boolean isVisible();

	public void setReadOnly(boolean readOnly);

	public boolean isReadOnly();

	public PropertyList getParent();

	public void setParent(PropertyList parent);

	public Property deepCopy();

	public void addPropertyListener(PropertyListener l);

	public void removePropertyListener(PropertyListener l);
}
