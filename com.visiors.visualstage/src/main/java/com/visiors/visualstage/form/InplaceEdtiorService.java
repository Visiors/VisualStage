package com.visiors.visualstage.form;


public class InplaceEdtiorService {

	private static InplaceTextditor editor;

	public static void registerEditor(InplaceTextditor e){
		editor = e;
	}
	
	public static void startEditing(TextContainer host) {
		if(editor == null) {
			
			//TODO no editor available
			return;
		}
		
		editor.startEditing(host);
	}

}
