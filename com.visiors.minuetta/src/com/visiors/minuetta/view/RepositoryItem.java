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
import com.visiors.visualstage.renderer.ImageFactory;
import com.visiors.visualstage.renderer.ShadowRenderer;
import com.visiors.visualstage.svg.DefaultSVGDocumentBuilder;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.PropertyUtil;

public class RepositoryItem {

	private final String category;
	private final String shapeName;
	private String toolTip;
	private final String localizedName;
	private final String tooltip;
	private final GraphBuilder.GraphObjectType type;
	private final VisualGraphObject vgo;
	private final GraphEditor editor;
	private WritableImage image;
	private final int defaultImageWidth = 50;
	private final int defaultImageHeight = 50;

	public RepositoryItem(GraphEditor editor, String category, PropertyList shapeProperties) {

		this.editor = editor;
		this.category = category;
		this.shapeName = PropertyUtil.getProperty(shapeProperties, "name", "");
		this.localizedName = PropertyUtil.getProperty(shapeProperties, "displayname", "");
		this.tooltip = PropertyUtil.getProperty(shapeProperties, "tooltip", "");
		this.type = GraphBuilder.GraphObjectType.valueOf(PropertyUtil.getProperty(shapeProperties, "type", ""));
		this.vgo = createGraphObject();
		this.image = createImage(defaultImageWidth, defaultImageHeight);
	}

	public String getDisplayName() {

		return localizedName;
	}

	public String getShapeName() {

		return shapeName;
	}

	public String getType() {

		return type.name();
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

	public WritableImage getShapePreview(double scale) {

		if (scale < 10) {
			image = new WritableImage(1, 1);
		} else {
			image = createImage((int) (defaultImageWidth * scale / 100.0), (int) (defaultImageHeight * scale / 100.0));
		}
		return image;
	}

	private VisualGraphObject createGraphObject() {

		final GraphBuilder builder = editor.getGraphBuilder();
		return builder.create(type, shapeName);
	}

	private WritableImage createImage(int width, int height) {

		WritableImage image = null;
		if (vgo != null) {
			image = getImage(width, height);
		}
		if (image != null) {
			return image;
		}
		return new WritableImage(width, height);
	}

	private WritableImage getImage(int width, int height) {

		final Transform transformer = createTransformation(vgo, width, height);
		vgo.setTransformer(transformer);
		final ImageFactory imageMaker = new ImageFactory(new DefaultSVGDocumentBuilder());
		Image snapshot = imageMaker.createSnapshot(vgo);
		if (snapshot == null) {
			return null;
		}
		// shadow
		ShadowRenderer.shadowSize = 3;
		snapshot = ShadowRenderer.createShadow(snapshot);
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
