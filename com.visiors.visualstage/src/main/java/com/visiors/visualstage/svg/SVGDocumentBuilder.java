package com.visiors.visualstage.svg;

import java.awt.Rectangle;

import com.visiors.visualstage.transform.Transform;

public interface SVGDocumentBuilder {

	public void createDocument(int width, int height);

	public void createDocument(int width, int height, Transform transform);

	public void createDocument(int width, int height, Transform xform, String svgBackgroundID, String svgFilterID,
			String svgTransformID);

	public void createEmptyDocument(Rectangle viewBox, Transform xform, String svgBackgroundID, String svgFilterID,
			String svgTransformID);

	public void addDocumentAttributes(String[][] attributes);

	public void addContent(String description);

	public void finlaizeDocument();

	public String getDocument();

}