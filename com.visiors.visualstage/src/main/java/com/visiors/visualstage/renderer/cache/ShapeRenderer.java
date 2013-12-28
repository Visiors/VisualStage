package com.visiors.visualstage.renderer.cache;

import java.awt.Image;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;

public interface ShapeRenderer {

	public Rectangle getBounds();

	public Image getSnapshot(DrawingContext context, DrawingSubject subject);

	public void draw(AWTCanvas awtCanvas, Image image);
}
