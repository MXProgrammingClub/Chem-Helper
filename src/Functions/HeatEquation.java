package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import HelperClasses.EnterField;
import HelperClasses.Units;

public class HeatEquation extends Function {
	
	private JPanel panel;
	private EnterField[] input;
	private JButton calculate;
	
	public HeatEquation() {
		super("Heat");
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		input = new EnterField[4];
		input[0] = new EnterField("Heat Energy", Units.getUnits("Energy"));
		input[1] = new EnterField("Specific Heat", Units.getUnits("Energy"), new String[]{"g\u00B7K", "g\u00B7\u2103"});
		input[2] = new EnterField("Mass", Units.getUnits("Mass"));
		input[3] = new EnterField("\u0394t", Units.getUnits("Time"));
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		for(int i = 0; i < input.length; i++) {
			c.gridy = i;
			subpanel.add(input[i], c);
		}
		
		calculate = new JButton("Calculate");
		c.gridy++;
		c.anchor = GridBagConstraints.CENTER;
		subpanel.add(calculate, c);
		
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	public JPanel getPanel() {
		return panel;
	}

}
