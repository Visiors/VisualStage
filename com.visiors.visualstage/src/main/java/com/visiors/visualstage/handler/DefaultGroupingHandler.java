package com.visiors.visualstage.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.GraphEditor;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.util.GraphInteractionUtil;

public class DefaultGroupingHandler implements GroupingHandler {

	private VisualGraph visualGraph;
	private final Map<VisualEdge, ConnectionInfo> edgeSourceMap;
	private final Map<VisualEdge, ConnectionInfo> edgeTargetMap;
	private final Map<VisualEdge, PropertyList> edgePropertiesMap;

	@Inject
	private UndoRedoHandler undoRedoHandler;

	@Inject
	public DefaultGroupingHandler() {

		edgeSourceMap = new HashMap<VisualEdge, ConnectionInfo>();
		edgeTargetMap = new HashMap<VisualEdge, ConnectionInfo>();
		edgePropertiesMap = new HashMap<VisualEdge, PropertyList>();
	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		this.visualGraph = graphDocument.getGraph();

	}

	@Override
	public boolean canGroup() {

		return allNodesOnSameLevel();
	}

	@Override
	public boolean canUngroup() {

		List<VisualGraphObject> selection = visualGraph.getSelection();
		if (selection.size() == 0) {
			return false;
		}

		for (VisualGraphObject vgo : selection) {
			if (!(vgo instanceof VisualGraph)) {
				return false;
			}
		}
		return true;
	}

	private List<VisualNode> getTargetedNodes() {

		List<VisualNode> result = new ArrayList<VisualNode>();
		List<VisualGraphObject> objects = visualGraph.getSelection();
		for (VisualGraphObject obj : objects) {
			if (obj instanceof VisualNode) {
				result.add((VisualNode) obj);
			}
		}
		return result;
	}

	private List<VisualEdge> getTargetedEdges() {

		// capture all selected edges
		List<VisualEdge> result = new ArrayList<VisualEdge>();
		List<VisualGraphObject> objects = visualGraph.getSelection();
		for (VisualGraphObject obj : objects) {
			if (obj instanceof VisualEdge) {
				result.add((VisualEdge) obj);
			}
		}

		// capture edges that are connected to the selected nodes
		List<VisualNode> nodes = getTargetedNodes();
		List<VisualEdge> connections;
		for (VisualNode node : nodes) {
			connections = node.getOutgoingEdges();
			for (VisualEdge edge : connections) {
				if (result.indexOf(edge) == -1) {
					result.add(edge);
				}
			}
			connections = node.getIncomingEdges();
			for (VisualEdge edge : connections) {
				if (result.indexOf(edge) == -1) {
					result.add(edge);
				}
			}
		}

		return result;
	}

