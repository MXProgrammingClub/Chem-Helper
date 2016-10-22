/**
 * Preferences.java
 * 
 * This class manages the import/export of the user preferences
 * 
 * @author Luke Giacalone
 * @version 10/22/2016
 */

package HelperClasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Preferences extends HashMap<String, String> {
	
	private String file;
	PrintWriter writer;
	private static String preferencesFileLoc = System.getProperty("user.home");	

	public Preferences(String file) throws IOException {
		updateFilePath();
		this.file = preferencesFileLoc + file;
		File f = new File(this.file);
		if(!f.exists())
			f.createNewFile();
		Scanner scan = new Scanner(f);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] pref = line.split(" ");
			this.put(pref[0], pref[1]);
		}
		scan.close();
	}
	
	/**
	 * Exports the preferences into an external file.
	 */
	public void export() {
		try {
			writer = new PrintWriter(file, "UTF-8");
			Thread write = new Thread(new BackgroundWriter());
			write.run();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			JOptionPane.showMessageDialog(null, "Error Exporting Preferences.");
		}
	}
	
	/**
	 * Puts a preference as a String into the map.
	 * 
	 * @param key The key associated with the preference
	 * @param value The value associated with the preference
	 */
	public void putString(String key, String value) {
		if(key.contains(" ") || value.contains(" "))
			throw new InvalidParameterException();
		this.put(key, value);
		export();
	}
	
	/**
	 * Puts a preference as a boolean into the map.
	 * 
	 * @param key The key associated with the preference
	 * @param value The value associated with the preference
	 */
	public void putBoolean(String key, boolean value) {
		if(key.contains(" "))
			throw new InvalidParameterException();
		this.put(key, "" + value);
		export();
	}
	
	/**
	 * Puts a preference as an integer into the map.
	 * 
	 * @param key The key associated with the preference
	 * @param value The value associated with the preference
	 */
	public void putInteger(String key, int value) {
		if(key.contains(" "))
			throw new InvalidParameterException();
		this.put(key, "" + value);
		export();
	}
	
	/**
	 * Gets and returns a preference stored as a String
	 * 
	 * @param key The key corresponding to the preference
	 * @return The preference as a String
	 */
	public String getString(String key) {
		if(key.contains(" "))
			throw new InvalidParameterException();
		return this.get(key);
	}
	
	/**
	 * Gets and returns a preference stored as a boolean
	 * 
	 * @param key The key corresponding to the preference
	 * @return The preference as a boolean
	 */
	public boolean getBoolean(String key) {
		if(key.contains(" "))
			throw new InvalidParameterException();
		String value = this.get(key);
		try {
			return Boolean.parseBoolean(value);
		}
		catch(Throwable e) {
			return false;
		}
	}
	
	/**
	 * Gets and returns a preference stored as an integer
	 * 
	 * @param key The key corresponding to the preference
	 * @return The preference as an integer
	 */
	public int getInteger(String key) {
		if(key.contains(" "))
			throw new InvalidParameterException();
		String value = this.get(key);
		try {
			return Integer.parseInt(value);
		}
		catch(Throwable e) {
			return 0;
		}
	}
	
	/**
	 * This is the private class that exports the preferences to the external File
	 * 
	 * @author Ted Pyne, Luke Giacalone
	 * @version 10/22/2016
	 */
	private class BackgroundWriter implements Runnable {
		public void run() { 
			for(String key: keySet()) {
				writer.println(key + " " + get(key));
			}
			
			writer.close();
		}
	}
	
	/**
	 * Updates the file path for the location of the properties file depending
	 * on the operating system.
	 */
	private static void updateFilePath() {
		if (System.getProperty("os.name").contains("Mac")) {
			preferencesFileLoc += File.separator + "Library" + File.separator + "ChemHelper" + File.separator;
		}
		else {
			preferencesFileLoc += File.separator + "ChemHelper" + File.separator;
		}
		File file = new File(preferencesFileLoc);
		file.mkdirs();
	}
	
}
