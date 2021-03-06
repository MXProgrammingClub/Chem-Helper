/*
 * Represents a polyatomic ion and has a public array of common polyatomic ions.
 * 
 * Author: Julia McClellan
 * Version: 4/24/2016
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
	
	/*
	 * Copy constructor.
	 */
	public Polyatomic(Polyatomic copy)
	{
		super(copy.getNum(), copy.getCharge());
		Monatomic[] copyE = copy.getElements();
		elements = new Monatomic[copyE.length];
		for(int index = 0; index < elements.length; index++)
		{
			elements[index] = new Monatomic(copyE[index]);
		}
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
		String ion = getNum() == 1 ? "" : "(";
		for(Monatomic e: elements)
		{
			ion += e;
		}
		ion += getNum() == 1 ? "" : ")";
		if(getNum() != 1) ion += "<sub>" + getNum() + "</sub>";
		if(getCharge() != 0) ion += "<sup>" + (Math.abs(getCharge()) == 1 ? "" : Math.abs(getCharge())) + (getCharge() > 0 ? "+" : "-") + "</sup>";
		return ion;
	}
	
	public String withoutCharge()
	{
		String ion = getNum() == 1 ? "" : "(";
		for(Monatomic e: elements)
		{
			ion += e.withoutCharge();
		}
		if(getNum() != 1) ion += ")<sub>" + getNum() + "</sub>";
		return ion;
	}
	
	public double getMolarMass()
	{
		double mass = 0;
		for(Monatomic ion: elements) mass += ion.getMolarMass();
		mass *= getNum();
		return mass;
	}

	public double getMolarMassSteps(StringBuffer str) 
	{
		str.append("(" + getNum() + " * (");
		double total = 0;
		for(Monatomic ion: elements) 
		{
			total += ion.getMolarMassSteps(str);
			str.append(" + ");
		}
		str.delete(str.length() - 3, str.length());
		str.append("))");
		return total;
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
	
	/*
	 * Finds and copies the polyatomic ion of the form H(numH) m O(numO). Returns null if one cannot be found.
	 */
	public static Polyatomic findIon(Monatomic m, int numO, int numH)
	{
		Polyatomic p = null;
		int[][] elements = {{7, 6, 16, 25, 15, 17, 24}, {3, 5, 7, 10, 11, 14, 18, 20}}; //First array holds element numbers, second hold starting indices
			//of those elements in POLYATOMIC_IONS
		int eNum = m.getElement().getNum();
		for(int i = 0; i < elements[0].length; i++)
		{
			if(eNum == elements[0][i])
			{
				for(int j = elements[1][i]; j < elements[1][i + 1]; j++)
				{
					Monatomic[] ions = POLYATOMIC_IONS[j].getElements();
					if(m.getNum() == ions[ions.length - 2].getNum() && (ions.length != 3 || numH == ions[0].getNum()) && numO == 
							ions[ions.length - 1].getNum())
					{
						p = new Polyatomic(POLYATOMIC_IONS[j]);
						break;
					}
				}
				break;
			}
		}
		return p;
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
			new Polyatomic(new Monatomic[]{new Monatomic(new Hydrogen()), new Monatomic(new Sulfur()), new Monatomic(new Oxygen(), 4)}, -1, 
			"Hydrogen sulfate"), new Polyatomic(new Monatomic[]{new Monatomic(new Manganese()), new Monatomic(new Oxygen(), 4)}, -1, 
			"Permanganate"), new Polyatomic(new Monatomic[]{new Monatomic(new Phosphorus()), new Monatomic(new Oxygen(), 4)}, -3, 
			"Phosphate"), new Polyatomic(new Monatomic[]{new Monatomic(new Hydrogen()), new Monatomic(new Phosphorus()), 
			new Monatomic(new Oxygen(), 4)}, -2, "Hydrogen phosphate"), new Polyatomic(new Monatomic[]{new Monatomic(new Hydrogen(), 2), 
			new Monatomic(new Phosphorus()), new Monatomic(new Oxygen(), 4)}, -1, "Dihydrogen phosphate"), 
			new Polyatomic(new Monatomic[]{new Monatomic(new Chlorine()), new Monatomic(new Oxygen())}, -1, "Hypochlorite"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chlorine()), new Monatomic(new Oxygen(), 2)}, -1, "Chlorite"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chlorine()), new Monatomic(new Oxygen(), 3)}, -1, "Chlorate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chlorine()), new Monatomic(new Oxygen(), 4)}, -1, "Perchlorate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chromium()), new Monatomic(new Oxygen(), 4)}, -2, "Chromate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Chromium(), 2), new Monatomic(new Oxygen(), 7)}, -2, "Dihromate"),
			new Polyatomic(new Monatomic[]{new Monatomic(new Oxygen(), 2)}, -2, "Peroxide")};
}