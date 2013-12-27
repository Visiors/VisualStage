package com.visiors.visualstage.pool;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;

/**
 * <p>
 * 
 * This class pools the concrete implementation of graph objects. The graph
 * object instances can be registered here so that they can be used later to
 * create new graph objects. Each instance is considered as a template that can
 * be cloned to create a new instance.
 * 
 * 
 */

@Singleton
public class __GraphObjectTemplateCollection {

	private final Map<String, VisualGraphObject> registeredGraphObjects = new HashMap<String, VisualGraphObject>();

	private __GraphObjectTemplateCollection() {

		/* Singleton */
	}

	/**
	 * Registers a {@link VisualGraphObject} which can be accessed by the
	 * specified <code>templateName</code>. The registered object will be used
	 * as a template to created graph objects on the runtime.
	 * 
	 * @param vgo
	 *            A concrete implementation of {@link VisualNode},
	 *            {@link VisualEdge} or {@link VisualGraph}
	 */
	public void register(String templateName, VisualGraphObject vgo) {

		registeredGraphObjects.put(templateName, vgo);
	}

	/**
	 * Returns true if a graph object with the specified
	 * <code>templateName</code> was already registered; otherwise it returns
	 * false.
	 */
	public boolean isRegistered(String templateName) {

		return registeredGraphObjects.containsKey(templateName);
	}

	public VisualGraphObject get(String templateName) {

		return registeredGraphObjects.get(templateName);
	}

}