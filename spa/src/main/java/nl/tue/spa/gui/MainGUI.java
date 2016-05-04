package nl.tue.spa.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import nl.tue.spa.core.Environment;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Toolkit;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.Box;

public class MainGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	private JSplitPane splitRoot2TopBottom;
	private JSplitPane splitTop2BrowserEditor;
	private JSplitPane splitBottom2ConsoleRunning;
	private JSplitPane splitRunning2VariablesActive;
	
	private JCheckBoxMenuItem chckbxmntmConsole;
	private JCheckBoxMenuItem chckbxmntmVariables;
	private JCheckBoxMenuItem chckbxmntmActive;
	private JCheckBoxMenuItem chckbxmntmEditor;
	
	private JDesktopPane desktopBrowserContainer;
	
	/**
	 * Create the frame.
	 */
	public MainGUI() {
		setTitle("Streaming Process data Analyzer");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/nl/tue/spa/resources/icons/2 (18).png")));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(0, 0, 1024, 768);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we){
				Environment.getMainController().closeProgram();
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnNewFile = new JMenu("New File");
		mnFile.add(mnNewFile);
		
		JMenuItem mntmJavaScript = new JMenuItem("Java Script");
		mntmJavaScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Environment.getMainController().newJavaScriptFile();
			}
		});
		mnNewFile.add(mntmJavaScript);
		
		JMenuItem mntmRScript = new JMenuItem("R Script");
		mnNewFile.add(mntmRScript);

		JMenuItem mntmGraphScript = new JMenuItem("Graph Script");
		mntmGraphScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Environment.getMainController().newGraphScriptFile();
			}
		});
		mnNewFile.add(mntmGraphScript);

		JMenuItem mntmStream = new JMenuItem("Stream");
		mntmStream.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Environment.getMainController().newStreamFile();
			}
		});
		mnNewFile.add(mntmStream);
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		
		JMenuItem mntmOpen = new JMenuItem("Open File");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Environment.getMainController().loadFile();
			}
		});
		mnFile.add(mntmOpen);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmSaveAll = new JMenuItem("Save All");
		mnFile.add(mntmSaveAll);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);
		
		JMenuItem mntmCloseAll = new JMenuItem("Close All");
		mnFile.add(mntmCloseAll);
		
		JSeparator separator_3 = new JSeparator();
		mnFile.add(separator_3);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnFile.add(mntmQuit);
		
		Component horizontalStrut = Box.createHorizontalStrut(10);
		menuBar.add(horizontalStrut);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);		
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		menuBar.add(horizontalStrut_1);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);		
		
		chckbxmntmConsole = new JCheckBoxMenuItem("Console", false);
		chckbxmntmConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmConsole.getState()){
					Environment.getMainController().openConsole();
				}else{
					Environment.getMainController().closeConsole();
				}
			}
		});
		mnView.add(chckbxmntmConsole);

		chckbxmntmVariables = new JCheckBoxMenuItem("Variables", false);
		chckbxmntmVariables.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmVariables.getState()){
					Environment.getMainController().openVariablesWindow();
				}else{
					Environment.getMainController().closeVariablesWindow();
				}
			}
		});
		mnView.add(chckbxmntmVariables);

		chckbxmntmActive = new JCheckBoxMenuItem("Active", false);
		chckbxmntmActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmActive.getState()){
					Environment.getMainController().openActiveWindow();
				}else{
					Environment.getMainController().closeActiveWindow();
				}
			}
		});
		mnView.add(chckbxmntmActive);

		chckbxmntmEditor = new JCheckBoxMenuItem("Editor", false);
		chckbxmntmEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmEditor.getState()){
					Environment.getMainController().openEditorWindow();
				}else{
					Environment.getMainController().closeEditorWindow();
				}
			}
		});
		mnView.add(chckbxmntmEditor);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		desktopBrowserContainer = new JDesktopPane();
		
		splitRoot2TopBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitTop2BrowserEditor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitBottom2ConsoleRunning = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitRunning2VariablesActive = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitRoot2TopBottom.setTopComponent(splitTop2BrowserEditor);
		splitRoot2TopBottom.setBottomComponent(splitBottom2ConsoleRunning);
		splitBottom2ConsoleRunning.setRightComponent(splitRunning2VariablesActive);
		splitRoot2TopBottom.setDividerLocation(700);
		splitTop2BrowserEditor.setDividerLocation(500);
		splitBottom2ConsoleRunning.setDividerLocation(600);
		splitRunning2VariablesActive.setDividerLocation(300);		
		
		splitTop2BrowserEditor.setLeftComponent(desktopBrowserContainer);
		
		contentPane.add(splitRoot2TopBottom, BorderLayout.CENTER);
	}
	
	public void setMenuConsoleSelected(boolean b) {
		chckbxmntmConsole.setState(b);
	}

	public void setMenuVariablesSelected(boolean b) {
		chckbxmntmVariables.setState(b);
	}

	public void setMenuActiveSelected(boolean b) {
		chckbxmntmActive.setState(b);
	}
	
	public void setMenuEditorSelected(boolean b) {
		chckbxmntmEditor.setState(b);
	}

	public void addWindow(Component gui){
		if (gui instanceof ConsoleGUI){
			splitBottom2ConsoleRunning.setLeftComponent(gui);
			gui.validate();
		}else if (gui instanceof EditorContainerGUI){
			splitTop2BrowserEditor.setRightComponent(gui);
			gui.validate();
		}else if (gui instanceof ActiveGUI){
			splitRunning2VariablesActive.setRightComponent(gui);
			gui.validate();
		}else if (gui instanceof VariablesGUI){
			splitRunning2VariablesActive.setLeftComponent(gui);
			gui.validate();
		}else if (gui instanceof BrowserGUI){
			desktopBrowserContainer.add(gui);
		}
	}

	public void removeWindow(Component gui){
		if (gui instanceof ConsoleGUI){
			splitBottom2ConsoleRunning.remove(gui);
		}else if (gui instanceof EditorContainerGUI){
			splitTop2BrowserEditor.remove(gui);
		}else if (gui instanceof ActiveGUI){
			splitRunning2VariablesActive.remove(gui);
		}else if (gui instanceof VariablesGUI){
			splitRunning2VariablesActive.remove(gui);
		}
	}

	public int[] getDividerLocations(){
		int[] result = new int[4];
		result[0] = splitRoot2TopBottom.getDividerLocation();
		result[1] = splitTop2BrowserEditor.getDividerLocation();
		result[2] = splitBottom2ConsoleRunning.getDividerLocation();
		result[3] = splitRunning2VariablesActive.getDividerLocation();
		return result;
	}
	
	public void setDividerLocations(int[] dividerLocations){
		splitTop2BrowserEditor.setDividerLocation(dividerLocations[1]);
		splitBottom2ConsoleRunning.setDividerLocation(dividerLocations[2]);
		splitRunning2VariablesActive.setDividerLocation(dividerLocations[3]);		
		splitRoot2TopBottom.setDividerLocation(dividerLocations[0]);
	}	
}
