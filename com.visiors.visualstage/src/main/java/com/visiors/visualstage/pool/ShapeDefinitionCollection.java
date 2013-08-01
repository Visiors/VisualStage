package com.visiors.visualstage.pool;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.util.PropertyUtil;


@Singleton
public class ShapeDefinitionCollection  {

	private final Map<String, PropertyList> shapesDefinitionMap = new HashMap<String, PropertyList>();



	public ShapeDefinitionCollection() {

	}

	//
	// /**
	// * Registers a {@link PropertyList} that will server as a copy for
	// * construction similar visual nodes.
	// *
	// * @param properties
	// * A concrete implementation of {@link PropertyList}
	// */
	/* (non-Javadoc)
	 * @see com.visiors.visualstage.pool.ShapeDefinitionCollection#add(com.visiors.visualstage.property.PropertyList)
	 */


	public synchronized void add(PropertyList properties) {

		shapesDefinitionMap.put(properties.getName(), properties);
	}



	public synchronized PropertyList remove( String name) {

		return shapesDefinitionMap.remove(name);
	}



	// /**
	// * Returns true if the graph object specified by <code>name</code> is
	// * already registered; otherwise false.
	// */

	public synchronized boolean contains(String name) {

		return shapesDefinitionMap.containsKey(name);
	}



	public synchronized PropertyList get(String name) {

		return shapesDefinitionMap.get(name);
	}


	public synchronized void loadAndPool(String xmlContent) {

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
