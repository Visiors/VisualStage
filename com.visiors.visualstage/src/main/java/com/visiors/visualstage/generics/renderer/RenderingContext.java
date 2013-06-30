package com.visiors.visualstage.generics.renderer;


public class RenderingContext {
    
	public enum Subject {OBJECT, SELECTION_INDICATORS, PORTS}
	public enum Resolution {SCREEN, SCREEN_LOW_DETAIL, PRINT, PREVIEW}
    
    public Subject subject ;
    public Resolution resolution;
	public boolean cacheable;
    
    

   
    public RenderingContext(Resolution resolution, Subject subject, boolean cacheable){
    	this.subject = subject;
    	this.resolution = resolution;
    	this.cacheable = cacheable;
    }

    
    @Override
    public boolean equals(Object obj) {
    	

    	RenderingContext other = (RenderingContext) obj;

    	return this == other || 
    		(subject.equals(other.subject) && resolution.equals(other.resolution)) ;
    }

    @Override
    public int hashCode() {
    	String ctx = resolution.toString() + subject.toString();
    	return ctx.hashCode();
    }
    
    @Override
    public String toString() {
    	return "Subject: " + subject.toString() + 
    		", Resoluton: " + resolution.toString();
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
    	
    	return new RenderingContext(resolution, subject, cacheable);
    }
}
