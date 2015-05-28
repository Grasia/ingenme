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


public class JLabelSmall extends JLabel {



  public  String iconName="";


  public JLabelSmall() {
     // this.setFont(FontConfiguration.getConfiguration().getStandardFont());
	  setFont(null);
  }

  public void setText(String text){

    super.setText("<html><small>"+text+"</small></html>");
  }


}
