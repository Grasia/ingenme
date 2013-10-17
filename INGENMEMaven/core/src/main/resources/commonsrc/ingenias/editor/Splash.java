
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

public class Splash extends javax.swing.JWindow  implements java.io.Serializable {
  JLabel splash = new JLabel();
  GridLayout gridLayout1 = new GridLayout();

  public Splash(Window owner) {
    super(owner);
    javax.swing.ImageIcon ii=new ImageIcon("images/splash.png");
    splash.setIcon(ii);

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void show(){
    Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
    this.pack();
    this.setLocation(ingenias.editor.widget.GraphicsUtils.getCenter(this.getSize()));
    super.show();
  
  }

  public static void main(String[] args){
    Splash splash1 = new Splash(null);
    splash1.show();
  }

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(gridLayout1);
    gridLayout1.setColumns(1);
    this.getContentPane().add(splash, null);
  }

}