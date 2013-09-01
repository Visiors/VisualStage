package com.visiors.visualstage.interaction;

import java.util.List;

import com.visiors.visualstage.handler.GraphViewHandler;
import com.visiors.visualstage.handler.ScopeAwareHandler;
import com.visiors.visualstage.interaction.listener.InteractionListener;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;

public interface InteractionHandler extends Interactable, GraphViewHandler, ScopeAwareHandler {

	public void registerMode(Tool handler);

	public void setActiveMode(String name);

	public String getActiveMode();

	public Tool getMode(String name);

	public List<Tool> getRegisteredModes();

	public void addInteractionListener(InteractionListener listener);

	public void removeInteractionListener(InteractionListener listener);

	public void combineModes(String[] modes, String alias);

	public void paintOnBackground(AWTCanvas awtCanvas, DrawingContext context);

	public void paintOnTop(AWTCanvas awtCanvas, DrawingContext context);

}
