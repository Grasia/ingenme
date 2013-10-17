package ingenias.editor;

// From Mr_Silly user. He can be found at http://forum.java.sun.com/profile.jsp?user=157799



import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * An implementation of the TabbedPaneUI that looks like the tabs that are used the Photoshop palette windows.
 * <p/>
 * Copyright (C) 2005 by Jon Lipsky
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. Y
 * ou may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software d
 * istributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class PSTabbedPaneUI extends BasicTabbedPaneUI
{
	private static final Insets NO_INSETS = new Insets(2, 0, 0, 0);

	/**
	 * The font to use for the selected tab
	 */
	private Font boldFont;

	/**
	 * The font metrics for the selected font
	 */
	private FontMetrics boldFontMetrics;

	/**
	 * The color to use to fill in the background
	 */
	private Color fillColor;

	// ------------------------------------------------------------------------------------------------------------------
	//  Custom installation methods
	// ------------------------------------------------------------------------------------------------------------------

	public static ComponentUI createUI(JComponent c)
	{
		return new PSTabbedPaneUI();
	}

	@Override
	protected JButton createScrollButton(int arg0) {
		JButton result= new BasicArrowButton(arg0,
				UIManager.getColor("TabbedPane.selected"),
				UIManager.getColor("TabbedPane.shadow"),
				UIManager.getColor("TabbedPane.darkShadow"),
				UIManager.getColor("TabbedPane.highlight")) {
			 
				public Dimension getPreferredSize() {
					return new Dimension(50, 50);
				}
			};
			return result;
	}

	protected void installDefaults()
	{
		super.installDefaults();
		tabAreaInsets.left = 4;
		selectedTabPadInsets = new Insets(0, 0, 0, 0);
		tabInsets = selectedTabPadInsets;

		Color background = tabPane.getBackground();
		fillColor = background.darker();

		boldFont = tabPane.getFont().deriveFont(Font.BOLD);
		boldFontMetrics = tabPane.getFontMetrics(boldFont);
	}

	// ------------------------------------------------------------------------------------------------------------------
	//  Custom sizing methods
	// ------------------------------------------------------------------------------------------------------------------

	public int getTabRunCount(JTabbedPane pane)
	{
		return 1;
	}

	protected Insets getContentBorderInsets(int tabPlacement)
	{
		return NO_INSETS;
	}

	protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight)
	{
		int vHeight = fontHeight;
		if (vHeight % 2 > 0)
		{
			vHeight += 1;
		}
		return vHeight;
	}

	protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics)
	{
		return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + metrics.getHeight();
	}

	// ------------------------------------------------------------------------------------------------------------------
	//  Custom painting methods
	// ------------------------------------------------------------------------------------------------------------------


	// ------------------------------------------------------------------------------------------------------------------
	//  Methods that we want to suppress the behaviour of the superclass
	// ------------------------------------------------------------------------------------------------------------------

	protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)
	{
		Polygon shape = new Polygon();

		shape.addPoint(x, y + h);
		shape.addPoint(x, y);
		shape.addPoint(x + w - (h / 2), y);

		if (isSelected || (tabIndex == (rects.length - 1)))
		{
			shape.addPoint(x + w + (h / 2), y + h);
		}
		else
		{
			shape.addPoint(x + w, y + (h / 2));
			shape.addPoint(x + w, y + h);
		}

		g.setColor(tabPane.getBackground());
		g.fillPolygon(shape);
	}

	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)
	{
		g.setColor(Color.BLACK);
		g.drawLine(x, y, x, y + h);
		g.drawLine(x, y, x + w - (h / 2), y);
		g.drawLine(x + w - (h / 2), y, x + w + (h / 2), y + h);

		if (isSelected)
		{
			g.setColor(Color.WHITE);
			g.drawLine(x + 1, y + 1, x + 1, y + h);
			g.drawLine(x + 1, y + 1, x + w - (h / 2), y + 1);

			g.setColor(shadow);
			g.drawLine(x + w - (h / 2), y + 1, x + w + (h / 2)-1, y + h);
		}
	}

	protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
	{
		Rectangle selectedRect = selectedIndex < 0 ? null : getTabBounds(selectedIndex, calcRect);

		selectedRect.width = selectedRect.width + (selectedRect.height / 2) - 1;

		g.setColor(Color.BLACK);

		g.drawLine(x, y, selectedRect.x, y);
		g.drawLine(selectedRect.x + selectedRect.width + 1, y, x + w, y);

		g.setColor(Color.WHITE);

		g.drawLine(x, y + 1, selectedRect.x, y + 1);
		g.drawLine(selectedRect.x + 1, y + 1, selectedRect.x + 1, y);
		g.drawLine(selectedRect.x + selectedRect.width + 2, y + 1, x + w, y + 1);

		g.setColor(shadow);
		g.drawLine(selectedRect.x + selectedRect.width, y, selectedRect.x + selectedRect.width + 1, y + 1);
	}

	protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
	{
		// Do nothing
	}

	protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
	{
		// Do nothing
	}

	protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
	{
		// Do nothing
	}

	protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected)
	{
		// Do nothing
	}

	protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex)
	{
		int tw = tabPane.getBounds().width;

		g.setColor(fillColor);
		g.fillRect(0, 0, tw, rects[0].height + 3);

		super.paintTabArea(g, tabPlacement, selectedIndex);
	}

	protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected)
	{
		if (isSelected)
		{
			int vDifference = (int)(boldFontMetrics.getStringBounds(title,g).getWidth()) - textRect.width;
			textRect.x -= (vDifference / 2);
			super.paintText(g, tabPlacement, boldFont, boldFontMetrics, tabIndex, title, textRect, isSelected);
		}
		else
		{
			super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
		}
	}

	protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected)
	{
		return 0;
	}
}

