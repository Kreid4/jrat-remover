package com.redpois0n;

import java.io.File;

public class FixWin implements Fixer {

	public void fix() {
		String msg = "Are you sure you want to delete these jRAT servers and the registry keys?\n\n";
		for (File file : ScannerWin.files) {
			msg += file.getAbsolutePath() + "\n";
		}
			
		if (!Util.yesNo(msg)) {
			return;
		}
		
		
		try {
			for (int i = 0; i < ScannerWin.files.size() && i < ScannerWin.regkeys.size(); i++) {
				File file = ScannerWin.files.get(i);
				file.delete();
				Main.debug("Deleted: " + file.getAbsolutePath());
				
				String name = ScannerWin.regkeys.get(i);

				
				String regquery = "reg delete HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\ /v \"" + name + "\" /f";
				
				try {
					Runtime.getRuntime().exec(regquery);
					Main.debug("Executed " + regquery);
				} catch (Exception ex) {
					ex.printStackTrace();
					Main.debug("Failed executing " + regquery + ": " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Main.remover.scan();
	}

}
