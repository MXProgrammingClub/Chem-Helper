package Equation;

import Elements.Element;

/**
 * File: Electron.java
 * Package: Equation
 * Version: 08/09/2016
 * Authors:Julia McClellan
 * -----------------------------------------------
 * Represents an electron.
 */
public class Electron extends Ions
{
	/**
	 * Constructs the electron.
	 */
	public Electron()
	{
		super(1, -1);
	}
	
	/**
	 * Returns true if the ion is an electron, false otherwise.
	 * @param The ion to check for equality.
	 * @return Whether other is an electron.
	 */
	public boolean equals(Ions other)
	{
		return other instanceof Electron;
	}
	
	/**
	 * Not applicable for electrons.
	 * @return null
	 */
	public Monatomic[] getElements()
	{
		return null;
	}
	
	/**
	 * Not applicable for electrons.
	 * @param numbers Doesn't affect the return value.
	 * @return null
	 */
	public Monatomic[] getElements(boolean numbers)
	{
		return null;
	}
	
	/**
	 * Returns a string representation of an electron.
	 * @return An electron as a string.
	 */
	public String toString()
	{
		return (getNum() == 1 ? "" : getNum()) + "e<sup>-</sup>";
	}
	
	/**
	 * Mass of an electron is negligible.
	 * @return 0
	 */
	public double getMolarMass()
	{
		return 0;
	}
	
	/**
	 * Not applicable for electrons as they are defined by their charge.
	 * @return toString()
	 */
	public String withoutCharge()
	{
		return toString();
	}
	
	/**
	 * Not applicable for electrons.
	 * @param e 
	 * @return 0
	 */
	public int numberOf(Element e)
	{
		return 0;
	}
	
	/**
	 * Not applicable for electrons.
	 * @param str A StringBuffer that will not be altered.
	 * @return null
	 */
	public double getMolarMassSteps(StringBuffer str)
	{
		return 0;
	}
}