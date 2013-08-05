package com.visiors.visualstage.pool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.svg.SVGDescriptor;
import com.visiors.visualstage.util.PropertyUtil;


public class TemplateFormatCollection implements FormatCollection {



	private final Map<String, SVGDescriptor> svgDefinitionMap = new HashMap<String, SVGDescriptor>();


	@Override
	public synchronized void add(SVGDescriptor svgDescriptor) {

		svgDefinitionMap.put(svgDescriptor.id, svgDescriptor);
	}

	@Override
	public synchronized SVGDescriptor remove(String name) {

		return svgDefinitionMap.remove(name);
	}

	@Override
	public synchronized boolean contains(String name) {

		return svgDefinitionMap.containsKey(name);
	}

	@Override
	public synchronized SVGDescriptor get(String name) {

		return svgDefinitionMap.get(name);
	}

	@Override
	public synchronized void loadAndPool(String xmlContent) throws IOException {

		final PropertyList properties = PropertyUtil.XML2PropertyList(xmlContent);
		for (Property property : properties) {
			if (property instanceof PropertyList) {
				PropertyList pl = (PropertyList) property;
				final String tag = pl.getName();
				//if (XMLConstants.DEFINITIONS_SECTION_TAG.equals(tag)) {
				add(new SVGDescriptor(pl));
				//}
			}
		}
	}


}
