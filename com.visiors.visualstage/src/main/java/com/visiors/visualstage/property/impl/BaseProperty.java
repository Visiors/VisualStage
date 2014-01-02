package com.visiors.visualstage.property.impl;

import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;

public abstract class BaseProperty implements Property {

	protected String name;
	protected String fullName;
	protected PropertyList parent;
	protected boolean readOnly;
	protected boolean visible = true;

	@Override
	public String getFullName() {

		return fullName;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public void setParent(PropertyList parent) {

		this.parent = parent;
		this.fullName = composteFullName();
	}

	@Override
	public PropertyList getParent() {

		return parent;
	}

	private String composteFullName() {

		StringBuffer path = new StringBuffer();
		composteFullName(this, path);
		return path.toString();
	}

	private void composteFullName(Property property, StringBuffer path) {

		if (property.getParent() != null) {
			composteFullName(property.getParent(), path);
			if (path.length() > 0) {
				path.append(Property.PATH_SEPARATOR);
			}
		}
		path.append(property.getName());
	}

	@Override
	public void setVisible(boolean visible) {

		this.visible = visible;
	}

	@Override
	public boolean isVisible() {

		return visible;
	}

	@Override
	public void setReadOnly(boolean readOnly) {

		this.readOnly = readOnly;
	}

	@Override
	public boolean isReadOnly() {

		return readOnly;
	}


}
