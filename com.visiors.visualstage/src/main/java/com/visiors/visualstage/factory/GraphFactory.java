package com.visiors.visualstage.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.exception.BuildException;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.DefaultVisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.impl.DefaultVisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.graph.view.node.impl.DefaultVisualNode;
import com.visiors.visualstage.io.GraphBuilder;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.resource.SVGDefinition;
import com.visiors.visualstage.resource.SVGDefinitionPool;

/**
 * <p>
 * This factory class is used to create instances of all graph objects. By
 * calling the static method {@link #instance()}, you can obtain an instance
 * that is assignable to this class.
 * </p>
 * 
 * <p>
 * The object types created by this factory are:
 * <ul>
 * <li>{@link DefaultVisualNode}</li>
 * <li>{@link DefaultVisualEdge}</li>
 * <li>{@link DefaultVisualGraph} (Representing subgraphs )</li>
 * </ul>
 * </p>
 * <p>
 * To create a new graph object you have to extends one of the abstracts class
 * {@link DefaultVisualNode}, {@link DefaultVisualEdge} or
 * {@link DefaultVisualGraph}. this abstract classed require implementation of
 * the abstract method <code>create()</code> which muss return an instance of
 * the concrete Implementation class.
 * </p>
 * <p>
 * example:
 * </p>
 * 
 * <pre>
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * class MyNode extends DefaultVisualNode {
 * 
 * 	public MyNode(long id) {
 * 
 * 		super(id);
 * 	}
 * 
 * 	public DefaultVisualNode create(long id) {
 * 
 * 		return new MyNode(id);
 * 	}
 * }
 * </pre>
 * <p>
 * Concrete implementation classes can registered by using the method
 * {@link #registerNode}, {@link #registerEdge} or {@link #registerSubgraph}.
 * </p>
 * <p>
 * example:
 * </p>
 * GraphFactory.instance().registerNode("Process", new MyNode());
 */

public class GraphFactory {

	private final Map<String, VisualNode> registeredNodes = new HashMap<String, VisualNode>();
	private final Map<String, VisualEdge> registeredEdges = new HashMap<String, VisualEdge>();
	private final Map<String, VisualGraph> registeredContainer = new HashMap<String, VisualGraph>();
	private String selectedMasterContainer;
	private String selectedMasterNode;
	private String selectedMasterEdge;

	private static GraphFactory m_instance = new GraphFactory();

	/**
	 * This method gives access to this singlton instance of this factory class.
	 * 
	 * @return the instance of the graph factory
	 */
	public static GraphFactory instance() {

		return m_instance;
	}

	private GraphFactory() {

		/* Singleton */
	}

	/**
	 * Registers a {@link VisualNode} that will server as a master copy for
	 * construction similar visual nodes.
	 * 
	 * @param node
	 *            A concrete implementation of {@link VisualNode}
	 */
	public void register(VisualNode node) {

		registeredNodes.put(node.getName(), node);
	}

	/**
	 * Registers a {@link VisualEdge} that will server as a master copy for
	 * construction similar visual edges.
	 * 
	 * @param edge
	 *            A concrete implementation of {@link VisualEdge}
	 */
	public void register(VisualEdge edge) {

		registeredEdges.put(edge.getName(), edge);
	}

	/**
	 * Registers a {@link VisualGraph} that will server as a master copy for
	 * construction similar visual graphs.
	 * 
	 * @param subgraph
	 *            A concrete implementation of {@link VisualGraph}
	 */
	public void register(VisualGraph subgraph) {

		registeredContainer.put(subgraph.getName(), subgraph);
	}

