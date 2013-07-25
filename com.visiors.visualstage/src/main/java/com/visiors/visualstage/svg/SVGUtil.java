package com.visiors.visualstage.svg;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.transform.Transform;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.w3c.dom.svg.SVGDocument;

import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.renderer.SVGTranscoder;
import com.visiors.visualstage.util.PropertyUtil;

public class SVGUtil {

	public static final void moveTo(StringBuffer sb, int x, int y) {

		sb.append(" M");
		sb.append(x);
		sb.append(',');
		sb.append(y);
	}

	public static final void lineTo(StringBuffer sb, int x, int y) {

		sb.append(" L");
		sb.append(x);
		sb.append(',');
		sb.append(y);
	}

	public static final void curveEllipticalTo(StringBuffer sb, int x, int y, int rounding, boolean sweep) {

		sb.append(" A");
		sb.append(rounding);
		sb.append(',');
		sb.append(rounding);
		sb.append(" 0 0,");
		sb.append(sweep ? "1 " : "0 ");
		sb.append(x);
		sb.append(',');
		sb.append(y);
	}

	public static final void bezierTo(StringBuffer sb, int x, int y, int cx1, int cy1, int cx2, int cy2) {

		sb.append(" C");
		sb.append(cx1);
		sb.append(',');
		sb.append(cy1);
		sb.append(" ");
		sb.append(cx2);
		sb.append(',');
		sb.append(cy2);
		sb.append(" ");
		sb.append(x);
		sb.append(',');
		sb.append(y);
	}

	public static final void quadraticBezierTo(StringBuffer sb, int x, int y, int cx, int cy) {

		sb.append(" Q");
		sb.append(cx);
		sb.append(',');
		sb.append(cy);
		sb.append(" ");
		sb.append(x);
		sb.append(',');
		sb.append(y);
	}

	public static String setElementAttribute(String svg, String tag, String attribute, String value) {

		PropertyList svgPl = PropertyUtil.XML2PropertyList(svg);
		PropertyList tagPl = PropertyUtil.getPropertyList(svgPl, tag);
		if (tagPl != null) {
			PropertyUnit pu = PropertyUtil.findPropertyUnit(tagPl, attribute);
			if (pu != null) {

				pu.setValue(value);

				return PropertyUtil.propertyList2XML(svgPl, false);
			}
		}
		return null;
	}

	public static String getElementAttribute(String svg, String tag, String attribute) {

		PropertyList svgPl = PropertyUtil.XML2PropertyList(svg);
		PropertyList tagPl = PropertyUtil.getPropertyList(svgPl, tag);
		if (tagPl != null) {
			String value = PropertyUtil.getProperty(tagPl, attribute, "");
			if (!value.isEmpty()) {
				return value;
			}
		}

		return null;

	}

	public static String documentToStr(SVGDocument svgDocument) {

		TransformerFactory factory = TransformerFactory.newInstance();
		Transform transformer;
		try {
			transformer = factory.newTransformer();
			Source src = new DOMSource(svgDocument);

			Writer outWriter = new StringWriter();
			StreamResult result = new StreamResult(outWriter);

			transformer.transform(src, result);

			return outWriter.toString();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public synchronized static Image svgToImage(String svg) {

		ByteArrayInputStream bais = new ByteArrayInputStream(svg.getBytes());
		SVGTranscoder transcoder = new SVGTranscoder(bais);
		return transcoder.generateImage();
	}

	public static String getSVGAttribute(String svg, String attribute) {

		int ptr;
		char ch;
		int attrLen = attribute.length();
		next: for (int i = 1, len = svg.length() - attrLen - 1; i < len; i++) {

			if (Character.isWhitespace(svg.charAt(i - 1))) {
				for (int j = 0; j < attribute.length(); j++) {
					if (Character.toLowerCase(svg.charAt(i + j)) != Character.toLowerCase(attribute.charAt(j))) {
						continue next;
					}
				}
				ptr = i + attrLen + 1;
				do {
					ch = svg.charAt(++ptr);
					if (ch == '"' || ch == '\'') {
						break;
					}
				} while (ptr < len);
				return svg.substring(i + attrLen + 2, ptr);
			}
		}
		return null;
	}

	public static SVGDocument duplicateSVGDocument(SVGDocument doc) {

		String str = documentToStr(doc);
		ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(null);
		try {
			SVGDocument newdoc = f.createSVGDocument(null, bais);
			UserAgent userAgent = new UserAgentAdapter();
			DocumentLoader loader = new DocumentLoader(userAgent);
			BridgeContext ctx = new BridgeContext(userAgent, loader);
			ctx.setDynamicState(BridgeContext.DYNAMIC);
			GVTBuilder builder = new GVTBuilder();
			builder.build(ctx, newdoc);
			return newdoc;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return doc;
	}

}
