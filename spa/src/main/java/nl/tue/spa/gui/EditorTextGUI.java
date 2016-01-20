package nl.tue.spa.gui;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;

import nl.tue.spa.controllers.EditorTextController;
import nl.tue.spa.util.TextLineNumber;

public class EditorTextGUI extends JScrollPane implements EditorGUI {
	private static final long serialVersionUID = 1L;

	public static EditorGUIType[] allEditorTextGUITypes = {EditorGUIType.TYPE_JAVA_SCRIPT,EditorGUIType.TYPE_GRAPH_SCRIPT,EditorGUIType.TYPE_R_SCRIPT};
	
	EditorTextController controller;
	
	private JTextPane textPane;
	private UndoManager undoManager;
	
	public EditorTextGUI(EditorTextController controller){
		textPane = new JTextPane();
		this.getViewport().add(textPane);
		TextLineNumber textLineNumber = new TextLineNumber(textPane);
		this.setRowHeaderView(textLineNumber);
		textPane.addKeyListener(controller);
		undoManager = new UndoManager();
		textPane.getDocument().addUndoableEditListener(undoManager);
		this.controller = controller;
	}
	
	public String getScript(){
		try {
			return textPane.getDocument().getText(0, textPane.getDocument().getLength());
		} catch (BadLocationException e) {
			return null;
		}
	}
	
	public void setScript(String script){
		try {
			textPane.getDocument().insertString(0, script, null);
			undoManager.discardAllEdits();
		} catch (BadLocationException e) {
		}
	}

	public String getSelectedText(){
		return textPane.getSelectedText();
	}

	public void setCaretPosition(int position) {
		textPane.setCaretPosition(position);
	}

	public void insertText(int caretPosition, String text) {
		try {
			textPane.getDocument().insertString(caretPosition, text, null);
		} catch (BadLocationException e) {
		}
	}

	public int getCaretPosition() {
		return textPane.getCaretPosition();
	}

	public EditorTextController getController() {
		return controller;
	}
	
	public void undo(){
		if (undoManager.canUndo()){
			undoManager.undo();
		}
	}
	
	public void redo(){
		if (undoManager.canRedo()){
			undoManager.redo();
		}
	}
}
