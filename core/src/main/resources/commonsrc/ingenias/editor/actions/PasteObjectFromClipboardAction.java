
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

package ingenias.editor.actions;

import java.awt.event.ActionEvent;

import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;

public class PasteObjectFromClipboardAction {
	private IDEState ids;
	private GUIResources resources;
	
	public PasteObjectFromClipboardAction(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}
	
	public void paste_actionPerformed(ActionEvent e) {
		ActionEvent e1 = new ActionEvent(this.ids.editor.getGraph(), e.getID(),
				e.getActionCommand(), e.getModifiers());
		if (ids.editor.getGraph()!=null) {
			(this.ids.editor.getGraph().getTransferHandler().getPasteAction()).
			actionPerformed(e1);
			ids.otherChange();
		}	
	}
}
