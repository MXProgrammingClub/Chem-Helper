/*
 * Represents a polyatomic ion and has a public array of common polyatomic ions.
 * 
 * Author: Julia McClellan
 * Version: 1/16/2016
 */

package Equation;

import java.util.ArrayList;
import java.util.Set;

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
	private Set<Monatomic> elements;
	String name;
	
	public Polyatomic(Monatomic[] elements)
	{
		super(1, 0);
		for(Monatomic ion: elements) this.elements.add(ion);
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
		this.elements.addAll(elements);
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
		this.elements.addAll(elements);
		name = "";
	}

	private void setArray(Monatomic[] ions){
		for(Monatomic ion: ions) this.elements.add(ion);
	}
	
	public Polyatomic(Monatomic[] elements, int num, int charge)
	{
		super(num, charge);
		setArray(elements);
		name = "";
	}
	
	private Polyatomic(Monatomic[] elements, int charge, String name)
	{
		super(1, charge);
		setArray(elements);
		this.name = name;
	}
	
	public Set<Monatomic> getElements()
	{
		return elements;
	}

	public boolean contains(Element e)
	{
		return elements.contains(e);
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
	
	//Commonly used polyatomic ions:
	public static final Polyatomic[] POLYATOMIC_IONS = {new Polyatomic(new Monatomic[]{new Monatomic(new Nitrogen()), 
			new Monatomic(new Hydrogen(), 4)}, 1, "Ammonium"), new Polyatomic(new Monatomic[]{new Monatomic(new Carbon()), 
			new Monatomic(new Hydrogen(), 3), new Monatomic(new Carbon()), new Monatomic(new Oxygen(), 2)}, -1, "Acetate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Carbon()), new Monatomic(new Nitrogen())}, -1, "Cyanide"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Nitrogen()), new Monatomic(new Oxygen(), 2)}, -1, "Nitrite"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Nitrogen()), new Monatomic(new Oxygen(), 3)}, -1, "Nitrate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Carbon()), new Monatomic(new Oxygen(), 3)}, -1, "Carbonate"),
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