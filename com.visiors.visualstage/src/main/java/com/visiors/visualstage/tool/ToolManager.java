package com.visiors.visualstage.tool;

import java.util.List;

import com.visiors.visualstage.handler.GraphViewHandler;
import com.visiors.visualstage.handler.ScopeAwareHandler;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;

public interface ToolManager extends Interactable, GraphViewHandler, ScopeAwareHandler {

	public void registerTool(Tool handler);

	public List<Tool> getAllTools();

	public Tool getTool(String name);

	public void activateTool(String name, boolean activate);

	public void deactivateAllTools();

	public List<Tool> getActiveTools();

	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop);
}
