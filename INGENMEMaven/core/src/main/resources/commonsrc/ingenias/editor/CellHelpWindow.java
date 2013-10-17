
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class CellHelpWindow extends javax.swing.JFrame implements java.io.Serializable {
  private Box box1;
  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel3 = new JPanel();
  private JButton close = new JButton();
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private BorderLayout borderLayout3 = new BorderLayout();
  private JScrollPane scp1 = new JScrollPane();
  JTextPane description = new JTextPane();
  JScrollPane scp2 = new JScrollPane();
  JTextPane recom = new JTextPane();

  public CellHelpWindow() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

  }
  private void jbInit() throws Exception {
    box1 = Box.createVerticalBox();
    this.getContentPane().setLayout(borderLayout1);
    close.setText("Close");
    close.addActionListener(new CellHelpWindow_close_actionAdapter(this));
    jLabel1.setText("Recommendation");
    jLabel2.setText("Description");
    jPanel1.setLayout(borderLayout2);
    jPanel2.setLayout(borderLayout3);
    description.setText("jTextPane1");
    description.setContentType("text/html");
    scp1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scp1.setToolTipText("");
    recom.setText("");
    recom.setContentType("text/html");
    this.getContentPane().add(box1, BorderLayout.CENTER);
    box1.add(jPanel1, null);
    box1.add(jPanel2, null);
    this.getContentPane().add(jPanel3,  BorderLayout.SOUTH);
    jPanel3.add(close, null);
    jPanel2.add(jLabel1,  BorderLayout.NORTH);
    jPanel2.add(scp2, BorderLayout.CENTER);
    scp2.getViewport().add(recom, null);
    jPanel2.add(scp2,  BorderLayout.CENTER);
    jPanel1.add(jLabel2, BorderLayout.NORTH);
    jPanel1.add(scp1,  BorderLayout.CENTER);
    scp1.getViewport().add(description, null);

  }

  public void setDescription(String desc){
    this.description.setText(desc);
    this.description.setCaretPosition(0);

/*    scp1.getVerticalScrollBar().setValueIsAdjusting(true);
    scp1.getVerticalScrollBar().setValue(0);
    scp1.getViewport().toViewCoordinates(new Point(0,0));*/


  }

  public void setRec(String rec){
    this.recom.setText(rec);
    this.recom.setCaretPosition(0);
/*    scp2.getViewport().toViewCoordinates(new Point(0,0));
    scp2.getVerticalScrollBar().setValueIsAdjusting(true);
    this.scp2.getVerticalScrollBar().setValue(0);*/
  }

  void close_actionPerformed(ActionEvent e) {
   this.hide();

  }

  public static void main(String args[]){
    CellHelpWindow chw=new CellHelpWindow();
    chw.setDescription(" Tasks is the encapsulation of actions or non-distributable algorithms. Tasks can use Applications and resources. Tasks generate changes in the mental state of the agent that executes them. Changes consist of: (a) modifying, creating or destroying mental entities; or (b) changes in the perception of the world by acting over applications (applications act over the world producing events, that are perceived by the agent). Though tasks can be also assigned to roles, at the end, it will belong to an agent. ");
    chw.setRec(" Tasks is the encapsulation of actions or non-distributable algorithms. Tasks can use Applications and resources. Tasks generate changes in the mental state of the agent that executes them. Changes consist of: (a) modifying, creating or destroying mental entities; or (b) changes in the perception of the world by acting over applications (applications act over the world producing events, that are perceived by the agent). Though tasks can be also assigned to roles, at the end, it will belong to an agent. ");
    chw.setLocation(100,100);
    chw.setSize(350,300);
    chw.show();
  }

}

class CellHelpWindow_close_actionAdapter implements java.awt.event.ActionListener,java.io.Serializable  {
  CellHelpWindow adaptee;

  CellHelpWindow_close_actionAdapter(CellHelpWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.close_actionPerformed(e);
  }
}