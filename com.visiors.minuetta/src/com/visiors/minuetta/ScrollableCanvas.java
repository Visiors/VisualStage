package com.visiors.minuetta;

import java.awt.Point;
import java.awt.Rectangle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.impl.GraphEditor;
import com.visiors.visualstage.tool.Interactable;

public class ScrollableCanvas extends BorderPane {

	protected ScrollBar hScrollbar;
	protected ScrollBar vScrollbar;

	protected final GraphEditor editor;
	protected GraphCanvas canvas;
	protected AutoScrollManager autoScrollManager;
	private javafx.scene.shape.Rectangle cornerRect;
	private int scrollBarWidth;

	public ScrollableCanvas(GraphEditor editor) {

		this.editor = editor;
		//		autoScrollManager = new AutoScrollManager(this);

		addCanvas();
		addVScrollbar();
		addHScrollbar();
		addListener();
	}


	public void invalidate() {
		//canvas.invalidate();
	}

	private void addHScrollbar() {

		HBox hBox = new HBox();
		hScrollbar = new ScrollBar();
		cornerRect = new javafx.scene.shape.Rectangle();
		cornerRect.widthProperty().bind(vScrollbar.widthProperty());
		cornerRect.heightProperty().bind(hScrollbar.heightProperty());
		cornerRect.setFill( Color.SILVER);
		hBox.setVisible(false);
		hBox.getChildren().addAll(hScrollbar, cornerRect);
		setBottom(hBox);
		hBox.layout();
	}

	private void addVScrollbar() {

		vScrollbar = new ScrollBar();
		vScrollbar.setOrientation(Orientation.VERTICAL);
		vScrollbar.setVisible(false);
		setRight(vScrollbar);
	}

	private void addCanvas() {

		canvas = new GraphCanvas(editor);
		setCenter(canvas);
		canvas.prefWidthProperty().bind(this.widthProperty());
		canvas.prefHeightProperty().bind(this.heightProperty());
	}

	public GraphEditor getEditor() {

		return editor;
	}

	/**
	 * This method can be used to scroll view to a given location. An additional
	 * space might be created if the target location is beyond the currently
	 * available working area.
	 * 
	 * @param x
	 *            Absolute x-position within the graph
	 * @param y
	 *            Absolute y-position within the graph
	 * @param animated
	 * 
	 * @see #ensureVisible(Rectangle)
	 * @see #getTransformer()
	 */
	public void scrollTo(double x, double y, boolean autoExpand, boolean animated) {

		// if(autoExpand) {
		// double width = getWidth();
		// double height = getHeight();
		// hScrollbar.setMin(Math.min(hScrollbar.getMin(), x - width));
		// vScrollbar.setMin(Math.min(vScrollbar.getMin(), y - height));
		// hScrollbar.setMax(Math.max(hScrollbar.getMax(), x + width));
		// vScrollbar.setMax(Math.max(vScrollbar.getMax(), y + height));
		// }

		hScrollbar.setValue(x);
		vScrollbar.setValue(y);

	}


	public ScrollBar getHScrollbar() {

		return hScrollbar;
	}

	public ScrollBar getVScrollbar() {

		return vScrollbar;
	}


	public GraphCanvas getCanvas() {

		return canvas;
	}


	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		updateViewport();
		adjustScrollbars();
	}

	private void updateViewport() {

		if(scrollBarWidth == 0) {
			//			scrollBarWidth = (int) hScrollbar.getHeight();
		}

		final int x = (int) -hScrollbar.getValue();
		final int y = (int) -vScrollbar.getValue();
		final int w = (int) getWidth() -scrollBarWidth;
		final int h = (int) getHeight() -scrollBarWidth;
		//		canvas.setViewport(x, y, w, h);
	}

	void adjustScrollbars() {

		final int windowWidth = (int) getWidth();
		final int windowHeight = (int) getHeight();

		final GraphDocument document = editor.getActiveDocument();
		final Rectangle graphDocumentBounds = document.getDocumentBoundary();
		final double  maxWidth = graphDocumentBounds.getWidth() + 2 * windowWidth ;
		//		maxWidth = Math.max(maxWidth, hScrollbar.getValue() + windowWidth*2);
		final double maxHeight = graphDocumentBounds.getHeight() + 2 *  windowHeight;
		final double hVisibleArea = windowWidth;
		final double vVisibleArea = windowHeight ;

		hScrollbar.setMin(-windowWidth);
		hScrollbar.setMax(maxWidth-windowWidth);
		hScrollbar.setUnitIncrement(20);
		hScrollbar.setVisibleAmount(hVisibleArea);

		vScrollbar.setMin(-windowHeight);
		vScrollbar.setMax(maxHeight-windowHeight);
		vScrollbar.setUnitIncrement(20);
		vScrollbar.setVisibleAmount(vVisibleArea);
		hScrollbar.setPrefWidth(windowWidth - cornerRect.getWidth());
	}

	private void addListener() {

		hScrollbar.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {

				//invalidate();
			}
		});

		vScrollbar.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {

				//invalidate();
			}
		});

		addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (e.getX() < canvas.getWidth() && e.getY() < canvas.getHeight()) {
					if (editor.mousePressed(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
						e.consume();
					}
				}
			}
		});

		addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (e.getX() < canvas.getWidth() && e.getY() < canvas.getHeight()) {
					if (editor.mouseReleased(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
						e.consume();
					}
				}
			}
		});

		addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				updateCursor();

				if (e.getX() < canvas.getWidth() && e.getY() < canvas.getHeight()) {
					if (editor.mouseMoved(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
						e.consume();
					}
				}
			}
		});

		addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (e.getX() < canvas.getWidth() && e.getY() < canvas.getHeight()) {
					if (editor.mouseDragged(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
						e.consume();
					}
				}
			}
		});

	}

	private Point vsPoint(MouseEvent e) {

		double dx = hScrollbar.getValue();
		double dy = vScrollbar.getValue();
		return new Point((int) (e.getX() + dx), (int) (e.getY() + dy));
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