	/**
	 * This method reads the given inputstream and extract definitions for
	 * VisualNodes, VisualEdges, VisualGraphs and Shapes. The extracted object
	 * definitions can be used as master copies for constructing similar
	 * objects.
	 * 
	 * @param stream
	 *            inputstream that contains XML based definition for graph
	 *            objects like visual nodes, edges, subgraph etc.
	 * 
	 * @see {@link #createEdge(long, String)}, {@link #createEdge(long, String)}
	 *      , {@link #createContainer(long, String)}
	 */
	public void loadResources(InputStream stream) {

		ResourceReader reader;
		try {
			reader = new ResourceReader(stream);
			poolNodeDefinitions(reader.extractNodeDefinitions());
			poolEdgeDefinitions(reader.extractEdgeDefinitions());
			poolContainerDefinitions(reader.extractSubgraphDefinitions());

			poolSVGDefinitions(reader.getSVGDefinitions());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			throw new RuntimeException("\nfailed to parse the XML definition. Reason:  " + e.getMessage());
		} catch (BuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns true if a master node with the specified <code>name</code> is
	 * already registered; otherwise it returns false.
	 */
	public boolean isMasterNodeRegistered(String name) {

		return registeredNodes.containsKey(name);
	}

	/**
	 * Returns true if a master edge with the specified <code>name</code> is
	 * already registered; otherwise it returns false.
	 */
	public boolean isMasterEdgeRegistered(String name) {

		return registeredEdges.containsKey(name);
	}

	/**
	 * Returns true if a master container with the specified <code>name</code>
	 * is already registered; otherwise it returns false.
	 */
	public boolean isMasterContainerRegistered(String name) {

		return registeredContainer.containsKey(name);
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
	public VisualNode createNode(long id, String name) {

		if (!registeredNodes.containsKey(name)) {
			throw new IllegalArgumentException("The node '" + name
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the node's descriptor, or registere the associated node.");
		}

		VisualNode template = registeredNodes.get(name);
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
	public VisualNode createNode() {

		if (!registeredNodes.containsKey(selectedMasterNode)) {
			throw new IllegalArgumentException("The default node '" + selectedMasterNode
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the node's descriptor, or registere the associated node.");
		}

		VisualNode template = registeredNodes.get(selectedMasterNode);
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
	public VisualEdge createEdge(long id, String name) {

		if (!registeredEdges.containsKey(name)) {
			throw new IllegalArgumentException("The edge '" + name
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the edge's descriptor, or registere the associated edge.");
		}
		VisualEdge template = registeredEdges.get(name);
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
	public VisualEdge createEdge() {

		if (!registeredEdges.containsKey(selectedMasterEdge)) {
			throw new IllegalArgumentException("The default-edge '" + selectedMasterEdge
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the edge's descriptor, or registere the associated edge.");
		}
		VisualEdge template = registeredEdges.get(selectedMasterEdge);
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
	public VisualGraph createContainer(long id, String name) {

		if (!registeredContainer.containsKey(name)) {
			throw new IllegalArgumentException("The subgraph '" + name
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the subgraph's descriptor, or registere the associated object.");
		}
		VisualGraph template = registeredContainer.get(name);
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
	public VisualGraph createContainer() {

		if (!registeredContainer.containsKey(selectedMasterContainer)) {
			throw new IllegalArgumentException("The default subgraph '" + selectedMasterContainer
					+ "' cannot be created beause the associated template could not be found.  "
					+ "Please load the subgraph's descriptor, or registere the associated object.");
		}
		VisualGraph template = registeredContainer.get(selectedMasterContainer);
		return (VisualGraph) template.deepCopy(-1);
	}

	/**
	 * Selects the default master node. The default master node will be used to
	 * create a new node using {@link #createNode()}
	 * 
	 * @param name
	 *            the name of the master node
	 */
	public void selectedMasterNode(String name) {

		this.selectedMasterNode = name;
	}

	/**
	 * Returns currently selected master node.
	 * 
	 * @see #createNode(), {@link #selectedMasterNode(String)}
	 */
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
	public void selectedMasterEdge(String name) {

		this.selectedMasterEdge = name;
	}

	/**
	 * Returns currently selected master edge.
	 * 
	 * @see #createEdge(), {@link #selectedMasterEdge(String)}
	 */
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
	public void selectedMasterContainer(String name) {

		this.selectedMasterContainer = name;
	}

	/**
	 * Returns currently selected master container.
	 * 
	 * @see #createContainer(), {@link #selectedMasterContainer(String)}
	 */
	public String getSelectedMasterContainer() {

		return selectedMasterContainer;
	}


	private void poolSVGDefinitions(List<PropertyList> presentation) throws BuildException {

		for (PropertyList template : presentation) {
			loadSVGDefinitions(template);
		}
	}

	private void loadSVGDefinitions(PropertyList defintion) {

		SVGDefinition def = new SVGDefinition(defintion);
		SVGDefinitionPool.pool(def.id, def);
	}


	////////review ///////////////////////////////////////

	public void loadAndPoolSVG(String id, InputStream data) {

		try {
			SVGDefinition p = new SVGDefinition(id, data);
			SVGDefinitionPool.pool(id, p);

		} catch (Exception e) {
			// TODO: handle exception

			System.err.println("'" + id + "' could not be loaded. Reason: " + e.getMessage());

		}
	}

	private void poolContainerDefinitions(List<PropertyList> containers) {

		for (PropertyList template : containers) {
			VisualGraph graph = VisualObjectBuilder.createSubgraphTemplate(template);
			if (graph != null) {
				register(graph);
			} else {
				System.err.println();// TODO LOG
			}

			// initialize the default subgraph with the first registered
			// container
			if (selectedMasterContainer == null) {
				selectedMasterContainer = graph.getName();
			}
		}
	}

	private void poolEdgeDefinitions(List<PropertyList> edges) {

		for (PropertyList template : edges) {
			VisualEdge edge = VisualObjectBuilder.createEdgeTemplate(template);
			if (edge != null) {
				register(edge);
			} else {
				System.err.println();// TODO LOG
			}

			// initialize the default edge with the first registered edge
			if (selectedMasterEdge == null) {
				selectedMasterEdge = edge.getName();
			}
		}
	}

	private void poolNodeDefinitions(List<PropertyList> nodes) {

		for (PropertyList template : nodes) {
			VisualNode node = VisualObjectBuilder.createNodeTemplate(template);
			if (node != null) {
				register(node);
			} else {
				System.err.println();// TODO LOG
			}

			// initialize the default node with the first registered node
			if (selectedMasterNode == null) {
				selectedMasterNode = node.getName();
			}
		}
	}

	public static String saveGraphDocument(GraphDocument graphDocument, OutputStream stream) throws IOException {

		return GraphBuilder.save(graphDocument, stream);
	}

	public static void loadGraphDocument(GraphDocument graphDocument, InputStream stream) throws IOException,
	ParserConfigurationException, SAXException {

		GraphBuilder.load(graphDocument, stream);
	}

}
