
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

 
package ingenias.editor;

import java.util.Vector;

import ingenias.editor.filters.DiagramFilter;

import javax.swing.*;

public class FilteredJToolBar extends JToolBar {

	private DiagramFilter filter=null;
	Vector<JButton> originalButtons= new Vector<JButton>();
	private String diagramType;
	private Object lastFilter;

	public FilteredJToolBar(String diagramType){
		super(JToolBar.VERTICAL);
		this.diagramType=diagramType;
	}

	public DiagramFilter getFilter() {
		return filter;
	}


	public void applyFilter(DiagramFilter filter){
		if (originalButtons.isEmpty()){
			int k=0;
			while (this.getComponentAtIndex(k)!=null){
				if (this.getComponentAtIndex(k) instanceof JButton){
					originalButtons.add((JButton) this.getComponentAtIndex(k));
				}
				k=k+1;
			}				
		}
		if (lastFilter==null || !lastFilter.equals(filter)){
			lastFilter=filter;
			this.removeAll();
			for (JButton button:originalButtons){
				if (filter.isValidEntity(diagramType, button.getName())){
					this.add(button);
				}
			}
			this.invalidate();
			this.validate();
			this.repaint();	
		}
	}			

}
