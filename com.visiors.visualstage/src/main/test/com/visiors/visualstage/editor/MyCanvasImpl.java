package com.visiors.visualstage.editor;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;

public class MyCanvasImpl implements Canvas {

	private final int screenWidth = 400;
	private final int screenHeight = 400;
	private final Image screen;

	public MyCanvasImpl() {

		screen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		screen.getGraphics().setColor(Color.BLACK);
		screen.getGraphics().drawRect(0,0,screenWidth-1, screenHeight-1);
	}

	@Override
	public void draw(int x, int y, Image image) {

		screen.getGraphics().drawImage(image, x, y, null);
	}


	public Image getScreen() {

		return screen;
	}

	@Override
	public DrawingContext getContext() {

		// TODO Auto-generated method stub
		return new DrawingContext() {

			@Override
			public Transform getTransform() {

				return new DefaultTransformer();
			}

			@Override
			public Resolution getResolution() {

				return Resolution.SCREEN;
			}

			@Override
			public Rectangle getBounds() {

				return new Rectangle(0, 0, screenWidth, screenHeight);
			}
		};
	}

}
