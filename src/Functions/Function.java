/*
 * An abstract class representing a function used in ChemHelper. Contains static methods for latex rendering equations, calculating significant figures, 
 * and rounds numbers to a given number of significant figures. Provides default operations for equation and number saving, to be implemented by child
 * classes that need them.
 * 
 * Authors: Ted Pyne, Hyun Choi, Julia McClellan
 * Version: 2/27/2016
 */

package Functions;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.scilab.forge.jlatexmath.*;

import Equation.Equation;
import Equation.Compound;
import Equation.Ions;
import Equation.Monatomic;
import Equation.Polyatomic;


public abstract class Function {
	private String name;
	private static int sigFig;
	public static final double TOLERANCE = .05;
	
	public Function(String name)
	{
		this.name = name;
	}
	
	public abstract JPanel getPanel();		//Return the frame containing all components for that chem function
	
	public String toString()
	{
		return name;
	}
	
	//If equation() returns true, then the function can save equations and use saved ones. saveEquation and useSaved should be implemented by those functions.
	public boolean equation(){return false;}
	public Equation saveEquation(){return null;}
	public void useSaved(Equation equation){}
	
	public void resetFocus(){} //For functions which contain an equation reader
	
	//If number() returns true, then there should be JLabels and JTextFields in the function that have been made savable or usable.
	public boolean number(){return false;}
	public double saveNumber(){return 0;}
	public void useSavedNumber(double num){}
	
	//If help() returns true, than getHelp() should return information to be put in a popup message.
	public boolean help(){return false;}
	public String getHelp(){return null;}
	
	public static JPanel wrapInFlow(Component comp){			//Wrap a component in a FlowLayout
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(comp);
		return panel;
	}
	
	public static JLabel latex(Equation eq) { //only for equations at this point
		JLabel label = new JLabel();
		
		String str = "";
		
		ArrayList<Compound> left = eq.getLeft();
		ArrayList<Compound> right = eq.getRight();
		
		ArrayList<ArrayList<Compound>> sides = new ArrayList<ArrayList<Compound>>();
		sides.add(left);
		sides.add(right);
		
		
		for (ArrayList<Compound> side: sides) {
			for (Compound comp: side) {
				if(comp.getNum() != 1) str += comp.getNum();
				
				for (Ions ion: comp.getIons()) {
					if(ion instanceof Polyatomic) str += "(";
					
					for(Monatomic sub: ion.getElements(false))
					{
						str += "\\text{" + sub.getElement().getSymbol() + "}";
					
						if (sub.getNum()>1) {
							str+= "_{" + sub.getNum() + "}";
						}
					}
					if(ion instanceof Polyatomic)
					{
						str += ")";
						if (ion.getNum()>1) str+= "_{" + ion.getNum() + "}";
						
					}
				}
				if(!comp.getState().equals(" ")) str += "\\text{(" + comp.getState() + ")}";
				str += "+";
				
				
			}
			str = str.substring(0, str.length()-1);
			
			str+= "\\textbf{\\longrightarrow}";
		}
		str = str.substring(0,str.length()-25);//remove last rightarrow
		
		
		
		
		TeXFormula formula = new TeXFormula (str);
		TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
				
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(new JLabel(), image.getGraphics(), 0, 0);
		
		label.setIcon(icon);
		
		return label;
		
	}
	
	// Find gcd of two ints
	public static int gcd (int num1, int num2)
	   {
	      while (num1 != num2)
	         if (num1 > num2)
	            num1 = num1 - num2;
	         else
	            num2 = num2 - num1;

	      return num1;
	   }
	
	// Find lcm of two ints
	public static int lcm(int num1, int num2){
		return num1 * (num2 / Function.gcd(num1, num2));
	}
	
	public static void setSigFigPref(int newPref)
	{
		sigFig = newPref;
	}
	
