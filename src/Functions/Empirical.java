/*
 * Calculates the empirical formula of a compound given its percent composition with the option of finding the molecular formula if an amount of the compound
 * is given. Shows calculation steps
 * number() returns true- can save either the empirical or molecular molar mass, and use a saved number in moles or mass for molecular calculations.
 * 
 * Author: Julia McClellan
 * Version: 2/21/2016
 */

package Functions;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Elements.Element;

public class Empirical extends Function
{
	private static final double ERROR_VALUE = 101, REST = -1;
	private static final String ENTER_REST = "R";
	
	private JPanel panel, amounts;
	private JTextField moles, mass;
	private ArrayList<TableRow> rows;
	private Box rowBox, steps;
	private JButton calculate, addRow;
	private JLabel error, empirical, mass1, molecular, mass2;
	
	private Double[] toSave;
	
	public Empirical()
	{
		super("Empirical Formula");
		
		rows = new ArrayList<TableRow>();
		for(int num = 0; num < 2; num++)
		{
			TableRow row = new TableRow();
			rows.add(row);
		}
		JLabel element = new JLabel(" Element  ");
		element.setBorder(BorderFactory.createLineBorder(Color.black));
		JLabel percent = new JLabel(" Percent  ");
		percent.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel topRow = new JPanel();
		topRow.add(element);
		topRow.add(percent);
		topRow.add(new JLabel("              "));
		rowBox = Box.createVerticalBox();
		rowBox.add(topRow);
		for(TableRow row: rows) rowBox.add(row);
		
		addRow = new JButton("Add row");
		addRow.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						panel.setVisible(false);
						TableRow row = new TableRow();
						rowBox.add(row);
						rows.add(row);
						panel.setVisible(true);
					}
				});
		addRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		calculate = new JButton("Calculate");
		calculate.setAlignmentX(Component.LEFT_ALIGNMENT);
		calculate.addActionListener(new Calculate());
		
		error = new JLabel();
		empirical = new JLabel();
		mass1 = new JLabel();
		molecular = new JLabel();
		mass2 = new JLabel();
		
		moles = new JTextField(5);
		mass = new JTextField(5);
		amounts = new JPanel();
		amounts.add(new JLabel("Moles: "));
		amounts.add(moles);
		amounts.add(new JLabel("  Mass: "));
		amounts.add(mass);
		
		Box box = Box.createVerticalBox();
		box.add(new JLabel("If one percent is given as the remainder, enter \"R\""));
		box.add(rowBox);
		box.add(addRow);
		box.add(Box.createVerticalStrut(10));
		box.add(new JLabel("If known, enter the "));
		box.add(amounts);
		box.add(Box.createVerticalStrut(10));
		box.add(calculate);
		box.add(error);
		box.add(Box.createVerticalStrut(10));
		box.add(empirical);
		box.add(mass1);
		box.add(Box.createVerticalStrut(10));
		box.add(molecular);
		box.add(mass2);
		
		steps = Box.createVerticalBox();
		
		panel = new JPanel();
		panel.add(box);
		panel.add(steps);
		
		toSave = new Double[2];
	}
	
	private class TableRow extends JPanel
	{
		private JTextField element, percent;
		RemoveButton remove;
		
		public TableRow()
		{
			element = new JTextField(5);
			element.setBorder(BorderFactory.createLineBorder(Color.black));
			percent = new JTextField(5);
			percent.setBorder(BorderFactory.createLineBorder(Color.black));
			remove = new RemoveButton(this);
			add(element);
			add(percent);
			add(remove);
		}
		
		public Element getElement()
		{
			return PeriodicTable.find(element.getText());
		}
		
		public double getPercent()
		{
			String text = percent.getText();
			if(text.equals(ENTER_REST)) return REST;
			try
			{
				double percent = Double.parseDouble(text);
				if(percent >= 100 || percent <= 0) return ERROR_VALUE;
				else return percent;
			}
			catch(Throwable e)
			{
				return ERROR_VALUE;
			}
		}
	}
	
	private class RemoveButton extends JButton
	{
		private TableRow row;
		public RemoveButton(TableRow row1)
		{
			super("X");
			this.row = row1;
			addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent arg0)
						{
							panel.setVisible(false);
							rows.remove(row);
							rowBox.remove(row);
							panel.setVisible(true);
						}
					});
		}
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			steps.removeAll();
			steps.setVisible(false);
			empirical.setText("");
			mass1.setText("");
			molecular.setText("");
			mass2.setText("");
			double[] values = new double[rows.size()];
			Element[] elements = new Element[rows.size()];
			int rest = -1;
			JLabel restLabel = new JLabel();
			double sum = 0;
			for(int index = 0; index < rows.size(); index++)
			{
				values[index] = rows.get(index).getPercent();
				elements[index] = rows.get(index).getElement();
				if(values[index] == ERROR_VALUE)
				{
					error.setText("Value entered outside range of 0 to 100.");
					return;
				}
				if(elements[index] == null)
				{
					error.setText("Invalid element entered.");
					return;
				}
				if(values[index] == REST)
				{
					if(rest != -1)
					{
						error.setText("Only one value can be replaced by \"R\".");
						return;
					}
					rest = index;
					steps.add(restLabel);
				}
				else
				{
					steps.add(new JLabel(elements[index] + ": " + values[index]));
					sum += values[index];
				}
			}
			if(rest != -1)
			{
				if(sum >= 100)
				{
					error.setText("Your percent values add up to more than 100.");
					return;
				}
				values[rest] = 100 - sum;
				restLabel.setText(elements[rest] + ": " + values[rest]);
			}
			else if(Math.abs(100 - sum) > TOLERANCE)
			{
				error.setText("Your percent values do not add up to 100.");
				return;
			}
			steps.add(Box.createVerticalStrut(5));
			
			steps.add(new JLabel("Divide each amount by the element's molar mass:"));
			double min = Double.MAX_VALUE;
			for(int index = 0; index < values.length; index++)
			{
				double molarMass = elements[index].getMolarMass();
				String step = elements[index] + ": " + values[index] + " / " + molarMass + " = ";
				values[index] /= molarMass;
				steps.add(new JLabel(step + values[index]));
				if(min > values[index]) min = values[index];
			}
			steps.add(Box.createVerticalStrut(5));
			
			steps.add(new JLabel("Divide each value by the smallest, " + min + ":"));
			for(int index = 0; index < values.length; index++)
			{
				String step = elements[index] + ": " + values[index] + " / " + min + " = ";
				values[index] /= min;
				steps.add(new JLabel(step + values[index]));
			}
			int times = integerize(values);
			steps.add(Box.createVerticalStrut(5));
			
			steps.add(new JLabel("Multiply by " + times + " to round to an integer value:"));
			int[] coefficients = new int[values.length];
			String formula = "<html>Empirical formula: ", massString = "<html>Calculate molar mass by multiplying each element's mass by its coefficient:<br>";
			double eMass = 0;
			for(int index = 0; index < coefficients.length; index++)
			{
				String step = elements[index] + ": " + values[index] + " * " + times + " \u2248 ";
				coefficients[index] = (int)Math.round(values[index] * times);
				steps.add(new JLabel(step + coefficients[index]));
				double mass = elements[index].getMolarMass();
				eMass += coefficients[index] * mass;
				massString += "(" + coefficients[index] + " * " + mass + ") + ";
				formula += elements[index].getSymbol();
				if(coefficients[index] != 1) formula += "<sub>" + coefficients[index] + "</sub>";
			}
			formula += "</html>";
			empirical.setText(formula);
			steps.add(new JLabel(formula));
			steps.add(new JLabel(massString.substring(0, massString.length() - 2) + "= " + eMass));
			mass1.setText("Molar Mass = " + eMass + " g/mol");
			toSave[0] = eMass;
			
			if(!mass.getText().trim().equals(""))
			{
				try
				{
					double mole = Double.parseDouble(moles.getText()), mMass;
					if(mole == 1)
					{
						mMass = Double.parseDouble(mass.getText());
						steps.add(new JLabel("Molar mass = " + mMass + " g/mol"));
					}
					else
					{
						double massForMoles = Double.parseDouble(mass.getText());
						String step = "Molar mass = " + massForMoles + " g / " + mole + " mol = ";
						mMass = massForMoles / mole;
						steps.add(new JLabel(step + mMass + " g/mol"));
					}
					int factor = (int)Math.round(mMass / eMass);
					steps.add(new JLabel("Find ratio of masses and round to an integer: " + mMass + " / " + eMass + " \u2248 " + factor));
					steps.add(new JLabel("Multiply all of the coefficients by " + factor + ":"));
					formula = "<html>Molecular formula: ";
					for(int index = 0; index < coefficients.length; index++)
					{
						coefficients[index] *= factor;
						formula += elements[index].getSymbol();
						if(coefficients[index] != 1) formula += "<sub>" + coefficients[index] + "</sub>";
					}
					formula += "</html>";
					molecular.setText(formula);
					steps.add(new JLabel(formula));
					mass2.setText("Molar Mass = " + mMass + " g/mol");
					toSave[1] = mMass;
				}
				catch(Throwable e)
				{
					molecular.setText("Insuffiecient information to calculate molecular formula.");
				}
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
		if(toSave[0] != 0)
		{
			if(toSave[1] != 0)
			{
				Object selected = JOptionPane.showInputDialog(panel, "Choose which number to save", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
						null, toSave, toSave[0]);
				if(selected instanceof Double)
				{
					if(selected.equals(toSave[0])) return toSave[0];
					return toSave[1];
				}
			}
			return toSave[0];
		}
		return 0;
	}
	
	public void useSavedNumber(double num)
	{
		String[] options = {"Moles", "Mass"};
		Object selected = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, options, "Moles");
		if(selected instanceof String)
		{
			if(selected.equals("Moles")) moles.setText("" + num);
			else mass.setText("" + num);
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}