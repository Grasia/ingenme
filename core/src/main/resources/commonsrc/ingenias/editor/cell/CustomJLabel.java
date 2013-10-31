
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

import javax.swing.*;


/**
 * <p>T�tulo: </p>
 * <p>Descripci�n: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Empresa: </p>
 * @author sin atribuir
 * @version 1.0
 */
import ingenias.editor.entities.*;

public class CustomJLabel extends JLabel {
  private Entity ent;
  public CustomJLabel(Entity ent) {
    super("");
    this.ent=ent;
    this.setText(ent.getId());
  }


  public String getText(){
    if (ent!=null){
      return ent.toString();
    } else
      return "";
  }

  public String toString(){
    return ent.toString();
  }

  public static void main(String args[]){
   JFrame jp= new JFrame();
   Entity mient=new Entity("1");
   CustomJLabel cl=new CustomJLabel(mient);
   jp.getContentPane().add(cl);
   jp.pack();
   jp.show();
   mient.setId("3");
   cl.repaint();
  }

}