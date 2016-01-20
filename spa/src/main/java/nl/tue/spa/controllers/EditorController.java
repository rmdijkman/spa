package nl.tue.spa.controllers;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.core.guistate.GUIStateSerializable;
import nl.tue.spa.gui.EditorTextGUI;
import nl.tue.spa.gui.EditorGUI;
import nl.tue.spa.gui.EditorGUI.EditorGUIType;
import nl.tue.spa.gui.EditorStreamGUI;

public abstract class EditorController implements GUIStateSerializable {

	String fileName;
	File file;
	EditorGUIType type;
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
			return true;
		}
		return false;		
	}

	public static EditorController load(){
		final JFileChooser fc = new JFileChooser();
		for (EditorGUIType type: EditorGUI.allEditorGUITypes){
			FileNameExtensionFilter filter = new FileNameExtensionFilter(type.getName() + " (." + type.getSuffix() + ")", type.getSuffix());
			fc.addChoosableFileFilter(filter);
		}
		int returnVal = Environment.getMainController().showDialog(fc, "Load");
		if (returnVal == JFileChooser.APPROVE_OPTION){
			return load(fc.getSelectedFile().getPath());
		}
		return null;
	}

	public static EditorController load(String fullPath){
		for (EditorGUIType type: EditorTextGUI.allEditorTextGUITypes){
			if (fullPath.endsWith("." + type.getSuffix())){
				return EditorTextController.load(fullPath);
			}
		}
		for (EditorGUIType type: EditorStreamGUI.allEditorStreamGUITypes){
			if (fullPath.endsWith("." + type.getSuffix())){
				return EditorStreamController.load(fullPath);
			}
		}
		return null;
	}
	
	public static EditorController restoreStateAbstract(GUIState state){
		String fullPath = (String) state.getStateVar("FILE");
		for (EditorGUIType type: EditorTextGUI.allEditorTextGUITypes){
			if (fullPath.endsWith("." + type.getSuffix())){
				EditorTextController etc = new EditorTextController();
				etc.restoreState(state);
				return etc;
			}
		}
		for (EditorGUIType type: EditorStreamGUI.allEditorStreamGUITypes){
			if (fullPath.endsWith("." + type.getSuffix())){
				EditorStreamController esc = new EditorStreamController();
				esc.restoreState(state);
				return esc;
			}
		}
		return null;
	}

	public EditorGUIType getTypeFromFileName(){
		for (EditorGUIType type: EditorGUI.allEditorGUITypes){
			if (fileName.endsWith("." + type.getSuffix())){
				return type;
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
	
	public void runScript(){
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
}
