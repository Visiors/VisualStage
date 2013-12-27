package com.visiors.visualstage.pool;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.property.PropertyList;

public class DefaultGraphBuilder implements GraphBuilder{

	private final String selectedSubgraph = ShapeDefinitionCollection.DEFAULT_SUBGRAPH;
	private final String selectedNode = ShapeDefinitionCollection.DEFAULT_NODE;
	private final String selectedEdge = ShapeDefinitionCollection.DEFAULT_EDGE;

	@Inject
	protected Provider<VisualNode> visualNodeProvider;
	@Inject
	protected Provider<VisualEdge> visualEdgeProvider;
	@Inject
	protected Provider<VisualGraph> visualGraphProvider;

	@Inject
	protected ShapeDefinitionCollection shapeDefinitionCollection;
	@Inject
	protected FormatDefinitionCollection formatDefinitionCollection;


	@Override
	public VisualNode createNode() {

		if (selectedNode == null) {
			throw new IllegalArgumentException("No node was selected");
		}
		return createNode(selectedNode);
	}

	@Override
	public VisualNode createNode(String name) {

		final VisualNode visualNode = visualNodeProvider.get();
		final PropertyList properties = getShapeDefinition(name);
		DI.injectMembers(visualNode);
		visualNode.setProperties(properties);
		return visualNode;
	}

	@Override
	public VisualNode createNode(PropertyList properties) {

		final VisualNode visualNode = visualNodeProvider.get();
		DI.injectMembers(visualNode);
		visualNode.setProperties(properties);
		return visualNode;
	}


	@Override
	public VisualEdge createEdge() {

		if (selectedEdge  == null) {
			throw new IllegalArgumentException("No edge was selected");
		}
		return createEdge(selectedEdge);
	}

	@Override
	public VisualEdge createEdge(String name) {

		final VisualEdge visualEdge = visualEdgeProvider.get();
		DI.injectMembers(visualEdge);
		final PropertyList properties = getShapeDefinition(name);
		visualEdge.setProperties(properties);
		return visualEdge;
	}

	@Override
	public VisualEdge createEdge(PropertyList properties) {

		final VisualEdge visualEdge = visualEdgeProvider.get();
		DI.injectMembers(visualEdge);
		visualEdge.setProperties(properties);
		return visualEdge;
	}



	@Override
	public VisualGraph createSubgraph() {

		if (selectedSubgraph == null) {
			throw new IllegalArgumentException("No sub-graph was selected");
		}
		return createSubgraph(selectedSubgraph);
	}

	@Override
	public VisualGraph createSubgraph(String name) {

		final VisualGraph visualSubgraph = visualGraphProvider.get();
		DI.injectMembers(visualSubgraph);
		final PropertyList properties = getShapeDefinition(name);
		visualSubgraph.setProperties(properties);
		return visualSubgraph;
	}

	@Override
	public VisualGraph createSubgraph(PropertyList properties) {

		final VisualGraph visualSubgraph = visualGraphProvider.get();
		DI.injectMembers(visualSubgraph);
		visualSubgraph.setProperties(properties);
		return visualSubgraph;
	}


	private PropertyList getShapeDefinition(String name) {

		// fetch the definition for the object
		if (!shapeDefinitionCollection.contains(name)) {
			throw new IllegalArgumentException("The graph object associated with '" + name + "' could not be find. ");
		}
		return shapeDefinitionCollection.get(name);
	}


	//
	//	private String getStyleID(PropertyList definition) {
	//
	//		final String styleID = PropertyUtil.getProperty(definition, "styleID", "");
	//		if (!styleID.isEmpty()) {
	//			if (!formatDefinitionCollection.contains(styleID)) {
	//				throw new RuntimeException("The definition for the style  with id' " + styleID + "' could not be find");
	//			}
	//		}
	//		return styleID;
	//	}


}
