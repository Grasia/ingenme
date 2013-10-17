package ingenias.editor.rendererxml;

import javax.swing.JLabel;
import javax.swing.Icon;
import java.awt.*;
import java.net.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


public class JLabelIcon extends JLabel {



  public  String iconName="";


  public JLabelIcon() {

  }



  public String getIconName(){
   return iconName;
  }

  public void setIconName(String name){
    try {
      super.setIcon(new javax.swing.ImageIcon(new java.net.URL(name)));
    }
    catch (MalformedURLException ex) {
      System.err.println(name);
     // ex.printStackTrace();

    }
    this.iconName=name;
  }

  public void setIcon(Icon icon){
    if (icon!=null && (icon instanceof javax.swing.ImageIcon)){
      this.setIconName(icon.toString());
    }
    super.setIcon(icon);
  }
}
