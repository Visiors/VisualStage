package com.visiors.visualstage.svg;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.inject.Inject;
import com.visiors.visualstage.pool.FormatCollection;
import com.visiors.visualstage.transform.Transform;

public class DefaultSVGDocumentBuilder implements SVGDocumentBuilder {

	private static final String[]     SVG_NAMESPACE      = new String[] { "xmlns", "http://www.w3.org/2000/svg" };
	private static final String[]     XLINK_NAMESPACE    = new String[] { "xmlns:xlink", "http://www.w3.org/1999/xlink" };
	private static final String[]     VERSION            = new String[] { "version", "1.1" };
	private final StringBuilder       svg;
	private final List<String>        usedReferences;
	private final boolean             formated           = true;
	private int                       groups;
	private final Map<String, String> documentAttributes = new HashMap<String, String>();
	@Inject
	private FormatCollection formatCollection; 

	public DefaultSVGDocumentBuilder() {

		svg = new StringBuilder();
		usedReferences = new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.SVGDocumentBuilder#createDocument(int, int)
	 */
	@Override
	public void createEmptyDocument(int width, int height) {

		createEmptyDocument(width, height, null, null);
	}

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.SVGDocumentBuilder#createDocument(int, int, com.visiors.visualstage.transform.Transform)
	 */
	@Override
	public void createEmptyDocument(int width, int height, Transform transform) {

		createEmptyDocument(width, height, transform, null);
	}

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.SVGDocumentBuilder#createDocument(int, int, com.visiors.visualstage.transform.Transform, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createEmptyDocument(int width, int height, Transform transform, DocumentConfig config) {

		createEmptyDocument(new Rectangle(0, 0, width, height), transform, config);
	}

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.SVGDocumentBuilder#createDocument(java.awt.Rectangle, com.visiors.visualstage.transform.Transform, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createEmptyDocument(Rectangle viewBox, Transform transform, DocumentConfig config) {

		svg.setLength(0);
		groups = 0;
		usedReferences.clear();

		appendSVGHeader(viewBox.x, viewBox.y, viewBox.width, viewBox.height);
		appendEffects(config);
		appendInternalTransform(transform);
	}

	private void appendInternalTransform(Transform transform) {

		if (transform == null) {
			return;
		}

		// translate
		svg.append("<g transform='translate(").append(transform.getXTranslate());
		svg.append(",").append(transform.getYTranslate()).append(") ");

		// scale
		svg.append(" scale(").append(transform.getScale()).append(",");
		svg.append(transform.getScale()).append(")'");
		/* // rotate
		 * svg.append(" rotate(").append(transform.getRotation
		 * ()).append(")"); // shear
		 * svg.append(" skewX(").append
		 * (transform.getShearX()).append(")");
		 * svg.append(" skewY("
		 * ).append(transform.getShearY()).append(")'");
		 */
		svg.append(">");
		lineBreak();
		groups++;
	}

	private void appendEffects(DocumentConfig config) {

		if (config != null) {
			// background for the view box
			if (config.getBackgroundColor() != null) {
				if (!usedReferences.contains(config.getBackgroundColor())) {
					usedReferences.add(config.getBackgroundColor());
				}
				svg.append(" <rect x='0' y='0' width='100%' height='100%' style='fill:url(#" + config.getBackgroundColor() + ")' />");

				lineBreak();
			}
			if (config.getFilter() != null || config.getTransformation() != null) {
				svg.append("<g");

				// Transform
				if (config.getTransformation() != null) {
					SVGDescriptor def = formatCollection.get(config.getTransformation());
					String xf = def.getAttribute("transform");
					if (xf != null) {
						svg.append(" transform='" + xf + "'");
					}
				}

				// filter
				if (config.getFilter() != null) {
					if (!usedReferences.contains(config.getFilter())) {
						usedReferences.add(config.getFilter());
					}
					svg.append(" filter='url(#" + config.getFilter() + ")'");
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
		documentAttributes.put(DefaultSVGDocumentBuilder.SVG_NAMESPACE[0], DefaultSVGDocumentBuilder.SVG_NAMESPACE[1]);
		documentAttributes.put(DefaultSVGDocumentBuilder.XLINK_NAMESPACE[0], DefaultSVGDocumentBuilder.XLINK_NAMESPACE[1]);
		documentAttributes.put(DefaultSVGDocumentBuilder.VERSION[0], DefaultSVGDocumentBuilder.VERSION[1]);
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
			SVGDescriptor def = formatCollection.get(usedReferences.get(i));
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
			svgdef = formatCollection.get(r);
			if (svgdef == null) {
				// System.err.println("DefaultSVGDocumentBuilder: Error while building the svg document: id: '" + r +
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

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.SVGDocumentBuilder#addDocumentAttributes(java.lang.String[][])
	 */
	@Override
	public void addDocumentAttributes(String[][] attributes) {

		if (attributes != null) {
			for (String[] s : attributes) {
				if (!documentAttributes.containsKey(s[0])) {
					documentAttributes.put(s[0], s[1]);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.SVGDocumentBuilder#addContent(java.lang.String)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.SVGDocumentBuilder#closeDocument()
	 */
	@Override
	public void finlaizeDocument() {

		for (int i = 0; i < groups; i++) {
			svg.append("</g>");
			lineBreak();

		}
		insertDocumentAttributes();

		insertDefinitions();

		svg.append("</svg>");
	}

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.SVGDocumentBuilder#getDocument()
	 */
	@Override
	public String getDocument() {

		String str = svg.toString();

		// System.err.println("-----------------------------------------------------------------");
		// System.err.println(str);

		return str;
	}

}
