/*
 * File: Waves.java
 * 
 * Power outage fun! This class implements the 3 wavelength equations
 * 
 * Author: Luke Giacalone
 * Version: 02/05/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import HelperClasses.EnterField;
import HelperClasses.Units;

public class Waves extends Function {
	
	private JPanel panel;
	private Box steps;
	private EnterField[] input;
	private JButton calculate;
	
	public Waves() {
		super("Waves");
		
		input = new EnterField[7];
		input[0] = new EnterField("Wavelength", "Length");
		input[0].setUnit(6);
		input[1] = new EnterField("Frequency", "Frequency");
		input[2] = new EnterField("Energy", "Energy");
		input[3] = new EnterField("Mass", "Mass");
		input[3].setUnit(6);
		input[4] = new EnterField("Velocity", "Velocity");
		input[5] = new EnterField("Light Speed", "Velocity");
		input[5].setAmount(3E8);
		input[6] = new EnterField("Planck's", "Planck");
		input[6].setAmount(6.626E-34);
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		for(int i = 0; i < input.length; i++) {
			c.gridy = i;
			subpanel.add(input[i], c);
		}
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		c.gridy++;
		subpanel.add(calculate, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 7;
		steps = Box.createVerticalBox();
		subpanel.add(steps, c);
		
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	private class Calculate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			steps.setVisible(false);
			steps.removeAll();
			if(!input[0].isEmpty()) {
				double newAmount = Units.toBaseUnit(input[0].getAmount(), input[0].getUnit());
				//steps.add(new JLabel("" + input[0].getAmount() + input[0].getUnitName() + " = " + newAmount + "m"));
				input[0].setAmount(newAmount);
				
			}
			if(!input[3].isEmpty()) {
				double newAmount = Units.toBaseUnit(input[3].getAmount(), input[3].getUnit());
				//steps.add(new JLabel("" + input[1].getAmount() + input[1].getUnitName() + " = " + newAmount + "g"));
				input[3].setAmount(newAmount);
			}
			steps.add(new JLabel());
			while(true) {
				if(input[0].isEmpty() && !input[1].isEmpty() && !input[5].isEmpty()) { //wavelength
					double newAmount = input[5].getAmount() / input[1].getAmount();
					input[0].setAmount(newAmount);
					steps.add(new JLabel("c = \u03BB\u22C5f \u2192 " + input[5].getAmount() +  "m/s / " + input[1].getAmount() 
							+ "Hz = " + newAmount + "m"));
				}
				else if(input[0].isEmpty() && !input[3].isEmpty() && !input[3].isEmpty()) { //wavelength
					double newAmount = input[6].getAmount() / (input[3].getAmount() * input[4].getAmount());
					input[0].setAmount(newAmount);
					steps.add(new JLabel("\u03BB = h/(m\u22C5v) \u2192 " + input[6].getAmount() + "J\u00B7s / (" 
							+ input[3].getAmount() + "g * " + input[4].getAmount() + "m/s) =" + newAmount + "m"));
				}
				else if(input[1].isEmpty() && !input[0].isEmpty() && !input[5].isEmpty()) { //frequency
					double newAmount = input[5].getAmount() / input[0].getAmount();
					input[1].setAmount(newAmount);
					steps.add(new JLabel("c = \u03BB\u22C5f \u2192 " + input[5].getAmount() + "m/s / " + input[0].getAmount() 
							+ "m = " + newAmount + "Hz"));
				}
				else if(input[1].isEmpty() && !input[2].isEmpty() && !input[6].isEmpty()) { //frequency
					double newAmount = input[2].getAmount() / input[6].getAmount();
					input[1].setAmount(newAmount);
					steps.add(new JLabel("E = h\u22C5f" + input[2].getAmount() + "J / " + input[6].getAmount() + "J\u00B7s = " 
							+ newAmount + "Hz"));
				}
				else if(input[2].isEmpty() && !input[0].isEmpty() && !input[5].isEmpty()) { //energy
					double newAmount = input[5].getAmount() / input[0].getAmount();
					input[2].setAmount(newAmount);
					steps.add(new JLabel("E = h\u22C5f" + input[5].getAmount() + "m/s / " + input[0].getAmount() + "m = " 
							+ newAmount + "J"));
				}
				else if(input[3].isEmpty() && !input[0].isEmpty() && !input[4].isEmpty() && !input[6].isEmpty()) { //mass
					double newAmount = input[6].getAmount() / (input[0].getAmount() * input[4].getAmount());
					input[3].setAmount(newAmount);
					steps.add(new JLabel("\u03BB = h/(m\u22C5v) \u2192 " + input[6].getAmount() + "J\u00B7s / (" 
							+ input[0].getAmount() + "m * " + input[4].getAmount() + "m/s) = " + newAmount + "g"));
				}
				else if(input[4].isEmpty() && !input[0].isEmpty() && !input[3].isEmpty() && !input[6].isEmpty()) { //velocity
					double newAmount = input[6].getAmount() / (input[0].getAmount() * input[3].getAmount());
					input[4].setAmount(newAmount);
					steps.add(new JLabel("\u03BB = h/(m\u22C5v) \u2192 " + input[6].getAmount() + "J\u00B7s / (" 
							+ input[0].getAmount() + "m * " + input[3].getAmount() + "g) = " + newAmount + "m/s"));
				}
				else if(input[5].isEmpty() && !input[0].isEmpty() && !input[1].isEmpty()) { //speed of light
					double newAmount = input[0].getAmount() * input[1].getAmount();
					input[5].setAmount(newAmount);
					steps.add(new JLabel("c = \u03BB\u22C5f \u2192 " + input[0].getAmount() + "m * " + input[1].getAmount() 
							+ "Hz = " + newAmount + "m/s"));
				}
				else if(input[6].isEmpty() && !input[1].isEmpty() && !input[2].isEmpty()) { //planck's constant
					double newAmount = input[2].getAmount() / input[1].getAmount();
					input[6].setAmount(newAmount);
					steps.add(new JLabel("E = h\u22C5f" + input[2].getAmount() + "J / " + input[1].getAmount() + "Hz = " 
							+ newAmount + "m/s"));
				}
				else if(input[6].isEmpty() && !input[0].isEmpty() && !input[3].isEmpty() && !input[4].isEmpty()) {
					double newAmount = input[0].getAmount() * input[3].getAmount() * input[4].getAmount();
					input[6].setAmount(newAmount);
					steps.add(new JLabel("\u03BB = h/(m\u22C5v) \u2192 " + input[0].getAmount() + "m * " + input[3].getAmount() 
							+ "g * " + input[4].getAmount() + "m/s = "+ newAmount + "J\u00B7s"));
				}
				else break;
			}
			if(!input[0].isEmpty()) {
				double newAmount = Units.fromBaseUnit(input[0].getAmount(), input[0].getUnit());
				//steps.add(new JLabel("" + input[0].getAmount() + "m"  + " = " + newAmount + input[0].getUnitName()));
				input[0].setAmount(newAmount);
			}
			if(!input[3].isEmpty()) {
				double newAmount = Units.fromBaseUnit(input[3].getAmount(), input[3].getUnit());
				//steps.add(new JLabel("" + input[1].getAmount() + "g" + " = " + newAmount + input[1].getUnitName()));
				input[3].setAmount(newAmount);
			}
			steps.setVisible(true);
		}
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public String getHelp() {
		return "<html>Enter all known quantities, being sure to<br>"
				+ "select the appropriate units. Click the calculate<br>"
				+ "button and ChemHelper will find as many values<br>"
				+ "as it can.</html>";
	}
	
}
