package com.visiors.visualstage.renderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.visiors.visualstage.util.UnitUtil;

/** This class uses the Batik SVG Toolkit to create a bitmap from a given SVG-file.
 * Please see http://xmlgraphics.apache.org/batik/license.html
 * 
 *
 */
public class SVGTranscoder {

	private Document svgDocument;
	private static SAXSVGDocumentFactory f;

	public SVGTranscoder(String uri){

		try {
			if (f == null) {
				f = new SAXSVGDocumentFactory(null);
			}

			this.svgDocument = f.createDocument(uri);
		} catch (IOException ex) {
		}
	}

	public SVGTranscoder(InputStream inputstream) {
		try {
			if (f == null) {
				f = new SAXSVGDocumentFactory(null);
			}

			this.svgDocument = f.createDocument(null, inputstream);
		} catch (IOException ex) {
			System.err.println(ex);

		}
	}

	public SVGTranscoder(SVGDocument svgDocument) {
		this.svgDocument = svgDocument;
	}

	public synchronized BufferedImage generateImage() {

		Element svgRoot = svgDocument.getDocumentElement();

		String sw = svgRoot.getAttribute("width");
		String sh = svgRoot.getAttribute("height");
		int w = (int) UnitUtil.strLength2px(sw);
		int h = (int) UnitUtil.strLength2px(sh);  
		return generateImage(w, h, false); 
	}

	public synchronized BufferedImage generateImage(int w, int h) {

		return generateImage(w, h, false); 
	}

	public synchronized BufferedImage generateImage(int w, int h, boolean autoCrop) {

		if (svgDocument != null) {
			try {
				if (autoCrop) {
					float ratio = getPreferredAspectRatio();
					if (ratio > 1.0) {
						h = (int) (w / ratio);
					} else if (ratio < 1.0) {
						w = (int) (h * ratio);
					}
				}


				BufferedImageTranscoder t = new BufferedImageTranscoder(w, h);
				t.transcode(svgDocument, null, null);
				return t.getBufferedImage();
			} catch (Exception e) {
				System.err.println(e.getLocalizedMessage());
			}
		}
		return showMissingImage();
	}


	private static BufferedImage showMissingImage() {
		//        ImageIcon img = TopologyUtil.loadIcon("resources/icons/missing.png",
		//                TopologyTopComponent.class);
		//        BufferedImage buImg = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		//        buImg.getGraphics().drawImage(img.getImage(), 0, 0, null);
		return null;
	}
	/** gets the preferred width and height attributes on the root
	 *  of the 'svg' element.
	 * @param document
	 * @return
	 */
	public float getPreferredAspectRatio() {

		if (svgDocument != null) {
			try {
				Element svgRoot = svgDocument.getDocumentElement();

				String sw = svgRoot.getAttribute("width");
				String sh = svgRoot.getAttribute("height");
				float fw = (float) UnitUtil.strLength2px(sw);
				float fh = (float) UnitUtil.strLength2px(sh);
				return fw / fh;
			} catch (Exception e) {
			}
		}
		return 1;
	}


	private static class BufferedImageTranscoder extends ImageTranscoder {

		protected BufferedImage bufferedImage;

		public BufferedImageTranscoder(int w, int h) {

			this.width = Math.max(w, 10);
			this.height = Math.max(h, 10);

			hints.put(KEY_FORCE_TRANSPARENT_WHITE , Boolean.FALSE);
			hints.put(KEY_WIDTH, new Float(this.width));
			/* this gives a better adjustment if the composites are already resized to
			 * the right ration*/
			/* hints.put(KEY_HEIGHT, new Float(this.height));*/
			hints.put(KEY_HEIGHT, new Float(this.height));
		}

		@Override
		public BufferedImage createImage(int width, int height) {

			return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}

		@Override
		public void writeImage(BufferedImage img, TranscoderOutput output) {
			bufferedImage = img;
		}

		public BufferedImage getBufferedImage() {
			return bufferedImage;
		}

		@Override
		protected void transcode(Document document, String uri,
				TranscoderOutput output) {

			try {
				super.transcode(document, uri, output);
			} catch (TranscoderException e) {
				e.printStackTrace();
			}
		}
	}
}
