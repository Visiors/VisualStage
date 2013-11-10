package com.visiors.visualstage.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public interface DrawClient {


	Rectangle getBounds();

	void draw(Graphics2D g);
}
