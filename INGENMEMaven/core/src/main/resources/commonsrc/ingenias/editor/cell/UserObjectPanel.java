
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz sobre código original de Rubén Fuentes
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

package ingenias.editor.cell;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import ingenias.editor.entities.*;

public class UserObjectPanel
    extends javax.swing.JPanel {
  Entity uo = null;


  public UserObjectPanel(Entity uo) {
    super();
    this.setUserObject(uo);
    this.setBackground(Color.WHITE);

  }

  public void setUserObject(Entity uo) {
    this.uo = uo;
  }

  public Entity getUserObject() {
    return this.uo;
  }

  /*  public void validate() {
      super.validate();
    }
    public void revalidate() {
       super.revalidate();
    }
    public void repaint(long tm, int x, int y, int width, int height) {
      super.repaint(tm,x,y,width,height);
    }
    public void repaint(Rectangle r) {
      super.repaint(r);
    }
    public void repaint() {
     super.repaint();
    }
   */
  public void paint(Graphics g) {
   //g.setXORMode(Color.WHITE);
    super.paint(g);
  }

}
