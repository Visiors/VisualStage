package com.visiors.visualstage.graph.view.shape.impl;

import com.visiors.visualstage.graph.view.shape.LayoutData;
import com.visiors.visualstage.property.PropertyList;

public class LaneLayoutData implements LayoutData {

	private PropertyList properties;
	private int row;
	private int column;

	@Override
	public void setProperties(PropertyList properties) {

		this.properties = properties;
	}

	@Override
	public PropertyList getProperties() {

		return properties;
	}

	@Override
	public LayoutData deepCopy() {
		final LaneLayoutData data = new LaneLayoutData();
		data.setColumn(column);
		data.setRow(row);
		data.setProperties(properties.deepCopy());
		return data;
	}

	public int getRow() {

		return row;
	}

	public void setRow(int row) {

		this.row = row;

	}

	public int getColumn() {

		return column;
	}

	public void setColumn(int column) {

		this.column = column;

	}
}
