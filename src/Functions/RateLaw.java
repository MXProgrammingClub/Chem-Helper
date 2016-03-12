/*
 * Determines the rate law of a reaction given several trials.
 * equation() returns true- has an EquationReader as an instance variable.
 * 
 * Author: Julia McClellan
 * Version: 3/12/2015
 */

package Functions;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.SwingConstants;

import Equation.Compound;
import Equation.Equation;

public class RateLaw extends Function 
{
	private JPanel panel, tablePanel, resultPanel;
	private EquationReader reader;
	private JButton calculate, reset, add;
	private ArrayList<TableRow> rows;
	private Box table, box2, box3, box4, steps;
	private JLabel errorMessage, displayEquation, law, kValue;
	private ArrayList<Compound> compounds;
	
	public RateLaw()
	{
		super("Rate Law");
		panel = new JPanel();
		setPanel();
	}
	
	private void setPanel()
	{
		reader = new EquationReader(this);
		errorMessage = new JLabel();
		
		displayEquation = new JLabel();
		add = new JButton("Add row");
		add.addActionListener(new AddRow());
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		table = Box.createVerticalBox();
		box4 = Box.createVerticalBox();
		box4.add(displayEquation);
		box4.add(Box.createVerticalStrut(10));
		box4.add(table);
		box4.add(add);
		box4.add(Box.createVerticalStrut(10));
		box4.add(calculate);
		box4.add(errorMessage);
		tablePanel = new JPanel();
		tablePanel.add(box4);
		tablePanel.setVisible(false);
		
		law = new JLabel();
		kValue = new JLabel();
		reset = new JButton("Reset");
		reset.addActionListener(new Reset());
		box2 = Box.createVerticalBox();
		box2.add(law);
		box2.add(kValue);
		box2.add(reset);
		resultPanel = new JPanel();
		resultPanel.add(box2);
		resultPanel.setVisible(false);
		
		box3 = Box.createVerticalBox();
		box3.add(tablePanel);
		box3.add(resultPanel);
		
		steps = Box.createVerticalBox();
		
		panel.add(reader.getPanel());
		panel.add(box3);
		panel.add(steps);
	}
	
	private class TableRow extends JPanel
	{
		private JTextField[] concentrations;
		private JTextField rate;
		private Box box;
		private JLabel numLabel;
		
		public TableRow(int num)
		{
			concentrations = new JTextField[compounds.size()];
			numLabel = new JLabel("" + (num + 1));
			numLabel.setBorder(BorderFactory.createLineBorder(Color.black));
			numLabel.setPreferredSize(new Dimension(30, 20));
			box = Box.createHorizontalBox();
			box.add(numLabel);
			for(int index = 0; index < compounds.size(); index++)
			{
				JTextField field = new JTextField(5);
				field.setBorder(BorderFactory.createLineBorder(Color.black));
				box.add(field);
				concentrations[index] = field;
			}
			rate = new JTextField(5);
			rate.setBorder(BorderFactory.createLineBorder(Color.black));
			box.add(rate);
			add(box);
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
		rows = new ArrayList<TableRow>();
		int cols = compounds.size(), rows = cols + 1;
		Box header = Box.createHorizontalBox();
		JLabel trial = new JLabel("Trial");
		trial.setPreferredSize(new Dimension(30, 30));
		trial.setMinimumSize(new Dimension(30, 30));
		trial.setMaximumSize(new Dimension(30, 30));
		trial.setBorder(BorderFactory.createLineBorder(Color.black));
		header.add(trial);
		JTextField testSize = new JTextField(5);
		for(Compound c: compounds)
		{
			String compound = c.toString();
			if(c.getNum() != 1) compound = compound.substring(1);
			JLabel cLabel = new JLabel("<html>" + compound + "</html>", SwingConstants.CENTER);
			cLabel.setPreferredSize(new Dimension(testSize.getPreferredSize().width, 30));
			cLabel.setMaximumSize(new Dimension(testSize.getPreferredSize().width, 30));
			cLabel.setMinimumSize(new Dimension(testSize.getPreferredSize().width, 30));
			cLabel.setBorder(BorderFactory.createLineBorder(Color.black));
			header.add(cLabel);
		}
		JLabel rate = new JLabel("Rate", SwingConstants.CENTER);
		rate.setPreferredSize(new Dimension(testSize.getPreferredSize().width, 30));
		rate.setMinimumSize(new Dimension(testSize.getPreferredSize().width, 30));
		rate.setMaximumSize(new Dimension(testSize.getPreferredSize().width, 30));
		rate.setBorder(BorderFactory.createLineBorder(Color.black));
		header.add(rate);
		table.add(header);
		for(int row = 0; row < rows; row++)
		{
			TableRow newRow = new TableRow(row);
			this.rows.add(newRow);
			table.add(newRow);
		}
	}
	
	private class AddRow implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			tablePanel.setVisible(false);
			TableRow newRow = new TableRow(rows.size());
			rows.add(newRow);
			table.add(newRow);
			tablePanel.setVisible(true);
		}
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			box4.remove(errorMessage);
			steps.removeAll();
			steps.setVisible(false);
			resultPanel.setVisible(false);
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
						box4.add(errorMessage);
						box4.setVisible(true);
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
					box4.add(errorMessage);
					box4.setVisible(true);
					return;
				}
			}
			
			ArrayList<String> stepList = new ArrayList<String>(); //For steps to be stored in during calculations
			double[] k = new double[1]; //Will hold the value of k once calculated
			int[] values = calculate(table, compounds.size(), k, stepList);
			
			String rate = "<html>Rate = k";
			for(int index = 0; index < values.length; index++) 
			{
				rate += "[" + compounds.get(index).withoutNum() + "]<sup>" + values[index] + "</sup>";
			}
			law.setText(rate);
			kValue.setText("<html>k = " + Function.withSigFigs(k[0], sigFigs) + " " + stepList.remove(stepList.size() - 1));
			for(String step: stepList)
			{
				if(step.equals("")) steps.add(Box.createVerticalStrut(10));
				else steps.add(new JLabel(step));
			}
			resultPanel.setVisible(true);
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
		panel.remove(reader.getPanel());
		panel.repaint();
		compounds = equation.getLeft();
		displayEquation.setText("<html>" + equation + "</html");
		generateTable();
		tablePanel.setVisible(true);
	}
}