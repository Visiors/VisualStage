package com.visiors.visualstage.pool;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.graph.view.node.impl.DefaultVisualNode;
import com.visiors.visualstage.graph.view.shape.CompositeLayout;
import com.visiors.visualstage.graph.view.shape.CompositeShape;
import com.visiors.visualstage.graph.view.shape.Shape;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.util.PropertyUtil;

public class GraphBuilder {

	private String selectedContainer;
	private String selectedNode;
	private String selectedEdge;

	@Inject
	protected Provider<VisualNode> visualNodeProvider;
	@Inject
	protected Provider<VisualEdge> visualEdgeProvider;
	@Inject
	protected Provider<VisualGraph> visualGraphProvider;
	@Inject
	protected GraphObjectTemplateCollection graphObjectTemplateCollection;
	@Inject
	protected ShapeDefinitionCollection shapeDefinitionCollection;
	@Inject
	protected SVGDescriptorCollection svgDescriptorCollection;

	/**
	 * Creates a new instance of {@link VisualNode} using the object-definition
	 * associated with <code>name</code>. If the definition contains the
	 * <code>template</code> definition, the node will be created by cloning the
	 * node instance associated with <code>template</code>; otherwise
	 * {@link DefaultVisualNode} will be used as the master copy.
	 * 
	 * @param name
	 *            the name of the associated template
	 * @return A new instance of {@link VisualNode}.
	 * 
	 * @see {@link #createNode()}
	 */
	public VisualNode createNode(String name) {

		final PropertyList definition = getObjectDefinition(name);
		final VisualGraphObject template = getTemplate(definition);
		final VisualNode visualNode = (template != null ? (VisualNode) template : visualNodeProvider.get());
		visualNode.setPresentationID(getPresentationID(definition));
		visualNode.setStyleID(getStyleID(definition));
		final PropertyList formDefinition = getFormDefinition(definition);
		addForm(visualNode, formDefinition);
		return visualNode;
	}

	private void addForm(VisualNode visualNode, PropertyList formDefinition) {

		final CompositeLayout layout = getFormLayout(formDefinition);
		visualNode.setLayout(layout);
		final List<Shape> children = buildChildren(formDefinition);
		visualNode.setChildren(children);
	}



	private List<Shape> buildChildren(PropertyList childrenDefinition) {

		final List<Shape> shapes = new ArrayList<Shape>();
		for (int i = 0; i < childrenDefinition.size(); i++) {
			final Property property = childrenDefinition.get(i);
			if (property instanceof PropertyList) {
				if (property.getName().equals("Composite")) {
					final CompositeShape composite = createComposite((PropertyList) property);
					shapes.add(composite);
				} else if (property.getName().equals("Shape")) {
					final Shape shape = createShape((PropertyList) property);
					shapes.add(shape);
				}
			}
		}
		return shapes;
	}

	private CompositeShape createComposite(PropertyList property) {

		final String compositeName = PropertyUtil.getProperty(property, "name", "");
		final String x = PropertyUtil.getProperty(property, "x", "0");
		final String y = PropertyUtil.getProperty(property, "y", "0");
		final String width = PropertyUtil.getProperty(property, "width", "100%");
		final String height = PropertyUtil.getProperty(property, "height", "100%");
		final String layout_info = PropertyUtil.getProperty(property, "layout_info", "");

		final CompositeShape composite = null; // create composite
		final List<Shape> children = buildChildren(property);
		composite.setChildren(children);
		return composite;
	}

	private Shape createShape(PropertyList property) {

		final String shapeName = PropertyUtil.getProperty(property, "name", "");
		final String x = PropertyUtil.getProperty(property, "x", "0");
		final String y = PropertyUtil.getProperty(property, "y", "0");
		final String width = PropertyUtil.getProperty(property, "width", "100%");
		final String height = PropertyUtil.getProperty(property, "height", "100%");
		final String layout_info = PropertyUtil.getProperty(property, "layout_info", "");

		Shape shape = null;
		return shape;
	}

	/**
	 * Creates a new instance of {@link VisualNode}. The definition for the node
	 * must be specified in advance through {@link #selectedNode(String))};
	 * 
	 * @see {@link #createNode(String))}
	 */
	public VisualNode createNode() {

		if (selectedNode == null) {
			throw new IllegalArgumentException("No node was selected");
		}
		return createNode(selectedNode);
	}

