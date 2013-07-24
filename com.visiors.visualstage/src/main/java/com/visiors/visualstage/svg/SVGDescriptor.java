package com.visiors.visualstage.svg;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.export.XMLService;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.util.PropertyUtil;
import com.visiors.visualstage.util.UnitUtil;

public class SVGDescriptor {

	public double width;
	public double height;
	public String id;
	public String definition;
	public double ratio;
	public boolean keepRatio;
	private PropertyList propertyList;
	private PropertyList headerInfo;

	private static String[] symbolAttributeTag = new String[] { "x", "y", "width", "height", "viewBox", "transform",
	"preserveAspectRatio" };

	public SVGDescriptor(PropertyList pl) throws IOException {

		propertyList = pl;

		id = getAttribute("id");

		PropertyList[] p = convertSVG2Symbol(pl);
		propertyList = p[0];
		headerInfo = p[1];

		if (id == null || id.isEmpty()) {
			// TODO warning
			System.err.println("Warning: the SVG definition " + pl.getName()
					+ " will be ignored since it does not define the requiered attribute 'id'");
			return;
		}

		final XMLService xmlService = new XMLService();
		final String xml = xmlService.propertyList2XML(pl, false);
		init(xml);
	}

	public SVGDescriptor(String id, InputStream data) {

		this.id = id;
		try {
			StringBuffer svg = new StringBuffer(IOUtils.toString(data, "UTF-8"));

			int s = findSVGTag(svg);
			if (s == -1) {
				System.err.println("Missing the svg tag in svg document '" + id + "'");
				return; // TODO exception
			}
			// end of svg-tag
			int e = svg.indexOf(">", s);
			String svgTag = svg.substring(s, e) + "/>";

			final XMLService xmlService = new XMLService();
			PropertyList pl =  xmlService.XML2PropertyList(svgTag);

			PropertyList[] p = convertSVG2Symbol(pl);
			headerInfo = p[1];

			StringBuffer symbolTag = new StringBuffer("<symbol id='").append(this.id).append("' ");

			for (int i = 0; i < p[0].size(); i++) {
				PropertyUnit pu = (PropertyUnit) p[0].get(i);
				symbolTag.append(pu.getName());
				symbolTag.append("='");
				symbolTag.append(pu.getValue());
				symbolTag.append("' ");
			}
			symbolTag.append(">");

			svg.replace(0, e + 1, symbolTag.toString());
			s = svg.lastIndexOf("<");
			svg.replace(s, svg.length(), "</symbol>");

			init(svg.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				data.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void init(String svgContent) {

		definition = svgContent;
		width = getWidth(definition);
		height = getHeight(definition);
		ratio = keepAspectRatio(definition);
		keepRatio = ratio != 0;
	}

	public String[][] getDocumentAttributes() {

		if (headerInfo != null) {
			String[][] attr = new String[headerInfo.size()][2];
			for (int i = 0; i < attr.length; i++) {

				PropertyUnit pu = (PropertyUnit) headerInfo.get(i);
				attr[i][0] = pu.getName();
				attr[i][1] = (String) pu.getValue();
			}
			return attr;
		}
		return null;
	}

	//
	// private PropertyList extractSVGHeaderAttributes(PropertyList pl) {
	//
	// if(Constants.SVG_TAG.equalsIgnoreCase(pl.getName()))
	// {
	// DefaultPropertyList attributes = new DefaultPropertyList("Header");
	//
	// for (int i = pl.size()-1; i >= 0 ; i--) {
	// Property p = pl.get(i);
	// if(p instanceof PropertyUnit) {
	// pl.remove(p.getName());
	// attributes.add(p);
	// }
	// else if("metadata".equalsIgnoreCase(p.getName())) {
	// pl.remove(p.getName());
	// attributes.add(p);
	//
	// }
	// }
	// return attributes;
	// }
	// return null;
	// }
	//

	private PropertyList[] convertSVG2Symbol(PropertyList pl) {

		PropertyList documentAttributes = new DefaultPropertyList("Header");
		PropertyList symbolAttributes = pl;

		if (PropertyConstants.SVG_TAG.equalsIgnoreCase(pl.getName())) {
			symbolAttributes = new DefaultPropertyList("symbol");
			for (int i = pl.size() - 1; i >= 0; i--) {
				Property p = pl.get(i);
				if (documentAttribute(p)) {
					documentAttributes.add(p);
				} else {
					symbolAttributes.add(p);
				}
			}
			if (symbolAttributes.get("viewBox") == null) {
				PropertyUnit wp = (PropertyUnit) pl.get("width");
				PropertyUnit hp = (PropertyUnit) pl.get("height");
				if (wp != null && hp != null) {
					symbolAttributes.add(new DefaultPropertyUnit("viewBox", "0 0 " + wp.getValue() + " "
							+ hp.getValue()));
					symbolAttributes.add(new DefaultPropertyUnit("xml:space", "preserve"));
				}
			}
		}
		return new PropertyList[] { symbolAttributes, documentAttributes };
	}

	private boolean documentAttribute(Property p) {

		String name = p.getName();

		for (String element : SVGDescriptor.symbolAttributeTag) {
			if (element.equalsIgnoreCase(name)) {
				return false;
			}
		}

		return true;
	}

	private int findSVGTag(StringBuffer svg) {

		int localPtr = 0;
		char ch;
		for (int i = 0; i < svg.length() - 5; i++) {
			if (Character.toLowerCase(svg.charAt(i)) == 's' && Character.toLowerCase(svg.charAt(i + 1)) == 'v'
					&& Character.toLowerCase(svg.charAt(i + 2)) == 'g') {
				localPtr = i - 1;
				do {
					ch = svg.charAt(localPtr--);
				} while (Character.isWhitespace(ch));
				if (ch == '<') {
					return localPtr + 1;
				}
				i += 2;
			}
		}
		return -1;
	}

	public static int findSVGBodyEnd(StringBuffer svg) {

		return svg.lastIndexOf("<");
	}

	public String getAttribute(String name) {

		if (propertyList != null) {
			return PropertyUtil.getProperty(propertyList, name, null);
		}
		return null;
	}

	private final double getWidth(String svg) {

		return UnitUtil.strLength2px(SVGUtil.getSVGAttribute(svg, "width"));
	}

	private final double getHeight(String svg) {

		return UnitUtil.strLength2px(SVGUtil.getSVGAttribute(svg, "height"));
	}

	private final double keepAspectRatio(String svg) {

		String sRatio = SVGUtil.getSVGAttribute(svg, "preserveAspectRatio");
		if (sRatio != null && !sRatio.equalsIgnoreCase("none")) {
			String vBox = SVGUtil.getSVGAttribute(svg, "viewBox");
			if (vBox != null) {
				String values[] = vBox.trim().split(" ");
				double x = UnitUtil.strLength2px(values[0]);
				double y = UnitUtil.strLength2px(values[1]);
				double w = UnitUtil.strLength2px(values[2]);
				double h = UnitUtil.strLength2px(values[3]);
				return (w - x) / (h - y);
			}
		}
		return 0.0;
	}

}
