/*
 * Represents a polyatomic ion and has a public array of common polyatomic ions.
 * 
 * Author: Julia McClellan
 * Version: 3/10/2016
 */

package Equation;

import java.util.ArrayList;

import Elements.Carbon;
import Elements.Chlorine;
import Elements.Chromium;
import Elements.Element;
import Elements.Hydrogen;
import Elements.Manganese;
import Elements.Nitrogen;
import Elements.Oxygen;
import Elements.Phosphorus;
import Elements.Sulfur;

public class Polyatomic extends Ions
{
	private Monatomic[] elements;
	String name;
	
	public Polyatomic(Monatomic[] elements)
	{
		super(1, 0);
		this.elements = elements;
		int charge = 0;
		for(Monatomic e: elements)
		{
			charge += e.getCharge() * e.getNum();
		}
		setCharge(charge);
	}
	
	public Polyatomic(ArrayList<Monatomic> elements)
	{
		super(1, 0);
		Monatomic[] array = new Monatomic[elements.size()];
		this.elements = elements.toArray(array);
		int charge = 0;
		for(Monatomic e: elements)
		{
			charge += e.getCharge() * e.getNum();
		}
		setCharge(charge);
	}
	
	public Polyatomic(ArrayList<Monatomic> elements, int num, int charge)
	{
		super(num, charge);
		Monatomic[] array = new Monatomic[elements.size()];
		this.elements = elements.toArray(array);
		name = "";
	}
	
	public Polyatomic(Monatomic[] elements, int num, int charge)
	{
		super(num, charge);
		this.elements = elements;
		name = "";
	}
	
	private Polyatomic(Monatomic[] elements, int charge, String name)
	{
		super(1, charge);
		this.elements = elements;
		this.name = name;
	}

	public Monatomic[] getElements()
	{
		Monatomic[] e = new Monatomic[elements.length];
		for(int index = 0; index < e.length; index++)
		{
			e[index] = new Monatomic(elements[index]);
			e[index].setNum(e[index].getNum() * getNum());
		}
		return e;
	}
	
	public Monatomic[] getElements(boolean numbers)
	{
		if(numbers) return getElements();
		return elements;
	}

	public boolean contains(Element e)
	{
		for(Monatomic element: elements)
		{
			if(e.equals(element)) return true;
		}
		return false;
	}
	
	public String toString() 
	{
		String ion = "(";
		for(Monatomic e: elements)
		{
			ion += e;
		}
		ion += ")";
		if(getNum() != 1) ion += "<sub>" + getNum() + "</sub>";
		if(getCharge() != 0) ion += "<sup>" + getCharge() + "</sup>";
		return ion;
	}
	
	public String withoutCharge()
	{
		String ion = "";
		for(Monatomic e: elements)
		{
			ion += e.getElement().getSymbol() + "<sub>" + e.getNum() + "</sub>";
		}
		ion += "<sub>" + getNum() + "</sub>";
		return ion;
	}
	
	public double getMolarMass()
	{
		double mass = 0;
		for(Monatomic ion: elements) mass += ion.getMolarMass();
		mass *= getNum();
		return mass;
	}

	public String getMolarMassSteps() 
	{
		String mass = "(" + getNum() + " * (";
		for(Monatomic ion: elements) mass += ion.getMolarMassSteps() + " + ";
		if(mass.length() != 0) mass = mass.substring(0, mass.length() - 3);
		mass += "))";
		return mass;
	}
	
	public boolean equals(Ions other)
	{
		if(other instanceof Monatomic) return false;
		for(Monatomic m1: other.getElements())
		{
			boolean found = false;
			for(Monatomic m2: elements)
			{
				if(m2.equals(m1))
				{
					found = true;
					break;
				}
			}
			if(!found) return false;
		}
		return true;
	}
	
	public int numberOf(Element e) 
	{
		int count = 0;
		for(Monatomic ion: elements) count += ion.numberOf(e);
		return count;
	}
	
	public static int findCharge(Polyatomic ion)
	{
		for(Ions test: POLYATOMIC_IONS)
		{
			if(test.equals(ion)) return test.getCharge();
		}
		return 0;
	}
	
	//Commonly used polyatomic ions:
	public static final Polyatomic[] POLYATOMIC_IONS = {new Polyatomic(new Monatomic[]{new Monatomic(new Nitrogen()), 
			new Monatomic(new Hydrogen(), 4)}, 1, "Ammonium"), new Polyatomic(new Monatomic[]{new Monatomic(new Carbon()), 
			new Monatomic(new Hydrogen(), 3), new Monatomic(new Carbon()), new Monatomic(new Oxygen(), 2)}, -1, "Acetate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Carbon()), new Monatomic(new Nitrogen())}, -1, "Cyanide"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Nitrogen()), new Monatomic(new Oxygen(), 2)}, -1, "Nitrite"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Nitrogen()), new Monatomic(new Oxygen(), 3)}, -1, "Nitrate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Carbon()), new Monatomic(new Oxygen(), 3)}, -2, "Carbonate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Hydrogen()), new Monatomic(new Carbon()), new Monatomic(new Oxygen(), 3)}, -1,
			"Hydrogen carbonate"), new Polyatomic(new Monatomic[]{new Monatomic(new Sulfur()), new Monatomic(new Oxygen(), 3)}, -2, 
			"Sulfite"), new Polyatomic(new Monatomic[]{new Monatomic(new Sulfur()), new Monatomic(new Oxygen(), 4)}, -2, "Sulfate"), 
			new Polyatomic(new Monatomic[]{new Monatomic(new Hydrogen()), new Monatomic(new Sulfur()), new Monatomic(new Oxygen(), 4)}, -2, 
			"Hydrogen sulfate"), new Polyatomic(new Monatomic[]{new Monatomic(new Manganese()), new Monatomic(new Oxygen(), 4)}, -1, 
			"Permanganate"), new Polyatomic(new Monatomic[]{new Monatomic(new Phosphorus()), new Monatomic(new Oxygen(), 4)}, -3, 
			"Phosphate"), new Polyatomic(new Monatomic[]{new Monatomic(new Hydrogen()), new Monatomic(new Phosphorus()), 
			new Monatomic(new Oxygen(), 4)}, -2, "Hydrogen phosphate"), new Polyatomic(new Monatomic[]{new Monatomic(new Hydrogen(), 2), 
			new Monatomic(new Phosphorus()), new Monatomic(new Oxygen(), 4)}, -2, "Dihydrogen phosphate"), 
			new Polyatomic(new Monatomic[]{new Monatomic(new Chlorine()), new Monatomic(new Oxygen())}, -1, "Hypochlorite"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chlorine()), new Monatomic(new Oxygen(), 2)}, -1, "Chlorite"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chlorine()), new Monatomic(new Oxygen(), 3)}, -1, "Chlorate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chlorine()), new Monatomic(new Oxygen(), 4)}, -1, "Perchlorate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chromium()), new Monatomic(new Oxygen(), 4)}, -2, "Chromate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chromium(), 2), new Monatomic(new Oxygen(), 7)}, -2, "Dihromate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Oxygen(), 2)}, -2, "Peroxide")};
}