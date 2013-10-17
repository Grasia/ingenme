package ingenias.editor.rendererxml;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScalableJLabel extends JLabel{
	// http://forums.sun.com/thread.jspa?messageID=10007040#10007040
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getIcon()!=null)
         g.drawImage(((ImageIcon)getIcon()).getImage(), 0, 0, getWidth(), getHeight(), null);
    }
	

	public ScalableJLabel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ScalableJLabel(Icon arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public ScalableJLabel(Icon arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ScalableJLabel(String arg0, Icon arg1, int arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public ScalableJLabel(String arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public ScalableJLabel(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
