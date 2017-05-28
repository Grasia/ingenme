/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz
 * 
 * This file is part of the INGENME tool. INGENME is an open source meta-editor
 * which produces customized editors for user-defined modeling languages
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 **/

package ingenias.editor.editiondialog;

import ingenias.editor.Editor;
import ingenias.editor.GraphManager;
import ingenias.editor.IDEState;
import ingenias.editor.MarqueeHandler;
import ingenias.editor.ModelJGraph;
import ingenias.editor.cell.CompositeRenderer;
import ingenias.editor.entities.Entity;
import ingenias.editor.entities.ModelEntity;
import ingenias.editor.entities.NAryEdgeEntity;
import ingenias.editor.entities.RoleEntity;
import ingenias.editor.events.ListenerContainer;
import ingenias.editor.events.WrongParent;
import ingenias.editor.widget.CustomJTextField;
import ingenias.editor.widget.Editable;
import ingenias.editor.widget.EntityWidgetPreferences;
import ingenias.generator.browser.Browser;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * Description of the Class
 * 
 * @author developer
 * @created 15 de enero de 2004
 */
public class GeneralEditionPanel extends javax.swing.JPanel implements
java.io.Serializable {
	private Vector<ActionListener> undo = new Vector<ActionListener>();
	private Vector<ActionListener> confirm = new Vector<ActionListener>();
	private Entity entity;
	private Hashtable ht;
	private Editor editor;
	private Border border1;
	private TitledBorder titledBorder1;
	private JScrollPane jScrollPane1 = new JScrollPane();
	Vector<ActionListener> actionsForUpdatingEntities = new Vector<ActionListener>();
	private Frame parentFrame = null;

	private ingenias.editor.ObjectManager om = null;
	private ingenias.editor.GraphManager gm = null;

	// private Browser browser;
	public static Image delImage;
	static {
		try {
			delImage = Toolkit.getDefaultToolkit().createImage(
					"images/delete.gif");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class CloseIconTitledBorder extends TitledBorder {
		Point location = new Point(0, 0);
		Dimension size = new Dimension(0, 0);

		public CloseIconTitledBorder(Border b, String title) {
			super(b, title);
		}

		public Point getLocation() {
			return location;
		}

		public Dimension getSize() {
			return size;
		}

		public void paintBorder(Component comp, Graphics g, int x1, int y1,
				int x2, int y2) {
			super.paintBorder(comp, g, x1, y1, x2, y2);
			int desp = 20;
			int orig = x2 - GeneralEditionPanel.delImage.getWidth(null) - desp;
			int fin = x2 - desp;
			g.setColor(Color.white);
			g.fillRect(orig - 2, 3,
					GeneralEditionPanel.delImage.getWidth(null) + 1,
					GeneralEditionPanel.delImage.getHeight(null) + 1);
			g.setColor(Color.black);
			g.drawRect(orig - 2, 3,
					GeneralEditionPanel.delImage.getWidth(null) + 1,
					GeneralEditionPanel.delImage.getHeight(null) + 1);
			g.drawImage(GeneralEditionPanel.delImage, orig - 1, 4, null);
			location = new Point(orig - 2, 3);
			size = new Dimension(
					GeneralEditionPanel.delImage.getWidth(null) + 1,
					GeneralEditionPanel.delImage.getHeight(null) + 1);

		}

	}

	/**
	 * Constructor for the GeneralEditionPanel object
	 * 
	 * @param ed
	 *            Description of the Parameter
	 * @param om
	 *            Description of the Parameter
	 * @param ent
	 *            Description of the Parameter
	 */
	public GeneralEditionPanel(Editor ed, Frame f,
			ingenias.editor.ObjectManager om, ingenias.editor.GraphManager gm,
			Entity ent) {
		super(new GridLayout(1, 1));
		this.editor = ed;
		this.om = om;
		this.gm = gm;
		this.parentFrame = f;

		if (om == null)
			throw new RuntimeException("OM is null");
		Box main = Box.createVerticalBox();
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(main);

		if (ModelEntity.class.isAssignableFrom(ent.getClass())) {
			JPanel subpanel = null;
			subpanel = this.createModelPanel((ModelEntity) ent);
			if (subpanel != null) {
				subpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				main.add(subpanel);
			}
		} else {

			entity = ent;
			Class entc = entity.getClass();
			Field[] fs = entc.getFields();
			try {
				String[] preferredOrder = this.getPreferredOrder();
				for (int k = 0; k < preferredOrder.length - 1; k++) {

					final Field cf = entc.getField(preferredOrder[k]);
					try {
						JPanel subpanel = null;						
						Border border1;
						TitledBorder border;
						border1 = BorderFactory
								.createLineBorder(Color.black, 2);
						border = new TitledBorder(border1, cf.getName());
						if (!this.isCollectionType(cf)) {
							this.getValue(entity.getClass(), cf);
							subpanel = this.createSinglePanel(cf);
						} else {
							subpanel = this.createCollectionPanel(cf);
						}
						if (subpanel != null) {
							subpanel.setBorder(border);
						}
				
						if (subpanel != null) {
							subpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
							main.add(subpanel);
						}
					
					} catch (NoSuchMethodException nsm) {
						nsm.printStackTrace();
					} catch (Exception nsme) {
						nsme.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param type
	 *            Description of the Parameter
	 * @param cf
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private JPanel createSubPanel(Entity ent) {

		GeneralEditionPanel gep = new GeneralEditionPanel(editor, parentFrame,
				om, gm, (Entity) ent);

		gep.setAlignmentX(Component.LEFT_ALIGNMENT);
		return gep;
	}

	/**
	 * Description of the Method
	 * 
	 * @param entType
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private Entity createEntity(Class entType) {

		try {
			Class[] conscf = { String.class };
			int index = entType.getName().lastIndexOf(".");
			String type = entType.getName().substring(index + 1,
					entType.getName().length());
			Object[] paracf = { "" + ingenias.editor.Editor.getNewId(om, gm) };

			String methodName = "create" + type.substring(0, 1).toUpperCase()
					+ type.substring(1, type.length());
			java.lang.reflect.Method m = ingenias.editor.ObjectManager.class
					.getMethod(methodName, conscf);
			final Entity result = (Entity) m.invoke(om, paracf);
			this.undo.add(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					om.removeEntity(result);
					// System.err.println("removing "+result);
				}
			});
			return result;

		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		} catch (java.lang.reflect.InvocationTargetException ite) {
			ite.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}

		return null;
	}

	/**
	 * Description of the Method
	 * 
	 * @param entType
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private Entity createModelEntity(Class entType) {
		Entity result = null;
		try {
			Class[] conscf = { String.class };
			Object[] paracf = { "" + ingenias.editor.Editor.getNewId(om, gm) };
			return (Entity) entType.getConstructor(conscf).newInstance(paracf);
		} catch (InstantiationException ie) {
			ie.printStackTrace();
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		} catch (java.lang.reflect.InvocationTargetException ite) {
			ite.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}

		return result;
	}

	/**
	 * Description of the Method
	 * 
	 * @param cf1
	 *            Description of the Parameter
	 * @param entc
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception NoSuchMethodException
	 *                Description of the Exception
	 * @exception ClassNotFoundException
	 *                Description of the Exception
	 * @exception IllegalAccessException
	 *                Description of the Exception
	 * @exception InstantiationException
	 *                Description of the Exception
	 */
	private JPanel createSimplePanel(Field cf1, Class entc)
			throws NoSuchMethodException, ClassNotFoundException,
			IllegalAccessException, InstantiationException {
		final Field cf = cf1;
		JPanel np = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

		JLabel jl = new JLabel(cf.getName());
		jl.setAlignmentX(Component.LEFT_ALIGNMENT);
		Object value = this.getValue(entc, cf);
		if (value == null) {
			value = "";
		}
		final ingenias.editor.widget.Editable jt = this.getPreferredWidget(
				this.entity.getClass(), cf1.getName());
		if (jt instanceof ingenias.editor.widget.ScrolledTArea) {
			BoxLayout bl = new javax.swing.BoxLayout(np,
					javax.swing.BoxLayout.Y_AXIS);
			((Component) jt).setSize(200, 100);
			np.setLayout(bl);
		}
		jt.setText(value.toString());
		jt.setAlignmentX(Component.LEFT_ALIGNMENT);
		np.add(jl);
		np.add((java.awt.Component) jt);
		this.add(np);
		np.setMinimumSize(new Dimension(cf.getName().length() * 10 + 30 * 10,
				20));

		setValueFromTextField(jt, cf1);

		/*
		 * jt.addFocusListener( new java.awt.event.FocusListener() { public void
		 * focusLost(java.awt.event.FocusEvent fe) { if (fe.getID() ==
		 * fe.FOCUS_LOST ) { JTextComponent jt = (JTextComponent)
		 * fe.getComponent(); setValue(jt.getText(), cf); } }
		 * 
		 * public void focusGained(java.awt.event.FocusEvent fe) { }
		 * 
		 * }); jt.addKeyListener( new java.awt.event.KeyListener() {
		 * 
		 * public void keyReleased(java.awt.event.KeyEvent ke) {
		 * 
		 * }
		 * 
		 * public void keyPressed(java.awt.event.KeyEvent ke) {
		 * 
		 * }
		 * 
		 * public void keyTyped(java.awt.event.KeyEvent ke) { if
		 * (ke.getKeyCode() == ke.VK_ENTER) { JTextComponent jt =
		 * (JTextComponent) ke.getComponent(); setValue(jt.getText(), cf); } }
		 * });
		 */

		return np;
	}

	/**
	 * Description of the Method
	 * 
	 * @param text
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public String getutf16Text(String text) {
		try {
			java.io.ByteArrayOutputStream ba = new java.io.ByteArrayOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(ba, "UTF16");
			osw.write(text);
			osw.close();
			// System.err.println(new String(ba.toByteArray()));
			return new String(ba.toByteArray());
		} catch (Exception uee) {
			uee.printStackTrace();
		}
		return "";
	}

	/**
	 * Description of the Method
	 * 
	 * @param me
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private JPanel createModelPanel(ModelEntity me) {
		final ModelEntity cent = me;
		JPanel np = new JPanel(new BorderLayout());
		np.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel top = new JPanel();
		JLabel value = null;
		if (cent.getModelID() == null) {
			value = new JLabel("NONE");
		} else {
			value = new JLabel(cent.getModelID());
		}
		final JLabel finalValue = value;
		top.add(new JLabel("Current value:"));
		top.add(value);
		JPanel middle = new JPanel(new BorderLayout());
		Vector instancesName = this.getModelInstancesNames("ingenias.editor."
				+ cent.getModelType());
		final Vector instances = gm.getInstances("ingenias.editor."
				+ cent.getModelType());
		final javax.swing.JComboBox jcb = new javax.swing.JComboBox(
				instancesName);
		middle.add(jcb, BorderLayout.CENTER);
		JButton selectValue = new JButton("Select one model");
		
		JPanel middleButtons = new JPanel();
		middleButtons.add(selectValue);
		
		middle.add(middleButtons, BorderLayout.SOUTH);

		selectValue.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int index = jcb.getSelectedIndex();
				if (index >= 0) {
					String selected = instances.elementAt(index).toString();
					cent.setModelID(selected);
					finalValue.setText(selected);
				}
			}
		});
		

		np.add(top, BorderLayout.NORTH);
		np.add(middle, BorderLayout.CENTER);
		return np;
	}

	/**
	 * Description of the Method
	 * 
	 * @param cf
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception NoSuchMethodException
	 *                Description of the Exception
	 * @exception InvocationTargetException
	 *                Description of the Exception
	 * @exception IllegalAccessException
	 *                Description of the Exception
	 * @exception InstantiationException
	 *                Description of the Exception
	 */
	private JPanel createModelPanel(Field cf) throws NoSuchMethodException,
	InvocationTargetException, IllegalAccessException,
	InstantiationException {
		ModelEntity cent1 = (ModelEntity) this.getValue(this.entity.getClass(),
				cf);
		if (cent1 == null) {
			Class[] paramt = { String.class };
			Object[] objects = { "" + ingenias.editor.Editor.getNewId(om, gm) };
			cent1 = (ModelEntity) cf.getType().getConstructor(paramt)
					.newInstance(objects);
			this.setValue(cent1, cf);
		}

		final ModelEntity cent = cent1;

		JPanel np = new JPanel(new BorderLayout());
		np.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel top = new JPanel();
		JLabel value = null;
		if (cent.getModelID() == null) {
			value = new JLabel("NONE");
		} else {
			value = new JLabel(cent.getModelID());
		}
		final JLabel finalValue = value;
		top.add(new JLabel("Current value:"));
		top.add(value);
		JPanel middle = new JPanel(new BorderLayout());
		Vector instancesName = this.getModelInstancesNames("ingenias.editor."
				+ cent.getModelType());
		final Vector instances = gm.getInstances("ingenias.editor."
				+ cent.getModelType());
		final javax.swing.JComboBox jcb = new javax.swing.JComboBox(
				instancesName);
		middle.add(jcb, BorderLayout.CENTER);
		JButton selectValue = new JButton("Select one model");
		JButton selectModel = new JButton("Show selected");
		JPanel middleButtons = new JPanel();
		middleButtons.add(selectValue);
		middle.add(middleButtons, BorderLayout.SOUTH);

		selectValue.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int index = jcb.getSelectedIndex();
				if (index >= 0) {
					String selected = instances.elementAt(index).toString();
					cent.setModelID(selected);
					finalValue.setText(selected);
				}
			}
		});
		selectModel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (cent.getModelID() != null
						&& !cent.getModelID().equalsIgnoreCase("")) {
					ingenias.editor.ModelJGraph mjg = gm.getModel(cent
							.getModelID());
					//editor.changeGraph(mjg);
					// updateButtonBars();
				}
			}
		});

		np.add(top, BorderLayout.NORTH);
		np.add(middle, BorderLayout.CENTER);

		return np;
	}

	/**
	 * Gets the modelInstancesNames attribute of the GeneralEditionPanel object
	 * 
	 * @param type
	 *            Description of the Parameter
	 * @return The modelInstancesNames value
	 */
	private Vector getModelInstancesNames(String type) {

		Vector instances = gm.getInstances(type);
		Vector instanceIDS = new Vector();
		Enumeration enumeration = instances.elements();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement().toString();
			instanceIDS.add(type + ":" + name);
		}

		return instanceIDS;
	}

	private void createReferencePanelToEntity(final JPanel np,
			final Entity ent, final Field field) {
		JButton open = new JButton("Edit");
		JButton delete = new JButton("Unlink");
		final JLabel jl = new JLabel(ent.toString() + ":" + ent.getType());
		JPanel jp = new JPanel();
		jp.add(jl);
		jp.add(open);
		jp.add(delete);
		final Component[] oldComponents = np.getComponents();
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int result = JOptionPane
						.showConfirmDialog(parentFrame,
								"Do you really want to Unlink?", "Unlink",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					setValue(null, field);
					np.removeAll();
					JPanel npanel = createSelectionPanel(field);
					np.add(npanel, BorderLayout.CENTER);
					np.validate();
				}
			}
		});
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GeneralEditionFrame ndialog = new GeneralEditionFrame(editor,
						om, gm, parentFrame, ent.getId() + ":" + ent.getType(),
						ent);
				ndialog.pack();
				ndialog.setModal(true);
				ndialog.setVisible(true);
				jl.setText(ent.toString() + ":" + ent.getType());
			}
		});
		// JPanel jp = createSubPanel(ent);
		np.removeAll();
		np.add(jp, BorderLayout.CENTER);
		np.validate();
	}

	/**
	 * Description of the Method
	 * 
	 * @param cf
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private JPanel createSelectionPanel(final Field cf) {
		final Field cf1 = cf;
		final Vector subclasses = this.getSubclasses(cf.getType());
		sortClasses(subclasses);
		final JPanel np = new JPanel(new BorderLayout());
		np.setAlignmentX(Component.LEFT_ALIGNMENT);

		final JScrollPane jsp = new JScrollPane();
		JPanel jp1 = new JPanel();
		final JList jl = new JList(subclasses);
		jp1.add(jl);
		jsp.getViewport().add(jl, null);
		JButton cnew = new JButton("Create new");
		JButton sexisting = new JButton("Select existing");
		np.add(jsp, BorderLayout.CENTER);
		final JPanel psouth = new JPanel();
		psouth.add(cnew);
		psouth.add(sexisting);
		JPanel npanel = new JPanel(new BorderLayout());
		npanel.add(jsp, BorderLayout.CENTER);
		npanel.add(psouth, BorderLayout.SOUTH);
		np.add(npanel, BorderLayout.CENTER);

		final GeneralEditionPanel self = this;

		cnew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (jl.getSelectedIndex() >= 0) {
					Class type = ((Class) subclasses.elementAt(jl
							.getSelectedIndex()));
					Entity ent = createEntity(type);
					setValue(ent, cf1);
					createReferencePanelToEntity(np, ent, cf);
				}
			}

		});

		sexisting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (jl.getSelectedIndex() >= 0) {
					String type = ((Class) subclasses.elementAt(jl
							.getSelectedIndex())).getName();
					Vector instances = om.getInstances(type);
					Vector instanceIDS = new Vector();
					Enumeration enumeration = instances.elements();
					Hashtable<String, Object> instanceIndex = new Hashtable<String, Object>();

					while (enumeration.hasMoreElements()) {
						Entity o = (Entity) enumeration.nextElement();
						String etype = o.getClass().getName();
						int index = etype.lastIndexOf(".");
						String className = etype.substring(index + 1,
								etype.length());
						instanceIDS.add(className + ":" + o.toString());
						instanceIndex.put(className + ":" + o.toString(), o);
					}
					Collections.sort(instanceIDS);
					if (instances.size() > 0) {
						javax.swing.JComboBox options = new javax.swing.JComboBox(
								instanceIDS);
						int result = JOptionPane.showConfirmDialog(parentFrame,
								options, "Select one", JOptionPane.NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (result == JOptionPane.OK_OPTION
								&& options.getSelectedIndex() >= 0) {
							Entity ent = (Entity) instanceIndex.get(options
									.getSelectedItem());
							setValue(ent, cf1);

							createReferencePanelToEntity(np, ent, cf);

							/*
							 * JPanel jp = new GeneralEditionPanel(editor,
							 * parentFrame, om, ent); np.removeAll(); np.add(jp,
							 * BorderLayout.CENTER); CloseIconTitledBorder
							 * border; border1 =
							 * BorderFactory.createLineBorder(Color.black, 2);
							 * border = new CloseIconTitledBorder(border1,
							 * cf1.getName()); np.setBorder(border);
							 * np.validate();
							 */
						}
					} else {
						JOptionPane.showMessageDialog(parentFrame,
								"There are no instances of " + type, "Warning",
								JOptionPane.WARNING_MESSAGE);
					}

				}
			}
		});

		return np;
	}

	/**
	 * Description of the Method
	 * 
	 * @param jl1
	 *            Description of the Parameter
	 * @param lm1
	 *            Description of the Parameter
	 * @param cf1
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private JPopupMenu createCollectionPopupmenu(JList jl1,
			javax.swing.DefaultListModel lm1, Field cf1) {
		JPopupMenu menu = new JPopupMenu();
		final Field cf = cf1;
		final javax.swing.DefaultListModel lm = lm1;
		final JList jl = jl1;

		// Edit
		menu.add(new AbstractAction("Add new element") {
			public void actionPerformed(ActionEvent e) {
				Runnable action = new Runnable() {
					public void run() {
						try {
							String type = cf.getType().getName();
							Vector instClasses = om.getValidEntitiesClasses();
							// sortClasses(instClasses);
							Vector classesIDS = new Vector();
							Vector validClasses = new Vector();
							Enumeration enumeration = instClasses.elements();
							Hashtable<String, Object> instanceIndex = new Hashtable<String, Object>();
							while (enumeration.hasMoreElements()) {
								Class o = (Class) enumeration.nextElement();
								if (getCollectionType(cf).isAssignableFrom(o)) {
									String etype = o.getName();
									int index = etype.lastIndexOf(".");
									String className = etype.substring(
											index + 1, etype.length());
									classesIDS.add(className);
									validClasses.add(o);
									instanceIndex.put(className, o);
								}
							}
							Collections.sort(classesIDS);

							if (classesIDS.size() > 0) {
								javax.swing.JComboBox options = new javax.swing.JComboBox(
										classesIDS);
								int result = JOptionPane.showConfirmDialog(
										parentFrame, options, "Select one",
										JOptionPane.NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
								if (result == JOptionPane.OK_OPTION
										&& options.getSelectedIndex() >= 0) {
									Class sclass = (Class) instanceIndex
											.get(options.getSelectedItem());
									Entity ent = null;
									if (ModelEntity.class
											.isAssignableFrom(sclass)) {
										ent = createModelEntity(sclass);
									} else {
										ent = createEntity(sclass);
									}

									GeneralEditionFrame gef = new GeneralEditionFrame(
											editor, om, gm, parentFrame,
											"Editing", ent);
									gef.pack();
									gef.setVisible(true);
									final Entity ent1 = ent;
									switch (gef.getStatus()) {
									case GeneralEditionFrame.ACCEPTED:
										addValue(ent, cf);

										SwingUtilities
										.invokeLater(new Runnable() {
											public void run() {
												lm.addElement(ent1);
												refreshList(jl);

											}
										});

										break;
									case GeneralEditionFrame.CANCELLED:
										break;

									}

								}
							} else {
								JOptionPane.showMessageDialog(parentFrame,
										"There are no valid classes assignable to "
												+ type, "Warning",
												JOptionPane.WARNING_MESSAGE);
							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				};
				SwingUtilities.invokeLater(action);
				// new Thread(action).start();

			}
		});
		menu.add(new AbstractAction("Add existing element") {
			public void actionPerformed(ActionEvent e) {
				Runnable action = new Runnable() {
					public void run() {
						try {
							String type = getCollectionType(cf).getName();
							Vector instances = om.getInstances(type);
							Vector instanceIDS = new Vector();
							Enumeration enumeration = instances.elements();
							Hashtable<String, Object> instanceIndex = new Hashtable<String, Object>();
							while (enumeration.hasMoreElements()) {
								Entity o = (Entity) enumeration.nextElement();
								String etype = o.getClass().getName();
								int index = etype.lastIndexOf(".");
								String className = etype.substring(index + 1,
										etype.length());
								instanceIDS.add(className + ":" + o.toString());
								instanceIndex.put(
										className + ":" + o.toString(), o);
							}

							Collections.sort(instanceIDS);

							if (instances.size() > 0) {
								javax.swing.JComboBox options = new javax.swing.JComboBox(
										instanceIDS);
								int result = JOptionPane.showConfirmDialog(
										parentFrame, options, "Select one",
										JOptionPane.NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
								if (result == JOptionPane.OK_OPTION
										&& options.getSelectedIndex() >= 0) {
									Entity ent = (Entity) instanceIndex
											.get(options.getSelectedItem());
									addValue(ent, cf);
									final Entity ent1 = ent;
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											lm.addElement(ent1);
										}
									});
									refreshList(jl);

								}
							} else {
								JOptionPane.showMessageDialog(parentFrame,
										"There are no instances of " + type,
										"Warning", JOptionPane.WARNING_MESSAGE);
							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				};
				new Thread(action).start();

			}
		});

		menu.add(new AbstractAction("Remove selected element") {
			public void actionPerformed(ActionEvent e) {
				Runnable action = new Runnable() {
					public void run() {
						Object[] selectedValues = jl.getSelectedValues();

						for (Object objectToDelete : selectedValues) {

							try {
								Class type = getCollectionType(cf);
								/*Enumeration enumeration = getCollection(cf);
								boolean found = false;
								while (enumeration.hasMoreElements() && !found) {
									final Object currentObject = enumeration
											.nextElement();*/
								//if (objectToDelete.equals(objectToDelete)) {
								final Object toDeleteFinal=objectToDelete;
								SwingUtilities
								.invokeLater(new Runnable() {
									public void run() {
										lm.removeElement(toDeleteFinal);
										refreshList(jl);
									}
								});
								if (type.equals(java.lang.String.class)) {
									removeValue(
											objectToDelete.toString(),
											cf);
									refreshList(jl);
								} else {
									ingenias.editor.entities.Entity en = (ingenias.editor.entities.Entity) objectToDelete;
									removeValue(en.getId(), cf);
									refreshList(jl);

								}
								//	}

								//}
								/*	if (!found) {
									// it is an element not added yet to the attribute. It has to be removed from the list.
									// though it has not been added yet, we assume that a  previous oiperation of aggregation will
									// occur on confirming. Hence, a remove is necessary to keep the ballance.
									lm.removeElement(objectToDelete);
									refreshList(jl);
									if (type.equals(java.lang.String.class)) {
										removeValue(
												objectToDelete.toString(),
												cf);
									} else {																			
										ingenias.editor.entities.Entity en = (ingenias.editor.entities.Entity) objectToDelete;
										removeValue(en.getId(), cf);
									}
								}*/

							} catch (Exception e1) {
								e1.printStackTrace();
							}

						}

					}

				};
				new Thread(action).start();

			}
		});

		menu.add(new AbstractAction("Open selected element") {
			public void actionPerformed(ActionEvent e) {
				Runnable action = new Runnable() {
					public void run() {
						int index = jl.getSelectedIndex();
						if (index > -1) {
							Entity ent = (Entity) lm.get(index);
							// System.err.println(ent.getClass());
							JDialog jf = new GeneralEditionFrame(editor, om,
									gm, parentFrame, "Edition", ent);
							jf.pack();
							jf.setVisible(true);
						}
					}
				};
				new Thread(action).start();
			}
		});

		return menu;
	}

	private void refreshList(final JList jl) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jl.invalidate();
				jl.repaint();
				parentFrame.repaint();
			}
		});
	}

	protected static void sortClasses(Vector instClasses) {
		for (int k = 0; k < instClasses.size(); k++) {
			Class a = (Class) instClasses.elementAt(k);
			for (int j = k; j < instClasses.size(); j++) {
				Class b = (Class) instClasses.elementAt(j);
				if (a.getName().compareTo(b.getName()) > 0) {
					instClasses.setElementAt(b, k);
					instClasses.setElementAt(a, j);
					a = b;
				}
			}
		}

	}

	protected void sort(Vector instances) {

		Collections.sort(instances);

	}

	/**
	 * Description of the Method
	 * 
	 * @param cf
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception NoSuchMethodException
	 *                Description of the Exception
	 */
	private JPanel createCollectionPanel(final Field cf)
			throws NoSuchMethodException {
		JPanel main = new JPanel(new GridLayout());
		main.setAlignmentX(Component.LEFT_ALIGNMENT);
		JScrollPane collection = new JScrollPane();
		collection
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		collection.setAlignmentX(Component.LEFT_ALIGNMENT);
		main.add(collection, null);
		collection.setAlignmentX(Component.LEFT_ALIGNMENT);
		final javax.swing.DefaultListModel dlm = new javax.swing.DefaultListModel();
		Enumeration enumeration = this.getCollection(cf);
		while (enumeration.hasMoreElements()) {
			dlm.addElement(enumeration.nextElement());
		}
		final JList jl = new JList(dlm);
		jl.setName("valueList" + cf.getName());
		jl.setAutoscrolls(true);
		jl.setAlignmentX(Component.LEFT_ALIGNMENT);
		// jl.setPreferredSize(new Dimension(300, 100));
		// main.add(jl,null);
		collection.getViewport().add(jl, null);
		collection
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		final Field cf1 = cf;
		jl.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					JPopupMenu menu = createCollectionPopupmenu(jl, dlm, cf1);
					menu.setName("listMenu" + cf.getName());
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		return main;
	}

	/**
	 * Description of the Method
	 * 
	 * @param cf
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception NoSuchMethodException
	 *                Description of the Exception
	 * @exception InstantiationException
	 *                Description of the Exception
	 * @exception IllegalAccessException
	 *                Description of the Exception
	 * @exception InvocationTargetException
	 *                Description of the Exception
	 */
	private JPanel createSinglePanel(Field cf) throws NoSuchMethodException,
	InstantiationException, IllegalAccessException,
	InvocationTargetException {
		JPanel subpanel = null;
		if (ingenias.editor.entities.ModelEntity.class.isAssignableFrom(cf
				.getType())) {
			subpanel = this.createModelPanel(cf);

			this.add(subpanel);
		} else if (ingenias.editor.entities.Entity.class.isAssignableFrom(cf
				.getType())) {
			if (this.getValue(this.entity.getClass(), cf) == null) {
				// select an existing one
				subpanel = this.createSelectionPanel(cf);
				// create a new one
			} else {
				Entity subentity = (Entity) this.getValue(
						this.entity.getClass(), cf);
				subpanel = new JPanel(new BorderLayout());
				if (subentity != null) {
					createReferencePanelToEntity(subpanel, subentity, cf);
				} else {
					JPanel npanel = new GeneralEditionPanel(editor,
							parentFrame, om, gm, (Entity) this.getValue(
									this.entity.getClass(), cf));
					subpanel.add(npanel, BorderLayout.CENTER);
				}
			}
		} else {
			// a basic type is, then
			try {
				subpanel = createSimplePanel(cf, this.entity.getClass());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return subpanel;
	}

	/**
	 * Description of the Method
	 * 
	 * @param e
	 *            Description of the Parameter
	 * @param subclasses
	 *            Description of the Parameter
	 */
	private void mouseClickedOnSelection(java.awt.event.MouseEvent e,
			Vector subclasses) {
		JList jl = (JList) e.getComponent();
		int index = jl.getSelectedIndex();
		if (index >= 0) {
			Class selected = (Class) subclasses.get(index);
		}
	}

	/**
	 * Gets the subclasses attribute of the GeneralEditionPanel object
	 * 
	 * @param c
	 *            Description of the Parameter
	 * @return The subclasses value
	 */
	private Vector getSubclasses(Class c) {
		Vector result = new Vector();
		final Vector validClasses = ingenias.editor.ObjectManager
				.getValidEntitiesClasses();
		Enumeration enumeration = validClasses.elements();
		while (enumeration.hasMoreElements()) {
			Class current = (Class) enumeration.nextElement();
			if (c.isAssignableFrom(current)) {
				result.add(current);
			}
		}
		// Package.getPackage("ingenias.editor.entities").;
		return result;
	}

	/**
	 * Gets the value attribute of the GeneralEditionPanel object
	 * 
	 * @param ent
	 *            Description of the Parameter
	 * @param cf
	 *            Description of the Parameter
	 * @return The value value
	 * @exception NoSuchMethodException
	 *                Description of the Exception
	 */
	private Object getValue(Class ent, Field cf) throws NoSuchMethodException {
		try {
			Class params[] = {};
			Object paramVal[] = {};
			java.lang.reflect.Method m = ent.getMethod("get"
					+ cf.getName().substring(0, 1).toUpperCase()
					+ cf.getName().substring(1, cf.getName().length()), params);
			return m.invoke(entity, paramVal);
		} catch (NoSuchMethodException nsm) {
			throw nsm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private void setValueFromTextField(final Editable jt, final Field cf) {
		try {

			Class params[] = { cf.getType() };

			String mname = "set" + cf.getName().substring(0, 1).toUpperCase()
					+ cf.getName().substring(1, cf.getName().length());
			java.lang.reflect.Method[] mms = entity.getClass().getMethods();
			for (int k = 0; k < mms.length; k++) {
				Class[] c = mms[k].getParameterTypes();
			}

			final java.lang.reflect.Method m = entity.getClass().getMethod(
					mname, params);

			final java.lang.reflect.Method undo = m;
			final Object oldvalue = getValue(entity.getClass(), cf);
			this.confirm.add(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Vector<Entity> objectsWithSameID = om.findUserObject(jt
								.getTypedContent().toString());
						if ((cf.getName().equalsIgnoreCase("id") && (objectsWithSameID
								.size() == 0 || (objectsWithSameID.size() == 1 && objectsWithSameID
								.contains(entity))))
								|| !cf.getName().equalsIgnoreCase("id")) {
							m.invoke(entity,
									new Object[] { jt.getTypedContent() });

						} else {
							JOptionPane
							.showMessageDialog(
									parentFrame,
									"There is another entity with that ID. Operation cancelled.",
									"Error",
									JOptionPane.WARNING_MESSAGE);
						}
						// System.err.println("setting old value "+getValue(entity.getClass(),cf));
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						System.err.println("Error in method " + m.getName()
								+ " invocation over entity " + entity + ":"
								+ entity.getType());
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
					} catch (InvocationTargetException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					;
				}
			});

		} catch (Exception iae) {
			iae.printStackTrace();
		}
	}

	/**
	 * Sets the value attribute of the GeneralEditionPanel object
	 * 
	 * @param value
	 *            The new value value
	 * @param cf
	 *            The new value value
	 */
	private void setValue(final Object value, final Field cf) {
		try {

			Class params[] = { cf.getType() };
			final Object paramVal[] = { value };
			String mname = "set" + cf.getName().substring(0, 1).toUpperCase()
					+ cf.getName().substring(1, cf.getName().length());
			java.lang.reflect.Method[] mms = entity.getClass().getMethods();
			for (int k = 0; k < mms.length; k++) {
				Class[] c = mms[k].getParameterTypes();
			}

			final java.lang.reflect.Method m = entity.getClass().getMethod(
					mname, params);

			final java.lang.reflect.Method undo = m;
			final Object oldvalue = getValue(entity.getClass(), cf);

			if (!value.equals(oldvalue)) {
				if ((cf.getName().equalsIgnoreCase("id") && this.om
						.findUserObject(value.toString()).size() == 0)
						|| !cf.getName().equalsIgnoreCase("id")) {
					this.confirm.add(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								m.invoke(entity, paramVal);

								insertAdditionalCellsForSpecialLayout(value, entity, editor.getGraph(), gm);

								// System.err.println("setting old value "+getValue(entity.getClass(),cf));
							} catch (IllegalArgumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
							} catch (InvocationTargetException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
							;
						}
					});

					this.undo.add(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								if (!value.equals(oldvalue))
									undo.invoke(entity,
											new Object[] { oldvalue });
								// System.err.println("setting old value "+getValue(entity.getClass(),cf));
							} catch (IllegalArgumentException e1) {
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								e1.printStackTrace();
							}
						}
					});
				} else {
					JOptionPane
					.showMessageDialog(
							parentFrame,
							"There is another entity with that ID. Operation cancelled.",
							"Error", JOptionPane.WARNING_MESSAGE);
				}
			}

		} catch (Exception iae) {
			iae.printStackTrace();
		}
	}

	public static void addValue(final Object value, Field cf, final Entity entity, 
			Vector<ActionListener> confirm, Vector<ActionListener> undo, final ModelJGraph graph, final GraphManager gm) {
		try {

			final Object paramVal[] = { value };
			String mname = "add" + cf.getName().substring(0, 1).toUpperCase()
					+ cf.getName().substring(1, cf.getName().length());
			String mnameremove = "remove"
					+ cf.getName().substring(0, 1).toUpperCase()
					+ cf.getName().substring(1, cf.getName().length())
					+ "Element";
			/*
			 * java.lang.reflect.Method[] mms = entity.getClass().getMethods();
			 * for (int k = 0; k < mms.length; k++) { Class[] c =
			 * mms[k].getParameterTypes(); }
			 */
			java.lang.reflect.Method m = null;
			java.lang.reflect.Method rev = null;
			Class vclass = value.getClass();
			Class params[] = null;
			while (m == null && !vclass.equals(Object.class)) {
				try {
					params = new Class[] { vclass };
					m = entity.getClass().getMethod(mname, params);
					rev = entity.getClass().getMethod(mnameremove,
							new Class[] { String.class });
				} catch (NoSuchMethodException nsme) {
					vclass = vclass.getSuperclass();
				}
			}

			final java.lang.reflect.Method fm = m;

			confirm.add(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						fm.invoke(entity, paramVal);
						insertAdditionalCellsForSpecialLayout(value, entity, graph, gm);
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}

			});

			final java.lang.reflect.Method revMethod = rev;
			undo.add(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (value instanceof Entity)
							revMethod.invoke(entity,
									new Object[] { ((Entity) value).getId() });
						else
							revMethod.invoke(entity,
									new Object[] { value.toString() });
						// System.err.println("undoing adding "+value);
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

		} catch (Exception iae) {
			iae.printStackTrace();
		}
	}

	/**
	 * Adds a feature to the Value attribute of the GeneralEditionPanel object
	 * 
	 * @param value
	 *            The feature to be added to the Value attribute
	 * @param cf
	 *            The feature to be added to the Value attribute
	 */
	private void addValue(final Object value, Field cf) {
		addValue(value, cf, entity, confirm, undo, editor.getGraph(), gm);
	}

	private static void insertAdditionalCellsForSpecialLayout(final Object value, Entity entity, ModelJGraph edgraph, GraphManager gm) {
		for (ModelJGraph graph:gm.getUOModels()){

			if (contains(graph,entity)){


				if (value instanceof ingenias.editor.entities.Entity
						&& edgraph != null
						&& edgraph.getAllowedEntities()
						.contains(value.getClass().getSimpleName())) {
					for (Object obj : graph.getRoots()) {
						if (obj instanceof DefaultGraphCell
								&& ((DefaultGraphCell) obj).getUserObject()!=null &&
								((DefaultGraphCell) obj).getUserObject().equals(
										entity) && ListenerContainer.isContainer(
												(DefaultGraphCell) obj, graph)) { // new
							// children
							// must
							// be
							// added
							// to
							// all
							// cells containing entity
							DefaultGraphCell cell = graph.insertDuplicated(new Point(1,
									1), (Entity) value);
							try {
								graph.getListenerContainer().setParent(cell,
										(DefaultGraphCell) obj);
								if (graph.getListenerContainer()
										.parentHasVisibleContainers((DefaultGraphCell) obj)
										.isEmpty()) {
									// no visible elements
									graph.getGraphLayoutCache().setVisible(cell, false);
								}
							} catch (WrongParent e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} // inserted cell
							
						}
					}
				}
			}
		}
	}

	private static boolean contains(ModelJGraph graph, Entity entity2) {
		boolean found=false;
		for (int k=0;k<graph.getModel().getRootCount() && !found;k++){
			if (graph.getModel().getRootAt(k) instanceof DefaultGraphCell){
				if (((DefaultGraphCell)(graph.getModel().getRootAt(k))).getUserObject()!=null)
					found= ((DefaultGraphCell)(graph.getModel().getRootAt(k))).getUserObject().equals(entity2);
			}
		}
		return found;
	}

	/**
	 * Description of the Method
	 * 
	 * @param id
	 *            Description of the Parameter
	 * @param cf
	 *            Description of the Parameter
	 */
	private void removeValue(final String id, Field cf) {
		try {

			Class params[] = { "".getClass() };
			final Object paramVal[] = { id };
			String mname = "remove"
					+ cf.getName().substring(0, 1).toUpperCase()
					+ cf.getName().substring(1, cf.getName().length())
					+ "Element";

			/*
			 * java.lang.reflect.Method[] mms = entity.getClass().getMethods();
			 * for (int k = 0; k < mms.length; k++) { Class[] c =
			 * mms[k].getParameterTypes(); }
			 */
			final java.lang.reflect.Method m = this.entity.getClass()
					.getMethod(mname, params);

			this.confirm.add(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						m.invoke(entity, paramVal);

						removeEntityFromCellChilds(id);

						// System.err.println("removing "+id);
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
			});

			// No need of undo here, since the undo consists in leaving it as it
			// is now and not executing the remove action

		} catch (Exception iae) {
			iae.printStackTrace();
		}
	}

	/**
	 * Gets the collection attribute of the GeneralEditionPanel object
	 * 
	 * @param cf
	 *            Description of the Parameter
	 * @return The collection value
	 */
	private Enumeration getCollection(Field cf) {
		try {

			Class params[] = {};
			Object paramVal[] = {};
			String mname = "get" + cf.getName().substring(0, 1).toUpperCase()
					+ cf.getName().substring(1, cf.getName().length())
					+ "Elements";
			java.lang.reflect.Method[] mms = entity.getClass().getMethods();

			java.lang.reflect.Method m = this.entity.getClass().getMethod(
					mname, params);

			return (Enumeration) m.invoke(entity, paramVal);
		} catch (Exception iae) {
			iae.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the collectionType attribute of the GeneralEditionPanel object
	 * 
	 * @param cf
	 *            Description of the Parameter
	 * @return The collectionType value
	 */
	private boolean isCollectionType(Field cf) {
		return cf.getType().equals(ingenias.editor.TypedVector.class);
	}

	/**
	 * Gets the collectionType attribute of the GeneralEditionPanel object
	 * 
	 * @param cf
	 *            Description of the Parameter
	 * @return The collectionType value
	 * @exception Exception
	 *                Description of the Exception
	 */
	private Class getCollectionType(Field cf) throws Exception {

		Class params[] = {};
		Object paramVal[] = {};
		String mname = "get" + cf.getName().substring(0, 1).toUpperCase()
				+ cf.getName().substring(1, cf.getName().length()) + "Type";
		java.lang.reflect.Method[] mms = entity.getClass().getMethods();

		java.lang.reflect.Method m = this.entity.getClass().getMethod(mname,
				params);

		return (Class) m.invoke(entity, paramVal);
	}

	/**
	 * Description of the Method
	 * 
	 * @param args
	 *            Description of the Parameter
	 */
	public static void main(String args[]) {

	}

	/**
	 * Description of the Method
	 * 
	 * @param object
	 *            Description of the Parameter
	 * @param name
	 *            Description of the Parameter
	 * @param params
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private java.lang.reflect.Method findAppropriateMethod(Class object,
			String name, Class[] params) {
		java.lang.reflect.Method[] ms = object.getDeclaredMethods();
		boolean found = false;
		java.lang.reflect.Method current = null;
		for (int k = 0; k < ms.length && !found; k++) {
			current = ms[k];
			if (current.getName().equals(name)
					&& current.getParameterTypes().length == params.length) {
				Class[] cparams = current.getParameterTypes();
				boolean correct = true;
				for (int j = 0; j < cparams.length && correct; j++) {
					correct = correct && cparams[j].isAssignableFrom(params[j]);
				}
				found = correct;
			}
		}
		if (found) {
			return current;
		} else {
			return null;
		}
	}

	/**
	 * Gets the preferredWidget attribute of the GeneralEditionPanel object
	 * 
	 * @param c
	 *            Description of the Parameter
	 * @param field
	 *            Description of the Parameter
	 * @return The preferredWidget value
	 * @exception IllegalAccessException
	 *                Description of the Exception
	 * @exception InstantiationException
	 *                Description of the Exception
	 */
	private ingenias.editor.widget.Editable getPreferredWidget(Class c,
			String field) throws IllegalAccessException, InstantiationException {
		try {
			Object widget = obtainWidget(c, field);
			if (widget == null) {
				CustomJTextField widget1 = new CustomJTextField();
				widget1.setName(field);
				return widget1;
			}
			ingenias.editor.widget.Editable edit = (ingenias.editor.widget.Editable) widget;
			edit.setName(field);
			// ((ConfigurableWidget)edit).setDefaultValues();
			return edit;
			// cparams).newInstance(cval);
		} catch (ClassNotFoundException cnf) {
			cnf.printStackTrace();

			return new CustomJTextField();
		} catch (Exception e) {
			e.printStackTrace();

			// e.printStackTrace();
			return new CustomJTextField();
		}

	}

	private Object obtainWidget(Class c, String field)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		String className = "ingenias.editor.widget."
				+ c.getName().substring(c.getName().lastIndexOf(".") + 1,
						c.getName().length()) + "WidgetPreferences";
		Class[] cparams = {};
		Object[] cval = {};

		EntityWidgetPreferences ep = (EntityWidgetPreferences) Class.forName(
				className).newInstance();
		Object widget = ep.getWidget(field);
		return widget;
	}

	public boolean isModified() {
		boolean ismodified = false;
		Component[] comps = this.getComponents();
		for (int k = 0; k < comps.length && !ismodified; k++) {
			if (comps[k] instanceof GeneralEditionPanel) {
				ismodified = ismodified
						|| ((GeneralEditionPanel) comps[k]).isModified();
			}
		}
		return ismodified || undo.size() > 0;
	}

	public void undo() {
		// System.err.println(""+undo.size()+" actions to undo");
		/*
		 * Component[] comps=this.getComponents(); for (int
		 * k=0;k<comps.length;k++){ if (comps[k] instanceof
		 * GeneralEditionPanel){ ((GeneralEditionPanel)comps[k]).undo(); } }
		 */
		while (!undo.isEmpty()) {
			ActionListener al = (ActionListener) undo.lastElement();
			al.actionPerformed(null);
			undo.removeElementAt(undo.size() - 1);
		}
		undo.clear();
		confirm.clear();
	}

	public void confirmActions() {
		/*
		 * Component[] comps=this.getComponents(); for (int
		 * k=0;k<comps.length;k++){ if (comps[k] instanceof
		 * GeneralEditionPanel){
		 * ((GeneralEditionPanel)comps[k]).confirmActions(); } }
		 */
		for (ActionListener al : confirm) {
			al.actionPerformed(null);
		}
		undo.clear();
		confirm.clear();
	}

	/**
	 * Gets the preferredOrder attribute of the GeneralEditionPanel object
	 * 
	 * @return The preferredOrder value
	 * @exception IllegalAccessException
	 *                Description of the Exception
	 * @exception InstantiationException
	 *                Description of the Exception
	 */
	private String[] getPreferredOrder() throws IllegalAccessException,
	InstantiationException {
		String[] defaultResult = {};
		try {
			Class c = this.entity.getClass();
			String className = "ingenias.editor.widget."
					+ c.getName().substring(c.getName().lastIndexOf(".") + 1,
							c.getName().length()) + "WidgetPreferences";
			Class[] cparams = {};
			Object[] cval = {};

			EntityWidgetPreferences ep = (EntityWidgetPreferences) Class
					.forName(className).newInstance();
			return ep.getPreferredOrder();
		} catch (ClassNotFoundException cnf) {
			cnf.printStackTrace();

			return defaultResult;
		} catch (Exception e) {
			e.printStackTrace();

			// e.printStackTrace();
			return defaultResult;
		}

	}

	public void applyChanges() {

	}

	private void removeEntityFromCellChilds(final String id) {


		for (ModelJGraph graph:this.gm.getUOModels()){

			if (contains(graph,entity)){

				for (Object obj : graph.getRoots()) {
					if (obj instanceof DefaultGraphCell &&
							((DefaultGraphCell) obj).getUserObject()!=null && 							
							((DefaultGraphCell) obj).getUserObject().equals(entity)) { // deleted
						// element
						// must
						// be
						// deleted
						// from
						// all cells containing it

						Vector<DefaultGraphCell> children = graph.getListenerContainer().getChildren((DefaultGraphCell) obj);
						int k = 0;
						boolean found = false;
						while (k < children.size() && !found) {
							DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.elementAt(k);
							if (child.getUserObject() instanceof Entity) {
								found = (((Entity) child.getUserObject()).getId()
										.equalsIgnoreCase(id));
							} 
							k++;
						}
						if (found){
							graph.setSelectionCell(children.elementAt(k-1));
							MarqueeHandler.removeAction(graph, gm, om);

						}

					}
				}
			}
		}
	}

}
