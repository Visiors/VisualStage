package com.visiors.visualstage.handler.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewAdapter;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.handler.SelectionHandler;

public class DefaultSelectionHander extends GraphViewAdapter implements GraphViewListener, SelectionHandler {

	protected boolean multiselection;
	protected VisualGraph visualGraph;
	private boolean processing;
	private volatile List<VisualGraphObject> listOfSelectedObjects;

	@Inject
	public DefaultSelectionHander() {

	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		this.visualGraph = graphDocument;
		graphDocument.addGraphViewListener(this);
		listOfSelectedObjects = new ArrayList<VisualGraphObject>();
	}

	@Override
	public void setMuliSelectionMode(boolean multiselection) {

		this.multiselection = multiselection;
	}

	@Override
	public boolean isMuliSelectionMode() {

		return multiselection;
	}

	@Override
	public int getSelectionCount() {

		return listOfSelectedObjects.size();
	}

	@Override
	public void clearSelection() {

		try {
			processing = true;
			while (listOfSelectedObjects.size() != 0) {
				final VisualGraphObject vgo = listOfSelectedObjects.remove(0);
				vgo.setSelected(false);
				// if(vgo instanceof GraphView) {
				// ((GraphView) vgo).getSelectionService().clearSelection();
				// }
			}
		} finally {
			processing = false;
		}
	}

	/**
	 * This method inverts the selection state of the specified object. It
	 * considers the current selection mode: while in multi-selection mode only
	 * the state of the specified object will be changed, in single-selection
	 * mode all other objects will be unselected.
	 * 
	 * @param graphObject
	 *            the {@link VisualGraphObject} of which the selection state is
	 *            to be changed
	 */
	@Override
	public void invertObjectSelection(VisualGraphObject graphObject) {

		final List<VisualGraphObject> objects = visualGraph.getGraphObjects();
		for (final VisualGraphObject vobj : objects) {
			if (vobj == graphObject) {
				vobj.setSelected(!vobj.isSelected() || !multiselection);
			} else {
				if (!multiselection) {
					vobj.setSelected(false);
				}
			}
		}
	}

	/**
	 * This method set the select
	 * 
	 * @param graphObject
	 * @param selected
	 */
	@Override
	public void select(VisualGraphObject graphObject, boolean selected) {

		List<VisualGraphObject> objects = visualGraph.getGraphObjects();

		for (final VisualGraphObject vobj : objects) {
			if (vobj == graphObject) {
				if (vobj.isSelected() != selected) {
					vobj.setSelected(selected);
				}
				break;
			}
		}

	}

	@Override
	public void select(List<VisualGraphObject> graphObject) {

		clearSelection();

		for (final VisualGraphObject vobj : graphObject) {
			vobj.setSelected(true);
		}

	}

	@Override
	public List<VisualGraphObject> getSelection() {

		return new ArrayList<VisualGraphObject>(listOfSelectedObjects);
	}

	// ////////////////////////////////////////////////////////////
	// Graph View events
	@Override
	public void nodeSelectionChanged(VisualNode node) {

		if (processing) {
			return;
		}

		final int index = listOfSelectedObjects.indexOf(node);
		if (index == -1 && node.isSelected()) {
			listOfSelectedObjects.add(node);
		} else if (index != -1 && !node.isSelected()) {
			listOfSelectedObjects.remove(index);
		}
	}

	@Override
	public void invalidate() {

		listOfSelectedObjects.clear();

		visualGraph.clearSelection();
		List<VisualGraphObject> vgos = visualGraph.getGraphObjects();

		for (final VisualGraphObject visualGraphObject : vgos) {

			if (visualGraphObject instanceof VisualGraph) {
				final VisualGraph gv = (VisualGraph) visualGraphObject;
				listOfSelectedObjects.addAll(gv.getSelection());
			}
			if (visualGraphObject.isSelected()) {
				listOfSelectedObjects.add(visualGraphObject);
			}
		}

	}

	@Override
	public void nodeAdded(VisualNode node) {

		if (node.isSelected() && !listOfSelectedObjects.contains(node)) {
			listOfSelectedObjects.add(node);
		}
	}

	@Override
	public void nodeRemoved(VisualNode node) {

		final int index = listOfSelectedObjects.indexOf(node);
		if (index != -1) {
			listOfSelectedObjects.remove(index);
		}
	}

	@Override
	public void edgeAdded(VisualEdge edge) {

		if (edge.isSelected() && !listOfSelectedObjects.contains(edge)) {
			listOfSelectedObjects.add(edge);
		}
	}

	@Override
	public void edgeRemoved(VisualEdge edge) {

		final int index = listOfSelectedObjects.indexOf(edge);
		if (index != -1) {
			listOfSelectedObjects.remove(index);
		}
	}

	@Override
	public void edgeSelectionChanged(VisualEdge edge) {

		if (processing) {
			return;
		}

		final int index = listOfSelectedObjects.indexOf(edge);
		if (index == -1 && edge.isSelected()) {
			listOfSelectedObjects.add(edge);
		} else if (index != -1 && !edge.isSelected()) {
			listOfSelectedObjects.remove(index);
		}
	}

}
