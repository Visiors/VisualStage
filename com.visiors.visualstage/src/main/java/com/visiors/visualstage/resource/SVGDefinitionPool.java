package com.visiors.visualstage.resource;

import java.util.HashMap;
import java.util.Map;



public class SVGDefinitionPool
{
//	 private static final SoftThreadLocal<HashMap> pool = new SoftThreadLocal<HashMap>() {
//		@Override
//		protected synchronized HashMap initialValue() {
//			return new HashMap();
//		}
//	};
	
	static Map<String, SVGDefinition> poolLocal = new HashMap<String, SVGDefinition>();
			    
	public SVGDefinitionPool() {
	}
	
    public static final void pool(String key, SVGDefinition presentation) {
//    	final HashMap<String, String> poolLocal =  pool.get();
    	poolLocal.put(key, presentation);
    }
    
    public static final void remove(String key) {
//    	final HashMap<String, String> poolLocal = pool.get();
    	poolLocal.remove(key);
	}
    
    public static boolean containsKey(String key) {
//    	final HashMap<String, String> poolLocal = pool.get();
    	return poolLocal.containsKey(key);
    }
        
    public static final SVGDefinition  get(String key) {
//    	final HashMap<String, String> poolLocal = pool.get();
    	return poolLocal.get(key);
    }
    
    
}




   