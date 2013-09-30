package com.visiors.visualstage.document.listener;

import com.visiors.visualstage.document.GraphDocument;

public interface GraphDocumentListener {

	public void undoStackModified();

	public void viewInvalid(GraphDocument documen);
	//
	//	void graphManipulated();

	void graphExpansionChanged();

}
