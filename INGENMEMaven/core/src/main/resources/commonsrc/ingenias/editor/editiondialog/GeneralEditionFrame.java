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


package ingenias.editor.editiondialog;

import ingenias.editor.GraphManager;
import ingenias.editor.entities.Entity;
import ingenias.editor.widget.DnDJTree;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

public class GeneralEditionFrame extends javax.swing.JDialog implements java.io.Serializable {
	private JScrollPane mainscroll = new JScrollPane();
	public static final int CANCELLED=0;
	public static final int ACCEPTED=1;
	public static final int PROGRESSING=2;	
	
	private int status=PROGRESSING;


	public GeneralEditionFrame(ingenias.editor.Editor editor,ingenias.editor.ObjectManager om,GraphManager gm,final Frame dialogOwner,String title,final Entity ent) {
		super(dialogOwner,title,true);

		final JPanel main=new JPanel();
		BorderLayout bl=new BorderLayout();
		main.setLayout(bl);
		this.getContentPane().add(main);
		main.addContainerListener(new java.awt.event.ContainerAdapter(){
			public void componentAdded(java.awt.event.ContainerEvent ce){
				pack();
			}
		});
				

		final JDialog self=this;
		final GeneralEditionPanel gep=new GeneralEditionPanel(editor,dialogOwner, om,gm,ent);
		main.add(mainscroll,BorderLayout.CENTER);
		mainscroll.getViewport().add(gep,null);    
		JButton cancel=new JButton("Cancel");
		cancel.setName("cancel");
		cancel.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent ae) {				
				gep.undo();
				self.setVisible(false);
				status=CANCELLED;
			}
		});

		JButton accept=new JButton("Accept");
		accept.setName("accept");
		accept.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent ae) {				
				gep.confirmActions();
				self.setVisible(false);
				status=ACCEPTED;
				
			}
		});

		JPanel southButtons=new JPanel();
		southButtons.add(accept);
		southButtons.add(cancel);
		main.add(southButtons,BorderLayout.SOUTH);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if (dialogOwner!=null)
					dialogOwner.requestFocusInWindow();

			}});
		self.requestFocusInWindow();
		setLocation(dialogOwner.getLocation().x+getSize().width/2,dialogOwner.getLocation().y+	getSize().height/2);

	}

	public void pack(){
		super.pack();
		final JDialog jd=this;
		this.setSize(this.getSize().width+30,300);

	}


	public static void main(String args[]){

		
	}

	public int getStatus() {
		return status;
	}
}