package com.visiors.visualstage.factory;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.visiors.visualstage.BindingModule;
import com.visiors.visualstage.GraphEditorBindingModule;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.listener.GraphDocumentListener;
import com.visiors.visualstage.exception.BuildException;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.GroupingHandler;
import com.visiors.visualstage.handler.SelectionHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.io.XMLService;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.resource.SVGDefinition;
import com.visiors.visualstage.resource.SVGDefinitionPool;
import com.visiors.visualstage.stage.interaction.InteractionHandler;
import com.visiors.visualstage.stage.interaction.listener.InteractionListener;
import com.visiors.visualstage.util.PropertyUtil;


public class GraphEditor implements Editor, GraphDocumentListener, InteractionListener {

	protected GraphDocument activeDocument;
	protected final Injector injector;
	protected final InteractionHandler interactionHandler;
	protected final UndoRedoHandler undoRedoHandler;
	protected final SelectionHandler selectionHandler;
	protected final GroupingHandler groupingHandler;
	protected final ClipboardHandler clipboardHandler;

	private String selectedMasterContainer;
	private String selectedMasterNode;
	private String selectedMasterEdge;
	private final Map<String, GraphDocument> documents = new HashMap<String, GraphDocument>();
	private final Map<String, VisualGraphObject> registeredGraphObjects = new HashMap<String, VisualGraphObject>();

	public GraphEditor() {

		this(new GraphEditorBindingModule());
	}

	public GraphEditor(BindingModule module) {

		this.injector = Guice.createInjector(module);
		this.interactionHandler = injector.getInstance(InteractionHandler.class);
		this.undoRedoHandler = injector.getInstance(UndoRedoHandler.class);
		this.selectionHandler = injector.getInstance(SelectionHandler.class);
		this.groupingHandler = injector.getInstance(GroupingHandler.class);
		this.clipboardHandler = injector.getInstance(ClipboardHandler.class);
	}

	@Override
	public GraphDocument newDocument(String name) {

		if (documents.containsKey(name)) {
			throw new RuntimeException("A document with the name '" + name + "' already exists!");
		}
		final GraphDocument document = injector.getInstance(GraphDocument.class);
		document.addGraphDocumentListener(this);
		documents.put(name, document);
		setActiveDocument(name);
		return document;
	}

	@Override
	public GraphDocument loadDocument(String content) throws IOException, ParserConfigurationException, SAXException  {

		final XMLService xmlService = new XMLService();
		final PropertyList properties = xmlService.XML2PropertyList(content);
		final PropertyList documentProperties = (PropertyList) properties
				.get(PropertyConstants.DOCUMENT_PROPERTY_SETTING);
		String name = (String) PropertyUtil.getProperty(properties, PropertyConstants.NODE_PROPERTY_NAME);
		if (documents.containsKey(name)) {
			throw new RuntimeException("A document with the name '" + name + "' already exists!");
		}
		final GraphDocument document = injector.getInstance(GraphDocument.class);
		document.setProperties(documentProperties);
		document.setName(name);
		final VisualGraph visualGraph = document.getGraphView();
		propertyList2VisualObjects(properties, visualGraph, false);
		document.addGraphDocumentListener(this);
		documents.put(document.getName(), document);
		setActiveDocument(name);
		return document;
	}

	@Override
	public String save(GraphDocument document) throws IOException {

		final PropertyList documentProperties = document.getProperties();
		visualObjects2ProperyList(document.getGraphView().getGraphObjects(), documentProperties);
		final XMLService xmlService = new XMLService();
		return xmlService.propertyList2XML(documentProperties, true);
	}

	@Override
	public void setActiveDocument(String name) {

		if (!documents.containsKey(name)) {
			throw new RuntimeException("Unknown document: " + name);
		}
		activeDocument = documents.get(name);
		interactionHandler.setScope(activeDocument);
		clipboardHandler.setScope(activeDocument);
		selectionHandler.setScope(activeDocument);
		groupingHandler.setScope(activeDocument);
	}

