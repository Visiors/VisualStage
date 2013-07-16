package com.visiors.visualstage.graph.view.shape.impl;

import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.visiors.visualstage.graph.view.shape.CompositeLayout;
import com.visiors.visualstage.graph.view.shape.CompositeShape;
import com.visiors.visualstage.graph.view.shape.LayoutData;
import com.visiors.visualstage.graph.view.shape.Shape;
import com.visiors.visualstage.graph.view.shape.Shape.Unit;

public class LaneLayout implements CompositeLayout {

	@Override
	public boolean layout(CompositeShape container, List<Shape> shapes, boolean adjustContainerSize) {

		final Rectangle bounds = container.getBounds();
		int requiredContainerMinWidth = 0;
		int requiredContaineMinHeight = 0;

		List<List<Shape>> rows = splitRows(shapes);

		// calc max row width
		for (List<Shape> row : rows) {
			requiredContainerMinWidth = Math.max(bounds.width, getRowMinWidth(row));
		}
		// adjust shapes' width in each row
		int shareableSpace = Math.max(0, bounds.width - requiredContainerMinWidth);
		for (List<Shape> row : rows) {
			adjustShapesWidthInRow(row, bounds.x, shareableSpace);
		}

		// calc row heights
		int colHeight[] = new int[rows.size()];
		for (int i = 0; i < rows.size(); i++) {
			colHeight[i] = getRowMinHeight(shapes);
			requiredContaineMinHeight += colHeight[i];
		}
		// arrange shapes' height in each row
		shareableSpace = Math.max(0, bounds.height - requiredContaineMinHeight);
		int curY = bounds.y;
		for (int i = 0; i < rows.size(); i++) {
			adjustShapesHeightInRow(rows.get(i), curY, requiredContaineMinHeight);
			curY += colHeight[i];
		}

		if (adjustContainerSize) {
			if (requiredContainerMinWidth > bounds.width || requiredContaineMinHeight > bounds.height) {
				bounds.width = requiredContainerMinWidth;
				bounds.height = requiredContaineMinHeight;
				container.setBounds(bounds);
				return true;
			}
		}

		return false;
	}

	private List<List<Shape>> splitRows(List<Shape> shapes) {

		final List<List<Shape>> rows = new ArrayList<List<Shape>>();
		final List<Shape> row = new ArrayList<Shape>();
		int curRow = 0;

		sortShapes(shapes);

		for (Shape shape : shapes) {
			final LaneLayoutData data = (LaneLayoutData) shape.getLayoutData();
			if (curRow != data.getRow()) { // new row

				if (!row.isEmpty()) {
					rows.add(row);
					row.clear();
				}
				curRow = data.getRow();
			}
			row.add(shape);
		}
		return rows;
	}

	private int getRowMinWidth(List<Shape> shapes) {

		int requiredWidth = 0;

		for (Shape shape : shapes) {
			Insets margin = shape.getMargin();
			if (shape.getPreferredWidthUnit() == Unit.PERCENT) {
				requiredWidth += margin.left + margin.right;
			} else {
				requiredWidth += shape.getPreferredWidth() + margin.left + margin.right;
			}
		}
		return requiredWidth;
	}

	private int getRowMinHeight(List<Shape> shapes) {

		int requiredHeight = 0;
		for (Shape shape : shapes) {
			Insets margin = shape.getMargin();
			if (shape.getPreferredHeightUnit() == Unit.PERCENT) {
				requiredHeight = Math.max(requiredHeight, margin.top + margin.bottom);

			} else {
				requiredHeight = Math.max(requiredHeight, shape.getPreferredHeight() + margin.top + margin.bottom);
			}
		}
		return requiredHeight;
	}

	private void adjustShapesWidthInRow(List<Shape> shapes, int x, int sharableSpace) {

		int allShares = 0;
		int width = 0;

		for (Shape shape : shapes) {
			if (shape.getPreferredWidthUnit() == Unit.PERCENT) {
				allShares += shape.getPreferredWidth();
			}
		}

		for (Shape shape : shapes) {
			Insets margin = shape.getMargin();
			if (shape.getPreferredWidthUnit() == Unit.PERCENT) {
				width += margin.left + margin.right + shape.getPreferredWidth() / allShares * sharableSpace;
			} else {
				width += margin.left + margin.right + shape.getPreferredWidth();
			}
			Rectangle b = shape.getBounds();
			b.x = x;
			b.width = width;
			x += width;
			shape.setBounds(b);
		}
	}

	private void adjustShapesHeightInRow(List<Shape> shapes, int y, int sharableSpace) {

		int allShares = 0;
		int height = 0;

		for (Shape shape : shapes) {
			if (shape.getPreferredWidthUnit() == Unit.PERCENT) {
				allShares += shape.getPreferredHeight();
			}
		}

		for (Shape shape : shapes) {
			Insets margin = shape.getMargin();
			if (shape.getPreferredWidthUnit() == Unit.PERCENT) {
				height += margin.top + margin.bottom + shape.getPreferredHeight() / allShares * sharableSpace;
			} else {
				height += margin.top + margin.bottom + shape.getPreferredHeight();
			}
			Rectangle b = shape.getBounds();
			b.y = y;
			b.height = height;
			shape.setBounds(b);
		}
	}

	private void sortShapes(List<Shape> shapes) {

		Collections.sort(shapes, new Comparator<Shape>() {

			@Override
			public int compare(Shape s1, Shape s2) {

				final LayoutData data1 = s1.getLayoutData();
				final LayoutData data2 = s2.getLayoutData();
				if (data1 instanceof LaneLayoutData == false || data2 instanceof LaneLayoutData == false) {
					// TODO exception
				}
				LaneLayoutData laneLayoutData1 = (LaneLayoutData) data1;
				LaneLayoutData laneLayoutData2 = (LaneLayoutData) data2;
				if (laneLayoutData1.getRow() < laneLayoutData2.getRow()) {
					return -1;
				}
				if (laneLayoutData1.getRow() > laneLayoutData2.getRow()) {
					return 1;
				}
				if (laneLayoutData1.getColumn() < laneLayoutData2.getColumn()) {
					return -1;
				}
				if (laneLayoutData1.getColumn() > laneLayoutData2.getColumn()) {
					return 1;
				}
				return 0;
			}
		});

	}
}
