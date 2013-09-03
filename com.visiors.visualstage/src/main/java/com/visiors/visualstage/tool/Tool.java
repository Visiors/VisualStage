package com.visiors.visualstage.tool;

import com.visiors.visualstage.handler.ScopeAwareHandler;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;

public interface Tool extends Interactable, ScopeAwareHandler {


	public String getName();

	public void setActive(boolean activated);

	public boolean isActive();

	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop);


}
