
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

import java.net.URL;
import java.net.URLClassLoader;
import java.net.JarURLConnection;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.Attributes;
import java.util.*;
import java.io.*;
import java.util.zip.*;

/**
 *
 *
 *@author     developer
 *@created    30 November 2003
 */
public class ImageLoader
   {
  private URL url;
  private static Vector reviewedFiles = new Vector();

  /**
   *  Creates a new JarClassLoader for the specified url.
   *
   *@param  url  the url of the jar file
   */

  public static java.awt.Image getImage(String path){
   ImageLoader img=new ImageLoader();
   URL url= img.getClass().getClassLoader().getResource(path);
   java.awt.Image image=null;
   if (url!=null){
     image=java.awt.Toolkit.getDefaultToolkit().getImage(url);
   } else {
     image=java.awt.Toolkit.getDefaultToolkit().getImage(path);
   }
   return image;
  }



}
