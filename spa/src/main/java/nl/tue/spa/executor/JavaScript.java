package nl.tue.spa.executor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import nl.tue.spa.controllers.MainController;
import nl.tue.spa.core.Environment;
import nl.tue.spa.executor.EvaluationResult.ResultType;
import nl.tue.spa.executor.java.JavaProcessor;

public class JavaScript extends Script{

	private File file;
	private String script;	
	private Context context;
	private Scriptable scope;

	@SuppressWarnings("unused")
	private JavaScript(){}
	
	public JavaScript(File file){
		this.file = file;		
	}
	
	public EvaluationResult execute(String line) {
		MainController mc = Environment.getMainController();
		EvaluationResult result = JavaProcessor.evaluateScript(context, scope, line, "");
		if (result.getType() == ResultType.ERROR){
    		Environment.getEventBus().unsubscribe(file.getName());
    		Environment.getRunner().removePartyToThread(file.getName());
    		Environment.getMainController().showMessageDialog(file.getName() + " is active, but an error occurred. Removed it from the list of active parties. Specific error: " + result.getResult(), "Update error", JOptionPane.ERROR_MESSAGE);
		}
		mc.updateJavaScope();
		
		return result;
	}

	public EvaluationResult execute(){
		MainController mc = Environment.getMainController();
		context = JavaProcessor.initializeContext();
		scope = JavaProcessor.initializeScope(context);
		EvaluationResult er = JavaProcessor.evaluateScript(context, scope, script, "");
		if (er.getType() != ResultType.UNDEFINED){
			mc.printEntry("\n");
			mc.printResult(er);
		}
		mc.updateJavaScope();
		return er;
	}

	@Override
	public void load() throws IOException {
		byte[] encoded = Files.readAllBytes(file.toPath());
		script = new String(encoded, StandardCharsets.UTF_8);
	}

	@Override
	public void save() throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(script);
		bw.flush();
		bw.close();
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
}
