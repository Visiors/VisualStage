package com.visiors.minuetta.editor;

import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.visiors.visualstage.editor.impl.GraphEditor;
import com.visiors.visualstage.tool.Interactable;

public class MultiPageEditor extends TabPane {

	private final GraphEditor editor;

	public MultiPageEditor() {

		this.editor = new GraphEditor();
		setSide(Side.TOP);

		newDocument("New Document");
		addKeyListener();
	}


	public void newDocument(String title) {

		final Tab tab = new EditorPage(editor, title);
		getTabs().add(tab);
		super.getSelectionModel().select(tab);



	}

	private void addKeyListener() {

		addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent e) {

				if (editor.keyPressed(vsKeyChar(e), vsKeyCode(e))) {
					e.consume();
				}
			}
		});

		addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent e) {

				if (editor.keyReleased(vsKeyChar(e), vsKeyCode(e))) {
					e.consume();
				}
			}
		});

	}


	private int vsKeyCode(KeyEvent e) {

		final KeyCode code = e.getCode();

		int vsKey = 0;
		if(e.isAltDown()) {
			vsKey |= Interactable.KEY_ALT;
		}
		if(e.isShiftDown()) {
			vsKey |= Interactable.KEY_SHIFT;
		}
		if(e.isControlDown()) {
			vsKey |= Interactable.KEY_CONTROL;
		}
		return vsKey;
	}

	private int vsKeyChar(KeyEvent e) {

		final String character = e.getCharacter();
		return character.charAt(0);
	}

	public GraphEditor getEditor() {

		return editor;
	}
}
