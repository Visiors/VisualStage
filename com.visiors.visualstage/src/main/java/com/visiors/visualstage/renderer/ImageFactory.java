package com.visiors.visualstage.renderer;

import java.awt.Image;
import java.awt.Rectangle;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.svg.SVGDocumentBuilder;
import com.visiors.visualstage.svg.SVGUtil;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;

public class ImageFactory {

	@Inject
	private final SVGDocumentBuilder docBuilder;

	public ImageFactory(SVGDocumentBuilder docBuilder) {

		this.docBuilder = docBuilder;
		DI.injectMembers(this);
	}

	public Image createSnapshot(VisualGraphObject vgo) {

		final String vgoDescriptor = vgo.getViewDescriptor(Resolution.SCREEN_LOW_DETAIL, DrawingSubject.OBJECT);
		if (!Strings.isNullOrEmpty(vgoDescriptor)) {
			final Transform xform = vgo.getTransformer();
			final Rectangle boundary = xform.transformToScreen(vgo.getBounds());
			final DefaultTransformer svgTransformer = new DefaultTransformer();
			svgTransformer.setXTranslate(-boundary.x + xform.getXTranslate());
			svgTransformer.setYTranslate(-boundary.y + xform.getYTranslate());
			docBuilder.createEmptyDocument(boundary.width , boundary.height, svgTransformer, null);
			docBuilder.addContent(vgoDescriptor);
			docBuilder.finlaizeDocument();
			final String svg = docBuilder.getDocument();
			return SVGUtil.svgToImage(svg);
		}
		return null;
	}
	public Image createSnapshot(VisualGraphObject vgo, DrawingContext context, DrawingSubject subject) {

		final String vgoDescriptor = vgo.getViewDescriptor(context.getResolution(), subject);
		if (!Strings.isNullOrEmpty(vgoDescriptor)) {
			//			System.err.println("create snapshot for: "+ vgo.getClass().getName()+ " / " + subject);
			final Transform xform = vgo.getTransformer();
			final Rectangle boundary = xform.transformToScreen(vgo.getBounds());
			final Rectangle extendedBoundary = xform.transformToScreen(vgo.getExtendedBoundary());
			boundary.grow((extendedBoundary.width - boundary.width) / 2, (extendedBoundary.height - boundary.height) / 2);
			final DefaultTransformer svgTransformer = new DefaultTransformer();
			svgTransformer.setXTranslate(-boundary.x + xform.getXTranslate());
			svgTransformer.setYTranslate(-boundary.y + xform.getYTranslate());
			docBuilder.createEmptyDocument(extendedBoundary.width , extendedBoundary.height, svgTransformer, null);
			docBuilder.addContent(vgoDescriptor);
			docBuilder.finlaizeDocument();
			final String svg = docBuilder.getDocument();
			final Image image = SVGUtil.svgToImage(svg);

			//			image = new BufferedImage(48, 24, BufferedImage.TYPE_INT_ARGB_PRE);
			//			image.getGraphics().setColor(Color.red);
			//			image.getGraphics().fillRect(0, 0, 48, 24);
			return image;
		}
		return null;
	}
}
