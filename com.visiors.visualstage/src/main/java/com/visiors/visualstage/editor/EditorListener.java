package com.visiors.visualstage.editor;

import com.visiors.visualstage.document.GraphDocument;


public interface EditorListener {

	void viewInvalid(GraphDocument documen);

	void boundaryChangedListener();
}
