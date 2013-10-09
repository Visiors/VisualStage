package com.visiors.visualstage.tool.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

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

	public static final String TOOL_SCROLLBAR = "Scroll-Bar Tool";
	public static final String TOOL_SELECTION = "Selection Tool";
	public static final String TOOL_MARQUEE_SELECTION = "Marquee Selection Tool";
	public static final String TOOL_OBJECT_EVENT_MEDIATOR = "Object event mediator Tool";
	public static final String TOOL_MOVE_SELECTION = "Object Move Tool";
	public static final String TOOL_DUPLICATE_ON_MOVE = "Duplicate on Move Tool";
	public static final String TOOL_NAVIGATION = "Navigation Tool";

	public static final String TOOL_EDGE_CREATION = "Edge Creation Tool";
	public static final String TOOL_AUTO_ALIGNMENT = "Auto Alignment Tool";
	public static final String TOOL_ARRANGEMENT = "Arrangemen Toolt";
	public static final String TOOL_PORT_EDIT = "Port Editing Tool";
	public static final String TOOL_NODE_CREATION = "Node Creation Tool";

	private final List<Tool> tools = new ArrayList<Tool>();
	private GraphDocument graphDocument;

	public DefaultToolManager() {

		/* Register the basic tools */

		registerTool(new ScrollTool(TOOL_SCROLLBAR));
		registerTool(new NavigationTool(TOOL_NAVIGATION));
		registerTool(new SelectionTool(TOOL_SELECTION));
		registerTool(new MarqueeSelectionTool(TOOL_MARQUEE_SELECTION));
		registerTool(new ObjectEditTool(TOOL_OBJECT_EVENT_MEDIATOR));
		registerTool(new DuplicateOnMoveTool(TOOL_DUPLICATE_ON_MOVE));
		registerTool(new MoveSelectionTool(TOOL_MOVE_SELECTION));
		//		registerTool(new EdgeCreationTool());
		// registerTool(new NodeCreationTool());
		// registerTool(new AutoSnapTool());
		// registerTool(new PortEditingTool());
		// registerTool(new FormComposeTool());
		activateTool(TOOL_SCROLLBAR, true);
		activateTool(TOOL_SELECTION, true);
		activateTool(TOOL_MARQUEE_SELECTION, true);
		activateTool(TOOL_DUPLICATE_ON_MOVE, true);
		activateTool(TOOL_OBJECT_EVENT_MEDIATOR, true);
		activateTool(TOOL_MOVE_SELECTION, true);
		activateTool(TOOL_NAVIGATION, true);
	}



	@Override
	public void setScope(GraphDocument graphDocument) {

		this.graphDocument = graphDocument;
		for (final Tool tool : tools) {
			tool.setScope(graphDocument);
		}
	}

	@Override
	public void registerTool(Tool tool) {

		if (getTool(tool.getName()) != null) {
			throw new IllegalArgumentException("An interaction tool with this name is already registered");
		}
		tools.add(tool);
	}

	@Override
	public Tool getTool(String toolName) {

		for (Tool tool : tools) {
			if(tool.getName() != null && tool.getName().equals(toolName)) {
				return tool;
			}
		}
		return null;
	}

	@Override
	public List<Tool> getAllTools() {

		return new ArrayList<Tool>(tools);
	}

	@Override
	public void deactivateAllTools() {

		for (final Tool tool : tools) {
			if (!tool.isActive()) {
				tool.setActive(false);
			}
		}
	}

	@Override
	public List<Tool> getActiveTools() {

		final List<Tool> result = new ArrayList<Tool>();
		for (final Tool tool : tools) {
			if (tool.isActive()) {
				result.add(tool);
			}
		}
		return result;
	}

	@Override
	public void activateTool(String name, boolean activate) {

		final Tool tool = getTool(name);
		if (tool == null) {

			throw new InvalidToolNameException("Invalid tool. A tool with the name '" + name
					+ "' could not be found. Please check the " + "' was not registered");
		}
		tool.setActive(activate);
	}



	/* Delegate all mouse and key events to currently active interaction-tool */

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (context.getResolution() == Resolution.SCREEN) {
			for (final Tool tool : tools) {
				if (tool.isActive()) {
					tool.drawHints(awtCanvas, context, onTop);
				}
			}
		}
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		for (final Tool tool : tools) {
			if (tool.isActive()) {

				if (tool.mousePressed(transform(pt), button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		for (final Tool tool : tools) {
			if (tool.isActive()) {
				if (tool.mouseReleased(transform(pt), button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		for (final Tool tool : tools) {
			if (tool.isActive()) {
				if (tool.mouseDoubleClicked(transform(pt), button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		for (final Tool tool : tools) {
			if (tool.isActive()) {
				if (tool.mouseMoved(transform(pt), button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		for (final Tool tool : tools) {
			if (tool.isActive()) {
				if (tool.mouseDragged(transform(pt), button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		for (final Tool tool : tools) {
			if (tool.isActive()) {
				if (tool.mouseEntered(transform(pt), button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		for (final Tool tool : tools) {
			if (tool.isActive()) {
				if (tool.mouseExited(transform(pt), button, functionKey)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		for (final Tool tool : tools) {
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

		for (final Tool tool : tools) {
			if (tool.isActive()) {
				if (tool.keyReleased(keyChar, keyCode)) {
					return true;
				}
			}
		}
		return false;
	}


	private final Point transform(Point pt) {

		return graphDocument.getTransformer().transformToGraph(pt);
	}


	@Override
	public void cancelInteraction() {

		for (final Tool tool : tools) {
			if (tool.isActive()) {
				tool.cancelInteraction();
			}
		}
	}

	@Override
	public void terminateInteraction() {

		for (final Tool tool : tools) {
			if (tool.isActive()) {
				tool.terminateInteraction();
			}
		}
	}

	@Override
	public boolean isInteracting() {

		for (final Tool tool : tools) {
			if (tool.isInteracting()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getPreferredCursor() {

		int cursor = Interactable.CURSOR_DEFAULT;
		for (final Tool tool : tools) {

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