	@Override
	public GraphDocument getActiveDocument() {

		return activeDocument;
	}

	public List<GraphDocument> getAllDocuments() {

		return new ArrayList<GraphDocument>(documents.values());
	}

	@Override
	public ClipboardHandler getClipboardHandler() {

		return clipboardHandler;
	}

	@Override
	public SelectionHandler getSelectionHandler() {

		return selectionHandler;
	}

	@Override
	public GroupingHandler getGroupingHandler() {

		return groupingHandler;
	}

	@Override
	public UndoRedoHandler getUndoRedoHandler() {

		return undoRedoHandler;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		throw new CloneNotSupportedException();
	}

	/**
	 * Registers a {@link VisualNode} that will server as a master copy for
	 * construction similar visual nodes.
	 * 
	 * @param node
	 *            A concrete implementation of {@link VisualNode}
	 */
	@Override
	public void register(VisualNode node) {

		registeredGraphObjects.put(node.getName(), node);
	}

	/**
	 * Registers a {@link VisualEdge} that will server as a master copy for
	 * construction similar visual edges.
	 * 
	 * @param edge
	 *            A concrete implementation of {@link VisualEdge}
	 */
	@Override
	public void register(VisualEdge edge) {

		registeredGraphObjects.put(edge.getName(), edge);
	}

	/**
	 * Registers a {@link VisualGraph} that will server as a master copy for
	 * construction similar visual graphs.
	 * 
	 * @param subgraph
	 *            A concrete implementation of {@link VisualGraph}
	 */
	@Override
	public void register(VisualGraph subgraph) {

		registeredGraphObjects.put(subgraph.getName(), subgraph);
	}

