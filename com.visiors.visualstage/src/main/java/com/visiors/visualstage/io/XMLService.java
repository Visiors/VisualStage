package com.visiors.visualstage.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;

public class XMLService {

	int indentLevel;
	Stack stack = new Stack();
	static PropertyList currentPropertyList = null;


	
	public void propertyList2XML(OutputStream os, PropertyList properties, boolean insertHeader) throws IOException {
		OutputStreamWriter osWriter = null;
		BufferedWriter bufferedWriter = null;
		PrintWriter pw = null;
		indentLevel = 0;
		// System.err.println(properties.toString());

		try {
			// Create document
			
			if(insertHeader) {
    			osWriter = new OutputStreamWriter(os, PropertyConstants.XML_CHAR_SET);
    			bufferedWriter = new BufferedWriter(osWriter);
    			pw = new PrintWriter(bufferedWriter);
    			pw.print(PropertyConstants.XML_HEADER);
			}
			else {
				osWriter = new OutputStreamWriter(os);
				bufferedWriter = new BufferedWriter(osWriter);
				pw = new PrintWriter(bufferedWriter);
			}
			// writing graph data...
			writeAttribute(pw, properties);

		} finally {
			pw.close();
			bufferedWriter.close();
			osWriter.close();
			os.flush();
			os.close();
		}
	}

	private void writeAttribute(PrintWriter pw, PropertyList properties) {
		// Tag-begin
		pw.print("\n" + space(indentLevel) + "<" + properties.getName());
		// attributes
		indentLevel++;

		final int len = properties.size();
		final String text = properties.getText();
		int attributes = 0;

		for (int i = 0; i < len; i++) {
			if (properties.get(i) instanceof PropertyUnit) {
				PropertyUnit p = (PropertyUnit) properties.get(i);
				pw.print(" " + p.getName() + "=\"" + p.getValue() + "\"");
				attributes++;
			}
		}
		if (attributes == len && text.isEmpty())
			pw.print("/>");
		else {
			pw.print(">");
			for (int i = 0; i < properties.size(); i++) {
				if (properties.get(i) instanceof PropertyList) {
					PropertyList p = (PropertyList) properties.get(i);
					writeAttribute(pw, p);
				}
			}
			//Text
			if(!text.isEmpty())
				pw.print(text);
			else
				pw.print("\n" + space(indentLevel-1));
			// Tag-end
			pw.print("</" + properties.getName() + ">");
		}
		indentLevel--;
	}

	private final String space(int level) {
		StringBuffer space = new StringBuffer();
		for (int i = 0; i < level; i++)
			space.append('\t');
		return space.toString();
	}

	
	public PropertyList XML2PropertyList(InputStream is) throws IOException, ParserConfigurationException,
			SAXException {
		SAXParser parser = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		DefaultHandler defaultHandler = new XMPParserDefaultHandler();
		indentLevel = 0;

		try {
			// Parsing input
			parser = factory.newSAXParser();
			parser.parse(is, defaultHandler);

			return currentPropertyList;
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} finally {
			is.close();
		}
	}

	class XMPParserDefaultHandler extends DefaultHandler {
		
		
		public void startDocument() throws SAXException {
			currentPropertyList = null;
		}

		
		public void endDocument() throws SAXException {
			// System.err.println(readProperties.toString());
		}

		public void startElement(String uri, String localName, String qName, Attributes attrs)
				throws SAXException {
//			if (attrs == null || attrs.getLength() == 0) {
//				PropertyList newList = new DefaultPropertyList(qName);
//				if (currentPropertyList != null)
//					currentPropertyList.add(newList);
//				currentPropertyList = newList;
//				stack.push(qName);
//			} else {
//				currentPropertyList.add(new DefaultPropertyUnit(qName, attrs.getValue(0)));
//			}
			
			PropertyList newList = new DefaultPropertyList(qName);
        	if(currentPropertyList != null)
        		currentPropertyList.add(newList);
        	currentPropertyList = newList;
        	stack.push(qName);    
        	if(attrs != null) {
        		for (int i = 0; i < attrs.getLength(); i++) {
        			currentPropertyList.add(new DefaultPropertyUnit( attrs.getQName(i), attrs.getValue(i) ));
        		}
        	}
        	
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if(length != 0) {
				String text = new String(ch, start, length).trim();
				if(!text.isEmpty()) {
					String str = currentPropertyList.getText();
					str += text;
					currentPropertyList.setText(str);
				}
			}
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (stack.lastElement().equals(qName)) {
				if (currentPropertyList.getParent() != null)
					currentPropertyList = currentPropertyList.getParent();
				stack.pop();
			}
		}
		
		 /** This method is called when warnings occur */
        public void warning(SAXParseException exception) {
          System.err.println("WARNING: line " + exception.getLineNumber() + ": "
              + exception.getMessage());
        }

        /** This method is called when errors occur */
        public void error(SAXParseException exception) {
//          System.err.println("ERROR: line " + exception.getLineNumber() + ": "
//              + exception.getMessage());
        }

        /** This method is called when non-recoverable errors occur. */
        public void fatalError(SAXParseException exception) throws SAXException {
          System.err.println("FATAL: line " + exception.getLineNumber() + ": "
              + exception.getMessage());
          throw (exception);
        }
	}
}
