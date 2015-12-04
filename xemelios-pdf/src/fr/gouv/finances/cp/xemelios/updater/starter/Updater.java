/*
 * Copyright 
 *   2006 axYus - www.axyus.com
 *   2006 C.Marchand - christophe.marchand@axyus.com
 * 
 * This file is part of XEMELIOS.
 * 
 * XEMELIOS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * XEMELIOS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package fr.gouv.finances.cp.xemelios.updater.starter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.gouv.finances.cp.utils.PropertiesExpansion;

/**
 * This class is responsible of patching xemelios installation directory
 * with previously downloaded files.
 * If no download is available or if it is incomplete, it destroys
 * the download directory and advise user he has to update again.
 * @author chm
 *
 */
public class Updater {
	
	private PropertiesExpansion props;
//	private static final transient String TMP_ENTRY = "fr.gouv.finances.cp.xemelios.updater.starter.Updater.tmp";
	private StringBuilder xmlScript;
	private PrintStream output;

	public Updater(PropertiesExpansion applicationProperties) {
		super();
                props = applicationProperties;
//		props = new PropertiesExpansion();
		xmlScript = new StringBuilder("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<update-script>\n");
//		for(Object s:System.getProperties().keySet()) props.put(s,System.getProperty((String)s));
		boolean bRet = false;
		//Properties props = new Properties();
		File tmpDir = new File(applicationProperties.getProperty("xemelios.dir")+"/tmp-upd");
		File outputFile = new File(applicationProperties.getProperty("xemelios.dir"),"update.log");
		//if(outputFile.exists()) outputFile.delete();
		try {
			output = new PrintStream(new FileOutputStream(outputFile,false));
		} catch(FileNotFoundException fnfEx) {
			output = System.out;
			try {
				System.out.println("unable to write to "+outputFile.getCanonicalPath());
			} catch(Throwable t) {}
		}
		if(tmpDir.exists()) {
			try {
				File batch = new File(tmpDir,"update-script.xem");
				if(batch.exists()) {
					ArrayList<String> lines = new ArrayList<String>();
					BufferedReader br = new BufferedReader(new FileReader(batch));
					String line = br.readLine();
					while(line!=null) {
						lines.add(line);
						line=br.readLine();
					}
					br.close();
					
					for(String s:lines) {
						if(s.startsWith("copy")) checkAvailability(s,tmpDir);
					}
					
					for(String s:lines) {
						if(s.startsWith("copy")) doCopy(s,tmpDir);
						else if(s.startsWith("delete")) doDelete(s);
						else if(s.startsWith("install-component")) doInstallComponent(s);
						else if(s.startsWith("remove-component")) doRemoveComponent(s);
						else if(s.startsWith("configuration")) doConfiguration(s);
					}
					
					xmlScript.append("</update-script>");
					File f = new File(props.replace("${xemelios.prg}"),"update-script.xml");
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(xmlScript.toString().getBytes("ISO-8859-1"));
					fos.flush(); fos.close();
					bRet = true;
				}
			} catch(IOException ioEx) {
				bRet = false;
				ioEx.printStackTrace();
			} catch(Exception ex) {
				bRet = false;
				ex.printStackTrace();
			}
			if(!bRet) {
				JOptionPane.showMessageDialog((JFrame)null,"Il y a eu un problème lors de la mise à jour de Xemelios.\nVous devez recommencer le processus de mise à jour.","Erreur",JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog((JFrame)null,"Xemelios a été correctement mis à jour.","Mise à jour",JOptionPane.INFORMATION_MESSAGE);
			}
			// at least, destroy directory
			for(File f:tmpDir.listFiles()) f.delete();
			tmpDir.delete();
		} else {
			output.println("no update dir found at "+tmpDir.getAbsolutePath());
		}
	}
	
	private void doCopy(String line,File updateDir) throws Exception {
		String[] args=splittLine(line);
		File f = new File(updateDir,args[1]);
		FileInfo fi = new FileInfo(f.getCanonicalPath(),props.replace(args[2]));
		if(!copyFile(fi)) throw new Exception();
		output.println("copying from "+fi.tmpFileName+" to "+fi.destFileName);
	}
	private void doDelete(String line) throws Exception {
		String[] args = splittLine(line);
		File f = new File(props.replace(args[1]));
		if(f.exists()) f.delete();
		output.println("deleting "+props.replace(args[1]));
	}
	private void doInstallComponent(String line) throws Exception {
		String[] args = splittLine(line);
		String id = args[1];
		String version = args[2];
		String description = args[3];
		xmlScript.append(" <component id=\"").append(id).append("\" version=\"").append(version).append("\" description=\"").append(description).append("\"/>\n");
		output.println("preparing to install component "+id+" - "+version+" - "+description);
	}
	private void doRemoveComponent(String line) throws Exception {
		String[] args = splittLine(line);
		String id = args[1];
		//String version = args[2];
		//String description = args[3];
		xmlScript.append(" <remove-component id=\"").append(id).append("\"/>\n");
		output.println("preparing to remove component "+id);
	}
	private void doConfiguration(String line) throws Exception {
		String[] args = splittLine(line);
		String className = args[1];
		xmlScript.append(" <configurator class=\"").append(className).append("\"");
		if(args.length>2) {
			xmlScript.append(" >\n");
			for(int i=2;i<args.length;) {
				xmlScript.append("  <param name=\"").append(args[i++]).append("\">").append(args[i++]).append("</param>\n");
			}
			xmlScript.append(" </configurator>\n");
		} else {
			xmlScript.append("/>\n");
		}
		output.println("preparing configurator "+className);
	}

	private void checkAvailability(String line,File updateDir) throws Exception {
		String[] args = splittLine(line);
		File f = new File(updateDir,args[1]);
		FileInfo fi = new FileInfo(f.getCanonicalPath(),args[2]);
		if(!isAvailable(fi)) throw new Exception(fi.tmpFileName+" not available.");
	}
	
	private boolean copyFile(FileInfo fi) {
		try {
			File dest = new File(fi.destFileName);
			File dir = dest.getParentFile();
			if(dir!=null && !dir.exists()) {
				output.println("trying to create "+dir.getAbsolutePath()+" directory");
				dir.mkdirs();
			}
			if(dest.exists()) dest.delete();
			FileInputStream fis = new FileInputStream(fi.tmpFileName);
			FileOutputStream fos = new FileOutputStream(dest);
			FileChannel inputChannel = fis.getChannel();
			FileChannel outputChannel = fos.getChannel();
			outputChannel.transferFrom(inputChannel,0,inputChannel.size());
			outputChannel.close();
			inputChannel.close();
			return true;
		} catch(IOException ioEx) {
			output.println("while copying "+fi.tmpFileName+" to "+fi.destFileName+"\n"+ioEx.getMessage());
			return false;
		}
	}

	private static boolean isAvailable(FileInfo fi) {
		File f = new File (fi.tmpFileName);
		return f.exists();
	}
	private static String[] splittLine(String line) {
		StringTokenizer st = new StringTokenizer(line,"|");
		int tokens = st.countTokens();
		String[] ret = new String[tokens];
		for(int i=0;i<tokens;i++) ret[i]=st.nextToken();
		return ret;
	}
	private class FileInfo {
		String tmpFileName, destFileName;
		public FileInfo(String source, String dest) {
			super();
			this.tmpFileName=source;
			this.destFileName=dest;
		}
	}
}
