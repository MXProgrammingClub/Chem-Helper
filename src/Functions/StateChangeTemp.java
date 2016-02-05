/*
 * File: StateChangeTemp.java
 * 
 * This function calculates the change in temperature in a state change based on the number of particles in solution
 * (boiling point elevation and freezing point depression)
 * 
 * Author: Luke Giacalone and Julia McClellan
 * Version: 02/5/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import HelperClasses.EnterField;
import HelperClasses.Units;

public class StateChangeTemp extends Function {
	
	private static final String[] TEMPERATURE_UNITS = Units.getUnits("Temperature");
	private static final String[] MOLE_UNITS = Units.getUnits("Amount");
	private static final String[] MASS_UNITS = Units.getUnits("Mass");
	private static final int UNKNOWN_VALUE = -500, ERROR_VALUE = -501;
	
	private JPanel panel, inputPanel;
	private JRadioButton togetherI, seperatedI;
	private EnterField deltaT, i, iIon, iSolute, k, m;
	private JButton calculate;
	private JLabel result;
	private double number;
	
	public StateChangeTemp() {
		super("State Change Temperature");;
		
		Box iBox = Box.createHorizontalBox();
		togetherI = new JRadioButton("<html><i>i</i> as One Value</html>");
		seperatedI = new JRadioButton("<html><i>i</i> as Seperated Values</html>");
		togetherI.setSelected(true);
		seperatedI.setSelected(false);
		iBox.add(togetherI);
		iBox.add(seperatedI);
		
		inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		deltaT = new EnterField("\u0394t", TEMPERATURE_UNITS);
		i = new EnterField("<html><i>i</i></html>");
		iIon = new EnterField("Ions", MOLE_UNITS);
		iSolute = new EnterField("Solute", MOLE_UNITS);
		k = new EnterField("k");
		m = new EnterField("molality", MOLE_UNITS, MASS_UNITS);
		c.gridx = 0;
		c.gridy = 0;
		inputPanel.add(deltaT, c);
		c.gridy = 1;
		inputPanel.add(i, c);
		c.gridy = 2;
		inputPanel.add(iIon, c);
		c.gridy = 3;
		inputPanel.add(iSolute, c);
		c.gridy = 4;
		inputPanel.add(k, c);
		c.gridy = 5;
		inputPanel.add(m, c);
		inputPanel.setVisible(true);
		i.setVisible(true);
		iIon.setVisible(false);
		iSolute.setVisible(false);
		m.setUnit2(6);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		
		result = new JLabel("");
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		subpanel.add(iBox, c);
		c.gridy = 1;
		subpanel.add(inputPanel, c);
		c.gridy = 2;
		subpanel.add(calculate, c);
		c.gridy = 3;
		subpanel.add(result, c);
		
		panel = new JPanel();
		panel.add(subpanel);
		
		togetherI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(seperatedI.isSelected()) {
					seperatedI.setSelected(false);
					i.setVisible(true);
					iIon.setVisible(false);
					iSolute.setVisible(false);
				}
				else
					togetherI.setSelected(true);
			}
		});
		seperatedI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(togetherI.isSelected()) {
					togetherI.setSelected(false);
					i.setVisible(false);
					iIon.setVisible(true);
					iSolute.setVisible(true);
				}
				else
					seperatedI.setSelected(true);
			}
		});
	}
	
	private class Calculate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			double[] values = new double[4];
			values[0] = toKelvin(deltaT.getAmount(), deltaT.getUnit());
			if(togetherI.isSelected())
				values[1] = i.getAmount();
			else
				if(iIon.getAmount() == UNKNOWN_VALUE || iSolute.getAmount() == UNKNOWN_VALUE)
					values[1] = UNKNOWN_VALUE;
				else
					values[1] = iIon.getAmount() / iSolute.getAmount();
			values[2] = k.getAmount();
			values[3] = toMolPerGram(m.getAmount(), m.getUnit2());
			
			int blank = -1;
			
			for(int index = 0; index < values.length; index++) {
				if(values[index] == ERROR_VALUE) {
					result.setText("An entered value was not a number.");
					return;
				}
				else if(values[index] == UNKNOWN_VALUE)
				{
					if(blank == -1)
						blank = index;
					else {
						result.setText("Only one value can be left blank.");
						return;
					}
				}
			}
			
			if(blank == -1)
				result.setText("Leave one value blank");
			else if(blank == 0)
				result.setText("\u0394t = " + toOriginalTemp(values[1] * values[2] * values[3], deltaT.getUnit()) 
								+ " " + deltaT.getUnitName());
			else if(blank == 1)
			{
				number = values[0] / values[2] / values[3];
				result.setText("<html><i>i</i></html> = " + number);
			}
			else if(blank == 2)
			{
				number = values[0] / values[1] / values[3];
				result.setText("k = " + number);
			}
			else if (blank == 3)
			{
				number = toOriginalMass(values[0] / values[1] / values[2], m.getUnit2());
				result.setText("m = " + number + " mol/" + MASS_UNITS[m.getUnit2()]);
			}
		}
	}
	
	//returns the kelvin amount of a temperature
	private double toKelvin(double amount, int unit) {
		if(amount == UNKNOWN_VALUE) return amount;
		else if(amount == ERROR_VALUE) return amount;
		else if(unit == 0) return amount; //if kelvin
		else if(unit == 1) return Units.celsiusToKelvin(amount); //if celcius
		else return Units.fahrenheitToKelvin(amount); //if fahrenheit
	}
	
	//returns the molality in mol/g
	private double toMolPerGram(double amount, int unit2) {
		if(amount == UNKNOWN_VALUE) return amount;
		else if(amount == ERROR_VALUE) return amount;
		else if(unit2 == 6) return amount;
		else return Units.fromBaseUnit(amount, unit2);
	}
	
	private double toOriginalTemp(double amount, int unit) {
		if(unit == 0) return amount; //if kelvin
		else if(unit == 1) return Units.kelvinToCelsius(amount);//if celcius
		else return Units.kelvinToFahrenheit(amount); //if fahrenheit
	}
	
	private double toOriginalMass(double amount, int unit2) {
		if(unit2 == 6) return amount;
		else return Units.toBaseUnit(amount, unit2);
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return number;
	}
	
	public void useSavedNumber(double num)
	{
		String[] options = {"\u0394t", "<html><i>i</i></html>", "Ions", "Solute", "k", "Molality"};
		String result = (String) JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose number", JOptionPane.PLAIN_MESSAGE, null, 
				options, "\u0394t");
		if(result.equals(options[0])) deltaT.setAmount(num);
		else if(result.equals(options[1])) i.setAmount(num);
		else if(result.equals(options[2])) iIon.setAmount(num);
		else if(result.equals(options[3])) iSolute.setAmount(num);
		else if(result.equals(options[4])) k.setAmount(num);
		else m.setAmount(num);
	}
}
