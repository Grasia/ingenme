
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
import java.awt.Graphics;
import java.util.Map;
import java.util.Hashtable;
import org.jgraph.graph.*;
import org.jgraph.*;
import org.jgraph.event.*;
import ingenias.editor.ObservableModel;

public class NAryView
    extends VertexView
    implements java.io.Serializable {

 // static JTextArea jta = new JTextArea();
  public static NAryViewRenderer renderer = new NAryViewRenderer();

  /*  protected class WrapTextArea extends JTextArea{
      WrapTextArea(){
        super();
        setWrapStyleWord(true);
      }
    }*/
  //private boolean calling = false;
  // Constructor for Superclass
  public NAryView(Object cell) {
    super(cell);
   // cm.putMapping(cell,this);

    // Create Attribute map.

    //this.setAttributes( new java.util.HashMap());//
   // this.setAttributes(this.createAttributes());

  }

  public java.awt.Component getRendererComponent(JGraph jg, boolean b1,
                                                 boolean b2, boolean b3) {
    JTextArea jta=renderer.renderer;
    org.jgraph.graph.AttributeMap m=this.getAllAttributes();
    //jta.setLocation(GraphConstants.getBounds(m).getLocation());


     //jta.setFont(GraphConstants.getFont(this.getAllAttributes()));
    String text = ( (DefaultGraphCell) (this.getCell())).getUserObject().
        toString();
    if (text.equals("null") || text.length() == 0 ) {
      Class objectClass = ( (DefaultGraphCell) (this.getCell())).getUserObject().
          getClass();
      text = "<<" +
          objectClass.getName().substring(objectClass.getName().lastIndexOf(".") +
                                          1) + ">>";
    }
    jta.setText(text);

    jta.validate();

    int length = jta.getFontMetrics(GraphConstants.getFont(this.
        getAllAttributes())).stringWidth(text) + 10;
    int height = jta.getFontMetrics(GraphConstants.getFont(this.
        getAllAttributes())).getHeight();
    jta.setSize(length, height + 5);


    GraphConstants.setSize(m, new Dimension(length, height + 5));
    GraphConstants.setMoveable(m, true);
     Rectangle rec = GraphConstants.getBounds(m).getBounds();
     rec.setSize(new Dimension(length, height + 5));
     GraphConstants.setBounds(m, rec);
     ((DefaultGraphCell)this.getCell()).setAttributes(m);

    jta.setBounds(rec);
    jta.setMargin(new Insets(3, 3, 3, 3));
    jta.setWrapStyleWord(true);
    jta.setLineWrap(true);
    return jta;
  }

  public CellViewRenderer getRenderer() {
    return renderer;
  }


  // Default NAryView Size.
  static public Dimension getSize() {
    return new Dimension(100, 20);

  }

}
