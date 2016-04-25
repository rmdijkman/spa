package nl.tue.spa.controllers;

import java.awt.Rectangle;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.Runner;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.core.guistate.GUIStateSerializable;
import nl.tue.spa.gui.ActiveGUI;

public class ActiveController implements GUIStateSerializable{

	ActiveGUI gui;
	
	public ActiveController() {
		this.gui = new ActiveGUI(this);
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

	public void addActive(String program) {
		gui.addActive(program);
	}

	public void removeActive(String program) {
		gui.removeActive(program);
	}
	
	public void stopActive(String program) {
		Environment.getRunner().removeRunningController(program);
	}

	public void selectionChanged() {
		gui.setRemoveEnabled(gui.hasSelection());
	}
}
