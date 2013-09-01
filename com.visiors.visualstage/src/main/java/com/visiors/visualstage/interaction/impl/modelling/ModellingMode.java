package com.visiors.visualstage.interaction.impl.modelling;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.constants.InteractionConstants;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.interaction.impl.BaseTool;
import com.visiors.visualstage.util.GraphInteractionUtil;

/**
 * 
 * Coordinating the interactions. - MAking sure that objects receive mouse/key
 * events - Ensures that manipulable object receive relevant information so that
 * they can react on user interaction. on user user action - Taking care of top
 * level actions like: + moving objects + connecting / reconnecting edges + copy
 * on move
 * 
 * @version $Id: $
 */
public class ModellingMode extends BaseTool {

	/* resizing details */
	public static final int NONE = -1;
	private static final int INTERNAL_ACTION_ID = 0xFFFF00;
	private static final int MOVE_OR_COPY_OBJECTS = ModellingMode.INTERNAL_ACTION_ID + 1;
	private static final int MOVE_OBJECTS = ModellingMode.INTERNAL_ACTION_ID + 2;

	private Point mousePressedPos;
	private Rectangle hitObjectPos;
	private VisualGraphObject hitObject;
	private VisualEdge connectingEdge;
	private VisualNode connectingSourceNode;
	private VisualNode connectingTargetNode;
	private int connectingTargetPort;
	private int connectingSourcePort;

	private boolean manipulatingNotified;
	private int manipulatoinIndex;
	private int currentCursor;
	private VisualGraph hitGroup;

	@Inject
	private UndoRedoHandler undoRedoHandler;

	public ModellingMode() {

		super();
	}

