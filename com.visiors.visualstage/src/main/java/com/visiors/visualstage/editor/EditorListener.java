package com.visiors.visualstage.editor;

import java.awt.Rectangle;

import com.visiors.visualstage.document.GraphDocument;


public interface EditorListener {

	void viewInvalid(GraphDocument documen);

	void boundaryChangedListener(Rectangle newBoundary);
}
