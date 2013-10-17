
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz, Ruben Fuentes, Juan Pavon
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

package ingenias.editor;


import ingenias.editor.widget.DnDJTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;



public abstract class IDEGUI extends javax.swing.JFrame  {

	public  JPanel rightPanel=new JPanel();
	JMenuItem about = new JMenuItem();
	JTree arbolObjetos =new JTree();;
	DnDJTree arbolProyectos = new DnDJTree();//rootProject);
	Border borderForEntitiesView;
	Border borderForProjectView;
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel buttonModelPanel=new JPanel(new java.awt.BorderLayout());
	JMenuItem clearMessages = new JMenuItem();	

	JMenu menuCodeGenerator = new JMenu();
	JMenuItem copy = new JMenuItem();
	private JMenuItem copyImage = new JMenuItem();
	JMenuItem cpClipboard = new JMenuItem();
	JMenuItem delete = new JMenuItem();

	// GraphManager gm=GraphManager.initInstance(rootProject,arbolProyectos);
	private JMenu edit = new JMenu();
	protected JCheckBoxMenuItem editOnMessages;
	protected JCheckBoxMenuItem editPopUpProperties;
	private JMenuItem elimOverlap;

	JRadioButtonMenuItem  enableINGENIASView= new JRadioButtonMenuItem();
	JRadioButtonMenuItem  enableUMLView= new JRadioButtonMenuItem();
	JMenuItem exit = new JMenuItem();
	JMenu file = new JMenu();
	JMenuItem forcegc = new JMenuItem();
	private JRadioButtonMenuItem fullinforelats;
	BorderLayout gridLayout1 = new BorderLayout();
	GridLayout gridLayout2 = new GridLayout();
	JMenu help = new JMenu();
	private JMenuItem importFile;
	private JMenu jMenu3;
	JPanel jPanel1 = new JPanel();
	private JPanel jPanel2;
	JSplitPane jSplitPane1 = new JSplitPane();
	JSplitPane splitPanelDiagramMessagesPaneSeparator = new JSplitPane();
	private JRadioButtonMenuItem labelsonly;
	JMenuItem load = new JMenuItem();
	JTextPane logs = new JTextPane();
	JMenuBar mainMenuBar = new JMenuBar();
	JMenuItem manual = new JMenuItem();
	JPopupMenu messagesMenu = new JPopupMenu();
	JTabbedPaneWithCloseIcons messagespane = new JTabbedPaneWithCloseIcons();
	private JMenu modelingLanguageNotationSwitchMenu;
	JTextPane moduleOutput = new JTextPane();
	JMenu menuModules = new JMenu();
	JTextPane moutput = new JTextPane();
	JMenuItem newProject = new JMenuItem();
	JRadioButtonMenuItem noinformationrelats = new JRadioButtonMenuItem();
	JScrollPane outputpane = new JScrollPane();
	JMenuItem paste = new JMenuItem();
	JPanel pprin = new JPanel();
	JMenu preferences = new JMenu();
	JMenu profiles=new JMenu();
	JMenu project = new JMenu();
	JMenuItem properties = new JMenuItem();
	ButtonGroup propertiesEditModeSelection= new ButtonGroup();
	private JMenu propertiesModeMenu;
	JMenuItem redo = new JMenuItem();
	ButtonGroup relationshipSelection= new ButtonGroup();
	JMenuItem  resizeAll= new JMenuItem();
	JMenuItem  resizeAllDiagrams= new JMenuItem();
	javax.swing.tree.DefaultMutableTreeNode rootObjetos=
		new javax.swing.tree.DefaultMutableTreeNode("System Objects");
	javax.swing.tree.DefaultMutableTreeNode rootProject=
		new javax.swing.tree.DefaultMutableTreeNode("Project");
	JMenuItem save = new JMenuItem();
	JMenuItem saveas = new JMenuItem();
	JScrollPane scrollLogs = new JScrollPane();
	JScrollPane scrolloutput = new JScrollPane();
	JScrollPane scrollPaneForEntitiesView = new JScrollPane();
	JScrollPane scrollPaneForProyectView = new JScrollPane();
	private JButton Search;
	JEditorPane searchDiagramPanel= new JEditorPane();
	protected JTextField searchField;
	private JPanel searchPanel;
	JMenuItem selectall = new JMenuItem();
	JSplitPane splitPaneSeparatingProjectsAndEntitiesView = new JSplitPane();
	JMenuItem  switchINGENIASView= new JMenuItem();
	JMenuItem  switchUMLView= new JMenuItem();
	TitledBorder titleBoderForProjectView;

