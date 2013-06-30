package com.visiors.visualstage.graph.view.listener;

import java.awt.Rectangle;

public interface GraphDocumentListener {

	public void undoStackModified();

	public void viewChanged();

	void graphManipulated();

	void graphExpansionChanged(Rectangle newBoundary);

}
