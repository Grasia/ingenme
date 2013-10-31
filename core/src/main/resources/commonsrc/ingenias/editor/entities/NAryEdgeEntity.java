
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz sobre código original de Rubén Fuentes
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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.jgraph.graph.*;
import ingenias.exception.*;

public class NAryEdgeEntity
    extends Entity
    implements java.io.Serializable {
	
	 public java.lang.String Label;

  // Entities related with this relationship. For each entity its id,
  // role and class is stored. implements java.io.Serializable
  // relatedEntities is a Map of pairs (id, Map) where the Map in the pair
  // contains 2 tuples (role, String) and (class, String).
  private Map<String, Map> relatedEntities;

  public NAryEdgeEntity(String id) {
    super(id);
    getPrefs(null).setView(ViewPreferences.ViewType.INGENIAS);
    relatedEntities = (Map)new Hashtable();
  }

  public NAryEdgeEntity() {
    this("");
  }
  
  public java.lang.String getLabel(){
      return Label;
    }
 public void setLabel(java.lang.String
					Label){
      this.Label=Label;
    }

  // Get the ids of the entities related with this relationship.
  public String[] getIds() {
    Vector tempIds = new Vector();
    Iterator it = relatedEntities.keySet().iterator();
    while (it.hasNext()) {
      tempIds.add(it.next());

    }
    String[] result = new String[tempIds.size()];
    for (int i = 0; i < tempIds.size(); i++) {
      result[i] = tempIds.get(i).toString();

    }
    return result;
  }

  // Get the role played by entity with id in this relationship.
  // It returns null if the id or its role does not exist.
  public String getRole(String id) throws ingenias.exception.NotFound{
    Map attributes = (Map) relatedEntities.get(id);

    if (attributes != null) {
      return ( (String) attributes.get("role"));
    }
    throw new ingenias.exception.NotFound(id +
                          " does not appear in the list of role players in " +
                          this.getId() + this.getType());

  }

  /**
   * Returns the entity associated with a rolename. If there is more than one
   * extreme of relationship with the same rolename, the first is returned
   * @param roleName
   * @return
   * @throws ingenias.exception.NotFound
   */
  public Entity getPlayer(String roleName) throws ingenias.exception.NotFound {
    /*Map attributes = (Map) relatedEntities.get(idcell);
         if (attributes != null){
      return (Entity)attributes.get("entity");
         }*/
    Iterator it = relatedEntities.values().iterator();
  //  Iterator keys = relatedEntities.keySet().iterator();
    Entity result = null;
    boolean found = false;

    while (it.hasNext() && !found) {
      Hashtable m = (Hashtable) it.next();
//      Object key = keys.next();
      if (m.containsKey("role") &&
          m.get("role").toString().equalsIgnoreCase(roleName)) {
        result = (Entity) (m.get("entity"));
        found = true;
      }
//     System.err.println(m.get("role"));
    }
    if (found) {
      return result;
    }
    throw new NotFound(roleName +
                       " does not appear in the list of role players in " +
                       this.getId() + this.getType());

  }

  public Entity getEntity(String id) throws ingenias.exception.NotFound{
    Map attributes = (Map) relatedEntities.get(id);

      if (attributes != null) {
        return ( (Entity) attributes.get("entity"));
      }
      throw new ingenias.exception.NotFound(id +
                            " does not appear in the list of role players in " +
                            this.getId() + this.getType());

    /*Iterator it = relatedEntities.values().iterator();
//    Iterator keys= relatedEntities.keySet().iterator();
    Entity result = null;
    boolean found = false;

    while (it.hasNext() && !found) {
      Hashtable m = (Hashtable) it.next();
//      Object key=keys.next();
      if (m.containsKey("entity") &&
          m.get("entity").toString().equalsIgnoreCase(id)) {
        result = (Entity) m.get("entity");
        found = true;
      }
//     System.err.println(m.get("role"));
    }
    if (found)
    return result;

  throw new ingenias.exception.NotFound(id +
                      " does not appear in the list of role players in " +
                      this.getId() + this.getType());
*/

  }

  public Entity searchEntityID(String id) throws ingenias.exception.NotFound {
  Iterator it = this.relatedEntities.values().iterator();
  boolean found = false;
  Entity result = null;
  String dgcid=null;
  while (it.hasNext() && !found) {
//    dgc = (String) it.next();
    Map m=(Map)it.next();
    result=(Entity) m.get("entity");
    if (result!=null)
    found = result.getId().equals(id);
  }
  if (found) {
    return result;
  }
  throw new ingenias.exception.NotFound();
}


  private Map search(String id) throws ingenias.exception.NotFound {
    Iterator it = this.relatedEntities.keySet().iterator();
    boolean found = false;
    DefaultGraphCell result = null;
    while (it.hasNext() && !found) {
      result = (DefaultGraphCell) it.next();
      found = ( (Entity) result.getUserObject()).getId().equals(id);
    }
    if (found) {
      return (Map)this.relatedEntities.get(result);
    }
    throw new ingenias.exception.NotFound();
  }

  public RoleEntity getRoleEntity(String id) {
    Map attributes = (Map)this.relatedEntities.get(id); //this.search(id);
    if (attributes != null) {
      return ( (RoleEntity) attributes.get("roleentity"));
    }
    else {
      return null;
    }
  }

  public String getClass(String id) throws ingenias.exception.NotFound{
    Map attributes = (Map)this.relatedEntities.get(id); //.search(id);

    if (attributes != null) {
      return ( (String) attributes.get("clas"));
    }

      throw new ingenias.exception.NotFound(id +
                     " does not appear in the list of role players in " +
                     this.getId() + this.getType());
  }

  // Add the object with id tho those involved in this relationship.
  /*  private void addObject(Object id) {
      if (relatedEntities.get(id) == null)
        relatedEntities.put(id, new Hashtable());
    }*/

  // Set the role played by entity with id in this relationship.
  private void setRole(String ent, String roleName) {
    Map attributes = (Map) relatedEntities.get(ent);

    if (attributes != null) {
      attributes.put("role", roleName);
      relatedEntities.put(ent, attributes);
    }
  }

  // Set the class of entity entity with id in this relationship. implements java.io.Serializable
  private void setClass(String id, String className) {
    Map attributes = (Map) relatedEntities.get(id);

    if (attributes != null) {
      attributes.put("clas", className);
      relatedEntities.put(id, attributes);
    }
  }

  // Set the class of entity entity with id in this relationship. implements java.io.Serializable
  private void setRoleEntity(String id, RoleEntity re) {
    Map attributes = (Map) relatedEntities.get(id);

    if (attributes != null) {
      attributes.put("roleentity", re);
      relatedEntities.put(id, attributes);
    }
  }

  // Add the object with id, role and class tho those involved in this relationship. implements java.io.Serializable
  public void addObject(String cellid, Entity ent, RoleEntity re,
                        String roleName, String className) {
    this.addCell(cellid);
    this.addEntity(cellid, ent);
    this.setRole(cellid, roleName);
    this.setClass(cellid, className);
    this.setRoleEntity(cellid, re);
//    System.err.println("added "+cellid+" "+roleName+" "+ent.getId());
  }

  private void addEntity(String id, Entity ent) {
    Map attributes = (Map) relatedEntities.get(id);

    if (attributes != null) {
      attributes.put("entity", ent);
      relatedEntities.put(id, attributes);
    }
  }

  private void addCell(String cell) {
    if (relatedEntities.get(cell) == null) {
      relatedEntities.put(cell, new Hashtable());
    }
  }

  // Add the object with id, role and class tho those involved in this relationship. implements java.io.Serializable
  public void removeObject(String id) {
    if (relatedEntities.get(id) != null) {
      relatedEntities.remove(id);
      ingenias.editor.Log.getInstance().logSYS("removed " + id);
    }

  }

  // To create a connection when the cell is not known. This happens in format
  // of version 1.0. This method requires that old insertion method is maintained
  // though later it must be converted to the new format, this is, modifying the
  // "empty" id that is used
  // DEPRECATED
  public void addObject(Entity ent, RoleEntity re, String roleName,
                        String className) {
    String cellid = "" + ent.hashCode()+roleName.hashCode()+this.relatedEntities.size(); // To obtain new id's
    this.addCell(cellid);
    this.addEntity(cellid, ent);
    this.setRole(cellid, roleName);
    this.setClass(cellid, className);
    this.setRoleEntity(cellid, re);
//    ingenias.editor.Log.getInstance().logSYS("WARNING!!! using a deprecated method");
//    new Exception().printStackTrace();
  }

  public void updateCells(DefaultGraphCell[] gcs) {
    for (int k = 0; k < gcs.length; k++) {
      this.updateCell(gcs[k]);
    }
  }

  public void updateCell(String oldid, String newid) throws NotFound, AlreadyExists{
   Map extremedata= (Map)this.relatedEntities.get(oldid);
   if (extremedata == null)
    throw new NotFound("Could not find "+oldid+" entity in relationship "+
    this.getId()+" of type "+this.getType());
   if (this.relatedEntities.containsKey(newid)){
     throw new AlreadyExists("There is an entry with the id "+newid+ ". Cannot replace it");
   }
   this.relatedEntities.remove(oldid);
   this.relatedEntities.put(newid,extremedata);

}


  public void updateCell(DefaultGraphCell dgc) {
    String[] str = this.getIds();
    for (int k = 0; k < str.length; k++) {
      try {
        Entity ent = this.getEntity(str[k]);
        if (ent.equals(dgc.getUserObject())) {
          Map m = (Map)this.relatedEntities.get(str[k]);
          this.relatedEntities.remove(str[k]);
          this.relatedEntities.put("" + dgc.hashCode(), m);
        }
      }
      catch (NotFound nf) {
        ingenias.editor.Log.getInstance().logSYS(
            "Error updating cells .I found an incorrect entry for id " + str[k] +
            " in " + this.getId() + " of type " + this.getType());
      }
    }
  }
  
  public void fromMap(Map ht){
	  super.fromMap(ht);

	  if (ht.get("Label") instanceof String)
	   this.setLabel(ht.get("Label").toString());



	  }
	  public void toMap(Map ht){
	  super.toMap(ht);

	  if (this.getLabel() instanceof String)
	   ht.put("Label",this.getLabel().toString());


	  }

	public void replace(String id, Entity newent) {
		Iterator<Map> values=this.relatedEntities.values().iterator();
		Iterator<String> ids=this.relatedEntities.keySet().iterator();
		while (values.hasNext()){
			String current=ids.next();
			Map value=values.next();
			if (value.get("entity").equals(newent)){
				value.put("entity",newent);
				ingenias.editor.Log.getInstance().logERROR("entity replaced "+this.getType());
			}
		}
		
	}

  /*public void setObject(Entity oldent, Entity newent){
      if (this.relatedEntities.get(oldent)!=null){
        this.setClass(oldent, newent.getClass().getName());
        Map m = (Map)this.relatedEntities.get(oldent);
        this.relatedEntities.remove(oldent);
        this.relatedEntities.put(newent, m);
      }
    }*/

}
