/*
 * Determines the rate law of a reaction given several trials.
 * equation() returns true- has an EquationReader as an instance variable.
 * 
 * Author: Julia McClellan
 * Version: 3/27/2015
 */

package Functions;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Equation.Compound;
import Equation.Equation;

public class RateLaw extends Function 
{
	private JPanel panel, subpanel, table;
	private EquationReader reader;
	private JButton add, calculate, reset;
	private JLabel rate, k, errorMessage, eq;
	private ArrayList<TableRow> rows;
	private ArrayList<Compound> compounds;
	private GridLayout t; //For the table
	private Box steps, results;
	
	public RateLaw()
	{
		super("Rate Law");
		panel = new JPanel();
		setPanel();
	}
	
	private void setPanel()
	{
		reader = new EquationReader(this);
		panel.add(reader.getPanel());
		
		subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		
		eq = new JLabel();
		subpanel.add(eq, c);
		
		c.gridy++;
		table = new JPanel();
		subpanel.add(table, c);
		
		add = new JButton("Add Row");
		add.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					t.setRows(t.getRows() + 1);
					subpanel.setVisible(false);
					rows.add(new TableRow());
					subpanel.setVisible(true);
				}
			});
		c.gridy++;
		subpanel.add(add, c);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		c.gridy++;
		subpanel.add(calculate, c);
		
		errorMessage = new JLabel();
		c.gridy++;
		subpanel.add(errorMessage, c);
		
		results = Box.createVerticalBox();
		rate = new JLabel();
		results.add(rate);
		k = new JLabel();
		results.add(k);
		reset = new JButton("Reset");
		reset.addActionListener(new Reset());
		results.add(reset);
		results.setVisible(false);
		c.gridy++;
		subpanel.add(results, c);
		
		subpanel.setVisible(false);
		JPanel sub2 = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		sub2.add(subpanel, c);
		c.gridx++;
		sub2.add(Box.createHorizontalStrut(10), c);
		c.gridx++;
		steps = Box.createVerticalBox();
		sub2.add(steps);
		panel.add(sub2);	
	}
	
	private class TableRow
	{
		private JTextField[] concentrations;
		private JTextField rate;
		private JLabel numLabel;
		
		public TableRow()
		{
			numLabel = new JLabel("" + (rows.size() + 1));
			numLabel.setBorder(BorderFactory.createLineBorder(Color.black));
			table.add(numLabel);
			
			concentrations = new JTextField[compounds.size()];
			for(int index = 0; index < compounds.size(); index++)
			{
				JTextField field = new JTextField(5);
				field.setBorder(BorderFactory.createLineBorder(Color.black));
				table.add(field);
				concentrations[index] = field;
			}
			
			rate = new JTextField(5);
			rate.setBorder(BorderFactory.createLineBorder(Color.black));
			table.add(rate);
		}
		
		public String getConcentration(int index)
		{
			return concentrations[index].getText();
		}
		
		public String getRate()
		{
			return rate.getText();
		}
	}
	
	private void generateTable()
	{
		t = new GridLayout(compounds.size() + 2, compounds.size() + 1);
		table.setLayout(t);
		//First row of the table
		JLabel trial = new JLabel("Trial");
		trial.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		table.add(trial);
		for(Compound c: compounds)
		{
			JLabel label = new JLabel("<html>[" + c.withoutNumState() + "]");
			label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			table.add(label);
		}
		JLabel rate = new JLabel("Rate");
		rate.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		table.add(rate);
		
		//All other rows
		rows = new ArrayList<TableRow>();
		for(int index = compounds.size(); index >= 0; index--)
		{
			rows.add(new TableRow());
		}
		subpanel.setVisible(true);
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			errorMessage.setText("");
			steps.removeAll();
			steps.setVisible(false);
			results.setVisible(false);
			//Converts the values stored in rows into a 2d array of doubles
			double[][] table = new double[rows.size()][compounds.size() + 1];
			int sigFigs = Integer.MAX_VALUE;
			for(int i = 0; i < table.length; i++)
			{
				for(int j = 0; j < table[0].length - 1; j++)
				{
					try
					{
						String concentration = rows.get(i).getConcentration(j);
						table[i][j] = Double.parseDouble(concentration);
						sigFigs = Math.min(sigFigs, Function.sigFigs(concentration));
					}
					catch(Throwable e)
					{
						errorMessage.setText("There was a problem with your input.");
						return;
					}					
				}
				try
				{
					String rate = rows.get(i).getRate();
					table[i][table[0].length - 1] = Double.parseDouble(rate);
					sigFigs = Math.min(sigFigs, Function.sigFigs(rate));
				}
				catch(Throwable e)
				{
					errorMessage.setText("There was a problem with your input.");
					return;
				}
			}
			
			ArrayList<String> stepList = new ArrayList<String>(); //For steps to be stored in during calculations
			double[] kValue = new double[1]; //Will hold the value of k once calculated
			int[] values = calculate(table, compounds.size(), kValue, stepList);
			
			String law = "<html>Rate = k";
			for(int index = 0; index < values.length; index++) 
			{
				law += "[" + compounds.get(index).withoutNum() + "]<sup>" + values[index] + "</sup>";
			}
			rate.setText(law);
			k.setText("<html>k = " + Function.withSigFigs(kValue[0], sigFigs) + " " + stepList.remove(stepList.size() - 1));
			for(String step: stepList)
			{
				if(step.equals("")) steps.add(Box.createVerticalStrut(10));
				else steps.add(new JLabel(step));
			}
			results.setVisible(true);
			steps.setVisible(true);
		}
	}
	
	public static int[] calculate(double[][] table, int compounds, double[] k, ArrayList<String> steps)
	{
		int[] values = new int[compounds];
		for(int index  = 0; index < values.length; index++)
		{
			values[index] = -1; //Sets all initial values to -1 so it can test if all are solved later.
		}
		
		for(int i = 0; i < table.length; i++)
		{
			boolean found = false;
			for(int j = 0; j < table.length; j++)
			{
				if(i != j)
				{
					String step = "<html>Trials " + (i + 1) + " and " + (j + 1) + ":<br>" + Arrays.toString(table[i]) + "<br>" + Arrays.toString(table[j]);
					String product = "<html>";
					int changed = -1;
					double value = table[j][table[0].length - 1];
					for(int index = 0; index < values.length; index++)
					{
						if(table[i][index] != table[j][index])
						{
							if(values[index] != -1)
							{
								//The value for another can still be calculated if the rate is adjusted to provide for the other changed values
								value /= Math.pow(table[j][index] / table[i][index], values[index]); 
								product += "(" + table[j][index] + " / " + table[i][index] + ")<sup>" + values[index] + "</sup>";
							}
							else if(changed == -1)
							{
								changed = index;
								product += "(" + table[j][index] + " / " + table[i][index] + ")<sup>x</sup> * ";
							}
							else
							{
								changed = -1;
								break;
							}
						}
					}
					
					if(changed != -1)
					{
						steps.add(step);
						product = product.substring(0, product.length() - 3) + " = " + table[j][table[0].length - 1] + " / " + table[i][table[0].length - 1];
						steps.add(product);
						double left = table[j][changed] / table[i][changed], right = value / table[i][table[0].length - 1];
						steps.add("<html>" + left + "<sup>x</sup> = " + right);
						values[changed] = (int)Math.round((Math.log(right) / Math.log(left))); // Equivalent to the log base left of right-> a^x=b, x=log(a)b
						steps.add("x = " + values[changed]);
						steps.add(""); //To create an extra line between trials.
					}
					
					//Checks if all values are solved and breaks if they are
					boolean solved = true;
					for(int val: values)
					{
						if(val == -1)
						{
							solved = false;
							break;
						}
					}
					if(solved)
					{
						found = true;
						break;
					}
				}
			}
			if(found) break;
		}
		
		//Calculates the value of k
		String kStep = "<html>k * ";
		double value = 1;
		for(int index = 0; index < values.length; index++)
		{
			value *= Math.pow(table[0][index], values[index]);
			kStep += "(" + table[0][index] + ")<sup>" + values[index] + "</sup> * ";
		}
		kStep = kStep.substring(0, kStep.length() - 3) + " = " + table[0][table[0].length - 1] + "</html>";
		steps.add(kStep);
		steps.add("k * " + value + " = " + table[0][table[0].length - 1]);
		k[0] = table[0][table[0].length - 1] / value;
		steps.add("k = " + table[0][table[0].length - 1] + " / " + value + " = " + k[0]);
		
		//Finds the unit of k
		int sum = 0;
		for(int num: values) sum += num;
		sum -= 1;
		String unit = "L<sup>" + sum + "</sup> / (mol<sup>" + sum + "</sup> * s)";
		steps.add("<html>k = " + k[0] + " " + unit);
		steps.add(unit); //So the value an be used later
		
		return values;
	}
	
	private class Reset implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			panel.setVisible(false);
			panel.removeAll();
			setPanel();
			panel.setVisible(true);
		}
	}
	
	public JPanel getPanel() 
	{
		return panel;
	}

	public boolean equation()
	{
		return true;
	}
	
	public Equation saveEquation()
	{
		return reader.saveEquation();
	}
	
	public void useSaved(Equation equation)
	{
		panel.setVisible(false);
		panel.remove(reader.getPanel());
		compounds = equation.getLeft();
		eq.setText("<html>" + equation + "</html");
		generateTable();
		panel.setVisible(true);
	}
	
	public String getHelp()
	{
		return "<html>Enter the equation to calculate the rate law for.<br>"
				+ "Enter all provided date. To add a row, click the<br>"
				+ "\"Add Row\" button. Click on the \"Calculate\" button<br>"
				+ "and ChemHelper will find the rate law. To enter a<br>"
				+ "new equation, click on the \"Reset\" button.</html>";
	}
}