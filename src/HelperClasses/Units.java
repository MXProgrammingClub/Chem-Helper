package HelperClasses;

import java.util.Map;

public class Units
{
	public static final String[] PREFIXES = {"p", "n", "\u00B5", "m", "c", "d", "", "da", "h", "k", "M", "T", "G"};
	public static final int[] POWERS = {-12, -9, -6, -3, -2, -1, 0, 1, 2, 3, 6, 9, 12};
	public static final Map<String, String[]> TYPES = generateMap();
	
	private static Map<String, String[]> generateMap()
	{
		return null;
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

	public static double volumeToLiters(double volume, int unitIndex) {
		return 0;
		//return volume * Math.pow(10, VOLUME_POWERS[unitIndex]);
	}
}