package ingenias.editor;

import java.awt.*;
import javax.swing.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class FontConfiguration{

   
   private Font standardFont = new Font(Font.SANS_SERIF,Font.PLAIN, 12);
   private Font emphasisFont = new Font(Font.SANS_SERIF,Font.BOLD, 12);
   private Font guiFont = new Font(Font.SANS_SERIF,Font.PLAIN,12);

   private static FontConfiguration instance = new FontConfiguration();
   public static FontConfiguration getConfiguration(){
         return instance;
   }


   protected FontConfiguration(){}
   public Font getStandardFont(){
      return standardFont;
   }
   public Font getEmphasisFont(){
      return emphasisFont;
   }
   public Font getGUIFont(){
      return guiFont;
   }
}
