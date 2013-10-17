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

import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;
import ingenias.editor.IDEUpdater;
import ingenias.editor.ProjectProperty;
import ingenias.exception.NotInitialised;
import ingenias.generator.browser.Browser;
import java.util.*;

import ingenias.generator.datatemplate.*;
import ingenias.generator.interpreter.TemplateTree;

import java.io.*;
import ingenias.generator.browser.*;

/**
 *  This interface describe the activation method of tools
 *
 *@author     Jorge J. Gomez-Sanz
 *@created    30 November 2003
 */

public abstract class BasicToolImp
        implements BasicTool {
    private Properties prop = new Properties();
    public Browser browser=null;
    public IDEUpdater ideUpdater=null;
    
	public IDEUpdater getIdeUpdater() {
		return ideUpdater;
	}
	public void setIdeUpdater(IDEUpdater ideUpdater) {
		this.ideUpdater = ideUpdater;
	}
	private IDEState ids;
	public IDEState getIds() {
		return ids;
	}
	public void setIds(IDEState ids) {
		this.ids = ids;
	}
	public GUIResources getResources() {
		return resources;
	}
	public void setResources(GUIResources resources) {
		this.resources = resources;
	}
	private GUIResources resources;
    
    /**
     *  A description of the purpose of this tool
     *
     *@return    The description
     */
    
    public abstract String getDescription();
    
    /**
     *  The name of this tool. This name will be included in the INGENIAS
     *  tool
     *
     *@return    The name
     */
    
    public abstract String getName();
    
    /**
     *  Creates a code generator that reuses that reuses an existing browser
     * directly. This constructor is invoked when launching a tool
     * from whithin the IDE.
     */
    public BasicToolImp(Browser browser) {
    	 this.prop=browser.getState().prop;
    	 
    	for (ProjectProperty pp: this.defaultProperties()){
        	this.putProperty(pp);
        }
        
            this.browser=browser;
           
        
    }
    
    /**
     *  Creates a code generator that initialises from scratch a browser.
     * This constructor is invoked when launching a stand alone version. When
     * the initialisation is made from a file, no default properties are retrieved.
     */
    
    public BasicToolImp(String file) throws ingenias.exception.UnknowFormat,
            ingenias.exception.DamagedFormat,
            ingenias.exception.CannotLoad {
        try {
        	        	
            browser=ingenias.generator.browser.BrowserImp.initialise(file);
            Set<Object> keys =browser.getState().prop.keySet();
            this.prop=browser.getState().prop;
            for (ProjectProperty pp: this.defaultProperties()){
            	this.putProperty(pp);
            }
            /*for (Object key:keys){
            	ProjectProperty pp=((ProjectProperty)ingenias.generator.browser.BrowserImp.getInstance().getState().prop.get(key));
            	this.putProperty(pp);
            }*/
            
            /*for (ProjectProperty pp: this.defaultProperties()){
            	this.putProperty(pp);
            }*/
                        
        } catch (Throwable e){
            e.printStackTrace();
        }
    }
    
    public Browser getBrowser(){
        return this.browser;
    }
    
    protected String getTrace(Exception e){
        StackTraceElement[] stes=e.getStackTrace();
        StringBuffer result=new StringBuffer();
        int k=1;
        e.printStackTrace();
        while (k<stes.length && !stes[k].getFileName().equals("IDE.java")){
            result.append(stes[k].getFileName()+" at "+stes[k].getLineNumber()+"\n");
            k=k+1;
        }
        return result.toString();
    }
    
    /**
     *  It provides the tool with the configuration properties it needs
     *
     *@param  p  Properties supplied
     */
    public void setProperties(Properties p) {
        this.prop = (Properties) p.clone();
    }
    
    /**
     *  Obtains the properties associated with this tool. Each property is a tuple
     *  (String key, ProjectProperty value)
     *
     *@return    Properties needed by this tool
     */
    public Properties getProperties() {
        return (Properties)this.prop.clone();
    }
    
    
    /**
     *  Main processing method for the BasicCodeProc object
     *
     *@param  b  Description of Parameter
     */
    abstract public void run();
    
    /**
     *  Defines the basic properties of a code generator. Required Properties in
     *  this kind of elements are:
     *
     *@return    Description of the Returned Value
     */
    
    protected abstract Vector<ProjectProperty> defaultProperties(); 
    
	/**
	 * It obtains a property given its id 
	 * 
	 * @param id The id of the property
	 * @return the property or null if none exist
	 */
	public ProjectProperty getProperty(String id){
		return (ProjectProperty) this.prop.get(this.getName()+":"+id);
	}
	
	public void setProperty(String id, String value){
		((ProjectProperty) this.prop.get(this.getName()+":"+id)).value=value;
	}

	
	/**
	 * It adds a new property 
	 * 
	 * @param pp The property to add
	 * 
	 */
	public void putProperty(ProjectProperty pp){
		if (!this.prop.containsKey(this.getName()+":"+pp.key))
			this.prop.put(this.getName()+":"+pp.key, pp);	
	}

}