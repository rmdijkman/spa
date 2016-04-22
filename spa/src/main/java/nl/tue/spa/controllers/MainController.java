package nl.tue.spa.controllers;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.Main;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.core.guistate.GUIStateSerializable;
import nl.tue.spa.executor.EvaluationResult;
import nl.tue.spa.executor.java.JavaProcessor;
import nl.tue.spa.gui.MainGUI;

public class MainController implements GUIStateSerializable{
	
	private MainGUI guiMain;
	private Map<String, BrowserController> graphWindows;
	
	private boolean consoleVisible;
	private boolean variablesVisible;
	private boolean editorVisible;
	private boolean activeVisible;

	public MainController() {
		this.guiMain = new MainGUI();
	}

	public void makeVisible(){
		guiMain.setVisible(true);
		graphWindows = new HashMap<String, BrowserController>();
	}
		
	public void closeConsole() {
		ConsoleController cc = Environment.getConsoleController();
		cc.closeWindow();
		guiMain.setMenuConsoleSelected(false);
		consoleVisible = false;
	}

	public void openConsole() {
		ConsoleController cc = Environment.getConsoleController();
		cc.openWindow();
		guiMain.setMenuConsoleSelected(true);
		consoleVisible = true;
	}

	public void newJavaScriptFile() {
		Environment.getEditorContainerController().newJavaScriptFile();
	}

	public void openActiveWindow() {
		ActiveController ac = Environment.getActiveController();
		ac.openWindow();
		guiMain.setMenuActiveSelected(true);
		activeVisible = true;
	}

	public void closeActiveWindow() {
		ActiveController ac = Environment.getActiveController();
		ac.closeWindow();
		guiMain.setMenuActiveSelected(false);
		activeVisible = false;
	}

	public void openVariablesWindow() {
		VariablesController vc = Environment.getVariablesController();
		vc.openWindow();
		guiMain.setMenuVariablesSelected(true);
		variablesVisible = true;
	}

	public void closeVariablesWindow() {
		VariablesController vc = Environment.getVariablesController();
		vc.closeWindow();
		guiMain.setMenuVariablesSelected(false);
		variablesVisible = false;
	}

	public void updateJavaScope() {
		VariablesController vc = Environment.getVariablesController();
		String variables[][] = JavaProcessor.getVariables();		
		vc.clear();
		for (String variable[]: variables){
			vc.setVariable(variable[0], variable[1]);
		}
	}

	public void printEntry(String entry) {
		Environment.getConsoleController().printEntry(entry);
	}

	public void printResult(EvaluationResult er) {
		Environment.getConsoleController().printResult(er);		
	}

	public void newGraphScriptFile() {
		Environment.getEditorContainerController().newGraphScriptFile();
	}

	public void newOrUpdatedGraph(String fileName, String script) {
		BrowserController bc = graphWindows.get(fileName);
		if (bc == null){
			bc = new BrowserController();
			bc.setFileName(fileName);
			graphWindows.put(fileName, bc);
		}
		if (!bc.isWindowOpen()){
			bc.openWindow();
		}
		bc.loadContent(script);
	}	
	
	public boolean executeJavaScriptOnParty(String fileName, String script){
		BrowserController bc = graphWindows.get(fileName);
		if (bc != null){
			bc.executeJavaScript(script);
			return true;
		}
		EditorTextController etc = Environment.getEditorContainerController().getEditorTextController(fileName);
		if (etc != null){
			etc.executeJavaScript(script);
			return true;
		}
		return false;
	}
	
	public void closeGraph(BrowserController gc){
		gc.closeWindow();
	}
	
	public void addWindow(JInternalFrame window){
		guiMain.getDesktopPane().add(window);
	}

	public void removeWindow(JInternalFrame window){
		guiMain.getDesktopPane().remove(window);
	}

	public void loadFile() {
		Environment.getEditorContainerController().loadFile();
	}

	public void closeProgram() {
		boolean closeCanContinue = Environment.getEditorContainerController().closeEditors();
		if (closeCanContinue){
			Main.saveState();;
		
			System.exit(0);
		}
	}
	
	public GUIState getState(){
		GUIState gs = new GUIState();
		gs.putStateVar("BOUNDS", guiMain.getBounds());
		gs.putStateVar("WINDOW_STATE", guiMain.getExtendedState());		
		gs.putStateVar("CONSOLE_VISIBLE", consoleVisible);
		gs.putStateVar("CONSOLE", Environment.getConsoleController().getState());
		gs.putStateVar("VARIABLES_VISIBLE", variablesVisible);
		gs.putStateVar("VARIABLES", Environment.getVariablesController().getState());
		gs.putStateVar("VARIABLE_VALUES", JavaProcessor.getVariablesAsScript());
		gs.putStateVar("EDITOR_VISIBLE", editorVisible);
		gs.putStateVar("EDITOR", Environment.getEditorContainerController().getState());
		gs.putStateVar("ACTIVE_VISIBLE", activeVisible);
		gs.putStateVar("ACTIVE", Environment.getActiveController().getState());
		return gs;
	}
	
	public void restoreState(GUIState state){
		guiMain.setBounds((Rectangle) state.getStateVar("BOUNDS"));
		guiMain.setExtendedState((int) state.getStateVar("WINDOW_STATE"));
		Environment.getConsoleController().restoreState((GUIState) state.getStateVar("CONSOLE")); 
		if ((Boolean) state.getStateVar("CONSOLE_VISIBLE")){
			openConsole();
		}
		Environment.getVariablesController().restoreState((GUIState) state.getStateVar("VARIABLES")); 
		if ((Boolean) state.getStateVar("VARIABLES_VISIBLE")){
			openVariablesWindow();
		}
		Context cx = JavaProcessor.initializeContext();
		Scriptable scope = JavaProcessor.initializeScope(cx);
		JavaProcessor.evaluateScript(cx, scope, (String) state.getStateVar("VARIABLE_VALUES"), "");
		Context.exit();
		updateJavaScope();
		Environment.getEditorContainerController().restoreState((GUIState) state.getStateVar("EDITOR")); 
		if ((Boolean) state.getStateVar("EDITOR_VISIBLE")){
			openEditorWindow();
		}
		Environment.getActiveController().restoreState((GUIState) state.getStateVar("ACTIVE")); 
		if ((Boolean) state.getStateVar("ACTIVE_VISIBLE")){
			openActiveWindow();
		}
	}

	public void closeEditorWindow() {
		EditorContainerController ecc = Environment.getEditorContainerController();
		ecc.closeWindow();
		guiMain.setMenuEditorSelected(false);
		editorVisible = false;
	}

	public void openEditorWindow() {
		EditorContainerController ecc = Environment.getEditorContainerController();
		ecc.openWindow();
		guiMain.setMenuEditorSelected(true);
		editorVisible = true;
	}

	public int showDialog(JFileChooser fc, String approveButtonText) {		
		return fc.showDialog(guiMain, approveButtonText);
	}

	public int showDialog(String text, String title, int yesNoCancelOption) {
		return JOptionPane.showConfirmDialog(guiMain, text, title, yesNoCancelOption);
	}

	public void newStreamFile() {
		Environment.getEditorContainerController().newStreamFile();
	}

	public void showMessageDialog(String text, String title, int messageType) {
		JOptionPane.showMessageDialog(guiMain, text, title, messageType);		
	}
}