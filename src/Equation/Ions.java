/*
 * Represents a collection of ions of the same type and charge. The two implementations are Polyatomic and Monatomic.
 * 
 * Author: Julia McClellan
 * Version: 1/9/2015
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
		if(ions.indexOf('/') == -1)
		{
			int symEnd = ions.indexOf("^");
			boolean isCharge = true, isNum = true;
			if(symEnd == -1)
			{
				symEnd = ions.indexOf(".");
				isCharge = false;
			}
			if(symEnd == -1)
				symEnd = ions.length();
		
			String symbol = ions.substring(0, symEnd);
			Element e = PeriodicTable.find(symbol);
		
			if(e == null) throw new InvalidInputException(0);
			int chargeEnd = ions.indexOf("."), charge;
			if(chargeEnd == -1) 
			{ 
				chargeEnd = ions.length();
				isNum = false;
			}
			if(!isCharge) charge = 0;
			else
			{
				try
				{
					charge = Integer.parseInt(ions.substring(symEnd + 1, chargeEnd));
				}
				catch(NumberFormatException e1)
				{
					throw new InvalidInputException(1);
				}
			}
			
			int num;
			if(!isNum) num = 1;
			else
			{
				try
				{
					num = Integer.parseInt(ions.substring(chargeEnd + 1));
				}
				catch(NumberFormatException e1)
				{
					throw new InvalidInputException(1);
				}
			}
			return new Monatomic(e, num, charge);
		}
		ions = ions.substring(1);
		ArrayList<Monatomic> inside = new ArrayList<Monatomic>();
		while(ions.indexOf('/') != -1)
		{
			inside.add((Monatomic)Ions.parseIons(ions.substring(0, ions.indexOf('/'))));
			ions = ions.substring(ions.indexOf('/') + 1);
		}
		inside.add((Monatomic)Ions.parseIons(ions.substring(0, ions.indexOf(')'))));
		ions = ions.substring(ions.indexOf(')')+ 1);
		int num = 1, numStart = ions.indexOf('.');
		if(numStart != -1)
		{
			num = Integer.parseInt(ions.substring(numStart + 1));
			if(numStart == 0) return new Polyatomic(inside, num, 0);
			ions = ions.substring(0, numStart);
		}
		int charge = 0, chargeStart = ions.indexOf('^');
		if(chargeStart != -1)
		{
			charge = Integer.parseInt(ions.substring(chargeStart + 1));
		}
		return new Polyatomic(inside, num, charge);
	}
}