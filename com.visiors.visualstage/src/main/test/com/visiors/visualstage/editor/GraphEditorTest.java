package com.visiors.visualstage.editor;




import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.impl.GraphEditor;
import com.visiors.visualstage.exception.DocumentExistsException;


public class GraphEditorTest {


	@Test (expected = DocumentExistsException.class)
	public void testNameConflict() {

		GraphEditor editor = new GraphEditor();
		editor.addCanvas(new MyCanvasImpl());
		editor.newDocument("New 1");
		editor.newDocument("New 1");		
	}


	@Test
	public void testDocumentManagement() {

		GraphEditor editor = new GraphEditor();
		editor.addCanvas(new MyCanvasImpl());

		GraphDocument doc;
		doc = editor.newDocument("Doc 1");
		Assert.assertNotNull(doc);
		Assert.assertEquals(doc.getTitle(), "Doc 1");
		doc = editor.newDocument("Doc 2");
		Assert.assertNotNull(doc);
		Assert.assertEquals(doc.getTitle(), "Doc 2");
		doc = editor.newDocument("Doc 3");
		Assert.assertNotNull(doc);
		Assert.assertEquals(doc.getTitle(), "Doc 3");

		doc = editor.getActiveDocument();
		Assert.assertEquals(doc.getTitle(), "Doc 3");

		List<GraphDocument> docs = editor.getDocuments();
		Assert.assertEquals(docs.size(), 3);
		editor.closeDocument("Doc 2");
		docs = editor.getDocuments();
		Assert.assertEquals(docs.size(), 2);

		doc = editor.getActiveDocument();
		Assert.assertNotNull(doc);

		editor.setActiveDocument("Doc 1");
		doc = editor.getActiveDocument();
		Assert.assertNotNull(doc);
		Assert.assertEquals(doc.getTitle(), "Doc 1");

		editor.closeDocument("Doc 1");
		doc = editor.getActiveDocument();
		Assert.assertNotNull(doc);
		Assert.assertEquals(doc.getTitle(), "Doc 3");

		editor.closeDocument("Doc 3");
		doc = editor.getActiveDocument();
		Assert.assertNull(doc);
		docs = editor.getDocuments();
		Assert.assertEquals(docs.size(), 0);
	}



}
