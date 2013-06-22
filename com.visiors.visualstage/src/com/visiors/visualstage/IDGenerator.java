package com.visiors.visualstage;

public class IDGenerator
{

    /** static counter for keeping track of the next assignable id */
    private static long nextid = 100;


    /**
     * @return the next available unique id.
     */
    public static long getNextID ()
    {
        return nextid++;
    }
    
    public static void considerExistingID(long id)
    {
        nextid = Math.max(nextid, id+1);
    }
}