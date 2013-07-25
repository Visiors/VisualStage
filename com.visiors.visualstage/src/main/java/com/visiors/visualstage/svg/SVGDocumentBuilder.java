package com.visiors.visualstage.svg;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.inject.Inject;
import com.visiors.visualstage.pool.SVDescriptorPool;
import com.visiors.visualstage.transform.Transform;

public class SVGDocumentBuilder {

	private static final String[]     SVG_NAMESPACE      = new String[] { "xmlns", "http://www.w3.org/2000/svg" };
	private static final String[]     XLINK_NAMESPACE    = new String[] { "xmlns:xlink", "http://www.w3.org/1999/xlink" };
	private static final String[]     VERSION            = new String[] { "version", "1.1" };
	private final StringBuilder       svg;
	private final List<String>        usedReferences;
	private final boolean             formated           = true;
	private int                       groups;
	private final Map<String, String> documentAttributes = new HashMap<String, String>();
	@Inject
	private SVDescriptorPool svgDescriptorPool; 

	public SVGDocumentBuilder() {

		svg = new StringBuilder();
		usedReferences = new ArrayList<String>();
	}

	public void createDocument(int width, int height) {

		createDocument(width, height, null, null, null, null);
	}

	public void createDocument(int width, int height, Transform transform) {

		createDocument(width, height, transform, null, null, null);
	}

	public void createDocument(int width, int height, Transform xform, String svgBackgroundID, String svgFilterID,
			String svgTransformID) {

		createDocument(new Rectangle(0, 0, width, height), xform, svgBackgroundID, svgFilterID, svgTransformID);
	}

	public void createDocument(Rectangle viewBox, Transform xform, String svgBackgroundID, String svgFilterID,
			String svgTransformID) {

		svg.setLength(0);
		groups = 0;
		usedReferences.clear();

		appendSVGHeader(viewBox.x, viewBox.y, viewBox.width, viewBox.height);
		appendEffects(svgBackgroundID, svgFilterID, svgTransformID);
		appendInternalTransform(xform);
	}

	private void appendInternalTransform(Transform xform) {

		if (xform == null) {
			return;
		}

		// translate
		svg.append("<g transform='translate(").append(xform.getTranslateX());
		svg.append(",").append(xform.getTranslateY()).append(")'");/*
		 * // scale
		 * svg.append(" scale(").append(xform.getScaleX
		 * ()).append(",");
		 * svg.append(xform.getScaleY()).append(")"); //
		 * rotate
		 * svg.append(" rotate(").append(xform.getRotation
		 * ()).append(")"); // shear
		 * svg.append(" skewX(").append
		 * (xform.getShearX()).append(")");
		 * svg.append(" skewY("
		 * ).append(xform.getShearY()).append(")'");
		 */
		svg.append(">");
		lineBreak();
		groups++;
	}

	private void appendEffects(String backgroundID, String filterID, String xformID) {

		if (backgroundID != null || filterID != null || xformID != null) {
			// background for the view box
			if (backgroundID != null) {
				if (!usedReferences.contains(backgroundID)) {
					usedReferences.add(backgroundID);
				}
				svg.append(" <rect x='0' y='0' width='100%' height='100%' style='fill:url(#" + backgroundID + ")' />");

				lineBreak();
			}
			if (filterID != null || xformID != null) {
				svg.append("<g");

				// Transform
				if (xformID != null) {
					SVGDescriptor def = svgDescriptorPool.get(xformID);
					String xf = def.getAttribute("transform");
					if (xf != null) {
						svg.append(" transform='" + xf + "'");
					}
				}

				// filter
				if (filterID != null) {
					if (!usedReferences.contains(filterID)) {
						usedReferences.add(filterID);
					}
					svg.append(" filter='url(#" + filterID + ")'");
				}
				svg.append(">");
				lineBreak();
				groups++;
			}
		}
	}

	private void appendSVGHeader(int x, int y, int width, int height) {

		svg.append("<svg ").append("$DOC$").append(">");
		lineBreak();
		svg.append("$DEF$");

		documentAttributes.clear();
		documentAttributes.put(SVGDocumentBuilder.SVG_NAMESPACE[0], SVGDocumentBuilder.SVG_NAMESPACE[1]);
		documentAttributes.put(SVGDocumentBuilder.XLINK_NAMESPACE[0], SVGDocumentBuilder.XLINK_NAMESPACE[1]);
		documentAttributes.put(SVGDocumentBuilder.VERSION[0], SVGDocumentBuilder.VERSION[1]);
		documentAttributes.put("width", Integer.toString(width));
		documentAttributes.put("height", Integer.toString(height));
		if (x != 0 || y != 0) {
			documentAttributes.put(
					"viewBox",
					Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(width) + " "
							+ Integer.toString(height));
		}

		// documentAttributes.add(new String[] {"shape-rendering", "optimizeSpeed"}); // = turn off antialiasing
		// documentAttributes.add(new String[] {"text-rendering", "optimizeLegibility"}); // = turn off antialiasing

		lineBreak();
	}

