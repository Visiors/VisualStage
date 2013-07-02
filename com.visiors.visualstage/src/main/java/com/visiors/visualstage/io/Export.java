package com.visiors.visualstage.io;

import java.io.OutputStream;

import com.visiors.visualstage.document.GraphDocument;

public class Export {

	private final GraphDocument graphDocument;
	
	
	public Export(GraphDocument graphDocument) {
		this.graphDocument = graphDocument;
	}
	
	public OutputStream exportToSVG() {

//		TransformerFactory factory = TransformerFactory.newInstance();
//		Transformer trans;
//		try {
//			trans = factory.newTransformer();
//			Source src = new DOMSource(graphDocument.getSVGDocument());
//			Result res = new StreamResult(System.out);
//			trans.transform(src, res);
//		} catch (TransformerConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return null;
	}
}
