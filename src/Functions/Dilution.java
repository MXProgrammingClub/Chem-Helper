/*
 * Uses the dilution equation to calculate the molarity or volume of the diluted solution.
 * 
 * Author: Julia McClellan
 * Version: 3/1/16
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import HelperClasses.DoubleEnterField;
import HelperClasses.Units;

public class Dilution extends Function
{
	private JPanel panel;
	private Box steps;
	private JButton calculate;
	private DoubleEnterField molarity, volume;
	private JLabel result;
	private double answer;
	
	public Dilution()
	{
		super("Dilution");
		
		molarity = new DoubleEnterField("Molarity", true, "Amount", "Volume");
		volume = new DoubleEnterField("Volume", true, "Volume");
		calculate = new JButton("Calculate");
		calculate.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					steps.removeAll();
					steps.setVisible(false);
					double[] values = {molarity.getBeforeValue(), molarity.getAfterValue(), volume.getBeforeValue(), volume.getAfterValue()};
					if(values[0] == Units.ERROR_VALUE)
					{
						result.setText("Error in value for Molarity-before.");
						return;
					}
					else if(values[1] == Units.ERROR_VALUE)
					{
						result.setText("Error in value for Molarity-after.");
						return;
					}
					else if(values[2] == Units.ERROR_VALUE)
					{
						result.setText("Error in value for Volume-before.");
						return;
					}
					else if(values[3] == Units.ERROR_VALUE)
					{
						result.setText("Error in value for Volume-after.");
						return;
					}
					else if(values[0] == Units.UNKNOWN_VALUE || values[2] == Units.UNKNOWN_VALUE)
					{
						result.setText("Do not leave a before value blank.");
						return;
					}
					
					steps.add(new JLabel("<html>M<sub>1</sub> * V<sub>1</sub> = M<sub>2</sub> * V<sub>2</sub>"));
					steps.add(new JLabel("<html>M<sub>1</sub> = " + values[0] + " mol / L"));
					steps.add(new JLabel("<html>V<sub>1</sub> = " + values[2] + " L"));
					int sigFigs = Math.min(molarity.getSigFigs(), volume.getSigFigs());
					if(values[1] == Units.UNKNOWN_VALUE)
					{
						steps.add(new JLabel("<html>M<sub>2</sub> = ? mol / L"));
						steps.add(new JLabel("<html>V<sub>2</sub> = " + values[3] + " L"));
						steps.add(Box.createVerticalStrut(10));
						double amount = values[0] * values[2] / values[3];
						steps.add(new JLabel("<html>M<sub>2</sub> = " + values[0] + " * " + values[2] + " / " + values[3] + " = " + amount));
						answer = molarity.getBlankAmount(amount);
						if(amount != answer) steps.add(new JLabel(amount + "mol / L  = " + answer + " " + molarity.getDesiredUnit()));
						result.setText("<html>M<sub>2</sub> = " + Function.withSigFigs(answer, sigFigs) + " "  + molarity.getDesiredUnit());
					}
					else if(values[3] == Units.UNKNOWN_VALUE)
					{
						steps.add(new JLabel("<html>M<sub>2</sub> = " + values[1] + " mol / L"));
						steps.add(new JLabel("<html>V<sub>2</sub> = ? L"));
						steps.add(Box.createVerticalStrut(10));
						double amount = values[2] * values[0] / values[1];
						steps.add(new JLabel("<html>V<sub>2</sub> = " + values[2] + " * " + values[0] + " / " + values[1] + " = " + amount));
						answer = volume.getBlankAmount(amount);
						if(amount != answer) steps.add(new JLabel(amount + "L  = " + answer + " " + volume.getDesiredUnit()));
						result.setText("<html>V<sub>2</sub> = " + Function.withSigFigs(answer, sigFigs) + " "  + volume.getDesiredUnit());
					}
					else 
					{
						result.setText("Leave an after value value blank.");
						return;
					}
					steps.setVisible(true);
				}
			});
		result = new JLabel();
			
		Box box = Box.createVerticalBox();
		box.add(new JLabel("<html>M<sub>1</sub> * V<sub>1</sub> = M<sub>2</sub> * V<sub>2</sub>"));
		box.add(Box.createVerticalStrut(10));
		box.add(molarity);
		box.add(volume);
		box.add(Box.createVerticalStrut(10));
		box.add(calculate);
		box.add(result);
		
		steps = Box.createVerticalBox();
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		subpanel.add(box, c);
		c.gridx++;
		subpanel.add(Box.createHorizontalStrut(10));
		c.gridx++;
		subpanel.add(steps, c);
		
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return answer;
	}
	
	public void useSavedNumber(double num)
	{
		String[] options = {"Molarity-before", "Molarity-after", "Volume-before", "Volume-after"};
		String section = (String) JOptionPane.showInputDialog(panel, "Choose where to use the number.", "Use Saved", JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);
		if(section == null) return;
		if(section.equals(options[0])) molarity.setBeforeValue(num);
		else if(section.equals(options[1])) molarity.setAfterValue(num);
		else if(section.equals(options[2])) volume.setBeforeValue(num);
		else volume.setAfterValue(num);
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}