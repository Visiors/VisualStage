package com.visiors.visualstage.handler.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.visiors.visualstage.handler.UndoListener;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.handler.Undoable;

/**
 * Singleton
 * 
 * 
 * @version $Id: $
 */

@Singleton
public class DefaultUndoRedoHandler implements UndoRedoHandler {

    private final UndoStack     undoStack;
    private final List<Integer> bookmarks;
    private boolean             processUndoAction;

    @Inject
    public DefaultUndoRedoHandler() {

        undoStack = new UndoStack();
        bookmarks = new ArrayList<Integer>();
        processUndoAction = false;
    }

    @Override
    public synchronized void setEnabled(boolean enable) {

        processUndoAction = !enable;
    }

    @Override
    public synchronized boolean isEnabled() {

        return !processUndoAction;
    }

    @Override
    public synchronized boolean canUndo() {

        return undoStack.canUndo();
    }

    @Override
    public synchronized boolean canRedo() {

        return undoStack.canRedo();
    }

    @Override
    public synchronized void stratOfGroupAction() {

        undoStack.stratOfGroupAction();

    }

    @Override
    public synchronized void endOfGroupAction() {

        undoStack.endOfGroupAction();
    }

    @Override
    public synchronized void registerAction(Undoable client, Object data) {

        if (processUndoAction) {
            return;
        }
        undoStack.registerAction(new UndoCommand(client, data));

        fireUndoStackModified();
    }

    @Override
    public synchronized boolean undoRedoInProcess() {

        return processUndoAction;
    }

    @Override
    public synchronized void undo() {

        UndoCommand[] actions = undoStack.undo();
        if (actions == null || processUndoAction) {
            return;
        }
        try {
            processUndoAction = true;
            for (UndoCommand undoCommand : actions) {
                undoCommand.undo();
            }
        } finally {
            processUndoAction = false;
        }
        fireUndoStackModified();
    }

    @Override
    public synchronized void redo() {

        UndoCommand[] actions = undoStack.redo();
        if (actions == null || processUndoAction) {
            return;
        }

        try {
            processUndoAction = true;
            for (UndoCommand undoCommand : actions) {
                undoCommand.redo();
            }
        } finally {
            processUndoAction = false;
        }
        fireUndoStackModified();
    }

    @Override
    public synchronized int setBookmark() {

        // TODO throw exception if setting bookmark withhin action groups!!
        bookmarks.add(new Integer(undoStack.pointer));
        return bookmarks.size() - 1;
    }

    @Override
    public synchronized void restoreBookmark(int bookmarkID) {

        if (bookmarkID > bookmarks.size() - 1 || bookmarkID < 0) {
            return;
        }
        int restorePoint = bookmarks.get(bookmarkID).intValue();
        int undoSteps = undoStack.pointer - restorePoint;

        for (int i = 0; i < undoSteps; i++) {
            undo();
        }
    }

    @Override
    public synchronized void clearHistory(int bookmarkID) {

        if (bookmarkID > bookmarks.size() - 1 || bookmarkID < 0) {
            return;
        }
        int restorePoint = bookmarks.get(bookmarkID).intValue();

        undoStack.removeHistory(restorePoint);
    }

    @Override
    public synchronized void clearHistory() {

        undoStack.removeHistory();
        bookmarks.clear();
        fireUndoStackModified();
    }

    // ===================================================
    // sending notification to listener

    protected List<UndoListener> undoRedoListener = new ArrayList<UndoListener>();

    @Override
    public void addUndoListener(UndoListener listener) {

        if (!undoRedoListener.contains(listener)) {
            undoRedoListener.add(listener);
        }
    }

    @Override
    public void removeUndoListener(UndoListener listener) {

        undoRedoListener.remove(listener);
    }

    protected void fireUndoStackModified() {

        for (UndoListener l : undoRedoListener) {
            l.undoStackModified();
        }
    }

}
