
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
import java.util.Vector;

import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;
import ingenias.editor.entities.Entity;
import ingenias.editor.entities.ViewPreferences;

public class SwitchViewsAction {
	private IDEState ids;
	private GUIResources resources;
	
	public SwitchViewsAction(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}
	
	public void switchUMLView_actionPerformed(ActionEvent e) {
		this.enableUMLView_actionPerformed(e);
		Vector<Entity> entities=this.ids.om.getAllObjects();
		for (int k=0;k<entities.size();k++){
			entities.elementAt(k).getPrefs(null).setView(ViewPreferences.ViewType.UML);
		}

	}
	public void switchINGENIASView_actionPerformed(ActionEvent e) {
		this.enableINGENIASView_actionPerformed(e);
		Vector<Entity> entities=this.ids.om.getAllObjects();
		for (int k=0;k<entities.size();k++){
			entities.elementAt(k).getPrefs(null).setView(ViewPreferences.ViewType.INGENIAS);
		}
	}
	
	public void enableUMLView_actionPerformed(ActionEvent e) {
		ids.prefs.setModelingLanguage(ingenias.editor.Preferences.ModelingLanguage.UML);
		resources.getEnableUMLView().setSelected(true);

		/*Enumeration rels=RelationshipManager.getRelationships(this.ids.gm);
	   Enumeration relscells=RelationshipManager.getRelationshipsCells(this.ids.gm);
	   Enumeration relsmodels=RelationshipManager.getRelationshipsModels(this.ids.gm);
	   while (rels.hasMoreElements()){
		   Entity ent=(Entity)rels.nextElement();
		   NAryEdge cell=(NAryEdge)relscells.nextElement();
		   ModelJGraph mjg=(ModelJGraph)relsmodels.nextElement();
		   ent.getPrefs().setView(ViewPreferences.ViewType.UML);
		   RenderComponentManager.setRelationshipView(ViewPreferences.ViewType.UML,ent,cell,mjg);
	   }*/
	}

	public void enableINGENIASView_actionPerformed(ActionEvent e) {
		ids.prefs.setModelingLanguage(ingenias.editor.Preferences.ModelingLanguage.INGENIAS);
		resources.getEnableINGENIASView().setSelected(true);
		/*Enumeration rels=RelationshipManager.getRelationships(this.ids.gm);
		Enumeration relscells=RelationshipManager.getRelationshipsCells(this.ids.gm);
		Enumeration relsmodels=RelationshipManager.getRelationshipsModels(this.ids.gm);
		while (rels.hasMoreElements()){
			Entity ent=(Entity)rels.nextElement();
			NAryEdge cell=(NAryEdge)relscells.nextElement();
			ModelJGraph mjg=(ModelJGraph)relsmodels.nextElement();
			ent.getPrefs().setView(ViewPreferences.ViewType.INGENIAS);
			RenderComponentManager.setRelationshipView(ViewPreferences.ViewType.INGENIAS,ent,cell,mjg);
		}*/
	}
}