	/**
	 * This method reads the given inputstream and extract definitions for
	 * VisualNodes, VisualEdges, VisualGraphs and Shapes. The extracted object
	 * definitions can be used as master copies for constructing similar
	 * objects.
	 * 
	 * @param stream
	 *            inputstream that contains XML definition of graph objects such
	 *            as visual nodes, edges, subgraph etc.
	 * 
	 * @see {@link #createEdge(long, String)}, {@link #createEdge(long, String)}
	 *      , {@link #createContainer(long, String)}
	 */
	@Override
	public void loadResources(InputStream stream) {

		try {
			final ResourceReader reader = new ResourceReader(stream);
			poolNodeDefinitions(reader.getNodeDefinitions());
			poolEdgeDefinitions(reader.getEdgeDefinitions());
			poolContainerDefinitions(reader.getSubgraphDefinitions());
			poolSVGDefinitions(reader.getSVGDefinitions());

			reader.release();

		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SAXException e) {
			throw new RuntimeException("failed to parse the XML definition. Reason:  " + e.getMessage());
		} catch (final BuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns true if the graph object specified by <code>name</code> is
	 * already registered; otherwise false.
	 */
	@Override
	public boolean isRegistered(String name) {

		return registeredGraphObjects.containsKey(name);
	}

	/**
	 * Creates a new instance of {@link VisualNode} by cloning the master-node
	 * identified by the given <code>name</code>.
	 * 
	 * <p>
	 * <b>Note:</b> The master-copy must be registered before this method can be
	 * called. To register a master-node directly use
	 * {@link #register(VisualNode)}; or use {@link #loadResources(InputStream)}
	 * to register one or more master-nodes using their XML based definition.
	 * </p>
	 * 
	 * @param name
	 *            Identifies the mater-node that must be cloned to create the
	 *            new node.
	 * @param id
	 *            The identifier for the new node. The id must be unique within
	 *            the graph. The graph assigns an unique id to this object
	 *            automatically if the give id is -1.
	 * @return A new instance of {@link VisualNode}.
	 * 
	 * @see {@link #createNode()}
	 */
	@Override
	public VisualNode createNode(long id, String name) {

		if (!registeredGraphObjects.containsKey(name)) {
			throw new IllegalArgumentException("The node '" + name
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the node's descriptor, or registere the associated node.");
		}

		final VisualNode template = (VisualNode) registeredGraphObjects.get(name);
		return (VisualNode) template.deepCopy(id);
	}

	/**
	 * Creates a new instance of {@link VisualNode} by cloning the master node
	 * which is currently selected as default. To select the default master node
	 * use {@link #selectedMasterNode(String)}
	 * 
	 * @return A new instance of {@link VisualNode}.
	 * 
	 * @see #createNode(long, String)
	 */
	@Override
	public VisualNode createNode() {

		if (!registeredGraphObjects.containsKey(selectedMasterNode)) {
			throw new IllegalArgumentException("The default node '" + selectedMasterNode
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the node's descriptor, or registere the associated node.");
		}

		final VisualNode template = (VisualNode) registeredGraphObjects.get(selectedMasterNode);
		return (VisualNode) template.deepCopy(-1);
	}

	/**
	 * Creates a new instance of {@link VisualEdge} by cloning the master-edge
	 * identified by the given <code>name</code>.
	 * 
	 * <p>
	 * <b>Note:</b> The master-copy must be registered before this method can be
	 * called. To register a master-edge directly use
	 * {@link #register(VisualEdge)}; or use {@link #loadResources(InputStream)}
	 * to register one or more master-edges using their XML based definition.
	 * </p>
	 * 
	 * @param name
	 *            Identifies the mater-edge that must be cloned to create the
	 *            new edge.
	 * @param id
	 *            The identifier for the new edge. The id must be unique within
	 *            the graph. The graph assigns an unique id to this object
	 *            automatically if the give id is -1.
	 * @return A new instance of {@link VisualEdge}.
	 * 
	 * @see {@link #createEdge()}
	 */
	@Override
	public VisualEdge createEdge(long id, String name) {

		if (!registeredGraphObjects.containsKey(name)) {
			throw new IllegalArgumentException("The edge '" + name
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the edge's descriptor, or registere the associated edge.");
		}
		final VisualEdge template = (VisualEdge) registeredGraphObjects.get(name);
		return (VisualEdge) template.deepCopy(id);
	}

	/**
	 * Creates a new instance of {@link VisualEdge} by cloning the master edge
	 * which is currently selected as default. To select the default master edge
	 * use {@link #selectedMasterEdge}
	 * 
	 * @return A new instance of {@link VisualEdge}.
	 * 
	 * @see #createEdge(long, String)
	 */
	@Override
	public VisualEdge createEdge() {

		if (!registeredGraphObjects.containsKey(selectedMasterEdge)) {
			throw new IllegalArgumentException("The default-edge '" + selectedMasterEdge
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the edge's descriptor, or registere the associated edge.");
		}
		final VisualEdge template = (VisualEdge) registeredGraphObjects.get(selectedMasterEdge);
		return (VisualEdge) template.deepCopy(-1);
	}

	/**
	 * Creates a new instance of {@link VisualGraph} by cloning the
	 * master-container identified by the given <code>name</code>.
	 * 
	 * <p>
	 * <b>Note:</b> The master-copy must be registered before this method can be
	 * called. To register master-container directly use
	 * {@link #register(VisualGraph)}; or use
	 * {@link #loadResources(InputStream)} to register one or more
	 * master-containers using their XML based definition.
	 * </p>
	 * 
	 * @param name
	 *            Identifies the mater-container that must be cloned to create
	 *            the new container.
	 * @param id
	 *            The identifier for the new container. The id must be unique
	 *            within the graph. The graph assigns an unique id to this
	 *            object automatically if the give id is -1.
	 * @return A new instance of {@link VisualGraph}.
	 * 
	 * @see {@link #createContainer()}
	 */
	@Override
	public VisualGraph createContainer(long id, String name) {

		if (!registeredGraphObjects.containsKey(name)) {
			throw new IllegalArgumentException("The subgraph '" + name
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the subgraph's descriptor, or registere the associated object.");
		}
		final VisualGraph template = (VisualGraph) registeredGraphObjects.get(name);
		return (VisualGraph) template.deepCopy(id);
	}

	/**
	 * Creates a new instance of {@link VisualGraph} by cloning the master
	 * container which is currently selected as default. To select the default
	 * master container use {@link #selectedMasterContainer}
	 * 
	 * @return A new instance of {@link VisualGraph}.
	 * 
	 * @see #createContainer(long, String)
	 */
	@Override
	public VisualGraph createContainer() {

		if (!registeredGraphObjects.containsKey(selectedMasterContainer)) {
			throw new IllegalArgumentException("The default subgraph '" + selectedMasterContainer
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the subgraph's descriptor, or registere the associated object.");
		}
		final VisualGraph template = (VisualGraph) registeredGraphObjects.get(selectedMasterContainer);
		return (VisualGraph) template.deepCopy(-1);
	}

	/**
	 * Selects the default master node. The default master node will be used to
	 * create a new node using {@link #createNode()}
	 * 
	 * @param name
	 *            the name of the master node
	 */
	@Override
	public void selectedMasterNode(String name) {

		this.selectedMasterNode = name;
	}

	/**
	 * Returns currently selected master node.
	 * 
	 * @see #createNode(), {@link #selectedMasterNode(String)}
	 */
	@Override
	public String getSelectedMasterNode() {

		return selectedMasterNode;
	}

	/**
	 * Selects the default master edge. The default master edge will be used to
	 * create a new edge using {@link #createEdge()}
	 * 
	 * @param name
	 *            the name of the master edge
	 */
	@Override
	public void selectedMasterEdge(String name) {

		this.selectedMasterEdge = name;
	}

	/**
	 * Returns currently selected master edge.
	 * 
	 * @see #createEdge(), {@link #selectedMasterEdge(String)}
	 */
	@Override
	public String getSelectedMasterEdge() {

		return selectedMasterEdge;
	}

	/**
	 * Selects the default master container. The default master container will
	 * be used by {@link #createContainer()}.
	 * 
	 * @param name
	 *            the name of the master node
	 */
	@Override
	public void selectedMasterContainer(String name) {

		this.selectedMasterContainer = name;
	}

	/**
	 * Returns currently selected master container.
	 * 
	 * @see #createContainer(), {@link #selectedMasterContainer(String)}
	 */
	@Override
	public String getSelectedMasterContainer() {

		return selectedMasterContainer;
	}

	// /------------------------------

	private void poolSVGDefinitions(List<PropertyList> presentation) throws BuildException {

		for (final PropertyList template : presentation) {
			loadSVGDefinitions(template);
		}
	}

	private void loadSVGDefinitions(PropertyList defintion) {

		final SVGDefinition def = new SVGDefinition(defintion);
		SVGDefinitionPool.pool(def.id, def);
	}

	// //////review ///////////////////////////////////////

	@Override
	public void loadAndPoolSVG(String id, InputStream data) {

		try {
			final SVGDefinition p = new SVGDefinition(id, data);
			SVGDefinitionPool.pool(id, p);

		} catch (final Exception e) {
			// TODO: handle exception

			System.err.println("'" + id + "' could not be loaded. Reason: " + e.getMessage());

		}
	}

	private void poolContainerDefinitions(List<PropertyList> containersPropertyList) {

		for (final PropertyList properties : containersPropertyList) {

			final VisualGraph container = injector.getInstance(VisualGraph.class);
			container.setProperties(properties);
			register(container);
			if (selectedMasterContainer == null) {
				selectedMasterContainer = (String) PropertyUtil.getProperty(properties,
						PropertyConstants.GRAPH_PROPERTY_NAME);
			}
		}
	}

	private void poolEdgeDefinitions(List<PropertyList> edgesPropertyList) {

		for (final PropertyList properties : edgesPropertyList) {
			final VisualEdge edge = injector.getInstance(VisualEdge.class);
			edge.setProperties(properties);
			register(edge);
			if (selectedMasterEdge == null) {
				selectedMasterEdge = (String) PropertyUtil
						.getProperty(properties, PropertyConstants.EDGE_PROPERTY_NAME);
			}
		}
	}

	private void poolNodeDefinitions(List<PropertyList> nodesPropertyList) {

		for (final PropertyList properties : nodesPropertyList) {
			final VisualNode node = injector.getInstance(VisualNode.class);
			node.setProperties(properties);
			register(node);
			if (selectedMasterNode == null) {
				selectedMasterNode = (String) PropertyUtil
						.getProperty(properties, PropertyConstants.NODE_PROPERTY_NAME);
			}
		}
	}

	/**
	 * Creates the PropertyList which contains all properties of given
	 * VisualObjects.
	 * 
	 * @param GraphObjectViews
	 * @param properties
	 */
	@Override
	public void visualObjects2ProperyList(List<VisualGraphObject> GraphObjectViews, PropertyList properties) {

		final PropertyList edgesProperties = new DefaultPropertyList(PropertyConstants.EDGE_SECTION_TAG);
		final PropertyList nodesProperties = new DefaultPropertyList(PropertyConstants.NODE_SECTION_TAG);
		final PropertyList subgraphProperties = new DefaultPropertyList(PropertyConstants.SUBGRAPH_SECTION_TAG);

		for (final VisualGraphObject vo : GraphObjectViews) {
			if (vo instanceof VisualGraph) {
				visualObjects2ProperyList(((VisualGraph) vo).getGraphObjects(), vo.getProperties());
				subgraphProperties.add(subgraphProperties);
			} else if (vo instanceof VisualNode) {

				nodesProperties.add(vo.getProperties());
			} else if (vo instanceof VisualEdge) {
				edgesProperties.add(vo.getProperties());
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

	@Override
	public void propertyList2VisualObjects(PropertyList properties, VisualGraph visualGraph, boolean reassingId) {

		final Map<Long, Long> oldNewIdMap = new HashMap<Long, Long>();

		final PropertyList edgesProperties = (PropertyList) properties.get(PropertyConstants.EDGE_SECTION_TAG);
		final PropertyList nodesProperties = (PropertyList) properties.get(PropertyConstants.NODE_SECTION_TAG);
		final PropertyList groupsProperties = (PropertyList) properties.get(PropertyConstants.SUBGRAPH_SECTION_TAG);

		if (groupsProperties != null) {
			for (int i = 0; i < groupsProperties.size(); i++) {
				final PropertyList subgraphProperties = (PropertyList) groupsProperties.get(i);
				createSubgraph(subgraphProperties, visualGraph, reassingId, oldNewIdMap);
			}
		}
		if (nodesProperties != null) {
			for (int i = 0; i < nodesProperties.size(); i++) {
				final PropertyList nodeProperties = (PropertyList) nodesProperties.get(i);
				createNode(nodeProperties, visualGraph, reassingId, oldNewIdMap);
			}
		}
		if (edgesProperties != null) {
			for (int i = 0; i < edgesProperties.size(); i++) {
				final PropertyList edgeProperties = (PropertyList) edgesProperties.get(i);
				createEdge(edgeProperties, visualGraph, reassingId, oldNewIdMap);
			}
		}
	}

	@Override
	public List<VisualGraphObject> createGraphObjects(PropertyList properties, VisualGraph rootContainer,
			boolean reassingId) {

		final Map<Long, Long> oldNewIdMap = new HashMap<Long, Long>();

		final List<VisualGraphObject> result = new ArrayList<VisualGraphObject>();

		final PropertyList edgesProperties = (PropertyList) properties.get(PropertyConstants.EDGE_SECTION_TAG);
		final PropertyList nodesProperties = (PropertyList) properties.get(PropertyConstants.NODE_SECTION_TAG);
		final PropertyList groupsProperties = (PropertyList) properties.get(PropertyConstants.SUBGRAPH_SECTION_TAG);

		if (groupsProperties != null) {
			for (int i = 0; i < groupsProperties.size(); i++) {
				final PropertyList groupProperties = (PropertyList) groupsProperties.get(i);
				final VisualGraph subgraph = createSubgraph(groupProperties, rootContainer, reassingId, oldNewIdMap);
				result.add(subgraph);
			}
		}
		if (nodesProperties != null) {
			for (int i = 0; i < nodesProperties.size(); i++) {
				final PropertyList nodeProperties = (PropertyList) nodesProperties.get(i);
				final VisualNode node = createNode(nodeProperties, rootContainer, reassingId, oldNewIdMap);
				result.add(node);
			}
		}
		if (edgesProperties != null) {
			for (int i = 0; i < edgesProperties.size(); i++) {
				final PropertyList edgeProperties = (PropertyList) edgesProperties.get(i);
				final VisualEdge edge = createEdge(edgeProperties, rootContainer, reassingId, oldNewIdMap);
				result.add(edge);
			}
		}
		return result;
	}

	private VisualGraph createSubgraph(PropertyList properties, VisualGraph rootContainer, boolean reassingId,
			Map<Long, Long> oldNewIdMap) {

		final String name = PropertyUtil.getProperty(properties, PropertyConstants.GRAPH_PROPERTY_NAME, "");
		long previousParentId = PropertyUtil.getProperty(properties, PropertyConstants.GRAPH_PROPERTY_PARENT_ID, -1L);
		final long previousID = PropertyUtil.getProperty(properties, PropertyConstants.GRAPH_PROPERTY_ID, -1L);
		final VisualGraph graph = createContainer(reassingId ? -1 : previousID, name);
		final long newID = graph.getID();

		// find the parent container and add this object to it.
		if (reassingId) {
			// keep track of old and new id
			oldNewIdMap.put(new Long(previousID), new Long(newID));
			// parent container might have a new ID
			if (oldNewIdMap.containsKey(new Long(previousParentId))) {
				previousParentId = oldNewIdMap.get(new Long(previousParentId)).longValue();
			}
		}

		// find the parent container and add this object into it.
		final VisualGraph parentContainer = findParentContainer(rootContainer, previousParentId);
		parentContainer.add(graph);

		final PropertyList groupProperties = properties.deepCopy();
		groupProperties.remove(PropertyConstants.EDGE_SECTION_TAG);
		groupProperties.remove(PropertyConstants.NODE_SECTION_TAG);
		groupProperties.remove(PropertyConstants.SUBGRAPH_SECTION_TAG);
		graph.setProperties(groupProperties);

		// create members
		propertyList2VisualObjects(properties, graph, reassingId);

		return graph;
	}

	private VisualNode createNode(PropertyList properties, VisualGraph rootContainer, boolean reassingId,
			Map<Long, Long> oldNewIdMap) {

		final String name = PropertyUtil.getProperty(properties, PropertyConstants.NODE_PROPERTY_NAME, "");
		long previousParentId = PropertyUtil.getProperty(properties, PropertyConstants.NODE_PROPERTY_PARENT_ID, -1L);
		final long previousID = PropertyUtil.getProperty(properties, PropertyConstants.NODE_PROPERTY_ID, -1L);
		final VisualNode node = createNode(reassingId ? -1 : previousID, name);
		final long newID = node.getID();
		node.setProperties(properties);

		// find the parent container and add this edge to it.
		if (reassingId) {
			// keep track of old and new id
			oldNewIdMap.put(new Long(previousID), new Long(newID));
			// parent container might have a new ID
			if (oldNewIdMap.containsKey(new Long(previousParentId))) {
				previousParentId = oldNewIdMap.get(new Long(previousParentId)).longValue();
			}
		}

		// find the parent container and add this node into it.
		final VisualGraph parentContainer = findParentContainer(rootContainer, previousParentId);
		parentContainer.add(node);
		return node;
	}

	private VisualEdge createEdge(PropertyList properties, VisualGraph rootContainer, boolean reassingId,
			Map<Long, Long> oldNewIdMap) {

		final String name = PropertyUtil.getProperty(properties, PropertyConstants.EDGE_PROPERTY_NAME, "");
		long previousParentId = PropertyUtil.getProperty(properties, PropertyConstants.EDGE_PROPERTY_PARENT_ID, -1L);
		final long previousID = PropertyUtil.getProperty(properties, PropertyConstants.EDGE_PROPERTY_ID, -1L);
		final VisualEdge edge = createEdge(reassingId ? -1 : previousID, name);
		final long newID = edge.getID();

		if (reassingId) {
			// keep track of old and new id
			oldNewIdMap.put(new Long(previousID), new Long(newID));
			// parent container might have a new ID
			if (oldNewIdMap.containsKey(new Long(previousParentId))) {
				previousParentId = oldNewIdMap.get(new Long(previousParentId)).longValue();
			}
		}
		// find the parent container and add this edge to it.
		final VisualGraph parentContainer = findParentContainer(rootContainer, previousParentId);
		parentContainer.add(edge);
		edge.setProperties(properties);

		// connect to source node
		long sourceID = PropertyUtil.getProperty(properties, PropertyConstants.EDGE_PROPERTY_SOURCE, -1L);
		if (sourceID != -1) {
			if (reassingId && oldNewIdMap.containsKey(new Long(sourceID))) {
				sourceID = oldNewIdMap.get(new Long(sourceID)).longValue();
			}
		}
		final VisualNode sourceNode = rootContainer.getNode(sourceID);
		final int sourcePortId = PropertyUtil.getProperty(properties, PropertyConstants.EDGE_PROPERTY_SOURCE_PORT, -1);

		// connect to target node
		long targetID = PropertyUtil.getProperty(properties, PropertyConstants.EDGE_PROPERTY_TARGET, -1L);
		if (targetID != -1) {
			if (reassingId && oldNewIdMap.containsKey(new Long(targetID))) {
				targetID = oldNewIdMap.get(new Long(targetID)).longValue();
			}
		}
		final VisualNode targetNode = rootContainer.getNode(targetID);
		final int targetPortId = PropertyUtil.getProperty(properties, PropertyConstants.EDGE_PROPERTY_TARGET_PORT, -1);

		edge.connect(sourceNode, sourcePortId, targetNode, targetPortId);

		return edge;
	}

	private VisualGraph findParentContainer(VisualGraph rootContainer, long parentId) {

		if (parentId == -1 || parentId == rootContainer.getID()) {
			return rootContainer;
		}

		final List<VisualNode> nodes = rootContainer.getNodes();
		for (final VisualNode node : nodes) {
			if (node instanceof VisualGraph) {
				final VisualGraph pc = findParentContainer((VisualGraph) node, parentId);
				if (pc != null) {
					return pc;
				}
			}
		}

		return null;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		return interactionHandler.keyPressed(keyChar, keyCode);

	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		return interactionHandler.keyReleased(keyChar, keyCode);
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return interactionHandler.mouseDoubleClicked(pt, button, functionKey);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		return interactionHandler.mouseDragged(pt, button, functionKey);
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return interactionHandler.mouseMoved(pt, button, functionKey);
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		return interactionHandler.mousePressed(pt, button, functionKey);
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return interactionHandler.mouseReleased(pt, button, functionKey);
	}

	@Override
	public boolean isInteracting() {

		return interactionHandler.isInteracting();
	}

	@Override
	public void cancelInteraction() {

		interactionHandler.cancelInteraction();
	}

	@Override
	public void terminateInteraction() {

		interactionHandler.cancelInteraction();
	}

	@Override
	public int getPreferredCursor() {

		return interactionHandler.getPreferredCursor();
	}

	@Override
	public void interactionModeChanged(String previousHandler, String currnetHandler) {


	}

	@Override
	public void undoStackModified() {

		// TODO Auto-generated method stub

	}

	@Override
	public void viewChanged() {

		// TODO Auto-generated method stub

	}

	@Override
	public void graphManipulated() {

		// TODO Auto-generated method stub

	}

	@Override
	public void graphExpansionChanged(Rectangle newBoundary) {

		// TODO Auto-generated method stub

	}
}
