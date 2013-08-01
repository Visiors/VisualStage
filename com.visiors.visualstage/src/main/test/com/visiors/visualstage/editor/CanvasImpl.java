package com.visiors.visualstage.editor;

import java.awt.Image;

import com.visiors.visualstage.renderer.Canvas;


public class CanvasImpl implements Canvas {

	@Override
	public void draw(int x, int y, Image image) {

		System.err.println("draw Image...");
	}



}
