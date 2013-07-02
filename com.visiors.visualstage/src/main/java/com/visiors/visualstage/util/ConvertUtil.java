
package com.visiors.visualstage.util;

import java.awt.Font;

public class ConvertUtil
{
    public static int object2int(Object obj)
    {
        return Integer.parseInt(obj.toString());
    }

    public static long object2long(Object obj)
    {
        return Integer.parseInt(obj.toString());
    }
    
    public static double object2double(Object obj)
    {
        return Double.parseDouble(obj.toString());
    }
    public static float object2float(Object obj)
    {
    	return Float.parseFloat(obj.toString());
    }

    public static String object2string(Object obj)
    {
        return obj.toString();
    }

    public static boolean object2boolean(Object obj)
    {
        return Boolean.parseBoolean(obj.toString());
    }

    public static Object int2object(int i)
    {
        return new Integer(i);
    }

    public static Object double2object(double d)
    {
        return new Double(d);
    }

    public static Object boolean2object(boolean b)
    {
        return new Boolean(b);
    }

    public static Object string2object(String s)
    {
        return new String(s);
    }


    public static int fontStyleName2Int(String strStyle) {
    	if( "Bold".equalsIgnoreCase(strStyle))
    		return Font.BOLD;
    	if( "Italic".equalsIgnoreCase(strStyle))
    		return Font.ITALIC;
    	if( "Bold+Italic".equalsIgnoreCase(strStyle))
    		return Font.BOLD | Font.ITALIC;
    	
    	return Font.PLAIN;
    }
    
    
	public static String fontStyle2String(int style) {

		switch (style) {
		case Font.BOLD:
			return "Bold";
		case Font.ITALIC :
			return "Italic";
		case Font.BOLD | Font.ITALIC :
			return "Bold+Italic";
		default:
			return "Normal";
		}
	}
}