/**
 * Code from http://stackoverflow.com/questions/10539013/swing-jtabbedpane-how-to-set-scroll-width
 * @author Max
 *
 */

 class ExtendedTabbedPaneUI extends BasicTabbedPaneUI {

    @Override
    protected JButton createScrollButton(int direction) {
         if (direction != SOUTH && direction != NORTH && direction != EAST &&
                                   direction != WEST) {
             throw new IllegalArgumentException("Direction must be one of: " +
                                                "SOUTH, NORTH, EAST or WEST");
         }

         //return new ScrollableTabButton(direction);

         return new BasicArrowButton(direction,
            UIManager.getColor("TabbedPane.selected"),
            UIManager.getColor("TabbedPane.shadow"),
            UIManager.getColor("TabbedPane.darkShadow"),
            UIManager.getColor("TabbedPane.highlight")) {

            @Override
            public Dimension getPreferredSize() {
                int maxWidth = calculateMaxTabWidth(JTabbedPane.LEFT);
                return new Dimension(maxWidth, super.getPreferredSize().height);
            }
        };
    }
}


public class JTabbedPaneWithCloseIcons extends JTabbedPane {
	
	
  public JTabbedPaneWithCloseIcons() {
    super();    
    this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);   
   //this.setUI(new ExtendedTabbedPaneUI());
 //   addMouseListener(this);            
 //   this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
  }
 
  
  public void addConventionalTab(Component component, String title) {
	    super.addTab(title, component);
   }
  
  public void addTab(String title, Component component) {
	this.addTab(title,null,component);
	//  super.addTab(title,component);
	//this.setTabComponentAt(this.getTabCount()-1, new ButtonTabComponent((JTabbedPane)this,null));	    
  }
  
  public void addTab(String title, Icon icon, Component component) {
		super.addTab(title,component);
		this.setTabComponentAt(this.getTabCount()-1, new ButtonTabComponent((JTabbedPane)this,icon));		
	  }
  
 


protected static ImageIcon createImageIcon(String path) {
      java.net.URL imgURL=null;
	try {
		imgURL = new URL("file:"+path);
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      if (imgURL != null) {
          return new ImageIcon(imgURL);
      } else {
          System.err.println("Couldn't find file: " + path);
          return null;
      }
  }

/*  


  public void addTab(String title, Component component, Icon extraIcon) {	  
    super.addTab(title, new CloseTabIcon(extraIcon), component);
  }
  
    

  public void mouseClicked(MouseEvent e) {
    int tabNumber=getUI().tabForCoordinate(this, e.getX(), e.getY());
    if (tabNumber < 0) return;

    if (e.isPopupTrigger()){
      javax.swing.JPopupMenu jm=new JPopupMenu("hola");
      jm.setLocation(e.getPoint());
      jm.setVisible(true);

    } else {
    	if ((CloseTabIcon) getIconAt(tabNumber)!=null){
      Rectangle rect = ( (CloseTabIcon) getIconAt(tabNumber)).getBounds();
      if (rect.contains(e.getX(), e.getY())) {
        //the tab is being closed
        this.removeTabAt(tabNumber);
      }
    	}
    }

  }

  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
}


class CloseTabIcon implements Icon {
  private int x_pos;
  private int y_pos;
  private int width;
  private int height;
  private Icon fileIcon;

  public CloseTabIcon(Icon fileIcon) {
    this.fileIcon=fileIcon;
    width=16;
    height=16;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    this.x_pos=x;
    this.y_pos=y;

    Color col=g.getColor();

    g.setColor(Color.black);
    int y_p=y+2;
    g.drawLine(x+1, y_p, x+12, y_p);
    g.drawLine(x+1, y_p+13, x+12, y_p+13);
    g.drawLine(x, y_p+1, x, y_p+12);
    g.drawLine(x+13, y_p+1, x+13, y_p+12);
    g.drawLine(x+3, y_p+3, x+10, y_p+10);
    g.drawLine(x+3, y_p+4, x+9, y_p+10);
    g.drawLine(x+4, y_p+3, x+10, y_p+9);
    g.drawLine(x+10, y_p+3, x+3, y_p+10);
    g.drawLine(x+10, y_p+4, x+4, y_p+10);
    g.drawLine(x+9, y_p+3, x+3, y_p+9);
    g.setColor(col);
    if (fileIcon != null) {
      fileIcon.paintIcon(c, g, x+width, y_p);
    }
  }

  public int getIconWidth() {
    return width + (fileIcon != null? fileIcon.getIconWidth() : 0);
  }

  public int getIconHeight() {
    return height;
  }

  public Rectangle getBounds() {
    return new Rectangle(x_pos, y_pos, width, height);
  }*/
  

	public static void main(String args[]){
		
	}
}