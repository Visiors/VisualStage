package com.visiors.visualstage.tool.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.util.GraphInteractionUtil;



public class DuplicateOnMoveTool extends BaseTool {

	@Inject
	UndoRedoHandler undoRedoHandler;
	private boolean doDuplicate;

	public DuplicateOnMoveTool(String name) {

		super(name);

		DI.injectMembers(this);
	}


	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		doDuplicate = isControlKeyPressed(functionKey);
		return false;
	}


	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (isControlKeyPressed(functionKey) && doDuplicate) {
			VisualGraphObject hitObject = GraphInteractionUtil.getFirstHitObjectAt(visualGraph, pt);
			if (hitObject != null ) {
				duplicateSelection(pt);
				doDuplicate = false;
				return true;
			}
		}
		return false;
	}


	void duplicateSelection(Point pt) {

		try {
			undoRedoHandler.stratOfGroupAction();
			final List<VisualGraphObject> selection = visualGraph.getSelection();
			final List<VisualGraphObject> duplicates = new ArrayList<VisualGraphObject>();
			for (VisualGraphObject vgo : selection) {
				PropertyList properties = vgo.getProperties();
				if(vgo instanceof VisualNode) {
					duplicates.add(visualGraph.createNode(properties));
				}
				else if(vgo instanceof VisualEdge) {
					duplicates.add(  visualGraph.createEdge(properties));
				}
				else if(vgo instanceof VisualGraph) {
					duplicates.add(visualGraph.createSubgraph(properties));
				}
			}
			for (VisualGraphObject visualGraphObject : selection) {
				visualGraph.toFront(visualGraphObject);
			}

		} finally {
			undoRedoHandler.endOfGroupAction();
		}

	}
}