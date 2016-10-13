/*
 * File: StateChangeTemp.java
 * 
 * This function calculates the change in temperature in a state change based on the number of particles in solution
 * (boiling point elevation and freezing point depression)
 * 
 * Author: Luke Giacalone and Julia McClellan
 * Version: 3/18/2016
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
	
	private JPanel panel, inputPanel;
	private Box steps;
	private JRadioButton togetherI, seperatedI;
	private EnterField deltaT, i, iIon, iSolute, k, m;
	private JButton calculate;
	private JLabel result;
	private double number;
	
	public StateChangeTemp() {
		super("Boiling Point Elevation/Freezing Point Depression");
		
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
		deltaT = new EnterField("\u0394t", "Temperature");
		i = new EnterField("<html><i>i</i></html>");
		iIon = new EnterField("Ions", "Amount");
		iSolute = new EnterField("Solute", "Amount");
		k = new EnterField("k");
		m = new EnterField("Molality", "Amount", "Mass");
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
		
		steps = Box.createVerticalBox();
		
		panel = new JPanel();
		panel.add(subpanel);
		panel.add(steps);
		
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
			steps.setVisible(false);
			steps.removeAll();
			double[] values = new double[4];
			int sigFigs = Integer.MAX_VALUE;
			
			values[0] = deltaT.getAmount();
			steps.add(Function.latex("\u0394t = " + (values[0] == Units.UNKNOWN_VALUE ? "?" : values[0]) + " K"));
			if(values[0] != Units.UNKNOWN_VALUE) sigFigs = deltaT.getSigFigs();
			
			if(togetherI.isSelected())
			{
				values[1] = i.getAmount();
				if(values[1] != Units.UNKNOWN_VALUE) sigFigs = Math.min(sigFigs, i.getSigFigs());
			}
			else
				if(iIon.getAmount() == Units.UNKNOWN_VALUE || iSolute.getAmount() == Units.UNKNOWN_VALUE)
					values[1] = Units.UNKNOWN_VALUE;
				else
				{
					values[1] = iIon.getAmount() / iSolute.getAmount();
					sigFigs = Math.min(Math.min(iIon.getSigFigs(), iSolute.getSigFigs()), sigFigs);
				}
			steps.add(Function.latex("i = " + (values[1] == Units.UNKNOWN_VALUE ? "?" : values[1])));	
			
			values[2] = k.getAmount();
			steps.add(Function.latex("k = " + (values[2] == Units.UNKNOWN_VALUE ? "?" : values[2])));
			if(values[2] != Units.UNKNOWN_VALUE) sigFigs = Math.min(sigFigs, k.getSigFigs());
			
			values[3] = m.getAmount(); 
			if(values[3] != Units.UNKNOWN_VALUE)
			{
				values[3] *= 1000; //Conversion from base of mol / g to desired of mol / kg
				sigFigs = Math.min(sigFigs, m.getSigFigs());
			}
			steps.add(Function.latex("m = " + (values[3] == Units.UNKNOWN_VALUE ? "?" : values[3]) + " \\frac{mol}{kg}"));
			
			steps.add(Function.latex("\u0394t = i * k * m"));
			int blank = -1;
			
			for(int index = 0; index < values.length; index++) {
				if(values[index] == Units.ERROR_VALUE) {
					result.setText("An entered value was not a number.");
					return;
				}
				else if(values[index] == Units.UNKNOWN_VALUE) {
					if(blank == -1)
						blank = index;
					else {
						result.setText("Only one value can be left blank.");
						return;
					}
				}
			}
			
			if(blank == -1) result.setText("Leave one value blank");
			else if(blank == 0) {
				number = values[1] * values[2] * values[3];
				steps.add(Function.latex("\u0394t = " + values[1] + " * " + values[2] + " * " + values[3] + " = " + number + " K"));
				number = deltaT.getBlankAmount(number);
				steps.add(Function.latex("\u0394t = " + number + " " + deltaT.getUnitName()));
				result.setText("\u0394t = " + Function.withSigFigs(number, sigFigs) + " " + deltaT.getUnitName());
			}
			else if(blank == 1) {
				number = values[0] / values[2] / values[3];
				steps.add(Function.latex("i = \\frac{\\frac{" + values[0] + "}{" + values[2] + "}{" + values[3] + "} = " + number));
				result.setText("<html><i>i</i> = " + Function.withSigFigs(number, sigFigs) + "</html>");
			}
			else if(blank == 2) {
				number = values[0] / values[1] / values[3];
				steps.add(Function.latex("k = \\frac{\\frac{" + values[0] + "}{" + values[1] + "}{" + values[3] + "} = " + number));
				result.setText("k = " + Function.withSigFigs(number, sigFigs));
			}
			else if (blank == 3) {
				number = values[0] / values[1] / values[2];
				steps.add(Function.latex("m = \\frac{\\frac{" + values[0] + "}{" + values[1] + "}{" + values[2] + "} = " + number + " \\frac{mol}{kg}"));
				number /= 1000; //Converting from mol / kg to base of mol / g
				number = m.getBlankAmount(number);
				steps.add(Function.latex("m = " + number + " \\frac{mol}{" + m.getUnit2Name() + "}"));
				result.setText("m = " + Function.withSigFigs(number, sigFigs) + " mol / " + m.getUnit2Name());
			}
			
			steps.setVisible(true);
		}
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
		String result = (String) JOptionPane.showInputDialog(panel, "Choose where to use the number", 
				"Choose number", JOptionPane.PLAIN_MESSAGE, null, options, "\u0394t");
		if(result.equals(options[0])) deltaT.setAmount(num);
		else if(result.equals(options[1])) i.setAmount(num);
		else if(result.equals(options[2])) iIon.setAmount(num);
		else if(result.equals(options[3])) iSolute.setAmount(num);
		else if(result.equals(options[4])) k.setAmount(num);
		else m.setAmount(num);
	}
	
	public String getHelp() {
		return "<html>Enter all known quantities, being sure to<br>"
				+ "select the appropriate units. If <i>i</i> is given<br>"
				+ "as one value, select the corresponding button. If <i>i</i><br>"
				+ "is given as moles of ions and moles of solute,<br>"
				+ "select the corresponding button. For the remaining<br>"
				+ "value, select the desired unit. Click the calculate<br>"
				+ "button and ChemHelper will calculate the remaining<br>"
				+ "value.";
	}
	
}