	@Override
	public String getName() {

		return InteractionConstants.MODE_MODELING;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		/* passing key events to current object */
		if (hitObject != null) {
			if (hitObject.keyPressed(keyChar, keyCode)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		/* passing key events to current object */
		if (hitObject != null) {
			if (hitObject.keyReleased(keyChar, keyCode)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		/* passing mouse events to current object */
		if (hitObject != null) {
			if (hitObject.mousePressed(pt, button, functionKey)) {
				return true;
			}
		}

		mousePressedPos = pt;

		/* do selection change on mouse click only if Control key is not pressed */
		if (hitObject == null) {
			/*
			 * deselect selected object when clicking on the background and
			 * control key is not pressed
			 */
			if (!isControlKeyPressed(functionKey) && !isShiftKeyPressed(functionKey)) {

				visualGraph.clearSelection();
			}
		} else {
			/*
			 * if an selected object has been hit, do nothing now, because user
			 * might want to move a bunch of selected objects. Once the mouse
			 * button is released we will deselect objects if they are't been
			 * moved
			 */

			// ensure objects on top can be moved even if objects below them are
			// selected
			// if(actionType != RESIZING_EDGE && actionType != RESIZING_NODE){
			// hitObject = getTopHitObject(activeGraphView, pt, false);
			hitObjectPos = hitObject.getBounds();
			// }

			// if(objectRequestingManipulation()) {
			// hitObject.manipulationPointClicked(manipulatoinIndex, pt,
			// functionKey);
			// manipulatoinIndex = hitObject.hitManipulablePoint(pt,
			// functionKey);
			// }
		}

		// TODO move it to an own mode implementiation
		// /* set allow multi-selection if the control- or shift key are pressed
		// */
		// visualGraph.setMuliSelectionMode(isControlKeyPressed(functionKey) ||
		// isShiftKeyPressed(functionKey));
		return false;
	}

	//
	// private void clearSelection(graphView gv) {
	// gv.getSelectionService().clearSelection();
	// GraphObjectView[] objects = gv.getGraphObjects();
	// for (int i = 0; i < objects.length; i++) {
	// if(objects[i] instanceof graphView)
	// clearSelection((graphView) objects[i]);
	// }
	// }

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		/* passing mouse events to current object */
		if (hitObject != null) {
			if (hitObject.mouseReleased(pt, button, functionKey)) {
				return true;
			}
		}

		if (mousePressedPos.equals(pt)) {
			/* only if mouse is released on the same object */

			if (hitObject == GraphInteractionUtil.getFirstHitObjectAt(visualGraph, pt)) { /*
			 * invert
			 * selection
			 * if
			 * multi
			 * -
			 * selection
			 * -
			 * mode
			 * is
			 * active
			 * ;
			 * otherwise
			 * reset
			 * the
			 * selection
			 */

				// TODO birng it to selectionMode
				// if (hitObject != null && !hitObject.isSelected() &&
				// !isControlKeyPressed(functionKey)
				// && !isShiftKeyPressed(functionKey)) {
				//
				// visualGraph.clearSelection();
				// visualGraph.select(hitObject, true);
				// }
				// if (visualGraph.isMuliSelectionMode()) {
				// if (hitObject != null) {
				// visualGraph.invertObjectSelection(hitObject);
				// }
				// } else {
				//
				// if (visualGraph.getSelectionCount() > 1) {
				// visualGraph.clearSelection();
				// visualGraph.select(hitObject, true);
				// }
				// }
			}
		}
		reassignConnection();

		relocateSelection(functionKey);

		/*
		 * for multiply copying on move; otherwise user would need to move mouse
		 * first in order to create a second copy of the same object
		 */
		if (hitObject != null) {
			if (manipulatoinIndex == ModellingMode.MOVE_OBJECTS) {
				manipulatoinIndex = ModellingMode.MOVE_OR_COPY_OBJECTS;
			}
		}

		cleanUp();

		return false;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		/* passing mouse events to current object */
		if (hitObject != null) {
			if (hitObject.mouseDoubleClicked(pt, button, functionKey)) {
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		currentCursor = InteractionConstants.CURSOR_DEFAULT;
		manipulatoinIndex = ModellingMode.NONE;

		updateHitObject(pt);

		// System.err.println("Container: "+ activeGraphView.getName() +
		// ", hit member: " + hitObject);

		/* passing key events to current object */
		if (hitObject != null) {
			if (hitObject.mouseMoved(pt, button, functionKey)) {
				currentCursor = hitObject.getPreferredCursor();

				return true;
			}
			manipulatoinIndex = ModellingMode.MOVE_OR_COPY_OBJECTS;
		}

		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		/* passing mouse events to current object */
		if (hitObject != null) {
			if (hitObject.mouseDragged(pt, button, functionKey)) {
				return true;
			}
		}

		if (hitObject != null) {
			if (!hitObject.isSelected()) { // always select objects that are
				// being dragged
				visualGraph.clearSelection();
				visualGraph.setSelection(hitObject);
			}
			if (manipulatoinIndex == ModellingMode.MOVE_OR_COPY_OBJECTS) { // Duplicate
				// selection
				if (isControlKeyPressed(functionKey)) {
					duplicateSelection(pt);
				}
				manipulatoinIndex = ModellingMode.MOVE_OBJECTS;
			} else if (manipulatoinIndex == ModellingMode.MOVE_OBJECTS) { // move
				// selected
				// objects
				if (!wasManipulationNotified()) {
					notifyActionBegin();
				}
				moveSelection(pt);
			}
			trackNodesHitByActiveConnector();
			trackNodesHitByNode();
			trackGroupHitByActiveObject(functionKey);
			return manipulatoinIndex != ModellingMode.MOVE_OBJECTS; // let other
			// like Edge
			// creation
			// do their
			// job
		}
		return false;
	}

	@Override
	public void cancelInteraction() {

		cleanUp();
	}

	private void cleanUp() {

		if (hitObject != null) {
			hitObject.setHighlighted(false);
		}

		if (wasManipulationNotified()) {
			notifyActionEnd();
		}

		connectingEdge = null;
		connectingSourceNode = null;
		connectingTargetNode = null;
		connectingSourcePort = ModellingMode.NONE;
		connectingTargetPort = ModellingMode.NONE;

		if (hitGroup != null) {
			hitGroup.setHighlighted(false);
			hitGroup = null;
		}
	}

	private void updateHitObject(Point pt) {

		final VisualGraphObject lastHitObject = hitObject;

		hitObject = GraphInteractionUtil.getFirstHitObjectAt(visualGraph, pt);
		if (lastHitObject != hitObject && lastHitObject != null) {
			lastHitObject.setHighlighted(false);
		}

		if (hitObject != null) {
			hitObject.setHighlighted(true);
			// TODO check this, changing the graph seems to be a bad idea
			// visualGraph = hitObject.getParentGraph();

		}
	}

	@Override
	public int getPreferredCursor() {

		return currentCursor;
	}

	private void trackGroupHitByActiveObject(int functionKey) {

		final boolean keyPressed = isAltKeyPressed(functionKey);
		if (keyPressed) {
			if (hitObject instanceof VisualNode) {
				if (hitObject.getParentGraph().getDepth() > 0) {
					hitObject.getParentGraph().setHighlighted(true);
				}
			}
			return;
		}

		if (hitGroup != null) {
			hitGroup.setHighlighted(false);
			hitGroup = null;
		}

		if (hitObject instanceof VisualNode) {

			final List<VisualGraphObject> selection = visualGraph.getSelection();
			hitGroup = GraphInteractionUtil.getLastGroupHitByObjects(visualGraph, selection);
			if (hitGroup != null) {
				hitObject.getParentGraph().setHighlighted(false);
				hitGroup.setHighlighted(true);
			}
		}
	}

	private void trackNodesHitByNode() {

		// TODO Auto-generated method stub

	}

	private void trackNodesHitByActiveConnector() {

		if (connectingTargetNode != null) {
			connectingTargetNode.illuminatePorts(false);
		}

		if (connectingSourceNode != null) {
			connectingSourceNode.illuminatePorts(false);
		}

		// check if a connector is acting
		if (hitObject instanceof VisualEdge) {

			final VisualEdge edge = (VisualEdge) hitObject;
			final Point sPt = edge.getPath().getStart().getPoint();
			// check if a node is hit by connector

			VisualNode node = GraphInteractionUtil.getFirstHitNodeAt(visualGraph, sPt);
			if (node != null) {
				node.illuminatePorts(true);
				final int port = node.getPreferredPort(sPt);
				node.highlightPort(port, true);
				connectingSourcePort = port;
				if (port != ModellingMode.NONE) {
					connectingEdge = edge;
					connectingSourceNode = node;
				}
			} else {
				connectingSourceNode = null;
			}

			final Point ePt = edge.getPath().getEnd().getPoint();
			node = GraphInteractionUtil.getFirstHitNodeAt(visualGraph, ePt);
			if (node != null) {
				node.illuminatePorts(true);
				final int port = node.getPreferredPort(ePt);
				connectingTargetPort = port;
				node.highlightPort(port, true);
				if (port != ModellingMode.NONE) {
					connectingEdge = edge;
					connectingTargetNode = node;
				}
			} else {
				connectingTargetNode = null;
			}
		}
	}

	private void relocateSelection(int functionKey) {

		trackGroupHitByActiveObject(functionKey);
		if (isAltKeyPressed(functionKey)) {
			return;
		}
		if (hitObject instanceof VisualNode && hitObject != hitGroup) {
			if (hitGroup == null) {
				hitGroup = visualGraph;
			}
			if (hitObject.getParentGraph() != hitGroup) {
				final List<VisualGraphObject> selection = visualGraph.getSelection();
				GraphInteractionUtil.relocateObject(selection, hitGroup);
			}
		}
	}

	private void reassignConnection() {

		if (connectingEdge != null) {
			if (connectingSourceNode != null) {
				connectingSourceNode.illuminatePorts(false);
				if (connectingSourcePort != ModellingMode.NONE) {
					connectingEdge.connect(connectingSourceNode, connectingSourcePort, connectingEdge.getTargetNode(),
							connectingEdge.getTargetPortId());
				}
			} else {
				connectingEdge.connect(null, -1, connectingEdge.getTargetNode(), connectingEdge.getTargetPortId());
			}

			if (connectingTargetNode != null) {
				connectingTargetNode.illuminatePorts(false);
				if (connectingTargetPort != ModellingMode.NONE) {
					connectingEdge.connect(connectingEdge.getSourceNode(), connectingEdge.getSourcePortId(),
							connectingTargetNode, connectingTargetPort);
				}
			} else {
				connectingEdge.connect(connectingEdge.getSourceNode(), connectingEdge.getSourcePortId(), null, -1);
			}
			GraphInteractionUtil.moveEdgeToAppropriateGraphView(connectingEdge);
		}
	}

	private final boolean wasManipulationNotified() {

		return manipulatingNotified;
	}

	private final void notifyActionBegin() {

		//		undoRedoHandler.stratOfGroupAction();
		//		// notify object about immediate actions
		//		final List<VisualGraphObject> selection = visualGraph.getSelection();
		//		for (final VisualGraphObject vgo : selection) {
		//			vgo.startManipulating();
		//		}
		manipulatingNotified = true;
	}

	private final void notifyActionEnd() {

		//		// notify object about end of interaction
		//		manipulatingNotified = false;
		//		final List<VisualGraphObject> selection = visualGraph.getSelection();
		//		for (final VisualGraphObject vgo : selection) {
		//			vgo.endManipulating();
		//		}

		undoRedoHandler.endOfGroupAction();
	}

	void duplicateSelection(Point pt) {

		try {
			undoRedoHandler.stratOfGroupAction();

			//TODO duplicate selected objects
			//			// create
			//			final List<VisualGraphObject> selection = visualGraph.getSelection();
			//			// selection.remove(hitObject);
			//
			//			final PropertyList propertyList = new DefaultPropertyList();
			//			GraphEditor.visualObjects2ProperyList(selection.toArray(new VisualGraphObject[0]), propertyList);
			//			
			//			final List<VisualGraphObject> duplicatedObjects = GraphEditor.createGraphObjects(propertyList, visualGraph,
			//					true);
			//			
			//
			//			visualGraph.setSelection(duplicatedObjects);
			//			for (int i = 0; i < duplicatedObjects.size(); i++) {
			//				visualGraph.toFront(duplicatedObjects.get(i));
			//			}

			updateHitObject(pt);

		} finally {
			undoRedoHandler.endOfGroupAction();
		}

	}

	void moveSelection(Point pt) {

		if (hitObject != null) {
			final Point startPos = hitObjectPos.getLocation();
			final Point currentPos = hitObject.getBounds().getLocation();
			final int dx = mousePressedPos.x - pt.x + currentPos.x - startPos.x;
			final int dy = mousePressedPos.y - pt.y + currentPos.y - startPos.y;

			final List<VisualGraphObject> selection = visualGraph.getSelection();

			moveEdgesWithSelectedNodes(selection, dx, dy);
			moveEdges(selection, dx, dy);
			moveNodes(selection, dx, dy);

		}
	}

	private void moveEdgesWithSelectedNodes(List<VisualGraphObject> selection, int dx, int dy) {

		final List<VisualEdge> edgesToMove = new ArrayList<VisualEdge>();
		VisualNode sn, tn;

		for (final VisualGraphObject vgo : selection) {
			if (vgo instanceof VisualNode) {
				final VisualNode n = (VisualNode) vgo;
				List<VisualEdge> connections = n.getOutgoingEdges();
				for (final VisualEdge edge : connections) {
					if (!edge.isSelected()) {
						tn = edge.getTargetNode();
						if (tn != null && tn.isSelected() && !edgesToMove.contains(edge)) {
							edgesToMove.add(edge);
						}
					}
				}
				connections = n.getIncomingEdges();
				for (final VisualEdge edge : connections) {
					if (!edge.isSelected()) {
						sn = edge.getSourceNode();
						if (sn != null && sn.isSelected() && !edgesToMove.contains(edge)) {
							edgesToMove.add(edge);
						}
					}
				}
			}
		}
		for (final VisualEdge edge : edgesToMove) {
			edge.move(-dx, -dy);
		}
	}

	private final synchronized void moveEdges(List<VisualGraphObject> selection, int dx, int dy) {

		for (final VisualGraphObject vgo : selection) {
			if (vgo instanceof VisualEdge) {
				vgo.move(-dx, -dy);
			}
		}
	}

	private final synchronized void moveNodes(List<VisualGraphObject> selection, int dx, int dy) {

		for (final VisualGraphObject vgo : selection) {
			if (vgo instanceof VisualNode) {
				vgo.move(-dx, -dy);
			}
		}
	}

}
