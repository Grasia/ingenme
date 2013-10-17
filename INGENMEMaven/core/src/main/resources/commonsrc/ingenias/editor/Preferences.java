
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

import ingenias.exception.InvalidProjectProperty;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Preferences {
	public String getSelectedFilter() {
		return selectedFilter;
	}

	public void setSelectedFilter(String selectedFilter) {
		this.selectedFilter = selectedFilter;
	}

	public boolean isFiltersEnabled() {
		return filtersEnabled;
	}

	public void setFiltersEnabled(boolean filtersEnabled) {
		this.filtersEnabled = filtersEnabled;
	}

	public enum DefaultRelationshipView {INGENIAS,UML};
	public enum EditPropertiesMode {POPUP,PANEL};
	public enum ModelingLanguage {UML,INGENIAS};
	public enum RelationshipLayout {AUTOMATIC_STRAIGHT,AUTOMATIC_RADIAL, MANUAL};
	public enum RelationshipsLookAndFeel {LABELS,NOTHING,FULL};


	private DefaultRelationshipView defaultRelationshipView=DefaultRelationshipView.INGENIAS;
	private EditPropertiesMode editPropertiesMode=EditPropertiesMode.PANEL;
	private RelationshipsLookAndFeel relationshipsLookAndFeel=RelationshipsLookAndFeel.NOTHING;
	private ModelingLanguage modelingLanguage=ModelingLanguage.INGENIAS;
	private RelationshipLayout relationshiplayout=RelationshipLayout.AUTOMATIC_STRAIGHT;	
	private String selectedFilter="";
	private boolean filtersEnabled=false;
	private String workspacePath=System.getProperty("user.dir");



	public String getWorkspacePath() {
		return workspacePath;
	}

	public String toXML(){
		StringBuffer sb=new StringBuffer();
		sb.append("<defaultRelationshipView>");
		sb.append(defaultRelationshipView);
		sb.append("</defaultRelationshipView>\n");
		sb.append("<editPropertiesMode>".toLowerCase());
		sb.append(editPropertiesMode);
		sb.append("</editPropertiesMode>\n".toLowerCase());
		sb.append("<relationshipsLookAndFeel>");
		sb.append(relationshipsLookAndFeel);
		sb.append("</relationshipsLookAndFeel>\n");
		sb.append("<relationshiplayout>");
		sb.append(relationshiplayout);
		sb.append("</relationshiplayout>\n");
		sb.append("<modeling>");
		sb.append(modelingLanguage);
		sb.append("</modeling>\n");
		sb.append("<workspace>");
		sb.append(workspacePath);
		sb.append("</workspace>\n");
		
		if (filtersEnabled){
			sb.append("<selectedfilter>");
			sb.append(selectedFilter);
			sb.append("</selectedfilter>\n");
		}
		return sb.toString();
	}

	public static Preferences fromXML(Node n) {
		Preferences prefs=new Preferences();

		NodeList children = n.getChildNodes();
		for (int k=0;k<children.getLength();k++){
			if (children.item(k).getNodeName().equalsIgnoreCase("defaultRelationshipView")){
				prefs.defaultRelationshipView=DefaultRelationshipView.valueOf(children.item(k).getChildNodes().item(0).getNodeValue());
			}
			if (children.item(k).getNodeName().equalsIgnoreCase("editPropertiesMode")){
				prefs.editPropertiesMode=EditPropertiesMode.valueOf(children.item(k).getChildNodes().item(0).getNodeValue());
			}
			if (children.item(k).getNodeName().equalsIgnoreCase("relationshipsLookAndFeel")){
				prefs.relationshipsLookAndFeel=RelationshipsLookAndFeel.valueOf(children.item(k).getChildNodes().item(0).getNodeValue());
			}
			if (children.item(k).getNodeName().equalsIgnoreCase("relationshiplayout")){
				try {
				prefs.relationshiplayout=RelationshipLayout.valueOf(children.item(k).getChildNodes().item(0).getNodeValue());
				} catch (java.lang.IllegalArgumentException iae){
					prefs.relationshiplayout=RelationshipLayout.AUTOMATIC_STRAIGHT;
				}
			}
			if (children.item(k).getNodeName().equalsIgnoreCase("modeling")){
				prefs.modelingLanguage=ModelingLanguage.valueOf(children.item(k).getChildNodes().item(0).getNodeValue());
			}
			if (children.item(k).getNodeName().equalsIgnoreCase("selectedfilter")){
				prefs.filtersEnabled=true;
				prefs.selectedFilter=children.item(k).getChildNodes().item(0).getNodeValue();
			}
			
			if (children.item(k).getNodeName().equalsIgnoreCase("workspace")){				
				prefs.setWorkspacePath(children.item(k).getChildNodes().item(0).getNodeValue());
			}
		}
		return prefs;

	}

	public DefaultRelationshipView getDefaultRelationshipView() {
		return defaultRelationshipView;
	}

	public void setDefaultRelationshipView(
			DefaultRelationshipView defaultRelationshipView) {
		this.defaultRelationshipView = defaultRelationshipView;
	}

	public EditPropertiesMode getEditPropertiesMode() {
		return editPropertiesMode;
	}

	public void setEditPropertiesMode(EditPropertiesMode editPropertiesMode) {
		this.editPropertiesMode = editPropertiesMode;
	}

	public RelationshipLayout getRelationshiplayout() {
		return relationshiplayout;
	}

	public void setRelationshiplayout(RelationshipLayout relationshiplayout) {
		this.relationshiplayout = relationshiplayout;
	}

	public RelationshipsLookAndFeel getRelationshipsLookAndFeel() {
		return relationshipsLookAndFeel;
	}

	public void setRelationshipsLookAndFeel(
			RelationshipsLookAndFeel relationshipsLookAndFeel) {
		this.relationshipsLookAndFeel = relationshipsLookAndFeel;
	}

	public ModelingLanguage getModelingLanguage() {
		return modelingLanguage;
	}

	public void setModelingLanguage(ModelingLanguage modelingLanguage) {
		this.modelingLanguage = modelingLanguage;
	}

	public void setWorkspacePath(String path) {
		this.workspacePath=path;
		
	}

}
