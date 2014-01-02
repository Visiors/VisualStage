package com.visiors.visualstage.editor;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.impl.GraphEditor;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.pool.FormatDefinitionCollection;
import com.visiors.visualstage.pool.ShapeDefinitionCollection;

public class VisualGraphDrawingTest {

	@Test
	public void testCanvas() throws IOException {

		GraphEditor editor = new GraphEditor();
		MyCanvasImpl canvas = new MyCanvasImpl();
		editor.addCanvas(canvas);
		initShapeDefinitionCollection(editor);
		GraphDocument document = editor.newDocument("GraphDocument");
		Assert.assertNotNull(document);
		// create two nodes and an edge using the default nodes and edge
		VisualGraph graph = document.getGraph();
		Assert.assertNotNull(graph);
		VisualNode startNode = graph.createNode("StartNode");
		startNode.setBounds(new Rectangle(0, 0, 100, 100));
		VisualNode endNode = graph.createNode("EndNode");
		endNode.setBounds(new Rectangle(200, 100, 80, 80));
		VisualEdge edge = graph.createEdge("Connector");
		Assert.assertNotNull(startNode);
		Assert.assertNotNull(endNode);
		Assert.assertNotNull(edge);
		// connect the both nodes using the edge
		edge.connect(startNode, 0, endNode, 0);

		String svgDocument = document.getSVGDocument(canvas.getContext());
		saveSVG(svgDocument, "svg_export.svg");

		// screen image
		Image screen = canvas.getScreen();
		saveImage(screen, "screen.png");

		// export image
		Image export = document.getImage(canvas.getContext());
		saveImage(export, "image_export.png");

	}

	private void saveImage(Image img, String file) {

		try {
			File outputfile = new File(file);
			ImageIO.write((BufferedImage) img, "png", outputfile);
			System.out.println("saved to: " + outputfile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void saveSVG(String content, String file) {


		File outputfile = new File(file);
		try {
			FileWriter fileWriter = new FileWriter(outputfile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(content);
			bufferedWriter.close();
			System.out.println("saved to: " + outputfile.getAbsolutePath());
		} catch (IOException ex) {
			System.out.println("Error writing to file '" + outputfile.getAbsolutePath() + "'");
		}
	}

	private void initShapeDefinitionCollection(Editor editor) throws IOException {

		final ShapeDefinitionCollection shapesCollection = editor.getShapesCollection();
		String xmlContent = Files.toString(new File(
				"src/main/test/com/visiors/visualstage/editor/GraphObjecDefinition.xml"), Charsets.UTF_8);
		shapesCollection.loadAndPool(xmlContent);

		final FormatDefinitionCollection formatsCollection = editor.getFormatsCollection();
		xmlContent = Files.toString(new File("src/main/test/com/visiors/visualstage/editor/FormatDefinition.xml"),
				Charsets.UTF_8);
		formatsCollection.loadAndPool(xmlContent);
	}

	@Test
	public void testGraphCreationWithForm() {

		GraphEditor editor = new GraphEditor();

	}

	@Test
	public void testGraphCreationWithFormId() {

	}
}
