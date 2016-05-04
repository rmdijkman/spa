package nl.tue.spa.executor.r;

import org.rosuda.JRI.Rengine;

import nl.tue.spa.core.Environment;
import nl.tue.spa.executor.EvaluationResult;
import nl.tue.spa.executor.java.JavaProcessor;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;

class RCallbacks implements RMainLoopCallbacks {
	RConsole rc = null;

	public void attachConsole(RConsole rc){
		this.rc = rc;
	}
	
	public void detachConsole(){
		this.rc = null;
	}
	
	public void rWriteConsole(Rengine re, String text, int oType) {
		if (rc != null){
			rc.printMessage(text);
		}
	}

	public void rBusy(Rengine re, int which) {
		System.out.println("ERROR: 'rBusy'. This functionality is not implemented");
	}

	public String rReadConsole(Rengine re, String prompt, int addToHistory) {
		return "ERROR: 'rReadConsole'. This functionality is not implemented";
	}

	public void rShowMessage(Rengine re, String message) {
		System.out.println("ERROR: 'rShowMessage'. This functionality is not implemented");
	}

	public String rChooseFile(Rengine re, int newFile) {
		return "ERROR: 'rChooseFile'. This functionality is not implemented";
	}

	public void rFlushConsole(Rengine re) {
		System.out.println("ERROR: 'rFlushConsole'. This functionality is not implemented");
	}

	public void rLoadHistory(Rengine re, String filename) {
		System.out.println("ERROR: 'rLoadHistory'. This functionality is not implemented");
	}

	public void rSaveHistory(Rengine re, String filename) {
		System.out.println("ERROR: 'rSaveHistory'. This functionality is not implemented");
	}
}

public class RProcessor {
	
	private Rengine rEngine;
	private RCallbacks rCallbacks;
	
	private static RProcessor singleton;
	
	private RProcessor(){
		rCallbacks = new RCallbacks();
		rCallbacks.attachConsole(Environment.getConsoleController());
		System.out.println("Creating Rengine (with arguments)");
		rEngine = new Rengine(new String[0], false, rCallbacks);
		if (!Rengine.versionCheck()) {
			System.err.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Rengine created, waiting for R");
		if (!rEngine.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}
		System.out.println(rEngine.eval("install.packages('jsonlite')"));
		System.out.println(rEngine.eval("library('jsonlite')"));
	}
	
	public static RProcessor getRProcessor(){
		if (singleton == null){
			singleton = new RProcessor();
		}
		return singleton;
	}
	
	public String evaluate(String expression) {
		return rEngine.eval(expression).toString();
	}
	
	public void attachConsole(RConsole rc){
		rCallbacks.attachConsole(rc);
	}
	
	public void detachConsole(){
		rCallbacks.detachConsole();		
	}
	
	private Rengine getREngine(){
		return rEngine;
	}

	public static EvaluationResult evaluateScript(String script) {
		Rengine re = getRProcessor().getREngine();
		
		putVariablesInScope(re);
		REXP result = re.eval(script);
		
		return new EvaluationResult();
	}
	
    private static synchronized void putVariablesInScope(Rengine re){
    	String[][] variables = JavaProcessor.getVariables();
    	for (String[] variable: variables){
    		re.eval(variable[0] + "=fromJSON('" + variable[1] + "')");    		
    	}
    }

}