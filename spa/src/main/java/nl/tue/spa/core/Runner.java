package nl.tue.spa.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JOptionPane;

import nl.tue.spa.executor.Script;
import nl.tue.spa.gui.ActiveGUI.ActiveType;

public class Runner implements Runnable{

	private static ConcurrentLinkedQueue<String> partiesToThread;
	private static ConcurrentLinkedQueue<File> partiesToRun;
	
	private static Map<String,Script> party2Script;
	
	public Runner(){
		partiesToThread = new ConcurrentLinkedQueue<String>();
		partiesToRun = new ConcurrentLinkedQueue<File>();
		party2Script = new HashMap<String,Script>();
	}
	
	@Override
	public void run() {
		while (true){			
			try {
				ArrayList<File> executed = new ArrayList<File>();
				for (File party: partiesToRun){
					try {
						Script script = Script.initialize(party);
						party2Script.put(party.getName(),script);
						script.load();
						script.execute();
						executed.add(party);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				partiesToRun.removeAll(executed);
				for (String fileName: partiesToThread){
					executeLineOnParty(fileName, "run()");
				}				
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void start(){
		new Thread(new Runner()).start();
	}
	
	public void addPartyToThread(String fileName){
		if (Environment.getActiveController().isActive(fileName)){
			Environment.getMainController().showMessageDialog(fileName + " is already active. Stop it before activating it again.", "Activation error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		partiesToThread.add(fileName);
		Environment.getActiveController().addActive(fileName, ActiveType.TYPE_THREAD);
	}
	
	public void addPartyToRun(File file){
		partiesToRun.add(file);
	}
	
	public void removePartyToThread(String fileName) {
		String toRemove = null;
		for (String ep: partiesToThread){
			if (ep.equals(fileName)){
				toRemove = ep;
				break;
			}
		}
		if (toRemove != null){
			partiesToThread.remove(toRemove);		
			Environment.getActiveController().removeActive(fileName);
		}
	}

	public void executeLineOnParty(String fileName, String line) {
		Script script = party2Script.get(fileName);
		script.execute(line);
	}
}
