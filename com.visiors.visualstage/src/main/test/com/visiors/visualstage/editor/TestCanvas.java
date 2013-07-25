package com.visiors.visualstage.editor;

import java.awt.Image;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;


public class TestCanvas implements Canvas {

	@Override
	public void draw(int x, int y, Image image) {

		System.err.println("draw Image...");
	}

	@Override
	public Resolution getResolution() {

		return Resolution.SCREEN;
	}

	@Override
	public Rectangle getCanvasBounds() {

		// TODO Auto-generated method stub
		return new Rectangle(0,0, 1000, 400);
	}

	@Override
	public Transform getTransform() {

		return new DefaultTransformer();
	}

}
