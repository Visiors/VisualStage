package com.visiors.visualstage.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.visiors.visualstage.graph.view.GraphObjectView;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.node.NodeView;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.util.GraphInteractionUtil;

public class DefaultGroupingHandler implements GroupingHandler {

	private GraphView graphView;
	private final Map<EdgeView, ConnectionInfo> edgeSourceMap;
	private final Map<EdgeView, ConnectionInfo> edgeTargetMap;
	private final Map<EdgeView, PropertyList> edgePropertiesMap;

	@Inject
	private UndoRedoHandler undoRedoHandler;

	@Inject
	public DefaultGroupingHandler() {

		edgeSourceMap = new HashMap<EdgeView, ConnectionInfo>();
		edgeTargetMap = new HashMap<EdgeView, ConnectionInfo>();
		edgePropertiesMap = new HashMap<EdgeView, PropertyList>();
	}

	@Override
	public void setScope(GraphView graphView) {

		this.graphView = graphView;

	}

	@Override
	public boolean canGroup() {

		return allNodesOnSameLevel();
	}

	@Override
	public boolean canUngroup() {

		List<GraphObjectView> selection = graphView.getSelection();
		if (selection.size() == 0) {
			return false;
		}

		for (GraphObjectView vgo : selection) {
			if (!(vgo instanceof GraphView)) {
				return false;
			}
		}
		return true;
	}

	private List<NodeView> getTargetedNodes() {

		List<NodeView> result = new ArrayList<NodeView>();
		List<GraphObjectView> objects = graphView.getSelection();
		for (GraphObjectView obj : objects) {
			if (obj instanceof NodeView) {
				result.add((NodeView) obj);
			}
		}
		return result;
	}

	private List<EdgeView> getTargetedEdges() {

		// capture all selected edges
		List<EdgeView> result = new ArrayList<EdgeView>();
		List<GraphObjectView> objects = graphView.getSelection();
		for (GraphObjectView obj : objects) {
			if (obj instanceof EdgeView) {
				result.add((EdgeView) obj);
			}
		}

		// capture edges that are connected to the selected nodes
		List<NodeView> nodes = getTargetedNodes();
		List<EdgeView> connections;
		for (NodeView node : nodes) {
			connections = node.getOutgoingEdges();
			for (EdgeView edge : connections) {
				if (result.indexOf(edge) == -1) {
					result.add(edge);
				}
			}
			connections = node.getIncomingEdges();
			for (EdgeView edge : connections) {
				if (result.indexOf(edge) == -1) {
					result.add(edge);
				}
			}
		}

		return result;
	}

