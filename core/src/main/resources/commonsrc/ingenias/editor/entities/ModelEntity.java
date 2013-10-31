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

package ingenias.editor.entities;
import java.util.*;


public class ModelEntity extends Entity  implements java.io.Serializable {

  public String modelID;
  public String modelType;

  public ModelEntity(String id) {
    super(id);
  }

  public String getModelID(){
    return modelID;
  }

  public void setModelID(String modelID){
    this.modelID=modelID;
  }

  public void setModelType(String modelType){
    this.modelType=modelType;
  }

  public String getModelType(){
    return this.modelType;
  }


public void fromMap(Map ht){
super.fromMap(ht);
if (ht.get("ModelID")!=null)
 this.setModelID(ht.get("ModelID").toString());
if (ht.get("ModelType")!=null)
this.setModelType(ht.get("ModelType").toString());


}

public void toMap(Map ht){
super.toMap(ht);
if (modelID!=null)
ht.put("ModelID",modelID);
if (modelType!=null)
ht.put("ModelType",modelType);


}

}