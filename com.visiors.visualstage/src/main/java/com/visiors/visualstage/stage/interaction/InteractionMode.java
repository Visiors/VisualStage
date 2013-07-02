package com.visiors.visualstage.stage.interaction;

import java.awt.Rectangle;

import com.visiors.visualstage.handler.ScopeAware;
import com.visiors.visualstage.renderer.Device;

public interface InteractionMode extends Interactable, ScopeAware {

	public String getName();

	public void setActive(boolean activated);

	public boolean isActive();

	public void paintOnBackground(Device device, Rectangle visibleScreenRect);

	public void paintOnTop(Device device, Rectangle visibleScreenRect);

}
