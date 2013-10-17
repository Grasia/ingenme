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

package ingenias.editor.extension;

import ingenias.editor.ProjectProperty;
import ingenias.generator.interpreter.TemplateTree;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

/**
 *  This interface describe the activation method of tools
 *
 *@author     Jorge J. Gomez-Sanz
 *@created    30 November 2003
 */
public interface BasicTool {

	/**
	 *  A description of the purpose of this tool
	 *
	 *@return    The description
	 */
	public String getDescription();


	/**
	 *  The name of this tool. This name will be included in the INGENIAS
         *  tool
	 *
	 *@return    The name
	 */
	public String getName();
	
	/**
	 *  The version of this tool. 
	 *
	 *@return    The name
	 */
	public String getVersion();


	/**
	 *  Enables the tool by providing an interface to traverse graphs defined with
	 *  the IDE
	 *
	 */
	public void run();
	


	/**
	 *  Obtains the properties associated with this tool. Each property is a tuple
	 *  (String key, ProjectProperty value)
	 *
	 *@return    Properties needed by this tool
	 */
	public Properties getProperties();
	
	/**
	 * It obtains a property given its id 
	 * 
	 * @param id The id of the property
	 * @return the property or null if none exist
	 */
	public ProjectProperty getProperty(String id);
	
	/**
	 * It adds a new property 
	 * 
	 * @param pp The property to add
	 * 
	 */
	public void putProperty(ProjectProperty pp);


	/**
	 *  It provides the tool with the configuration properties it needs
	 *
	 *@param  p  Properties supplied
	 */
	public void setProperties(Properties p);

}
