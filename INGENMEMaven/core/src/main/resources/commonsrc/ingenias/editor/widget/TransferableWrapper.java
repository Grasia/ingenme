/*
    This code has been extracted and modified from an original
    work from Rob Kenworthy and an example from Sheetal Gupta
    (http://java.sun.com/docs/books/tutorial/dnd/sheetal.html)

*/


package ingenias.editor.widget;

import java.awt.datatransfer.*;
import java.io.*;
import javax.swing.tree.*;

public class TransferableWrapper implements Serializable , Transferable{

  private String content;

  public static String DefaultMutableTreeNode_MIME="DefaultMutableTreeNode";
   public static DataFlavor DefaultMutableTreeNode_FLAVOR=
        new DataFlavor(DefaultMutableTreeNode.class, "Tree node");

  public TransferableWrapper(DefaultMutableTreeNode content) {

    this.content="";
  }


  public boolean isDataFlavorSupported(DataFlavor df) {
    //System.err.println(df.getClass());
    return df.equals(this.DefaultMutableTreeNode_FLAVOR);
  }

  /** implements Transferable interface */
  public Object getTransferData(DataFlavor df)
      throws UnsupportedFlavorException, IOException {
    if (df.equals(DefaultMutableTreeNode_FLAVOR)) {
      return content;
    }
    else throw new UnsupportedFlavorException(df);
  }

  /** implements Transferable interface */
  public DataFlavor[] getTransferDataFlavors() {
    DataFlavor[] flavors={this.DefaultMutableTreeNode_FLAVOR};
    return flavors;
  }

  // --------- Serializable --------------

   private void writeObject(java.io.ObjectOutputStream out) throws IOException {
     out.defaultWriteObject();
   }

   private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException {
     in.defaultReadObject();
   }



 }