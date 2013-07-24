package com.visiors.visualstage.handler;


public interface GroupingHandler extends ScopeAwareHandler {

    boolean canGroup();

    boolean canUngroup();

    void groupSelection(String graphviewToUse);

    void ungroupSelection();

}
