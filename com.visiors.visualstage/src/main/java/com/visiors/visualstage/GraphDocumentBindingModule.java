package com.visiors.visualstage;

import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.graphstage.DefaultStageDesigner;
import com.visiors.visualstage.graphstage.StageDesigner;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.DefaultGroupingHandler;
import com.visiors.visualstage.handler.GroupingHandler;
import com.visiors.visualstage.handler.SelectionHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.handler.impl.VSClipboardHandler;
import com.visiors.visualstage.handler.impl.VSSelectionHander;
import com.visiors.visualstage.handler.impl.VSUndoRedoHandler;
import com.visiors.visualstage.layer.MultiLayerEditor;
import com.visiors.visualstage.layer.impl.DefaultMultiLayerEditor;
import com.visiors.visualstage.mode.InteractionHandler;
import com.visiors.visualstage.mode.impl.DefaultInteractionHandler;
import com.visiors.visualstage.system.DefaultSystemUnitService;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.validation.DefaultValidator;
import com.visiors.visualstage.validation.Validator;
import com.visiors.visualstage.view.edge.EdgeView;
import com.visiors.visualstage.view.edge.impl.DefaultEdgeView;
import com.visiors.visualstage.view.edge.impl.EdgeViewFactory;
import com.visiors.visualstage.view.graph.GraphView;
import com.visiors.visualstage.view.graph.impl.DefaultGraphView;
import com.visiors.visualstage.view.graph.impl.GraphViewFactory;
import com.visiors.visualstage.view.node.NodeView;
import com.visiors.visualstage.view.node.impl.DefaultNodeView;
import com.visiors.visualstage.view.node.impl.NodeViewFactory;
import com.visiors.visualstage.view.transform.Transformer;
import com.visiors.visualstage.view.transform.VSTransformer;

public class GraphDocumentBindingModule extends BindingModule {

    // binding the guava event bus
    public void bindEventBus(Binder binder) {

        binder.bind(EventBus.class).in(Singleton.class);
    }

    // binding the default clip board handler
    public void bindDefaultClipboardHandler(Binder binder) {

        binder.bind(ClipboardHandler.class).to(VSClipboardHandler.class);
    }

    // binding the default redo-undo handler
    public void bindDefaultUndoRedoHandler(Binder binder) {

        binder.bind(UndoRedoHandler.class).to(VSUndoRedoHandler.class);
    }

    // binding the default selection handler
    public void bindDefaultSelectionHander(Binder binder) {

        binder.bind(SelectionHandler.class).to(VSSelectionHander.class);
    }

    // binding the default transformer
    public void bindAffineTransform(Binder binder) {

        binder.bind(Transformer.class).to(VSTransformer.class);
    }

    // binding the default transformer
    public void bindStageDesigner(Binder binder) {

        binder.bind(StageDesigner.class).to(DefaultStageDesigner.class);
    }

    // binding the default grouping handler
    public void bindGroupingHandler(Binder binder) {

        binder.bind(GroupingHandler.class).to(DefaultGroupingHandler.class);
    }

    // binding the default interaction manager
    public void bindInteractionManager(Binder binder) {

        binder.bind(InteractionHandler.class).to(DefaultInteractionHandler.class);
    }

    // binding the default validator
    public void bindValidator(Binder binder) {

        binder.bind(Validator.class).to(DefaultValidator.class);
    }

    // binding the default layer manager
    public void bindLayerManager(Binder binder) {

        binder.bind(MultiLayerEditor.class).to(DefaultMultiLayerEditor.class);
    }

    // binding the default unit system service
    public void bindUnitSystemService(Binder binder) {

        binder.bind(SystemUnit.class).to(DefaultSystemUnitService.class);
    }

    public void configureFileExtensions(Binder binder) {

        binder.bind(String.class).annotatedWith(Names.named(PropertyConstants.FILE_EXTENSIONS)).toInstance("vst");
    }

    public void bindGraphViewFactory(Binder binder) {

        binder.install(new FactoryModuleBuilder().implement(GraphView.class, DefaultGraphView.class).build(
                GraphViewFactory.class));
    }

    public void bindNodeViewFactory(Binder binder) {

        binder.install(new FactoryModuleBuilder().implement(NodeView.class, DefaultNodeView.class).build(
                NodeViewFactory.class));
    }

    public void bindEdgeViewFactory(Binder binder) {

        binder.install(new FactoryModuleBuilder().implement(EdgeView.class, DefaultEdgeView.class).build(
                EdgeViewFactory.class));
    }

}
