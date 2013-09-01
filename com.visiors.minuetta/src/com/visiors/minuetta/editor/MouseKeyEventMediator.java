package com.visiors.minuetta.editor;

import java.awt.Point;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import com.visiors.visualstage.editor.GraphEditor;
import com.visiors.visualstage.interaction.Interactable;

/**
 * This helper class listens to mouse and key events and passes them to {@link GraphEditor}
 */
public class MouseKeyEventMediator {

	public MouseKeyEventMediator(MultiPageEditor multiPageEditor) {

		final GraphEditor editor = multiPageEditor.getEditor();

		multiPageEditor.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				editor.getActiveDocument().update();
				if (editor.mousePressed(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

		multiPageEditor.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (editor.mouseReleased(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

		multiPageEditor.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (editor.mouseMoved(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

		multiPageEditor.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (editor.mouseDragged(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

		multiPageEditor.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent e) {

				if (editor.keyPressed(vsKeyChar(e), vsKeyCode(e))) {
					e.consume();
				}
			}
		});

		multiPageEditor.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent e) {

				if (editor.keyReleased(vsKeyChar(e), vsKeyCode(e))) {
					e.consume();
				}
			}
		});
	}

	private Point vsPoint(MouseEvent e) {

		return new Point((int) e.getX(), (int) e.getY());
	}

	private int vsFunctionKey(MouseEvent e) {

		int key = 0;
		if (e.isAltDown()) {
			key |= Interactable.KEY_ALT;
		}
		if (e.isControlDown()) {
			key |= Interactable.KEY_CONTROL;
		}
		if (e.isShiftDown()) {
			key |= Interactable.KEY_SHIFT;
		}
		return key;
	}

	private int vsMouseButton(MouseEvent e) {

		if (e.isPrimaryButtonDown()) {
			return Interactable.BUTTON_LEFT;
		}
		if (e.isMiddleButtonDown()) {
			return Interactable.BUTTON_MIDDLE;
		}
		if (e.isSecondaryButtonDown()) {
			return Interactable.BUTTON_RIGHT;
		}
		return 0;
	}

	private int vsKeyCode(KeyEvent e) {

		final KeyCode code = e.getCode();
		return code.ordinal();
	}

	private int vsKeyChar(KeyEvent e) {

		final String character = e.getCharacter();
		return character.charAt(0);
	}

}
