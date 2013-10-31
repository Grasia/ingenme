

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

package ingenias.editor.rendererxml;

import javax.swing.JPanel;
import java.awt.*;
import javax.swing.border.*;
import java.util.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ingenias.editor.*;
import ingenias.editor.entities.Entity;

import javax.swing.*;

public class AttributesPanel extends JComponent {
  private boolean settingcollection=false;
  private Box box = Box.createVerticalBox();

  Vector tobeduplicated=new Vector();
  
  public AttributesPanel() {
    this.setLayout(new BorderLayout());
    super.add(box, BorderLayout.CENTER);
  }
  
  public void setEntity(java.lang.Object ent){
	  Method[] methods = ent.getClass().getDeclaredMethods();
	  box.removeAll();
	  for (Method m: methods){
		  if (m.getName().toLowerCase().startsWith("get")){
			  String attName=m.getName().substring(3);
			  Object result;
			try {
				result = m.invoke(ent,new Object[]{});
				String value="null";
				  if (result!=null)
					  value=result.toString();			  
				  JPanel attPane=new JPanel(new FlowLayout(FlowLayout.LEFT));
				  attPane.add(new JLabel(attName+" : "+m.getReturnType().getName()+" = "+value));			  
				  box.add(attPane);
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				
				e.printStackTrace();
			}			  
		  }
	  }
  }

  

  public static void main(String[] args) throws IllegalArgumentException,
      IllegalAccessException {

  }
}
