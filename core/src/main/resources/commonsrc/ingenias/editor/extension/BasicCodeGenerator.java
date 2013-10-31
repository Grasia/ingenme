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

import ingenias.editor.ProgressListener;

import java.util.Hashtable;


/**
 *  A code generator is a tool that generates code on been enabled and
 * that has the ability  of verifying the code according to the framework
 * in which this class generates code
 *
 *@author     Jorge J. Gomez Sanz
 *@created    30 November 2003
 */
public interface BasicCodeGenerator extends BasicTool {
	/**
	 *  Determines whether current diagrams contains the information this code
	 *  generator needs. When there are mistakes, the method uses the log
	 *  facilities to inform of errors.
	 *
	 *@return    true in case there is all the information it needs, false i.o.c.
	 */
	public abstract boolean verify();
        
    /**
	 * Similar to the run method but adapted to the needs of the 
	 * INGENIAS Instances Editor
	 *
	 */
	public Hashtable editorrun();
	
	
	/**
	 * It is used to inform of the progress. The actual progress is provided to the Progress Listener.
	 * Other components will attend to these changes and update corresponding progress bars.
	 * @param pl
	 */
	public void setProgressListener(ProgressListener pl);
	
	public void fatalError();


}