	//
	// /**
	// * Creates a new instance of {@link VisualEdge}. The new object will be
	// * created by cloning the template edge which must registered beforehand
	// by
	// * using {@link #register(VisualGraphObject)};
	// *
	// * @param name
	// * Identifies the associated template-edge
	// * @return A new instance of {@link VisualEdge}.
	// *
	// * @see {@link #createEdge()}
	// */
	// public VisualEdge createEdge(String name) {
	//
	// if (!graphObjectTemplateCollection.isRegistered(name)) {
	// throw new IllegalArgumentException("The graph object associated with '" +
	// name + "' could not be found.  "
	// + "Please load registere the associated graph object first.");
	// }
	//
	// return (VisualEdge) graphObjectTemplateCollection.get(name).deepCopy();
	// }
	//
	// /**
	// * Creates a new instance of {@link VisualEdge}. The new object will be
	// * created by cloning the selected template edge. The template edge must
	// be
	// * registered in advance; the template name must be selected by using
	// * {@link #selectedEdge(String))}
	// *
	// * @see {@link #createEdge(String))}
	// */
	// public VisualEdge createEdge() {
	//
	// if (selectedNode == null) {
	//
	// throw new IllegalArgumentException("No edge was selected");
	// }
	//
	// return createEdge(selectedEdge);
	// }
	//
	// /**
	// * Creates a new instance of {@link VisualGraph}. The new object will be
	// * created by cloning the template graph which must registered beforehand
	// by
	// * using {@link #register(VisualGraphObject)};
	// *
	// * @param name
	// * Identifies the associated template-container
	// * @return A new instance of {@link VisualGraph}.
	// *
	// * @see {@link #createContainer()}
	// */
	// public VisualGraph createContainer(String name) {
	//
	// if (!graphObjectTemplateCollection.isRegistered(name)) {
	// throw new IllegalArgumentException("The graph object associated with '" +
	// name + "' could not be found.  "
	// + "Please load registere the associated graph object first.");
	// }
	//
	// return (VisualGraph) graphObjectTemplateCollection.get(name).deepCopy();
	// }
	//
	// /**
	// * Creates a new instance of {@link VisualGraph}. The new object will be
	// * created by cloning the selected template graph. The template graph must
	// * be registered in advance; the template name must be selected by using
	// * {@link #selectedContainer)};
	// *
	// * @see {@link #createContainer(String))}
	// */
	// public VisualGraph createContainer() {
	//
	// if (selectedNode == null) {
	//
	// throw new IllegalArgumentException("No container was selected");
	// }
	//
	// return createContainer(selectedContainer);
	// }
	//
	// /**
	// * Selects the default node. The default node will be used to create a new
	// * node using {@link #createNode()}
	// *
	// * @param name
	// * the name of the node
	// */
	// public void selectedNode(String name) {
	//
	// this.selectedNode = name;
	// }
	//
	// /**
	// * Returns currently selected node.
	// *
	// * @see #createNode(), {@link #selectedNode(String)}
	// */
	// public String getSelectedNode() {
	//
	// return selectedNode;
	// }
	//
	// /**
	// * Selects the default edge. The default edge will be used to create a new
	// * edge using {@link #createEdge()}
	// *
	// * @param name
	// * the name of the edge
	// */
	// public void selectedEdge(String name) {
	//
	// this.selectedEdge = name;
	// }
	//
	// /**
	// * Returns currently selected edge.
	// *
	// * @see #createEdge(), {@link #selectedEdge(String)}
	// */
	// public String getSelectedEdge() {
	//
	// return selectedEdge;
	// }
	//
	// /**
	// * Selects the default container. The default container will be used by
	// * {@link #createContainer()}.
	// *
	// * @param name
	// * the name of the node
	// */
	// public void selectedContainer(String name) {
	//
	// this.selectedContainer = name;
	// }
	//
	// /**
	// * Returns currently selected container.
	// *
	// * @see #createContainer(), {@link #selectedContainer(String)}
	// */
	// public String getSelectedContainer() {
	//
	// return selectedContainer;
	// }

	private PropertyList getObjectDefinition(String name) {

		// fetch the definition for the object
		if (!shapeDefinitionCollection.contains(name)) {
			throw new IllegalArgumentException("The graph object associated with '" + name + "' could not be find. ");
		}
		return shapeDefinitionCollection.get(name);
	}

	private PropertyList getFormDefinition(PropertyList definition) {

		// check if the form is represented by an shapeID
		final String formID = PropertyUtil.getProperty(definition, "formID", "");
		if (!formID.isEmpty()) {
			// got to the shapes pool and find the definition for formID
		} else {
			return PropertyUtil.findPropertyList(definition, "form");
		}
		return null;
	}

	private CompositeLayout getFormLayout(PropertyList form) {

		final String layout = PropertyUtil.getProperty(form, "layout", "");
		if (!layout.isEmpty()) {
			;// use default layout
		}

		return null;
	}

	private String getPresentationID(PropertyList definition) {

		final String presentationID = PropertyUtil.getProperty(definition, "presentationID", "");
		if (!presentationID.isEmpty()) {
			if (!svgDescriptorCollection.contains(presentationID)) {
				throw new RuntimeException("A presentation with id' " + presentationID + "' could not be find");
			}
		}
		return presentationID;
	}

	private String getStyleID(PropertyList definition) {

		final String styleID = PropertyUtil.getProperty(definition, "styleID", "");
		if (!styleID.isEmpty()) {
			if (!svgDescriptorCollection.contains(styleID)) {
				throw new RuntimeException("The definition for the style  with id' " + styleID + "' could not be find");
			}
		}
		return styleID;
	}

	private VisualGraphObject getTemplate(PropertyList definition) {

		final String template = PropertyUtil.getProperty(definition, "template", "");
		if (!template.isEmpty()) {
			if (!graphObjectTemplateCollection.isRegistered(template)) {
				throw new RuntimeException("A template with id '" + template + "' is not registered");
			}
			return (VisualGraphObject) graphObjectTemplateCollection.get(template).deepCopy();
		}
		return null;
	}

}
