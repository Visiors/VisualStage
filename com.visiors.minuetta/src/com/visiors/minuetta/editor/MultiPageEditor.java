package com.visiors.minuetta.editor;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import com.visiors.visualstage.editor.GraphEditor;

public class MultiPageEditor extends TabPane {

	private final GraphEditor editor;

	public MultiPageEditor() {

		this.editor = new GraphEditor();
		setSide(Side.TOP);

		newDocument("New Document");

		new MouseKeyEventMediator(this); 
	}

	public void newDocument(String title) {

		final Tab tab = new EditorPage(this, title);
		getTabs().add(tab);
		super.getSelectionModel().select(tab);
	}


	public GraphEditor getEditor() {

		return editor;
	}


}
