package Elements;
/*
 * Represents an element of the periodic table. 
 * Current functions: finding the electron shell configuration, phase at room temperature, whether it is metal, non-metal, or metalloid
 * 
 * Author: Julia McClellan
 * Version: 11/10/2015
 */
public class Element
{
	public final static int S = 2, P = 6, D = 10, F = 14; //The number of electrons in each part of the shell configuration
	public final static double RM_TEMP = 293.15; // Room temperature in Kelvin
	
	private int num, group, period; //Atomic number, group number, and period number. Lanthanides are group -1, actinides are -2
	private String name, sym; //Element name and symbol, such as "Hydrogen" and "H"
    private double mass, boil, freeze, dense; //The average atomic mass, freezing point, boiling point, and density. Freezing and boiling points are in Kelvin
    
    public Element(int num, int group, int period, String name, String sym, double mass, double boil, double freeze, double dense)
    {
        this.num = num;
        this.group = group;
        this.period = period;
        this.name = name;
        this.sym = sym;
        this.mass = mass;
        this.boil = boil;
        this.freeze = freeze;
        this.dense = dense;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getNum()
    {
		return num;
	}

	public int getGroup()
	{
		return group;
	}

	public int getPeriod()
	{
		return period;
	}

	public double getBoil() 
	{
		return boil;
	}

	public double getFreeze()
	{
		return freeze;
	}

	public double getDense()
	{
		return dense;
	}

	public String getSymbol()
    {
        return sym;
    }
    
    public double getMolarMass()
    {
        return mass;
    }
    
    /*
     * Returns the electron shell configuration for the given element.
     */
    public String getEShell()
    {
    	String shell = "";
    	for(int row = 1, elec = num; row <= period; row++)
    	{
    		//Adds the s for the row
    		int s;
    		if(elec < S)
    		{
    			s = elec;
    			elec = 0;
    		}
    		else
    		{
    			s = S;
    			elec -= S;
    		}
    		shell += row + "s^" + s + " ";
    		
    		//Adds the f for the row two before, if applicable
    		if(row >= 6 && elec > 0) //The first row with an insertion of a previous f row in it is 6
    		{
    			int f;
        		if(elec < F)
        		{
        			f = elec;
        			elec = 0;
        		}
        		else
        		{
        			f = F;
        			elec -= F;
        		}
        		shell += (row - 2) + "f^" + f + " ";
    		}
    		
    		//Adds the d for the row before, if applicable
    		if(row >= 4 && elec > 0) //The first row with an insertion of a previous d row in it is 4
    		{
    			int d;
        		if(elec < D)
        		{
        			d = elec;
        			elec = 0;
        			
        			//Deals with exception where d should be filled before the next s.
        			if(d == D - 1)
        			{
        				d = D;
        				shell = shell.substring(0, shell.length() - 2) + (S - 1) + " ";
        			}
        		}
        		else
        		{
        			d = D;
        			elec -= D;
        		}
        		shell += (row - 1) + "d^" + d + " ";
    		}
    		
    		//Adds the p for the row, if applicable
    		if(row >= 2 && elec > 0) //The first row with a p section is 2
    		{
    			int p;
        		if(elec < P)
        		{
        			p = elec;
        			elec = 0;
        		}
        		else
        		{
        			p = P;
        			elec -= P;
        		}
        		shell += row + "p^" + p + " ";
    		}
    	}
    	String toReturn = "<html>";
    	while(shell.length() != 0)
    	{
    		toReturn += shell.substring(0, 2);
    		int start = shell.indexOf("^");
    		int end = shell.indexOf(" ");
    		toReturn += "<sup>" + shell.substring(start + 1, end) + "</sup>";
    		shell = shell.substring(end + 1);
    	}
    	return toReturn;
    }
    
    /*
     * Determines what phase the element is in at room temperature.
     */
    public String getState()
    {
    	if(boil < RM_TEMP || name.equals("Ununoctium")) return "Gas"; //If the boiling point is below room temperature, the element is a gas
    	else if(freeze > RM_TEMP || boil == Double.MAX_VALUE || freeze == 0) return "Solid"; //If the freezing point is above room temperature, the element is a solid
    	else return "Liquid"; //Otherwise, the element is a liquid.
    }
    
    /*
     * Determines if the elements is a metal, non-metal, or metalloid
     */
    public String getMetal()
    {
    	if(num == 1 || group >= 17 || period <= group - 12) //Hydrogen and the last two columns are non-metals, along with the diagonal of Selenium to Carbon
    	{
    		return "Non-metal";
    	}
    	else if(group <= 12 || period >= group - 9 || num == 13)//The first 12 columns, the diagonal from Gallium to Livermorium, and Aluminum are metals
    	{
    		return "Metal";
    	}
    	else
    	{
    		return "Metalloid";
    	}
    }
    
    /*
     * Determines the group name of the element.
     */
    public String getGroupName()
    {
    	if(group == 1 && ! name.equals("Hydrogen")) return "Alkali Metal";
    	else if(group == 2) return "Alkaline Earth Metal";
    	else if(group == 15) return "Pnictogen";
    	else if(group == 16) return "Chalcogen";
    	else if(group == 17) return "Halogen";
    	else if(group == 18) return "Noble Gas";
    	else return "None";
    }
}