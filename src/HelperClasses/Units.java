/*
 * Contains groups of units, useful constants, and methods to convert between units.
 * 
 * Author: Julia McClellan
 * Version: 2/5/2016
 */

package HelperClasses;

import java.util.TreeMap;

public class Units
{
	public static final double R = .0821, STANDARD_PRESSURE = 1, STANDARD_TEMPERATURE = 273.15, C = 300000000, h = 6.626*Math.pow(10, -34);
	public static final String[] PREFIXES = {"p", "n", "\u00B5", "m", "c", "d", "", "da", "h", "k", "M", "T", "G"};
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
		return amount * Math.pow(10, POWERS[unit]);
	}
	
	public static double fromBaseUnit(double amount, int unit)
	{
		return amount / Math.pow(10, POWERS[unit]);
	}
	
	public static double betweenUnits(double amount, int from, int to)
	{
		return amount * Math.pow(10, POWERS[from - to]);
	}
}