	private boolean allNodesOnSameLevel() {

		List<VisualNode> nodes = getTargetedNodes();
		if (nodes.size() > 1) {

			VisualGraph parent = nodes.get(0).getParentGraph();
			for (int i = 1; i < nodes.size(); i++) {
				if (nodes.get(i).getParentGraph() != parent) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private VisualGraph getObjectsParentContainer() {

		List<VisualGraphObject> objects = visualGraph.getSelection();
		if (objects.size() > 0) {

			VisualGraph parent = objects.get(0).getParentGraph();
			for (int i = 1; i < objects.size(); i++) {
				if (objects.get(i).getParentGraph() != parent) {
					return null;
				}
			}
			return parent == null ? visualGraph : parent;
		}
		return null;
	}

	@Override
	public void groupSelection(String graphviewToUse) {

		VisualGraph parentGraph = getObjectsParentContainer();
		if (parentGraph == null) {
			return;
		}

		VisualGraph group = GraphEditor.instance().createContainer(-1, graphviewToUse);

		undoRedoHandler.stratOfGroupAction();
		visualGraph.fireStartGrouping(group);

		parentGraph.add(group);
		List<VisualGraphObject> objectsToBeGroupped = parentGraph.getSelection();
		objectsToBeGroupped
		.addAll(expandToNotIncludedConnections(objectsToBeGroupped, parentGraph));

		saveEdgeConnectionInfo(objectsToBeGroupped);
		// delete edges first then nodes. This is needed for undo to create
		// edges with exiting nodes
		for (VisualGraphObject vgo : objectsToBeGroupped) {
			if (vgo instanceof VisualEdge) {
				parentGraph.remove(vgo);
			}
		}
		for (VisualGraphObject vgo : objectsToBeGroupped) {
			if (vgo instanceof VisualNode) {
				parentGraph.remove(vgo);
			}
		}

		group.setSelected(true);

		// add nodes first
		for (VisualGraphObject vgo : objectsToBeGroupped) {
			if (vgo instanceof VisualNode) {
				group.add(vgo);
			}
		}
		for (VisualGraphObject vgo : objectsToBeGroupped) {
			if (vgo instanceof VisualEdge) {
				group.add(vgo);
			}
		}

		group.clearSelection();

		reconnectEdges(objectsToBeGroupped);

		visualGraph.fireEndGrouping(group);
		undoRedoHandler.endOfGroupAction();
	}

	private boolean edgeHasConnectionToOutside(VisualEdge e) {

		ConnectionInfo c1 = edgeSourceMap.get(e);
		ConnectionInfo c2 = edgeTargetMap.get(e);
		return (c1 == null || !c1.node.isSelected() || c2 == null || !c2.node.isSelected());
	}

	private List<VisualGraphObject> expandToNotIncludedConnections(List<VisualGraphObject> objects,
			VisualGraph parnetGraphView) {

		List<VisualGraphObject> objectToBeIncluded = new ArrayList<VisualGraphObject>();
		// include edges that are at least from one side are connected to
		// selected objects
		VisualEdge[] edges = parnetGraphView.getEdges();
		for (VisualEdge e : edges) {
			if (!objects.contains(e)) {
				if (isSourceNodeInList(e, objects) || isTargetNodeInList(e, objects)) {
					objectToBeIncluded.add(e);
				}
			}
		}
		return objectToBeIncluded;
	}

	private boolean isSourceNodeInList(VisualEdge e, List<VisualGraphObject> objects) {

		VisualNode sn = e.getSourceNode();
		for (VisualGraphObject obj : objects) {
			if (obj instanceof VisualNode) {
				if (isChildOf(sn, (VisualNode) obj)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isTargetNodeInList(VisualEdge e, List<VisualGraphObject> objects) {

		VisualNode tn = e.getTargetNode();
		for (VisualGraphObject obj : objects) {
			if (obj instanceof VisualNode) {
				if (isChildOf(tn, (VisualNode) obj)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isChildOf(VisualNode node, VisualNode parent) {

		if (node == null) {
			return false;
		}

		if (node == parent) {
			return true;
		}

		return isChildOf(node.getParentGraph(), parent);
	}

	private void saveEdgeConnectionInfo(List<VisualGraphObject> objects) {

		edgeSourceMap.clear();
		edgeTargetMap.clear();
		VisualEdge e;
		VisualNode n;
		for (VisualGraphObject vgo : objects) {
			if (vgo instanceof VisualEdge) {
				e = ((VisualEdge) vgo);
				n = e.getSourceNode();
				if (n != null) {
					edgeSourceMap.put((VisualEdge) vgo, new ConnectionInfo(n, e.getSourcePortId()));
				}
				n = ((VisualEdge) vgo).getTargetNode();
				if (n != null) {
					edgeTargetMap.put((VisualEdge) vgo, new ConnectionInfo(n, e.getTargetPortId()));
				}
				edgePropertiesMap.put(e, e.getProperties());
			}

		}
	}

	private void reconnectEdges(List<VisualGraphObject> objects) {

		VisualEdge e;
		ConnectionInfo c;
		for (VisualGraphObject vgo : objects) {
			if (vgo instanceof VisualEdge) {
				e = (VisualEdge) vgo;
				c = edgeSourceMap.get(e);
				if (c != null) {
					e.setSourceNode(c.node);
					e.setSourcePortId(c.portID);
				}
				c = edgeTargetMap.get(e);
				if (c != null) {
					e.setTargetNode(c.node);
					e.setTargetPortId(c.portID);
				}
				e.setProperties(edgePropertiesMap.get(e));
				GraphInteractionUtil.moveEdgeToAppropriateGraphView(e);
			}
		}
	}

	@Override
	public void ungroupSelection() {

		VisualGraph parentGraph = getObjectsParentContainer();
		if (parentGraph == null) {
			return;
		}

		undoRedoHandler.stratOfGroupAction();
		List<VisualGraphObject> selection = visualGraph.getSelection();

		if (selection.size() == 0) {
			return;
		}

		for (VisualGraphObject vgo : selection) {

			if (vgo instanceof VisualGraph) {

				VisualGraph group = (VisualGraph) vgo;
				VisualGraph parentContainer = group.getParentGraph();
				VisualGraphObject[] objects = group.getGraphObjects();
				List<VisualGraphObject> objectsToBeUngroupped = new ArrayList<VisualGraphObject>(
						Arrays.asList(objects));
				objectsToBeUngroupped.addAll(expandToNotIncludedConnections(objectsToBeUngroupped,
						parentGraph));
				saveEdgeConnectionInfo(objectsToBeUngroupped);

				// delete edges first then nodes. This is needed for undo to
				// create edges with exiting nodes
				for (VisualGraphObject go : objectsToBeUngroupped) {
					if (go instanceof VisualEdge) {
						group.deleteGraphObject(go);
					}
				}
				for (VisualGraphObject go : objectsToBeUngroupped) {
					if (go instanceof VisualNode) {
						group.deleteGraphObject(go);
					}
				}

				parentContainer.deleteGraphObject(group);

				// add nodes first
				for (VisualGraphObject go : objectsToBeUngroupped) {
					if (go instanceof VisualNode) {
						parentContainer.addGraphObject(go);
						go.setSelected(true);
					}
				}
				for (VisualGraphObject go : objectsToBeUngroupped) {
					if (go instanceof VisualEdge) {
						parentContainer.addGraphObject(go);
						go.setSelected(true);
					}
				}

				reconnectEdges(objectsToBeUngroupped);
			}
		}
		undoRedoHandler.endOfGroupAction();
	}

	// private List<GraphObjectView> getObjectsToGroup() {
	//
	// List<GraphObjectView> selection =
	// visualGraph.getSelectionService().getSelection();
	// if(selection.size() == 0)
	// return null;
	// List<GraphObjectView> result = new ArrayList<GraphObjectView>();
	// int highestGraph = 0XFFFF;
	// for(GraphObjectView vgo : selection){
	// graphView parent = vgo.getParentGraph();
	// highestGraph = Math.min(highestGraph, parent.getLevel());
	// }
	// for(GraphObjectView vgo : selection){
	// graphView parent = vgo.getParentGraph();
	// if(parent.getLevel() != highestGraph){
	// vgo = findParentOfLevel(vgo, highestGraph);
	// if(vgo == null)
	// return null;
	// }
	// result.add(vgo);
	// }
	// return result;
	// }

	private VisualGraphObject findParentOfLevel(VisualGraphObject vgo, int level) {

		for (;;) {
			VisualGraph parent = vgo.getParentGraph();
			if (parent == null) {
				return null;
			} else if (parent.getDepth() == level) {
				return parent;
			} else {
				vgo = parent;
			}
		}
	}

	static private class ConnectionInfo {

		VisualNode node;
		int portID;

		ConnectionInfo(VisualNode node, int portID) {

			this.node = node;
			this.portID = portID;

		}
	}
}
