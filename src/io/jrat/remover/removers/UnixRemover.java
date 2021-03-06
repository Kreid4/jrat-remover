package io.jrat.remover.removers;

import io.jrat.remover.Detection;
import io.jrat.remover.Frame;
import io.jrat.remover.Main;
import io.jrat.remover.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Linux and BSD remover
 */
public class UnixRemover extends Remover {

	public UnixRemover(Frame frame) {
		super(frame);
	}

	@Override
	public List<Detection> perform(boolean dryrun) {
		List<Detection> detections = new ArrayList<Detection>();
		List<File> files = new ArrayList<File>();
		List<File> desktopentries = new ArrayList<File>();
		
		File dir = new File(System.getProperty("user.home") + "/.config/autostart/");
		
		if (!dir.exists()) {
			Utils.err("jRAT Remover", "No autostart directory found");
			return null;
		}
		
		for (File file : dir.listFiles()) {
			try {
				boolean add = false;
				String name = null;
				String path = null;
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line;
				
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					
					if (line.startsWith("Name=")) {
						name = line.split("=")[1];
					} else if (line.startsWith("Exec=")) {
						String command = line.substring(line.indexOf("="), line.length());
						
						if (command.contains("java") && command.contains("-jar")) {
							add = true;
							path = command.replace("java", "").replace("-jar", "").replace("\"", "").replace("'", "").replace("=", "").trim();
						}
					}
				}
				
				reader.close();
				
				if (add && path != null) {
					files.add(new File(path));
					desktopentries.add(file);
					
					if (name == null) {
						name = file.getName();
					}
					
					detections.add(new Detection(name, path));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		if (!dryrun) {
			for (int i = 0; i < files.size() && i < desktopentries.size(); i++) {
				File file = files.get(i);
				File launchagent = desktopentries.get(i);
				
				Main.debug("Deleting Stub: " + file.getAbsolutePath());
				Main.debug("Deleting Desktop File: " + launchagent.getAbsolutePath());
				
				if (file.exists()) {
					file.delete();
				}
				
				if (launchagent.exists()) {
					launchagent.delete();
				}
			}
		}
		
		return detections;
	}

}
