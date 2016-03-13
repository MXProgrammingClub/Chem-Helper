/*
 * File: HeatEquation.java
 * 
 * Represents the q=mc(dT) equation.
 * 
 * Author: Luke Giacalone
 * Version: 03/12/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import HelperClasses.EnterField;
import HelperClasses.Units;

public class HeatEquation extends Function {
	
	private JPanel panel;
	private JRadioButton togetherTemp, seperatedTemp;
	private EnterField[] input;
	private JButton calculate;
	private JLabel result;
	private double answer;
	
	public HeatEquation() {
		super("Heat Equation");
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		input = new EnterField[6];
		input[0] = new EnterField("Heat Energy", "Energy");
		input[1] = new EnterField("Specific Heat", "Energy", "Mass*Temp");
		input[2] = new EnterField("Mass", "Mass");
		input[2].setUnit(6);
		input[3] = new EnterField("\u0394T", "Temperature");
		input[4] = new EnterField("Start Temp", "Temperature");
		input[4].setVisible(false);
		input[5] = new EnterField("End Temp", "Temperature");
		input[5].setVisible(false);
		
		c.gridx = 0;
		c.gridy = 0;
		togetherTemp = new JRadioButton("\u0394T as One Value");
		togetherTemp.setSelected(true);
		seperatedTemp = new JRadioButton("\u0394T as Start/End");
		subpanel.add(togetherTemp, c);
		c.gridx = 1;
		subpanel.add(seperatedTemp, c);
		
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		c.gridx = 0;
		for(int i = 0; i < input.length; i++) {
			c.gridy++;
			subpanel.add(input[i], c);
		}
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		c.gridy++;
		c.anchor = GridBagConstraints.CENTER;
		subpanel.add(calculate, c);
		
		result = new JLabel();
		c.gridy++;
		subpanel.add(result, c);
		
		panel = new JPanel();
		panel.add(subpanel);
		
		togetherTemp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(seperatedTemp.isSelected()) {
					seperatedTemp.setSelected(false);
					input[3].setVisible(true);
					input[4].setVisible(false);
					input[5].setVisible(false);
				}
				else
					togetherTemp.setSelected(true);
			}
		});
		seperatedTemp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(togetherTemp.isSelected()) {
					togetherTemp.setSelected(false);
					input[3].setVisible(false);
					input[4].setVisible(true);
					input[5].setVisible(true);
				}
				else
					seperatedTemp.setSelected(true);
			}
		});
	}
	
	private class Calculate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int blank = -1;
			if(!input[4].isEmpty() && !input[5].isEmpty() && input[3].isEmpty()) {
				input[3].setAmount(Units.toKelvin(input[5].getAmount(), input[5].getUnit()) 
						- Units.toKelvin(input[4].getAmount(), input[4].getUnit()));
				input[3].setUnit(0);
			}
			for(int i = 0; i < 4; i++) { //dont traverse the seperated temp
				if(input[i].isEmpty()) 
					if(blank == -1)
						blank = i;
					else {
						result.setText("Leave only one blank.");
						return;
					}
			}
			if(!input[2].isEmpty()) input[2].setAmount(Units.toBaseUnit(input[2].getAmount(), input[2].getUnit()));
			if(!input[3].isEmpty()) input[3].setAmount(Units.toKelvin(input[3].getAmount(), input[3].getUnit()));
			if(blank == 0) {
				answer = input[1].getAmount() * input[2].getAmount() * input[3].getAmount();
				result.setText("q = " + answer + " J");
			}
			else if(blank == 1) {
				answer = input[0].getAmount() / input[2].getAmount() / input[3].getAmount();
				result.setText("c = " + answer + " J/(" + input[1].getUnit2Name() + ")");
			}
			else if(blank == 2) {
				answer = input[0].getAmount() / input[1].getAmount() / input[3].getAmount();
				answer = Units.fromBaseUnit(answer, input[2].getUnit());
				result.setText("m = " + answer + " " + input[2].getUnitName());
			}
			else if(blank == 3) {
				answer = input[0].getAmount() / input[1].getAmount() / input[2].getAmount();
				answer = Units.toOriginalTime(answer, input[3].getUnit());
				result.setText("\u0394T = " + answer + " " + input[3].getUnit());
			}
			if(!input[2].isEmpty()) input[2].setAmount(Units.fromBaseUnit(input[2].getAmount(), input[2].getUnit()));
			if(!input[3].isEmpty()) input[3].setAmount(Units.toOriginalTemp(input[3].getAmount(), input[3].getUnit()));
		}
	}
	
	public boolean number() {
		return true;
	}
	
	public double saveNumber() {
		return answer;
	}
	
	public void useSavedNumber(double num) {
		String[] options = {"Heat Energy", "Specific Heat", "Mass", "\u0394t"};
		String result = (String) JOptionPane.showInputDialog(panel, "Choose where to use the number", 
				"Choose number", JOptionPane.PLAIN_MESSAGE, null, options, "Heat Energy");
		if(result == null) return;
		if(result.equals(options[0])) input[0].setAmount(num);
		else if(result.equals(options[1])) input[1].setAmount(num);
		else if(result.equals(options[2])) input[2].setAmount(num);
		else if(result.equals(options[3])) input[3].setAmount(num);
	}
	
	public JPanel getPanel() {
		return panel;
	}

}
