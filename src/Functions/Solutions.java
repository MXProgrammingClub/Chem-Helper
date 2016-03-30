/*
 * Calculates properties of solutions.
 * 
 * Author: Julia McClellan
 * Version: 2/15/16
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
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
	private LinkedList<Double> numbers;
	private Box steps;
	private JLabel error;
	private static final String[] solValues1 = {"Solute", "Solvent", "Solution"}, solValues2 = {"Mass", "Vol", "Mole"}, 
			percents = {"% by Mass", "% by Volume"}, moles = {"Molarity", "Molality", "Mole Fraction"};
	
	public Solutions()
	{
		super("Solution Properties");
		numbers = new LinkedList<Double>();
		
		JPanel subPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		
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
			percent[i] = new EnterField(percents[i], "Percent");
			subPanel.add(percent[i], c);
			c.gridy++;
		}

		mole = new EnterField[moles.length];
		for(int i = 0; i < moles.length; i++)
		{
			mole[i] = new EnterField(moles[i], i == 2 ? null : "Amount", (i == 0) ? "Volume" : (i == 1) ? "Mass" : null);
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
					steps.removeAll();
					steps.setVisible(false);
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
					
					//mass solute / mass solution * 100 = percent by mass
					if(values[9] == Units.UNKNOWN_VALUE && values[0] != Units.UNKNOWN_VALUE && values[6] != Units.UNKNOWN_VALUE)
					{
						double result = 100 * values[0] / values[6];
						int sigFigs = Math.min(sol[0][0].getSigFigs(), sol[2][0].getSigFigs());
						steps.add(new JLabel("Percent by Mass = (Mass Solute / Mass Solution) * 100"));
						steps.add(new JLabel("Percent by Mass = ?"));
						steps.add(new JLabel("Mass Solute = " + values[0] + " g"));
						steps.add(new JLabel("Mass Solution = " + values[6] + " g"));
						steps.add(new JLabel("Percent by Mass = (" + values[0] + " / " + values[6] + ") * 100 = " + result + "%"));
						steps.add(new JLabel("Percent by Mass = " + Function.withSigFigs(result, sigFigs) + "%"));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						percent[0].setAmount(result);
					}
					else if(values[0] == Units.UNKNOWN_VALUE && values[9] != Units.UNKNOWN_VALUE && values[6] != Units.UNKNOWN_VALUE)
					{
						double result = values[9] * values[6] / 100;
						int sigFigs = Math.min(percent[0].getSigFigs(), sol[2][0].getSigFigs());
						steps.add(new JLabel("Percent by Mass = (Mass Solute / Mass Solution) * 100"));
						steps.add(new JLabel("Percent by Mass = " + values[9] + "%"));
						steps.add(new JLabel("Mass Solute = ? g"));
						steps.add(new JLabel("Mass Solution = " + values[6] + " g"));
						steps.add(new JLabel("Mass Solute = (" + values[9] + " * " + values[6] + ") / 100 = " + result + " g"));
						result = sol[0][0].getBlankAmount(result);
						steps.add(new JLabel("Mass Solute = " + Function.withSigFigs(result, sigFigs) + " " + sol[0][0].getUnitName()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[0][0].setAmount(result);
					}
					else if(values[6] == Units.UNKNOWN_VALUE && values[0] != Units.UNKNOWN_VALUE && values[9] != Units.UNKNOWN_VALUE)
					{
						double result = values[0] / values[9] * 100;
						int sigFigs = Math.min(percent[0].getSigFigs(), sol[0][0].getSigFigs());
						steps.add(new JLabel("Percent by Mass = (Mass Solute / Mass Solution) * 100"));
						steps.add(new JLabel("Percent by Mass = " + values[9] + "%"));
						steps.add(new JLabel("Mass Solute = " + values[0] + " g"));
						steps.add(new JLabel("Mass Solution = ? g"));
						steps.add(new JLabel("Mass Solution = (" + values[0] + " / " + values[9] + ") * 100 = " + result + " g"));
						result = sol[2][0].getBlankAmount(result);
						steps.add(new JLabel("Mass Solute = " + Function.withSigFigs(result, sigFigs) + " " + sol[2][0].getUnitName()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[2][0].setAmount(result);
					}
					
					//volume solute / volume solution * 100 = percent by volume
					if(values[10] == Units.UNKNOWN_VALUE && values[1] != Units.UNKNOWN_VALUE && values[7] != Units.UNKNOWN_VALUE)
					{
						double result = 100 * values[1] / values[7];
						int sigFigs = Math.min(sol[0][1].getSigFigs(), sol[2][1].getSigFigs());
						steps.add(new JLabel("Percent by Volume = (Volume Solute / Volume Solution) * 100"));
						steps.add(new JLabel("Percent by Volume = ?"));
						steps.add(new JLabel("Volume Solute = " + values[1] + " L"));
						steps.add(new JLabel("Volume Solution = " + values[7] + " L"));
						steps.add(new JLabel("Percent by Volume = (" + values[1] + " / " + values[7] + ") * 100 = " + result + "%"));
						steps.add(new JLabel("Percent by Volume = " + Function.withSigFigs(result, sigFigs) + "%"));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						percent[1].setAmount(result);
					}
					else if(values[1] == Units.UNKNOWN_VALUE && values[10] != Units.UNKNOWN_VALUE && values[7] != Units.UNKNOWN_VALUE)
					{
						double result = values[10] * values[7] / 100;
						int sigFigs = Math.min(percent[1].getSigFigs(), sol[2][1].getSigFigs());
						steps.add(new JLabel("Percent by Volume = (Volume Solute / Volume Solution) * 100"));
						steps.add(new JLabel("Percent by Volume = " + values[10] + "%"));
						steps.add(new JLabel("Volume Solute = ? L"));
						steps.add(new JLabel("Volume Solution = " + values[7] + " L"));
						steps.add(new JLabel("Volume Solute = (" + values[10] + " * " + values[7] + ") / 100 = " + result + " L"));
						result = sol[0][1].getBlankAmount(result);
						steps.add(new JLabel("Volume Solute = " + Function.withSigFigs(result, sigFigs) + " " + sol[0][1].getUnitName()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[0][1].setAmount(result);
					}
					else if(values[7] == Units.UNKNOWN_VALUE && values[1] != Units.UNKNOWN_VALUE && values[10] != Units.UNKNOWN_VALUE)
					{
						double result = values[1] / values[10] * 100;
						int sigFigs = Math.min(percent[1].getSigFigs(), sol[0][1].getSigFigs());
						steps.add(new JLabel("Percent by Volume = (Volume Solute / Volume Solution) * 100"));
						steps.add(new JLabel("Percent by Volume = " + values[10] + "%"));
						steps.add(new JLabel("Volume Solute = " + values[1] + " L"));
						steps.add(new JLabel("Volume Solution = ? L"));
						steps.add(new JLabel("Volume Solution = (" + values[1] + " / " + values[10] + ") * 100 = " + result + " L"));
						result = sol[2][1].getBlankAmount(result);
						steps.add(new JLabel("Mass Solute = " + Function.withSigFigs(result, sigFigs) + " " + sol[2][1].getUnitName()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[2][1].setAmount(result);
					}
					
					//moles solute / volume solution = molarity
					if(values[11] == Units.UNKNOWN_VALUE && values[2] != Units.UNKNOWN_VALUE && values[7] != Units.UNKNOWN_VALUE)
					{
						double result = values[2] / values[7];
						int sigFigs = Math.min(sol[0][2].getSigFigs(), sol[2][1].getSigFigs());
						steps.add(new JLabel("Molarity = Moles Solute / Volume Solution"));
						steps.add(new JLabel("Molarity = ?"));
						steps.add(new JLabel("Moles Solute = " + values[2] + " mol"));
						steps.add(new JLabel("Volume Solution = " + values[7] + " L"));
						steps.add(new JLabel("Molarity = " + values[2] + " / " + values[7] + " = " + result + " mol / L"));
						result = mole[0].getBlankAmount(result);
						steps.add(new JLabel("Molarity = " + Function.withSigFigs(result, sigFigs) + " " + mole[0].getUnitName() + " / " +
								mole[0].getUnit2Name()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						mole[0].setAmount(result);
					}
					else if(values[7] == Units.UNKNOWN_VALUE && values[2] != Units.UNKNOWN_VALUE && values[11] != Units.UNKNOWN_VALUE)
					{
						double result = values[2] / values[11];
						int sigFigs = Math.min(sol[0][2].getSigFigs(), mole[0].getSigFigs());
						steps.add(new JLabel("Molarity = Moles Solute / Volume Solution"));
						steps.add(new JLabel("Molarity = " + values[11] + " mol / L"));
						steps.add(new JLabel("Moles Solute = " + values[2] + " mol"));
						steps.add(new JLabel("Volume Solution = ? L"));
						steps.add(new JLabel("Volume Solution = " + values[2] + " / " + values[11] + " = " + result + " L"));
						result = sol[2][1].getBlankAmount(result);
						steps.add(new JLabel("Volume Solution = " + Function.withSigFigs(result, sigFigs) + " " + sol[2][1].getUnitName()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[2][1].setAmount(result);
					}
					else if(values[2] == Units.UNKNOWN_VALUE && values[7] != Units.UNKNOWN_VALUE && values[11] != Units.UNKNOWN_VALUE)
					{
						double result = values[7] * values[11];
						int sigFigs = Math.min(sol[2][1].getSigFigs(), mole[0].getSigFigs());
						steps.add(new JLabel("Molarity = Moles Solute / Volume Solution"));
						steps.add(new JLabel("Molarity = " + values[11] + " mol / L"));
						steps.add(new JLabel("Moles Solute = ? mol"));
						steps.add(new JLabel("Volume Solution = " + values[7] + " L"));
						steps.add(new JLabel("Moles Solution = " + values[7] + " * " + values[11] + " = " + result + " mol"));
						result = sol[0][2].getBlankAmount(result);
						steps.add(new JLabel("Moles Solution = " + Function.withSigFigs(result, sigFigs) + " " + sol[0][2].getUnitName()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[0][2].setAmount(result);
					}
					
					//moles solute / mass solution = molality
					if(values[12] == Units.UNKNOWN_VALUE && values[2] != Units.UNKNOWN_VALUE && values[3] != Units.UNKNOWN_VALUE)
					{
						double result = values[2] / (values[3] / 1000); //the base unit of mass is kg, not g
						int sigFigs = Math.min(sol[0][2].getSigFigs(), sol[1][0].getSigFigs());
						steps.add(new JLabel("Molality = Moles Solute / Mass Solution"));
						steps.add(new JLabel("Molality = ?"));
						steps.add(new JLabel("Moles Solute = " + values[2] + " mol"));
						steps.add(new JLabel("Mass Solution = " + (values[3] / 1000) + " kg"));
						steps.add(new JLabel("Molality = " + values[2] + " / " + (values[3] / 1000) + " = " + result + " mol / kg"));
						result = mole[1].getBlankAmount((result / 1000)); //Convert from mol / kg to mol / g to the desired unit
						steps.add(new JLabel("Molality = " + Function.withSigFigs(result, sigFigs) + " " + mole[1].getUnitName() + " / " +
								mole[1].getUnit2Name()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						mole[1].setAmount(result);
					}
					else if(values[3] == Units.UNKNOWN_VALUE && values[2] != Units.UNKNOWN_VALUE && values[12] != Units.UNKNOWN_VALUE)
					{
						double result = values[2] / (values[12] * 1000); //From mol / g to mol / kg
						int sigFigs = Math.min(sol[0][2].getSigFigs(), mole[1].getSigFigs());
						steps.add(new JLabel("Molality = Moles Solute / Mass Solution"));
						steps.add(new JLabel("Molality = " + (values[12] * 1000) + " mol / kg"));
						steps.add(new JLabel("Moles Solute = " + values[2] + " mol"));
						steps.add(new JLabel("Mass Solution = ? kg"));
						steps.add(new JLabel("Mass Solution = " + values[2] + " / " + (values[12] * 1000) + " = " + result + " kg"));
						result = sol[1][0].getBlankAmount((result * 1000)); //From kg to g to desired unit
						steps.add(new JLabel("Mass Solution = " + Function.withSigFigs(result, sigFigs) + " " + sol[1][0].getUnitName()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[1][0].setAmount(result);
					}
					else if(values[2] == Units.UNKNOWN_VALUE && values[3] != Units.UNKNOWN_VALUE && values[12] != Units.UNKNOWN_VALUE)
					{
						double result = values[3] * values[12];
						int sigFigs = Math.min(sol[1][0].getSigFigs(), mole[1].getSigFigs());
						steps.add(new JLabel("Molality = Moles Solute / Mass Solution"));
						steps.add(new JLabel("Molality = " + (values[12] / 1000) + " mol / kg"));
						steps.add(new JLabel("Moles Solute = ? mol"));
						steps.add(new JLabel("Mass Solution = " + (values[3] / 1000) + " kg"));
						steps.add(new JLabel("Moles Solution = " + (values[3] / 1000) + " * " + (values[12] / 1000) + " = " + result + " mol"));
						result = sol[0][2].getBlankAmount(result);
						steps.add(new JLabel("Moles Solution = " + Function.withSigFigs(result, sigFigs) + " " + sol[0][2].getUnitName()));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[0][2].setAmount(result);
					}
					
					//Moles Solute / Moles Solution = Mole Fraction
					if(values[13] == Units.UNKNOWN_VALUE && values[2] != Units.UNKNOWN_VALUE && values[8] != Units.UNKNOWN_VALUE)
					{
						double result = values[2] / values[8];
						int sigFigs = Math.min(sol[0][2].getSigFigs(), sol[2][2].getSigFigs());
						steps.add(new JLabel("Mole Fraction = Moles Solute / Moles Solution"));
						steps.add(new JLabel("Mole Fraction = ?"));
						steps.add(new JLabel("Moles Solute = " + values[2] + " mol"));
						steps.add(new JLabel("Moles Solution = " + values[8] + " mol"));
						steps.add(new JLabel("Mole Fraction = " + values[2] + " / " + values[8] + " = " + result));
						steps.add(new JLabel("Mole Fraction = " + Function.withSigFigs(result, sigFigs)));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						mole[2].setAmount(result);
					}
					else if(values[8] == Units.UNKNOWN_VALUE && values[2] != Units.UNKNOWN_VALUE && values[13] != Units.UNKNOWN_VALUE)
					{
						double result = values[2] / values[13];
						int sigFigs = Math.min(sol[0][2].getSigFigs(), mole[2].getSigFigs());
						steps.add(new JLabel("Mole Fraction = Moles Solute / Moles Solution"));
						steps.add(new JLabel("Mole Fraction = " + values[13]));
						steps.add(new JLabel("Moles Solute = " + values[2] + " mol"));
						steps.add(new JLabel("Moles Solution = ? mol"));
						steps.add(new JLabel("Moles Solution = " + values[2] + " / " + values[13] + " = " + result + " mol"));
						steps.add(new JLabel("Moles Solution = " + Function.withSigFigs(result, sigFigs) + " mol"));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[2][2].setAmount(result);
					}
					else if(values[2] == Units.UNKNOWN_VALUE && values[8] != Units.UNKNOWN_VALUE && values[13] != Units.UNKNOWN_VALUE)
					{
						double result = values[8] * values[13];
						int sigFigs = Math.min(sol[2][2].getSigFigs(), mole[2].getSigFigs());
						steps.add(new JLabel("Mole Fraction = Moles Solute / Moles Solution"));
						steps.add(new JLabel("Mole Fraction = " + values[13]));
						steps.add(new JLabel("Moles Solute = ? mol"));
						steps.add(new JLabel("Moles Solution = " + values[8] + " mol"));
						steps.add(new JLabel("Moles Solute = " + values[8] + " * " + values[13] + " = " + result + " mol"));
						steps.add(new JLabel("Moles Solute = " + Function.withSigFigs(result, sigFigs) + " mol"));
						steps.add(Box.createVerticalStrut(10));
						numbers.add(result);
						sol[0][2].setAmount(result);
					}
					
					steps.setVisible(true);
				}
			});
		subPanel.add(calculate, c);
		
		error = new JLabel();
		c.gridy++;
		subPanel.add(error, c);
		
		JPanel subPanel2 = new JPanel(new GridBagLayout());
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		subPanel2.add(subPanel, c);
		c.gridx++;
		subPanel2.add(Box.createHorizontalStrut(20), c);
		c.gridx++;
		steps = Box.createVerticalBox();
		subPanel2.add(steps, c);
		
		panel = new JPanel();
		panel.add(subPanel2);
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		if(numbers.size() == 0) return 0;
		if(numbers.size() == 1) return numbers.get(0);
		return (Double) JOptionPane.showInputDialog(panel, "Choose a number to save.", "Save Number", JOptionPane.QUESTION_MESSAGE, null, numbers.toArray(), 
				numbers.get(0));
	}
	
	public void useSavedNumber(double num)
	{
		String[] options = {"Solute Mass", "Solute Volume", "Solute Moles", "Solvent Mass", "Solvent Volume", "Solvent Moles", "Solution Mass", 
				"Solution Volume", "Solution Moles", "Percent by Mass", "Percent by Volume", "Molarity", "Molality", "Mole Fraction"};
		String option = (String)JOptionPane.showInputDialog(panel, "Choose where to use number.", "Use Saved", JOptionPane.QUESTION_MESSAGE, null, 
				options, options[0]);
		if(option == null) return;
		switch(option)
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
	
	public String getHelp()
	{
		return "<html>Enter all known information about the solution with<br>"
				+ "the appropriate units, then click the calculate button<br>"
				+ "and ChemHelper will display all properties which can<br>"
				+ "be calculated from the provided information.</html>";
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}