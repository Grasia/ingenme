
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

import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.awt.Color;
import java.awt.*;
import javax.swing.border.*;

public class LinePanel extends JPanel {

  public LinePanel() {
//    DashedBorder db=new DashedBorder(Color.black);
//    this.setBorder(db);
  this.setLayout(new FlowLayout(FlowLayout.CENTER,0,2));
  }

  public LinePanel(LayoutManager p0, boolean p1) {
    super(p0, p1);
  }

  public LinePanel(LayoutManager p0) {
    super(p0);
  }

  public LinePanel(boolean p0) {
    super(p0);
  }


  public void paint(Graphics g){
        super.paint(g);
    Dimension size=this.getSize();
//    g.setPaintMode();
    g.setColor(Color.black);
    g.drawLine(0,size.height/2,size.width,size.height/2);
  }

  public static void main(String[] args) {
    LinePanel dashedBorderPanel1 = new LinePanel();
  }
}
