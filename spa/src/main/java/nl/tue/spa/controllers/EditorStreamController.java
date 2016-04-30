package nl.tue.spa.controllers;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.executor.Script;
import nl.tue.spa.executor.Script.ScriptType;
import nl.tue.spa.executor.StreamScript;
import nl.tue.spa.gui.EditorGUI;
import nl.tue.spa.gui.EditorStreamGUI;

public class EditorStreamController extends EditorController {

	EditorStreamGUI gui;
	
	public EditorStreamController() {
		this.gui = new EditorStreamGUI(this);
		saved = false;
	}

	public EditorStreamController(ScriptType type) {
		this();
		this.type = type;
	}
	
	public void runScript(){
		askToSaveBeforeExecution();
		if (saved){
			if (Environment.getActiveController().isActive(fileName)){
				Environment.getMainController().showMessageDialog(fileName + " is already active. Stop it before activating it again.", "Activation error", JOptionPane.ERROR_MESSAGE);
				return;
			}		
			Environment.getRunner().addPartyToRun(file);
			Environment.getRunner().addPartyToThread(fileName);
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
			StreamScript script = new StreamScript(file);
			script.setFileToLoad(gui.getSelectedFile());
			script.setCSVFormat(gui.getSelectedCSVFormat());
			script.setDelimiter(gui.getDelimiter());
			script.setHasHeaderRow(gui.hasHeaderRow());
			script.setVariableName(gui.getVariableName());
			script.save();
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
	
	public static EditorStreamController load(String fullPath){
		EditorStreamController ec = new EditorStreamController();
		ec.file = new File(fullPath);
		ec.fileName = ec.file.getName();
		ec.type = Script.getTypeFromFileName(ec.fileName);
		try{
			if (ec.type == null){
				throw new IOException("The extension of the filename is not of a known type.");
			}
			StreamScript script = new StreamScript(ec.file);
			script.load();			
			ec.gui.setSelectedFile(script.getFileToLoad());
			ec.gui.setSelectedCSVFormat(script.getCSVFormat());
			ec.gui.setDelimiter("" + script.getDelimiter());
			ec.gui.setHeaderRow(script.hasHeaderRow());
			ec.gui.setVariableName(script.getVariableName());
		}catch (Exception e){
			Environment.getMainController().showMessageDialog("An error occurred while trying to load the file: " + e.getMessage(), "Load error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		ec.saved = true;
		Environment.getEditorContainerController().updateSavedState();
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
		EditorStreamController ec = load((String) state.getStateVar("FILE"));
		if (ec != null){
			file = ec.file;
			fileName = ec.fileName;
			type = ec.type;
			gui.setSelectedFile(ec.gui.getSelectedFile());
			gui.setSelectedCSVFormat(ec.gui.getSelectedCSVFormat());
			gui.setDelimiter(Character.toString(ec.gui.getDelimiter()));
			gui.setHeaderRow(ec.gui.hasHeaderRow());
			gui.setVariableName(ec.gui.getVariableName());
			saved = ec.saved;
			Environment.getEditorContainerController().updateSavedState();
			refreshFile();
		}
	}

	public void fileChanged() {
		if (saved){
			saved = false;
			Environment.getEditorContainerController().updateSavedState();
		}
	}

	public void refreshFile() {
		StreamScript script = new StreamScript(file);
		script.setFileToLoad(gui.getSelectedFile());
		script.setCSVFormat(gui.getSelectedCSVFormat());
		script.setDelimiter(gui.getDelimiter());
		script.setHasHeaderRow(gui.hasHeaderRow());
		script.setVariableName(gui.getVariableName());
		
		script.loadCSV();
		gui.setHeader(script.getHeader());
		gui.setData(script.loadFirstLinesFromCSV());
	}
}
