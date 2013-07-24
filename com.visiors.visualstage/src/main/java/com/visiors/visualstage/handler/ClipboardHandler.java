package com.visiors.visualstage.handler;


public interface ClipboardHandler extends ScopeAwareHandler {

    /**
     * Copying is only possible if all objects coming from the same level, i.e. all given object have the same parent
     * 
     * @param objects Objects that ware going to be copied
     * @return true if copying given objects can be curried out
     */

    public boolean canCopy();

    public void copySelection();

    public boolean canPaste();

    public void paste();

    public void clear();

}