	private void insertDocumentAttributes() {

		StringBuilder attributes = new StringBuilder();

		Set<Entry<String, String>> set = documentAttributes.entrySet();
		for (Entry<String, String> entry : set) {
			attributes.append(entry.getKey()).append("='").append(entry.getValue()).append("' ");
		}

		int idx = svg.indexOf("$DOC$");
		svg.replace(idx, idx + 5, attributes.toString());
	}

	private void insertDefinitions() {

		int idx = svg.indexOf("$DEF$");

		if (usedReferences.size() == 0) {
			svg.replace(idx, idx + 5, "");
			return;
		}

		StringBuilder defTag = new StringBuilder();

		defTag.append("\n<defs>");

		for (int i = 0; i < usedReferences.size(); i++) {
			SVGDescriptor def = svgDescriptorPool.get(usedReferences.get(i));
			if (def == null) {

				// System.err.println("Warning: The variable '" + usedReferences.get(i) +
				// "' referes to a not existing graphical template objects!");
				// usedReferences.remove(i--);
				continue;
			}
			addReferredDefiniton(def, defTag);
		}

		defTag.append("\n</defs>");
		svg.replace(idx, idx + 5, defTag.toString());
	}

	private void addReferredDefiniton(SVGDescriptor def, StringBuilder defs) {

		String ref[] = getReferenceIDs(def.definition);
		SVGDescriptor svgdef;
		for (String r : ref) {
			svgdef = svgDescriptorPool.get(r);
			if (svgdef == null) {
				// System.err.println("SVGDocumentBuilder: Error while building the svg document: id: '" + r +
				// "' Reason: the reference '" +svgdef+"' does not exists in the definition-pool");
			} else {
				addReferredDefiniton(svgdef, defs);
			}
		}
		defs.append(def.definition);
	}

	private final void lineBreak() {

		if (formated) {
			svg.append('\n');
		}
	}

	private final void tab() {

		if (formated) {
			svg.append('\t');
		}
	}

	public void addDocumentAttributes(String[][] attributes) {

		if (attributes != null) {
			for (String[] s : attributes) {
				if (!documentAttributes.containsKey(s[0])) {
					documentAttributes.put(s[0], s[1]);
				}
			}
		}
	}

	public void addContent(String description) {

		lineBreak();
		tab();

		if (description == null) {

			System.err.println("Error!");

		}

		svg.append(description);
		String[] ref = getReferenceIDs(description);
		for (String r : ref) {
			if (!usedReferences.contains(r)) {
				usedReferences.add(r);
			}
		}
	}

	private final String[] getReferenceIDs(String description) {

		List<String> refs = new ArrayList<String>();

		int s;

		for (s = 0;;) {
			s = description.indexOf("url(", s) + 5;
			if (s <= 4) {
				break;
			}
			int e = description.indexOf(')', s);
			refs.add(description.substring(s, e));
			s = e;
		}

		for (s = 0;;) {
			s = description.indexOf("href", s) + 5;
			if (s <= 4) {
				break;
			}
			char ch = description.charAt(s);
			int e = description.indexOf(ch, s + 1);
			refs.add(description.substring(s + 2, e));
			s = e;
		}
		for (s = 0;;) {
			s = description.indexOf("class", s) + 6;
			if (s <= 5) {
				break;
			}
			char ch = description.charAt(s);
			int e = description.indexOf(ch, s + 1);
			refs.add(description.substring(s + 1, e));
			s = e;
		}

		return refs.toArray(new String[refs.size()]);
	}

	public void closeDocument() {

		for (int i = 0; i < groups; i++) {
			svg.append("</g>");
			lineBreak();

		}
		insertDocumentAttributes();

		insertDefinitions();

		svg.append("</svg>");
	}

	public String getDocument() {

		String str = svg.toString();

		// System.err.println("-----------------------------------------------------------------");
		// System.err.println(str);

		return str;
	}

}
