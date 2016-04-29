package nl.tue.spa.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.executor.EvaluationResult;
import nl.tue.spa.executor.EvaluationResult.ResultType;
import nl.tue.spa.executor.java.JavaProcessor;
import nl.tue.spa.gui.EditorGUI;
import nl.tue.spa.gui.EditorTextGUI;
import nl.tue.spa.gui.EditorGUI.EditorGUIType;

public class EditorTextController extends EditorController implements KeyListener {

	EditorTextGUI gui;
	Context context;
	Scriptable scope;
	
	public EditorTextController() {
		this.gui = new EditorTextGUI(this);
		saved = false;
	}

	public EditorTextController(EditorGUIType type) {
		this();
		this.type = type;
	}
	
	/**
	 * Runs the script that belongs to this controller.
	 * This method should typically called from the user interface.
	 * It primarily handles user interaction related to running the script.
	 * It delegates the actual execution to 'execute' methods.
	 * 
	 * If the script is not saved, asks to save the script. 
	 * If the user does not save the script, aborts.
	 * If the script is already running, presents a warning to the user and aborts.
	 */
	public void runScript(){
		askToSaveBeforeExecution();
		if (saved){
			if (Environment.getActiveController().isActive(fileName)){
				Environment.getMainController().showMessageDialog(fileName + " is already active. Stop it before activating it again.", "Activation error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			switch (type){
			case TYPE_JAVA_SCRIPT:
				runJavaScript();
				break;
			case TYPE_GRAPH_SCRIPT:
				runGraphScript();
				break;
			case TYPE_R_SCRIPT:
				runRScript();
				break;
			default:
				break;
			}
		}
	}
	
	private void runJavaScript() {
		Environment.getRunner().addOnceRunningController(fileName);
	}

	private void runGraphScript() {
		String script = gui.getScript();
		String variables = "<script>\n" + JavaProcessor.getVariablesAsScript() + "</script>\n";
		int headIndex = script.indexOf("<head>");
		if (headIndex != -1){
			script = script.substring(0, headIndex + 6) + variables + script.substring(headIndex + 6);
		}else{
			int bodyIndex = script.indexOf("<body>");
			if (bodyIndex != -1){
				script = script.substring(0, bodyIndex + 6) + variables + script.substring(bodyIndex + 6);
			}else{
				JOptionPane.showMessageDialog(gui, "Cannot create graph, because your graph script does not contain a head or a body tag.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		Environment.getMainController().newOrUpdatedGraph(fileName, script);
	}
		
	private void runRScript() {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (saved){
			saved = false;
			Environment.getEditorContainerController().updateSavedState();
		}
	}
	
	public boolean save(boolean saveAs){
		if (saveAs || (this.fileName == null)){
			boolean selected = selectSaveFile();
			if (!selected){
				return false;
			}
		}
		try{
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(gui.getScript());
			bw.flush();
			bw.close();
		}catch (Exception e){
			file = null;
			fileName = null;
			type = null;
			Environment.getMainController().showMessageDialog("An error occurred while trying to save the file: " + e.getMessage(), "Save error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		saved = true;
		Environment.getEditorContainerController().updateSavedState();
		return true;
	}

	public static EditorTextController load(String fullPath){
		EditorTextController ec = new EditorTextController();
		ec.file = new File(fullPath);
		ec.fileName = ec.file.getName();
		ec.type = ec.getTypeFromFileName();
		try{
			if (ec.type == null){
				throw new IOException("The extension of the filename is not of a known type.");
			}
			byte[] encoded = Files.readAllBytes(ec.file.toPath());
			ec.gui.setScript(new String(encoded, StandardCharsets.UTF_8));
		}catch (Exception e){
			JOptionPane.showMessageDialog(ec.gui, "An error occurred while trying to load the file: " + e.getMessage(), "Load error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		ec.saved = true;
		return ec;		
	}
		
	public EditorGUI getGUI(){
		return gui;
	}

	@Override
	public GUIState getState() {
		return super.getState();
	}

	@Override
	public void restoreState(GUIState state) {
		EditorTextController ec = load((String) state.getStateVar("FILE"));
		if (ec != null){
			file = ec.file;
			fileName = ec.fileName;
			type = ec.type;
			gui.setScript(ec.gui.getScript());
			saved = ec.saved;
			Environment.getEditorContainerController().updateSavedState();
		}
	}

	/**
	 * Executes the given script on the existing context of the script.
	 * Requires that this controller represents a JavaScript.
	 * Requires that a context belonging to this controller already exists.
	 * (This context is produced by calling executeJavaScript().)
	 *  
	 * @param script The script to execute. Must be JavaScript.
	 * @return The result of the evaluation of the script
	 */
	public EvaluationResult executeScript(String script) {
		MainController mc = Environment.getMainController();
		EvaluationResult result = JavaProcessor.evaluateScript(context, scope, script, "");
		mc.updateJavaScope();
		
		return result;
	}
	/**
	 * Executes the entire script that belongs to this controller.
	 * Requires that this controller represents a JavaScript.
	 * Creates a context for the script that belongs to this controller.
	 * 
	 * @return The result of the evaluation of the script that belongs to this controller
	 */
	public EvaluationResult executeScript(){
		MainController mc = Environment.getMainController();
		context = JavaProcessor.initializeContext();
		scope = JavaProcessor.initializeScope(context);
		EvaluationResult er = JavaProcessor.evaluateScript(context, scope, gui.getScript(), "");
		if (er.getType() != ResultType.UNDEFINED){
			mc.printEntry("\n");
			mc.printResult(er);
		}
		mc.updateJavaScope();
		return er;
	}
}
