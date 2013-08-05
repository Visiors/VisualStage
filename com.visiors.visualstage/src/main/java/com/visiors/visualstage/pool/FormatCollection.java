package com.visiors.visualstage.pool;

import java.io.IOException;

import com.visiors.visualstage.svg.SVGDescriptor;

public interface FormatCollection {

	public void add(SVGDescriptor svgDescriptor);

	public SVGDescriptor remove(String key);

	public boolean contains(String key);

	public SVGDescriptor get(String key);

	public void loadAndPool(String xmlContent) throws IOException;

}