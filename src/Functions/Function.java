package Functions;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.scilab.forge.jlatexmath.*;

import ChemHelper.Equation;
import ChemHelper.Ions;
import Elements.Compound;

public abstract class Function {
	public static final double C = 300000000, h = 6.626*Math.pow(10, -34);
	private String name;
	
	public Function(String name)
	{
		this.name = name;
	}
	
	public abstract JPanel getPanel();		//Return the frame containing all components for that chem function
	
	public String toString()
	{
		return name;
	}
	
	
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
				str += comp.getNum();
				
				for (Ions ion: comp.getIons()) {
					
					str += "\\text{" + ion.getElement().getSymbol() + "}";
					if (ion.getCharge() < 0) {
						str += "^{" + ion.getCharge() + "}";
					}
					else if (ion.getCharge() > 0) {
						str += "^{+" + ion.getCharge() + "}";
					}
					
					if (ion.getNum()>1) {
						str+= "_{" + ion.getNum() + "}";
					}
					
				}
				str += "+";
				
				
			}
			str = str.substring(0, str.length()-1);
			
			str+= "\\textbf{\\longrightarrow}";
		}
		str = str.substring(0,str.length()-26);//remove last rightarrow
		
		
		
		
		TeXFormula formula = new TeXFormula (str);
		TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
				
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(new JLabel(), image.getGraphics(), 0, 0);
		
		label.setIcon(icon);
		
		return label;
		
	}
	
	// Written by Lewis/Loftus/Cocking
	public static int gcd (int num1, int num2)
	   {
	      while (num1 != num2)
	         if (num1 > num2)
	            num1 = num1 - num2;
	         else
	            num2 = num2 - num1;

	      return num1;
	   }
	
	/*
	 * Returns the number of significant figures a number has, if numString is a String representation of a double.
	 */
	public static int sigFigs(String numString)
	{
		int sigFigs = numString.length();
		if(numString.indexOf("-") != -1) sigFigs--;
		if(numString.indexOf(".") != -1) sigFigs--;
			//These characters lead the String to be longer than purely 
		else for(int index = numString.length() -1; index >= 0 && numString.charAt(index) == '0'; index--) sigFigs--; 
			//If a number does not contain a decimal point, for every 0 at the end of the number, subtract a sig fig.
		return sigFigs;
	}

	/*
	 * Returns a String representation of the number with the given number of significant figures, unless that would require scientific notation
	 * because I didn't feel like doing that and might do it later. Also doesn't round properly because I just remembered I should do that but
	 * it's already past my bedtime and I'm tired so I'll do it tomorrow.
	 * pre: sigFigs >= 1
	 */
	public static String withSigFigs(double num, int sigFigs)
	{
		int count = 0;
		boolean neg = num < 0;
		num = Math.abs(num);
		String original = "" + num, numString = "";
		int index;
		if(num > 1)
		{
			for(; count < original.length() && original.charAt(count) != '.' && count < sigFigs; numString += original.charAt(count), count++);
			if(count == sigFigs) // Adding extra zeros to the end if necessary.
			{
				for(int i = count; i < original.length() && original.charAt(i) != '.'; i++, numString += '0');
				if(neg) numString = '-' + numString;
				return numString;
			}
			index = count + 1;
		}
		else 
		{
			numString = "0";
			index = 2;
		}
		numString += '.';
		for(; index < original.length() && count < sigFigs;  numString += original.charAt(index), count++, index++);
		for(; count < sigFigs; count++, numString += '0');
		return numString;
	}
}