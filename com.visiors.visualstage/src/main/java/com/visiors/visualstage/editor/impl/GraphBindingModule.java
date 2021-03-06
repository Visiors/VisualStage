package com.visiors.visualstage.editor.impl;

import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.VisualEdgeProvider;
import com.visiors.visualstage.document.VisualGraphProvider;
import com.visiors.visualstage.document.VisualNodeProvider;
import com.visiors.visualstage.document.impl.DefaultGraphDocument;
import com.visiors.visualstage.document.layer.LayerManager;
import com.visiors.visualstage.document.layer.impl.DefaultLayerManager;
import com.visiors.visualstage.editor.BindingModule;
import com.visiors.visualstage.editor.Editor;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.DefaultGroupingHandler;
import com.visiors.visualstage.handler.GroupingHandler;
import com.visiors.visualstage.handler.SelectionHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.handler.impl.DefaultClipboardHandler;
import com.visiors.visualstage.handler.impl.DefaultSelectionHander;
import com.visiors.visualstage.handler.impl.DefaultUndoRedoHandler;
import com.visiors.visualstage.pool.DefaultFormatDefinitionCollection;
import com.visiors.visualstage.pool.DefaultGraphBuilder;
import com.visiors.visualstage.pool.DefaultShapeDefinitionCollection;
import com.visiors.visualstage.pool.FormatDefinitionCollection;
import com.visiors.visualstage.pool.GraphBuilder;
import com.visiors.visualstage.pool.ShapeDefinitionCollection;
import com.visiors.visualstage.stage.DefaultStageDesigner;
import com.visiors.visualstage.stage.StageDesigner;
import com.visiors.visualstage.svg.DefaultSVGDocumentBuilder;
import com.visiors.visualstage.svg.SVGDocumentBuilder;
import com.visiors.visualstage.system.DefaultSystemUnitService;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.tool.ToolManager;
import com.visiors.visualstage.tool.impl.DefaultToolManager;
import com.visiors.visualstage.transform.DefaultTransformer;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.validation.DefaultValidator;
import com.visiors.visualstage.validation.Validator;

public class GraphBindingModule extends BindingModule {

	// binding the guava event bus
	public void bindEventBus(Binder binder) {

		binder.bind(EventBus.class).in(Singleton.class);
	}

	// binding the default editor 
	public void bindDefaultGraphEditor(Binder binder) {

		binder.bind(Editor.class).to(GraphEditor.class);
	}

	// binding the default graph document
	public void bindDefaultGraphDocument(Binder binder) {

		binder.bind(GraphDocument.class).to(DefaultGraphDocument.class);
	}

	// binding the default clip board handler
	public void bindDefaultClipboardHandler(Binder binder) {

		binder.bind(ClipboardHandler.class).to(DefaultClipboardHandler.class);
	}

	// binding the default redo-undo handler
	public void bindDefaultUndoRedoHandler(Binder binder) {

		binder.bind(UndoRedoHandler.class).to(DefaultUndoRedoHandler.class);
	}

	// binding the default selection handler
	public void bindDefaultSelectionHander(Binder binder) {

		binder.bind(SelectionHandler.class).to(DefaultSelectionHander.class);
	}

	// binding the default transform
	public void bindAffineTransform(Binder binder) {

		binder.bind(Transform.class).to(DefaultTransformer.class);
	}

	// binding the default transform
	public void bindStageDesigner(Binder binder) {

		binder.bind(StageDesigner.class).to(DefaultStageDesigner.class).in(Singleton.class);
	}

	// binding the default grouping handler
	public void bindGroupingHandler(Binder binder) {

		binder.bind(GroupingHandler.class).to(DefaultGroupingHandler.class);
	}

	// binding the default interaction manager
	public void bindInteractionManager(Binder binder) {

		binder.bind(ToolManager.class).to(DefaultToolManager.class).in(Singleton.class);;
	}

	// binding the default validator
	public void bindValidator(Binder binder) {

		binder.bind(Validator.class).to(DefaultValidator.class);
	}

	// binding the default layer manager
	public void bindLayerManager(Binder binder) {

		binder.bind(LayerManager.class).to(DefaultLayerManager.class);
	}

	// binding the default unit system service
	public void bindUnitSystemService(Binder binder) {

		binder.bind(SystemUnit.class).to(DefaultSystemUnitService.class);
	}
	// binding the visual node provider
	public void bindVisualNodeProvider(Binder binder) {

		binder.bind(VisualNode.class).toProvider(VisualNodeProvider.class);
	}
	// binding the visual edge provider
	public void bindVisualEdgeProvider(Binder binder) {

		binder.bind(VisualEdge.class).toProvider(VisualEdgeProvider.class);
	}
	// binding the visual graph provider
	public void bindVisualGraphProvider(Binder binder) {

		binder.bind(VisualGraph.class).toProvider(VisualGraphProvider.class);
	}
	// binding the shapes collection
	public void bindShapesCollection(Binder binder) {

		binder.bind(ShapeDefinitionCollection.class).to(DefaultShapeDefinitionCollection.class).in(Singleton.class);
	}
	// binding the format collection
	public void bindFormatCollection(Binder binder) {

		binder.bind(FormatDefinitionCollection.class).to(DefaultFormatDefinitionCollection.class).in(Singleton.class);
	}
	// binding the graph builder
	public void bindGraphBuilder(Binder binder) {

		binder.bind(GraphBuilder.class).to(DefaultGraphBuilder.class).in(Singleton.class);
	}
	// binding the default svg document builder  collection
	public void bindSVGDocumentBuilder(Binder binder) {

		binder.bind(SVGDocumentBuilder.class).to(DefaultSVGDocumentBuilder.class);
	}

	// public void configureFileExtensions(Binder binder) {
	//
	// binder.bind(String.class).annotatedWith(Names.named(PropertyConstants.FILE_EXTENSIONS)).toInstance("vst");
	// }
}
