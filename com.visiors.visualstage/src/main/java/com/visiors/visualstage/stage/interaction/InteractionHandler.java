package com.visiors.visualstage.stage.interaction;

import java.awt.Rectangle;
import java.util.List;

import com.visiors.visualstage.handler.GraphViewHandler;
import com.visiors.visualstage.handler.ScopeAware;
import com.visiors.visualstage.renderer.Device;
import com.visiors.visualstage.renderer.RenderingContext.Resolution;
import com.visiors.visualstage.stage.interaction.listener.InteractionListener;

public interface InteractionHandler extends Interactable, GraphViewHandler, ScopeAware {

	public void registerMode(InteractionMode handler);

	public void setActiveMode(String name);

	public String getActiveMode();

	public InteractionMode getMode(String name);

	public List<InteractionMode> getRegisteredModes();

	public void addInteractionListener(InteractionListener listener);

	public void removeInteractionListener(InteractionListener listener);

	public void combineModes(String[] modes, String alias);

	public void paintOnBackground(Device device, Rectangle visibleScreenRect, Resolution resolution);

	public void paintOnTop(Device device, Rectangle visibleScreenRect, Resolution resolution);

}
