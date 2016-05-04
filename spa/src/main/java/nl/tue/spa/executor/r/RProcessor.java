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
		String args[] = {"--no-save"};
		rEngine = new Rengine(args, false, rCallbacks);
		if (!Rengine.versionCheck()) {
			System.err.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Rengine created, waiting for R");
		if (!rEngine.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}
		rEngine.eval("library('jsonlite')");
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
		re.eval(script);
		getVariablesFromScope(re);
		
		return new EvaluationResult();
	}
	
    private static synchronized void putVariablesInScope(Rengine re){
    	String[][] variables = JavaProcessor.getVariables();
    	for (String[] variable: variables){
    		re.eval(variable[0] + "=fromJSON('" + variable[1] + "')");    		
    	}
    }
    
    private static synchronized void getVariablesFromScope(Rengine re){
    	REXP variableExp = re.eval("ls()");
    	String variables[] = variableExp.asStringArray();
    	String result[][] = new String[variables.length][2];

    	for (int i = 0; i < variables.length; i++){
    		result[i][0] = variables[i];
    		result[i][1] = re.eval("toJSON("+variables[i]+",auto_unbox = TRUE)").asString();
    	}
    	
    	JavaProcessor.setVariables(result);
    }

}