package com.visiors.visualstage.interaction.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.visiors.visualstage.constants.GraphStageConstants;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.interaction.InteractionHandler;
import com.visiors.visualstage.interaction.InteractionMode;
import com.visiors.visualstage.interaction.impl.attachnemt.FormComposeMode;
import com.visiors.visualstage.interaction.impl.edgecreation.EdgeCreationMode;
import com.visiors.visualstage.interaction.impl.marquee.MarqueeSelectionMode;
import com.visiors.visualstage.interaction.impl.modelling.ModellingMode;
import com.visiors.visualstage.interaction.impl.nodecreateion.NodeCreationMode;
import com.visiors.visualstage.interaction.impl.portedit.PortEditingMode;
import com.visiors.visualstage.interaction.impl.readonlymode.ReadOnlyMode;
import com.visiors.visualstage.interaction.impl.snap.AutoSnapMode;
import com.visiors.visualstage.interaction.listener.InteractionListener;
import com.visiors.visualstage.renderer.DrawingContext.Resolution;

/**
 * This class allows registering different {@link InteractionMode}s and
 * switching between them. It registered the following four basic
 * interaction-handler:
 * <ul>
 * <li> {@link ReadOnlyMode}</li>
 * <li> {@link ModellingMode}</li>
 * <li> {@link EdgeCreationMode}</li>
 * <li> {@link NodeCreationMode}</li>
 * </ul>
 * Custom {@link InteractionMode}s can be registered and use at the run-time.
 */
public class DefaultInteractionHandler implements InteractionHandler {

	private String lastMode;
	private String currentMode;
	private final Map<String, InteractionMode> modes = new HashMap<String, InteractionMode>();
	private final Map<String, String[]> interactionGroup = new HashMap<String, String[]>();
	boolean combinedAction;

	@Inject
	public DefaultInteractionHandler() {

		/* Register the basic modes */
		registerMode(new ReadOnlyMode());
		registerMode(new ModellingMode());
		registerMode(new EdgeCreationMode());
		registerMode(new NodeCreationMode());
		registerMode(new AutoSnapMode());
		registerMode(new MarqueeSelectionMode());
		registerMode(new PortEditingMode());
		registerMode(new FormComposeMode());

		combineModes(new String[] { GraphStageConstants.MODE_MODELING,
				GraphStageConstants.MODE_EDGE_CREATION, GraphStageConstants.MODE_MARQUEE_SELECTION,
				GraphStageConstants.MODE_AUTO_ALIGNMENT }, GraphStageConstants.MODE_EDIT);

		setActiveMode(GraphStageConstants.MODE_EDIT);

	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		List<InteractionMode> regModes = getRegisteredModes();
		for (InteractionMode interactionMode : regModes) {
			interactionMode.setScope(graphDocument);
		}
	}

	@Override
	public void registerMode(InteractionMode handler) {

		if (modes.containsKey(handler.getName())) {
			throw new IllegalArgumentException(
					"An interaction handler with this name is already registered");
		}
		modes.put(handler.getName(), handler);
	}

	@Override
	public void combineModes(String[] interactionNames, String alias) {

		interactionGroup.put(alias, interactionNames);
	}

	@Override
	public void setActiveMode(String name) {

		if (!modes.containsKey(name) && !interactionGroup.containsKey(name)) {
			throw new IllegalArgumentException("An interaction handler with the name '" + name
					+ "' could not be found. Please check the "
					+ "name, or check if the associated interaction-handler is properly registered");
		}

		if (currentMode == null || !currentMode.equals(name)) {

			if (currentMode != null) {
				if (modes.containsKey(currentMode)) {
					modes.get(currentMode).setActive(false);
				} else {
					String[] names = interactionGroup.get(currentMode);
					for (String name2 : names) {
						modes.get(name2).setActive(false);
					}
				}
			}

			lastMode = currentMode;
			currentMode = name;

			if (modes.containsKey(currentMode)) {
				combinedAction = false;
				modes.get(currentMode).setActive(true);
			} else {
				combinedAction = true;
				String[] names = interactionGroup.get(currentMode);
				for (int i = 0; i < names.length; i++) {
					if (!modes.containsKey(names[i])) {

						System.err.println("Waring: interaction service associated with '"
								+ names[i] + "' must be registered first");
						continue;
					}
					modes.get(names[i]).setActive(true);
				}
			}

			fireInteractionModeChanged();
		}
	}

	@Override
	public String getActiveMode() {

		return currentMode;
	}

	@Override
	public InteractionMode getMode(String name) {

		return modes.get(name);
	}

	@Override
	public List<InteractionMode> getRegisteredModes() {

		return new ArrayList<InteractionMode>(modes.values());
	}

