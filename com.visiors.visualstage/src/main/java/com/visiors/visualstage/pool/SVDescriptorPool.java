package com.visiors.visualstage.pool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.visiors.visualstage.exception.XMLDocumentReadException;
import com.visiors.visualstage.export.XMLService;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.svg.SVGDescriptor;

@Singleton
public class SVDescriptorPool {

	private final Map<String, SVGDescriptor> svgDefinition = new HashMap<String, SVGDescriptor>();

	/**
	 * This method reads the given <code>description</code> and extract
	 * definitions for node descriptors.
	 * 
	 */
	public synchronized void loadAndPool(String description) {

		try {
			XMLService xmlService = new XMLService();
			PropertyList properties = xmlService.XML2PropertyList(description);
			for (Property property : properties) {

				SVGDescriptor svgDefinition = new SVGDescriptor((PropertyList) property);
				add(svgDefinition.id, svgDefinition);
			}
		} catch (IOException e) {
			throw new XMLDocumentReadException("Error while reading the XML content!", e);
		}

	}

	public synchronized SVGDescriptor get(String key) {

		return svgDefinition.get(key);
	}

	public synchronized void add(String key, SVGDescriptor presentation) {

		svgDefinition.put(key, presentation);
	}

	public synchronized void remove(String key) {

		svgDefinition.remove(key);
	}

	public synchronized boolean contains(String key) {

		return svgDefinition.containsKey(key);
	}

}
