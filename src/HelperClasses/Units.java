/*
 * Contains groups of units, useful constants, and methods to convert between units.
 * 
 * Author: Julia McClellan
 * Version: 2/5/2016
 */

package HelperClasses;

import java.util.TreeMap;

import javax.swing.JTextField;

public class Units
{
	public static final int ENTERFIELD_AMOUNT_COLUMNS = 6;
	public static final int ENTERFIELD_AMOUNT_WIDTH = ((System.getProperty("os.name").contains("Mac")) ? -6 : 0) +
			(int)(new JTextField(ENTERFIELD_AMOUNT_COLUMNS).getPreferredSize().getWidth());
	public static final int ENTERFIELD_AMOUNT_HEIGHT = 26 + ((System.getProperty("os.name").contains("Mac")) ? 6 : 0);
	public static final double R = .0821, STANDARD_PRESSURE = 1, STANDARD_TEMPERATURE = 273.15, C = 3E8, h = 6.626E-34;
	public static final int UNKNOWN_VALUE = -500, ERROR_VALUE = -501;
	public static final String[] PREFIXES = {"p", "n", "\u00B5", "m", "c", "d", "", "da", "h", "k", "M", "G", "T"};
	public static final int[] POWERS = {-12, -9, -6, -3, -2, -1, 0, 1, 2, 3, 6, 9, 12};
	private static final TreeMap<String, String[]> UNITS = generateMap();
	
	private static TreeMap<String, String[]> generateMap()
	{
		TreeMap<String, String[]> units = new TreeMap<String, String[]>();
		units.put("Pressure", new String[]{"atm", "torr", "kPa"});
		units.put("Temperature", new String[]{"K", "\u2103", "\u2109"});
		units.put("Amount", new String[]{"mol"});
		units.put("Volume", createGroup("L"));
		units.put("Length", createGroup("m"));
		units.put("Mass", createGroup("g"));
		units.put("Frequency", new String[]{"Hz"});
		units.put("Energy", new String[]{"J"});
		units.put("Velocity", new String[] {"m/s"});
		units.put("Planck", new String[]{"J\u00B7s"});
		units.put("Time", new String[]{"s", "hr", "day"});
		units.put("Mass*Temp", new String[]{"g\u00B7K", "g\u00B7\u2103"});
		units.put("Percent", new String[]{"%"});
		return units;
	}
	
	private static String[] createGroup(String base)
	{
		String[] group = new String[PREFIXES.length];
		for(int index = 0; index < PREFIXES.length; index++)
		{
			group[index] = PREFIXES[index] + base;
		}
		return group;
	}
	
	public static String[] getUnits(String type)
	{
		if(type.equals("Moles")) type = "Amount";
		return UNITS.get(type);
	}
	
	public static String[][] getUnits(String[] types)
	{
		String[][] units = new String[types.length][];
		for(int index = 0; index < types.length; index++)
		{
			units[index] = getUnits(types[index]);
		}
		return units;
	}
	
	public static double fahrenheitToKelvin(double fahrenheit)
	{
		return (fahrenheit + 459.67) * 5 / 9;
	}
	
	public static double kelvinToFahrenheit(double kelvin)
	{
		return (kelvin  * 9 / 5) - 459.67;
	}
	
	public static double celsiusToKelvin(double celsius)
	{
		return celsius + 273.15;
	}
	
	public static double kelvinToCelsius(double kelvin)
	{
		return kelvin - 273.15;
	}
	
	public static double torrToatm(double torr)
	{
		return torr * 0.00131579;
	}
	
	public static double atmTotorr(double atm)
	{
		return atm / 0.00131579;
	}
	
	public static double kPaToatm(double kPa)
	{
		return kPa * 0.00986923;
	}
	
	public static double atmTokPa(double atm)
	{
		return atm / 0.00986923;
	}
	
	public static double toBaseUnit(double amount, int unit)
	{
		if(amount == Units.UNKNOWN_VALUE) return amount;
		else if(amount == Units.ERROR_VALUE) return amount;
		else return amount * Math.pow(10, POWERS[unit]);
	}
	
	public static double fromBaseUnit(double amount, int unit)
	{
		if(amount == Units.UNKNOWN_VALUE) return amount;
		else if(amount == Units.ERROR_VALUE) return amount;
		else return amount / Math.pow(10, POWERS[unit]);
	}
	
	public static double betweenUnits(double amount, int from, int to)
	{
		return amount * Math.pow(10, POWERS[from - to]);
	}
	
	public static double toKelvin(double amount, int unit) {
		if(amount == UNKNOWN_VALUE) return amount;
		else if(amount == ERROR_VALUE) return amount;
		else if(unit == 0) return amount; //if kelvin
		else if(unit == 1) return Units.celsiusToKelvin(amount); //if celcius
		else return Units.fahrenheitToKelvin(amount); //if fahrenheit
	}
	
	public static double toOriginalTemp(double amount, int unit) {
		if(unit == 0) return amount; //if kelvin
		else if(unit == 1) return Units.kelvinToCelsius(amount);//if celcius
		else return Units.kelvinToFahrenheit(amount); //if fahrenheit
	}
	
	public static double toSeconds(double amount, int unit) {
		if(unit == 0) return amount; //if seconds
		else if(unit == 1) return amount * 3600; //if hours
		else return amount * 86400; //if days
	}
	
	public static double toOriginalTime(double amount, int unit) {
		if(unit == 0) return amount; //if seconds
		else if(unit == 1) return amount / 3600; //if hours
		else return amount / 86400; //if days
	}
	
	public static double toAtm(double amount, int unit)
	{
		if(amount == UNKNOWN_VALUE || amount == ERROR_VALUE || unit == 0) return amount;
		if(unit == 1) return torrToatm(amount);
		return  kPaToatm(amount);
	}
	
	public static double fromAtm(double amount, int unit)
	{
		if(amount == UNKNOWN_VALUE || amount == ERROR_VALUE || unit == 0) return amount;
		if(unit == 1) return atmTotorr(amount);
		return  atmTokPa(amount);
	}
	
	public static double toStandard(double amount, int unit, String type)
	{
		if(amount == UNKNOWN_VALUE || amount == ERROR_VALUE) return amount;
		if(type.equals("Temperature")) return toKelvin(amount, unit);
		else if(type.equals("Pressure")) return toAtm(amount, unit);
		else if(type.equals("Time")) return toSeconds(amount, unit);
		else if(UNITS.get(type).length == PREFIXES.length) return toBaseUnit(amount, unit);
		else return amount;
	}
	
	public static double fromStandard(double amount, int unit, String type)
	{
		if(amount == UNKNOWN_VALUE || amount == ERROR_VALUE) return amount;
		if(type.equals("Temperature")) return toOriginalTemp(amount, unit);
		else if(type.equals("Pressure")) return fromAtm(amount, unit);
		else if(type.equals("Time")) return toOriginalTime(amount, unit);
		else if(UNITS.get(type).length == PREFIXES.length) return fromBaseUnit(amount, unit);
		else return amount;
	}
}