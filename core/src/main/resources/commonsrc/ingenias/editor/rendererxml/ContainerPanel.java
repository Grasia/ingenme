
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.border.*;
import java.util.*;
import java.beans.*;
import java.io.*;
import ingenias.editor.*;
import javax.print.attribute.HashAttributeSet;
import javax.swing.*;

public class ContainerPanel extends CollectionPanel {
	private boolean settingcollection=false;
	private JPanel box = new JPanel(new GridBagLayout());
	private static Hashtable<TypedVector,Vector> duplicatesCache=new Hashtable<TypedVector,Vector>();
	int counter=0;

	Vector tobeduplicated=new Vector();
	public ContainerPanel() {
		super();
		/*final JScrollPane jsp=new JScrollPane(box,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		jsp.addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent arg0) {}
			public void componentMoved(ComponentEvent arg0) {}
			public void componentResized(ComponentEvent arg0) {
				box.revalidate();
				jsp.revalidate();		
				System.out.println("revalidating1");
			}
			public void componentShown(ComponentEvent arg0) {}});
		
		this.addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent arg0) {}
			public void componentMoved(ComponentEvent arg0) {}
			public void componentResized(ComponentEvent arg0) {
				box.revalidate();
				revalidate();		
				System.out.println("revalidating2");
			}
			public void componentShown(ComponentEvent arg0) {}});*/
		
	
		this.removeAll();
	
	}

	public ContainerPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	public ContainerPanel(LayoutManager p0) {
		super(p0);
	}

	public ContainerPanel(boolean p0) {
		super(p0);

	}

	public void add(Component comp,Object name){
		// System.err.println("adding3 "+name.toString()+" "+comp.getClass().getName());
		super.add(comp,name);
	}


	public Component add(String name,Component comp){
		// System.err.println("adding2 "+name+" "+comp.getClass().getName());
		return super.add(name,comp);
	}

	public Component add(Component comp){
		//System.err.println("adding1 "+comp.getClass().getName()+" "+comp.getName());
		if (!settingcollection){
			// System.err.println("to be duplicated");
			tobeduplicated.add(comp);
		}
		return comp;
		//  return super.add(comp);
	}

/*
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(200,200);
	}
*/
	

	public Vector fold(Component cont){
		Vector result=new Vector();
		result.add(cont);
		if (cont instanceof Container){
			Container container=(Container)cont;
			Component[] components=container.getComponents();
			for (int k=0;k<components.length;k++){
				result.addAll(fold(components[k]));
			}
		}
		return result;
	}

	public Hashtable add(HashSet comps){
		//System.err.println("adding hashset");
		Iterator it=comps.iterator();
		Hashtable ht=new Hashtable();
		while(it.hasNext()){
			Component comp=(Component)it.next();      
			Vector folded=fold(comp);
			Iterator it1=folded.iterator();
			while (it1.hasNext()){
				Component current=(Component)it1.next();
				// System.err.println(current);  
				if (current.getName()!=null){
					//   System.err.println("adding "+current.getName());
					ht.put(current.getName(), current);
				}

			}
			GridBagConstraints gbc=new GridBagConstraints();
			gbc.gridx=0;
			gbc.gridy=counter;
			gbc.fill=GridBagConstraints.BOTH;
			gbc.weighty=1;
			gbc.weightx=1;
			this.box.add(comp,gbc);
			counter++;
		}
		return ht;
	}

	public static String capitalize(String string){
		if (string!=null && !string.equals(""))
			return string.substring(0,1).toUpperCase()+string.substring(1,string.length());
		else
			return "";
	}

	public void setCollection(String attname,ingenias.editor.TypedVector data, Class type) throws
	IllegalAccessException, IllegalArgumentException {
		
	}


	public static void main(String[] args) throws IllegalArgumentException,
	IllegalAccessException {
		System.err.println(GridBagConstraints.VERTICAL);
//		System.err.println(capitalize("capitalize"));
		/*JFrame jf=new JFrame();

    CollectionPanel colpal = new CollectionPanel();
    TypedVector tv=new TypedVector(ingenias.editor.entities.Method.class);
    ingenias.editor.entities.Method m=new ingenias.editor.entities.Method("uno");
    m.setName("uno");
    tv.add(m);
    m=new ingenias.editor.entities.Method("dos");
    m.setName("dos");
    tv.add(m);

    JLabel jl=new JLabel("hola");
    jl.setName("Name");
    JLabelIcon jl1=new JLabelIcon();
    java.net.URL url=colpal.getClass().getResource("images/method.gif");
    System.err.println("imagen en "+url);
    jl1.setIconName(url.toString());
    jl1.setName("Name1");
    colpal.add(jl);
    colpal.add(jl1);
    jf.getContentPane().add(colpal);
    jf.pack();
    jf.show();
    colpal.setCollection("",tv,ingenias.editor.entities.Method.class );
    jf.pack();*/
	}
}
