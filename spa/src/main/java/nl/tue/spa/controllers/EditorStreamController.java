package nl.tue.spa.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import nl.tue.spa.core.Environment;
import nl.tue.spa.core.guistate.GUIState;
import nl.tue.spa.executor.EvaluationResult;
import nl.tue.spa.executor.EvaluationResult.ResultType;
import nl.tue.spa.executor.java.JavaProcessor;
import nl.tue.spa.gui.EditorGUI;
import nl.tue.spa.gui.EditorStreamGUI;
import nl.tue.spa.gui.EditorGUI.EditorGUIType;

public class EditorStreamController extends EditorController {

	EditorStreamGUI gui;
	String fileToLoad;
	String header[];
	Iterator<CSVRecord> fileLoader;
	
	public EditorStreamController() {
		this.gui = new EditorStreamGUI(this);
		saved = false;
	}

	public EditorStreamController(EditorGUIType type) {
		this();
		this.type = type;
	}
	
	public void runScript(){
		super.runScript();
		if (!saved) return;
		if (gui.getVariableName().length() == 0) return;
		
		if (fileLoader == null){
			try {
				File f = new File(fileToLoad);
				loadHeaderFromCSV(f);
				CSVFormat csvFormat = gui.getSelectedCSVFormat();
				csvFormat = csvFormat.withDelimiter(gui.getDelimiter());
				CSVParser parser;
				parser = CSVParser.parse(f, Charset.defaultCharset(), csvFormat);
				fileLoader = parser.iterator();
				if (gui.hasHeaderRow() && fileLoader.hasNext()){
					fileLoader.next();
				}
				JavaProcessor.evaluateScript(gui.getVariableName() + "=[]", "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String script = "";
		if (fileLoader.hasNext()){
			script = gui.getVariableName() + ".push(" + recordToString(fileLoader.next()) + ")";
		}
		MainController mc = Environment.getMainController();
		EvaluationResult er = JavaProcessor.evaluateScript(script, "");
		if (er.getType() == ResultType.ERROR){
			mc.printEntry("\n");
			mc.printResult(er);
		}
		mc.updateJavaScope();
	}
	
	private String recordToString(CSVRecord record){
		String result = "{";
		for (int i = 0; i < header.length; i++){
			String value = (i >= record.size())?"":record.get(i);
			result += "'" + header[i] + "':'" + value + "'";
			if (i < header.length - 1){
				result += ",";
			}
		}
		result += "}";
		return result;
	}
		
	public boolean save(boolean saveAs){
		if (saveAs || (this.fileName == null)){
			boolean selected = selectSaveFile();
			if (!selected){
				return false;
			}
		}
		try{
			PrintWriter writer = new PrintWriter(file);
			writer.println(fileToLoad);
			writer.println(gui.getSelectedCSVFormatAsNumber());
			writer.println(gui.getDelimiter());
			writer.println(gui.hasHeaderRow());
			writer.println(gui.getVariableName());
			writer.close();
		}catch (Exception e){
			file = null;
			fileName = null;
			type = null;
			Environment.getMainController().showMessageDialog("An error occurred while trying to save the file: " + e.getMessage(), "Save error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		saved = true;
		Environment.getEditorContainerController().updateSavedState();
		return true;
	}
	
	public static EditorStreamController load(String fullPath){
		EditorStreamController ec = new EditorStreamController();
		ec.file = new File(fullPath);
		ec.fileName = ec.file.getName();
		ec.type = ec.getTypeFromFileName();
		try{
			if (ec.type == null){
				throw new IOException("The extension of the filename is not of a known type.");
			}
			FileReader fr = new FileReader(ec.file);
			BufferedReader br = new BufferedReader(fr);
			ec.fileToLoad = br.readLine();
			ec.fileLoader = null;
			ec.gui.setFileName(new File(ec.fileToLoad).getName());
			ec.gui.setSelectedCSVFormatAsNumber(Integer.parseInt(br.readLine()));
			ec.gui.setDelimiter(br.readLine());
			ec.gui.setHeaderRow(Boolean.parseBoolean(br.readLine()));
			ec.gui.setVariableName(br.readLine());
			br.close();
			fr.close();
		}catch (Exception e){
			Environment.getMainController().showMessageDialog("An error occurred while trying to load the file: " + e.getMessage(), "Load error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		ec.saved = true;
		Environment.getEditorContainerController().updateSavedState();
		return ec;		
	}
			
	public EditorGUI getGUI(){
		return gui;
	}

	@Override
	public GUIState getState() {
		return super.getState();
	}

	@Override
	public void restoreState(GUIState state) {
		EditorStreamController ec = load((String) state.getStateVar("FILE"));
		if (ec != null){
			file = ec.file;
			fileName = ec.fileName;
			type = ec.type;
			fileToLoad = ec.fileToLoad;
			fileLoader = null;
			gui.setFileName(ec.gui.getFileName());
			gui.setSelectedCSVFormatAsNumber(ec.gui.getSelectedCSVFormatAsNumber());
			gui.setDelimiter(Character.toString(ec.gui.getDelimiter()));
			gui.setHeaderRow(ec.gui.hasHeaderRow());
			gui.setVariableName(ec.gui.getVariableName());
			saved = ec.saved;
			Environment.getEditorContainerController().updateSavedState();
			refreshFile();
		}
	}

	public void loadCSV() {
		final JFileChooser fc = new JFileChooser();
		String lastFolder = Environment.getProperties().getLastFolder();
		if (lastFolder != null){
			fc.setCurrentDirectory(new File(lastFolder));
		}
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Separated (.csv)", "csv");
		fc.addChoosableFileFilter(filter);
		int returnVal = Environment.getMainController().showDialog(fc, "Open");
		if (returnVal == JFileChooser.APPROVE_OPTION){
			fileToLoad = fc.getSelectedFile().getAbsolutePath();
			File f = new File(fileToLoad);
			Environment.getProperties().setLastFolder(f.getParent());
			gui.setFileName(f.getName());
			loadHeaderFromCSV(f);
			gui.setHeader(header);
			gui.setData(loadFirstLinesFromCSV(f));			
		}
	}
	
	private void loadHeaderFromCSV(File f){
		try {
			CSVFormat csvFormat = gui.getSelectedCSVFormat();
			csvFormat = csvFormat.withDelimiter(gui.getDelimiter());
			CSVParser parser = CSVParser.parse(f, Charset.defaultCharset(), csvFormat);

			Iterator<CSVRecord> i = parser.iterator();
			if (!i.hasNext()){
				header = new String[0];
			}
			
			CSVRecord currRecord = i.next();
			int nrCols = currRecord.size();
			header = new String[nrCols];

			for (int currCol = 0; currCol < nrCols; currCol++){
				header[currCol] = gui.hasHeaderRow()?currRecord.get(currCol):"c" + Integer.toString(currCol+1);
			}
			
			parser.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String[][] loadFirstLinesFromCSV(File f){
		String[][] result = null;
		try {
			CSVFormat csvFormat = gui.getSelectedCSVFormat();
			csvFormat = csvFormat.withDelimiter(gui.getDelimiter());
			CSVParser parser = CSVParser.parse(f, Charset.defaultCharset(), csvFormat);

			Iterator<CSVRecord> i = parser.iterator();
			if (!i.hasNext()){
				return new String[0][];
			}
			if (gui.hasHeaderRow()){
				i.next();
			}
			if (!i.hasNext()){
				return new String[0][];
			}
			
			CSVRecord currRecord = i.next();
			int currRow = 0;
			int nrCols = header.length;
			result = new String[10][nrCols];

			do{
				for (int currCol = 0; currCol < Math.min(currRecord.size(), nrCols); currCol++){
					result[currRow][currCol] = currRecord.get(currCol);
				}				
				currRecord = i.hasNext()?i.next():null;
				currRow ++;
			}while ((currRecord != null) && (currRow < 9));
			if (currRecord != null){
				for (int currCol = 0; currCol < Math.min(currRecord.size(), nrCols); currCol++){
					result[currRow][currCol] = "...";
				}								
			}
			parser.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void refreshFile() {
		if (fileToLoad != null){
			File f = new File(fileToLoad);
			loadHeaderFromCSV(f);
			gui.setHeader(header);
			gui.setData(loadFirstLinesFromCSV(f));
			fileLoader = null;
		}
	}

	public void fileChanged() {
		if (saved){
			saved = false;
			Environment.getEditorContainerController().updateSavedState();
		}
	}
}
