package com.visiors.minuetta;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DefaultDrawingContext;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;

public class GraphCanvas implements Canvas {

	private final DrawingContext context;
	private Resolution resolution;
	private Rectangle bounds;
	private final Transform transform;
	private final ImageView imageView;

	public GraphCanvas(ImageView imageView) {

		this.imageView = imageView;
		resolution = Resolution.SCREEN;
		transform = new DefaultTransformer();
		bounds = new Rectangle(100, 100);
		context = new DefaultDrawingContext(resolution, bounds, transform){
			@Override
			public Rectangle getBounds() {
				return bounds;
			}
		};
	}

	public void setBounds(Rectangle bounds) {

		this.bounds = bounds;
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

		transform.setTranslateX(dx);
	}

	public double getXTranslate() {

		return transform.getTranslateX();
	}

	public void setYTranslate(double dy) {

		transform.setTranslateY(dy);
	}

	public double getYTranslate() {

		return transform.getTranslateY();
	}

	@Override
	public void draw(int x, int y, Image image) {

		BufferedImage bufferedImage = (BufferedImage) image;
		WritableImage fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
		imageView.setImage(fxImage);
	}

	@Override
	public DrawingContext getContext() {

		return context;
	}

	public void setWidth(int newWidth) {

		bounds.width = newWidth;
	}

	public void setHeight(int newHeight) {

		bounds.height = newHeight;
	}

}
