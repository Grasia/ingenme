
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

import org.w3c.dom.*;
import ingenias.exception.*;

public class ProjectProperty implements java.io.Serializable {
	public String name;
	public String value;
	public String tooltip;
	public String key;
	public String module;

	public ProjectProperty(String module,String key,String name,String value,String tt){
		this.key=key;
		this.name=name;
		this.value=value;
		this.tooltip=tt;
		this.module=module;
	}

	public ProjectProperty(ProjectProperty oldProjProperty) {
		this.key=oldProjProperty.key;
		this.name=oldProjProperty.name;
		this.value=oldProjProperty.value;
		this.tooltip=oldProjProperty.tooltip;
		this.module=oldProjProperty.module;		
	}

	public String toXML(){
		String prop="<projectproperty id=\""+key+"\""+
		" module=\""+ingenias.editor.entities.Entity.encodeutf8Text(module)+"\" "+
		" name=\""+ingenias.editor.entities.Entity.encodeutf8Text(name)+
		"\" value=\""+ingenias.editor.entities.Entity.encodeutf8Text(value)+"\" "+
		" tooltip=\""+ingenias.editor.entities.Entity.encodeutf8Text(tooltip)+"\" "+
		"/>\n";

		return prop;
	}

	public static ProjectProperty fromXML(Node n) throws InvalidProjectProperty{

		NamedNodeMap nnm;
		nnm = n.getAttributes();
		if (nnm.getNamedItem("module") == null && nnm.getNamedItem("id")!=null ){
			Log.getInstance().logWARNING("Project has a property which is not properly formatted. Please, add a \"module\" attribute" +
					" to the <projectproperty> tag. Property "+nnm.getNamedItem("id")+ " omitted ");
			throw new InvalidProjectProperty("Some fields are empty, need to declare id, value, and description of the property");
		} else   
			if (nnm.getNamedItem("module") == null ){
				Log.getInstance().logWARNING("Project has a property which is not properly formatted. Please, add a \"module\" attribute" +
				" to the <projectproperty> tag. Property omitted ");
				throw new InvalidProjectProperty("Some fields are empty, need to declare id, value, and description of the property");
			} else 
				if (nnm.getNamedItem("id") == null ||
						nnm.getNamedItem("value") == null ||
						nnm.getNamedItem("tooltip") == null ||
						nnm.getNamedItem("name") == null){
					Log.getInstance().logERROR("Some fields are empty, need to declare id, value, and description of the property. Please, review" +
					" <projectproperty> tags in your project file");
					throw new InvalidProjectProperty("Some fields are empty, need to declare id, value, and description of the property"); 
				} else {

					String key = nnm.getNamedItem("id").getNodeValue();
					String name = nnm.getNamedItem("name").getNodeValue();
					String value = nnm.getNamedItem("value").getNodeValue();
					String tooltip = nnm.getNamedItem("tooltip").getNodeValue();
					String module = nnm.getNamedItem("module").getNodeValue();
					return new ProjectProperty(module,key,name, value, tooltip);
				}

	}
	
	public String toString(){
		return "["+key+","+value+"]";
	}
}
