package com.visiors.minuetta;

import java.awt.Point;
import java.awt.image.BufferedImage;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.Editor;
import com.visiors.visualstage.editor.EditorListener;
import com.visiors.visualstage.tool.Interactable;

public class GraphCanvas extends StackPane implements EditorListener {

	protected final CanvasDrawingContext context;
	ImageView imageViewer = new ImageView();
	private final Editor editor;

	public GraphCanvas(Editor editor) {

		super();
		this.editor = editor;
		this.context = new CanvasDrawingContext();
		getChildren().add(imageViewer);
		setMinWidth(10);
		setMinHeight(10);
		editor.addEditorListener(this);

		addInteractionListener();
		addResizeListener();
	}

	@Override
	protected void layoutChildren() {

		final BufferedImage bufferedImage = (BufferedImage) editor.getActiveDocument().getScreen(context);
		final WritableImage fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
		imageViewer.setImage(fxImage);
	}

	private void invalidate() {

		requestLayout();
	}

	private void setViewport(int width, int height) {

		editor.getActiveDocument().setViewportSize(width, height);
	}

	@Override
	public void viewInvalid(GraphDocument documen) {

		invalidate();

	}

	@Override
	public void boundaryChangedListener() {

		invalidate();
	}

	private void addResizeListener() {

		widthProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {

				setViewport((int) getWidth(), (int) getHeight());
			}
		});
		heightProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {

				setViewport((int) getWidth(), (int) getHeight());
			}
		});
	}

	private void addInteractionListener() {

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

				if (editor.mouseReleased(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

		addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				updateCursor();

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
		addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (editor.mouseEntered(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});
		addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (editor.mouseExited(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
					e.consume();
				}
			}
		});

	}

	private Point vsPoint(MouseEvent e) {
		return new Point((int) e.getX() , (int) e.getY() );
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

	private void updateCursor() {

		Cursor cursor;
		switch (editor.getPreferredCursor()) {
		case Interactable.CURSOR_CROSSHAIR:
			cursor = Cursor.DEFAULT;
			break;
		case Interactable.CURSOR_E_RESIZE:
			cursor = Cursor.E_RESIZE;
			break;
		case Interactable.CURSOR_N_RESIZE:
			cursor = Cursor.N_RESIZE;
			break;
		case Interactable.CURSOR_W_RESIZE:
			cursor = Cursor.W_RESIZE;
			break;
		case Interactable.CURSOR_S_RESIZE:
			cursor = Cursor.S_RESIZE;
			break;
		case Interactable.CURSOR_SW_RESIZE:
			cursor = Cursor.SW_RESIZE;
			break;
		case Interactable.CURSOR_SE_RESIZE:
			cursor = Cursor.SE_RESIZE;
			break;
		case Interactable.CURSOR_NE_RESIZE:
			cursor = Cursor.NE_RESIZE;
			break;
		case Interactable.CURSOR_NW_RESIZE:
			cursor = Cursor.NW_RESIZE;
			break;
		case Interactable.CURSOR_MOVE:
			cursor = Cursor.MOVE;
			break;
		case Interactable.CURSOR_EDIT_TEXT:
			cursor = Cursor.TEXT;
			break;
		default:
			cursor = Cursor.DEFAULT;
			break;
		}
		setCursor(cursor);
	}

}
