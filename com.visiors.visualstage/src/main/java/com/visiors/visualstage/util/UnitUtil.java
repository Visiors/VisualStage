package com.visiors.visualstage.util;

public class UnitUtil {

	/** Conversion factor to calculate point to pixcel */
	public static final double PT_TO_PX = 1.25;

	/** Conversion factor to calculate pica to pixcel */
	public static final double PC_TO_PX = 15;

    /** Conversion factor to calculate inch to centimeters */
    public static final double INCH_TO_CM = 2.54;
    
    /** 25.40 mm per inch */
    private static final double mmPerInch = INCH_TO_CM * 10;

    /** The Java print resolution is at 72 dpi. */
    public static final double PRINT_DPI = 72.0;
    
    
    public static double strLength2px(String strValue)
    {
        if (strValue == null || strValue.isEmpty())
            return 0.0;
        
        int len = strValue.length();
        int ptr = 0;
        char ch;
        do {
			ch = strValue.charAt(ptr);
			if(!Character.isDigit(ch) && ch != '.' && ch != '-'  && ch != '+')
				break;
		} while (++ptr < len);
        
        double value = Double.parseDouble(strValue.substring(0, ptr).trim());
        if(ptr != len) {
            String unit = strValue.substring(ptr).toLowerCase().trim();
            if(unit.equals("mm"))
            	return mm2px(value);
            if(unit.equals("in"))
            	return inch2mm(value);
            else if(unit.equals("cm"))
            	return mm2px(value) * 10;
            else if(unit.equals("pt"))
            	return pt2px(value);
            else if(unit.equals("pc"))
            	return pc2px(value);
        }
        return value;
    }
    
    
    public static double px2mm(double pixel)
    {
        if (pixel == 0)
            return 0.0;
        double inch = pixel / PRINT_DPI;
        return inch * mmPerInch;
    }

    public static double pt2px(double pt)
    {
    	return pt * PT_TO_PX;
    }
    
    public static double pc2px(double pt)
    {
    	return pt * PC_TO_PX;
    }
    
	 public static double mm2px(double mm)
	 {
	        if (mm == 0)
	            return 0;

	        // to inch
	        double inch = mm / mmPerInch;
	        
	        // to 1/72 inch
	        return inch * PRINT_DPI;
	    }
	    
	    /**
	     * Converts an inch value to mm.
	     * 
	     * @param inch  The distance in inch
	     * @return      The distance in mm
	     */
	    public static double inch2mm(double inch)
	    {
	        return inch * mmPerInch;
	    }

	    /**
	     * Converts an mm value to point.
	     * 
	     * @param mm  The distance in mm
	     * @return    The distance in points
	     */
	    public static double mm2pt(double mm)
	    {
	        return (mm / mmPerInch) * PRINT_DPI;
	    }
}
