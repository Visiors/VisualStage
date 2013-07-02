package com.visiors.visualstage.form;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.GenericText;
import org.apache.batik.dom.svg.SVGOMTSpanElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGTextContentElement;
import org.w3c.dom.svg.SVGTextElement;

/*
 * This class breaks a given text-elements in lines by putting the content in diffrent span-elements. 
 * The expected text must be text-element; i.e.  <text>some cotent</text>, or a text-elements that 
 * contains one or more tspan-elements; i.e. <text><tspan>some content</tspan><tspan>and some more</tspan></text>.
 * 
 * This class ignores all position related attributes like x, y, dx and dy. when a span needs to be 
 * split in other spans, the new span-elements will be define as child-span-elements of the original 
 * span-element.
 * In order to be sure that spans won't be merge together; i.e. after the available box expands, nesting 
 * of span-elements must be avoided. 
 * */
public class TextWrapper {

	private SVGDocument document;
	private Point location;
	


	public void setDocument(SVGDocument document) {
		this.document = document;
		location = null;
	}

	public SVGDocument getDocument() {
		return document;
	}


	public synchronized void wrap(int maxWidth, Point location) {
		if (document != null && location != null && maxWidth > 0) {
			this.location = location;
			SVGTextContentElement textElement = findLeadingTextElement(document
					.getRootElement());
			if (textElement != null) {
				wrapTextElement(textElement, maxWidth);
			}
		}
	}

	
	public Dimension getSize() {

		if (document != null) {
    		SVGTextContentElement textElement = findLeadingTextElement(document.getRootElement());
    		if (textElement != null) {
    			if (textElement instanceof SVGLocatable) {
    				final SVGLocatable loc = (SVGLocatable) textElement;
    				final SVGRect bbox = loc.getBBox();
    				if (bbox != null) {
    					return new Dimension((int) bbox.getWidth(), (int) bbox.getHeight());
    				}
    			}
    		}
		}
		return new Dimension();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////

	private void restoreDocument() {

		SVGTextContentElement textElement = findLeadingTextElement(document.getRootElement());
		if (textElement != null) {
			/*
			 * //test List<SVGOMTSpanElement> sl = getSpanElements(textElement,
			 * new ArrayList<SVGOMTSpanElement>());
			 * System.err.println("+Number of span-elements before restore: " +
			 * sl.size()); //
			 */

			List<SVGOMTSpanElement> spanList = getToplevelSpanElements(textElement,
					new ArrayList<SVGOMTSpanElement>());
			for (int s = 0; s < spanList.size(); s++) {
				flattenSpanTree(spanList.get(s));
			}

			/*
			 * //test sl = getSpanElements(textElement, new
			 * ArrayList<SVGOMTSpanElement>());
			 * System.err.println("+Number of span-elements before restore: " +
			 * sl.size() + "\n");
			 * 
			 * //
			 */
		}
	}
	

	private  void flattenSpanTree(SVGOMTSpanElement span) {

		String type = span.getAttributeNS(null, "type");
//		if( "newline".equals(type)) {
			span.removeAttributeNS(null, "dx");
			span.removeAttributeNS(null, "dy");
			span.removeAttributeNS(null, "x");
			span.removeAttributeNS(null, "y");
//		}
		// keep the content
		String content = span.getTextContent();

		// remove child-spans and set content into the root span element
		span.setTextContent(content);
	}

	private SVGTextContentElement findLeadingTextElement(Node node) {
		// find the text-element and apply line break to it
		if (node instanceof SVGTextElement) {
			SVGTextContentElement textElement = (SVGTextContentElement) node;
			return textElement;
		}
		NodeList children = node.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			SVGTextContentElement textElement = findLeadingTextElement(children.item(j));
			if (textElement != null)
				return textElement;
		}
		return null;
	}
	
	private void initTextElement(SVGTextContentElement textElement) {
		
		final List<SVGOMTSpanElement> spans = getToplevelSpanElements(textElement, new ArrayList<SVGOMTSpanElement>());
		final SVGLocatable loc = (SVGLocatable) textElement;
		final SVGRect bbox = loc.getBBox();
		final float height = bbox.getHeight();
		
		// text element has no span yet
		if(spans.size() == 0) {
			GenericText gen = getTextElementNetContent(textElement);
			SVGOMTSpanElement span = newSpanElement(gen.getTextContent().trim(), "0", false);
			textElement.insertBefore(span, textElement.getFirstChild());
			textElement.removeChild(gen);
		}
		
		// considering line breaks
		for (int i = 0; i < spans.size(); i++) {
			SVGOMTSpanElement span = spans.get(i); 
			String pos = span.getAttributeNS(null, "pos");
			if(i == 0 || "newline".equalsIgnoreCase(pos)) {
				span.setAttributeNS(null, "dy", height + "");			
			}
		}
	}

	
	
