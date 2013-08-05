package com.visiors.visualstage.editor;




import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.layer.Layer;
import com.visiors.visualstage.exception.InvalidLayerRemovalException;


public class LayerTest {

	@Test (expected = InvalidLayerRemovalException.class)
	public void layerIntegrityTest() {

		GraphEditor editor = new GraphEditor();
		editor.addCanvas(new MyCanvasImpl());
		GraphDocument document = editor.newDocument("GraphDocument");
		Assert.assertNotNull(document);

		List<Layer> layers = document.getLayers();
		Assert.assertEquals(layers.size(), 1);
		Assert.assertEquals(document.getLayerCount(), 1);
		Layer l0  = layers.get(0);
		Assert.assertEquals(document.getCurrentLayer(), l0);
		Layer l2 = document.addLayer(2);
		Assert.assertEquals(document.getCurrentLayer(), l2);
		Layer l1 = document.addLayer(1);
		Assert.assertEquals(document.getCurrentLayer(), l1);
		layers = document.getLayers();

		// check sorting by order
		Assert.assertEquals(layers.get(0).getOrder(), 0);
		Assert.assertEquals(layers.get(0).getID(), 0);
		Assert.assertEquals(layers.get(1).getOrder(), 1);
		Assert.assertEquals(layers.get(1).getID(), 2);
		Assert.assertEquals(layers.get(2).getOrder(), 2);
		Assert.assertEquals(layers.get(2).getID(), 1);

		// check visibility
		l2.setVisible(false);
		Assert.assertTrue(l0.isVisible());
		Assert.assertTrue(l1.isVisible());
		Assert.assertFalse(l2.isVisible());

		// check add/remove
		document.removeLayer(1);
		Assert.assertEquals(document.getCurrentLayer(), l2);
		document.removeLayer(2);
		Assert.assertEquals(document.getCurrentLayer(), l0);

		// expect an exception while trying to delete the last existing layer
		document.removeLayer(0);

		Assert.fail("last layer has been removed");
	}

}
