package ingenias.editor.rendererxml;

import javax.swing.JLabel;
import javax.swing.Icon;
import java.awt.*;
import java.net.*;
import ingenias.editor.FontConfiguration;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


public class JLabelHTML extends JLabel {



  public  String iconName="";


  public JLabelHTML() {
     this.setFont(FontConfiguration.getConfiguration().getStandardFont());
  }

  public void setText(String text){
   //System.err.println(text);
    super.setText("<html>"+text+"</html>");
  }


}
