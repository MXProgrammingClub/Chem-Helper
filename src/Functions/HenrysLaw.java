/*
 * Calculates the solubility or pressure of a solution using Henry's Law.
 * 
 * Author: Julia McClellan
 * Version: 2/8/2016
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import HelperClasses.DoubleEnterField;
import HelperClasses.Units;

public class HenrysLaw extends Function
{
	private JPanel panel;
	private DoubleEnterField solubility, pressure;
	private JButton calculate;
	private JLabel result;
	private Box steps;
	private double num;
	
	public HenrysLaw()
	{
		super("Henry's Law");
		
		solubility = new DoubleEnterField("Solubility", true, "Mass", "Volume");
		pressure = new DoubleEnterField("Pressure", true, "Pressure");
		calculate = new JButton("Calculate");
		calculate.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					steps.setVisible(false);
					steps.removeAll();
					steps.add(new JLabel("<html>S<sub>1</sub> / S<sub>2</sub> = P<sub>1</sub> / P<sub>2</sub>"));
					steps.add(Box.createVerticalStrut(5));
					
					double s1 = solubility.getBeforeValue(), s2 = solubility.getAfterValue(), p1 = pressure.getBeforeValue(), p2 = pressure.getAfterValue();
					if(s1 == Units.ERROR_VALUE || s2 == Units.ERROR_VALUE || p1 == Units.ERROR_VALUE || p2 == Units.ERROR_VALUE)
					{
						result.setText("There was a problem with your input.");
						return;
					}
					
					int sigFigs = Math.min(solubility.getSigFigs(), pressure.getSigFigs());
					if(sigFigs == -1)
					{
						result.setText("Do not leave an original value blank.");
					}
					
					s1 /= 10; //The enterfield will convert it to g/L, and dividing by 10 gives g/dL = g/100mL
					if(s2 == Units.UNKNOWN_VALUE)
					{
						if(p2 == Units.UNKNOWN_VALUE)
						{
							result.setText("Leave only one value blank");
							return;
						}
						steps.add(new JLabel("<html>S<sub>1</sub> = " + s1 + " g / 100mL</html>"));
						steps.add(new JLabel("<html>P<sub>1</sub> = " + p1 + " atm</html>"));
						steps.add(new JLabel("<html>S<sub>2</sub> = ? g / 100mL</html>"));
						steps.add(new JLabel("<html>P<sub>1</sub> = " + p1 + " atm</html>"));
						steps.add(Box.createVerticalStrut(5));
						
						steps.add(new JLabel("<html>S<sub>2</sub> = S<sub>1</sub> * P<sub>2</sub> / P<sub>1</sub></html>"));
						s2 = s1 * p2 / p1;
						steps.add(new JLabel("<html>S<sub>2</sub> = " + s2 + " g / 100mL"));
						num = solubility.getBlankAmount(s2 * 10); //Into the standard units of g/L
						String unit = "g / 100mL";
						if(s2 != num)
						{
							unit = solubility.getDesiredUnit();
							steps.add(new JLabel(s2 + " g / 100mL = " + num + " " + unit));
						}
						result.setText("<html>S<sub>2</sub> = " + Function.withSigFigs(num, sigFigs) + " " + unit);
					}
					else
					{
						if(s2 == Units.UNKNOWN_VALUE)
						{
							result.setText("Leave only one value blank");
							return;
						}
						s2 /= 10;
						steps.add(new JLabel("<html>S<sub>1</sub> = " + s1 + " g / 100mL</html>"));
						steps.add(new JLabel("<html>P<sub>1</sub> = " + p1 + " atm</html>"));
						steps.add(new JLabel("<html>S<sub>2</sub> = " + s2 + " g / 100mL</html>"));
						steps.add(new JLabel("<html>P<sub>1</sub> = ? atm</html>"));
						steps.add(Box.createVerticalStrut(5));
						
						steps.add(new JLabel("<html>P<sub>2</sub> = P<sub>1</sub> * S<sub>2</sub> / S<sub>1</sub></html>"));
						p2 = s1 * p2 / s1;
						steps.add(new JLabel("<html>P<sub>2</sub> = " + p2 + " atm"));
						num = pressure.getBlankAmount(p2);
						String unit = "atm";
						if(p2 != num)
						{
							unit = pressure.getDesiredUnit();
							steps.add(new JLabel(p2 + " atm = " + num + " " + unit));
						}
						result.setText("<html>P<sub>2</sub> = " + Function.withSigFigs(num, sigFigs) + " " + unit);
					}
					steps.setVisible(true);
				}
			});
		result = new JLabel();
		
		Box box = Box.createVerticalBox();
		box.add(new JLabel("<html>S<sub>1</sub> / S<sub>2</sub> = P<sub>1</sub> / P<sub>2</sub>"));
		box.add(solubility);
		box.add(pressure);
		box.add(calculate);
		box.add(result);
		
		panel = new JPanel();
		panel.add(box);
		panel.add(Box.createHorizontalStrut(5));
		steps = Box.createVerticalBox();
		panel.add(steps);
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return num;
	}
	
	public void useSavedNumber(double num)
	{
		String[] options = {"Solubility - before", "Solubility - after", "Pressure - before", "Pressure - after"};
		String result = (String)JOptionPane.showInputDialog(panel, "Choose where to use the number.", "Choose number", JOptionPane.PLAIN_MESSAGE, null, 
				options, options[0]);
		if(result.equals(options[0])) solubility.setBeforeValue(num);
		else if(result.equals(options[1])) solubility.setAfterValue(num);
		else if(result.equals(options[2])) pressure.setBeforeValue(num);
		else pressure.setAfterValue(num);
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}