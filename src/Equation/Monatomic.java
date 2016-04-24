/*
 * Represents a monatomic ion.
 * 
 * Author: Julia McClellan
 * Version: 4/24/2016
 */
package Equation;

import Elements.Element;
import Functions.Function;

public class Monatomic extends Ions
{
	private Element element;
	public Monatomic(Element element)
	{
		super(1, 0);
		this.element = element;
	}
	
	public Monatomic(Element element, int num)
	{
		super(num, 0);
		this.element = element;
	}
	
	public Monatomic(Element element, int num, int charge)
	{
		super(num, charge);
		this.element = element;
	}
	
	/*
	 * A copy constructor.
	 */
	public Monatomic(Monatomic copy)
	{
		super(copy.getNum(), copy.getCharge());
		element = copy.getElement();
	}
	
	public Element getElement()
	{
		return element;
	}
	
	public Monatomic[] getElements()
	{
		Monatomic[] list = {this};
		return list;
	}
	
	public Monatomic[] getElements(boolean numbers)
	{
		return getElements();
	}

	public String toString() 
	{
		String ion = element.getSymbol();
		if(getNum() != 1) ion += "<sub>" + getNum() + "</sub>"; 
		if(getCharge() != 0) ion += "<sup>" + (Math.abs(getCharge()) == 1 ? "" : Math.abs(getCharge())) + (getCharge() > 0 ? "+" : "-") + "</sup>";
		return ion;
	}
	
	public String withoutCharge()
	{
		String sym = element.getSymbol();
		if(getNum() != 1) sym += "<sub>" + getNum() + "</sub>";
		return sym;
	}

	public double getMolarMass()
	{
		return element.getMolarMass() * super.getNum();
	}

	public double getMolarMassSteps(StringBuffer str) 
	{
		str.append("(" + element.getMolarMass() + "\\frac{g\\text{ }" + element.getSymbol() + "}{mol\\text{ }" + element.getSymbol() + "} * " + super.getNum()
		+ ")");
		return getMolarMass();
	}
	
	/*
	 * Returns true if the element of each ion is the same.
	 */
	public boolean equals(Monatomic other)
	{
		return other.getElement().equals(element);
	}
	
	public boolean equals(Ions other)
	{
		return other instanceof Polyatomic ? false : equals((Monatomic)other);
	}

	public int numberOf(Element e)
	{
		return e.equals(element) ? getNum() : 0;
	}
}