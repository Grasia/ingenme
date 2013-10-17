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
package ingenias.idegen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;






public class MainGUI {
	private static boolean saved;
	DefaultListModel dlm=new DefaultListModel();
	Hashtable<String,Properties> projectsProperties=new Hashtable<String,Properties>();
	Hashtable<String,File> propertiesFiles=new Hashtable<String,File>();
	private JTextArea logs;
	private JTextArea logserror;

	private JList existingProjects;


	public static void main(String args[]) throws FileNotFoundException, IOException{
		
		Properties prop=new Properties();
		boolean alreadyAccepted=false;
		char acceptance=' ';
		File acceptanceFile=new File (""+System.getProperty("user.home")+"/.ingened.properties");
		if (System.getProperty("user.home")!=null){
			alreadyAccepted=acceptanceFile.exists();
		}
		String header="\n\n\nINGENME Copyright (C) 2010  Jorge J. Gomez-Sanz\n"+
				"This file is part of the INGENME tool. INGENME is an open source meta-editor\n"+
				"which produces customized editors for user-defined modeling languages\n"+
				"This program comes with ABSOLUTELY NO WARRANTY. This is free software, and you \n" +
				"are welcome to use it according to the following terms :\n\n";
		String toBeShown="";
		if (!alreadyAccepted){



			toBeShown=toBeShown+header;

			StringBuffer licenseContent=new StringBuffer();
			FileInputStream fis=new FileInputStream("LICENSE_EN.txt");
			int read=1;
			while (read!=-1){
				read=fis.read();
				if (read!=-1)
					licenseContent.append((char)read);
			}
			fis.close();
			toBeShown=toBeShown+("\n"+licenseContent);

			JTextPane textArea=new JTextPane();			
			textArea.setText(toBeShown);
			JPanel content=new JPanel();
			content.setLayout(new BorderLayout());
			JScrollPane jsp=new JScrollPane(textArea);
			jsp.setPreferredSize(new Dimension(600,200));
			content.add(jsp, BorderLayout.CENTER);
			content.add(new JLabel("Do you accept this license?"), BorderLayout.SOUTH);		

			int result = JOptionPane.showConfirmDialog(
					null, content, "License acceptance",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (result==JOptionPane.YES_OPTION){
				acceptance='Y';
				acceptanceFile.createNewFile();
			}


		}
		if (acceptance=='Y' || alreadyAccepted){
		
			prop.load(new FileInputStream("defaultproject.properties"));
			MainGUI mgui=new MainGUI();
		
		}  else 
			System.exit(-1);
		
	}

	public MainGUI(){
		final JFrame jf=new JFrame("Project manager");

		initializeProjects();
		initializeList();

		GridBagConstraints gbc=null;
		JPanel main=new JPanel(new BorderLayout());
		JPanel projects=new JPanel(new GridBagLayout());
		main.add(projects,BorderLayout.NORTH);
		jf.getContentPane().add(main);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		JLabel existingProjectsl=new JLabel("Available Projects:");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.LINE_START;
		projects.add(existingProjectsl,gbc);

		existingProjects=new JList(dlm);
		JScrollPane jspprojects=new JScrollPane(existingProjects);
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=2;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.LINE_START;
		projects.add(jspprojects,gbc);

		final JButton generateEditor=new JButton("Generate Editor for Current Metamodel");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=4;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.LINE_START;
		projects.add(generateEditor,gbc);
		generateEditor.setToolTipText("Creates a new editor using the metamodel.xml file from the selected project");

		JButton editProjectData=new JButton("Edit Prop of Selected Project");
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=2;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.LINE_START;
		projects.add(editProjectData,gbc);

		final JButton editMetamodelINGENED=new JButton("Edit Metamodel With INGENED");
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=3;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.LINE_START;
		projects.add(editMetamodelINGENED,gbc);

		final JButton editMetamodelHand=new JButton("Edit metamodel.xml file manually");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=3;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.LINE_START;
		projects.add(editMetamodelHand,gbc);

		JButton createProject=new JButton("Create new project");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=2;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.LINE_START;
		projects.add(createProject,gbc);

		final JButton launchEditor=new JButton("Launch Generated Editor");
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=4;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.LINE_START;
		projects.add(launchEditor,gbc);


		logs=new JTextArea(10,10);
		logserror=new JTextArea(10,10);

		JTabbedPane tabpane=new JTabbedPane();
		JScrollPane jsp=new JScrollPane(logs);
		jsp.setBorder(BorderFactory.createTitledBorder("logs"));
		jsp.setMinimumSize(logs.getMinimumSize());
		tabpane.add("Error", jsp);
		tabpane.add("Output", new JScrollPane(logserror));
		main.add(tabpane,BorderLayout.CENTER);


		editMetamodelHand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Properties props=projectsProperties.get(existingProjects.getSelectedValue().toString());
				String folder=props.getProperty("projectResources");
				final String mfile=folder+"/metamodel/metamodel.xml";
				if (new File(mfile).exists()){
					String content=readFile(mfile);

					editMetamodelHand.setEnabled(false);
					final JFrame feditor=new JFrame();
					feditor.getContentPane().setLayout(new BorderLayout());


					feditor.setTitle("Editing "+props.getProperty("toolname")+" metamodel");
					JPanel acceptCancel=new JPanel();
					final JTextArea editor=new JTextArea(80,40);
					editor.setText(content);
					editor.setLineWrap(true);
					editor.setWrapStyleWord(true);
					feditor.getContentPane().add(new JScrollPane(editor),BorderLayout.CENTER);
					JButton ok = new JButton("OK");
					JButton cancel = new JButton("CANCEL");
					acceptCancel.add(ok);
					acceptCancel.add(cancel);
					feditor.getContentPane().add(acceptCancel,BorderLayout.SOUTH);
					ok.addActionListener(new ActionListener() {	
						@Override
						public void actionPerformed(ActionEvent e) {
							String text=editor.getText();
							FileOutputStream fos;
							try {
								fos = new FileOutputStream(mfile);
								fos.write(text.getBytes());
								fos.close();
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							editMetamodelHand.setEnabled(true);
							feditor.setVisible(false);
						}
					});
					cancel.addActionListener(new ActionListener() {	
						@Override
						public void actionPerformed(ActionEvent e) {
							feditor.setVisible(false);

							editMetamodelHand.setEnabled(true);

						}
					});
					feditor.addWindowListener(new WindowListener() {
						
						@Override
						public void windowOpened(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void windowIconified(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void windowDeiconified(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void windowDeactivated(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void windowClosing(WindowEvent e) {
							editMetamodelHand.setEnabled(true);
							
						}
						
						@Override
						public void windowClosed(WindowEvent e) {
							
							
						}
						
						@Override
						public void windowActivated(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}
					});
					feditor.pack();
					feditor.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(null,"Metamodel file "+mfile+" not found","Error editing metamodel", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		editMetamodelINGENED.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editMetamodelINGENED.setEnabled(false);
				SwingWorker<String, Void> sw=new SwingWorker<String, Void>(){
					@Override
					protected String doInBackground() throws Exception {
						try {
							logs.setText("");

							Properties props=projectsProperties.get(existingProjects.getSelectedValue().toString());
							String projResources=props.getProperty("projectResources");

							try {
								if (!(new File(projResources).exists())){
									projResources= System.getProperty("user.dir")+"/"+projResources;
									if (!(new File(projResources).exists())){
										throw new FileNotFoundException("Could not find the folder looking at the current path in "+projResources);
									}
								} 
								String ingenedMMFile=new File(projResources).getAbsolutePath()+"/metamodel/metamodelINGENED.xml";
								String conventionalMMFile=new File(projResources).getAbsolutePath()+"/metamodel/metamodel.xml";

								if (new File(ingenedMMFile).exists())
									launchINGENED(ingenedMMFile);
								else
								{
									createINGENEDDEscription(ingenedMMFile,conventionalMMFile);
									launchINGENED(ingenedMMFile);
								}
							} catch (FileNotFoundException fnf){

							}
							editMetamodelINGENED.setEnabled(true);
						} catch (Throwable t){
							t.printStackTrace();
						}
						return "";
					}








				};
				sw.execute();
			}
		});
		editProjectData.addActionListener(new ActionListener() {


			@Override
			public void actionPerformed(ActionEvent arg0) {
				int result=showProjectProperties(projectsProperties.get(existingProjects.getSelectedValue().toString()));
				if (result==JOptionPane.OK_OPTION){
					File f=propertiesFiles.get(existingProjects.getSelectedValue().toString());
					try {
						projectsProperties.get(existingProjects.getSelectedValue().toString()).save(
								new FileOutputStream(f), "Properties");

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					initializeProjects();
					initializeList();
				}
			}
		});

		createProject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String pname="";
				pname=JOptionPane.showInputDialog(jf, "Type a name for the project (no spaces):","Project name");
				if (pname!=null){
					if (new File("projects/"+pname).exists()){
						JOptionPane.showMessageDialog(jf,"That folder already exists","Error", JOptionPane.ERROR_MESSAGE);
					} else {
						new File("projects/"+pname).mkdir();
						new File("projects/"+pname+"/src").mkdir();
						new File("projects/"+pname+"/examples").mkdir();
						new File("projects/"+pname+"/images").mkdir();
						new File("projects/"+pname+"/licenses").mkdir();
						new File("projects/"+pname+"/libs").mkdir();
						new File("projects/"+pname+"/metamodel").mkdir();
						new File("projects/"+pname+"/pluginssrc").mkdir();
						new File("projects/"+pname+"/sounds").mkdir();
						File propertiesFile=new File("projects/"+pname+"/project.properties");
						FileOutputStream fos;
						try {
							fos = new FileOutputStream(propertiesFile);
							fos.write("#Please, read rules for properties files: http://java.sun.com/j2se/1.4.2/docs/api/java/util/Properties.html#load%28java.io.InputStream%29\n".getBytes());
							fos.write("#Set a name and a version for the editor\n".getBytes());
							fos.write("versionnumber=0\n".getBytes());
							fos.write("authorname=anonymous\n".getBytes());
							fos.write("vendor=anonymous\n".getBytes());
							fos.write("distributionURL=http://anonymous.com\n".getBytes());
							fos.write("#The dist folder points at the place where the new editor will be \n".getBytes());
							fos.write("#deployed. If the folder did not exist, a new one will be created\n".getBytes());
							if (new File(System.getProperty("user.home")+"/myeditor").exists()){
								int k=0;
								while (new File(System.getProperty("user.home")+"/myeditor"+k).exists()){
									k=k+1;
								}
								File edfolder=new File(System.getProperty("user.home")+"/myeditor"+k);
								fos.write(("dist=${user.home}/myeditor"+k+"\n").getBytes());
								fos.write(("toolname=MyEditor"+k+"\n").getBytes());
							} else {
								fos.write("dist=${user.home}/myeditor\n".getBytes());
								fos.write("toolname=MyEditor\n".getBytes());
							}
							fos.write("#The resources folder points at the necessary files for building\n".getBytes());
							fos.write("#the custom editor. Please, check the documentation to verify\n".getBytes());
							fos.write("#the number and type of resources needed.\n".getBytes());
							fos.write(("projectResources=projects/"+pname+"\n").getBytes());
							fos.close();
							fos = new FileOutputStream("projects/"+pname+"/metamodel/metamodel.xml");
							fos.write("".getBytes());
							fos.close();
							initializeProjects();
							initializeList();
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});

		generateEditor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				generateEditor.setEnabled(false);
				SwingWorker<String, Void> sw=new SwingWorker<String, Void>(){
					@Override
					protected String doInBackground() throws Exception {
						try {
							logs.setText("");
							Properties props=projectsProperties.get(existingProjects.getSelectedValue().toString());
							generateEditor(props);
							JOptionPane.showMessageDialog(jf, "Generation complete", "Generation Complete", JOptionPane.INFORMATION_MESSAGE);
						} catch (Throwable ex){
							JOptionPane.showMessageDialog(jf, "Error generating the editor", "Error", JOptionPane.ERROR_MESSAGE);	
						}

						generateEditor.setEnabled(true);
						return "";
					}


				};
				sw.execute();
			}
		});

		launchEditor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SwingWorker<String, Void> sw=new SwingWorker<String, Void>(){
					@Override
					protected String doInBackground() throws Exception {
						try {
							logs.setText("");
							Properties props=projectsProperties.get(existingProjects.getSelectedValue().toString());
							String editordistDir=props.getProperty("dist").replaceAll("\\$\\{user\\.home\\}",System.getProperty("user.home"));
							String locationOfBuildFile=(editordistDir+"/build.xml");
							if (!new File(locationOfBuildFile).exists()){
								JOptionPane.showMessageDialog(jf, "There is no generated editor yet. File "+locationOfBuildFile+" not found");
							} else {
								launchGeneratedEditor(editordistDir,
										locationOfBuildFile);

							}
						} catch (Throwable t){
							t.printStackTrace();
						}
						return "";
					}


				};
				sw.execute();
			}
		});

		jf.pack();
		jf.setLocationByPlatform(true);
		jf.setVisible(true);

	}

	protected String readFile(String mfile) {
		StringBuffer sb=new StringBuffer();
		int read=-1;
		FileInputStream fis=null;
		try {
			fis = new FileInputStream(mfile);

			do {
				read=fis.read();
				if (read!=-1)
					sb.append((char)read);
			} while(read!=-1);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	private void createINGENEDDEscription(String ingenedMMFile, String metamodelFile) {

		String content="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<project cid=\"0\" version=\"1.2\">"+
				"<projectproperties>"+
				"<projectproperty id=\"extfolder\" module=\"IDK\"  name=\"Extension Module Folder\" value=\"ext\"  tooltip=\"Folder where the IDE will find its new modules\" />"+
				"<projectproperty id=\"htmldoc\" module=\"HTML Document generator\"  name=\"HTML document folder\" value=\"html\"  tooltip=\"The document folder that will contain HTML version of this specification\" />"+
				"<projectproperty id=\"defaultOutput\" module=\"INGENME translator\"  name=\"Default file where results should be dumped\" value=\""+
				metamodelFile+"\"  tooltip=\"\" />"+

		"</projectproperties>"+
		"<leafpackages>"+
		"  <path>"+
		"   <package id=\"Project\"/>"+
		"  </path>"+
		"</leafpackages>"+
		"<objects>"+
		"</objects>"+
		"<relationships>"+
		"</relationships>"+
		"<models> "+
		"</models>"+
		"</project>";
		FileOutputStream fos;
		try {
			new File(ingenedMMFile).createNewFile();
			fos = new FileOutputStream(ingenedMMFile);
			fos.write(content.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private void launchINGENED(String ingenedMMFile) {
		Properties props=projectsProperties.get("INGENED:ingened");
		try{
			String editordistDir=props.getProperty("dist").replaceAll("\\$\\{user\\.home\\}",System.getProperty("user.home"));
			String locationOfBuildFile=(editordistDir+"/build.xml");
			if (!new File(locationOfBuildFile).exists()){
				// There is no INGENED
				generateINGENED();
				launchGeneratedEditor(editordistDir,
						locationOfBuildFile, ingenedMMFile);
			} else {
				launchGeneratedEditor(editordistDir,
						locationOfBuildFile, ingenedMMFile);

			}
		} catch (Throwable t){
			t.printStackTrace();
		}
	}

	private void generateINGENED() {
		try {
			Properties props=projectsProperties.get("INGENED:ingened");
			generateEditor(props);
		} catch (Throwable t){
			t.printStackTrace();
		}
	}

	private void generateEditor(Properties props) {
		File buildFile = new File("build.xml");
		Project p = new Project();
		logs.append("Generating editor \n");
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.init();
		PrintStream error = new PrintStream(new FilteredStream(new ByteArrayOutputStream(),logserror));
		PrintStream out = new PrintStream(new FilteredStream(new ByteArrayOutputStream(),logs));
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(error);
		consoleLogger.setOutputPrintStream(out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);

		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);

		for (Object key:props.keySet()){
			p.setProperty(key.toString(), props.getProperty(key.toString()).replaceAll("\\$\\{user\\.home\\}",System.getProperty("user.home")));
		}
		p.executeTarget(p.getDefaultTarget());
	};

	private void launchGeneratedEditor(String editordistDir,
			String locationOfBuildFile) {
		File buildFile = new File(locationOfBuildFile);
		Project p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		logs.append("Launching generated editor on "+editordistDir+"\n");
		p.init();
		PrintStream error = new PrintStream(new FilteredStream(new ByteArrayOutputStream(),logserror));
		PrintStream out = new PrintStream(new FilteredStream(new ByteArrayOutputStream(),logs));
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(error);
		consoleLogger.setOutputPrintStream(out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);

		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.setBasedir(editordistDir);
		p.executeTarget(p.getDefaultTarget());
	};

	private void launchGeneratedEditor(String editordistDir,
			String locationOfBuildFile, String specification) {
		File buildFile = new File(locationOfBuildFile);
		Project p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.setUserProperty("specfile", specification);

		p.init();
		PrintStream error = new PrintStream(new FilteredStream(new ByteArrayOutputStream(),logserror));
		PrintStream out = new PrintStream(new FilteredStream(new ByteArrayOutputStream(),logs));
		logs.append("Launching editor on "+editordistDir+" with specfile "+specification+"\n");
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(error);
		consoleLogger.setOutputPrintStream(out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);

		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.setBasedir(editordistDir);
		p.executeTarget("runidespec");

	};

	private void initializeList() {
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				dlm.clear();
				for(String key:projectsProperties.keySet()){
					dlm.addElement(key);
				}
			}

		});

	}

	private void initializeProjects() {
		File f=new File("projects");
		projectsProperties.clear();
		for (File folder:f.listFiles()){
			if (folder.isDirectory()){
				File[] propfiles = folder.listFiles(new FileFilter(){
					@Override
					public boolean accept(File arg0) {
						return (arg0.getName().toLowerCase().endsWith("project.properties"));
					}
				});
				if (propfiles.length==1){
					Properties props=new Properties();
					try {
						System.err.println("Loading "+propfiles[0].toString());
						props.load(new FileInputStream(propfiles[0]));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					projectsProperties.put(props.getProperty("toolname")+":"+folder.getName(),props );
					propertiesFiles.put(props.getProperty("toolname")+":"+folder.getName(), propfiles[0]);
				}
			}
		}
	}

	private static int showProjectProperties(final Properties prop) {
		final JDialog jd=new JDialog((JFrame)null,"Project data",true);
		saved=false;
		GridBagConstraints gbc=null;
		JPanel projectConfiguration=new JPanel(new GridBagLayout());
		gbc=new GridBagConstraints();

		JLabel tnamel=new JLabel("Tool name:");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(tnamel,gbc);

		final JTextField tname=new JTextField(prop.getProperty("toolname"),30);
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=0;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(tname,gbc);

		JLabel anamel=new JLabel("Author name:");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(anamel,gbc);

		final JTextField aname=new JTextField(prop.getProperty("authorname"),30);
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=1;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(aname,gbc);

		JLabel vnamel=new JLabel("Vendor name:");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=2;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(vnamel,gbc);

		final JTextField vname=new JTextField(prop.getProperty("vendor"),30);
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=2;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(vname,gbc);

		JLabel distnamel=new JLabel("Distribution URL:");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=3;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(distnamel,gbc);

		final JTextField distname=new JTextField(prop.getProperty("distributionURL"),30);
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=3;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(distname,gbc);

		JLabel distfolderl=new JLabel("Folder for the new editor:");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=4;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(distfolderl,gbc);

		final JTextField distfolder=new JTextField(prop.getProperty("dist"),30);
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=4;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(distfolder,gbc);

		JLabel presourcesl=new JLabel("Project resources");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=5;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(presourcesl,gbc);

		final JTextField presources=new JTextField(prop.getProperty("projectResources"),30);
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=5;
		gbc.anchor=GridBagConstraints.LINE_START;
		projectConfiguration.add(presources,gbc);

		JButton saveButton=new JButton("Store");
		gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=6;
		gbc.fill=GridBagConstraints.BOTH;
		projectConfiguration.add(saveButton,gbc);

		JButton cancelButton=new JButton("Cancel");
		gbc=new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=6;
		gbc.fill=GridBagConstraints.BOTH;
		projectConfiguration.add(cancelButton,gbc);
		jd.getContentPane().add(projectConfiguration);

		cancelButton.addActionListener(new ActionListener() {
			private boolean saved;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				jd.setVisible(false);		
				saved=false;
			}
		});
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				prop.setProperty("toolname",tname.getText());
				prop.setProperty("authorname",aname.getText());
				prop.setProperty("vendor",vname.getText());
				prop.setProperty("distributionURL",distname.getText());
				prop.setProperty("dist",distfolder.getText());
				prop.setProperty("projectResources",presources.getText());
				jd.setVisible(false);	
				saved=true;

			}
		});

		jd.pack();
		jd.setLocationByPlatform(true);
		jd.setVisible(true);

		if (saved)
			return JOptionPane.OK_OPTION;
		else 
			return JOptionPane.CANCEL_OPTION;
	}
}
