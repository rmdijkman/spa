package nl.tue.spa.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.core.guistate.GUIStateSerializable;
import nl.tue.spa.executor.Script.ScriptType;
import nl.tue.spa.gui.EditorContainerGUI;

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
	
	public EditorController getEditorController(String fileName){
		for (EditorController ec: editorWindows){
			if (ec.getFileName().equals(fileName)){
				return ec;
			}
		}
		return null;
	}

	public void newJavaScriptFile() {
		EditorController ec = new EditorTextController(ScriptType.TYPE_JAVA_SCRIPT);
		editorWindows.add(ec);
		gui.addEditor("new js", ec);
	}

	public void newGraphScriptFile() {
		EditorController ec = new EditorTextController(ScriptType.TYPE_GRAPH_SCRIPT);
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

	public void runScript() {
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

	public void stopScript() {
		EditorController ec = gui.getSelectedEditor();
		if (ec == null) return;
		Environment.getRunner().removePartyToThread(ec.getFileName());
	}

	public void newStreamFile() {
		EditorController ec = new EditorStreamController(ScriptType.TYPE_STREAM);
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

	public void newRScriptFile() {
		EditorController ec = new EditorTextController(ScriptType.TYPE_R_SCRIPT);
		editorWindows.add(ec);
		gui.addEditor("new R", ec);
	}
}
