package com.visiors.visualstage.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.GroupingHandler;
import com.visiors.visualstage.handler.SelectionHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.stage.interaction.Interactable;

public interface Editor extends Interactable{




	public GraphDocument newDocument(String name);

	/**
	 * Caller is responsible for closing the <code>inputstream</code>
	 * @param content
	 * @return
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public GraphDocument loadDocument(String content) throws IOException, ParserConfigurationException,
	SAXException;

	/**
	 *  Caller is responsible for closing the <code>inputstream</code>
	 * @param document
	 * @return TODO
	 * @throws IOException
	 */
	public String save(GraphDocument document) throws IOException;

	public void setActiveDocument(String name);

	public GraphDocument getActiveDocument();



	public SelectionHandler getSelectionHandler();

	public GroupingHandler getGroupingHandler();

	public UndoRedoHandler getUndoRedoHandler();

	public ClipboardHandler getClipboardHandler();


	/**
	 * Registers a {@link VisualNode} that will server as a master copy for
	 * construction similar visual nodes.
	 * 
	 * @param node
	 *            A concrete implementation of {@link VisualNode}
	 */
	public void register(VisualNode node);

	/**
	 * Registers a {@link VisualEdge} that will server as a master copy for
	 * construction similar visual edges.
	 * 
	 * @param edge
	 *            A concrete implementation of {@link VisualEdge}
	 */
	public void register(VisualEdge edge);

	/**
	 * Registers a {@link VisualGraph} that will server as a master copy for
	 * construction similar visual graphs.
	 * 
	 * @param subgraph
	 *            A concrete implementation of {@link VisualGraph}
	 */
	public void register(VisualGraph subgraph);

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
	public void loadResources(InputStream stream);

	/**
	 * Returns true if the graph object specified by <code>name</code> is
	 * already registered; otherwise false.
	 */
	public boolean isRegistered(String name);

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
	public VisualNode createNode(long id, String name);

	/**
	 * Creates a new instance of {@link VisualNode} by cloning the master node
	 * which is currently selected as default. To select the default master node
	 * use {@link #selectedMasterNode(String)}
	 * 
	 * @return A new instance of {@link VisualNode}.
	 * 
	 * @see #createNode(long, String)
	 */
	public VisualNode createNode();

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
	public VisualEdge createEdge(long id, String name);

	/**
	 * Creates a new instance of {@link VisualEdge} by cloning the master edge
	 * which is currently selected as default. To select the default master edge
	 * use {@link #selectedMasterEdge}
	 * 
	 * @return A new instance of {@link VisualEdge}.
	 * 
	 * @see #createEdge(long, String)
	 */
	public VisualEdge createEdge();

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
	public VisualGraph createContainer(long id, String name);

	/**
	 * Creates a new instance of {@link VisualGraph} by cloning the master
	 * container which is currently selected as default. To select the default
	 * master container use {@link #selectedMasterContainer}
	 * 
	 * @return A new instance of {@link VisualGraph}.
	 * 
	 * @see #createContainer(long, String)
	 */
	public VisualGraph createContainer();

	/**
	 * Selects the default master node. The default master node will be used to
	 * create a new node using {@link #createNode()}
	 * 
	 * @param name
	 *            the name of the master node
	 */
	public void selectedMasterNode(String name);

	/**
	 * Returns currently selected master node.
	 * 
	 * @see #createNode(), {@link #selectedMasterNode(String)}
	 */
	public String getSelectedMasterNode();

	/**
	 * Selects the default master edge. The default master edge will be used to
	 * create a new edge using {@link #createEdge()}
	 * 
	 * @param name
	 *            the name of the master edge
	 */
	public void selectedMasterEdge(String name);

	/**
	 * Returns currently selected master edge.
	 * 
	 * @see #createEdge(), {@link #selectedMasterEdge(String)}
	 */
	public String getSelectedMasterEdge();

	/**
	 * Selects the default master container. The default master container will
	 * be used by {@link #createContainer()}.
	 * 
	 * @param name
	 *            the name of the master node
	 */
	public void selectedMasterContainer(String name);

	/**
	 * Returns currently selected master container.
	 * 
	 * @see #createContainer(), {@link #selectedMasterContainer(String)}
	 */
	public String getSelectedMasterContainer();

	// //////review ///////////////////////////////////////

	public void loadAndPoolSVG(String id, InputStream data);

	/**
	 * Creates the PropertyList which contains all properties of given
	 * VisualObjects.
	 * 
	 * @param GraphObjectViews
	 * @param properties
	 */
	public void visualObjects2ProperyList(List<VisualGraphObject> GraphObjectViews, PropertyList properties);

	public void propertyList2VisualObjects(PropertyList properties, VisualGraph visualGraph, boolean reassingId);

	public List<VisualGraphObject> createGraphObjects(PropertyList properties, VisualGraph rootContainer,
			boolean reassingId);

}
