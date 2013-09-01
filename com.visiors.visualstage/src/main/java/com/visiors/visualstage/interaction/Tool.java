package com.visiors.visualstage.interaction;

import com.visiors.visualstage.handler.ScopeAwareHandler;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;

public interface Tool extends Interactable, ScopeAwareHandler {

	public String getName();

	public void setActive(boolean activated);

	public boolean isActive();

	public void paintOnBackground(AWTCanvas awtCanvas, DrawingContext context);

	public void paintOnTop(AWTCanvas awtCanvas, DrawingContext context);

}
