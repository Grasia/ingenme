
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

public class HorizontalCollectionPanel extends JPanel {
	private boolean settingcollection=false;
	private JPanel box = new JPanel(new GridBagLayout());
	private static Hashtable<TypedVector,Vector> duplicatesCache=new Hashtable<TypedVector,Vector>();
	int counter=0;

	Vector tobeduplicated=new Vector();
	public HorizontalCollectionPanel() {
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
		
		this.setLayout(new BorderLayout());
		super.add(box, BorderLayout.CENTER);
	}

	public HorizontalCollectionPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	public HorizontalCollectionPanel(LayoutManager p0) {
		super(p0);
	}

	public HorizontalCollectionPanel(boolean p0) {
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


	private static Vector duplicate(ingenias.editor.TypedVector data, Vector tobeduplicated) {
		Vector result;
		if (duplicatesCache.containsKey(data)){
			result=duplicatesCache.get(data);
			if (result.size()==data.size()){
				return result;
			}
		}

		ObjectOutputStream oos = null;
		Vector results = new Vector();
		try {        
			java.io.ByteArrayOutputStream sw = new java.io.ByteArrayOutputStream(50000);
			oos = new ObjectOutputStream(sw);


			Enumeration enumeration = tobeduplicated.elements();
			HashSet temp = new HashSet();
			while (enumeration.hasMoreElements()) {
				Component comp = (Component) enumeration.nextElement();
				temp.add(comp);                
			}            
			oos.writeObject(temp);
			oos.close();
			for (int k = 0; k < data.size(); k++) {
				ObjectInputStream decoder = new ObjectInputStream(new java.io.ByteArrayInputStream(sw.toByteArray()));
				temp = (HashSet) decoder.readObject();
				decoder.close();
				results.add(temp);
			}

			duplicatesCache.put(data,results);

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} 
		return results;
	}

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
			gbc.gridx=counter;
			gbc.gridy=0;
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
		this.settingcollection=true;
		this.box.removeAll();
		counter=0;
		Vector duplicates=duplicate(data,tobeduplicated);
		// System.err.println("setting "+attname+" to "+data.size());
		for (int i=0;i<data.size();i++){
			Object current=data.elementAt(i);
			HashSet comps=(HashSet)duplicates.elementAt(i);      
			Hashtable added=this.add(comps);
			//System.err.println(added);
			// System.err.println("adding collection"+added);
			java.lang.reflect.Field[] f=null;
			if (type.isAssignableFrom(current.getClass()))

				f=current.getClass().getFields();
			else
				f=type.getFields();

			for (int k=0;k<f.length;k++){
				Object fvalue=f[k].get(current);

				Component comp=(Component)added.get(attname+"_"+capitalize(f[k].getName())); // obtains the rendering component
				// System.err.println("inspecting "+f[k].getName()+":"+fvalue+" "+attname+"_"+capitalize(f[k].getName())+comp);
				if (comp!=null && fvalue!=null){
					//System.err.println("processing attribute "+f[k].getName()+" with value "+fvalue.toString());
					if (comp instanceof HorizontalCollectionPanel && 
							fvalue instanceof ingenias.editor.TypedVector) {
						// it is another collection and comp another panel
						//  System.err.println("comp: "+comp.getClass()+" "+comp);
						( (HorizontalCollectionPanel) comp).setCollection(f[k].getName(), (TypedVector) fvalue,
								fvalue.getClass());
					}
					else {
						if (comp instanceof javax.swing.text.JTextComponent){
							// it is a simple type. String conversion is invoked
							( (javax.swing.text.JTextComponent) comp).setText(fvalue.toString());
							//  System.err.println("primero:"+fvalue.toString());
						} else {
							String value="";
							if (fvalue instanceof ingenias.editor.TypedVector){
								Enumeration elements=((TypedVector) fvalue).elements();
								for (int j=0;j<((TypedVector) fvalue).size();j++){
									value=value+((TypedVector) fvalue).elementAt(j).toString()+",";
								}
								( (javax.swing.JLabel) comp).setText(value);
							} else 
								( (javax.swing.JLabel) comp).setText(fvalue.toString());

							// System.err.println("segundo:"+fvalue.toString());
						}
					}
				}
			}
		}
		this.settingcollection=false;
	}


	public static void main(String[] args) throws IllegalArgumentException,
	IllegalAccessException {
		System.err.println(GridBagConstraints.VERTICAL);
//		System.err.println(capitalize("capitalize"));
		JFrame jf=new JFrame();

    HorizontalCollectionPanel colpal = new HorizontalCollectionPanel();
    TypedVector tv=new TypedVector(ingenias.editor.entities.Entity.class);
    ingenias.editor.entities.Entity m=new ingenias.editor.entities.Entity("uno");
    m.setId("uno");
    tv.add(m);
    m=new ingenias.editor.entities.Entity("dos");
    m.setId("dos");
    tv.add(m);

    JLabel jl=new JLabel("hola");
    jl.setName("Name");
    JLabelIcon jl1=new JLabelIcon();
    java.net.URL url=colpal.getClass().getResource("images/arrow.gif");
    System.err.println("imagen en "+url);
   // jl1.setIconName(url.toString());
    jl1.setName("Name1");
    colpal.add(jl);
    colpal.add(jl1);
    jf.getContentPane().add(colpal);
    jf.pack();
    jf.show();
    colpal.setCollection("",tv,ingenias.editor.entities.Entity.class );
    jf.pack();
	}
}
