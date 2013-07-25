package com.visiors.visualstage.interaction;

import java.util.List;

import com.visiors.visualstage.handler.GraphViewHandler;
import com.visiors.visualstage.handler.ScopeAwareHandler;
import com.visiors.visualstage.interaction.listener.InteractionListener;
import com.visiors.visualstage.renderer.Canvas;

public interface InteractionHandler extends Interactable, GraphViewHandler, ScopeAwareHandler {

	public void registerMode(InteractionMode handler);

	public void setActiveMode(String name);

	public String getActiveMode();

	public InteractionMode getMode(String name);

	public List<InteractionMode> getRegisteredModes();

	public void addInteractionListener(InteractionListener listener);

	public void removeInteractionListener(InteractionListener listener);

	public void combineModes(String[] modes, String alias);

	public void paintOnBackground(Canvas canvas);

	public void paintOnTop(Canvas canvas);

}