	/*
	 * Returns the number of significant figures a number has, if numString is a String representation of a double.
	 */
	public static int sigFigs(String numString)
	{
		if(sigFig == 0) return 0; //There is no point in calculating if they won't be used.
		int sigFigs = numString.length();
		if(numString.indexOf("-") != -1) sigFigs--;	//These characters lead the String to be longer than the number of sig figs
		if(numString.indexOf(".") != -1)
		{
			sigFigs--;
			if(Math.abs(Double.parseDouble(numString)) < 1)
			{
				for(int index = numString.indexOf('.') + 1; index < numString.length() && numString.charAt(index) == '0'; sigFigs--, index++);
			}
		}
		else for(int index = numString.length() -1; index >= 0 && numString.charAt(index) == '0'; index--) sigFigs--; 
			//If a number does not contain a decimal point, for every 0 at the end of the number, subtract a sig fig.
		return sigFigs;
	}

	/*
	 * Returns a String representation of the number with the given number of significant figures.
	 * pre: sigFigs >= 1
	 */
	public static String withSigFigs(double num, int sigFigs)
	{
		if(sigFig == 0) return num + "";
		if(sigFig == 1)
		{
			int count = 0;
			boolean neg = num < 0, no = false; //neg for whether to add a negative sign later, no for if it needs to be in scientific notation
			num = Math.abs(num);
			String original = "" + num, numString = "";
			int index;
			if(num > 1)
			{
				for(; count < original.length() && original.charAt(count) != '.' && count < sigFigs; numString += original.charAt(count), count++);
				if(count == sigFigs) // Adding extra zeros to the end if necessary.
				{
					
					if((count + 1 < original.length() && original.charAt(count) == '.' && original.charAt(count + 1) >= '5') || 
							(count < original.length() && original.charAt(count) >= '5'))
					{
						numString = roundUp(numString);
					}
					if(numString.charAt(sigFigs - 1) == '0') no = true; //If the last character is 0, it has to be in scientific notation
					else
					{
						for(int i = count; i < original.length() && original.charAt(i) != '.'; i++, numString += '0');
						if(neg) numString = '-' + numString;
						return numString;
					}
				}
				index = count + 1;
			}
			else 
			{
				numString = "0";
				index = 2;
			}
			if(!no)
			{
				numString += '.';
				for(; index < original.length() && count < sigFigs;  numString += original.charAt(index), count++, index++);
				if(count == sigFigs && index < original.length() && original.charAt(index) >= '5') 
				{
					numString = roundUp(numString);
				}
				for(; count < sigFigs; count++, numString += '0');
				if(numString.length() == sigFigs + 1)
				{
					if(neg) numString = '-' + numString;
					return numString;
				}
			}
		}
		String format = "0";
		if(sigFigs>1) format += ".";
		for(int i = 1; i < sigFigs; i++) format+="0";
		
		if(!(("" + num).length()== format.length())) format+="E0";
		
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(num);
	}
	
	private static String roundUp(String toRound)
	{
		
		String resultant = toRound.substring(0, toRound.length() - 1) + (char)(toRound.charAt(toRound.length() - 1) + 1);
		for(int index = resultant.length() - 1; index > 0 && resultant.charAt(index) == ':'; index--)
		{
			if(resultant.charAt(index - 1) == '.')
			{
				resultant = resultant.substring(0, index - 2) + ((char)(resultant.charAt(index - 2) + 1)) + ".0";
				index--;
			}
			else resultant = resultant.substring(0, index - 1) + (char)(resultant.charAt(index - 1) + 1) + '0';
		}
		if(resultant.charAt(0) == ':') 
		{
			resultant = "100" + resultant.substring(1);
			if(resultant.charAt(resultant.length() - 1) > '5') resultant = roundUp(resultant.substring(0, resultant.length() - 1));
			else resultant = resultant.substring(0, resultant.length() - 1);
		}
		return resultant;
	}
	
	/*
	 * Returns the smallest possible factor to mulitply each number by in order to have an array of integers.
	 */
	public static int integerize(double[] values)
	{
		int factor = 1;
		for(int index = 0; index < values.length; index++)
		{
			for(int num = 1; num <= Integer.MAX_VALUE; num++) //Though I hope it doesn't get there.
			{
				double value = num * values[index];
				if(closeToInt(value))
				{
					if(factor % num != 0) factor *= num;
					break;
				}
			}
		}
		return factor;
	}
	
	private static boolean closeToInt(double num)
	{
		int down = (int)num, up = down + 1;
		return num - down < TOLERANCE || up - num < TOLERANCE;
	}
}