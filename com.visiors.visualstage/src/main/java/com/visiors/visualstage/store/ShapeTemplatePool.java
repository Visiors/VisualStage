package com.visiors.visualstage.store;

import java.util.HashMap;
import java.util.Map;

import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.util.PropertyUtil;

public class ShapeTemplatePool {

	private final Map<String, PropertyList> registeredGraphObjects = new HashMap<String, PropertyList>();

	public ShapeTemplatePool() {

	}

	//
	// /**
	// * Registers a {@link PropertyList} that will server as a copy for
	// * construction similar visual nodes.
	// *
	// * @param properties
	// * A concrete implementation of {@link PropertyList}
	// */
	public void add(PropertyList properties) {

		registeredGraphObjects.put(properties.getName(), properties);
	}

	// /**
	// * Returns true if the graph object specified by <code>name</code> is
	// * already registered; otherwise false.
	// */
	public boolean contains(String type) {

		return registeredGraphObjects.containsKey(type);
	}

	public PropertyList get(String type) {

		return registeredGraphObjects.get(type);
	}

	/**
	 * This method reads the given xml and extract definitions for VisualNodes,
	 * VisualEdges, VisualGraphs and Shapes. The extracted object definitions
	 * can be used as master copies for constructing similar objects.
	 * 
	 * @param xml
	 *            XML definition of graph objects such as visual nodes, edges,
	 *            subgraph etc.
	 * 
	 * @see {@link #createEdge(long, String)}, {@link #createEdge(long, String)}
	 *      , {@link #createContainer(long, String)}
	 */

	public void loadAndPool(String xmlContent) {

		final PropertyList properties = PropertyUtil.XML2PropertyList(xmlContent);
		for (final Property property : properties) {
			final String tag = property.getName();
			if (PropertyConstants.NODE_SECTION_TAG.equals(tag) || PropertyConstants.EDGE_SECTION_TAG.equals(tag)
					|| PropertyConstants.SUBGRAPH_SECTION_TAG.equals(tag)) {
				add(properties);
			}
		}
	}

}
