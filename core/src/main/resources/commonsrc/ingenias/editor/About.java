
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz
 * 
 * This file is part of the INGENME tool. INGENME is an open source meta-editor
 * which produces customized editors for user-defined modeling languages
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License
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



public class About extends JDialog  implements java.io.Serializable {
  JLabel splash = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JTextPane jTextPane1 = new JTextPane();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton close = new JButton();

  public About(){
    super((Frame)null,"ABOUT",true);


   /* javax.swing.ImageIcon ii=new ImageIcon("images/splash.png");
    splash.setIcon(ii);*/
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    About about1 = new About();
    about1.pack();
    about1.show();
  }
  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    jTextPane1.setContentType("text/html");
    jTextPane1.setBackground(new Color(0, 0, 173));
    jTextPane1.setFont(new java.awt.Font("Dialog", 1, 12));
    jTextPane1.setForeground(Color.yellow);
    jTextPane1.setBorder(BorderFactory.createEtchedBorder());
    jTextPane1.setEditable(false);
    jTextPane1.setText("<html><body>" +
    		"<Font color=\"#ffffcc\"><b><CENTER>@toolname@</CENTER><br>\n" +    		
    		"Version @versionnumber@ of the language created by @authorname@ <br>\n"+
    		"New versions of the editor can be created in <br>@distributionURL@<br>\n"+
    		"<p>This editor was created using the INGENME <br>" +
    		"(ingenme.sourceforge.net) framework.</p>" +
    		"<p>This editor is distributed under the terms of the <br>" +
    		"GPLv3 (http://www.gnu.org/licenses/gpl.html)</a>.<br>" +
    		"Copies of this license should have been delivered <br>"+
    		"together with this software. By using this software, <br>" +
    		"you are accepting the terms of this license</p>" +
    		"</b></font></body></html>");
    jPanel1.setLayout(borderLayout2);
    close.setText("Close");
    close.addActionListener(new About_close_actionAdapter(this));
    this.getContentPane().add(splash, BorderLayout.CENTER);
    this.getContentPane().add(jPanel1,  BorderLayout.SOUTH);
    jPanel1.add(jTextPane1, BorderLayout.CENTER);
    jPanel1.add(close, BorderLayout.SOUTH);
    this.setResizable(false);
  }

  void close_actionPerformed(ActionEvent e) {
   this.hide();
  }
}

class About_close_actionAdapter implements java.awt.event.ActionListener,  java.io.Serializable  {
  About adaptee;

  About_close_actionAdapter(About adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.close_actionPerformed(e);
  }
}
