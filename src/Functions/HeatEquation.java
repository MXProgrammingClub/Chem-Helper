package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import HelperClasses.EnterField;
import HelperClasses.Units;

public class HeatEquation extends Function {
	
	private JPanel panel;
	private EnterField[] input;
	private JButton calculate;
	private JLabel result;
	private double answer;
	
	public HeatEquation() {
		super("Heat Equation");
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		input = new EnterField[4];
		input[0] = new EnterField("Heat Energy", "Energy");
		input[1] = new EnterField("Specific Heat", "Energy", "Mass*Temp");
		input[2] = new EnterField("Mass", "Mass");
		input[2].setUnit(6);
		input[3] = new EnterField("\u0394t", "Time");
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		for(int i = 0; i < input.length; i++) {
			c.gridy = i;
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
	}
	
	private class Calculate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int blank = -1;
			for(int i = 0; i < input.length; i++) {
				if(input[i].isEmpty()) 
					if(blank == -1)
						blank = i;
					else {
						result.setText("Leave only one blank.");
						return;
					}
			}
			if(!input[2].isEmpty()) input[2].setAmount(Units.toBaseUnit(input[2].getAmount(), input[2].getUnit()));
			if(!input[3].isEmpty()) input[3].setAmount(Units.toSeconds(input[3].getAmount(), input[3].getUnit()));
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
				result.setText("\u0394t = " + answer + " " + input[3].getUnit());
			}
			if(!input[2].isEmpty()) input[2].setAmount(Units.fromBaseUnit(input[2].getAmount(), input[2].getUnit()));
			if(!input[3].isEmpty()) input[3].setAmount(Units.toOriginalTime(input[3].getAmount(), input[3].getUnit()));
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
		if(result.equals(options[0])) input[0].setAmount(num);
		else if(result.equals(options[1])) input[1].setAmount(num);
		else if(result.equals(options[2])) input[2].setAmount(num);
		else if(result.equals(options[3])) input[3].setAmount(num);
	}
	
	public JPanel getPanel() {
		return panel;
	}

}
