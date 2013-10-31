package ingenias.editor.editiondialog;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JToolTip;

public class MyJLabel extends JLabel {

	public MyJLabel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MyJLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		// TODO Auto-generated constructor stub
	}

	public MyJLabel(Icon image) {
		super(image);
		// TODO Auto-generated constructor stub
	}

	public MyJLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		// TODO Auto-generated constructor stub
	}

	public MyJLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		// TODO Auto-generated constructor stub
	}

	public MyJLabel(String text) {
		super(text);
		// TODO Auto-generated constructor stub
	}

	@Override
	public JToolTip createToolTip() {
		return new JMultiLineToolTip();
	}
	
	

}
