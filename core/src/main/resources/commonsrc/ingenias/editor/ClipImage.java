package ingenias.editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;

// class ClipImage was written by fredrik2
// http://forum.java.sun.com/profile.jsp?user=1564

public class ClipImage
    implements Transferable {
  private DataFlavor[] myFlavors;
  private BufferedImage myImage;

  public ClipImage(BufferedImage theImage) {
    myFlavors = new DataFlavor[] {
        DataFlavor.imageFlavor};
    myImage = theImage;
  }

  public Object getTransferData(DataFlavor flavor) throws
      UnsupportedFlavorException {
    if (flavor != DataFlavor.imageFlavor) {
      throw new UnsupportedFlavorException(flavor);
    }
    return myImage;
  }


  public DataFlavor[] getTransferDataFlavors() {
    return myFlavors;
  }


  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return (flavor == DataFlavor.imageFlavor);
  }
}