	/* Delegate all mouse and key events to currently active interaction-handler */

	@Override
	public void paintOnBackground(Canvas canvas) {

		if (resolution != Resolution.SCREEN) {
			return;
		}
		if (!combinedAction) {
			modes.get(currentMode).paintOnBackground(canvas, visibleScreenRect);
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (String name : names) {
				modes.get(name).paintOnBackground(canvas, visibleScreenRect);
			}
		}
	}

	@Override
	public void paintOnTop(Canvas canvas) {

		if (resolution != Resolution.SCREEN) {
			return;
		}
		if (!combinedAction) {
			modes.get(currentMode).paintOver(canvas);
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (String name : names) {
				modes.get(name).paintOver(canvas);
			}
		}
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		boolean ret = false;
		if (!combinedAction) {
			ret = modes.get(currentMode).mousePressed(pt, button, functionKey);
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (int i = 0; i < names.length && !ret; i++) {
				ret |= modes.get(names[i]).mousePressed(pt, button, functionKey);
			}
		}
		return ret;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		boolean ret = false;
		if (!combinedAction) {
			ret = modes.get(currentMode).mouseReleased(pt, button, functionKey);
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (int i = 0; i < names.length && !ret; i++) {
				ret |= modes.get(names[i]).mouseReleased(pt, button, functionKey);
			}
		}
		return ret;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		boolean ret = false;
		if (!combinedAction) {
			ret = modes.get(currentMode).mouseDoubleClicked(pt, button, functionKey);
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (int i = 0; i < names.length && !ret; i++) {
				ret |= modes.get(names[i]).mouseDoubleClicked(pt, button, functionKey);
			}
		}
		return ret;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		boolean ret = false;
		if (!combinedAction) {
			ret = modes.get(currentMode).mouseMoved(pt, button, functionKey);
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (int i = 0; i < names.length && !ret; i++) {
				ret |= modes.get(names[i]).mouseMoved(pt, button, functionKey);
			}
		}
		return ret;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		boolean ret = false;
		if (!combinedAction) {
			ret = modes.get(currentMode).mouseDragged(pt, button, functionKey);
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (int i = 0; i < names.length && !ret; i++) {
				ret |= modes.get(names[i]).mouseDragged(pt, button, functionKey);
			}
		}
		return ret;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		boolean ret = false;
		if (!combinedAction) {
			ret = modes.get(currentMode).keyPressed(keyChar, keyCode);
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (String name : names) {
				ret |= modes.get(name).keyPressed(keyChar, keyCode);
			}
		}
		return ret;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		boolean ret = false;
		if (!combinedAction) {
			ret = modes.get(currentMode).keyReleased(keyChar, keyCode);
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (String name : names) {
				ret |= modes.get(name).keyReleased(keyChar, keyCode);
			}
		}
		return ret;
	}

	@Override
	public void cancelInteraction() {

		if (!combinedAction) {
			modes.get(currentMode).cancelInteraction();
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (String name : names) {
				modes.get(name).cancelInteraction();
			}
		}
	}

	@Override
	public void terminateInteraction() {

		if (!combinedAction) {
			modes.get(currentMode).terminateInteraction();
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (String name : names) {
				modes.get(name).terminateInteraction();
			}
		}
	}

	@Override
	public boolean isInteracting() {

		boolean ret = false;
		if (!combinedAction) {
			ret = modes.get(currentMode).isInteracting();
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (String name : names) {
				ret |= modes.get(name).isInteracting();
			}
		}
		return ret;
	}

	@Override
	public int getPreferredCursor() {

		if (!combinedAction) {
			return modes.get(currentMode).getPreferredCursor();
		} else {
			String[] names = interactionGroup.get(currentMode);
			for (int i = 0, cursor; i < names.length; i++) {
				cursor = modes.get(names[i]).getPreferredCursor();
				if (cursor != GraphStageConstants.CURSOR_DEFAULT) {
					return cursor;
				}
			}
			return GraphStageConstants.CURSOR_DEFAULT;
		}

	}

	// ===================================================
	// sending/receiving notification to listener

	protected List<InteractionListener> interactionListener = new ArrayList<InteractionListener>();

	@Override
	public void addInteractionListener(InteractionListener listener) {

		if (!interactionListener.contains(listener)) {
			interactionListener.add(listener);
		}
	}

	@Override
	public void removeInteractionListener(InteractionListener listener) {

		interactionListener.remove(listener);
	}

	private void fireInteractionModeChanged() {

		String lastMode = "";
		if (lastMode != null) {
			lastMode = lastMode;
		}
		String currnetMode = currentMode;
		for (InteractionListener l : interactionListener) {
			l.interactionModeChanged(lastMode, currnetMode);
		}
	}

}
