package com.visiors.visualstage.util;

public class PrinterUtil {
    /** constant list of available paper type names.*/
    public final static String[] paperTypeNames =
        new String[] {
            "A4",
            "16K",
            "A2",
            "A3",
            "A5",
            "B5",
            "B5 (JIS)",
            "B5 (ISO)",
            "C5 Envelope",
            "Common 10 Envelope",
            "DL Envelope",
            "Envelope #10 ",
            "Envelope B5",
            "Envelope C5",
            "Envelope DL",
            "Envelope Monarch",
            "Executive",
            "Executive JIS",
            "Legal",
            "Letter",
            "US Legal",
            "US Legal small",
            "US Letter",
            "US Letter small",
            "A1"};

    /** Constant list of sizes in mm of the predefined paper types. */
    public final static double[][] paperTypeSizes = {
            new double[] { 210.0, 297.0 },
            new double[] { 196.80, 273.0 },
            new double[] { 420.0, 594.0 },
            new double[] { 297.0, 420.0 },
            new double[] { 148.0, 210.0 },
            new double[] { 182.0, 257.10 },            
            new double[] { 182.0, 257.0 },
            new double[] { 176.0, 249.90 },
            new double[] { 161.90, 228.90 },
            new double[] { 104.648, 241.290 },
            new double[] { 110.0, 220.10 },
            new double[] { 104.648, 241.290 },
            new double[] { 176.00, 250.0 },
            new double[] { 162.0, 229.0 },
            new double[] { 110.0, 220.0 },
            new double[] { 98.2980, 190.5 },
            new double[] { 184.149, 266.70 },
            new double[] { 215.899, 329.946 },
            new double[] { 215.899, 355.599 },
            new double[] { 215.899, 279.40 },
            new double[] { 215.899, 355.599 },
            new double[] { 215.899, 355.599 },
            new double[] { 215.899, 279.40 },
            new double[] { 215.899, 279.40 },
            new double[] { 594.0, 841.0 }
    };
    
    

    /** The Java print resolution is at 72 dpi. */
    public static final double PRINT_DPI = 72.0;
    /** 25.40 mm per inch */
    public static final double mmPerInch = 25.4;
    
    public static  double[] getPaperSize(String paper) {
    	double[] ret = new double[2];
    	for (int i = 0; i < paperTypeNames.length; i++) {

    		if(paperTypeNames[i].equalsIgnoreCase(paper)) { 
    			
    			ret[0] = paperTypeSizes[i][0] / PrinterUtil.mmPerInch * PrinterUtil.PRINT_DPI;
    			ret[1] = paperTypeSizes[i][1] / PrinterUtil.mmPerInch * PrinterUtil.PRINT_DPI;
    			return ret;
    		}
		}
    	return ret;
    }

	
}
