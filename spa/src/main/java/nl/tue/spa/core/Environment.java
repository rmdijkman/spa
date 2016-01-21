package nl.tue.spa.core;

import nl.tue.spa.controllers.ActiveController;
import nl.tue.spa.controllers.ConsoleController;
import nl.tue.spa.controllers.EditorContainerController;
import nl.tue.spa.controllers.MainController;
import nl.tue.spa.controllers.VariablesController;
import nl.tue.spa.executor.r.RProcessor;

public class Environment {

	public static final transient String STATE_FILE = ".spastate.ser"; 

	private static RProcessor rProcessor;
	private static MainController mainController;
	private static ConsoleController consoleController;
	private static VariablesController variablesContoller;
	private static ActiveController activeContoller;	
	private static EditorContainerController editorContoller;
	private static EventBus eventBus;
	private static Properties properties;
	
	public static RProcessor getRProcessor(){
		if (rProcessor == null){
			rProcessor = new RProcessor();			
		}
		return rProcessor;
	}

	public static MainController getMainController(){
		if (mainController == null){
			mainController = new MainController();
		}
		return mainController;
	}

	public static ConsoleController getConsoleController(){
		if (consoleController == null){
			consoleController = new ConsoleController();
		}
		return consoleController;
	}


	public static VariablesController getVariablesController() {
		if (variablesContoller == null){
			variablesContoller = new VariablesController();
		}
		return variablesContoller;
	}

	public static ActiveController getActiveController() {
		if (activeContoller == null){
			activeContoller = new ActiveController();
		}
		return activeContoller;
	}
	
	public static EditorContainerController getEditorContainerController() {
		if (editorContoller == null){
			editorContoller = new EditorContainerController();
		}
		return editorContoller;
	}
	
	public static EventBus getEventBus(){
		if (eventBus == null){
			eventBus = new EventBus();
		}
		return eventBus;
	}

	public static Properties getProperties(){
		if (properties == null){
			properties = new Properties();
		}
		return properties;
	}

	public static boolean isMac(){
		return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
	}
}
