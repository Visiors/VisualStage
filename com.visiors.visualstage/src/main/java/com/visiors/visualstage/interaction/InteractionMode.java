package com.visiors.visualstage.interaction;

import com.visiors.visualstage.handler.ScopeAwareHandler;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;

public interface InteractionMode extends Interactable, ScopeAwareHandler {

	public String getName();

	public void setActive(boolean activated);

	public boolean isActive();

	public void paintOnBackground(Canvas canvas, DrawingContext context);

	public void paintOnTop(Canvas canvas, DrawingContext context);

}
