package nl.tue.spa.controllers;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.Runner;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.core.guistate.GUIStateSerializable;
import nl.tue.spa.gui.EditorContainerGUI;
import nl.tue.spa.gui.EditorGUI.EditorGUIType;

public class EditorContainerController implements GUIStateSerializable{	
	
	EditorContainerGUI gui;
	
	Set<EditorController> editorWindows;
	
	public EditorContainerController(){
		gui = new EditorContainerGUI(this);
		editorWindows = new HashSet<EditorController>();
	}
	
	public void closeWindow() {
		gui.setVisible(false);
		Environment.getMainController().removeWindow(gui);
	}
	
	public void openWindow() {
		Environment.getMainController().addWindow(gui);
		gui.setVisible(true);
	}

	public boolean closeEditors() {
		for (EditorController ec: editorWindows){
			if (!ec.saveOnExit()){
				return false;
			}
		}
		return true;
	}
	
	public EditorTextController getEditorTextController(String fileName){
		for (EditorController ec: editorWindows){
			if ((ec instanceof EditorTextController) && (ec.getFileName().equals(fileName))){
				return (EditorTextController) ec;
			}
		}
		return null;
	}

	public void newJavaScriptFile() {
		EditorController ec = new EditorTextController(EditorGUIType.TYPE_JAVA_SCRIPT);
		editorWindows.add(ec);
		gui.addEditor("new js", ec);
	}

	public void newGraphScriptFile() {
		EditorController ec = new EditorTextController(EditorGUIType.TYPE_GRAPH_SCRIPT);
		editorWindows.add(ec);
		gui.addEditor("new graph", ec);
	}

	public void loadFile() {
		EditorController ec = EditorController.load();
		if (ec != null){
			editorWindows.add(ec);
			String fileName = ec.getFileName();
			gui.addEditor(fileName, ec);
		}
	}

	public void executeScript() {
		EditorController ec = gui.getSelectedEditor();
		if (ec == null) return;
		ec.runScript();
		String fileName = ec.getFileName();
		if (fileName != null){
			gui.setEditorTitle(ec, fileName);
		}
	}

	public void saveScript() {
		EditorController ec = gui.getSelectedEditor();
		if (ec == null) return;
		ec.save(false);
		String fileName = ec.getFileName();
		if (fileName != null){
			gui.setEditorTitle(ec, fileName);
		}
	}

	public void close(EditorController ec) {
		if (ec.saveOnExit()){
			editorWindows.remove(ec);
			gui.removeTab(ec);
		}
	}

	@Override
	public GUIState getState() {
		GUIState gs = new GUIState();
		gs.putStateVar("BOUNDS", gui.getBounds());
		List<GUIState> editors = new ArrayList<GUIState>();
		for (EditorController ec: editorWindows){
			GUIState ecState = ec.getState();
			if (ecState != null){
				editors.add(ecState);
			}
		}
		gs.putStateVar("EDITORS", editors);		
		return gs;
	}

	@Override
	public void restoreState(GUIState state) {
		gui.setBounds((Rectangle) state.getStateVar("BOUNDS"));
		@SuppressWarnings("unchecked")
		List<GUIState> editors = (ArrayList<GUIState>) state.getStateVar("EDITORS");
		for (GUIState editorState: editors){
			EditorController ec = EditorController.restoreStateAbstract(editorState);
			if (ec.getFileName() != null){
				editorWindows.add(ec);
				gui.addEditor(ec.getFileName(), ec);
			}
		}		
	}

	public void executeScriptContinuously() {
		EditorController ec = gui.getSelectedEditor();
		if (ec == null) return;
		ec.runScript();
		String fileName = ec.getFileName();
		if (fileName != null){
			gui.setEditorTitle(ec, fileName);
			Environment.getRunner().addRunningController(fileName);
		}		
	}

	public void stopScript() {
		EditorController ec = gui.getSelectedEditor();
		if (ec == null) return;
		Environment.getRunner().removeRunningController(ec.getFileName());
	}

	public void newStreamFile() {
		EditorController ec = new EditorStreamController(EditorGUIType.TYPE_STREAM);
		editorWindows.add(ec);
		gui.addEditor("new stream", ec);
	}

	public void editorSelected() {
		updateSavedState();
	}
	
	public void updateSavedState(){
		EditorController ec = gui.getSelectedEditor();
		if (ec == null){
			gui.setSaveEnabled(false);
		}else{
			gui.setSaveEnabled(!ec.isSaved());
		}		
	}
}
