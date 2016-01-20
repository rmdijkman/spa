package nl.tue.spa.gui;

import nl.tue.spa.controllers.EditorController;

public interface EditorGUI {

	public static EditorGUIType[] allEditorGUITypes = {EditorGUIType.TYPE_JAVA_SCRIPT,EditorGUIType.TYPE_GRAPH_SCRIPT,EditorGUIType.TYPE_R_SCRIPT,EditorGUIType.TYPE_STREAM};
	public enum EditorGUIType {
		TYPE_JAVA_SCRIPT ("JavaScript","js"), TYPE_GRAPH_SCRIPT ("Graph","gs"), TYPE_R_SCRIPT ("R","R"), TYPE_STREAM ("Stream","stm");
		private String name;
		private String suffix;
		EditorGUIType(String name, String suffix){
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

	public EditorController getController();
	
}