	private void wrapTextElement(SVGTextContentElement textElement, float maxWidth) {

		restoreDocument();
		initTextElement(textElement);

		updateLocation(textElement);
		//wrapAllChildrenSpans((SVGTextElement) textElement, maxWidth);
	}

	
	
	
	private void wrapAllChildrenSpans(SVGTextElement rootTextElement, float maxWidth) {

		List<SVGOMTSpanElement> spanList = getToplevelSpanElements(rootTextElement,
				new ArrayList<SVGOMTSpanElement>());
		for (int s = 0; s < spanList.size(); s++) {
			final SVGOMTSpanElement span = spanList.get(s);
			if (s == 0)
				span.setAttributeNS(null, "pos", "tempLineBreak");
			recursiveWrapSpanElement(span, maxWidth);
		}
	}

	private void recursiveWrapSpanElement(SVGOMTSpanElement span, float maxWidth) {

		int lastWhiteSpace = -1;
		final String content = span.getTextContent();
		SVGRect ex;
		float dy = 0;

		for (int index = 0, len = content.length(); index < len; index++) {

			ex = span.getExtentOfChar(index);
			dy = Math.max(dy, ex.getHeight());
			if (index > 0 && Character.isWhitespace(content.charAt(index)))
				lastWhiteSpace = index + 1;

			if (ex.getX() - location.x > maxWidth) {

				if (lastWhiteSpace == -1) // word break required
					lastWhiteSpace = index;

				String part1 = content.substring(0, lastWhiteSpace);
				String part2 = content.substring(lastWhiteSpace);

				span.setTextContent(part1);
				final SVGOMTSpanElement span2 = newSpanElement(part2, Float.toString(dy), true);
				span2.setAttributeNS(null, "pos", "tempLineBreak");
				span.appendChild(span2);
				lastWhiteSpace = -1;
				dy = 0;

				recursiveWrapSpanElement(span2, maxWidth);
				break;
			}
		}
	}

	
	private GenericText getTextElementNetContent(SVGTextContentElement textElement) {
		NodeList children = textElement.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			if (children.item(j) instanceof GenericText) {
				final GenericText gen = (GenericText) children.item(j);
				final String content = gen.getTextContent();
				if (content != null && !content.trim().isEmpty()) {
					return gen;
				}
			}
		}
		return null;
	}

	private List<SVGOMTSpanElement> getToplevelSpanElements(Node root,
			ArrayList<SVGOMTSpanElement> list) {

		NodeList children = root.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			if (children.item(j) instanceof SVGOMTSpanElement) {
				list.add((SVGOMTSpanElement) children.item(j));
			}
		}
		return list;
	}
	


	private List<SVGOMTSpanElement> getSpanElements(Node root, ArrayList<SVGOMTSpanElement> list) {

		if (root instanceof SVGOMTSpanElement) {
			list.add((SVGOMTSpanElement) root);
		}
		NodeList children = root.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			list = (ArrayList<SVGOMTSpanElement>) getSpanElements(children.item(j), list);
		}

		return list;
	}

	/* all new spans get */
	private SVGOMTSpanElement newSpanElement(String text, String dy, boolean newLine) {

		SVGOMTSpanElement newSpan = new SVGOMTSpanElement(null, (AbstractDocument) document);
//		if(newLine)
//			newSpan.setAttributeNS(null, "x", location.x + "");
		newSpan.setAttributeNS(null, "dy", dy);
		newSpan.setTextContent(text);
		return newSpan;
	}

	private float computeSpanElementsHeight(SVGOMTSpanElement span) {
		
		float height = 0;
		int len = span.getNumberOfChars();
		for (int i = 0; i < len; i++) {
			SVGRect ex = span.getExtentOfChar(i);
			height = Math.max(height, ex.getHeight());
		}
		return height;
	}
	
	public void updateLocation(Point newlocation) {

		if (newlocation != null && !newlocation.equals(this.location)) {
			this.location = newlocation;

			updateLocation(findLeadingTextElement(document));
		}
	}

	
	private void updateLocation(SVGTextContentElement textElement) {

		textElement.setAttributeNS(null, "x", Integer.toString(location.x));
		textElement.setAttributeNS(null, "y", Integer.toString(location.y));
		
		List<SVGOMTSpanElement> spans = getToplevelSpanElements(textElement, new ArrayList<SVGOMTSpanElement>());
		
		// considering line breaks
		float dy = 0;
		for (int i = 0; i < spans.size(); i++) {
			SVGOMTSpanElement span = spans.get(i); 
			dy = computeSpanElementsHeight(span);
			String pos = span.getAttributeNS(null, "pos");
			if(i == 0 || "newline".equalsIgnoreCase(pos)) {
				//TODO I commented this line to correct the position of texts. check it later!!
//				span.setAttributeNS(null, "dy", dy + "");		
				span.setAttributeNS(null, "x", Integer.toString(location.x));
			}
		}
//		
//		if (node instanceof SVGOMTextElement) {

//		} else if (node instanceof SVGOMTSpanElement) {
//			SVGOMTSpanElement spanElement = (SVGOMTSpanElement) node;
////			if(spanElement.getAttributeNodeNS(null, "pos") != null)
////				spanElement.setAttributeNS(null, "x", Integer.toString(location.x));
//		}
//
//		NodeList chideren = node.getChildNodes();
//		for (int j = 0; j < chideren.getLength(); j++) {
//			updateLocation(chideren.item(j));
//		}
	}

}
