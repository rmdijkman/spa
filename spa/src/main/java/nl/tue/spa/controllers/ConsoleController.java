package nl.tue.spa.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import nl.tue.spa.core.AppClipboard;
import nl.tue.spa.core.Environment;
import nl.tue.spa.executor.EvaluationResult;
import nl.tue.spa.executor.EvaluationResult.ResultType;
import nl.tue.spa.executor.java.JavaProcessor;
import nl.tue.spa.gui.ConsoleGUI;

public class ConsoleController implements KeyListener {

	ConsoleGUI gui;
	
	Context context;
	Scriptable scope;
	
	int caretPosition;
	
	List<String> history = new ArrayList<String>();
	int historyPosition = -1;
	
	public ConsoleController() {
		this.gui = new ConsoleGUI(this);
		gui.printPrompt();
		caretPosition = gui.getCaretPosition();
		checkState();
		context = JavaProcessor.initializeContext();
		scope = JavaProcessor.initializeScope(context);
	}
	
	public void processKey(int keyCode) {
		if (keyCode == KeyEvent.VK_ENTER){
			String command = gui.getText(caretPosition, gui.getTextLength() - caretPosition);
			history.add(command);
			historyPosition = history.size();
			evaluate(command, "", 0);
		}else if (keyCode == KeyEvent.VK_HOME){
			gui.setCaretPosition(caretPosition);
		}else if (keyCode == KeyEvent.VK_UP){
			if (history.size() > 0){
				if (historyPosition > 0){
					historyPosition--;
				}
				gui.removeText(caretPosition, gui.getTextLength() - caretPosition);
				gui.printEntry(history.get(historyPosition));
			}
		}else if (keyCode == KeyEvent.VK_DOWN){
			if (history.size() > 0){
				if (historyPosition < history.size()){
					historyPosition++;
				}
				gui.removeText(caretPosition, gui.getTextLength() - caretPosition);
				if (historyPosition == history.size()){
					gui.printEntry("");
				}else{
					gui.printEntry(history.get(historyPosition));
				}
			}			
		}
		checkState();
	}

	public void keyTyped(KeyEvent e) {
		checkState();
	}

	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() != KeyEvent.VK_CONTROL) && (e.getKeyCode() != KeyEvent.VK_META) &&
				!((e.getKeyCode() == KeyEvent.VK_C) && (e.isControlDown() || e.isMetaDown()))
           ){
				checkState();
		}
		if (Environment.isMac()){ //For Mac OS, we must implement CMD-C, CMD-V
			//If the key-combi is ctrl-c, copy the selected text
			if ((e.getKeyCode() == KeyEvent.VK_C) && (e.isControlDown() || e.isMetaDown())){
				String selectedText = gui.getSelectedText();
				if (selectedText != null){
					AppClipboard.toClipboard(selectedText);
				}
			}
			//If the key-combi is ctrl-v, paste the selected text
			if ((e.getKeyCode() == KeyEvent.VK_V) && (e.isControlDown() || e.isMetaDown())){
				String selectedText = AppClipboard.fromClipboard();
				gui.insertText(gui.getCaretPosition(), selectedText);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		checkState();
	}

	private void checkState(){
		if (gui.getCaretPosition() > caretPosition){
			gui.enableMoveLeft();
		}else{
			gui.setCaretPosition(caretPosition);
			gui.disableMoveLeft();
		}
	}
	
	public EvaluationResult evaluate(String command, String source, int lineNo){
		gui.printEntry("\n");
		EvaluationResult er = JavaProcessor.evaluate(context, scope, command, source, lineNo);
		printResult(er);
		Environment.getMainController().updateJavaScope();
		return er;
	}

	public void printEntry(String entry) {
		gui.printEntry(entry);
	}

	public void printResult(EvaluationResult er) {
		if (er.getType() == ResultType.RESULT){
			gui.printMessage(er.getResult());
			gui.printEntry("\n");
		}else if (er.getType() == ResultType.ERROR){
			gui.printError(er.getResult());
			gui.printEntry("\n");
		}
		gui.printPrompt();
		caretPosition = gui.getCaretPosition();
	}

	public void openWindow() {
		Environment.getMainController().addWindow(gui);
		gui.setVisible(true);
	}
	
	public void closeWindow() {
		gui.setVisible(false);
		Environment.getMainController().removeWindow(gui);
	}
	
	public void log(String text){
		gui.printError("\n" + text + "\n");
		gui.printPrompt();
		gui.setCaretPosition(gui.getTextLength());
		caretPosition = gui.getCaretPosition();
	}
}
