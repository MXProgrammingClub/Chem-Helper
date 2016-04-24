/*
 * Given two of the following, computes the third: a compound, moles of a compound, or mass of a compound. If The compound is left blank, its molar mass
 * is calculated. Also performs unit conversions, which it shouldn't do but the unit conversion class has not yet been created. Shows calculation steps.
 * number() returns true- saves last calculated value and can use saved in moles or mass.
 * 
 * Authors: Julia McClellan and Luke Giacalone
 * Version: 4/23/2016
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
import HelperClasses.TextField;
import HelperClasses.Units;

public class CompoundStoichiometry extends Function 
{
	private JPanel panel;
	private EnterField compound, mass, moles;
	private JButton calculate;
	private JLabel result;
	private Box steps;
	private double toSave;
	
	public CompoundStoichiometry()
	{
		super("Compound Stoichiometry");
		
		compound = new EnterField("Compound", true);
		mass = new EnterField("Mass", "Mass", false);
		moles = new EnterField("Moles", "Amount", false);
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
			int sigFigs = Integer.MAX_VALUE;
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
			
			givenMass = mass.getAmount();
			if(givenMass == Units.UNKNOWN_VALUE)
			{
				if(molarMass != blank)
				{
					givenMass = blank;
					steps.add(new JLabel("Mass = ? g"));
				}
				else
				{
					result.setText("Only one field can be left blank.");
					return;
				}
			}
			else if(givenMass == Units.ERROR_VALUE)
			{
				result.setText("The mass must be a number.");
				return;
			}
			else 
			{
				steps.add(new JLabel("Mass = " + givenMass + " g"));
				sigFigs = mass.getSigFigs();
			}
			
			givenMoles = moles.getAmount();
			if(givenMoles == Units.UNKNOWN_VALUE)
			{
				if(molarMass != blank && givenMass != blank)
				{
					givenMoles = blank;
					steps.add(new JLabel("Moles = ? mol"));
				}
				else
				{
					result.setText("Only one field can be left blank.");
					return;
				}
			}
			else if(givenMoles == Units.ERROR_VALUE)
			{
				result.setText("The number of moles must be a number.");
				return;
			}
			else 
			{
				steps.add(new JLabel("Moles = " + givenMoles + " mol"));
				sigFigs = Math.min(sigFigs, moles.getSigFigs());
			}
			
			steps.add(Box.createVerticalStrut(5));
			
			if(molarMass == blank)
			{
				toSave = givenMass / givenMoles;
				result.setText("Molar mass = " + Function.withSigFigs(toSave, sigFigs) + " g/mol");
				steps.add(Function.latex("\\text{Molar mass=}\\frac{" + givenMass + "\\text{ g}}{" + givenMoles + "\\text{ mol}}\\text{=" + toSave + 
						"\\frac{g}{mol}}"));
			}
			else if(givenMass == blank)
			{
				toSave = molarMass * givenMoles;
				steps.add(Function.latex("\\text{Mass=" + molarMass + "\\frac{g}{mol} * " + givenMoles + " mol = " + toSave + " g}"));
				String unit = mass.getUnitName();
				if(!unit.equals("g"))
				{
					String step = toSave + " g = ";
					toSave = mass.getBlankAmount(toSave);
					step += toSave + " " + unit;
					steps.add(new JLabel(step));
				}
				result.setText("Mass = " + Function.withSigFigs(toSave, sigFigs) + " " + unit);
			}
			else
			{
				toSave = givenMass / molarMass;
				result.setText("Moles = " + Function.withSigFigs(toSave, sigFigs) + " mol");
				steps.add(Function.latex("\\text{Mass=}\\frac{" + givenMass + "\\text{ g}}{" + molarMass + "\\frac{g}{mol}}\\text{=" + toSave + " mol}"));
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
	
	public String getHelp()
	{
		return "<html>" + TextField.getHelp() + "</html>";
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}