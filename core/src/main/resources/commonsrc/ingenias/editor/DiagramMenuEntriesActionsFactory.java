
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


import java.awt.Point;
import java.util.Vector;

import javax.swing.AbstractAction;

import org.jgraph.graph.DefaultGraphCell;

public abstract class DiagramMenuEntriesActionsFactory {
	 private GUIResources resources;
	private IDEState state;
	protected abstract Vector<AbstractAction> createChangeViewActions(final DefaultGraphCell cell, final ModelJGraph graph);
     public DiagramMenuEntriesActionsFactory(GUIResources resources, IDEState state){
    	 this.resources=resources;
    	 this.state=state;
     }
     
     protected IDEState getState(){
    	 return state;
     }
          
	abstract protected Vector<AbstractAction> createDiagramSpecificInsertActions(Point point, ModelJGraph graph) ;
}
