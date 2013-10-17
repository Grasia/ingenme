
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
package ingenias.editor.rendererxml;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.border.*;

public class DoubleBorderPanel extends JPanel {

   public DoubleBorderPanel() {
	   CompoundBorder cb=new CompoundBorder(new CompoundBorder(new LineBorder(Color.black,3), new EmptyBorder(3,3,3,3)), new LineBorder(Color.black,3));
    this.setBorder(cb);
  }

  public DoubleBorderPanel(LayoutManager p0, boolean p1) {
    super(p0, p1);
    CompoundBorder cb=new CompoundBorder(new CompoundBorder(new LineBorder(Color.black,3), new EmptyBorder(3,3,3,3)), new LineBorder(Color.black,3));
    this.setBorder(cb);
  }

  public DoubleBorderPanel(LayoutManager p0) {
    super(p0);
    CompoundBorder cb=new CompoundBorder(new CompoundBorder(new LineBorder(Color.black,3), new EmptyBorder(3,3,3,3)), new LineBorder(Color.black,3));
    this.setBorder(cb);
  }

  public DoubleBorderPanel(boolean p0) {
    super(p0);
    CompoundBorder cb=new CompoundBorder(new CompoundBorder(new LineBorder(Color.black,3), new EmptyBorder(3,3,3,3)), new LineBorder(Color.black,3));
    this.setBorder(cb);
  }
  public static void main(String[] args) {
    DoubleBorderPanel dashedBorderPanel1 = new DoubleBorderPanel();
    dashedBorderPanel1.add(new JLabel("hola"));
    JFrame jf=new JFrame();
    jf.getContentPane().add(dashedBorderPanel1);
    jf.pack();
    jf.setVisible(true);
  }
}