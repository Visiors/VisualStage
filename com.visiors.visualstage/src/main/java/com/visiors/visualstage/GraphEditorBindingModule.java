package com.visiors.visualstage;

import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.DefaultVisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.impl.DefaultVisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.graph.view.node.impl.DefaultVisualNode;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.DefaultGroupingHandler;
import com.visiors.visualstage.handler.GroupingHandler;
import com.visiors.visualstage.handler.SelectionHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.handler.impl.DefaultClipboardHandler;
import com.visiors.visualstage.handler.impl.DefaultSelectionHander;
import com.visiors.visualstage.handler.impl.DefaultUndoRedoHandler;
import com.visiors.visualstage.stage.interaction.InteractionHandler;
import com.visiors.visualstage.stage.interaction.impl.DefaultInteractionHandler;
import com.visiors.visualstage.stage.layer.MultiLayerEditor;
import com.visiors.visualstage.stage.layer.impl.DefaultMultiLayerEditor;
import com.visiors.visualstage.stage.ruler.DefaultStageDesigner;
import com.visiors.visualstage.stage.ruler.StageDesigner;
import com.visiors.visualstage.system.DefaultSystemUnitService;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.transform.Transformer;
import com.visiors.visualstage.transform.VSTransformer;
import com.visiors.visualstage.validation.DefaultValidator;
import com.visiors.visualstage.validation.Validator;

public class GraphEditorBindingModule extends BindingModule {

    // binding the guava event bus
    public void bindEventBus(Binder binder) {

        binder.bind(EventBus.class).in(Singleton.class);
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

        binder.install(new FactoryModuleBuilder().implement(VisualGraph.class, DefaultVisualGraph.class).build(
                GraphViewFactory.class));
    }

    public void bindNodeViewFactory(Binder binder) {

        binder.install(new FactoryModuleBuilder().implement(VisualNode.class, DefaultVisualNode.class).build(
                NodeViewFactory.class));
    }

    public void bindEdgeViewFactory(Binder binder) {

        binder.install(new FactoryModuleBuilder().implement(VisualEdge.class, DefaultVisualEdge.class).build(
                EdgeViewFactory.class));
    }

}
