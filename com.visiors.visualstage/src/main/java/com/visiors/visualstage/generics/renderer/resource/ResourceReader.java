package com.visiors.visualstage.generics.renderer.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.visiors.visualstage.generics.attribute.PropertyList;
import com.visiors.visualstage.graph.view.Constants;

public class ResourceReader {
	List<PropertyList> nodes = new ArrayList<PropertyList>();
	List<PropertyList> edges = new ArrayList<PropertyList>();
	List<PropertyList> subgraphs = new ArrayList<PropertyList>();
	List<PropertyList> forms = new ArrayList<PropertyList>();
	List<PropertyList> svgDefinitions = new ArrayList<PropertyList>();

	// List<PropertyList> visualCues = new ArrayList<PropertyList>();

	public ResourceReader(String content) {
		PropertyList properties = GraphBuilder.XML2PropertyList(content);
		read(properties);
	}

	public ResourceReader(InputStream is) throws IOException, ParserConfigurationException,
			SAXException {

		XMLService xmlService = new XMLService();
		PropertyList properties = xmlService.XML2PropertyList(is);
		read(properties);

	}

	void read(PropertyList properties) {

		for (int i = 0; i < properties.size(); i++) {
			PropertyList p = (PropertyList) properties.get(i);
			if (Constants.NODE_SECTION_TAG.equals(p.getName())) {
				grabNodes(p);
			} else if (Constants.EDGE_SECTION_TAG.equals(p.getName())) {
				graphEdges(p);
			} else if (Constants.SUBGRAPH_SECTION_TAG.equals(p.getName())) {
				graphSubgraphs(p);
			} else if (Constants.SVG_DEFINITION_SECTION_TAG.equals(p.getName())) {
				objectSVGDefinition(p);
			} else if (Constants.SVG_FORMS_SECTION_TAG.equals(p.getName())) {
				formDefinition(p);
			} else {
				System.err.println("unknown tag: " + p.getName());
			}
		}
	}

	private void grabNodes(PropertyList properties) {
		for (int i = 0; i < properties.size(); i++) {
			final PropertyList p = (PropertyList) properties.get(i);
			if (Constants.NODE_PROPERTY_PREFIX.equals(p.getName())) {
				nodes.add(p);
			} else {
				System.err.println("Unexpected tag '" + p.getName() + "' in the section '"
						+ Constants.NODE_SECTION_TAG + "'");
			}
		}
	}

	private void graphEdges(PropertyList properties) {
		for (int i = 0; i < properties.size(); i++) {
			final PropertyList p = (PropertyList) properties.get(i);
			if (Constants.EDGE_PROPERTY_PREFIX.equals(p.getName())) {
				edges.add(p);
			} else {
				System.err.println("Unexpected tag '" + p.getName() + "' in the section '"
						+ Constants.EDGE_SECTION_TAG + "'");
			}
		}
	}

	private void graphSubgraphs(PropertyList properties) {
		for (int i = 0; i < properties.size(); i++) {
			final PropertyList p = (PropertyList) properties.get(i);
			if (Constants.GRAPH_PROPERTY_PREFIX.equals(p.getName())) {
				subgraphs.add(p);
			} else {
				System.err.println("Unexpected tag '" + p.getName() + "' in the section '"
						+ Constants.SUBGRAPH_SECTION_TAG + "'");
			}
		}
	}

	private void formDefinition(PropertyList properties) {
		for (int i = 0; i < properties.size(); i++) {
			final PropertyList p = (PropertyList) properties.get(i);
			if (Constants.SVG_FORM_SECTION_TAG.equals(p.getName())) {
				forms.add(p);
			} else {
				System.err.println("Unexpected tag '" + p.getName() + "' in the section '"
						+ Constants.SVG_FORM_SECTION_TAG + "'");
			}
		}
	}

	private void objectSVGDefinition(PropertyList properties) {
		for (int i = 0; i < properties.size(); i++) {
			svgDefinitions.add((PropertyList) properties.get(i));
		}
	}

	// private void visualCues(PropertyList properties) {
	// for (int i = 0; i < properties.size(); i++) {
	// PropertyList p = (PropertyList) properties.get(i);
	// if(Constants.CUE_TAG.equals( p.getName()))
	// visualCues.add((PropertyList) properties.get(i));
	// else
	// System.err.println("Unexpected tag '" + p.getName() +
	// "' in the section '"
	// + Constants.VISUAL_CUES_SECTION_TAG + "'");
	// }
	// }

	public List<PropertyList> getNodeTemplates() {
		return nodes;
	}

	public List<PropertyList> getEdgeTemplates() {
		return edges;
	}

	public List<PropertyList> getSubgraphTemplates() {
		return subgraphs;
	}

	public List<PropertyList> getFormTemplates() {
		return forms;
	}

	public List<PropertyList> getSVGDefinitions() {
		return svgDefinitions;
	}
	//
	//
	// public List<PropertyList> getVisualCues() {
	// return visualCues;
	// }

}
