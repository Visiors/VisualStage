package com.visiors.visualstage.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.stage.cache.GraphObjectView;
import com.visiors.visualstage.util.PropertyUtil;

public class GraphBuilder {

	// map to track new ids
	// private static Map<Object, GraphObjectView> idMap = new HashMap<Object,
	// GraphObjectView>();

	private static Map<Long, Long> old2newID = new HashMap<Long, Long>();

	public static String save(GraphDocument graphDocument, OutputStream stream) throws IOException {

		PropertyList documentProperties = graphDocument.getProperties();
		visualObjects2ProperyList(graphDocument.getGraphView().getGraphObjects(),
				documentProperties);
		XMLService xmlService = new XMLService();
		xmlService.propertyList2XML(stream, documentProperties, true);
		stream.flush();
		return stream.toString();
	}

	public static void load(GraphDocument graphDocument, InputStream stream) throws IOException,
			ParserConfigurationException, SAXException {

		XMLService xmlService = new XMLService();
		PropertyList properties = xmlService.XML2PropertyList(stream);
		PropertyList documentProperties = (PropertyList) properties
				.get(PropertyConstants.DOCUMENT_PROPERTY_SETTING);
		graphDocument.setProperties(documentProperties);
		VisualGraph graphView = graphDocument.getGraphView();
		graphView.clear();
		GraphBuilder.old2newID.clear();
		propertyList2VisualObjects(properties, graphView, false);
		graphDocument.setUpdateView(true);
	}

