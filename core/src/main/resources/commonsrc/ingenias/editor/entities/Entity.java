
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

import ingenias.editor.entities.ViewPreferences.ViewType;
import ingenias.exception.WrongConversion;

import java.util.*;
import java.io.*;


public class Entity  implements java.io.Serializable, Comparable {

  public String id;
  public String type;
  public static String helprec="";
  public String helpdesc="";
  public ViewPreferences prefs=new ViewPreferences();
 

  public Entity(String id) {
   this.id=id;
  }

  public String getId(){
   return id;
  }

  public void setId(String id){
  this.id=id;
  }

  public void setPrefs(ViewPreferences prefs){
   this.prefs=prefs;
  }

  public void setHelpdesc(String helpdesc){
   this.helpdesc=helpdesc;
  }



  public String toString(){
   return id;
  }

  public void toMap(Map ht){

 //  ht.put("id",this.getId());
   ht.put("_view_type",this.prefs.view.toString());

  }

  public void fromMap(Map m){
    try {
    	if (m.get("_view_type")!=null)
    	prefs.view=prefs.view.fromString(m.get("_view_type").toString());
    	
	} catch (WrongConversion e) {
		e.printStackTrace();
	}
  }

  public String getType(){
   String className=this.getClass().getName();
   int index=className.lastIndexOf(".");
   return className.substring(index+1,className.length());
  }

  public boolean equals(Object object){

    if (object instanceof Entity)
      return ((Entity)object).getId().equalsIgnoreCase(this.getId());
    return super.equals(object);

  }

  public int hashCode(){
    return this.getId().hashCode();
  }


  public static String decodeSpecialSymbols(String text){
  try {
  String s=text;
  s=ingenias.generator.util.Conversor.restoreInvalidChar(text);

  return  s;
} catch (Exception uee){
  uee.printStackTrace();
}
return "";
}


  public static String encodeutf8Text(String text){
  try {
    java.io.ByteArrayOutputStream ba=new java.io.ByteArrayOutputStream();
    OutputStreamWriter osw=new OutputStreamWriter(ba,"UTF-8");
    osw.write(text);
    osw.close();

    String s=new String(ba.toByteArray(),"UTF-8");
    s=text;
    s=ingenias.generator.util.Conversor.replaceInvalidChar(s);

//    s=new RE("\\").subst(s,"#2f;#2f;");

    StringBuffer sb=new StringBuffer(s);

/*    int index=sb.toString().indexOf("&");
    while (index>=0){
      String temp=sb.toString();
//      System.err.println(index+":"+temp);

     if (!(temp.indexOf("&quot;",index)==index ||
        temp.indexOf("&apos;",index)==index ||
        temp.indexOf("&gt;",index)==index ||
        temp.indexOf("&lt;",index)==index ||
        temp.indexOf("&amp;",index)==index)){
//          System.err.println("deleting");
        sb.deleteCharAt(index);
        sb.insert(index,"&amp;");
//        System.err.println("after\n"+sb);
        } else
      index=sb.toString().indexOf("&",index+1);
    }
    s=sb.toString();
*/
    return  s;
  } catch (Exception uee){
    uee.printStackTrace();
  }
  return "";
}

 public static String decodeutf8Text(String text){
	 return text;
  /*StringBuffer sb=new StringBuffer();
  try {
    java.io.StringBufferInputStream ba=new   java.io.StringBufferInputStream(text);
    InputStreamReader isr=new InputStreamReader(ba,"UTF8");
    int cont=0;
    while (cont!=-1){
      cont=isr.read();
      if (cont!=-1)
        sb.append((char)cont);
    }

    isr.close();
//    System.err.println(sb.toString());
    return  sb.toString();
  } catch (Exception uee){
//    System.err.println(text);
    uee.printStackTrace();
  }
  return text;*/
  }

  public void setHelpDesc(String desc){
    this.helpdesc=desc;
  }

  public String getHelpDesc(){
    return this.helpdesc;
  }

  public void setHelpRecom(String rec){
    this.helprec=rec;
  }

  public String getHelpRecom(){
    return this.helprec;
  }

public int compareTo(Object o) {
	if (o instanceof Entity){
		Entity e=(Entity)o;
		return this.getId().compareTo(e.getId());
	}
	return 0;
}

/*public ViewPreferences getPrefs() {
	return prefs;
}*/

public ViewPreferences getPrefs(Map m) {	
	if (m!=null && m.containsKey("view")){
		ViewPreferences vp=new ViewPreferences();
		try {
			vp.setView(ViewPreferences.ViewType.fromString(m.get("view").toString()));
		} catch (WrongConversion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vp;
	} else 
		if (m==null){
			ViewPreferences vp=new ViewPreferences();
			vp.setView(ViewType.INGENIAS); // there
			
			return vp;
		}
		
	return prefs;
}

}
