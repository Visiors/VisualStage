package com.visiors.visualstage.interaction.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.exception.InvalidToolNameException;
import com.visiors.visualstage.interaction.impl.readonlymode.ReadOnlyTool;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.Tool;
import com.visiors.visualstage.tool.ToolManager;

/**
 * This class allows registering tools which implement the interface
 * {@link Tool}. Tools can be activate switching between them. It registered the
 * following four basic interaction-tool:
 * <ul>
 * <li> {@link ReadOnlyTool}</li>
 * <li> {@link ToolllingTool}</li>
 * <li> {@link EdgeCreationTool}</li>
 * <li> {@link NodeCreationTool}</li>
 * </ul>
 * Custom {@link Tool}s can be registered and use at the run-time.
 */
public class DefaultToolManager implements ToolManager {

	public static final String TOOL_MOVE_TOOL     = "Object Move Tool";
	public static final String TOOL_OBJECT_EVENT_MEDIATOR = "Object event mediator Tool";
	public static final String TOOL_EDGE_CREATION     = "Edge Creation Tool";
	public static final String TOOL_AUTO_ALIGNMENT    = "Auto Alignment Tool";
	public static final String TOOL_ARRANGEMENT       = "Arrangemen Toolt";
	public static final String TOOL_PORT_EDIT         = "Port Editing Tool";
	public static final String TOOL_SELECTION 		  = "Selection Tool";
	public static final String TOOL_MARQUEE_SELECTION = "Marquee Selection Tool";
	public static final String TOOL_NODE_CREATION     = "Node Creation Tool";


	private final Map<String, Tool> tools = new HashMap<String, Tool>();

	public DefaultToolManager() {

		/* Register the basic tools */

		registerTool(new SelectionTool(TOOL_SELECTION));
		registerTool(new MarqueeSelectionTool(TOOL_MARQUEE_SELECTION));
		registerTool(new ObjectEditTool(TOOL_OBJECT_EVENT_MEDIATOR));
		registerTool(new MoveObjectTool(TOOL_MOVE_TOOL));
		// registerTool(new EdgeCreationTool());
		// registerTool(new NodeCreationTool());
		// registerTool(new AutoSnapTool());
		// registerTool(new PortEditingTool());
		// registerTool(new FormComposeTool());
		activateTool(TOOL_SELECTION, true);

	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		for ( Tool tool : tools.values()) {
			tool.setScope(graphDocument);
		}
	}

	@Override
	public void registerTool(Tool tool) {

		if (tools.containsKey(tool.getName())) {
			throw new IllegalArgumentException("An interaction tool with this name is already registered");
		}
		tools.put(tool.getName(), tool);
	}

	@Override
	public Tool getTool(String toolName) {

		return tools.get(toolName);
	}

	@Override
	public List<Tool> getAllTools() {

		return new ArrayList<Tool>(tools.values());
	}

	@Override
	public void deactivateAllTools() {

		for ( Tool tool : tools.values()) {
			if (!tool.isActive()) {
				tool.setActive(false);
			}
		}
	}

	@Override
	public List<Tool> getActiveTools() {

		final List<Tool> result = new ArrayList<Tool>();
		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				result.add(tool);
			}
		}
		return result;
	}

	@Override
	public void activateTool(String name, boolean activate) {

		if (!tools.containsKey(name)) {

			throw new InvalidToolNameException("Invalid tool. A tool with the name '" + name
					+ "' could not be found. Please check the "
					+ "' was not registered");
		}

		for ( Tool tool : tools.values()) {
			if (!tool.isActive()) {
				tool.setActive(activate);
			}
		}
	}

	/* Delegate all mouse and key events to currently active interaction-tool */

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (context.getResolution() == Resolution.SCREEN) {
			for ( Tool tool : tools.values()) {
				if (tool.isActive()) {
					tool.drawHints(awtCanvas, context, onTop);
				}
			}
		}
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				if (tool.mousePressed(pt, button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				if (tool.mouseReleased(pt, button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				if (tool.mouseDoubleClicked(pt, button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				if (tool.mouseMoved(pt, button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				if (tool.mouseDragged(pt, button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				if (tool.keyPressed(keyChar, keyCode)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				if (tool.keyReleased(keyChar, keyCode)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void cancelInteraction() {

		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				tool.cancelInteraction();
			}
		}
	}

	@Override
	public void terminateInteraction() {

		for ( Tool tool : tools.values()) {
			if (tool.isActive()) {
				tool.terminateInteraction();
			}
		}
	}

	@Override
	public boolean isInteracting() {

		for ( Tool tool : tools.values()) {
			if (tool.isInteracting()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getPreferredCursor() {

		int cursor = Interactable.CURSOR_DEFAULT;
		for ( Tool tool : tools.values()) {

			if (tool.isActive()) {
				cursor = tool.getPreferredCursor();
				if (cursor != Interactable.CURSOR_DEFAULT) {
					break;
				}
			}
		}
		return cursor;
	}

}
