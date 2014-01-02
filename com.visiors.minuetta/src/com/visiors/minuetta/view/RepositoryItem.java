package com.visiors.minuetta.view;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import com.visiors.visualstage.editor.impl.GraphEditor;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.pool.GraphBuilder;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.renderer.DefaultDrawingContext;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.ImageFactory;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.svg.DefaultSVGDocumentBuilder;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.PropertyUtil;

public class RepositoryShapeItem {

	private final String category;
	private final String shapeName;
	private String toolTip;
	private final WritableImage image;
	private final String localizedName;
	private final String tooltip;
	private final String type;
	private final VisualGraphObject vgo;
	private final GraphEditor editor;
	private final int width = 40;
	private final int height = 40;

	public RepositoryShapeItem(GraphEditor editor, String category, PropertyList shapeProperties) {

		this.editor = editor;
		this.category = category;
		this.shapeName = PropertyUtil.getProperty(shapeProperties, "name", "");
		this.localizedName = PropertyUtil.getProperty(shapeProperties, "displayname", "");
		this.tooltip = PropertyUtil.getProperty(shapeProperties, "tooltip", "");
		this.type = PropertyUtil.getProperty(shapeProperties, "type", "");
		this.image = createImage();
		this.vgo = fetchGraphObject();
	}

	public String getDisplayName() {

		return localizedName;
	}

	public String getShapeName() {

		return shapeName;
	}

	public VisualGraphObject getShape() {

		return vgo;
	}

	public String getToolTip() {

		return toolTip;
	}

	public String getCategory() {

		return category;
	}

	public String getTooltip() {

		return tooltip;
	}

	public WritableImage getShapePreview() {

		return image;
	}

	private VisualGraphObject fetchGraphObject() {

		final GraphBuilder builder = editor.getGraphBuilder();

		if (type.equalsIgnoreCase("edge")) {
			return builder.createEdge(shapeName);
		}
		if (type.equalsIgnoreCase("node")) {
			return builder.createNode(shapeName);
		}
		if (type.equalsIgnoreCase("subgrph")) {
			return builder.createSubgraph(shapeName);
		}

		return null;

	}

	private WritableImage createImage() {

		if (vgo != null) {
			return getImage();
		}
		return new WritableImage(width, height);
	}



	private WritableImage getImage() {

		final Transform transformer = createTransformation(vgo, width, height);
		vgo.setTransformer(transformer);
		final DrawingContext ctx = new DefaultDrawingContext(Resolution.SCREEN_LOW_DETAIL, DrawingSubject.OBJECT);
		final ImageFactory imageMaker = new ImageFactory(new DefaultSVGDocumentBuilder());
		final Image snapshot = imageMaker.createSnapshot(vgo, ctx, DrawingSubject.OBJECT);
		final WritableImage imageFx = new WritableImage(width, height);
		return SwingFXUtils.toFXImage((BufferedImage) snapshot, imageFx);
	}

	private Transform createTransformation(VisualGraphObject vgo, int w, int h) {

		final Transform transformer = new DefaultTransformer();
		final Rectangle r = vgo.getBounds();
		final double scale = Math.min((double) (w) / r.width, (double) (h) / r.height);
		transformer.setScale(scale);
		transformer.setXTranslate((int) ((w - r.width * scale) / 2));
		transformer.setYTranslate((int) ((h - r.height * scale) / 2));
		return transformer;
	}

}