	TitledBorder titledBorderForEntitiesView;
	TitledBorder titledBorderForMessagesPane;
	JMenu menuTools = new JMenu();
	JMenuItem undo = new JMenuItem();
	ButtonGroup viewSelection= new ButtonGroup();
	JProgressBar pbar=new JProgressBar(1,100);

	public IDEGUI() {
		// To enable changes in cursor's shape
		this.getGlassPane().addMouseListener(new MouseAdapter(){});

		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	void about_actionPerformed(ActionEvent e) {

	}	

	void arbolObjetos_mouseClicked(MouseEvent e) {

	}

	
	void arbolProyectos_mouseClicked(MouseEvent e) {

	}
	
	void arbolProyectos_mouseEntered(MouseEvent e) {

	}
	void arbolProyectos_mouseExited(MouseEvent e) {

	}
	void arbolProyectos_mousePressed(MouseEvent e) {

	}
	void arbolProyectos_mouseReleased(MouseEvent e) {

	}
	void capture_actionPerformed(ActionEvent e) {
	}
	public void changePropertiesPanel(String oldName,String nname){
	 for (int k=0;k<messagespane.getTabCount();k++){
		 if (messagespane.getTitleAt(k).equalsIgnoreCase(oldName)){
			 messagespane.setTitleAt(k,nname);
		 }
	 }
	}
	void clearMessages_actionPerformed(ActionEvent e, JTextPane pane) {

	}
	void copy_actionPerformed(ActionEvent e) {

	}

	void cpClipboard_actionPerformed(ActionEvent e) {

	}

	void delete_actionPerformed(ActionEvent e) {

	}

	public void editOnMessages_selected() {
		// TODO Auto-generated method stub
		
	}

	public void editPopUpProperties_selected() {
		// TODO Auto-generated method stub
		
	};

	void elimOverlapActionPerformed(ActionEvent evt) {
		System.out.println("elimOverlap.actionPerformed, event=" + evt);
		//TODO add your code for elimOverlap.actionPerformed
	}

	void elimOverlapKeyPressed(KeyEvent evt) {
		System.out.println("elimOverlap.keyPressed, event=" + evt);
		//TODO add your code for elimOverlap.keyPressed
	}

	void elimOverlapMenuKeyTyped(MenuKeyEvent evt) {
		System.out.println("elimOverlap.menuKeyTyped, event=" + evt);
		//TODO add your code for elimOverlap.menuKeyTyped
	}

	void enableINGENIASView_actionPerformed(ActionEvent e) {

	}

	void enableRelatinshipLabels_actionPerformed(ActionEvent e) {

	}



	void enableUMLView_actionPerformed(ActionEvent e) {

	}


	void exit_actionPerformed(ActionEvent e) {

	}

	void forcegc_actionPerformed(ActionEvent e) {

	}


	public JPanel getButtonModelPanel() {
		return buttonModelPanel;
	}

	void importFileActionPerformed(ActionEvent evt) {
		System.out.println("importFile.actionPerformed, event=" + evt);
		//TODO add your code for importFile.actionPerformed
	}

	public boolean isEditPropertiesPopUp(){		 
		return this.editPopUpProperties.isSelected();
	}



	private void jbInit() throws Exception {
		borderForProjectView = BorderFactory.createLineBorder(Color.black,2);
		titleBoderForProjectView = new TitledBorder(borderForProjectView,"Project view");
		borderForEntitiesView = BorderFactory.createLineBorder(Color.black,2);
		titledBorderForEntitiesView = new TitledBorder(borderForEntitiesView,"Entities view");
		titledBorderForMessagesPane = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)),"Messages");
		this.getContentPane().setLayout(borderLayout2);
		file.setText("File");
		save.setEnabled(false);
		save.setText("Save");
		save.setName("Savefilemenu");
		save.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save_actionPerformed(e);
			}
		});
		load.setText("Load");
		load.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load_actionPerformed(e);
			}
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setJMenuBar(mainMenuBar);
		this.setTitle("INGENIAS Development Kit");
		this.setSize(625, 470);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				this_windowClosed(e);
			}
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
		});
		splitPaneSeparatingProjectsAndEntitiesView.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneSeparatingProjectsAndEntitiesView.setBottomComponent(scrollPaneForEntitiesView);
		splitPaneSeparatingProjectsAndEntitiesView.setTopComponent(scrollPaneForProyectView);
		jPanel1.setLayout(gridLayout1);
		arbolObjetos.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				arbolObjetos_mouseClicked(e);
			}
		});
		scrollPaneForProyectView.setAutoscrolls(true);
		scrollPaneForProyectView.setBorder(titleBoderForProjectView);
		scrollPaneForEntitiesView.setBorder(titledBorderForEntitiesView);
		edit.setText("Edit");
		copyImage.setText("Copy diagram as a file");
		copyImage.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e) {
				capture_actionPerformed(e);
			}
		});
		saveas.setText("Save as");
		saveas.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e) {
				saveas_actionPerformed(e);
			}
		});
		help.setText("Help");
		manual.setText("Tool manual");
		manual.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e) {
				manual_actionPerformed(e);
			}
		});
		about.setText("About");
		about.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e) {
				about_actionPerformed(e);
			}
		});
		project.setText("Project");
		copy.setText("Copy");
		copy.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e) {
				copy_actionPerformed(e);
			}
		});
		paste.setText("Paste");
		paste.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e) {
				paste_actionPerformed(e);
			}
		});
		exit.setText("Exit");
		exit.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e) {
				exit_actionPerformed(e);
			}
		});
		splitPanelDiagramMessagesPaneSeparator.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPanelDiagramMessagesPaneSeparator.setLastDividerLocation(150);
		pprin.setLayout(new BorderLayout());
		pprin.setName("DiagramPane");
		pprin.setPreferredSize(new Dimension(400, 300));
		pprin.add(BorderLayout.SOUTH,pbar);
		pbar.setVisible(false);
		jSplitPane1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		scrollLogs.setBorder(titledBorderForMessagesPane);
		scrollLogs.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				jScrollPane3_keyPressed(e);
			}
		});
		this.clearMessages.setText("Clear");
		clearMessages.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearMessages_actionPerformed(e, (JTextPane)messagesMenu.getInvoker());
			}
		});
		forcegc.setText("Force GC");
		forcegc.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				forcegc_actionPerformed(e);
			}
		});

		menuTools.setText("Tools");
		menuCodeGenerator.setText("Code Generator");
		profiles.setText("Profiles");
		
		menuModules.setText("Modules");
		this.properties.setText("Properties");
		properties.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				properties_actionPerformed(e);
			}
		});
		moutput.setEditable(false);
		moutput.setSelectionStart(0);
		moutput.setText("");
		moduleOutput.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				moduleOutput_mouseClicked(e);
			}
		});
		moduleOutput.setFont(new java.awt.Font("Monospaced", 0, 11));
		logs.setContentType("text/html");
		logs.setEditable(false);
		logs.setText("");
		logs.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				logs_mouseClicked(e);
			}
		});
		logs.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				logs_componentResized(e);
			}
		});
		newProject.setText("New");
		newProject.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newProject_actionPerformed(e);
			}
		});
		undo.setText("Undo");
		undo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				undo_actionPerformed(e);
			}
		});
		redo.setText("Redo");
		redo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redo_actionPerformed(e);
			}
		});
		delete.setText("Delete");
		delete.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delete_actionPerformed(e);
			}
		});
		selectall.setText("Select all");
		selectall.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectall_actionPerformed(e);
			}
		});
		cpClipboard.setText("Copy diagram to clipboard");
		cpClipboard.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cpClipboard_actionPerformed(e);
			}
		});
		preferences.setText("Preferences");

		enableUMLView.setToolTipText("UML view" +
		"instead of its type");
		enableUMLView.setText("Enable UML view from now on");
		enableUMLView.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enableUMLView_actionPerformed(e);
			}
		});
		enableINGENIASView.setToolTipText("INGENIAS view");
		enableINGENIASView.setText("Enable INGENIAS view from now on");
		enableINGENIASView.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enableINGENIASView_actionPerformed(e);
			}
		});
		
		switchINGENIASView.setToolTipText("Switch to INGENIAS view");
		switchINGENIASView.setText("Switch to INGENIAS view");
		switchINGENIASView.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchINGENIASView_actionPerformed(e);
			}			
		});
		
		switchUMLView.setToolTipText("Switch to UML view");
		switchUMLView.setText("Switch to UML view");
		switchUMLView.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchUMLView_actionPerformed(e);
			}			
		});

		resizeAll.setToolTipText("Resize all");
		resizeAll.setText("Resize all entities within current diagram");
		resizeAll.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resizeAll_actionPerformed(e);
			}
		});

		resizeAllDiagrams.setToolTipText("Resize all diagrams");
		resizeAllDiagrams.setText("Resize all entities within all defined diagram");
		resizeAllDiagrams.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resizeAllDiagrams_actionPerformed(e);
			}
		});
		
		JMenuItem workspaceEntry=new JMenuItem("Switch workspace");
		workspaceEntry.setToolTipText("Change current workspace");
		workspaceEntry.setText("Switch workspace");
		workspaceEntry.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeWorkspace(e);
			}
		});
		preferences.add(workspaceEntry);
		preferences.add( resizeAll);
		preferences.add( resizeAllDiagrams);
		{
			elimOverlap = new JMenuItem();
			preferences.add(elimOverlap);
			elimOverlap.setText("Eliminate overlap");
			elimOverlap.setAccelerator(KeyStroke.getKeyStroke("F3"));
			elimOverlap.addMenuKeyListener(new MenuKeyListener() {
				public void menuKeyPressed(MenuKeyEvent evt) {
					System.out.println("elimOverlap.menuKeyPressed, event="
							+ evt);
					//TODO add your code for elimOverlap.menuKeyPressed
				}
				public void menuKeyReleased(MenuKeyEvent evt) {
					System.out.println("elimOverlap.menuKeyReleased, event="
							+ evt);
					//TODO add your code for elimOverlap.menuKeyReleased
				}
				public void menuKeyTyped(MenuKeyEvent evt) {
					elimOverlapMenuKeyTyped(evt);
				}
			});
			elimOverlap.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent evt) {
					elimOverlapKeyPressed(evt);
				}
			});
			elimOverlap.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					elimOverlapActionPerformed(evt);
				}
			});
		}
		{
			modelingLanguageNotationSwitchMenu = new JMenu();
			preferences.add(modelingLanguageNotationSwitchMenu);
			modelingLanguageNotationSwitchMenu.setText("Modelling language");
			modelingLanguageNotationSwitchMenu.add(enableINGENIASView);
			
			viewSelection.add(enableINGENIASView);
			modelingLanguageNotationSwitchMenu.add(enableUMLView);
			viewSelection.add(enableUMLView);
			
			enableINGENIASView.setSelected(true);
			modelingLanguageNotationSwitchMenu.add(switchUMLView);
			modelingLanguageNotationSwitchMenu.add(switchINGENIASView);
		}
		{
			propertiesModeMenu = new JMenu();
			preferences.add(propertiesModeMenu);
			propertiesModeMenu.setText("Edit Properties Mode");
			{
				editPopUpProperties = new JCheckBoxMenuItem();
				propertiesModeMenu.add(editPopUpProperties);
				editPopUpProperties
				.setText("Edit Properties in a PopUp Window");
				editPopUpProperties.setSelected(true);
				editPopUpProperties.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						editPopUpProperties_selected();						
					}
				}	);
				propertiesEditModeSelection.add(editPopUpProperties);
			}
			{
				editOnMessages = new JCheckBoxMenuItem();
				propertiesModeMenu.add(editOnMessages);
				editOnMessages.setText("Edit Properties in Messages Panel");
				propertiesEditModeSelection.add(editOnMessages);
				editOnMessages.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						editOnMessages_selected();						
					}
				}	);
			}
		}
		
		mainMenuBar.add(file);
		mainMenuBar.add(edit);
		mainMenuBar.add(project);
		mainMenuBar.add(menuModules);
		mainMenuBar.add(profiles);
		mainMenuBar.add(preferences);
		mainMenuBar.add(help);
		file.add(newProject);
		file.add(load);
		{
			importFile = new JMenuItem();
			file.add(importFile);
			importFile.setText("Import file");
			importFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					importFileActionPerformed(evt);
				}
			});
		}
		file.add(save);
		file.add(saveas);
		file.addSeparator();
		file.add(exit);
		file.addSeparator();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(buttonModelPanel, BorderLayout.WEST);
		this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
		rightPanel.add(splitPanelDiagramMessagesPaneSeparator,BorderLayout.CENTER);
		jSplitPane1.add(splitPaneSeparatingProjectsAndEntitiesView, JSplitPane.LEFT);
		splitPaneSeparatingProjectsAndEntitiesView.add(scrollPaneForProyectView, JSplitPane.TOP);
		{
			jPanel2 = new JPanel();
			BorderLayout jPanel2Layout = new BorderLayout();
			jPanel2.setLayout(jPanel2Layout);
			splitPaneSeparatingProjectsAndEntitiesView.add(jPanel2, JSplitPane.BOTTOM);
			jPanel2.add(jPanel1, BorderLayout.SOUTH);
			jPanel2.add(scrollPaneForEntitiesView, BorderLayout.CENTER);
		}
		jSplitPane1.add(rightPanel, JSplitPane.RIGHT);
		splitPanelDiagramMessagesPaneSeparator.add(pprin, JSplitPane.TOP);
		splitPanelDiagramMessagesPaneSeparator.add(messagespane, JSplitPane.BOTTOM);
		JScrollPane scrollSearchDiagram=new JScrollPane();
		scrollSearchDiagram.getViewport().add(searchDiagramPanel,null);
		searchDiagramPanel.setContentType("text/html");
		searchDiagramPanel.setEditable(false);


		messagespane.addConventionalTab(scrollLogs,   "Logs");			
		scrollLogs.getViewport().add(logs, null);
		scrolloutput.getViewport().add(this.moduleOutput, null);
		messagespane.addConventionalTab(scrolloutput,  "Module Output");
		messagespane.addConventionalTab(scrollSearchDiagram, "Search");
		scrolloutput.getViewport().add(moduleOutput, null);
		{
			searchPanel = new JPanel();
			FlowLayout searchPanelLayout = new FlowLayout();
			searchPanelLayout.setVgap(1);
			searchPanel.setLayout(searchPanelLayout);
			jPanel1.add(searchPanel, BorderLayout.SOUTH);
			searchPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			{
				searchField = new JTextField();
				searchPanel.add(searchField);
				searchField.setColumns(15);
			
				searchField.addKeyListener(new KeyAdapter() {
					public void keyTyped(KeyEvent evt) {
						searchFieldKeyTyped(evt);
					}
				});
			}
			{
				Search = new JButton();
				scrollPaneForProyectView.setViewportView(arbolProyectos);
				scrollPaneForEntitiesView.setViewportView(arbolObjetos);
				searchPanel.add(Search);

				Search.setIcon(new ImageIcon("images/lense.png"));
				Search.setPreferredSize(new java.awt.Dimension(20, 18));
				Search.setIconTextGap(0);
				Search.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						SearchActionPerformed(evt);
					}
				});
			}
		}
		edit.add(undo);
		edit.add(redo);
		edit.addSeparator();
		edit.add(copy);
		edit.add(paste);
		edit.add(delete);
		edit.add(selectall);
		edit.addSeparator();
		edit.add(copyImage);
		edit.add(cpClipboard);
		help.add(manual);
		help.add(about);
		help.add(forcegc);

		menuModules.add(menuTools);
		menuModules.add(menuCodeGenerator);
		messagesMenu.add(this.clearMessages);
		project.add(    this.properties);

		project.addSeparator();
		jSplitPane1.setDividerLocation(250);
		splitPaneSeparatingProjectsAndEntitiesView.setDividerLocation(250);
		arbolProyectos.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				arbolProyectos_mouseClicked(e);
			}
		});
		splitPanelDiagramMessagesPaneSeparator.setDividerLocation(400);
	}
	
	void changeWorkspace(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	void jScrollPane3_keyPressed(KeyEvent e) {
		/*   System.err.println("pulsado");
		 */
	}
	abstract void load_actionPerformed(ActionEvent e) ;
	void logs_componentHidden(ComponentEvent e) {

	}
	void logs_componentMoved(ComponentEvent e) {

	}
	void logs_componentResized(ComponentEvent e) {
//		System.err.println("pulsado");
		Point p=new Point(0,(int)logs.getSize().getHeight());
		scrollLogs.getViewport().setViewPosition(p);
		/*   Point p=scrollLogs.getViewport().getGraphLayoutCachePosition();
		 p.y=p.y+10;
		 scrollLogs.getViewport().setViewPosition(p);*/
	}
	void logs_componentShown(ComponentEvent e) {

	}
	void logs_mouseClicked(MouseEvent e) {

	}

	void logs_mouseEntered(MouseEvent e) {

	}
	void logs_mouseExited(MouseEvent e) {

	}

	void logs_mousePressed(MouseEvent e) {

	}

	void logs_mouseReleased(MouseEvent e) {

	}

	void manual_actionPerformed(ActionEvent e) {

	}

	void moduleOutput_mouseClicked(MouseEvent e) {

	}

	void newProject_actionPerformed(ActionEvent e) {

	}

	void paste_actionPerformed(ActionEvent e) {

	}

	void properties_actionPerformed(ActionEvent e) {

	}

	void redo_actionPerformed(ActionEvent e) {

	}
	public void replaceCurrentTrees(DnDJTree arbolProyecto, JTree arbolObjetos2){
		this.arbolProyectos=arbolProyecto;
		this.arbolObjetos=arbolObjetos2;
		arbolObjetos.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				arbolObjetos_mouseClicked(e);
			}
		});
		scrollPaneForProyectView.setViewportView(arbolProyectos);
		scrollPaneForEntitiesView.setViewportView(arbolObjetos);
		arbolProyectos.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				arbolProyectos_mouseClicked(e);
			}
		});
		this.arbolProyectos.setCellRenderer(new ProjectTreeRenderer());
		this.arbolObjetos.setCellRenderer(new ProjectTreeRenderer());
	}

	void resizeAll_actionPerformed(ActionEvent e) {

	}

	void resizeAllDiagrams_actionPerformed(ActionEvent e) {

	};
	void save_actionPerformed(ActionEvent e) {

	}

	void saveas_actionPerformed(ActionEvent e) {

	}

	public void SearchActionPerformed(ActionEvent evt) {
		System.out.println("Search.actionPerformed, event=" + evt);
		//TODO add your code for Search.actionPerformed
	}

	abstract public void searchFieldKeyTyped(KeyEvent evt);

	void selectall_actionPerformed(ActionEvent e) {

	}



	
	
	public void setButtonModelPanel(JPanel buttonModelPanel) {
		this.buttonModelPanel = buttonModelPanel;
	}

	public void switchINGENIASView_actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		
	}





	public void switchUMLView_actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

void this_windowClosed(WindowEvent e) {

}

	/*public void labelsonlyActionPerformed(ActionEvent evt) {
		System.out.println("labelsonly.actionPerformed, event=" + evt);
		//TODO add your code for labelsonly.actionPerformed
	}

	public void noinformationrelatsActionPerformed(ActionEvent evt) {
		System.out.println("noinformationrelats.actionPerformed, event=" + evt);
		//TODO add your code for noinformationrelats.actionPerformed
	}

	public void fullinforelatsActionPerformed(ActionEvent evt) {
		System.out.println("fullinforelats.actionPerformed, event=" + evt);

	}*/
	
	void this_windowClosing(WindowEvent e) {

	};
	void undo_actionPerformed(ActionEvent e) {

	};

}