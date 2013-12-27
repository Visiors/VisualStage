package com.visiors.visualstage.pool;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.inject.Singleton;
import com.visiors.visualstage.constants.XMLConstants;
import com.visiors.visualstage.exception.InvalidKeyException;
import com.visiors.visualstage.exception.ResourceLoadException;
import com.visiors.visualstage.exception.XMLDocumentReadException;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.util.PropertyUtil;

@Singleton
public class DefaultShapeDefinitionCollection implements ShapeDefinitionCollection {

	private final static String[] setctionTags = new String[] { XMLConstants.NODE_SECTION_TAG,
			XMLConstants.EDGE_SECTION_TAG, XMLConstants.SUBGRAPH_SECTION_TAG };

	private final static String[] groupSetctionTags = new String[] { XMLConstants.NODES_SECTION_TAG,
			XMLConstants.EDGES_SECTION_TAG, XMLConstants.SUBGRAPHS_SECTION_TAG };

	private final Map<String, PropertyList> shapeDefinitionMap = new HashMap<String, PropertyList>();

	public DefaultShapeDefinitionCollection() {

		loadDefaultDefinition();
	}

	@Override
	public synchronized void add(String key, PropertyList properties) {

		if (Strings.isNullOrEmpty(key)) {
			throw new InvalidKeyException("Invalid key: " + key);
		}
		shapeDefinitionMap.put(key, properties);
	}

	@Override
	public synchronized PropertyList remove(String key) {

		return shapeDefinitionMap.remove(key);
	}

	@Override
	public synchronized boolean contains(String key) {

		return shapeDefinitionMap.containsKey(key);
	}

	@Override
	public synchronized PropertyList get(String key) {

		return shapeDefinitionMap.get(key);
	}

	@Override
	public synchronized void loadAndPool(String xmlContent) {

		final PropertyList properties = PropertyUtil.XML2PropertyList(xmlContent);
		for (final Property property : properties) {
			if (property instanceof PropertyList) {
				final PropertyList pl = (PropertyList) property;
				final String tag = pl.getName();
				if (isValidTag(groupSetctionTags, tag)) {
					addElements(pl);
				}
			}
		}
	}

	private void addElements(PropertyList properties) {

		for (final Property property : properties) {
			if (property instanceof PropertyList) {
				final PropertyList pl = (PropertyList) property;
				final String tag = pl.getName();
				if (isValidTag(setctionTags, tag)) {
					final String key = PropertyUtil.getProperty(pl, "name", "");
					add(key, pl);
				}
			}
		}
	}

	private boolean isValidTag(String[] tags, String tag) {

		for (final String t : tags) {
			if (tag.equals(t)) {
				return true;
			}
		}
		return false;
	}

	private void loadDefaultDefinition() {

		final String name = "DefaultGraphObjecDefinition.xml";
		final InputStream is = getClass().getResourceAsStream(name);
		if (is == null) {
			throw new ResourceLoadException("Failed to load the default graph object definition " + name);
		}
		try {
			final String xmlContent = CharStreams.toString(new InputStreamReader(is, "UTF-8"));
			loadAndPool(xmlContent);
		} catch (final IOException e) {
			throw new XMLDocumentReadException("Failed to read the default graph object  definition " + name, e);
		}
	}
}
