
package com.visiors.visualstage.svg;

import java.awt.Rectangle;

import com.visiors.visualstage.transform.Transform;

public interface SVGDocumentBuilder {

	public void createEmptyDocument(int width, int height);

	public void createEmptyDocument(int width, int height, Transform transform);

	public void createEmptyDocument(int width, int height, Transform xform, DocumentConfig config);

	public void createEmptyDocument(Rectangle viewBox, Transform xform, DocumentConfig config);

	public void addDocumentAttributes(String[][] attributes);

	public void addContent(String description);

	public void finlaizeDocument();

	public String getDocument();

}