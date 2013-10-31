
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

package ingenias.editor.utils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.swixml.SwingEngine;


public class XMLGUITester {


  public static void main(String args[]) throws Exception {
    String result="";
    SwingEngine se = ingenias.editor.cell.SWIRenderer.getSWIEngine();
//    se.getTaglib().registerTag("dashedpanel",ingenias.editor.rendererxml.DashedBorderPanel.class);
    FileInputStream fis =  new FileInputStream(args[0]);
    StringBuffer sb = new StringBuffer();
    int read = 0;
    while (read != -1) {
      read = fis.read();
      if (read != -1) {
        sb.append( (char) read);
      }
    }
    ;
    fis.close();

    result = sb.toString();
    result = result.replaceAll("##", "<");
    result = result.replaceAll("#", ">");
    final JFrame jf=new JFrame();
    
    JFrame packFrame =new JFrame("Pack");
    JButton packbutton=new JButton("pack");
    packFrame.getContentPane().add(packbutton);
    
    packFrame.pack();
    packbutton.addActionListener(new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			jf.pack();
			jf.invalidate();
		}
	});
    jf.getContentPane().setLayout(new BorderLayout());
    jf.getContentPane().add(se.render(new java.io.StringReader(result)), BorderLayout.CENTER);
    jf.pack();
    jf.show();
    packFrame.setVisible(true);

;
  }
}