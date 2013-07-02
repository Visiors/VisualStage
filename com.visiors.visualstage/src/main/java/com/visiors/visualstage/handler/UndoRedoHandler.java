package com.visiors.visualstage.handler;



/**
 * An instance of this class can be used to record actions that are desired to
 * be undone/redone by user.
 * 
 */
public interface UndoRedoHandler {
    
    public void setEnabled(boolean enable);

    public boolean isEnabled();

    public void clearHistory();

    public boolean canUndo();

    public boolean canRedo();

    public void undo();

    public void redo();

    public void registerAction(Undoable owner, Object data);

    public boolean undoRedoInProcess();
    
    public void stratOfGroupAction();

    public void endOfGroupAction();

    public void addUndoListener(UndoListener listener);

    public void removeUndoListener(UndoListener listener);

	int setBookmark();

	void restoreBookmark(int bookmarkID);

	void clearHistory(int bookmarkID);
	
}
