package nl.tue.spa.gui;

import javax.swing.JPanel;

import nl.tue.spa.controllers.EditorStreamController;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.apache.commons.csv.CSVFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;

public class EditorStreamGUI extends JPanel implements EditorGUI, ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;

	public static EditorGUIType[] allEditorStreamGUITypes = {EditorGUIType.TYPE_STREAM};
	
	EditorStreamController controller;
	private JTextField txtFile;
	private JTable table;
	private JTextField txtDelimiter;

	private JRadioButton rdbtnTDF;
	private JRadioButton rdbtnRFC4180;
	private JRadioButton rdbtnMySQL;
	private JRadioButton rdbtnDefault;
	private JRadioButton rdbtnExcel;

	JCheckBox chkHasHeaderRow; 
	private JTextField txtVariableName;
	
	public EditorStreamGUI(EditorStreamController controller){
		setPreferredSize(new Dimension(660, 510));
		setMinimumSize(new Dimension(660, 510));
		this.controller = controller;
		setLayout(null);
		
		JLabel lblFile = new JLabel("File:");
		lblFile.setBounds(30, 54, 61, 16);
		add(lblFile);
		
		txtFile = new JTextField();
		txtFile.setEnabled(false);
		txtFile.setEditable(false);
		txtFile.setBounds(70, 48, 286, 28);
		add(txtFile);
		txtFile.setColumns(10);
		
		JButton btnFile = new JButton("...");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.fileChanged();
				controller.loadCSV();
			}
		});
		btnFile.setBounds(368, 48, 32, 28);
		add(btnFile);
				
		String columnNames[] = {};
		table = new JTable(new CSVTableModel(columnNames, 0));
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrlContents = new JScrollPane(table);
		scrlContents.setBounds(30, 180, 600, 250);
		add(scrlContents);

		JLabel lblFileFormat = new JLabel("File format:");
		lblFileFormat.setBounds(30, 89, 75, 14);
		add(lblFileFormat);
		
		rdbtnExcel = new JRadioButton("Excel");
		rdbtnExcel.setBounds(175, 85, 61, 23);
		rdbtnExcel.addActionListener(this);
		add(rdbtnExcel);
		
		rdbtnDefault = new JRadioButton("Default");
		rdbtnDefault.setSelected(true);
		rdbtnDefault.setBounds(100, 85, 75, 23);
		rdbtnDefault.addActionListener(this);
		add(rdbtnDefault);
		
		rdbtnMySQL = new JRadioButton("MySQL");
		rdbtnMySQL.setBounds(238, 85, 75, 23);
		rdbtnMySQL.addActionListener(this);
		add(rdbtnMySQL);
		
		rdbtnRFC4180 = new JRadioButton("RFC 4180");
		rdbtnRFC4180.setBounds(315, 85, 80, 23);
		rdbtnRFC4180.addActionListener(this);
		add(rdbtnRFC4180);
		
		rdbtnTDF = new JRadioButton("TDF");
		rdbtnTDF.setBounds(400, 85, 75, 23);
		rdbtnTDF.addActionListener(this);
		add(rdbtnTDF);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnTDF);
		group.add(rdbtnRFC4180);
		group.add(rdbtnMySQL);
		group.add(rdbtnDefault);
		group.add(rdbtnExcel);
		
		JLabel lblSeparator = new JLabel("Separator:");
		lblSeparator.setBounds(30, 118, 61, 14);
		add(lblSeparator);
		
		txtDelimiter = new JTextField();
		txtDelimiter.setText(",");
		txtDelimiter.setBounds(105, 111, 54, 28);
		add(txtDelimiter);
		txtDelimiter.setColumns(10);
		txtDelimiter.addKeyListener(this);
		
		JLabel lblHasHeaderRow = new JLabel("Has header row:");
		lblHasHeaderRow.setBounds(30, 149, 101, 14);
		add(lblHasHeaderRow);
		
		chkHasHeaderRow = new JCheckBox("");
		chkHasHeaderRow.setBounds(142, 145, 97, 23);
		chkHasHeaderRow.addActionListener(this);
		add(chkHasHeaderRow);
		
		JButton btnRefresh = new JButton("");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.refreshFile();
			}
		});
		btnRefresh.setToolTipText("Refresh");
		btnRefresh.setIcon(new ImageIcon(EditorStreamGUI.class.getResource("/nl/tue/spa/resources/icons/3 (21).png")));
		btnRefresh.setBounds(412, 48, 32, 28);
		add(btnRefresh);
		
		JLabel lblVariableName = new JLabel("Variable name:");
		lblVariableName.setBounds(30, 450, 89, 14);
		add(lblVariableName);
		
		txtVariableName = new JTextField();
		((AbstractDocument) txtVariableName.getDocument()).setDocumentFilter(new VariableNameFilter());
		txtVariableName.setBounds(116, 443, 197, 28);
		add(txtVariableName);
		txtVariableName.setColumns(10);
		txtVariableName.addKeyListener(this);
	}
	
	public EditorStreamController getController() {
		return controller;
	}

	public CSVFormat getSelectedCSVFormat() {
		if (rdbtnTDF.isSelected()){
			return CSVFormat.TDF;
		}else if (rdbtnRFC4180.isSelected()){
			return CSVFormat.RFC4180;
		}else if (rdbtnMySQL.isSelected()){
			return CSVFormat.MYSQL;
		}else if (rdbtnExcel.isSelected()){
			return CSVFormat.EXCEL;
		}
		return CSVFormat.DEFAULT;
	}

	public int getSelectedCSVFormatAsNumber() {
		if (rdbtnTDF.isSelected()){
			return 1;
		}else if (rdbtnRFC4180.isSelected()){
			return 2;
		}else if (rdbtnMySQL.isSelected()){
			return 3;
		}else if (rdbtnExcel.isSelected()){
			return 4;
		}
		return 0;
	}
	
	public void setSelectedCSVFormatAsNumber(int number) {
		switch (number){
		case 1:
			rdbtnTDF.setSelected(true);
			break;
		case 2:
			rdbtnRFC4180.setSelected(true);
			break;
		case 3:
			rdbtnMySQL.setSelected(true);
			break;
		case 4:
			rdbtnExcel.setSelected(true);
			break;
		default:
			rdbtnDefault.setSelected(true);
			break;
		}
	}	

	public char getDelimiter() {		
		return txtDelimiter.getText().charAt(0);
	}
	
	public void setDelimiter(String delimiter) {
		txtDelimiter.setText(delimiter);
	}

	public boolean hasHeaderRow() {
		return chkHasHeaderRow.isSelected();
	}
	
	public void setHeaderRow(boolean hasHeaderRow){
		chkHasHeaderRow.setSelected(hasHeaderRow);
	}

	public void setHeader(String[] header){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setColumnIdentifiers(header);
		model.getDataVector().removeAllElements();		
	}
	
	public void setData(String[][] sData) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = 0; i < sData.length; i++){
			model.addRow(sData[i]);
		}
	}
	
	public void setFileName(String fileName){
		txtFile.setText(fileName);
	}

	public String getFileName(){
		return txtFile.getText();
	}
	
	public String getVariableName(){
		return txtVariableName.getText();
	}
	
	public void setVariableName(String variableName){
		txtVariableName.setText(variableName);
	}
	
	class CSVTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public CSVTableModel(String[] columnNames, int nrRows) {
			super(columnNames, nrRows);
		}

		public boolean isCellEditable(int row, int column){  
			return false;  
	    }
	}
	
	class VariableNameFilter extends DocumentFilter {

	    @Override
	    public void replace(FilterBypass fb, int i, int i1, String string, AttributeSet as) throws BadLocationException {
	        for (int n = string.length(); n > 0; n--) {
	            char c = string.charAt(n - 1);
	            if (Character.isAlphabetic(c)) {
	                super.replace(fb, i, i1, String.valueOf(c), as);
	            }
	        }
	    }

	    @Override
	    public void remove(FilterBypass fb, int i, int i1) throws BadLocationException {
	        super.remove(fb, i, i1);
	    }

	    @Override
	    public void insertString(FilterBypass fb, int i, String string, AttributeSet as) throws BadLocationException {
	        super.insertString(fb, i, string, as);

	    }
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		controller.fileChanged();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.fileChanged();
	}
}
