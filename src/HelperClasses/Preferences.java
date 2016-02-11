/*
 * File: Preferences.java
 * 
 * Making our own preferences class that will write to a file
 * 
 * Author: Luke Giacalone
 * Version: 02/09/2016
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

public class Preferences extends HashMap<String, String> {
	
	private String file;

	public Preferences(String file) throws IOException {
		this.file = file;
		File f = new File(file);
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
	
	public void export() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		
		for(String key: this.keySet())
			writer.println(key + " " + this.get(key));
		
		writer.close();
	}
	
	public void putString(String key, String value) {
		if(key.contains(" ") || value.contains(" "))
			throw new InvalidParameterException();
		this.put(key, value);
	}
	
	public void putBoolean(String key, boolean value) {
		if(key.contains(" "))
			throw new InvalidParameterException();
		this.put(key, "" + value);
	}
	
	public void putInteger(String key, int value) {
		if(key.contains(" "))
			throw new InvalidParameterException();
		this.put(key, "" + value);
	}
	
	public String getString(String key) {
		if(key.contains(" "))
			throw new InvalidParameterException();
		return this.get(key);
	}
	
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
	
}