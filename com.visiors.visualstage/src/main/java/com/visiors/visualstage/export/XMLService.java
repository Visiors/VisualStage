package com.visiors.visualstage.export;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.visiors.visualstage.constants.XMLConstants;
import com.visiors.visualstage.exception.XMLDocumentReadException;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;

public class XMLService {

	int indentLevel;
	Stack stack = new Stack();
	static PropertyList currentPropertyList = null;

	public String propertyList2XML(PropertyList properties, boolean insertHeader) throws IOException {

		indentLevel = 0;
		// System.err.println(properties.toString());

		final Writer stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);
		if (insertHeader) {
			printWriter.print(XMLConstants.XML_HEADER);
		}
		// writing graph data...
		writeAttribute(printWriter, properties);
		stringWriter.flush();
		String result = stringWriter.toString();
		stringWriter.close();
		// System.err.println(result);
		return result;

	}

	private void writeAttribute(PrintWriter writer, PropertyList properties) {

		// Tag-begin
		writer.print("\n" + space(indentLevel) + "<" + properties.getName());
		// attributes
		indentLevel++;

		final int len = properties.size();
		final String text = properties.getText();
		int attributes = 0;

		for (int i = 0; i < len; i++) {
			if (properties.get(i) instanceof PropertyUnit) {
				PropertyUnit p = (PropertyUnit) properties.get(i);
				writer.print(" " + p.getName() + "=\"" + p.getValue() + "\"");
				attributes++;
			}
		}
		if (attributes == len && text.isEmpty()) {
			writer.print("/>");
		} else {
			writer.print(">");
			for (int i = 0; i < properties.size(); i++) {
				if (properties.get(i) instanceof PropertyList) {
					PropertyList p = (PropertyList) properties.get(i);
					writeAttribute(writer, p);
				}
			}
			// Text
			if (!text.isEmpty()) {
				writer.print(text);
			} else {
				writer.print("\n" + space(indentLevel - 1));
			}
			// Tag-end
			writer.print("</" + properties.getName() + ">");
		}
		indentLevel--;
	}

	private final String space(int level) {

		StringBuffer space = new StringBuffer();
		for (int i = 0; i < level; i++) {
			space.append('\t');
		}
		return space.toString();
	}

	public PropertyList XML2PropertyList(String xml){

		SAXParser parser = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		DefaultHandler defaultHandler = new XMPParserDefaultHandler();
		indentLevel = 0;


		// Parsing input
		try {
			parser = factory.newSAXParser();
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			parser.parse(is, defaultHandler);
			is.close();

		} catch (ParserConfigurationException e) {
			throw new XMLDocumentReadException("Document could not be parsed: ", e );
		} catch (SAXException e) {
			throw new XMLDocumentReadException("Document could not be parsed: ", e );
		} catch (IOException e) {
			throw new XMLDocumentReadException("Document could not be read: ", e );
		}

		return currentPropertyList;
	}

	class XMPParserDefaultHandler extends DefaultHandler {

		@Override
		public void startDocument() throws SAXException {

			currentPropertyList = null;
		}

		@Override
		public void endDocument() throws SAXException {

			// System.err.println(readProperties.toString());
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {

			// if (attrs == null || attrs.getLength() == 0) {
			// PropertyList newList = new DefaultPropertyList(qName);
			// if (currentPropertyList != null)
			// currentPropertyList.add(newList);
			// currentPropertyList = newList;
			// stack.push(qName);
			// } else {
			// currentPropertyList.add(new DefaultPropertyUnit(qName,
			// attrs.getValue(0)));
			// }

			PropertyList newList = new DefaultPropertyList(qName);
			if (currentPropertyList != null) {
				currentPropertyList.add(newList);
			}
			currentPropertyList = newList;
			stack.push(qName);
			if (attrs != null) {
				for (int i = 0; i < attrs.getLength(); i++) {
					currentPropertyList.add(new DefaultPropertyUnit(attrs.getQName(i), attrs.getValue(i)));
				}
			}

		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {

			if (length != 0) {
				String text = new String(ch, start, length).trim();
				if (!text.isEmpty()) {
					String str = currentPropertyList.getText();
					str += text;
					currentPropertyList.setText(str);
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {

			if (stack.lastElement().equals(qName)) {
				if (currentPropertyList.getParent() != null) {
					currentPropertyList = currentPropertyList.getParent();
				}
				stack.pop();
			}
		}

		/** This method is called when warnings occur */
		@Override
		public void warning(SAXParseException exception) {

			System.err.println("WARNING: line " + exception.getLineNumber() + ": " + exception.getMessage());
		}

		/** This method is called when errors occur */
		@Override
		public void error(SAXParseException exception) {

			// System.err.println("ERROR: line " + exception.getLineNumber() +
			// ": "
			// + exception.getMessage());
		}

		/** This method is called when non-recoverable errors occur. */
		@Override
		public void fatalError(SAXParseException exception) throws SAXException {

			System.err.println("FATAL: line " + exception.getLineNumber() + ": " + exception.getMessage());
			throw (exception);
		}
	}
}
