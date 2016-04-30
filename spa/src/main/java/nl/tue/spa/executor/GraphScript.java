package nl.tue.spa.executor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import nl.tue.spa.controllers.BrowserController;
import nl.tue.spa.core.Environment;
import nl.tue.spa.executor.java.JavaProcessor;

public class GraphScript extends Script{

	File file;
	String script;
	
	@SuppressWarnings("unused")
	private GraphScript(){}
	
	public GraphScript(File file){
		this.file = file;
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

	@Override
	public EvaluationResult execute() {
		String variables = "<script>\n" + JavaProcessor.getVariablesAsScript() + "</script>\n";
		int headIndex = script.indexOf("<head>");
		if (headIndex != -1){
			script = script.substring(0, headIndex + 6) + variables + script.substring(headIndex + 6);
		}else{
			int bodyIndex = script.indexOf("<body>");
			if (bodyIndex != -1){
				script = script.substring(0, bodyIndex + 6) + variables + script.substring(bodyIndex + 6);
			}else{
				//TODO JOptionPane.showMessageDialog(gui, "Cannot create graph, because your graph script does not contain a head or a body tag.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		Environment.getMainController().newOrUpdatedGraph(file.getName(), script);
		return new EvaluationResult();
	}

	@Override
	public EvaluationResult execute(String line) {
		BrowserController bc = Environment.getMainController().getGraphWindow(file.getName());
		if (bc != null){
			bc.executeScript(line);
		}		
		return new EvaluationResult();
	}

}
