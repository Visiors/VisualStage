package com.visiors.visualstage.tool;

import java.awt.Point;

public interface Interactable {

	/* cursor types */
	public static final int CURSOR_DEFAULT = 0;
	public static final int CURSOR_N_RESIZE = 2;
	public static final int CURSOR_S_RESIZE = 3;
	public static final int CURSOR_W_RESIZE = 4;
	public static final int CURSOR_E_RESIZE = 5;
	public static final int CURSOR_NW_RESIZE = 6;
	public static final int CURSOR_NE_RESIZE = 7;
	public static final int CURSOR_SW_RESIZE = 8;
	public static final int CURSOR_SE_RESIZE = 9;
	public static final int CURSOR_MOVE		 = 10;
	public static final int CURSOR_CROSSHAIR = 11;
	public static final int CURSOR_EDIT_TEXT = 12;


	/* mouse buttons */
	public static final int BUTTON_LEFT = 1;
	public static final int BUTTON_MIDDLE = 2;
	public static final int BUTTON_RIGHT = 3;

	/* function keys */
	public static final int KEY_SHIFT = 1 << 0;
	public static final int KEY_CONTROL = 1 << 1;
	public static final int KEY_ALT = 1 << 2;

	boolean mousePressed(Point pt, int button, int functionKey);

	boolean mouseReleased(Point pt, int button, int functionKey);

	boolean mouseDoubleClicked(Point pt, int button, int functionKey);

	boolean mouseDragged(Point pt, int button, int functionKey);

	boolean mouseMoved(Point pt, int button, int functionKey);

	boolean keyPressed(int keyChar, int keyCode);

	boolean keyReleased(int keyChar, int keyCode);

	boolean isInteracting();

	void cancelInteraction();

	void terminateInteraction();

	public int getPreferredCursor();
}
