package com.visiors.visualstage.pool;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.visiors.visualstage.constants.XMLConstants;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.util.PropertyUtil;

@Singleton
public class TemplateShapeCollection  implements ShapeCollection{


	private final static String[] setctionTags = new String[] {
		XMLConstants.NODE_SECTION_TAG,
		XMLConstants.EDGE_SECTION_TAG,
		XMLConstants.SUBGRAPH_SECTION_TAG};

	private final static String[] groupSetctionTags = new String[] {
		XMLConstants.NODES_SECTION_TAG,
		XMLConstants.EDGES_SECTION_TAG,
		XMLConstants.SUBGRAPHS_SECTION_TAG};

	private final Map<String, PropertyList> shapeDefinitionMap = new HashMap<String, PropertyList>();

	public TemplateShapeCollection() {

	}

	@Override
	public synchronized void add(String key, PropertyList properties) {

		if (Strings.isNullOrEmpty(key)) {
			// TODO exception
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
		for (Property property : properties) {
			if (property instanceof PropertyList) {
				PropertyList pl = (PropertyList) property;
				final String tag = pl.getName();
				if (isValidTag(groupSetctionTags, tag)) {
					addElements(pl);
				}
			}
		}
	}

	private void addElements(PropertyList properties) {

		for (Property property : properties) {
			if (property instanceof PropertyList) {
				PropertyList pl = (PropertyList) property;
				final String tag = pl.getName();
				if (isValidTag(setctionTags, tag)) {
					String key = PropertyUtil.getProperty(pl, "name", "");
					add(key, pl);
				}
			}
		}
	}

	private boolean isValidTag(String[] tags, String tag) {

		for (String t : tags) {
			if (tag.equals(t)) {
				return true;
			}
		}
		return false;
	}




}
