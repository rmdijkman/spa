package nl.tue.spa.gui;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import nl.tue.spa.controllers.ConsoleController;
import nl.tue.spa.core.Environment;
import nl.tue.spa.executor.r.RConsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ConsoleGUI extends JInternalFrame implements RConsole{
	private static final long serialVersionUID = 1L;
	
	private static String PROMPT = "> ";

	private String BACK_SPACE_ACTION;
	private String LEFT_ACTION;
	
	ConsoleController controller;
	
	JTextPane textPane;	
	
	public ConsoleGUI(ConsoleController controller){
		super("Console", true, true, false, false);
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.addInternalFrameListener(new InternalFrameAdapter(){
			public void internalFrameClosing(InternalFrameEvent e){
				Environment.getMainController().closeConsole();
			}
		});
		setBounds(0, 0, 800, 600);
		setResizable(true);
		BasicInternalFrameUI ui = (BasicInternalFrameUI) getUI();
		Container north = (Container) ui.getNorthPane();
		north.remove(0);
		north.validate();
		north.repaint();

		this.controller = controller;

		textPane = new JTextPane();
		replaceKeyByAction(KeyEvent.VK_ENTER);
		replaceKeyByAction(KeyEvent.VK_UP);
		replaceKeyByAction(KeyEvent.VK_DOWN);
		replaceKeyByAction(KeyEvent.VK_HOME);
		BACK_SPACE_ACTION = (String) textPane.getInputMap().get(KeyStroke.getKeyStroke("BACK_SPACE"));
		LEFT_ACTION = (String) textPane.getInputMap().get(KeyStroke.getKeyStroke("LEFT"));
		textPane.addKeyListener(controller);
		textPane.setFont(new Font("Courier New", Font.PLAIN, 13));
		JScrollPane scrollPane = new JScrollPane(textPane);

		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	
	public void disableMoveLeft(){
		textPane.getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), "none");
		textPane.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "none");
	}
	
	public void enableMoveLeft(){
		textPane.getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), BACK_SPACE_ACTION);
		textPane.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), LEFT_ACTION);
	}
	
	private void replaceKeyByAction(final int keyCode){
		InputMap iMap = textPane.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap aMap = textPane.getActionMap();
		iMap.put(KeyStroke.getKeyStroke(keyCode, 0), keyCode);
		aMap.put(keyCode, new AbstractAction(){
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				controller.processKey(keyCode);
			}
		});		
	}
	
	public String getText(int fromPosition, int length){
		try {
			return textPane.getDocument().getText(fromPosition, length);
		} catch (BadLocationException e) {
			return null;
		}
	}
	
	public String getSelectedText(){
		return textPane.getSelectedText();
	}

	public void printError(String error){
		appendTextToPane(error, Color.RED);
	}
	
	public void printMessage(String msg){
		appendTextToPane(msg, Color.BLUE);
	}
	
	public void printPrompt(){
		appendTextToPane(PROMPT, Color.BLACK);
	}
	
	public void printEntry(String entry){
		appendTextToPane(entry, Color.BLACK);		
	}
	
	public int getCaretPosition(){
		return textPane.getCaretPosition();
	}
	
	public int getTextLength(){
		return textPane.getDocument().getLength();
	}
	
	public void setCaretPosition(int position){
		textPane.setCaretPosition(position);
	}
	
	private void appendTextToPane(String msg, Color c){
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        textPane.setCaretPosition(textPane.getDocument().getLength());
        textPane.setCharacterAttributes(aset, false);
        textPane.replaceSelection(msg);
        textPane.setCaretPosition(textPane.getDocument().getLength());
    }

	public void removeText(int caretPosition, int length) {
		try {
			textPane.getDocument().remove(caretPosition, length);
		} catch (BadLocationException e) {
		}
	}

	public void insertText(int caretPosition, String text) {
		try {
			textPane.getDocument().insertString(caretPosition, text, null);
		} catch (BadLocationException e) {
		}
	}
}