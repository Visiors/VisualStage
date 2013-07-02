package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Rectangle;
import java.util.List;

import com.visiors.visualstage.graph.view.GraphObjectView;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewAdapter;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.NodeView;

/**
 * This class passes events coming from a subgraph to its parent graph
 */
class GraphContentManager {

	final DefaultGraphView graphview;

	GraphContentManager(DefaultGraphView graphview) {

		this.graphview = graphview;

		graphview.addGraphViewListener(graphListener);
		// updateMemberState();

		// TODO review !!
		/*
		 * PropertyList pl = graphview.getProperties(); if (pl != null) {
		 * graphview.getProperties().addPropertyListener(new PropertyListener()
		 * {
		 * 
		 * @Override public void propertyChanged(List<PropertyList> path,
		 * PropertyUnit property) { updateMemberState(); } }); }
		 */
	}

	// private void updateMemberState() {
	//
	// List<GraphObjectView> objects = graphview.getGraphObjects();
	// for (GraphObjectView graphObjectView : objects) {
	//
	// graphObjectView.getAttributes().setSelectable(b);
	// AttributeUtil.setSelectable(objects[i], graphview.contentSelectable);
	// if (!graphview.contentSelectable) {
	// objects[i].setSelected(false);
	// }
	// AttributeUtil.setMovable(objects[i], graphview.contentMovable);
	// AttributeUtil.setDeletable(objects[i], graphview.contentDeletable);
	// }
	// }

	void fitToContent(Rectangle newExpansion) {

		if (newExpansion.isEmpty()) {
			// graphview.getParent().deleteGraphObject(graphview);
		} else {
			newExpansion.grow(graphview.margin, graphview.margin);
			graphview.setBounds(newExpansion);
		}
	}

	void ensureContentIsVisible() {

		if (graphview.getParentGraph() == null) {
			return;
		}
		Rectangle b = graphview.getBounds();

		// b.grow(-margin , -margin);
		Rectangle ex = graphview.getExtendedBoundary();
		b = b.union(ex);
		// graphview.setBounds(b);

	}

	// private void selectParent() {
	// if(parent != null && selected)
	// parent.setSelected(true);
	// }

	GraphViewListener graphListener = new GraphViewAdapter() {

		@Override
		public void graphExpansionChanged(GraphView graph, Rectangle newBoundary) {

			if (graphview.fitToContent) {
				fitToContent(newBoundary);
			}
		}

		@Override
		public void nodeStoppedChangingBoundary(NodeView node, Rectangle oldBoundary) {

			if (!graphview.fitToContent) {
				ensureContentIsVisible();
			}
		}

		@Override
		public void nodeAdded(NodeView node) {

			// AttributeUtil.setSelectable(node, graphview.contentSelectable);
			// AttributeUtil.setMovable(node, graphview.contentMovable);
			// AttributeUtil.setMovable(node, graphview.contentDeletable);
		}

		@Override
		public void edgeAdded(EdgeView edge) {

			// AttributeUtil.setSelectable(edge, graphview.contentSelectable);
			// AttributeUtil.setMovable(edge, graphview.contentMovable);
			// AttributeUtil.setMovable(edge, graphview.contentDeletable);
		}

		boolean unselecting;

		@Override
		public void nodeSelectionChanged(NodeView node) {

			boolean selected = node.isSelected();
			/*
			 * either the graphview or its members can be selected; otherwise it
			 * would be not possible to select and delete a member without
			 * deleting the entire group
			 */
			if (selected) {
				unselecting = true;
				graphview.setSelected(false);
				unselecting = false;
			}

			/* */
			if (!unselecting && selected && node instanceof GraphView) {
				unselectMembers((GraphView) node);
			}
		}

		private void unselectMembers(GraphView gv) {

			List<GraphObjectView> members = gv.getGraphObjects();
			for (GraphObjectView member : members) {
				member.setSelected(false);
			}
		}
	};

}
