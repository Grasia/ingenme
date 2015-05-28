package ingenias.editor.events;

import ingenias.editor.widget.DnDJTreeObject;

import java.awt.datatransfer.DataFlavor;

public class ObjectTreeFlavor extends DataFlavor {

    public static final ObjectTreeFlavor SINGLETON = new ObjectTreeFlavor();

    private ObjectTreeFlavor() {

        super(DnDJTreeObject.class, null);

    }

}