package com.visiors.visualstage.renderer;

import java.awt.Image;
import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.svg.SVGDocumentBuilder;
import com.visiors.visualstage.svg.SVGUtil;
import com.visiors.visualstage.transform.Transform;

public class VisualObjectSnapshotGenerator {

	public static Image createSnapshot(VisualGraphObject vgo, DrawingContext context, DrawingSubject subject) {

		String desc = vgo.getViewDescriptor(context, subject);
		if (desc == null) {
			return null;
		}
		return createSnapshot(vgo, desc, context, subject);
	}

	public static Image createSnapshot(VisualGraphObject vgo, String viewDescriptor, DrawingContext context, DrawingSubject subject) {

		// svgAttributes = vgo.getSVGDocumentAttributes();
		Transform transform = vgo.getTransformer();
		final Rectangle viewBox = transform.transformToScreen(vgo.getExtendedBoundary());
		viewBox.x -= transform.getTranslateX();
		viewBox.y -= transform.getTranslateY();

		SVGDocumentBuilder doc = new SVGDocumentBuilder();
		doc.createDocument(viewBox, null, null, null, null);
		doc.addContent(viewDescriptor);
		// doc.addDocumentAttributes(svgAttributes);
		doc.closeDocument();
		Image img = SVGUtil.svgToImage(doc.getDocument());
		// Graphics g = img.getGraphics();
		// g.setColor(Color.orange);
		// g.drawRect(1, 1, viewBox.width-2, viewBox.height-2);
		return img;
	}

}
