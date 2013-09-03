package com.visiors.visualstage.document.listener;

import java.awt.Rectangle;

public interface GraphDocumentListener {

	public void undoStackModified();

	//	public void viewChanged();
	//
	//	void graphManipulated();

	void graphExpansionChanged(Rectangle newBoundary);

}
