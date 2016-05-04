package nl.tue.spa.gui;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.tue.spa.controllers.ActiveController;

import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActiveGUI extends JPanel implements ListSelectionListener{
	private static final long serialVersionUID = 1L;
	
	public enum ActiveType {
		TYPE_THREAD ("thread"), TYPE_PUBSUB ("subscribed");
		private String name;
		ActiveType(String name){
			this.name = name;
		}
		public String getName(){
			return name;
		}
	};
	
	private static String columnNames[] = {"component","type"};
	
	private ActiveController controller;
	private JTable table;
	private JButton btnRemove;
	
	public ActiveGUI(ActiveController controller){		
		this.controller = controller;
		
		this.setLayout(new BorderLayout());

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setFocusable(false);
		add(toolBar, BorderLayout.NORTH);
		
		btnRemove = new JButton("");
		btnRemove.setToolTipText("Stop");
		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() != -1){
					controller.stopActive((String) table.getModel().getValueAt(table.getSelectedRow(), 0));
				}
			}
		});
		btnRemove.setFocusable(false);
		btnRemove.setIcon(new ImageIcon(ActiveGUI.class.getResource("/nl/tue/spa/resources/icons/3 (41).png")));
		toolBar.add(btnRemove);		
		
		table = new JTable(new ActiveTableModel(columnNames, 0));
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public void addActive(String program, ActiveType at) {
		if (getActive(program) != -1){
			return;
		}
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new Object[]{program, at.getName()});		
	}

	public void removeActive(String program){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowToRemove = getActive(program);
		if (rowToRemove != -1){
			model.removeRow(rowToRemove);
		}
	}
	
	public int getActive(String program){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowOfActive = -1;
		for (int i = 0; i < table.getRowCount(); i++){
			if (model.getValueAt(i, 0).toString().equals(program)){
				rowOfActive = i;
				break;
			}
		}
		return rowOfActive;
	}
	
	class ActiveTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public ActiveTableModel(String[] columnNames, int nrRows) {
			super(columnNames, nrRows);
		}

		public boolean isCellEditable(int row, int column){  
			return false;  
	    }
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		controller.selectionChanged();
	}

	public boolean hasSelection() {
		return table.getSelectedRow() != -1;
	}

	public void setRemoveEnabled(boolean hasSelection) {
		btnRemove.setEnabled(hasSelection);
	}

}
