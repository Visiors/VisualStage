package com.visiors.visualstage.editor;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;

public class MyCanvasImpl implements Canvas {

	private final int screenWidth = 400;
	private final int screenHeight = 400;
	private final Image screen;

	public MyCanvasImpl() {

		screen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		screen.getGraphics().setColor(Color.BLACK);
		screen.getGraphics().drawRect(0,0,screenWidth-1, screenHeight-1);
	}

	public void draw(int x, int y, Image image) {

		screen.getGraphics().drawImage(image, x, y, null);
	}


	public Image getScreen() {

		return screen;
	}

	@Override
	public DrawingContext getContext() {

		return new DrawingContext() {


			@Override
			public Resolution getResolution() {

				return Resolution.SCREEN;
			}



			@Override
			public DrawingSubject[] getDrawingSubject() {

				return new DrawingSubject[] { DrawingSubject.OBJECT, DrawingSubject.PORTS, DrawingSubject.SELECTION_INDICATORS};
			}
		};
	}

	@Override
	public void invalidate() {

		// TODO Auto-generated method stub

	}

}
