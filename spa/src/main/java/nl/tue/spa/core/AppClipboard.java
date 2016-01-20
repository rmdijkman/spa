package nl.tue.spa.core;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class AppClipboard {

	public static void toClipboard(String text){
		StringSelection stringSelection = new StringSelection(text);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}
	
	public static String fromClipboard(){
		try {
			return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			return "";
		}
	}
}
