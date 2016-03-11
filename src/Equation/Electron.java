/*
 * Represents an electron.
 * 
 * Author: Julia McClellan
 * Version: 3/10/16
 */

package Equation;

import Elements.Element;

public class Electron extends Ions
{
	public Electron()
	{
		super(1, -1);
	}
	
	public boolean equals(Ions other)
	{
		return other instanceof Electron;
	}
	
	public Monatomic[] getElements()
	{
		return null;
	}
	
	public Monatomic[] getElements(boolean numbers)
	{
		return null;
	}
	
	public String toString()
	{
		return (getNum() == 1 ? "" : getNum()) + "e<sup>-</sup>";
	}
	
	public double getMolarMass()
	{
		return 0;
	}
	
	public String getMolarMassSteps()
	{
		return null;
	}
	
	public String withoutCharge()
	{
		return toString();
	}

	public int numberOf(Element e)
	{
		return 0;
	}
}