package com.visiors.visualstage.interaction;

import java.awt.Rectangle;

import com.visiors.visualstage.handler.ScopeAwareHandler;
import com.visiors.visualstage.renderer.Canvas;

public interface InteractionMode extends Interactable, ScopeAwareHandler {

	public String getName();

	public void setActive(boolean activated);

	public boolean isActive();

	public void paintOnBackground(Canvas canvas, Rectangle visibleScreenRect);

	public void paintOnTop(Canvas canvas, Rectangle visibleScreenRect);

}
