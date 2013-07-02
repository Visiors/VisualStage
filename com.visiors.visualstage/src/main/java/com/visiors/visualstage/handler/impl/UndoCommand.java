package com.visiors.visualstage.handler.impl;

import com.visiors.visualstage.handler.Undoable;

class UndoCommand {

    private final Undoable owner;
    private final Object   undoData;

    UndoCommand(Undoable owner, Object undoData) {

        this.owner = owner;
        this.undoData = undoData;
    }

    public void redo() {

        owner.redo(undoData);
    }

    public void undo() {

        owner.undo(undoData);
    }

}
