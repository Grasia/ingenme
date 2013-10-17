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
package ingenias.editor.entities;

import ingenias.exception.WrongConversion;

public class ViewPreferences implements java.io.Serializable{
	ViewType view=ViewType.INGENIAS;
	
	public enum ViewType {NOICON,UML,INGENIAS,LABEL;

	public static ViewType fromString(String string) throws WrongConversion {
		if (string.toLowerCase().equals("label")){
			return ViewType.LABEL;
		}
		if (string.toLowerCase().equals("ingenias")){
			return ViewType.INGENIAS; 
		}
		
		if (string.toLowerCase().equals("uml")){
			return ViewType.UML;
		}
		if (string.toLowerCase().equals("noicon")){
			return ViewType.NOICON;
		}
		throw new ingenias.exception.WrongConversion();
	}}

	public ViewType getView() {
		return view;
	}

	public void setView(ViewType view) {
		this.view = view;
	};

}
