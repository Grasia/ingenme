
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz
 * 
 * This file is part of the INGENME tool. INGENME is an open source meta-editor
 * which produces customized editors for user-defined modeling languages
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 **/


package ingenias.editor;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PropertiesWindow extends javax.swing.JDialog  implements java.io.Serializable {


	BorderLayout borderLayout1 = new BorderLayout();
	JLabel jLabel1 = new JLabel();
	JPanel jPanel1 = new JPanel();
	JButton cancel = new JButton();
	JButton ok = new JButton();
	Object[][] props=null;
	JPanel central = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	JPanel table = null;
	Properties iProperties=null;
	TextAreaCellEditor tace=null;
	Hashtable<ProjectProperty,JTextField> storedValues=new Hashtable<ProjectProperty,JTextField>(); 
	

	public PropertiesWindow(Properties prop) {
		super((Frame)null,"IDE Properties",true);
		this.iProperties=prop;
		props=new Object[prop.size()][];
		Vector<String> propertyKeys=new Vector<String>();
		for (Object key:prop.keySet()){
			propertyKeys.add(key.toString());
		}
		
		Collections.sort(propertyKeys);
		
		Enumeration enumeration=propertyKeys.elements();
		int k=2;		
		GridBagLayout gbl=new GridBagLayout();
		table=new JPanel(gbl);
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.ipadx=10;
		gbc.ipady=10;		
		addLabelColumn(0,0, gbl, gbc, "Name", Color.YELLOW);
		addLabelColumn(1,0, gbl, gbc, "Value", Color.YELLOW);
		addLabelColumn(2,0, gbl, gbc, "Tip", Color.YELLOW);
		String lastModule="";

		while (enumeration.hasMoreElements()){ 

			final ProjectProperty p=(ProjectProperty)prop.get(enumeration.nextElement().toString());			
						
			if (!p.module.equals(lastModule)){
				lastModule=p.module;
				gbc.gridx=0;
				gbc.gridy=k+1;
				gbc.gridwidth=3;
				gbc.ipady=0;
				int lastValue=gbc.anchor;
				gbc.anchor=gbc.CENTER;
				//gbc.fill=gbc.BOTH;

				addLabelColumn(0,k+1, gbl, gbc, p.module, Color.GREEN);
				//line.setBackground(Color.black);			
				//gbl.setConstraints(line,gbc);
				//table.add(line);

				gbc.gridwidth=1;
				gbc.ipady=10;
				gbc.anchor=lastValue;
				k=k+2;				
			} else
				k=k+1;
			
			//addLabelColumn(0,k, gbl, gbc, p.module);
			addLabelColumn(0,k, gbl, gbc, p.name);
			addLabelColumn(2,k, gbl, gbc, p.tooltip);
			
			gbc.gridx=1;
			gbc.gridy=k;
			final JTextField modValue=new JTextField(20);
			storedValues.put(p, modValue);
			modValue.setText(p.value);
			gbl.setConstraints(modValue, gbc);
			table.add(modValue);

			

		}



		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		this.pack();

	}

	private void addLabelColumn(int x,int y,GridBagLayout gbl,
			GridBagConstraints gbc, final String value) {
		JLabel modLabel=new JLabel(value);
		modLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		gbc.gridx=x;
		gbc.gridy=y;			
		gbl.setConstraints(modLabel, gbc);
		table.add(modLabel);
	}
	
	private void addLabelColumn(int x,int y,GridBagLayout gbl,
			GridBagConstraints gbc, final String value, Color color) {
		JLabel modLabel=new JLabel(value);
		modLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);		
		modLabel.setBackground(color);
		modLabel.setOpaque(true);
		modLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		gbc.gridx=x;
		gbc.gridy=y;			
		gbc.anchor=gbc.CENTER;
		gbl.setConstraints(modLabel, gbc);
		table.add(modLabel);
		gbc.anchor=gbc.LINE_START;
	}

	public static void main(String[] args) {
		Properties ps=new Properties();
		ps.put("primero",new ProjectProperty("p1","1","uno","dos","tres"));
		ps.put("segundo",new ProjectProperty("p2","2","cuatro","cinco","seis"));
		PropertiesWindow p = new PropertiesWindow(ps);
		p.pack();
		p.show();
	}

	public PropertiesWindow() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void updateProperties(){
		Enumeration enumeration=this.storedValues.keys();
		while (enumeration.hasMoreElements()){     
			ProjectProperty pp=(ProjectProperty)enumeration.nextElement();
			
			JTextField jtf=(JTextField)storedValues.get(pp);
			/*if (!pp.value.equals(jtf.getText()))
				IDE.setChanged();*/
			pp.value=jtf.getText();

		}

	}

	private void jbInit() throws Exception {
		jLabel1.setText("You can define properties value at the middle column");
		this.getContentPane().setLayout(borderLayout1);
		cancel.setText("Cancel");
		cancel.addActionListener(new PropertiesWindow_cancel_actionAdapter(this));
		ok.setText("OK");
		ok.addActionListener(new PropertiesWindow_ok_actionAdapter(this));
		central.setLayout(borderLayout2);
		table.addFocusListener(new PropertiesWindow_table_focusAdapter(this));
		this.getContentPane().add(jLabel1, BorderLayout.NORTH);
		this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(ok, null);
		jPanel1.add(cancel, null);
		this.getContentPane().add(central, BorderLayout.CENTER);
		central.add(jScrollPane1, BorderLayout.CENTER);
//		central.add(table.getTableHeader(),BorderLayout.NORTH);
		jScrollPane1.getViewport().add(table, null);

	}

	void ok_actionPerformed(ActionEvent e) {

		this.updateProperties();
		this.hide();
	}

	void cancel_actionPerformed(ActionEvent e) {
		this.hide();
	}

	void table_focusLost(FocusEvent e) {
		/*    javax.swing.table.TableColumn helpcol=table.getColumnModel().getColumn(2);
    helpcol.getCellEditor().stopCellEditing();*/


	}


}

class PropertiesWindow_ok_actionAdapter implements java.awt.event.ActionListener,java.io.Serializable {
	PropertiesWindow adaptee;

	PropertiesWindow_ok_actionAdapter(PropertiesWindow adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.ok_actionPerformed(e);
	}
}

class PropertiesWindow_cancel_actionAdapter implements java.awt.event.ActionListener,java.io.Serializable {
	PropertiesWindow adaptee;

	PropertiesWindow_cancel_actionAdapter(PropertiesWindow adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.cancel_actionPerformed(e);
	}
}

class PropertiesWindow_table_focusAdapter extends java.awt.event.FocusAdapter implements java.io.Serializable {
	PropertiesWindow adaptee;

	PropertiesWindow_table_focusAdapter(PropertiesWindow adaptee) {
		this.adaptee = adaptee;
	}
	public void focusLost(FocusEvent e) {
		adaptee.table_focusLost(e);
	}
}