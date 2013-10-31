

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
 **/package ingenias.editor.rendererxml;

import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.awt.Color;
import java.awt.*;
import javax.swing.border.*;

public class DashedVerticalLinePanel
    extends JPanel {

  public DashedVerticalLinePanel() {
//    DashedBorder db=new DashedBorder(Color.black);
//    this.setBorder(db);
  }

  public DashedVerticalLinePanel(LayoutManager p0, boolean p1) {
    super(p0, p1);
  }

  public DashedVerticalLinePanel(LayoutManager p0) {
    super(p0);
  }

  public DashedVerticalLinePanel(boolean p0) {
    super(p0);
  }

  public void paint(Graphics g) {
    super.paint(g);
    Dimension size = this.getSize();
    g.setPaintMode();
    g.setColor(Color.black);
    Stroke old = ( (Graphics2D) g).getStroke();
    BasicStroke bs = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                                     BasicStroke.JOIN_MITER, 10.0f,
                                     new float[] {5.0f}
                                     , 0.0f);
    ( (Graphics2D) g).setStroke(bs);
    g.drawLine(size.width / 2, 0, size.width / 2, size.height);
    ( (Graphics2D) g).setStroke(old);
  }

  public static void main(String[] args) {
    DashedVerticalLinePanel dashedBorderPanel1 = new DashedVerticalLinePanel();
  }
}
