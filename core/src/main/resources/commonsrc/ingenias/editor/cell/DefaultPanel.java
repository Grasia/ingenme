
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

package ingenias.editor.cell;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class DefaultPanel extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
private JPanel inside;

  public DefaultPanel(JPanel inside) {
   this.setLayout(new BorderLayout());
   this.inside=inside;
   this.add(inside, BorderLayout.CENTER);
  }
  
  public JPanel getPanelInside(){
	  return inside;
  }

  public void paint(Graphics graph){
	 
  // graph.setXORMode(Color.WHITE); // If activated, fonts are painted without anti-aliasing. They will look ugly.
   super.paint(graph); 
   if(inside!=null && inside.getBorder()!=null && inside.getBorder().isBorderOpaque()){
	   Graphics2D g2d=(Graphics2D) graph;
    graph.drawRect(0,0,getSize().width,getSize().height);
   }
 //  graph.setPaintMode();

  }

}