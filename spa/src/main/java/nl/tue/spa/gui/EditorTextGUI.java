package nl.tue.spa.gui;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;

import nl.tue.spa.controllers.EditorTextController;

public class EditorTextGUI extends RTextScrollPane implements EditorGUI {
	private static final long serialVersionUID = 1L;

	public static EditorGUIType[] allEditorTextGUITypes = {EditorGUIType.TYPE_JAVA_SCRIPT,EditorGUIType.TYPE_GRAPH_SCRIPT,EditorGUIType.TYPE_R_SCRIPT};
	
	EditorTextController controller;

    TextEditorPane textPane;
	
	public EditorTextGUI(EditorTextController controller){
	    textPane = new TextEditorPane();
	    textPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
		this.setViewportView(textPane);
		this.setLineNumbersEnabled(true);
		textPane.addKeyListener(controller);
		this.controller = controller;
	}
	
	public String getScript(){
		return textPane.getText();
	}
	
	public void setScript(String script){
		textPane.setText(script);
		textPane.discardAllEdits();
	}

	public EditorTextController getController() {
		return controller;
	}
}
