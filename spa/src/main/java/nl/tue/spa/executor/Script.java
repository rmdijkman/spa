package nl.tue.spa.executor;

import java.io.File;
import java.io.IOException;

public abstract class Script {

	public static ScriptType[] allScriptTypes = {ScriptType.TYPE_JAVA_SCRIPT,ScriptType.TYPE_GRAPH_SCRIPT,ScriptType.TYPE_R_SCRIPT,ScriptType.TYPE_STREAM};
	
	public enum ScriptType {
		TYPE_JAVA_SCRIPT ("JavaScript","js"), TYPE_GRAPH_SCRIPT ("Graph","gs"), TYPE_R_SCRIPT ("R","R"), TYPE_STREAM ("Stream","stm");
		private String name;
		private String suffix;
		ScriptType(String name, String suffix){
			this.name = name;
			this.suffix = suffix;
		}
		public String getName(){
			return name;
		}
		public String getSuffix(){
			return suffix;
		}
	};	

	boolean isDirty;
	
	public static ScriptType getTypeFromFileName(String fileName){
		for (ScriptType type: Script.allScriptTypes){
			if (fileName.endsWith("." + type.getSuffix())){
				return type;
			}
		}
		return null;
	}

	public boolean isDirty() {
		return isDirty;
	}
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
	
	public abstract void load() throws IOException;
	
	public abstract void save() throws IOException;

	public static Script initialize(File file) throws IOException{
		switch (getTypeFromFileName(file.getName())){
		case TYPE_JAVA_SCRIPT:
			Script javaScript = new JavaScript(file);
			return javaScript;
		case TYPE_GRAPH_SCRIPT:
			Script graphScript = new GraphScript(file);
			return graphScript;
		case TYPE_R_SCRIPT:
			break;
		default:
			break;
		}
		return null;
	}
	
	/**
	 * Executes the entire script.
	 * Creates a context for the script.
	 * 
	 * @return The result of the evaluation of the script
	 */
	public abstract EvaluationResult execute();
	
	/**
	 * Executes the given line on the existing context of the script.
	 * Requires that a context belonging to this controller already exists.
	 * (This context is produced by calling execute().)
	 *  
	 * @param line The line to execute
	 * @return The result of the evaluation of the script
	 */
	public abstract EvaluationResult execute(String line);
}
