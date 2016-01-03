/*
 * Calculates something's density, mass, or volume given the other two.
 * number() returns true- saves last calculated value and can use saved for density, mass, or volume.
 * 
 * Author: Julia McClellan
 * Version: 12/31/2015
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Density extends Function 
{
	private JPanel panel;
	private JTextField density, volume, mass, vUnit, mUnit;
	private JButton calculate;
	private JLabel result;
	private double toSave;
	
	private static final String prefixes = "GMkdcmunp";
	private static final int[] powers = {9, 6, 3, -1, -2, -3, -6, -9, -12};
	
	public Density()
	{
		super("Density Calculator");
		toSave = 0;
		
		density = new JTextField(5);
		JPanel densityPanel = new JPanel();
		densityPanel.add(new JLabel("Density:"));
		densityPanel.add(density);
		densityPanel.add(new JLabel("g/L"));
		
		volume = new JTextField(5);
		vUnit = new JTextField("L", 2);
		JPanel volumePanel = new JPanel();
		volumePanel.add(new JLabel("Volume:"));
		volumePanel.add(volume);
		volumePanel.add(vUnit);
		
		mass = new JTextField(5);
		mUnit = new JTextField("g", 2);
		JPanel massPanel = new JPanel();
		massPanel.add(new JLabel("Mass"));
		massPanel.add(mass);
		massPanel.add(mUnit);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		
		result = new JLabel();
		
		Box box = Box.createVerticalBox();
		box.add(new JLabel("Enter known information:"));
		box.add(densityPanel);
		box.add(volumePanel);
		box.add(massPanel);
		box.add(calculate);
		box.add(result);
		
		panel = new JPanel();
		panel.add(box);
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			double givenMass = 0, givenVolume = 0, givenDensity = 0;
			String unknown = "", unknownUnit = "";
			try
			{
				givenMass = Double.parseDouble(mass.getText());
				String unit = mUnit.getText();
				if(!unit.equals("g"))
				{
					if(unit.length() == 2 && unit.charAt(1) == 'g' && prefixes.indexOf(unit.charAt(0)) != -1) 
					{
						givenMass = normalizeUnit(givenMass, unit.charAt(0));
					}
					else
					{
						result.setText("Unaccptable unit for mass");
						return;
					}
				}
			}
			catch(Throwable t)
			{
				if(mass.getText().trim().equals(""))
				{
					unknown = "Mass";
					unknownUnit = mUnit.getText();
				}
				else
				{
					result.setText("Unacceptable value for mass.");
					return;
				}
			}
			
			try
			{
				givenVolume = Double.parseDouble(volume.getText());
				String unit = vUnit.getText();
				if(!unit.equals("L"))
				{
					if(unit.length() == 2 && unit.charAt(1) == 'L' && prefixes.indexOf(unit.charAt(0)) != -1) 
					{
						givenVolume = normalizeUnit(givenVolume, unit.charAt(0));
					}
					else
					{
						result.setText("Unaccptable unit for volume.");
						return;
					}
				}
			}
			catch(Throwable t)
			{
				if(volume.getText().trim().equals(""))
				{
					if(!unknown.equals(""))
					{
						result.setText("Leave only one field blank.");
						return;
					}
					else
					{
						unknown = "Volume";
						unknownUnit = vUnit.getText();
					}
				}
				else
				{
					result.setText("Unacceptable value for volume.");
					return;
				}
			}

			try
			{
				givenDensity = Double.parseDouble(density.getText());
			}
			catch(Throwable t)
			{
				if(density.getText().trim().equals(""))
				{
					if(!unknown.equals(""))
					{
						result.setText("Leave only one field blank.");
						return;
					}
					else unknown = "Density";
				}
				else
				{
					result.setText("Unacceptable value for density.");
					return;
				}
			}
			
			double amount;
			if(unknown.equals("Mass"))
			{
				amount = givenDensity * givenVolume;
				if(!unknownUnit.equals("g"))
				{
					if(unknownUnit.length() == 2 && unknownUnit.charAt(1) == 'g' && prefixes.indexOf(unknownUnit.charAt(0)) != -1) 
					{
						amount = convertUnit(amount, unknownUnit.charAt(0));
					}
					else
					{
						result.setText("Unaccptable unit for mass");
						return;
					}
				}
			}
			else if(unknown.equals("Volume"))
			{
				amount = givenMass / givenDensity;
				if(!unknownUnit.equals("L"))
				{
					if(unknownUnit.length() == 2 && unknownUnit.charAt(1) == 'L' && prefixes.indexOf(unknownUnit.charAt(0)) != -1) 
					{
						amount = convertUnit(amount, unknownUnit.charAt(0));
					}
					else
					{
						result.setText("Unaccptable unit for volume");
						return;
					}
				}
			}
			else if(unknown.equals("Density"))
			{
				amount = givenMass / givenVolume;
				unknownUnit = "g/L";
			}
			else
			{
				result.setText("Leave one field blank.");
				return;
			}
			toSave = amount;
			result.setText(unknown + " = " + amount + unknownUnit);
		}
	}
	
	private double normalizeUnit(double number, char prefix)
	{
		int index = prefixes.indexOf(prefix);
		if(index != -1) return number * Math.pow(10, powers[index]);
		return 0;
	}
	
	private double convertUnit(double number, char prefix)
	{
		int index = prefixes.indexOf(prefix);
		if(index != -1) return number / Math.pow(10, powers[index]);
		return 0;
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
		String[] fields = {"Density", "Volume", "Mass"};
		Object field = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, fields, "Density");
		if(field instanceof String)
		{
			if(field.equals("Density")) density.setText("" + num);
			else if(field.equals("Volume")) volume.setText("" + num);
			else mass.setText("" + num);
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}