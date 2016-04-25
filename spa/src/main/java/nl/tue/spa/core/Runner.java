package nl.tue.spa.core;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Runner implements Runnable{

	private ConcurrentLinkedQueue<String> executingParties;
	
	public Runner(){
		executingParties = new ConcurrentLinkedQueue<String>(); 
	}
	
	@Override
	public void run() {
		while (true){			
			try {				
				for (String party: executingParties){
					Environment.getMainController().executeJavaScriptOnParty(party, "run()");		
				}				
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void start(){
		new Thread(new Runner()).start();
	}
	
	public void addRunningController(String fileName){
		boolean controllerExists = false;
		for (String ep: executingParties){
			if (ep.equals(fileName)){
				controllerExists = true;
				break;
			}
		}

		if (!controllerExists){
			executingParties.add(fileName);
			Environment.getActiveController().addActive(fileName);
		}
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
