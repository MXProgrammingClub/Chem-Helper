package Functions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
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
	
	public CompoundStoichiometry()
	{
		super("Compound Stoichiometry");
		
		compound = new JTextField(7);
		JPanel compoundPanel = new JPanel();
		compoundPanel.add(new JLabel("Compound: "));
		compoundPanel.add(compound);
		compoundPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		mass = new JTextField(5);
		massUnit = new JTextField("g", 2);
		JPanel massPanel = new JPanel();
		massPanel.add(new JLabel("Mass: "));
		massPanel.add(mass);
		massPanel.add(massUnit);
		massPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		moles = new JTextField(5);
		JPanel molesPanel = new JPanel();
		molesPanel.add(new JLabel("Moles: "));
		molesPanel.add(moles);
		molesPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		calculate.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JLabel instruction = new JLabel("<html>Enter known quantities to calculate the remaining."
				+ "<br>If the compound is left blank, its molar mass will be calculated.</html>");
		instruction.setAlignmentX(Component.RIGHT_ALIGNMENT);
		result = new JLabel();
		result.setAlignmentX(Component.RIGHT_ALIGNMENT);

		Box box = Box.createVerticalBox();
		box.add(instruction);
		box.add(compoundPanel);
		box.add(massPanel);
		box.add(molesPanel);
		box.add(calculate);
		box.add(result);
		
		panel = new JPanel();
		panel.add(box);
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			final int blank = -1;
			double molarMass, givenMass, givenMoles;
			try
			{
				molarMass = Compound.parseCompound(compound.getText().trim()).getMolarMass();
			}
			catch(InvalidInputException e)
			{
				result.setText(e.getMessage());
				return;
			}
			catch(Throwable e)
			{
				molarMass = blank;
			}
			
			try
			{
				givenMass = Double.parseDouble(mass.getText().trim());
				String unit = massUnit.getText();
				if(!unit.equals("g"))
				{
					if(unit.length() != 2 || unit.charAt(1) != 'g')
					{
						result.setText("That is not a valid unit for mass.");
						return;
					}
					char[] prefixes = {'G', 'M', 'k', 'd', 'c', 'm', 'u', 'n', 'p'};
					int[] powers = {9, 6, 3, -1, -2, -3, -6, -9, -12};
					int prefix = -1;
					for(int index = 0; index < prefixes.length; index++)
					{
						if(prefixes[index] == unit.charAt(0))
						{
							prefix = index;
							break;
						}
					}
					if(prefix == -1)
					{
						result.setText("That is not a valid unit for mass.");
						return;
					}
					givenMass *= Math.pow(10, powers[prefix]);
				}
			}
			catch(NumberFormatException e)
			{
				if(mass.getText().equals(""))
				{
					if(molarMass != blank) givenMass = blank;
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
			}
			catch(NumberFormatException e)
			{
				if(moles.getText().equals(""))
				{
					if(molarMass != blank && givenMass != blank) givenMoles = blank;
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
			
			if(molarMass == blank) result.setText("Molar mass = " + (givenMass / givenMoles) + " g/mol");
			else if(givenMass == blank) result.setText("Mass = " + (molarMass * givenMoles) + " g");
			else result.setText("Moles = " + (givenMass / molarMass) + " mol");
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}