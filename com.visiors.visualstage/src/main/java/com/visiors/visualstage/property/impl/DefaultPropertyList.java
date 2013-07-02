package com.visiors.visualstage.property.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.listener.PropertyListener;

public class DefaultPropertyList implements PropertyList, PropertyListener {

	protected String name;
	private String contentText = "";
	protected PropertyList parent;
	protected Vector<Property> properties = new Vector<Property>();
	private int attributes;

	public DefaultPropertyList() {

	}

	public DefaultPropertyList(String name) {

		this.name = name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getName() {

		return this.name;
	}

	@Override
	public boolean add(Property property) {

		if (property != null) {
			property.setParent(this);
			property.addPropertyListener(this);
			return this.properties.add(property);
		}
		return false;
	}

	// public boolean add(PropertyUnit property) {
	// if (property != null) {
	// property.setParent(this);
	// property.addPropertyListener(this);
	// return this.properties.add(property);
	// }
	// return false;
	// }
	//
	// public boolean add(PropertyList propertyList) {
	// if (propertyList != null) {
	// propertyList.setParent(this);
	// propertyList.addPropertyListener(this);
	// return this.properties.add(propertyList);
	// }
	// return false;
	// }

	@Override
	public Property get(String name) {

		for (int i = 0; i < properties.size(); i++) {
			if (properties.get(i) instanceof PropertyList) {
				if (((PropertyList) properties.get(i)).getName().equals(name)) {
					return properties.get(i);
				}
			} else if (properties.get(i) instanceof PropertyUnit) {
				if (((PropertyUnit) properties.get(i)).getName().equals(name)) {
					return properties.get(i);
				}
			}
		}

		return null;
	}

	@Override
	public Property get(int index) {

		if (index >= properties.size()) {
			throw new NoSuchElementException();
		}
		return properties.get(index);
	}

	@Override
	public int size() {

		return properties.size();
	}

	@Override
	public boolean remove(String name) {

		Property p = get(name);
		if (p != null) {
			p.setParent(null);
			p.removePropertyListener(this);
			return properties.remove(p);
		}
		return false;
	}

	@Override
	public boolean remove(int index) {

		Property p = get(index);

		p.setParent(null);
		p.removePropertyListener(this);
		return properties.remove(index) != null;
	}

	@Override
	public PropertyList getParent() {

		return this.parent;
	}

	@Override
	public void setParent(PropertyList parent) {

		this.parent = parent;
	}

	@Override
	public int getAttribute() {

		return attributes;
	}

	@Override
	public void setAttribute(int a) {

		this.attributes = a;
	}

	@Override
	public PropertyList deepCopy() {

		PropertyList d = new DefaultPropertyList();
		d.setName(name);
		d.setParent(parent);
		for (Property p : properties) {
			if (p instanceof PropertyUnit) {
				d.add(p.deepCopy());
			} else if (p instanceof PropertyList) {
				d.add(p.deepCopy());
			}
		}

		// for (PropertyListener l : propertyListener) {
		// d.addPropertyListener(l);
		// }
		return d;
	}

	@Override
	public String toString() {

		String indent = "   ";

		StringBuffer sb = new StringBuffer();
		PropertyList l = this;
		int depth = 1;
		while (l.getParent() != null) {
			l = l.getParent();
			depth++;
		}
		sb.append("<").append(getName()).append(">");
		for (int i = 0; i < size(); i++) {
			sb.append("\n");
			for (int j = 0; j < depth; j++, sb.append(indent)) {
				;
			}
			sb.append(properties.get(i).toString());
		}
		sb.append("\n");
		for (int j = 0; j < depth - 1; j++, sb.append(indent)) {
			;
		}

		if (!contentText.isEmpty()) {
			sb.append(contentText);
		}
		sb.append("</").append(getName()).append(">");
		return sb.toString();
	}

	// //////////////////////////////////////////////////////////////////////////
	// Notifications - sending notification to listener

	protected List<PropertyListener> propertyListener = new ArrayList<PropertyListener>();

	@Override
	public void addPropertyListener(PropertyListener listener) {

		if (!propertyListener.contains(listener)) {
			this.propertyListener.add(listener);
		}

	}

	@Override
	public void removePropertyListener(PropertyListener listener) {

		if (!propertyListener.contains(listener)) {
			this.propertyListener.remove(listener);
		}

	}

	@Override
	public void propertyChanged(List<PropertyList> path, PropertyUnit property) {

		path.add(0, this);
		for (PropertyListener l : propertyListener) {
			l.propertyChanged(path, property);
		}
	}

	@Override
	public String getText() {

		return contentText;
	}

	@Override
	public void setText(String contentText) {

		this.contentText = contentText;
	}

	@Override
	public Iterator<Property> iterator() {

		return new PropertyListIterator(this);
	}

	public static class PropertyListIterator<Property> implements Iterator<Property> {

		private int count;
		private final PropertyList pl;

		public PropertyListIterator(PropertyList pl) {

			this.pl = pl;
		}

		@Override
		public boolean hasNext() {

			return count < pl.size();
		}

		@Override
		public Property next() {

			return (Property) pl.get(count++);
		}

		@Override
		public void remove() {

			pl.remove(count);
		}
	}
}
