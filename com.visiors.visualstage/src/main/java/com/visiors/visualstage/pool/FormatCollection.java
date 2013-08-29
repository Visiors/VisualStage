package com.visiors.visualstage.pool;

import java.io.IOException;

import com.visiors.visualstage.svg.SVGDescriptor;

public interface FormatCollection {

	public final String DEFAULT_STYLE = "DEFAULT_STYLE";
	public final String DEFAULT_NODE_PRESENTATION = "DEFAULT_NODE_PRESENTATION";
	public final String DEFAULT_EDGE_PRESENTATION = "DEFAULT_EDGE_PRESENTATION";
	public final String DEFAULT_SUBGRAPH_PRESENTATION = "DEFAULT_SUBGRAPH_PRESENTATION";

	public void add(SVGDescriptor svgDescriptor);

	public SVGDescriptor remove(String key);

	public boolean contains(String key);

	public SVGDescriptor get(String key);

	public void loadAndPool(String xmlContent) throws IOException;

}