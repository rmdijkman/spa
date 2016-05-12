package nl.tue.spa.controllers;

import nl.tue.spa.core.Environment;
import nl.tue.spa.executor.java.JavaProcessor;
import nl.tue.spa.gui.VariablesGUI;

public class VariablesController{

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

	public void clearVariables() {
		JavaProcessor.clearVariablesAndFunctions();
		Environment.getMainController().updateJavaScope();
	}

	public void removeVariable(String variableName) {
		JavaProcessor.removeVariableOrFunction(variableName);
		Environment.getMainController().updateJavaScope();
	}

	public void selectionChanged() {
		gui.setRemoveEnabled(gui.hasSelection());
	}
}
