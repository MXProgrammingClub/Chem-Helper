/*
 * Represents a collection of ions of the same type and charge. The two implementations are Polyatomic and Monatomic.
 * 
 * Author: Julia McClellan
 * Version: 1/16/2016
 */

package Equation;

import java.util.ArrayList;

import ChemHelper.InvalidInputException;
import Elements.Element;
import Functions.PeriodicTable;

public abstract class Ions 
{
	private int num, charge;
	
	public Ions(int num, int charge)
	{
		this.num = num;
		this.charge = charge;
	}

	public int getNum() 
	{
		return num;
	}

	public void setNum(int num) 
	{
		this.num = num;
	}

	public int getCharge() 
	{
		return charge;
	}

	public void setCharge(int charge) 
	{
		this.charge = charge;
	}
	
	public abstract Monatomic[] getElements();
	
	public abstract String toString();
	
	public abstract double getMolarMass();
	
	public abstract String getMolarMassSteps();
	
	public abstract String withoutCharge();
	
	public static Ions parseIons(String ions) throws InvalidInputException
	{
		Ions ion;
		if(ions.charAt(0) == '(')
		{
	        ArrayList<Monatomic> monatomic = new ArrayList<Monatomic>();
	        String temp = "";

	        for(int index = 1; ions.charAt(index) != ')'; index++) 
	        {
	            if(Character.isUpperCase(ions.charAt(index))) 
	            {
	                if(temp.length() > 0) monatomic.add((Monatomic)Ions.parseIons(temp));
	                temp = "" + ions.charAt(index);
	            }
	            else temp += ions.charAt(index);
	        }
	        monatomic.add((Monatomic)Ions.parseIons(temp));
	        ion = new Polyatomic(monatomic);
	        ions = ions.substring(ions.indexOf(')') + 1);
		}
		else
		{
			int end = ions.indexOf('<');
			if(end == -1) end = ions.length();
			Element e = PeriodicTable.find(ions.substring(0, end));
			if(e == null) throw new InvalidInputException(0);
			else ion = new Monatomic(e);
		}
		
		int num = 1, charge = 0;
		if(ions.indexOf("<sub>") != -1)
		{
			try
			{
				int end = ions.indexOf("</sub>");
				if(end == -1) end = ions.length();
				num = Integer.parseInt(ions.substring(ions.indexOf("<sub>") + 5, end));
			}
			catch(NumberFormatException e)
			{
				throw new InvalidInputException(1);
			}
		}
		ion.setNum(num);
		
		if(ions.indexOf("<sup>") != -1)
		{
			try
			{
				charge = Integer.parseInt(ions.substring(ions.indexOf("<sup>") + 5, ions.indexOf("</sup>")));
			}
			catch(NumberFormatException e)
			{
				throw new InvalidInputException(1);
			}
		}
		ion.setCharge(charge);
		
		return ion;
	}
}