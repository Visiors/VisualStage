package com.visiors.visualstage.renderer.cache;

import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;

public interface ShapeOffScreenRenderer {

	void render(AWTCanvas awtCanvas, DrawingContext context, DrawingSubject subject);

	void invalidate();
}
