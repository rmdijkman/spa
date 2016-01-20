package nl.tue.spa.controllers;

import nl.tue.spa.core.Environment;
import nl.tue.spa.gui.BrowserGUI;

public class BrowserController {

	BrowserGUI gui;
	String fileName;
	
	public BrowserController(){
		this.gui = new BrowserGUI(this);
		Environment.getMainController().addWindow(gui);
	}
	
	public void loadContent(String content){
		gui.loadContent(content);
	}

	public void closeWindow() {
		gui.setVisible(false);
	}
	
	public void openWindow() {
		gui.setVisible(true);
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		gui.setTitle("View - " + fileName);
	}
	
	public String getFileName(){
		return fileName;
	}

	public boolean isWindowOpen() {
		return gui.isVisible();
	}
	
	public void executeJavaScript(String script){
		gui.executeJavaScript(script);
	}
}
