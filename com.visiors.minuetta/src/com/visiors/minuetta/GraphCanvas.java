package com.visiors.minuetta;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import com.visiors.visualstage.renderer.DefaultDrawingContext;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;

public class GraphCanvas extends Pane implements com.visiors.visualstage.renderer.Canvas {

	protected final DrawingContext context;
	protected final Transform transform;
	protected Resolution resolution;
	ImageView imageViewer = new ImageView();
	private final Rectangle viewPort = new Rectangle();

	public GraphCanvas() {

		super();
		getChildren().add(imageViewer);
		resolution = Resolution.SCREEN;
		transform = new DefaultTransformer();
		context = new DefaultDrawingContext(resolution, new Rectangle(), transform) {

			@Override
			public Rectangle getClipBounds() {

				return viewPort;
			}

			@Override
			public Transform getTransform() {

				return transform;
			}
		};
	}

	@Override
	public void draw(int x, int y, Image image) {

		final BufferedImage bufferedImage = (BufferedImage) image;
		final WritableImage fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

		imageViewer.setTranslateX(x);
		imageViewer.setTranslateY(y);
		imageViewer.setImage(fxImage);

	}

	public void setViewPort(Rectangle2D r) {

		this.viewPort.setBounds((int) r.getMinX(), (int) r.getMinY(), (int) r.getWidth(), (int) r.getHeight());

		//		imageViewer.setViewport(new Rectangle2D(0, 0, r.getWidth(), r.getHeight()));
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

}
