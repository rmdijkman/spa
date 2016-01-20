package nl.tue.spa.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import nl.tue.spa.controllers.EditorController;

public class Runner implements Runnable{

	private static ConcurrentLinkedQueue<EditorController> executingPrograms;
	
	private Runner(){
		executingPrograms = new ConcurrentLinkedQueue<EditorController>(); 
	}
	
	@Override
	public void run() {
		while (true){			
			try {				
				for (EditorController ec: executingPrograms){
					if (ec.isSaved()){
						ec.runScript();
					}else{
						removeRunningController(ec);
					}
				}				
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void start(){
		new Thread(new Runner()).start();
	}
	
	public static void addRunningController(EditorController controller){
		if (!executingPrograms.contains(controller)){
			executingPrograms.add(controller);
			Environment.getActiveController().addActive(controller.getFileName());
		}
	}
	
	public static void removeRunningController(EditorController controller){
		Environment.getActiveController().removeActive(controller.getFileName());
		executingPrograms.remove(controller);		
	}

	public static void removeRunningController(String program) {
		EditorController toRemove = null;
		for (EditorController ec: executingPrograms){
			if (ec.getFileName().equals(program)){
				toRemove = ec;
				break;
			}
		}
		if (toRemove != null){
			removeRunningController(toRemove);
		}
	}
}
