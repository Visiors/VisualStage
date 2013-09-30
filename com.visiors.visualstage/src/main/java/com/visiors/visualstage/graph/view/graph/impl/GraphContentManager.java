package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Rectangle;
import java.util.List;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewAdapter;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.VisualNode;

/**
 * This class passes events coming from a subgraph to its parent graph
 */
class GraphContentManager {

	final DefaultVisualGraph graphview;

	GraphContentManager(DefaultVisualGraph graphview) {

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
		} else {
			//			newExpansion.grow(graphview.margin, graphview.margin);
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
		public void graphExpansionChanged(VisualGraph graph) {

			if (graphview.fitToContent) {
				fitToContent(graph.getExtendedBoundary());
			}
		}

		@Override
		public void nodeStoppedChangingBoundary(VisualNode node, Rectangle oldBoundary) {

			if (!graphview.fitToContent) {
				ensureContentIsVisible();
			}
		}

		@Override
		public void nodeAdded(VisualNode node) {

			// AttributeUtil.setSelectable(node, graphview.contentSelectable);
			// AttributeUtil.setMovable(node, graphview.contentMovable);
			// AttributeUtil.setMovable(node, graphview.contentDeletable);
		}

		@Override
		public void edgeAdded(VisualEdge edge) {

			// AttributeUtil.setSelectable(edge, graphview.contentSelectable);
			// AttributeUtil.setMovable(edge, graphview.contentMovable);
			// AttributeUtil.setMovable(edge, graphview.contentDeletable);
		}

		boolean unselecting;

		@Override
		public void nodeSelectionChanged(VisualNode node) {

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
			if (!unselecting && selected && node instanceof VisualGraph) {
				unselectMembers((VisualGraph) node);
			}
		}

		private void unselectMembers(VisualGraph gv) {

			List<VisualGraphObject> members = gv.getGraphObjects();
			for (VisualGraphObject member : members) {
				member.setSelected(false);
			}
		}
	};

}
