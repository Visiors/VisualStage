package com.visiors.visualstage.handler;

import com.visiors.visualstage.document.GraphDocument;

public interface ScopeAwareHandler {

	void setScope(GraphDocument graphDocument);
}
