package com.visiors.minuetta;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import com.visiors.visualstage.renderer.DefaultDrawingContext;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;

public class GraphCanvas extends Canvas implements com.visiors.visualstage.renderer.Canvas {

	protected final DrawingContext context;
	protected final Transform transform;
	protected Resolution resolution;

	public GraphCanvas() {

		super();

		resolution = Resolution.SCREEN;
		transform = new DefaultTransformer();
		context = new DefaultDrawingContext(resolution, new Rectangle(), transform) {

			@Override
			public Rectangle getVisibleBounds() {

				return new Rectangle((int) getWidth(), (int) getHeight());
			}
		};
	}

	@Override
	public void draw(int x, int y, Image image) {

		final BufferedImage bufferedImage = (BufferedImage) image;
		final WritableImage fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
		GraphicsContext gc = getGraphicsContext2D();
		gc.drawImage(fxImage, 0, 0);
	}

	@Override
	public DrawingContext getContext() {

		return context;
	}

	public void setResolution(Resolution resolution) {

		this.resolution = resolution;
	}

	public void setScale(double s) {

		transform.setScale(s);
	}

	public double getScale() {

		return transform.getScale();
	}

	public void setXTranslate(double dx) {

		transform.setXTranslate(dx);
	}

	public double getXTranslate() {

		return transform.getXTranslate();
	}

	public void setYTranslate(double dy) {

		transform.setYTranslate(dy);
	}

	public double getYTranslate() {

		return transform.getYTranslate();
	}

	// private void initResizeListeners() {
	//
	// widthProperty().addListener(new ChangeListener<Number>() {
	//
	// @Override
	// public void changed(ObservableValue<? extends Number> observable, Number
	// oldValue, Number newValue) {
	//
	// context.getVisibleBounds().width = newValue.intValue();
	// }
	// });
	//
	// heightProperty().addListener(new ChangeListener<Number>() {
	//
	// @Override
	// public void changed(ObservableValue<? extends Number> observable, Number
	// oldValue, Number newValue) {
	//
	// context.getVisibleBounds().height = newValue.intValue();
	// }
	// });
	// }

}
