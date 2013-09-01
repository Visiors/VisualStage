package com.visiors.visualstage.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.google.common.base.Strings;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.Path;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.svg.SVGDocumentBuilder;
import com.visiors.visualstage.svg.SVGUtil;
import com.visiors.visualstage.transform.Transform;

public class VisualObjectSnapshotGenerator {


	private final SVGDocumentBuilder docBuilder;
	private final VisualGraphObject vgo;



	public VisualObjectSnapshotGenerator(SVGDocumentBuilder docBuilder, VisualGraphObject vgo) {
		this.docBuilder = docBuilder;
		this.vgo = vgo;

	}

	public Image createSnapshot(DrawingContext context, DrawingSubject subject) {

		final String desc = vgo.getViewDescriptor(context.getResolution(), subject);
		// if (desc == null) {
		// return null;
		// }
		return createSnapshot( desc, context, subject);
	}

	public Image createSnapshot( String viewDescriptor, DrawingContext context,
			DrawingSubject subject) {

		Image img = null;
		// svgAttributes = vgo.getSVGDocumentAttributes();
		final Transform transform = vgo.getTransformer();
		final Rectangle viewBox = transform.transformToScreen(vgo.getExtendedBoundary());
		viewBox.x -= transform.getXTranslate();
		viewBox.y -= transform.getYTranslate();
		if (!Strings.isNullOrEmpty(viewDescriptor)) {
			docBuilder.createEmptyDocument(viewBox, null, null);
			docBuilder.addContent(viewDescriptor);
			// doc.addDocumentAttributes(svgAttributes);
			docBuilder.finlaizeDocument();
			img = SVGUtil.svgToImage(docBuilder.getDocument());
		}

		// Debug drawing

		if (subject == DrawingSubject.OBJECT) {

			final Rectangle b = vgo.getBounds();
			img = new BufferedImage(b.width+2, b.height+2, BufferedImage.TYPE_INT_ARGB_PRE);
			final Graphics g = img.getGraphics();
			if (vgo instanceof VisualEdge) {
				g.setColor(Color.orange);
				VisualEdge edge = (VisualEdge) vgo;
				Path path = edge.getPath();
				EdgePoint[] points = path.getPoints();
				for (int i = 1 ; i < points.length ; i++) {
					Point pt1 = points[i-1].getPoint();
					Point pt2 = points[i].getPoint();
					g.drawLine(-b.x  + pt1.x,-b.y +  pt1.y, -b.x  + pt2.x, -b.y + pt2.y);
				}
			} else {
				g.setColor(vgo instanceof VisualGraph ? Color.blue: Color.red);
				g.drawRect(0, 0, b.width - 1, b.height - 1);
			}
		}
		return img;
	}

}
