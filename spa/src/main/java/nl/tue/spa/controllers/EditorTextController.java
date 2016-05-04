package nl.tue.spa.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.executor.GraphScript;
import nl.tue.spa.executor.JavaScript;
import nl.tue.spa.executor.RScript;
import nl.tue.spa.executor.Script;
import nl.tue.spa.executor.Script.ScriptType;
import nl.tue.spa.gui.EditorGUI;
import nl.tue.spa.gui.EditorTextGUI;

public class EditorTextController extends EditorController implements KeyListener {

	EditorTextGUI gui;
	
	public EditorTextController() {
		this.gui = new EditorTextGUI(this);
		saved = false;		
	}

	public EditorTextController(ScriptType type) {
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
				Environment.getRunner().addPartyToRun(file);
				break;
			case TYPE_GRAPH_SCRIPT:
				Environment.getRunner().addPartyToRun(file);
				break;
			case TYPE_R_SCRIPT:
				Environment.getRunner().addPartyToRun(file);
				break;
			default:
				break;
			}
		}
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
			switch (type){
			case TYPE_JAVA_SCRIPT:
				JavaScript javaScript = new JavaScript(file);
				javaScript.setScript(gui.getScript());
				javaScript.save();
				break;
			case TYPE_GRAPH_SCRIPT:
				GraphScript graphScript = new GraphScript(file);
				graphScript.setScript(gui.getScript());
				graphScript.save();
				break;
			case TYPE_R_SCRIPT:
				RScript rScript = new RScript(file);
				rScript.setScript(gui.getScript());
				rScript.save();
				break;
			default:
				break;
			}
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
		ec.type = Script.getTypeFromFileName(ec.fileName);
		try{
			if (ec.type == null){
				throw new IOException("The extension of the filename is not of a known type.");
			}
			switch (ec.type){
			case TYPE_JAVA_SCRIPT:
				JavaScript javaScript = new JavaScript(ec.file);
				javaScript.load();
				ec.gui.setScript(javaScript.getScript());
				break;
			case TYPE_GRAPH_SCRIPT:
				GraphScript graphScript = new GraphScript(ec.file);
				graphScript.load();
				ec.gui.setScript(graphScript.getScript());
				break;
			case TYPE_R_SCRIPT:
				RScript rScript = new RScript(ec.file);
				rScript.load();
				ec.gui.setScript(rScript.getScript());
				break;
			default:
				break;
			}
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
}
