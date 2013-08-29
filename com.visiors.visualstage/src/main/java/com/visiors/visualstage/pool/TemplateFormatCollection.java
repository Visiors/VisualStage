package com.visiors.visualstage.pool;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.visiors.visualstage.exception.InvalidDescriptorException;
import com.visiors.visualstage.exception.ResourceLoadException;
import com.visiors.visualstage.exception.XMLDocumentReadException;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.svg.SVGDescriptor;
import com.visiors.visualstage.util.PropertyUtil;

public class TemplateFormatCollection implements FormatCollection {

	private final Map<String, SVGDescriptor> svgDefinitionMap = new HashMap<String, SVGDescriptor>();

	public TemplateFormatCollection() {

		loadDefaultFomatDefinition();
	}

	@Override
	public synchronized void add(SVGDescriptor svgDescriptor) {

		if (Strings.isNullOrEmpty(svgDescriptor.getDefinition())) {
			throw new InvalidDescriptorException("Descriptor must not be empty!");
		}

		final String key = svgDescriptor.getID();
		if (Strings.isNullOrEmpty(key)) {
			throw new InvalidDescriptorException("Descriptor has no id" + svgDescriptor.getDefinition());
		}
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
		for (final Property property : properties) {
			if (property instanceof PropertyList) {
				final PropertyList pl = (PropertyList) property;
				final String tag = pl.getName();
				// if (XMLConstants.DEFINITIONS_SECTION_TAG.equals(tag)) {
				add(new SVGDescriptor(pl));
				// }
			}
		}
	}

	private void loadDefaultFomatDefinition() {

		final String name = "DefaultFormatDefinition.xml";
		final InputStream is = getClass().getResourceAsStream(name);
		if (is == null) {
			throw new ResourceLoadException("Failed to load the default format definition " + name);
		}
		try {
			final String xmlContent = CharStreams.toString(new InputStreamReader(is, "UTF-8"));
			loadAndPool(xmlContent);
		} catch (final IOException e) {
			throw new XMLDocumentReadException("Failed to read the default format definition " + name, e);
		}
	}

}
