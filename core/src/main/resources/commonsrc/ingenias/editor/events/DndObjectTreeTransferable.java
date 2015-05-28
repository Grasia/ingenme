package ingenias.editor.events;

import ingenias.editor.widget.DnDJTreeObject;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

// a modified code of http://stackoverflow.com/questions/11201734/java-how-to-drag-and-drop-jpanel-with-its-components
public class DndObjectTreeTransferable implements Transferable {

    private DataFlavor[] flavors = new DataFlavor[]{ObjectTreeFlavor.SINGLETON};
    private DefaultMutableTreeNode treeNode;

    public DndObjectTreeTransferable(DefaultMutableTreeNode tree) {
        this.treeNode = tree;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {

        // Okay, for this example, this is over kill, but makes it easier
        // to add new flavor support by subclassing
        boolean supported = false;

        for (DataFlavor mine : getTransferDataFlavors()) {
            if (mine.equals(flavor)) {
                supported = true;
                break;
            }

        }

        return supported;

    }

    public DefaultMutableTreeNode getTreeNode() {

        return treeNode;

    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        Object data = null;
        if (isDataFlavorSupported(flavor)) {
            data = getTreeNode();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }

        return data;
    }

}
