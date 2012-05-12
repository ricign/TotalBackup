package mobi.durian.tbackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

public class FileManager {
	private static final String TBACKUP_DIR = "TBackup";
	
	public static final String CALLHISTORY_FILE = "CallHistories";
	public static final String SMS_FILE = "SMSes";
	public static final String CONTACT_FILE = "Contacts";
	public static final String BOOKMARK_FILE = "Bookmarks";
	
	static File sdCardDir = null; 
	
	public static boolean checkSdCard() throws IOException {
		if (sdCardDir != null) 
			return (new File(sdCardDir, CALLHISTORY_FILE).exists());
			
		ArrayList<File> posibleDir = new ArrayList<File>();
		File esd = Environment.getExternalStorageDirectory();
		posibleDir.add(new File(esd, "external_sd"));
		posibleDir.add(esd);
		posibleDir.add(new File("/sdcard"));

		// Use first possible sdcard directory
		for (File dir : posibleDir) {
			if (dir.exists() && dir.isDirectory()) {
				sdCardDir = new File(dir, TBACKUP_DIR);
				if(!sdCardDir.exists())
					sdCardDir.mkdir();
				return true;
			}
		}

		throw new IOException();
	}
	
	public static PrintStream writeFile(String name) {
		try {
			File file = new File(sdCardDir, name);
			if (file.exists())
				file.delete();
			file.createNewFile();
			return new PrintStream(new FileOutputStream(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BufferedReader readFile(String name) {
		try {			
			File log = new File(sdCardDir, name);
			if (log.exists()) {
				return new BufferedReader(new FileReader(log));
			}else {
				return null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
