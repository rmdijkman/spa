package nl.tue.spa.gui;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.tue.spa.controllers.VariablesController;

import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VariablesGUI extends JPanel implements ListSelectionListener{
	private static final long serialVersionUID = 1L;
	
	private static String columnNames[] = {"variable", "value"};
	
	private VariablesController controller;
	private JTable table;
	JButton btnRemove;
	
	public VariablesGUI(VariablesController controller){
		this.controller = controller;

		this.setLayout(new BorderLayout());
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setFocusable(false);
		add(toolBar, BorderLayout.NORTH);
				
		JButton btnSave = new JButton("");
		btnSave.setToolTipText("Clear variables");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.clearVariables();
			}
		});
		btnSave.setFocusable(false);
		btnSave.setIcon(new ImageIcon(VariablesGUI.class.getResource("/nl/tue/spa/resources/icons/4 (50).png")));
		toolBar.add(btnSave);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut);
		
		btnRemove = new JButton("");
		btnRemove.setToolTipText("Remove variable");
		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() != -1){
					controller.removeVariable((String) table.getModel().getValueAt(table.getSelectedRow(), 0));
				}
			}
		});
		btnRemove.setFocusable(false);
		btnRemove.setIcon(new ImageIcon(VariablesGUI.class.getResource("/nl/tue/spa/resources/icons/3 (47).png")));
		toolBar.add(btnRemove);		
		
		table = new JTable(new VariableTableModel(columnNames, 0));
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(300);
		table.getSelectionModel().addListSelectionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public void setVariable(String name, String value){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new Object[]{name, value});
	}
	
	public void clear(){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		while (model.getRowCount() > 0){
			model.removeRow(0);
		}
	}
	
	class VariableTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public VariableTableModel(String[] columnNames, int nrRows) {
			super(columnNames, nrRows);
		}

		public boolean isCellEditable(int row, int column){  
			return false;  
	    }
	}
	
	public boolean hasSelection(){
		return table.getSelectedRow() != -1;
	}

	public void setRemoveEnabled(boolean removeEnabled){
		btnRemove.setEnabled(removeEnabled);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		controller.selectionChanged();
	}
}
