package nl.tue.spa.gui;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;

import nl.tue.spa.controllers.EditorTextController;
import nl.tue.spa.executor.Script.ScriptType;

public class EditorTextGUI extends RTextScrollPane implements EditorGUI {
	private static final long serialVersionUID = 1L;

	public static ScriptType[] allEditorTextGUITypes = {ScriptType.TYPE_JAVA_SCRIPT,ScriptType.TYPE_GRAPH_SCRIPT,ScriptType.TYPE_R_SCRIPT};
	
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
