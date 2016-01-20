package nl.tue.spa.controllers;

import java.awt.Rectangle;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.core.guistate.GUIStateSerializable;
import nl.tue.spa.executor.java.JavaProcessor;
import nl.tue.spa.gui.VariablesGUI;

public class VariablesController implements GUIStateSerializable{

	VariablesGUI gui;
	
	public VariablesController() {
		this.gui = new VariablesGUI(this);
	}

	public void setVariable(String name, String value){
		gui.setVariable(name, value);
	}
	
	public void clear(){
		gui.clear();
	}

	public void closeWindow() {
		gui.setVisible(false);
		Environment.getMainController().removeWindow(gui);
	}
	
	public void openWindow() {
		Environment.getMainController().addWindow(gui);
		gui.setVisible(true);
	}

	@Override
	public GUIState getState() {
		GUIState gs = new GUIState();
		gs.putStateVar("BOUNDS", gui.getBounds());
		return gs;
	}

	@Override
	public void restoreState(GUIState state) {
		gui.setBounds((Rectangle) state.getStateVar("BOUNDS"));
	}

	public void clearVariables() {
		JavaProcessor.clearVariables();
		Environment.getMainController().updateJavaScope();
	}

	public void removeVariable(String variableName) {
		JavaProcessor.removeVariable(variableName);
		Environment.getMainController().updateJavaScope();
	}

	public void selectionChanged() {
		gui.setRemoveEnabled(gui.hasSelection());
	}
}
