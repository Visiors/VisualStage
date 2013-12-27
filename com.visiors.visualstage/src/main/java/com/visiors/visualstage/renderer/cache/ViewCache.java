package com.visiors.visualstage.renderer.cache;

import java.awt.Image;

import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;

public interface ViewCache {

	public Image get(DrawingContext context, DrawingSubject subject);

	public void refresh();

	void setProvider(GraphObjectImageProvider provider);
}
