package com.visiors.minuetta;

import java.awt.Point;
import java.awt.Rectangle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.EditorListener;
import com.visiors.visualstage.editor.GraphEditor;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.tool.Interactable;

public class ScrollableCanvas extends ScrollPane {

	protected ScrollBar hScrollbar;
	protected ScrollBar vScrollbar;

	protected final GraphEditor editor;
	protected GraphCanvas canvas;

	public ScrollableCanvas(GraphEditor editor) {

		this.editor = editor;
		canvas = new GraphCanvas();
		//		canvas.prefWidthProperty().bind(this.widthProperty());
		//		canvas.prefHeightProperty().bind(this.heightProperty());
		setContent(canvas);

		addListener();
	}


	public void update() {

		editor.update();
	}


	public void setScrollValueX(int y) {

		//		canvas.setYTranslate(-y);
		update();
	}

	public void setScrollValueY(int x) {

		//		canvas.setXTranslate(-x);
		update();
	}



	public Canvas getCanvas() {

		return canvas;
	}

	private void xScrollValueChagned(int x) {

		//		canvas.setXTranslate(-x);
		update();

	}
	private void yScrollValueChagned(int y) {

		// TODO Auto-generated method stub

	}



	private void updateCanvasViewportValues() {

		Bounds viewPort = getViewportBounds();
		canvas.setViewPort(new Rectangle2D(viewPort.getMinX(), viewPort.getMinY(), viewPort.getWidth(), viewPort.getHeight()));
	}

	void adjustScrollbars() {

		final Rectangle paneBounds = new Rectangle((int)getWidth(), (int)getHeight());
		final GraphDocument document = editor.getActiveDocument();
		//		final Transform xform = document.getGraph().getTransformer();
		//		final double tx = xform.getXTranslate();
		//		final double ty = xform.getYTranslate();
		//		final Rectangle graphRect = xform.transformToScreen(document.getDocumentBoundary());

		final Rectangle scrollRect =/* paneBounds.union*/(document.getDocumentBoundary() );

		//		System.err.println("----------------------" );
		//		System.err.println("paneBounds: " + paneBounds);
		//		System.err.println("document bounds: " + graphRect);
		//		System.err.println("scrollRect: " + scrollRect);

		canvas.prefWidthProperty().set(scrollRect.width);
		canvas.prefHeightProperty().set(scrollRect.height);



		//		scrollRect.translate((int) -tx, (int) -ty);

		setHmin(scrollRect.x);
		setHmax(scrollRect.x + scrollRect.width);
		setVmin(scrollRect.y);
		setVmax(scrollRect.y + scrollRect.height);
		//		System.err.println(scrollRect);

		//		// H-Scrollbar
		//		hScrollbar.setUnitIncrement(20);
		//		hScrollbar.setBlockIncrement(paneBounds.width);
		//		hScrollbar.setVisibleAmount(paneBounds.width);
		//		hScrollbar.setMinimum(scrollRect.x);
		//		hScrollbar.setMaximum(scrollRect.x + scrollRect.width);
		//		/* set visibility*/
		//		hScrollbar.setVisible(graphRect.x < paneBounds.x || 
		//				graphRect.x + graphRect.width > paneBounds.x + paneBounds.width);
		//
		//		// V-Scrollbar
		//		vScrollbar.setUnitIncrement(20);
		//		vScrollbar.setBlockIncrement(paneBounds.height);
		//		vScrollbar.setVisibleAmount(paneBounds.height);
		//		vScrollbar.setMinimum(scrollRect.y);
		//		vScrollbar.setMaximum(scrollRect.y + scrollRect.height);
		//		/* set visibility*/
		//		vScrollbar.setVisible(graphRect.y < paneBounds.y
		//				|| graphRect.y + graphRect.height > paneBounds.y + paneBounds.height);

	}

	private void addListener() {

		editor.addEditorListener(new EditorListener(){
			@Override
			public void boundaryChangedListener(Rectangle newBoundary) {
				adjustScrollbars();
			}
		});

		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				updateCanvasViewportValues();
				adjustScrollbars();
			}
		});

		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				updateCanvasViewportValues();
				adjustScrollbars();
			}
		});

		hvalueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				updateCanvasViewportValues();
				xScrollValueChagned(new_val.intValue());
			}
		});
		vvalueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				updateCanvasViewportValues();
				yScrollValueChagned(new_val.intValue());
			}
		});
		addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if(getBoundsInLocal().contains(e.getX(), e.getY())){
					if (editor.mousePressed(vsPoint(e), vsMouseButton(e), vsFunctionKey(e))) {
						e.consume();
					}
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
			public void handle(KeyEvent e) {

				if (editor.keyPressed(vsKeyChar(e), vsKeyCode(e))) {
					e.consume();
				}
			}
		});

		addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent e) {

				if (editor.keyReleased(vsKeyChar(e), vsKeyCode(e))) {
					e.consume();
				}
			}
		});		
	}


	private Point vsPoint(MouseEvent e) {

		double dx = -getViewportBounds().getMinX();
		double dy = -getViewportBounds().getMinY();
		return /*canvas.getContext().getTransform().transformToGraph*/(new Point((int)( e.getX()+ dx), (int) (e.getY()+dy)));
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
