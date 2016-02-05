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

import javax.swing.JButton;
import javax.swing.JPanel;

import HelperClasses.EnterField;
import HelperClasses.Units;

public class Waves extends Function {
	
	private JPanel panel;
	private EnterField[] input;
	private JButton calculate;
	
	public Waves() {
		super("Waves");
		
		input = new EnterField[7];
		input[0] = new EnterField("Wavelength", Units.getUnits("Length"));
		input[0].setUnit(6);
		input[1] = new EnterField("Frequency", Units.getUnits("Frequency"));
		input[2] = new EnterField("Energy", Units.getUnits("Energy"));
		input[3] = new EnterField("Mass", Units.getUnits("Mass"));
		input[3].setUnit(6);
		input[4] = new EnterField("Velocity", Units.getUnits("Velocity"));
		input[5] = new EnterField("Light Speed", Units.getUnits("Velocity"));
		input[5].setAmount(3E8);
		input[6] = new EnterField("Planck's", Units.getUnits("Planck"));
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
		
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	private class Calculate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(!input[0].isEmpty()) input[0].setAmount(Units.toBaseUnit(input[0].getAmount(), input[0].getUnit()));
			if(!input[3].isEmpty()) input[3].setAmount(Units.toBaseUnit(input[3].getAmount(), input[3].getUnit()));
			while(true) {
				if(input[0].isEmpty() && !input[1].isEmpty() && !input[5].isEmpty()) //wavelength
					input[0].setAmount(input[5].getAmount() / input[1].getAmount());
				else if(input[0].isEmpty() && !input[3].isEmpty() && !input[3].isEmpty()) //wavelength
					input[0].setAmount(input[6].getAmount() / (input[3].getAmount() * input[4].getAmount()));
				else if(input[1].isEmpty() && !input[0].isEmpty() && !input[5].isEmpty()) //frequency
					input[1].setAmount(input[5].getAmount() / input[0].getAmount());
				else if(input[1].isEmpty() && !input[2].isEmpty() && !input[6].isEmpty()) //frequency
					input[1].setAmount(input[2].getAmount() / input[6].getAmount());
				else if(input[2].isEmpty() && !input[0].isEmpty() && !input[5].isEmpty()) //energy
					input[2].setAmount(input[5].getAmount() / input[0].getAmount());
				else if(input[3].isEmpty() && !input[0].isEmpty() && !input[4].isEmpty() && !input[6].isEmpty()) //mass
					input[3].setAmount(input[6].getAmount() / (input[0].getAmount() * input[4].getAmount()));
				else if(input[4].isEmpty() && !input[0].isEmpty() && !input[3].isEmpty() && !input[6].isEmpty()) //velocity
					input[4].setAmount(input[6].getAmount() / (input[0].getAmount() * input[3].getAmount()));
				else if(input[5].isEmpty() && !input[0].isEmpty() && !input[1].isEmpty()) //speed of light
					input[5].setAmount(input[0].getAmount() * input[1].getAmount());
				else if(input[6].isEmpty() && !input[1].isEmpty() && !input[2].isEmpty()) //planck's constant
					input[6].setAmount(input[2].getAmount() / input[1].getAmount());
				else if(input[6].isEmpty() && !input[0].isEmpty() && !input[3].isEmpty() && !input[4].isEmpty() )
					input[6].setAmount(input[0].getAmount() * input[3].getAmount() * input[4].getAmount());
				else break;
			}
			if(!input[0].isEmpty()) input[0].setAmount(Units.fromBaseUnit(input[0].getAmount(), input[0].getUnit()));
			if(!input[3].isEmpty()) input[3].setAmount(Units.fromBaseUnit(input[3].getAmount(), input[3].getUnit()));
		}
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
}
