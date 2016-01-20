package nl.tue.spa.gui;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import nl.tue.spa.controllers.ActiveController;
import nl.tue.spa.core.Environment;

import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActiveGUI extends JInternalFrame implements ListSelectionListener{
	private static final long serialVersionUID = 1L;
	
	private static String columnNames[] = {"active"};
	
	private ActiveController controller;
	private JTable table;
	private JButton btnRemove;
	
	public ActiveGUI(ActiveController controller){
		super("Active", true, true, false, false);
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.addInternalFrameListener(new InternalFrameAdapter(){
			public void internalFrameClosing(InternalFrameEvent e){
				Environment.getMainController().closeActiveWindow();
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

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setFocusable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
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
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	
	public void addActive(String program) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new Object[]{program});		
	}

	public void removeActive(String program) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowToRemove = -1;
		for (int i = 0; i < table.getRowCount(); i++){
			if (model.getValueAt(i, 0).toString().equals(program)){
				rowToRemove = i;
				break;
			}
		}
		if (rowToRemove != -1){
			model.removeRow(rowToRemove);
		}
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
