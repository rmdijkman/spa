package nl.tue.spa.executor.r;

import org.rosuda.JRI.Rengine;
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
	
	Rengine re;
	RCallbacks rCallbacks;
	
	public RProcessor(){
		rCallbacks = new RCallbacks();
		re = new Rengine(new String[0], false, rCallbacks);
		if (!Rengine.versionCheck()) {
			System.err.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Creating Rengine (with arguments)");
		re = new Rengine();
		System.out.println("Rengine created, waiting for R");
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}		
	}
	
	public String evaluate(String expression) {
		return re.eval(expression).toString();
	}
	
	public void attachConsole(RConsole rc){
		rCallbacks.attachConsole(rc);
	}
	
	public void detachConsole(){
		rCallbacks.detachConsole();		
	}
}