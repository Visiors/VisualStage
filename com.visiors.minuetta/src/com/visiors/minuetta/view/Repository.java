package com.visiors.minuetta.view;

import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.visiors.visualstage.editor.impl.GraphEditor;
import com.visiors.visualstage.export.XMLService;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.util.PropertyUtil;

public class Repository {

	Map<String, Map<String, RepositoryItem>> shapesMap = Maps.newHashMap();
	private final GraphEditor editor;

	public Repository(GraphEditor editor) {

		this.editor = editor;

	}

	public void loadRepository(String xml) {

		if (!Strings.isNullOrEmpty(xml)) {
			final PropertyList pl = convertToPropertyList(xml);
			setup(pl);
		}
	}

	public List<String> getCategories() {

		return ImmutableList.copyOf(shapesMap.keySet());

	}

	public List<RepositoryItem> getShapes(String category) {

		final Map<String, RepositoryItem> shapes = shapesMap.get(category);
		if (shapes != null) {
			return ImmutableList.copyOf(shapes.values());
		}
		return Lists.newArrayList();
	}

	private PropertyList convertToPropertyList(String xml) {

		final XMLService xmlService = new XMLService();
		return xmlService.XML2PropertyList(xml);
	}

	private void setup(PropertyList propertyList) {

		for (final Property property : propertyList) {
			if (property instanceof PropertyList) {
				createCategory((PropertyList) property);
			}
		}
	}

	private void createCategory(PropertyList shapes) {

		final String category = PropertyUtil.getProperty(shapes, "name", "");
		for (int i = 0; i < shapes.size(); i++) {
			final Property shape = shapes.get(i);
			if (shape instanceof PropertyList) {
				addShape(category, (PropertyList) shape);
			}
		}
	}

	private void addShape(String category, PropertyList shapeProperties) {

		Map<String, RepositoryItem> shapes = shapesMap.get(category);
		if (shapes == null) {
			shapes = Maps.newHashMap();
			shapesMap.put(category, shapes);
		}
		final RepositoryItem repositoryItem = new RepositoryItem(editor, category, shapeProperties);
		shapes.put(repositoryItem.getShapeName(), repositoryItem);
	}
}
