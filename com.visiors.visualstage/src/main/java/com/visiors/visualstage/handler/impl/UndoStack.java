package com.visiors.visualstage.handler.impl;

import java.util.Vector;

class UndoStack {

    protected Vector actions = new Vector();

    protected int    pointer;
    private int      groupAction;
    private boolean  firstGroupAction;

    void removeHistory() {

        pointer = 0;
        groupAction = 0;
        actions.clear();
    }

    UndoCommand[] undo() {

        if (pointer > 0) {
            return releaseAction(actions.get(--pointer), true);
        }

        return null;
    }

    UndoCommand[] redo() {

        if (pointer < actions.size()) {
            return releaseAction(actions.get(pointer++), false);
        }
        return null;
    }

    boolean canUndo() {

        return pointer > 0;
    }

    boolean canRedo() {

        return pointer < actions.size();
    }

    void stratOfGroupAction() {

        if (++groupAction == 1) {
            firstGroupAction = true;
        }

    }

    void endOfGroupAction() {

        --groupAction;
    }

    private UndoCommand[] releaseAction(Object entry, boolean undo) {

        Vector actionSet = new Vector();
        UndoCommand[] result = null;

        if (entry instanceof UndoCommand) // simple action
        {
            actionSet.add(entry);
            result = (UndoCommand[]) actionSet.toArray(new UndoCommand[0]);
        } else if (entry instanceof Vector)// release group for a group of
        // actions
        {
            result = (UndoCommand[]) ((Vector) entry).toArray(new UndoCommand[0]);
        }
        if (undo && result != null) {
            result = reverse(result);
        }

        return result;
    }

    private UndoCommand[] reverse(UndoCommand[] b) {

        for (int left = 0, right = b.length - 1; left < right; left++, right--) {
            UndoCommand temp = b[left];
            b[left] = b[right];
            b[right] = temp;
        }

        return b;
    }

    void registerAction(UndoCommand action) {

        // 1- if the new action is coming
        // after a undo step, ignore all previous actions before this point.
        if (pointer < actions.size()) {
            actions.setSize(pointer);
        }

        // 2- register the new action:
        // is the action as a member of a group action?
        if (groupAction > 0) {
            Vector groupActions = null;
            // if it is the first action in the group , create a new Vector
            // to store the group members
            if (firstGroupAction) {
                ++pointer;
                groupActions = new Vector();
                actions.add(groupActions);
                firstGroupAction = false;
            } else // get and use the vector for this group action
            {
                groupActions = (Vector) actions.get(pointer - 1);
            }
            groupActions.add(action);
        } else {
            ++pointer;
            actions.add(action);
        }
        if (pointer % 100 == 0) {
            System.err.println("Stack pointer:" + pointer);
            // System.out.println("Stack pointer:" + pointer);
        }

    }

    public void removeHistory(int restorePoint) {

        if (restorePoint < pointer) {
            pointer = restorePoint;
            actions.setSize(restorePoint);
        }
    }

}
