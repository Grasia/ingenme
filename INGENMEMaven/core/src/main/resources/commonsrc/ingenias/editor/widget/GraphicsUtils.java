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
package ingenias.editor.widget;

import java.awt.*;
import javax.swing.*;

public class GraphicsUtils {


 public static Point getCenter(Dimension size){
  Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
  Point result=new Point(
  d.width / 2 - size.width / 2,
  d.height / 2 - size.height / 2);
  return result;
}
  
 public static Point getCenter(Frame parent, Dimension size){
	  Dimension d = parent.getSize();
	  Point result=new Point(parent.getLocation().x+
	  d.width / 2 - size.width / 2,parent.getLocation().y+
	  d.height / 2 - size.height / 2);
	  return result;
	} 
 
	public	static Point getCenter(Component owner, Dimension size){
		Dimension d = owner.getSize();
		Point result=new Point(
				(d.width / 2 - size.width / 2)+owner.getLocation().x,
				(d.height / 2 - size.height / 2)+owner.getLocation().y);
		return result;
	}

public static JWindow showMessageWindow(String message, JWindow parent) {
   JWindow jw = new JWindow(parent);
   JLabel jl = new JLabel(message);
   jl.setFont(new java.awt.Font("Dialog", 1, 36));

   jw.getContentPane().add(jl);
   Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
   jw.pack();
   jw.setLocation(getCenter(d));
/*   jw.setLocation(
       d.width / 2 - jw.getSize().width / 2,
       d.height / 2 - jw.getSize().height / 2);*/

   jw.show();
// This line hangs swing
//            jw.toFront();
   return jw;
 }


}