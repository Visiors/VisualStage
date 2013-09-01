package com.visiors.minuetta.editor;

import java.awt.Point;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import com.visiors.minuetta.GraphCanvas;
import com.visiors.visualstage.editor.GraphEditor;
import com.visiors.visualstage.interaction.Interactable;

public class GraphEditorPane extends StackPane {

	private final GraphEditor editor;
	private final GraphCanvas canvas;

	public GraphEditorPane() {

		super();

		final ImageView imageView = new ImageView();
		setAlignment(imageView, Pos.TOP_LEFT);
		getChildren().add(imageView);
		this.canvas = new GraphCanvas(imageView);
		this.editor = new GraphEditor();
		editor.addCanvas(canvas);

		initListeners();
	}

	public GraphEditor getEditor() {

		return editor;
	}

	private void initListeners() {

		widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {

				canvas.setWidth(newWidth.intValue());
			}
		});
		heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldHeight, Number newHeight) {

				canvas.setHeight(newHeight.intValue());
			}
		});

		addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (editor.mousePressed(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

		addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (editor.mousePressed(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

		addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (editor.mouseMoved(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

		addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (editor.mouseDragged(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

		addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent  e) {

				if (editor.keyPressed(vsKeyChar(e), vsKeyCode(e))) {
					e.consume();
				}
			}
		});

		addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent  e) {

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
