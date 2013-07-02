package com.visiors.visualstage.handler;

/**
 * A listener will be notified whenever the undo-redo-stack is changed.
 */
public interface UndoListener
{
    public void undoStackModified();
}
