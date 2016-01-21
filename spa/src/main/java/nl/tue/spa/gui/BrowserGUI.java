package nl.tue.spa.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import nl.tue.spa.controllers.BrowserController;
import nl.tue.spa.core.Environment;

public class BrowserGUI extends JInternalFrame{
	private static final long serialVersionUID = 1L;
	
	BrowserController controller;
	
    private final JFXPanel jfxPanel;
    private WebEngine engine;
	
	public BrowserGUI(BrowserController controller){
		super("View", true, true, false, false);
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.addInternalFrameListener(new InternalFrameAdapter(){
			public void internalFrameClosing(InternalFrameEvent e){
				Environment.getMainController().closeGraph(controller);
			}
		});
		setBounds(0, 0, 775, 600);
		setResizable(true);
		BasicInternalFrameUI ui = (BasicInternalFrameUI) getUI();
		Container north = (Container) ui.getNorthPane();
		north.remove(0);
		north.validate();
		north.repaint();

		this.controller = controller;
		
		jfxPanel = new JFXPanel();

		getContentPane().add(jfxPanel, BorderLayout.CENTER);
	}
	
	public void loadContent(String content){
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                WebView view = new WebView();
                engine = view.getEngine();
                jfxPanel.setScene(new Scene(view));
            	JSObject window = (JSObject) engine.executeScript("window");
            	window.setMember("java", Environment.getConsoleController());
            	engine.executeScript("console.log = function(message){java.log(message);};");
                engine.loadContent(content);
            	window.setMember("eventBus", Environment.getEventBus());
            }
        });
	}
		
	public void executeJavaScript(String script){
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
            	try{
            		engine.executeScript(script);
            	}catch (Exception e){
            		Environment.getEventBus().unsubscribe(controller.getFileName());
            		Environment.getMainController().showMessageDialog(controller.getFileName() + " has subscribed to events, but an error occurred. Maybe it does not have a function update(<variable name>,<value>). Removed it from the subscription. Specific error: " + e.getMessage(), "Update error", JOptionPane.ERROR_MESSAGE);
            	}
            }
        });		
	}	
}