/*
 * Given two of the following, computes the third: a compound, moles of a compound, or mass of a compound. If The compound is left blank, its molar mass
 * is calculated. Also performs unit conversions, which it shouldn't do but the unit conversion class has not yet been created. Shows calculation steps.
 * number() returns true- saves last calculated value and can use saved in moles or mass.
 * 
 * Authors: Julia McClellan and Luke Giacalone
 * Version: 1/17/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Equation.Compound;
import ChemHelper.InvalidInputException;
import HelperClasses.EnterField;

public class CompoundStoichiometry extends Function 
{
	private JPanel panel;
	private EnterField compound, mass, moles;
	private JButton calculate;
	private JLabel result;
	private Box steps;
	private double toSave;
	
	private static final String[] MASS_UNITS = {"pg", "ng", "\u00B5g", "mg", "cg", "dg", "g", "dag", "hg", "kg", "Mg", "Tg", "Gg"};
	private static final int[] POWERS = {-12, -9, -6, -3, -2, -1, 0, 1, 2, 3, 6, 9, 12};
	private static final String[] MOLE_UNITS = {"mol"};
	
	public CompoundStoichiometry()
	{
		super("Compound Stoichiometry");
		
		compound = new EnterField("Compound");
		mass = new EnterField("Mass", MASS_UNITS);
		mass.setUnit(6); //sets default mass unit to grams
		moles = new EnterField("Moles", MOLE_UNITS);
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		result = new JLabel();
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		
		subpanel.add(new JLabel("<html>Enter known quantities to calculate the remaining."
				+ "<br>If the compound is left blank, its molar mass will be calculated.</html>"), c);
		
		c.gridy = 1;
		subpanel.add(compound, c);
		
		c.gridy = 2;
		subpanel.add(mass, c);
		
		c.gridy = 3;
		subpanel.add(moles, c);
		
		c.gridy = 4;
		subpanel.add(calculate, c);
		
		c.gridy = 5;
		subpanel.add(result, c);
		
		steps = Box.createVerticalBox();
		
		panel = new JPanel();
		panel.add(subpanel);
		panel.add(Box.createHorizontalStrut(20));
		panel.add(steps);
		
		toSave = 0;
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			steps.removeAll();
			steps.setVisible(false);
			final int blank = -1;
			double molarMass, givenMass, givenMoles;
			try
			{
				Compound c = Compound.parseCompound(compound.getText().trim());
				steps.add(new JLabel("<html>Calculate the molar mass of " + c + ":</html>"));
				String mass = c.getMolarMassSteps();
				molarMass = Double.parseDouble(mass.substring(mass.lastIndexOf('=') + 1, mass.lastIndexOf('g')));
				steps.add(new JLabel(mass));
				steps.add(Box.createVerticalStrut(5));
				steps.add(new JLabel("Molar mass = " + molarMass + " g/mol"));
			}
			catch(InvalidInputException e)
			{
				result.setText(e.getMessage());
				return;
			}
			catch(Throwable e)
			{
				molarMass = blank;
				steps.add(new JLabel("Molar mass = ? g/mol"));
			}
			
			try
			{
				givenMass = Double.parseDouble(mass.getText().trim());
				String unit = MASS_UNITS[mass.getUnit()];
				String step = "Mass = " + givenMass + " " + unit;
				if(!unit.equals("g"))
				{
					int index = indexOf(MASS_UNITS, unit);
					step = "<html>" + step + " * (10<sup>" + POWERS[index] + "</sup> g" + " / 1 " + unit + ") = ";
					givenMass *= Math.pow(10, POWERS[index]);
					step += givenMass + " g</html>";
				}
				steps.add(new JLabel(step));
			}
			catch(NumberFormatException e)
			{
				if(mass.getText().equals(""))
				{
					if(molarMass != blank)
					{
						givenMass = blank;
						steps.add(new JLabel("Mass = ? g"));
					}
					else
					{
						result.setText("One one field can be left blank.");
						return;
					}
				}
				else
				{
					result.setText("The mass must be a number.");
					return;
				}
			}
			
			try
			{
				givenMoles = Double.parseDouble(moles.getText().trim());
				steps.add(new JLabel("Moles = " + givenMoles + " mol"));
			}
			catch(NumberFormatException e)
			{
				if(moles.getText().equals(""))
				{
					if(molarMass != blank && givenMass != blank)
					{
						givenMoles = blank;
						steps.add(new JLabel("Moles = ? mol"));
					}
					else
					{
						result.setText("One one field can be left blank.");
						return;
					}
				}
				else
				{
					result.setText("The number of moles must be a number.");
					return;
				}
			}
			steps.add(Box.createVerticalStrut(5));
			
			if(molarMass == blank)
			{
				toSave = givenMass / givenMoles;
				result.setText("Molar mass = " + toSave + " g/mol");
				steps.add(new JLabel("Molar mass = " + givenMass + " / " + givenMoles + " = " + toSave + " g/mol"));
			}
			else if(givenMass == blank)
			{
				toSave = molarMass * givenMoles;
				
				steps.add(new JLabel("Mass = " + molarMass + " * " + givenMoles + " = " + toSave + " g"));
				String unit = MASS_UNITS[mass.getUnit()];
				if(!unit.equals("g"))
				{
					int index  = indexOf(MASS_UNITS, unit);
					String step = "<html>" + toSave + " g * (1 " + unit + " / 10<sup>" + POWERS[index] + "</sup> " + " g) = ";
					toSave /= Math.pow(10, POWERS[index]);
					step += toSave + " " + unit + "</html>";
					steps.add(new JLabel(step));
				}
				result.setText("Mass = " + toSave + " " + unit);
			}
			else
			{
				toSave = givenMass / molarMass;
				result.setText("Moles = " + toSave + " mol");
				steps.add(new JLabel("Mass = " + givenMass + " / " + molarMass + " = " + toSave + " mol"));
			}
			steps.setVisible(true);
		}
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return toSave;
	}
	
	public void useSavedNumber(double num)
	{
		ArrayList<String> options = new ArrayList<String>();
		options.add("Mass");
		options.add("Moles");
		Object selected = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, options.toArray(), "Mass");
		if(selected instanceof String)
		{
			if(options.indexOf(selected) == 0) mass.setAmount(num);
			else moles.setAmount(num);
		}		
	}
	
	private int indexOf(String[] arr, String str) {
		int index = -1;
		for(int i = 0; i < arr.length; i++)
			if(str.equals(arr[i]))
				index = i;
		return index;
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}