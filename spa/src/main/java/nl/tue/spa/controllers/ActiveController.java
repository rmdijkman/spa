package nl.tue.spa.controllers;


import nl.tue.spa.core.Environment;
import nl.tue.spa.gui.ActiveGUI;

public class ActiveController{

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
	
	public boolean isActive(String program){
		return gui.getActive(program) != -1;
	}

	public void addActive(String program, ActiveGUI.ActiveType activeType) {
		gui.addActive(program, activeType);
	}

	public void removeActive(String program) {
		gui.removeActive(program);
	}
	
	public void stopActive(String program) {
		Environment.getRunner().removePartyToThread(program);
		Environment.getEventBus().unsubscribe(program);
	}

	public void selectionChanged() {
		gui.setRemoveEnabled(gui.hasSelection());
	}
}
