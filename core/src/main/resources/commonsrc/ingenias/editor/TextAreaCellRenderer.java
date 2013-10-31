
/**
 * Cell renderer to show a table cell as a text area.
 * By jmerrin2 from swing forum in http://www.javasoft.com
 */

package ingenias.editor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;



public class TextAreaCellRenderer extends DefaultTableCellRenderer implements java.io.Serializable 
{

public TextAreaCellRenderer ()
{
this.myText = new JTextArea();
this.myText.setLineWrap(true);
this.myText.setWrapStyleWord(true);
this.myText.setOpaque(true);
this.myText.setEditable(false);
}

/**
* Gets the text area component for a cell
*
* @param table
* The parent table.
*
* @param value
* The cell value.
*
* @param isSelected
* Is the cell selected?
*
* @param hasFocus
* Does the cell have focus?
*
* @param row
* Current row being rendered.
*
* @param col
* Current column being rendered.
*
* @return
* The text area component.
*/
public Component
getTableCellRendererComponent ( JTable table,
Object value,
boolean isSelected,
boolean hasFocus,
int row,
int col )
{
JPanel panel = new JPanel ();

panel.setLayout (new GridBagLayout());
panel.add (this.myText,
new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
GridBagConstraints.CENTER,
GridBagConstraints.BOTH,
new Insets(0, 3, 3, 0), 0, 0));
if (isSelected)
{
this.myText.setForeground(table.getSelectionForeground());
this.myText.setBackground(table.getSelectionBackground());
}
else
{
this.myText.setForeground(table.getForeground());
this.myText.setBackground(table.getBackground());
}

this.myText.setText(value==null?"":(String)value);

int tableRowHeight = table.getRowHeight(row);

// if the current height is different, resize the row height
if ( (tableRowHeight < panel.getPreferredSize().getHeight() ) )
{
table.setRowHeight(row, (int)(panel.getPreferredSize().getHeight()));
}
return panel;
}

JTextArea myText;

} // end class MultiLineCellRndrr implements java.io.Serializable 

