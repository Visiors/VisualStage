package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.visiors.visualstage.graph.view.GraphObjectView;

public class DepotObjectContainer {

	DepotObject[] objects;
	GraphObjectView[] objectsTorDraw;
	boolean modified;
	private Rectangle expansion;

	DepotObjectContainer() {

		this.objects = new DepotObject[0];
		this.expansion = new Rectangle();
		this.modified = true;
	}

	void add(GraphObjectView obj) {

		DepotObject[] tmp = new DepotObject[objects.length + 1];
		System.arraycopy(objects, 0, tmp, 0, objects.length);
		tmp[objects.length] = new DepotObject(obj, objects.length);
		objects = tmp;
		modified = true;
	}

	void delete(GraphObjectView obj) {

		int index = findObject(obj);
		DepotObject[] tmp = new DepotObject[objects.length - 1];
		System.arraycopy(objects, 0, tmp, 0, index);
		System.arraycopy(objects, index + 1, tmp, index, objects.length - 1 - index);
		objects = tmp;

		// update drawing-order exiting objects
		for (int i = index; i < objects.length; i++) {
			objects[i].order--;
		}

		modified = true;
	}

	// DepotObject[] sortByDrawingOrder(DepotObject[] objArray) {
	// Arrays.sort(objArray, new Comparator<DepotObject>() {
	// public int compare(DepotObject o1, DepotObject o2) {
	// if (o1.order < o2.order)
	// return -1;
	// else
	// return 1;
	// }
	// });
	// return objArray;
	// }

	// draw first groups then the nodes and finally the edges.
	List<DepotObject> sortByDrawingOrder(List<DepotObject> objList) {

		Collections.sort(objList, new Comparator<DepotObject>() {

			@Override
			public int compare(DepotObject o1, DepotObject o2) {

				if (o1.typeFixedPriority < o2.typeFixedPriority) {
					return -1;
				} else if (o1.typeFixedPriority > o2.typeFixedPriority) {
					return 1;
				}

				if (o1.order < o2.order) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		return objList;
	}

	public void toFront(GraphObjectView obj) {

		int idx = findObject(obj);
		int objOrder = objects[idx].order;
		for (int i = 0; i < objects.length; i++) {
			if (objects[i].order > objOrder) {
				objects[i].order--;
			}
		}
		objects[idx].order = objects.length - 1;
	}

	public void toBack(GraphObjectView obj) {

		int idx = findObject(obj);
		int objOrder = objects[idx].order;
		for (int i = 0; i < objects.length; i++) {
			if (objects[i].order < objOrder) {
				objects[i].order++;
			}
		}
		objects[idx].order = 0;
	}

	public void moveForward(GraphObjectView obj) {

		int idx = findObject(obj);
		int objOrder = objects[idx].order;
		for (int i = 0; i < objects.length; i++) {
			if (objects[i].order == objOrder + 1) {
				objects[i].order--;
				objects[idx].order++;
				break;
			}
		}
	}

	public void moveBackward(GraphObjectView obj) {

		int idx = findObject(obj);
		int objOrder = objects[idx].order;
		for (int i = 0; i < objects.length; i++) {
			if (objects[i].order == objOrder - 1) {
				objects[i].order++;
				objects[idx].order--;
				break;
			}
		}
	}

	void setObjectBoundaryChanged(GraphObjectView obj) {

		int index = findObject(obj);
		if (index != -1) {
			objects[index].updatePosition();
		}
		modified = true;
	}

	GraphObjectView[] getObjectToDraw() {

		// long t1 = System.currentTimeMillis();

		List<DepotObject> objectsInInterval = new ArrayList<DepotObject>();
		for (DepotObject object : objects) {
			// TODO no significant performance enhancement could be observed by
			// filtering out objects
			// beside the visible area !!
			// if (objects[i].x2 >= x1 && objects[i].x1 <= x2 && objects[i].y2
			// >= y1
			// && objects[i].y1 <= y2) {
			objectsInInterval.add(object);
			// }
		}

		objectsInInterval = sortByDrawingOrder(objectsInInterval);

		GraphObjectView[] result = extractVisualObjects(objectsInInterval);

		// System.err.println("sequenciell search for objects to draw: "
		// + (System.currentTimeMillis() - t1) + " ms");
		return result;
	}

	GraphObjectView[] extractVisualObjects(DepotObject[] depotObject) {

		GraphObjectView result[] = new GraphObjectView[depotObject.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = depotObject[i].object;
		}
		return result;
	}

	GraphObjectView[] extractVisualObjects(List<DepotObject> depotObject) {

		GraphObjectView result[] = new GraphObjectView[depotObject.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = depotObject.get(i).object;
		}
		return result;
	}

	int findObject(GraphObjectView obj) {

		for (int i = 0; i < objects.length; i++) {
			if (objects[i].object == obj) {
				return i;
			}
		}
		return -1;
	}

	public GraphObjectView getObject(long id) {

		for (DepotObject object : objects) {
			if (object.object.getID() == id) {
				return object.object;
			}
		}
		return null;
	}

	public GraphObjectView[] getObjects() {

		return extractVisualObjects(objects);
	}

	public void clear() {

		this.objects = new DepotObject[0];
		this.expansion = new Rectangle();
		this.modified = true;
	}

	public Rectangle getTotalExpansion() {

		if (objects.length == 0) {
			return new Rectangle();
		}

		if (modified) {
			int x1 = objects[0].x1;
			int x2 = objects[0].x2;
			int y1 = objects[0].y1;
			int y2 = objects[0].y2;

			for (int i = 1; i < objects.length; i++) {

				x1 = Math.min(x1, objects[i].x1);
				x2 = Math.max(x2, objects[i].x2);
				y1 = Math.min(y1, objects[i].y1);
				y2 = Math.max(y2, objects[i].y2);
			}
			expansion.x = x1;
			expansion.y = y1;
			expansion.width = x2 - x1;
			expansion.height = y2 - y1;
			modified = false;
		}
		return expansion;
	}
}
