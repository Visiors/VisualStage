package com.visiors.visualstage.renderer.cache;

import java.awt.Image;

import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;


public interface GraphObjectImageProvider {

	Image provide(DrawingContext context, DrawingSubject subject);

	boolean isModified();
}
