/*
 * Given two of the following, computes the third: a compound, moles of a compound, or mass of a compound. If The compound is left blank, its molar mass
 * is calculated. Also performs unit conversions, which it shouldn't do but the unit conversion class has not yet been created. Shows calculation steps.
 * number() returns true- saves last calculated value and can use saved in moles or mass.
 * 
 * Author: Julia McClellan
 * Version: 1/1/2015
 */

package Functions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ChemHelper.Compound;
import ChemHelper.InvalidInputException;

public class CompoundStoichiometry extends Function 
{
	private JPanel panel;
	private JTextField compound, mass, massUnit, moles;
	private JButton calculate;
	private JLabel result;
	private Box steps;
	private double toSave;
	
	private final static String prefixes = "GMkdcmunp";
	private final static int[] powers = {9, 6, 3, -1, -2, -3, -6, -9, -12};
	
	public CompoundStoichiometry()
	{
		super("Compound Stoichiometry");
		
		compound = new JTextField(7);
		JPanel compoundPanel = new JPanel();
		compoundPanel.add(new JLabel("Compound: "));
		compoundPanel.add(compound);
		
		mass = new JTextField(5);
		massUnit = new JTextField("g", 2);
		JPanel massPanel = new JPanel();
		massPanel.add(new JLabel("Mass: "));
		massPanel.add(mass);
		massPanel.add(massUnit);
		
		moles = new JTextField(5);
		JPanel molesPanel = new JPanel();
		molesPanel.add(new JLabel("Moles: "));
		molesPanel.add(moles);
		molesPanel.add(new JLabel("mol"));
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		calculate.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JLabel instruction = new JLabel("<html>Enter known quantities to calculate the remaining."
				+ "<br>If the compound is left blank, its molar mass will be calculated.</html>");
		instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
		result = new JLabel();
		result.setAlignmentX(Component.CENTER_ALIGNMENT);

		Box box = Box.createVerticalBox();
		box.add(instruction);
		box.add(compoundPanel);
		box.add(massPanel);
		box.add(molesPanel);
		box.add(calculate);
		box.add(result);
		
		steps = Box.createVerticalBox();
		
		panel = new JPanel();
		panel.add(box);
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
				String unit = massUnit.getText();
				String step = "Mass = " + givenMass + " " + unit;
				if(!unit.equals("g"))
				{
					if(unit.length() != 2 || unit.charAt(1) != 'g')
					{
						result.setText("That is not a valid unit for mass.");
						return;
					}
					int prefix = prefixes.indexOf(unit.charAt(0));
					if(prefix == -1)
					{
						result.setText("That is not a valid unit for mass.");
						return;
					}
					step = "<html>" + step + " * (10<sup>" + powers[prefix] + "</sup> g" + " / 1 " + unit + ") = ";
					givenMass *= Math.pow(10, powers[prefix]);
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
				String unit = massUnit.getText();
				if(!unit.equals("g"))
				{
					if(unit.length() != 2 || unit.charAt(1) != 'g')
					{
						result.setText("That is not a valid unit for mass.");
						return;
					}
					int prefix = prefixes.indexOf(unit.charAt(0));
					if(prefix == -1)
					{
						result.setText("That is not a valid unit for mass.");
						return;
					}
					String step = "<html>" + toSave + " g * (1 " + unit + " / 10<sup>" + powers[prefix] + "</sup> " + " g) = ";
					toSave /= Math.pow(10, powers[prefix]);
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
			if(options.indexOf(selected) == 0) mass.setText("" + num);
			else moles.setText("" + num);
		}		
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}