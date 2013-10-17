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
package ingenias.editor.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JWindow;

import ingenias.editor.GUIResources;
import ingenias.editor.IDE;
import ingenias.editor.IDEAbs;
import ingenias.editor.IDEState;
import ingenias.editor.utils.DialogWindows;

public class AutomaticBackupAction implements Runnable{
	Thread backupthread=null;
	boolean finish=false;
	int backupFrequency=0;
	IDEState ids;
	GUIResources resources;
	int counter=0;
	public final static int MAXIMUM_BACKUPS=10;

	public AutomaticBackupAction(IDEState ids, GUIResources resources, int backupFrequency){
		this.backupFrequency=backupFrequency;
		this.ids=ids;
		this.resources=resources;
	}

	public void startBackup(){
		this.backupthread=new Thread(this);
		this.backupthread.start();
	}

	public boolean isStarted(){
		return backupthread!=null;
	}

	private void moveFile(File orig, File target) throws IOException{
		byte buffer[]=new byte[1000];
		int bread=0;
		FileInputStream fis=new FileInputStream(orig);
		FileOutputStream fos=new FileOutputStream(target);
		while (bread!=-1){
			bread=fis.read(buffer);
			if (bread!=-1)
				fos.write(buffer,0,bread);
		}
		fis.close();
		fos.close();
		orig.delete();
	}

	public int copyBackupFiles(){
		/*JFileChooser jfc = new JFileChooser();
		File homedir = jfc.getCurrentDirectory();
		File[] files=homedir.listFiles();
		int lastBackupCopied=0;		
		String targetName ="backup/idbackup"+lastBackupCopied+".xml";
		int copied=0;
		File backup=new File(targetName);
		while (backup.exists()){
			lastBackupCopied++;
			targetName ="backup/idbackup"+lastBackupCopied+".xml";
			backup=new File(targetName);
		}
		

		for (int k=0;k<MAXIMUM_BACKUPS;k++){
			String filename = homedir.getPath() + "/.idkbackup"+counter+".xml";
			File original=new File(filename);
			if (original.exists()){
				targetName ="backup/idbackup"+lastBackupCopied+".xml";
				backup=new File(targetName);
				try {
					moveFile(original,backup);
					copied++;
				} catch (IOException e) {
					e.printStackTrace();
					copied=-1;
				}
				lastBackupCopied++;
			}
		}
		return copied;*/
		return 0;
	}

	public void run() {
		try {
			while (!finish){
				Thread.currentThread().sleep(60*backupFrequency*1000);
				while (ids.isBusy()){
					Thread.currentThread().sleep(1000);
				}
	
				if (ids.isChanged()){
					final JWindow jw=DialogWindows.showMessageWindow("Security Backup in progress...",ids, resources);	
					resources.getMainFrame().setEnabled(false);
					
					new Thread(){
						public void run(){
							while (!jw.isVisible()){
								Thread.currentThread().yield();
							}
							PersistenceManager p = new PersistenceManager();
							JFileChooser jfc = new JFileChooser();
							File homedir = jfc.getCurrentDirectory();
							String filename = homedir.getPath() + "/.idk/idkbackup"+counter+".xml";			
							File backup=new File(filename);	        
							try {
								p.save(backup, ids);
							} catch (IOException e) {				
								e.printStackTrace();
							}
							counter=(counter+1)%MAXIMUM_BACKUPS;
							jw.setVisible(false);
							resources.getMainFrame().setEnabled(true);		
						}
					}.start();
					
				}
			}
		} catch (InterruptedException ie){
			ie.printStackTrace();
		}
		resources.getMainFrame().setEnabled(true);
	}

	public void stop(){
		finish=true;
	}



}
