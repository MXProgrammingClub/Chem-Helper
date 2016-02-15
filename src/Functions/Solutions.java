/*
 * Calculates properties of solutions.
 * 
 * Author: Julia McClellan
 * Version: 2/14/16
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import HelperClasses.EnterField;
import HelperClasses.Units;

public class Solutions extends Function
{
	private JPanel panel;
	private EnterField[][] sol;
	private EnterField[] percent, mole;
	private JButton calculate;
	private ArrayList<Double> numbers;
	private Box steps;
	private JLabel error;
	private static final String[] solValues1 = {"Solute", "Solvent", "Solution"}, solValues2 = {"Mass", "Vol", "Mole"}, percents = {"% by Mass",
			"% by Volume"}, moles = {"Molarity", "Molality", "Mole Fraction"};
	
	public Solutions()
	{
		super("Solution Properties");
		numbers = new ArrayList<Double>();
		
		JPanel subPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;

		sol = new EnterField[solValues1.length][solValues2.length];
		for(int i = 0; i < solValues1.length; i++)
		{
			for(int j = 0; j < solValues2.length; j++)
			{
				sol[i][j] = new EnterField(solValues1[i] + " " + solValues2[j], j == 0 ? "Mass" : j == 1 ? "Volume" : "Amount");
				subPanel.add(sol[i][j], c);
				c.gridy++;
			}
		}
		
		percent = new EnterField[percents.length];
		for(int i = 0; i < percents.length; i++)
		{
			percent[i] = new EnterField(percents[i]);
			subPanel.add(percent[i], c);
			c.gridy++;
		}

		mole = new EnterField[moles.length];
		for(int i = 0; i < moles.length; i++)
		{
			mole[i] = new EnterField(moles[i], "Amount", (i == 0) ? "Volume" : (i == 1) ? "Mass" : "Amount");
			subPanel.add(mole[i], c);
			c.gridy++;
		}
		
		subPanel.add(Box.createVerticalStrut(10), c);
		c.gridy++;
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					double[] values = new double[sol.length * sol[0].length + percent.length + mole.length];
					int index = 0;
					for(int i = 0; i < sol.length; i++)
					{
						for(int j = 0; j < sol[0].length; j++)
						{
							values[index] = sol[i][j].getAmount();
							if(values[index++] == Units.ERROR_VALUE)
							{
								error.setText("Error in value entered for " + solValues1[i] + " " + solValues2[j]);
							}
						}
					}
					for(int i = 0; i < percent.length; i++)
					{
						values[index] = percent[i].getAmount();
						if(values[index++] == Units.ERROR_VALUE)
						{
							error.setText("Error in value entered for " + percents[i]);
						}
					}
					for(int i = 0; i < mole.length; i++)
					{
						values[index] = mole[i].getAmount();
						if(values[index++] == Units.ERROR_VALUE)
						{
							error.setText("Error in value entered for " + moles[i]);
						}
					}
				}
			});
		subPanel.add(calculate, c);
		
		error = new JLabel();
		c.gridy++;
		subPanel.add(error, c);
		
		panel = new JPanel();
		panel.add(subPanel);
		steps = Box.createVerticalBox();
		panel.add(steps);
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		if(numbers.size() == 0) return 0;
		return (Double) JOptionPane.showInputDialog(panel, "Choose a number to save.", "Save Number", JOptionPane.QUESTION_MESSAGE, null, numbers.toArray(), 
				numbers.get(0));
	}
	
	public void useSavedNumber(double num)
	{
		String[] options = {"Solute Mass", "Solute Volume", "Solute Moles", "Solvent Mass", "Solvent Volume", "Solvent Moles", "Solution Mass", 
				"Solution Volume", "Solution Moles", "Percent by Mass", "Percent by Volume", "Molarity", "Molality", "Mole Fraction"};
		switch((String)JOptionPane.showInputDialog(panel, "Choose where to use number.", "Use Saved", JOptionPane.QUESTION_MESSAGE, null, options, options[0]))
		{
			case "Solute Mass":
				sol[0][0].setAmount(num);
				break;
			case "Solute Volume":
				sol[0][1].setAmount(num);
				break;
			case "Solute Moles":
				sol[0][2].setAmount(num);
				break;
			case "Solvent Mass":
				sol[1][0].setAmount(num);
				break;
			case "Solvent Volume":
				sol[1][1].setAmount(num);
				break;
			case "Solvent Moles":
				sol[1][2].setAmount(num);
				break;
			case "Solution Mass":
				sol[2][0].setAmount(num);
				break;
			case "Solution Volume":
				sol[2][1].setAmount(num);
				break;
			case "Solution Moles":
				sol[2][2].setAmount(num);
				break;
			case "Percent by Mass":
				percent[0].setAmount(num);
				break;
			case "Percent by Volume":
				percent[1].setAmount(num);
				break;
			case "Molarity":
				mole[0].setAmount(num);
				break;
			case "Molality":
				mole[1].setAmount(num);
				break;
			default:
				mole[2].setAmount(num);
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}