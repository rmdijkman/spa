package nl.tue.spa.controllers;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.core.guistate.GUIStateSerializable;
import nl.tue.spa.executor.Script;
import nl.tue.spa.executor.Script.ScriptType;
import nl.tue.spa.gui.EditorGUI;
import nl.tue.spa.gui.EditorStreamGUI;
import nl.tue.spa.gui.EditorTextGUI;

public abstract class EditorController implements GUIStateSerializable {

	String fileName;
	File file;
	ScriptType type;
	boolean saved;
	
	public String getFileName(){
		return fileName;
	}

	public boolean isSaved(){
		return saved;
	}	

	/**
	 * Checks whether the file was saved. If not, asks the user whether he wants to save.
	 * If the user wants to save, saves the file.
	 * Returns true if the user does not want to save, or if the user wants to save and save was successful.
	 * Returns false if the user wants to cancel, or if the user wants to save and save was unsuccessful.
	 * 
	 * @return true, if and only if the program should continue with a hypothetical quit action.
	 */
	public boolean saveOnExit(){
		if (!saved){
			int answer = Environment.getMainController().showDialog("Your script has not been saved. Do you want to save now?", "Notification", JOptionPane.YES_NO_CANCEL_OPTION); 					
			if (answer == JOptionPane.YES_OPTION){
				return save(false);
			}else if (answer == JOptionPane.CANCEL_OPTION){
				return false;
			}
		}
		return true;
	}

	public boolean selectSaveFile(){
		final JFileChooser fc = new JFileChooser();
		String lastFolder = Environment.getProperties().getLastFolder();
		if (lastFolder != null){
			fc.setCurrentDirectory(new File(lastFolder));
		}
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(type.getName() + " (." + type.getSuffix() + ")", type.getSuffix());
		fc.addChoosableFileFilter(filter);
		int returnVal = Environment.getMainController().showDialog(fc, "Save");
		if (returnVal == JFileChooser.APPROVE_OPTION){
			file = fc.getSelectedFile();
			fileName = file.getName();
			if (!fileName.endsWith("." + type.getSuffix())){
				file = new File(fc.getSelectedFile().getAbsolutePath() + "." + type.getSuffix());
				fileName = file.getName();
			}
			Environment.getProperties().setLastFolder(file.getParent());
			return true;
		}
		return false;		
	}

	public static EditorController load(){
		final JFileChooser fc = new JFileChooser();
		String lastFolder = Environment.getProperties().getLastFolder();
		if (lastFolder != null){
			fc.setCurrentDirectory(new File(lastFolder));
		}
		for (ScriptType type: Script.allScriptTypes){
			FileNameExtensionFilter filter = new FileNameExtensionFilter(type.getName() + " (." + type.getSuffix() + ")", type.getSuffix());
			fc.addChoosableFileFilter(filter);
		}
		int returnVal = Environment.getMainController().showDialog(fc, "Load");
		if (returnVal == JFileChooser.APPROVE_OPTION){
			Environment.getProperties().setLastFolder(fc.getSelectedFile().getParent());
			return load(fc.getSelectedFile().getPath());
		}
		return null;
	}

	public static EditorController load(String fullPath){
		for (ScriptType type: EditorTextGUI.allEditorTextGUITypes){
			if (fullPath.endsWith("." + type.getSuffix())){
				return EditorTextController.load(fullPath);
			}
		}
		for (ScriptType type: EditorStreamGUI.allEditorStreamGUITypes){
			if (fullPath.endsWith("." + type.getSuffix())){
				return EditorStreamController.load(fullPath);
			}
		}
		return null;
	}
	
	public static EditorController restoreStateAbstract(GUIState state){
		String fullPath = (String) state.getStateVar("FILE");
		for (ScriptType type: EditorTextGUI.allEditorTextGUITypes){
			if (fullPath.endsWith("." + type.getSuffix())){
				EditorTextController etc = new EditorTextController();
				etc.restoreState(state);
				return etc;
			}
		}
		for (ScriptType type: EditorStreamGUI.allEditorStreamGUITypes){
			if (fullPath.endsWith("." + type.getSuffix())){
				EditorStreamController esc = new EditorStreamController();
				esc.restoreState(state);
				return esc;
			}
		}
		return null;
	}

	@Override
	public GUIState getState() {
		if (file == null){
			return null;
		}else{
			GUIState gs = new GUIState();
			gs.putStateVar("FILE", file.getAbsolutePath());
			return gs;
		}
	}
	
	/**
	 * If the script that belongs to this controller is not saved, asks the user to save the script.
	 * If the user wants to save, opens the save dialog and handles saving.
	 */
	public void askToSaveBeforeExecution(){
		if (!saved){
			int answer = Environment.getMainController().showDialog("Your script must be saved before it can be executed. Do you want to save now?", "Notification", JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION){
				return;
			}
		}
		save(false);
	}
	
	public abstract boolean save(boolean saveAs);

	public abstract EditorGUI getGUI();
	
	public abstract void runScript();	
}
