package nl.tue.spa.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Iterator;

import nl.tue.spa.controllers.MainController;
import nl.tue.spa.core.Environment;
import nl.tue.spa.executor.EvaluationResult.ResultType;
import nl.tue.spa.executor.java.JavaProcessor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class StreamScript extends Script{

	File file;

	Iterator<CSVRecord> fileLoader;
	
	String fileToLoad;
	
	String header[];	
	Context context;
	Scriptable scope;
	
	CSVFormat csvFormat;
	char delimiter;
	boolean hasHeaderRow;
	String variableName;

	@SuppressWarnings("unused")
	private StreamScript(){}
	
	public StreamScript(File file){
		this.file = file;		
	}

	@Override
	public void load() throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		setFileToLoad(br.readLine());
		setCSVFormat(numberToCSVFormat(Integer.parseInt(br.readLine())));
		setDelimiter(br.readLine().charAt(0));
		setHasHeaderRow(Boolean.parseBoolean(br.readLine()));
		setVariableName(br.readLine());
		br.close();
		fr.close();
	}

	@Override
	public void save() throws IOException {
		PrintWriter writer = new PrintWriter(file);
		writer.println(fileToLoad);
		writer.println(csvFormatToNumber(csvFormat));
		writer.println(delimiter);
		writer.println(hasHeaderRow);
		writer.println(variableName);
		writer.close();
	}

	@Override
	public EvaluationResult execute() {
		if (getVariableName().length() == 0) return new EvaluationResult("The stream does not have a variable name. It must have a variable name to be executed.", ResultType.ERROR);
		
		if (fileLoader == null){
			try {
				File f = new File(fileToLoad);
				loadHeaderFromCSV(f);
				CSVParser parser = CSVParser.parse(f, Charset.defaultCharset(), csvFormat);
				fileLoader = parser.iterator();
				if (hasHeaderRow() && fileLoader.hasNext()){
					fileLoader.next();
				}
				context = JavaProcessor.initializeContext();
				scope = JavaProcessor.initializeScope(context);
				JavaProcessor.evaluateScript(context, scope, variableName + "=[]", "");
			} catch (IOException e) {
				return new EvaluationResult("An error occurred while loading the stream. Specific error: " + e.getMessage(), ResultType.ERROR);
			}
		}
		
		String script = "";
		if (fileLoader.hasNext()){
			script = variableName + ".push(" + recordToString(fileLoader.next()) + ")";
		}
		MainController mc = Environment.getMainController();
		EvaluationResult er = JavaProcessor.evaluateScript(context, scope, script, "");
		mc.updateJavaScope();
		return er;
	}

	private void loadHeaderFromCSV(File f){
		try {
			CSVParser parser = CSVParser.parse(f, Charset.defaultCharset(), csvFormat);

			Iterator<CSVRecord> i = parser.iterator();
			if (!i.hasNext()){
				header = new String[0];
			}
			
			CSVRecord currRecord = i.next();
			int nrCols = currRecord.size();
			header = new String[nrCols];

			for (int currCol = 0; currCol < nrCols; currCol++){
				header[currCol] = hasHeaderRow()?currRecord.get(currCol):"c" + Integer.toString(currCol+1);
			}
			
			parser.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void loadCSV() {
		File f = new File(fileToLoad);
		Environment.getProperties().setLastFolder(f.getParent());
		loadHeaderFromCSV(f);
	}
		
	public String[][] loadFirstLinesFromCSV(File f){
		String[][] result = null;
		try {
			CSVParser parser = CSVParser.parse(f, Charset.defaultCharset(), csvFormat);

			Iterator<CSVRecord> i = parser.iterator();
			if (!i.hasNext()){
				return new String[0][];
			}
			if (hasHeaderRow){
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

	private int csvFormatToNumber(CSVFormat csvFormat) {
		if (csvFormat == CSVFormat.TDF){
			return 1;
		}else if (csvFormat == CSVFormat.RFC4180){
			return 2;
		}else if (csvFormat == CSVFormat.MYSQL){
			return 3;
		}else if (csvFormat == CSVFormat.EXCEL){
			return 4;
		}
		return 0;
	}
	
	private CSVFormat numberToCSVFormat(int number){
		if (number == 1){
			return CSVFormat.TDF;
		}else if (number == 2){
			return CSVFormat.RFC4180;
		}else if (number == 3){
			return CSVFormat.MYSQL;
		}else if (number == 4){
			return CSVFormat.EXCEL;
		}
		return CSVFormat.DEFAULT;
	}

	@Override
	public EvaluationResult execute(String line) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getFileToLoad() {
		return fileToLoad;
	}

	public void setFileToLoad(String fileToLoad) {
		this.fileToLoad = fileToLoad;
	}

	public CSVFormat getCSVFormat() {
		return csvFormat;
	}

	public void setCSVFormat(CSVFormat csvFormat) {
		this.csvFormat = csvFormat;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
		this.csvFormat = csvFormat.withDelimiter(delimiter);
	}

	public boolean hasHeaderRow() {
		return hasHeaderRow;
	}

	public void setHasHeaderRow(boolean hasHeaderRow) {
		this.hasHeaderRow = hasHeaderRow;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
}
