package nl.tue.spa.core;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JOptionPane;

import nl.tue.spa.gui.ActiveGUI.ActiveType;

public class Runner implements Runnable{

	private static ConcurrentLinkedQueue<String> executingParties;
	private static ConcurrentLinkedQueue<String> onceExecutingParties;
	
	public Runner(){
		executingParties = new ConcurrentLinkedQueue<String>();
		onceExecutingParties = new ConcurrentLinkedQueue<String>();
	}
	
	@Override
	public void run() {
		while (true){			
			try {
				ArrayList<String> executed = new ArrayList<String>();
				for (String party: onceExecutingParties){
					Environment.getMainController().executeScriptOnParty(party);
					executed.add(party);
				}
				onceExecutingParties.removeAll(executed);
				for (String party: executingParties){
					Environment.getMainController().executeScriptOnParty(party, "run()");
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
	
	public void addRunningController(String fileName){
		if (Environment.getActiveController().isActive(fileName)){
			Environment.getMainController().showMessageDialog(fileName + " is already active. Stop it before activating it again.", "Activation error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		boolean controllerExists = false;
		for (String ep: executingParties){
			if (ep.equals(fileName)){
				controllerExists = true;
				break;
			}
		}

		if (!controllerExists){
			executingParties.add(fileName);
			Environment.getActiveController().addActive(fileName, ActiveType.TYPE_THREAD);
		}
	}
	
	public void addOnceRunningController(String fileName){
		onceExecutingParties.add(fileName);
	}
	
	public void removeRunningController(String fileName) {
		String toRemove = null;
		for (String ep: executingParties){
			if (ep.equals(fileName)){
				toRemove = ep;
				break;
			}
		}
		if (toRemove != null){
			executingParties.remove(fileName);		
			Environment.getActiveController().removeActive(fileName);
		}
	}
}
