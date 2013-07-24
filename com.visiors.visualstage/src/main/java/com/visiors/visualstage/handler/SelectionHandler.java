package com.visiors.visualstage.handler;

import java.util.List;

import com.visiors.visualstage.graph.view.VisualGraphObject;

public interface SelectionHandler extends ScopeAwareHandler {

	public void setMuliSelectionMode(boolean multiselection);

	public boolean isMuliSelectionMode();

	public int getSelectionCount();

	public void clearSelection();

	/**
	 * This method inverts the selection state of the specified object. It
	 * considers the current selection mode: while in multi-selection mode only
	 * the state of the specified object will be changed, in single-selection
	 * mode all other objects will be unselected.
	 * 
	 * @param graphObject
	 *            the {@link VisualGraphObject} of which the selection state is to
	 *            be changed
	 */
	public void invertObjectSelection(VisualGraphObject graphObject);

	public void select(VisualGraphObject graphObject, boolean selected);

	public void select(List<VisualGraphObject> graphObject);

	public List<VisualGraphObject> getSelection();

	public void invalidate();

}
