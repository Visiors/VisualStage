package com.visiors.visualstage.editor;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.GroupingHandler;
import com.visiors.visualstage.handler.SelectionHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.interaction.Interactable;
import com.visiors.visualstage.pool.ShapeTemplatePool;
import com.visiors.visualstage.renderer.Device;

public interface Editor extends Interactable {

	public void addCanvas(Device device);

	public GraphDocument newDocument(String title);

	public GraphDocument loadDocument(String content);

	public GraphDocument getDocument(String title);

	public String saveDocument(String title);

	public void printDocument(String title);

	public void renameDocument(String currentTitle, String newTitle);

	public void setActiveDocument(String title);

	public GraphDocument getActiveDocument();

	public ShapeTemplatePool getGraphObjectTemplateStore();

	public SelectionHandler getSelectionHandler();

	public GroupingHandler getGroupingHandler();

	public UndoRedoHandler getUndoRedoHandler();

	public ClipboardHandler getClipboardHandler();




	// public void registerInplaceTextEditor(InplaceTextditor editor);

}