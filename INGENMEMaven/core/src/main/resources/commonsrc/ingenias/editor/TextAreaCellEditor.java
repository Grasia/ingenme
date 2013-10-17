
/**
 * JTextArea editor for cells in JTable. By beschwab from swing forum in http://www.javasoft.com
 */

package ingenias.editor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class TextAreaCellEditor extends AbstractCellEditor implements TableCellEditor,java.io.Serializable {

private JTextArea area;
private JScrollPane pane;
private int width = 100;
private int height = 100;
/**
* Constructs a TableCellEditor that uses a text area.
*
* @param x a JTextArea object ...
*/
public TextAreaCellEditor(JTextArea textArea) {
area = textArea;
area.setLineWrap (true);
area.setWrapStyleWord (true);
pane = new JScrollPane (area,
JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
pane.getViewport().setPreferredSize (new Dimension (width, height));
}

public Object getCellEditorValue() {
return area.getText();
}

public Component getTableCellEditorComponent(
JTable table, Object value,
boolean isSelected,
int row, int column) {
area.setText((value != null) ? value.toString() : "");
return pane;
}

public void setDimensions (int width, int height) {
this.width = width;
this.height = height;
}
}