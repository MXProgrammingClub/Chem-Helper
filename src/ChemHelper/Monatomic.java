/*
 * Represents a monatomic ion.
 * 
 * Author: Julia McClellan
 * Version: 1/6/2016
 */
package ChemHelper;

import Elements.Element;

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
	
	public Element getElement()
	{
		return element;
	}
	
	public Monatomic[] getElements()
	{
		Monatomic[] list = {this};
		return list;
	}

	public String toString() 
	{
		return element.getSymbol() + "<sub>" + getNum() + "</sub><sup>" + getCharge() + "</sup>";
	}

	public double getMolarMass()
	{
		return element.getMolarMass() * super.getNum();
	}

	public String getMolarMassSteps() 
	{
		return "(" + element.getMolarMass() + " * " + super.getNum() + ")";
	}
}