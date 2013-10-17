package ingenias.editor;

import ingenias.editor.ButtonTabComponent;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;


//Code partially reused from 
// http://stackoverflow.com/questions/60269/how-to-implement-draggable-tab-using-java-swing
// hack to simulate full screen drawing
//http://weblogs.java.net/blog/joshy/archive/2003/12/swing_hack_7_le.html
//drop on new window code is original
public class DraggableTabbedPane extends JTabbedPaneWithCloseIcons {

	private static GraphicsDevice device;
	private static DisplayMode original_mode;
	private static Dimension size;
	private static BufferedImage screen;
	private boolean dragging = false;
	private Image tabImage = null;
	private Point currentMouseLocation = null;
	private int draggedTabIndex = 0;
	private JFrame falseForeground;
	private BufferedImage toPaintOver;
	private int oldx;
	private int oldy;

	public DraggableTabbedPane() {
		super();
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {

				if(!dragging) {
					// Gets the tab index based on the mouse position
					int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());

					if(tabNumber >= 0) {
						draggedTabIndex = tabNumber;
						Rectangle bounds = getComponentAt(draggedTabIndex).getBounds(); 
						//getUI().getTabBounds(DraggableTabbedPane.this, tabNumber);


						// Paint the tabbed pane to a buffer
						Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics totalGraphics = totalImage.getGraphics();
						totalGraphics.setClip(bounds);
						// Don't be double buffered when painting to a static image.
						setDoubleBuffered(false);
						paint(totalGraphics);

						// Paint just the dragged tab to the buffer
						tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
						Graphics graphics = tabImage.getGraphics();
						graphics.drawImage(totalImage, 0, 0, 
								bounds.width, bounds.height, bounds.x, bounds.y,
								bounds.x + bounds.width, bounds.y+bounds.height,
								DraggableTabbedPane.this);

						dragging = true;
						/*try {
							captureScreen();
						} catch (AWTException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
						repaint();
					}
				} else {
					currentMouseLocation = e.getPoint();

					// Need to repaint
					repaint();
				}

				super.mouseDragged(e);
			}
		});

		addMouseListener(new MouseAdapter() {
			private Icon draggedIcon;

			public void mouseReleased(MouseEvent e) {

				if(dragging) {
					int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), 10);
					final Component comp = getComponentAt(draggedTabIndex);
					final String title = getTitleAt(draggedTabIndex);
					if (tabNumber>=0)
						draggedIcon=getIconAt(tabNumber);
					removeTabAt(draggedTabIndex);
					if(tabNumber >= 0 && 
							e.getY()<getSize().height && e.getY()>=0 &&
							e.getX()>=0 && e.getX()<getSize().width) {	
						 
						insertTab(title, null, comp, null, tabNumber);
						setTabComponentAt(tabNumber, new ButtonTabComponent((JTabbedPane)DraggableTabbedPane.this,draggedIcon));
//						falseForeground.dispose();
//						falseForeground=null;
						setSelectedIndex(tabNumber);						
						invalidate();						
					} else {						
						// create a tab in a separated window
						final JFrame jf=new JFrame(title);
						jf.getContentPane().setLayout(new BorderLayout());  
						jf.getContentPane().add(comp,BorderLayout.CENTER);
						jf.addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent evt) {
								jf.getContentPane().remove(comp);								
								insertTab(title, null, comp, null, draggedTabIndex);
								setTabComponentAt(draggedTabIndex, new ButtonTabComponent((JTabbedPane)DraggableTabbedPane.this,draggedIcon));		
								setSelectedIndex(draggedTabIndex);
								DraggableTabbedPane.this.invalidate();
							}
						});
						jf.setLocation(e.getLocationOnScreen());
						jf.pack();
						jf.setVisible(true);
						/*falseForeground.dispose();
						falseForeground=null;*/
					}
				
				}

				dragging = false;
				tabImage = null;
			}
		});
	}
	
	public static void captureScreen() throws AWTException {
	    Robot robot = new Robot();
	    Toolkit tk = Toolkit.getDefaultToolkit();
	    size = tk.getScreenSize();
	    Rectangle bounds = new Rectangle(0,0,(int)size.getWidth(),(int)size.getHeight());
	    screen = robot.createScreenCapture(bounds);
	}
	
	public static JFrame goFullScreen() {
	    GraphicsEnvironment env = GraphicsEnvironment.
	        getLocalGraphicsEnvironment();
	    device = env.getDefaultScreenDevice();
	    original_mode = device.getDisplayMode();
	    JFrame frame = new JFrame(device.getDefaultConfiguration());
	    frame.setUndecorated(true);
	    frame.setResizable(false);
	    frame.setLocationByPlatform(true);
	    device.setFullScreenWindow(frame);
	    return frame;
	}

	public static void goNormalScreen() {
	    device.setDisplayMode(original_mode);
	    device.setFullScreenWindow(null);
	}
	
	public void paint(Graphics g) {
		super.paint(g);

		// Are we dragging?
		if(dragging && currentMouseLocation != null && tabImage != null) {
			
		/*	if (falseForeground==null){
				falseForeground=goFullScreen();		
				falseForeground.setAlwaysOnTop(true);
				falseForeground.setVisible(true);							
			}
			g=falseForeground.getGraphics();	
			g.drawImage(screen, 0, 0, this);*/		
			// Draw the dragged tab
			
			//g.drawImage(toPaintOver, oldx, oldy, this);
			g.drawImage(tabImage, currentMouseLocation.x, currentMouseLocation.y, this);
	//		oldx=currentMouseLocation.x;
	//		oldy=currentMouseLocation.y;
			int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, currentMouseLocation.x, 10);
			if (tabNumber>=0)
				setSelectedIndex(tabNumber);
			
			
		}
	}
	

	 public void addTabWithCloseIcon(String title, Component component) {
		super.addTab(title,component);
		this.setTabComponentAt(this.getTabCount()-1, new ButtonTabComponent((JTabbedPane)this,null));		
	  }

	public static void main(String[] args) {
		JFrame test = new JFrame("Tab test");
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.setSize(400, 400);
		  GraphicsEnvironment ge = 
	            GraphicsEnvironment.getLocalGraphicsEnvironment();
	        GraphicsDevice gd = ge.getDefaultScreenDevice();

	        //If translucent windows aren't supported, exit.
	     /*   if (!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
	            System.err.println(
	                "Translucency is not supported");
	                System.exit(0);
	        }*/
		DraggableTabbedPane tabs = new DraggableTabbedPane();
		tabs.addTab("One", new JButton("One"));
		tabs.addTab("Two", new JButton("Two"));
		tabs.addTab("Three", new JButton("Three"));
		tabs.addTab("Four", new JButton("Four"));

		test.add(tabs);
		test.setVisible(true);
	}
}