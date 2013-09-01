package com.visiors.minuetta.editor;

import java.awt.Rectangle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;

import com.visiors.minuetta.GraphCanvas;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.EditorListener;
import com.visiors.visualstage.editor.GraphEditor;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.transform.Transform;

public class ScrollableCanvas extends ScrollPane {

	protected ScrollBar hScrollbar;
	protected ScrollBar vScrollbar;

	protected final GraphEditor editor;
	protected GraphCanvas canvas;

	public ScrollableCanvas(GraphEditor editor) {

		this.editor = editor;

		setup();
		addListener();
		update();
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
				adjustScrollbars();
			}
		});

		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				adjustScrollbars();
			}
		});

		vvalueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				canvas.setYTranslate(new_val.intValue());
			}
		});
	}


	public void update() {

		editor.update();
	}

	private void setup() {

		canvas = new GraphCanvas();
		setContent(canvas);
	}


	public void setScrollValueX(int y) {

		//canvas.setYTranslate(-y);
		update();
	}

	public void setScrollValueY(int x) {

		//		canvas.setXTranslate(-x);
		update();
	}


	public Canvas getCanvas() {

		return canvas;
	}


	void adjustScrollbars() {


		final Rectangle paneBounds = new Rectangle((int)getWidth(), (int)getHeight());
		final GraphDocument document = editor.getActiveDocument();
		final Transform xform = document.getGraph().getTransformer();
		final double tx = xform.getXTranslate();
		final double ty = xform.getYTranslate();
		final Rectangle graphRect = xform.transformToScreen(document.getDocumentBoundary());
		final Rectangle scrollRect = paneBounds.union(graphRect );

		//		System.err.println("----------------------" );
		//		System.err.println("paneBounds: " + paneBounds);
		//		System.err.println("document bounds: " + graphRect);
		//		System.err.println("scrollRect: " + scrollRect);


		canvas.setWidth(scrollRect.getWidth());
		canvas.setHeight(scrollRect.getHeight());
		scrollRect.translate((int) -tx, (int) -ty);
		setHmin(scrollRect.x);
		setHmax(scrollRect.x + scrollRect.width);
		setVmin(scrollRect.y);
		setVmax(scrollRect.y + scrollRect.height);

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
}