	private boolean allNodesOnSameLevel() {

		List<NodeView> nodes = getTargetedNodes();
		if (nodes.size() > 1) {

			GraphView parent = nodes.get(0).getParentGraph();
			for (int i = 1; i < nodes.size(); i++) {
				if (nodes.get(i).getParentGraph() != parent) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private GraphView getObjectsParentContainer() {

		List<GraphObjectView> objects = graphView.getSelection();
		if (objects.size() > 0) {

			GraphView parent = objects.get(0).getParentGraphGraph();
			for (int i = 1; i < objects.size(); i++) {
				if (objects.get(i).getParentGraph() != parent) {
					return null;
				}
			}
			return parent == null ? graphView : parent;
		}
		return null;
	}

	@Override
	public void groupSelection(String graphviewToUse) {

		GraphView parentGraph = getObjectsParentContainer();
		if (parentGraph == null) {
			return;
		}

		GraphView group = GraphFactory.instance().createContainer(-1, graphviewToUse);

		undoRedoHandler.stratOfGroupAction();
		graphView.fireStartGrouping(group);

		parentGraph.addGraphObject(group);
		List<GraphObjectView> objectsToBeGroupped = parentGraph.getSelection();
		objectsToBeGroupped
				.addAll(expandToNotIncludedConnections(objectsToBeGroupped, parentGraph));

		saveEdgeConnectionInfo(objectsToBeGroupped);
		// delete edges first then nodes. This is needed for undo to create
		// edges with exiting nodes
		for (GraphObjectView vgo : objectsToBeGroupped) {
			if (vgo instanceof EdgeView) {
				parentGraph.deleteGraphObject(vgo);
			}
		}
		for (GraphObjectView vgo : objectsToBeGroupped) {
			if (vgo instanceof NodeView) {
				parentGraph.deleteGraphObject(vgo);
			}
		}

		group.setSelected(true);

		// add nodes first
		for (GraphObjectView vgo : objectsToBeGroupped) {
			if (vgo instanceof NodeView) {
				group.addGraphObject(vgo);
			}
		}
		for (GraphObjectView vgo : objectsToBeGroupped) {
			if (vgo instanceof EdgeView) {
				group.addGraphObject(vgo);
			}
		}

		group.clearSelection();

		reconnectEdges(objectsToBeGroupped);

		graphView.fireEndGrouping(group);
		undoRedoHandler.endOfGroupAction();
	}

	private boolean edgeHasConnectionToOutside(EdgeView e) {

		ConnectionInfo c1 = edgeSourceMap.get(e);
		ConnectionInfo c2 = edgeTargetMap.get(e);
		return (c1 == null || !c1.node.isSelected() || c2 == null || !c2.node.isSelected());
	}

	private List<GraphObjectView> expandToNotIncludedConnections(List<GraphObjectView> objects,
			GraphView parnetGraphView) {

		List<GraphObjectView> objectToBeIncluded = new ArrayList<GraphObjectView>();
		// include edges that are at least from one side are connected to
		// selected objects
		EdgeView[] edges = parnetGraphView.getEdges();
		for (EdgeView e : edges) {
			if (!objects.contains(e)) {
				if (isSourceNodeInList(e, objects) || isTargetNodeInList(e, objects)) {
					objectToBeIncluded.add(e);
				}
			}
		}
		return objectToBeIncluded;
	}

	private boolean isSourceNodeInList(EdgeView e, List<GraphObjectView> objects) {

		NodeView sn = e.getSourceNode();
		for (GraphObjectView obj : objects) {
			if (obj instanceof NodeView) {
				if (isChildOf(sn, (NodeView) obj)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isTargetNodeInList(EdgeView e, List<GraphObjectView> objects) {

		NodeView tn = e.getTargetNode();
		for (GraphObjectView obj : objects) {
			if (obj instanceof NodeView) {
				if (isChildOf(tn, (NodeView) obj)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isChildOf(NodeView node, NodeView parent) {

		if (node == null) {
			return false;
		}

		if (node == parent) {
			return true;
		}

		return isChildOf(node.getParentGraph(), parent);
	}

	private void saveEdgeConnectionInfo(List<GraphObjectView> objects) {

		edgeSourceMap.clear();
		edgeTargetMap.clear();
		EdgeView e;
		NodeView n;
		for (GraphObjectView vgo : objects) {
			if (vgo instanceof EdgeView) {
				e = ((EdgeView) vgo);
				n = e.getSourceNode();
				if (n != null) {
					edgeSourceMap.put((EdgeView) vgo, new ConnectionInfo(n, e.getSourcePortId()));
				}
				n = ((EdgeView) vgo).getTargetNode();
				if (n != null) {
					edgeTargetMap.put((EdgeView) vgo, new ConnectionInfo(n, e.getTargetPortId()));
				}
				edgePropertiesMap.put(e, e.getProperties());
			}

		}
	}

	private void reconnectEdges(List<GraphObjectView> objects) {

		EdgeView e;
		ConnectionInfo c;
		for (GraphObjectView vgo : objects) {
			if (vgo instanceof EdgeView) {
				e = (EdgeView) vgo;
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

		GraphView parentGraph = getObjectsParentContainer();
		if (parentGraph == null) {
			return;
		}

		undoRedoHandler.stratOfGroupAction();
		List<GraphObjectView> selection = graphView.getSelection();

		if (selection.size() == 0) {
			return;
		}

		for (GraphObjectView vgo : selection) {

			if (vgo instanceof GraphView) {

				GraphView group = (GraphView) vgo;
				GraphView parentContainer = group.getParentGraph();
				GraphObjectView[] objects = group.getGraphObjects();
				List<GraphObjectView> objectsToBeUngroupped = new ArrayList<GraphObjectView>(
						Arrays.asList(objects));
				objectsToBeUngroupped.addAll(expandToNotIncludedConnections(objectsToBeUngroupped,
						parentGraph));
				saveEdgeConnectionInfo(objectsToBeUngroupped);

				// delete edges first then nodes. This is needed for undo to
				// create edges with exiting nodes
				for (GraphObjectView go : objectsToBeUngroupped) {
					if (go instanceof EdgeView) {
						group.deleteGraphObject(go);
					}
				}
				for (GraphObjectView go : objectsToBeUngroupped) {
					if (go instanceof NodeView) {
						group.deleteGraphObject(go);
					}
				}

				parentContainer.deleteGraphObject(group);

				// add nodes first
				for (GraphObjectView go : objectsToBeUngroupped) {
					if (go instanceof NodeView) {
						parentContainer.addGraphObject(go);
						go.setSelected(true);
					}
				}
				for (GraphObjectView go : objectsToBeUngroupped) {
					if (go instanceof EdgeView) {
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
	// graphView.getSelectionService().getSelection();
	// if(selection.size() == 0)
	// return null;
	// List<GraphObjectView> result = new ArrayList<GraphObjectView>();
	// int highestGraph = 0XFFFF;
	// for(GraphObjectView vgo : selection){
	// GraphView parent = vgo.getParentGraph();
	// highestGraph = Math.min(highestGraph, parent.getLevel());
	// }
	// for(GraphObjectView vgo : selection){
	// GraphView parent = vgo.getParentGraph();
	// if(parent.getLevel() != highestGraph){
	// vgo = findParentOfLevel(vgo, highestGraph);
	// if(vgo == null)
	// return null;
	// }
	// result.add(vgo);
	// }
	// return result;
	// }

	private GraphObjectView findParentOfLevel(GraphObjectView vgo, int level) {

		for (;;) {
			GraphView parent = vgo.getParentGraph();
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

		NodeView node;
		int portID;

		ConnectionInfo(NodeView node, int portID) {

			this.node = node;
			this.portID = portID;

		}
	}
}
