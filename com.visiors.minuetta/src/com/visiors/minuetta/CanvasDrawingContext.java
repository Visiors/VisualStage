package com.visiors.minuetta;

import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;

public class CanvasDrawingContext implements DrawingContext {



	@Override
	public Resolution getResolution() {

		return Resolution.SCREEN;
	}

	@Override
	public DrawingSubject[] getDrawingSubject() {

		return new DrawingSubject[] { DrawingSubject.OBJECT, DrawingSubject.PORTS, DrawingSubject.SELECTION_INDICATORS};
	}

}