	public static String propertyList2XML(PropertyList properties, boolean insertHeader) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			XMLService xmlService = new XMLService();
			xmlService.propertyList2XML(bos, properties, insertHeader);
			bos.flush();
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PropertyList XML2PropertyList(String strXML) {

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(strXML.getBytes());
			XMLService xmlService = new XMLService();
			return xmlService.XML2PropertyList(bis);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates the PropertyList which contains all properties of given
	 * VisualObjects.
	 * 
	 * @param GraphObjectViews
	 * @param properties
	 */
	public static void visualObjects2ProperyList(VisualGraphObject[] GraphObjectViews,
			PropertyList properties) {

		PropertyList edgesProperties = new DefaultPropertyList(PropertyConstants.EDGE_SECTION_TAG);
		PropertyList nodesProperties = new DefaultPropertyList(PropertyConstants.NODE_SECTION_TAG);
		PropertyList subgraphProperties = new DefaultPropertyList(
				PropertyConstants.SUBGRAPH_SECTION_TAG);

		for (VisualGraphObject vo : GraphObjectViews) {

			if (vo instanceof VisualGraph) {
				appendGraphProperties((VisualGraph) vo, subgraphProperties);
			} else if (vo instanceof VisualNode) {
				appendNodeProperties((VisualNode) vo, nodesProperties);
			} else if (vo instanceof VisualEdge) {
				appendEdgeProperties((VisualEdge) vo, edgesProperties);
			}
		}

		if (edgesProperties.size() != 0) {
			properties.add(edgesProperties);
		}
		if (nodesProperties.size() != 0) {
			properties.add(nodesProperties);
		}
		if (subgraphProperties.size() != 0) {
			properties.add(subgraphProperties);
		}
	}

	private static void appendNodeProperties(VisualNode node, PropertyList properties) {

		properties.add(node.getProperties());
	}

	private static void appendEdgeProperties(VisualEdge edge, PropertyList properties) {

		properties.add(edge.getProperties());
	}

	private static void appendGraphProperties(VisualGraph graph, PropertyList properties) {

		PropertyList subgraphProperties = graph.getProperties();
		visualObjects2ProperyList(graph.getGraphObjects(), subgraphProperties);
		properties.add(subgraphProperties);
	}

	public static void propertyList2VisualObjects(PropertyList properties, VisualGraph graphView,
			boolean assignNewID) {

		PropertyList edgesProperties = (PropertyList) properties
				.get(PropertyConstants.EDGE_SECTION_TAG);
		PropertyList nodesProperties = (PropertyList) properties
				.get(PropertyConstants.NODE_SECTION_TAG);
		PropertyList groupsProperties = (PropertyList) properties
				.get(PropertyConstants.SUBGRAPH_SECTION_TAG);

		if (groupsProperties != null) {
			for (int i = 0; i < groupsProperties.size(); i++) {
				PropertyList subgraphProperties = (PropertyList) groupsProperties.get(i);
				createSubgraph(subgraphProperties, graphView, assignNewID);
			}
		}
		if (nodesProperties != null) {
			for (int i = 0; i < nodesProperties.size(); i++) {
				PropertyList nodeProperties = (PropertyList) nodesProperties.get(i);
				createNode(nodeProperties, graphView, assignNewID);
			}
		}
		if (edgesProperties != null) {
			for (int i = 0; i < edgesProperties.size(); i++) {
				PropertyList edgeProperties = (PropertyList) edgesProperties.get(i);
				createEdge(edgeProperties, graphView, assignNewID);
			}
		}
	}

	public static List<VisualGraphObject> createGraphObjects(PropertyList properties,
			VisualGraph topContainer, boolean assignNewID) {

		GraphBuilder.old2newID.clear();

		List<VisualGraphObject> result = new ArrayList<VisualGraphObject>();

		PropertyList edgesProperties = (PropertyList) properties
				.get(PropertyConstants.EDGE_SECTION_TAG);
		PropertyList nodesProperties = (PropertyList) properties
				.get(PropertyConstants.NODE_SECTION_TAG);
		PropertyList groupsProperties = (PropertyList) properties
				.get(PropertyConstants.SUBGRAPH_SECTION_TAG);

		if (groupsProperties != null) {
			for (int i = 0; i < groupsProperties.size(); i++) {
				PropertyList groupProperties = (PropertyList) groupsProperties.get(i);
				VisualGraph subgraph = createSubgraph(groupProperties, topContainer, assignNewID);
				result.add(subgraph);
			}
		}
		if (nodesProperties != null) {
			for (int i = 0; i < nodesProperties.size(); i++) {
				PropertyList nodeProperties = (PropertyList) nodesProperties.get(i);
				VisualNode node = createNode(nodeProperties, topContainer, assignNewID);
				result.add(node);
			}
		}
		if (edgesProperties != null) {
			for (int i = 0; i < edgesProperties.size(); i++) {
				PropertyList edgeProperties = (PropertyList) edgesProperties.get(i);
				VisualEdge edge = createEdge(edgeProperties, topContainer, assignNewID);
				result.add(edge);
			}
		}
		return result;
	}

	private static VisualGraph createSubgraph(PropertyList properties, VisualGraph subgraph,
			boolean assignNewID) {

		String name = PropertyUtil.getProperty(properties, PropertyConstants.GRAPH_PROPERTY_NAME,
				"");
		long previousParentId = PropertyUtil.getProperty(properties,
				PropertyConstants.GRAPH_PROPERTY_PARENT_ID, -1L);
		long previousID = PropertyUtil.getProperty(properties, PropertyConstants.GRAPH_PROPERTY_ID,
				-1L);
		VisualGraph graph = GraphFactory.instance().createContainer(assignNewID ? -1 : previousID,
				name);
		long newID = graph.getID();

		// find the parent container and add this object to it.
		if (assignNewID) {
			// keep track of old and new id
			GraphBuilder.old2newID.put(new Long(previousID), new Long(newID));
			// parent container might have a new ID
			if (GraphBuilder.old2newID.containsKey(new Long(previousParentId))) {
				previousParentId = GraphBuilder.old2newID.get(new Long(previousParentId))
						.longValue();
			}
		}

		// find the parent container and add this object into it.
		VisualGraph parentContainer = getParentContainer(subgraph, previousParentId);
		parentContainer.addGraphObject(graph);

		PropertyList groupProperties = properties.deepCopy();
		groupProperties.remove(PropertyConstants.EDGE_SECTION_TAG);
		groupProperties.remove(PropertyConstants.NODE_SECTION_TAG);
		groupProperties.remove(PropertyConstants.SUBGRAPH_SECTION_TAG);
		graph.setProperties(groupProperties);

		// create members
		propertyList2VisualObjects(properties, graph, assignNewID);

		return graph;
	}

	public static VisualNode createNode(PropertyList properties, VisualGraph rootContainer,
			boolean assignNewID) {

		String name = PropertyUtil
				.getProperty(properties, PropertyConstants.NODE_PROPERTY_NAME, "");
		long previousParentId = PropertyUtil.getProperty(properties,
				PropertyConstants.NODE_PROPERTY_PARENT_ID, -1L);
		long previousID = PropertyUtil.getProperty(properties, PropertyConstants.NODE_PROPERTY_ID,
				-1L);
		VisualNode node = GraphFactory.instance().createNode(assignNewID ? -1 : previousID, name);
		long newID = node.getID();
		node.setProperties(properties);

		// find the parent container and add this edge to it.
		if (assignNewID) {
			// keep track of old and new id
			GraphBuilder.old2newID.put(new Long(previousID), new Long(newID));
			// parent container might have a new ID
			if (GraphBuilder.old2newID.containsKey(new Long(previousParentId))) {
				previousParentId = GraphBuilder.old2newID.get(new Long(previousParentId))
						.longValue();
			}
		}

		// find the parent container and add this node into it.
		VisualGraph parentContainer = getParentContainer(rootContainer, previousParentId);
		parentContainer.addGraphObject(node);
		return node;
	}

	private static VisualEdge createEdge(PropertyList properties, VisualGraph rootContainer,
			boolean assignNewID) {

		String name = PropertyUtil
				.getProperty(properties, PropertyConstants.EDGE_PROPERTY_NAME, "");
		long previousParentId = PropertyUtil.getProperty(properties,
				PropertyConstants.EDGE_PROPERTY_PARENT_ID, -1L);
		long previousID = PropertyUtil.getProperty(properties, PropertyConstants.EDGE_PROPERTY_ID,
				-1L);
		VisualEdge edge = GraphFactory.instance().createEdge(assignNewID ? -1 : previousID, name);
		long newID = edge.getID();

		if (assignNewID) {
			// keep track of old and new id
			GraphBuilder.old2newID.put(new Long(previousID), new Long(newID));
			// parent container might have a new ID
			if (GraphBuilder.old2newID.containsKey(new Long(previousParentId))) {
				previousParentId = GraphBuilder.old2newID.get(new Long(previousParentId))
						.longValue();
			}
		}
		// find the parent container and add this edge to it.
		VisualGraph parentContainer = getParentContainer(rootContainer, previousParentId);
		parentContainer.addGraphObject(edge);
		edge.setProperties(properties);

		// connect to source node
		long sourceID = PropertyUtil.getProperty(properties,
				PropertyConstants.EDGE_PROPERTY_SOURCE, -1L);
		if (sourceID != -1) {

			if (assignNewID && GraphBuilder.old2newID.containsKey(new Long(sourceID))) {
				sourceID = GraphBuilder.old2newID.get(new Long(sourceID)).longValue();
			}
		}
		VisualNode sourceNode = rootContainer.getNode(sourceID);
		if (sourceNode != null) {
			int sourcePortID = PropertyUtil.getProperty(properties,
					PropertyConstants.EDGE_PROPERTY_SOURCE_PORT, -1);
			edge.setSourceNode(sourceNode, sourcePortID);
		}

		// connect to target node
		long targetID = PropertyUtil.getProperty(properties,
				PropertyConstants.EDGE_PROPERTY_TARGET, -1L);
		if (targetID != -1) {

			if (assignNewID && GraphBuilder.old2newID.containsKey(new Long(targetID))) {
				targetID = GraphBuilder.old2newID.get(new Long(targetID)).longValue();
			}
		}
		VisualNode targetNode = rootContainer.getNode(targetID);
		if (targetNode != null) {
			int targetPortID = PropertyUtil.getProperty(properties,
					PropertyConstants.EDGE_PROPERTY_TARGET_PORT, -1);
			edge.setTargetNode(targetNode, targetPortID);
		}

		return edge;
	}

	private static VisualGraph getParentContainer(VisualGraph rootContainer, long parentId) {

		if (parentId == -1 || parentId == rootContainer.getID()) {
			return rootContainer;
		}

		VisualNode[] nodes = rootContainer.getNodes();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] instanceof VisualGraph) {
				VisualGraph pc = getParentContainer((VisualGraph) nodes[i], parentId);
				if (pc != null) {
					return pc;
				}
			}
		}

		return null;
	}

}
