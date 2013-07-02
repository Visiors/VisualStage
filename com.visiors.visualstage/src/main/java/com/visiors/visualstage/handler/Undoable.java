package com.visiors.visualstage.handler;


/**
 * This interface must be implemented by all ??? that have to provide 
 * undo/redo functionalities. 
 * An instance of this class can be used to record actions that are desired to
 * be undone/redone by user.
 *  
 */
public interface Undoable
{
  
    public void undo(Object data);
    
    /** recovers the state as is specified by <code>data</code> 
     * @param data recovering data that is need to recover an old state.
     */ 
    public void redo(Object data);
    
}
