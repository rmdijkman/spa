package nl.tue.spa.executor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import nl.tue.spa.controllers.MainController;
import nl.tue.spa.core.Environment;
import nl.tue.spa.executor.EvaluationResult.ResultType;
import nl.tue.spa.executor.r.RProcessor;

public class RScript extends Script{

	private File file;
	private String script;	

	@SuppressWarnings("unused")
	private RScript(){}
	
	public RScript(File file){
		this.file = file;		
	}
	
	public EvaluationResult execute(String line) {
		return null;
	}

	public EvaluationResult execute(){
		MainController mc = Environment.getMainController();
		EvaluationResult er = RProcessor.evaluateScript(script);
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
