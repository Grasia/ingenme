package ingenias.editor.utils;

import ingenias.editor.IDEState;
import ingenias.editor.ModelJGraph;
import ingenias.editor.entities.Entity;
import ingenias.exception.InvalidAttribute;
import ingenias.exception.InvalidEntity;
import ingenias.exception.NotFound;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.AttributedElement;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.BrowserImp;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphAttributeFactory;
import ingenias.generator.browser.GraphAttributeImp;
import ingenias.generator.browser.GraphCollectionImp;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphEntityFactory;
import ingenias.generator.browser.GraphEntityImp;
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.browser.GraphRelationshipFactory;
import ingenias.generator.browser.GraphRole;
import ingenias.generator.browser.GraphRoleImp;
import ingenias.generator.browser.RelationshipFactory;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;

import org.jgraph.graph.DefaultGraphCell;

public class DiagramManipulation {
	
	
	public static void moveRelsFromTo(DefaultGraphCell from, DefaultGraphCell to, ModelJGraph mjg, IDEState state){
		List childrenFrom = from.getChildren();
		Vector copyChildren=new Vector(childrenFrom);
		for (Object child:copyChildren){
			to.add((MutableTreeNode) child);
		}
		mjg.refresh();
		/*
	
		try {
			
			
			GraphEntityImp oldEntity=new GraphEntityImp((Entity) from.getUserObject(),mjg,state);
			GraphEntityImp newEntity=new GraphEntityImp((Entity) to.getUserObject(),mjg,state);
			DiagramManipulation.replaceInOneDiagram(new GraphEntityFactory(state),
						new GraphRelationshipFactory(state),
						new GraphAttributeFactory(state),
						oldEntity,
						newEntity,
						new BrowserImp(state));
		} catch (NullEntity e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidEntity e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAttribute e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	
	public static void replace(DefaultGraphCell from, DefaultGraphCell to, ModelJGraph mjg, IDEState state){
		try {
			GraphEntityImp oldEntity=new GraphEntityImp((Entity) from.getUserObject(),mjg,state);
			GraphEntityImp newEntity=new GraphEntityImp((Entity) to.getUserObject(),mjg,state);
			DiagramManipulation.replace(new GraphEntityFactory(state),
						new GraphRelationshipFactory(state),
						new GraphAttributeFactory(state),
						oldEntity,
						newEntity,
						new BrowserImp(state));
		} catch (NullEntity e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidEntity e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAttribute e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	


	public static void replace(GraphEntity oldEntity, 
			GraphEntity newEntity,
			AttributedElement source,GraphAttributeFactory gaf) throws NullEntity, InvalidAttribute {
		GraphAttribute[] attrs = source.getAllAttrs();		
		for (GraphAttribute attr:attrs){
			if (attr.isCollectionValue()){
				if (attr.getCollectionValue()!=null){
					GraphCollectionImp colectionValue = (GraphCollectionImp)attr.getCollectionValue();
					for (int k=0;k<colectionValue.size();k++){
						if (colectionValue.getElementAt(k).getID().equals(oldEntity.getID())){
							colectionValue.removeElementAt(k);
							colectionValue.addElementAt(k, newEntity);
						} else {
							replace(oldEntity,newEntity,colectionValue.getElementAt(k),gaf);
						}
					}
				}				
			} else				
				if (attr.isEntityValue() && attr.getEntityValue()!=null){
					if (attr.getEntityValue().getID().equals(oldEntity.getID())){
						GraphAttributeImp gai=(GraphAttributeImp) attr;						
						gai.setValue(newEntity);
						GraphAttribute ngat=gaf.createAttribute(
								gai.getName(), 
								newEntity, oldEntity.getGraph());
						source.setAttribute(ngat);
					} else
						replace(oldEntity,newEntity,attr.getEntityValue(),gaf);
				}
		}
	}

	public static void replace(
			GraphEntityFactory gef, 
			GraphRelationshipFactory rf, 
			GraphAttributeFactory gaf, 
			GraphEntity oldEntity, GraphEntity newEntity,Browser browser) throws NullEntity, NotFound, InvalidEntity, InvalidAttribute {
		GraphEntity[] alents = browser.getAllEntities();		
		for (GraphEntity ent:alents){			
			replace(oldEntity,newEntity,ent,gaf);
			Vector<GraphRelationship> rels = ent.getAllRelationships();
			replaceEntityInRelationships(gef, rf, gaf, oldEntity, newEntity,
					browser, rels);			
		}
	}
	

	public static void replaceInOneDiagram(
			GraphEntityFactory gef, 
			GraphRelationshipFactory rf, 
			GraphAttributeFactory gaf, 
			GraphEntity oldEntity, GraphEntity newEntity,
			Browser browser) throws NullEntity, NotFound, InvalidEntity, InvalidAttribute {
		
			GraphRelationship[] rels = oldEntity.getRelationships();
			Vector<GraphRelationship> grels=new Vector<GraphRelationship>();
			for (GraphRelationship rel:rels)
				grels.add(rel);
			replaceEntityInRelationships(gef, rf, gaf, oldEntity, newEntity,
					browser, grels);			
		
	}



	private static void replaceEntityInRelationships(GraphEntityFactory gef,
			GraphRelationshipFactory rf, GraphAttributeFactory gaf,
			GraphEntity oldEntity, GraphEntity newEntity, Browser browser,
			Vector<GraphRelationship> rels) throws NullEntity,
			InvalidAttribute, InvalidEntity, NotFound {
		for (GraphRelationship rel:rels){
			replace(oldEntity,newEntity,rel,gaf);
			GraphRole[] roles = rel.getRoles();				
			for (GraphRole role:roles){
				if (role.getPlayer().getID().equals(oldEntity.getID())){
					((GraphRoleImp)role).setPlayer(newEntity.getEntity());
					Graph diagram = rel.getGraph();						
					gef.reuseEntity(newEntity.getID(),diagram);
					GraphRole[] rolesRel = rel.getRoles();
					String[] rolePlayers=new String[rolesRel.length];
					String replacedRole="";
					for (int k=0;k<rolePlayers.length;k++){
						rolePlayers[k]=rolesRel[k].getPlayer().getID();
						if (rolePlayers[k].equals(oldEntity.getID())){
							rolePlayers[k]=newEntity.getID();
							replacedRole=rolesRel[k].getName();
						}
					}
					Vector<Hashtable<String, String>> assignments = null;
					assignments = 
							rf.getPossibleRoleAssignment(rel.getType(), 
									rolePlayers, diagram, browser);	
					
					boolean found=false;
					int k=0;
					Hashtable<String, String> selectedAssignment=null;
					for (Hashtable<String, String> assignment:assignments){
						boolean allCorrect=true;
						for (int j=0;j<rolesRel.length;j++){
							String assigEntityId = assignment.get(rolesRel[j].getName());
							String oldAssigEntityId=rolesRel[j].getPlayer().getID();
							allCorrect= allCorrect&& assigEntityId.equals(oldAssigEntityId) ||
									(assigEntityId.equals(newEntity.getID()) && replacedRole.equals(rolesRel[j].getName()));										
						}
						if (allCorrect)
							selectedAssignment=assignment;
					}													
					GraphRelationship nrel = rf.createRelationship(rel.getType(), diagram, selectedAssignment);
					replaceAttributes(gaf, rel, nrel);
					GraphRole[] oldRoles = rel.getRoles();
					GraphRole[] newRoles = nrel.getRoles();
					for (GraphRole oldRole:oldRoles){
						GraphAttribute[] attsOldRole = oldRole.getAllAttrs();
						int j=0;
						while (!found && j<newRoles.length){
							if (newRoles[j].getName().equalsIgnoreCase(oldRole.getName())){
								replaceAttributes(gaf,oldRole,newRoles[j]);
								found=true;
							}
							j++;
						}
					}
					diagram.remove(rel);
				}					
			}
			
		}
	}

	private static void replaceAttributes(GraphAttributeFactory gaf,
			AttributedElement oldElement, AttributedElement newElement) throws NullEntity,
			InvalidAttribute {
		GraphAttribute[] attsRel=oldElement.getAllAttrs();
		for (GraphAttribute gat:attsRel){
			if (gat.isEntityValue() && gat.getEntityValue()!=null ||
					gat.isCollectionValue() && gat.getCollectionValue().size()>0 ||
					gat.isSimpleValue() && gat.getSimpleValue()!=null && 
					!gat.getSimpleValue().equals("") && !gat.getName().equalsIgnoreCase("id") &&
					!gat.getName().equalsIgnoreCase("type")
					)								
			gaf.setAttribute(newElement, gat);		

				
		}
	};

}
