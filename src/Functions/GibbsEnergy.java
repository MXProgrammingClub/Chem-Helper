/*
 * Calculates the Gibbs Free Energy of a reaction and determines if the reaction is spontaneous.
 * number returns true- saves last calculated value and can use saved in any field.
 * 
 * Author: Julia McClellan
 * Version: 2/3/2016
 */
package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import HelperClasses.EnterField;
import HelperClasses.Units;

public class GibbsEnergy extends Function 
{
	private static final String[] FIELDS = {"\u0394G", "T", "\u0394S"};
	private EnterField[] fields;
	private JPanel panel;
	private JButton calculate;
	private JLabel answer, spontaneous;
	private double dG;
	
	public GibbsEnergy()
	{
		super("Gibbs Free Energy");
		Box box = Box.createVerticalBox();
		box.add(new JLabel("\u0394G = \u0394H - T\u0394S"));		
		fields = new EnterField[3];
		fields[0] = new EnterField("\u0394H");
		fields[1] = new EnterField("T", IdealGas.UNITS[3]);
		fields[2] = new EnterField("\u0394S");
		for(EnterField field: fields) box.add(field);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						try
						{
							double dH = Double.parseDouble(fields[0].getText()), t = Double.parseDouble(fields[1].getText()), 
									dS = Double.parseDouble(fields[2].getText());
							int unit = fields[1].getUnit();
							if(unit == 1) t = Units.celsiusToKelvin(t);
							else if(unit == 2) t = Units.fahrenheitToKelvin(t);
							dG = dH - t * dS;
							answer.setText("\u0394G = " + dG);
							if(dG > 0) spontaneous.setText("Not spontaneous");
							else if(dG == 0) spontaneous.setText("Equilibrium");
							else spontaneous.setText("Spontaneous");
						}
						catch(Throwable e)
						{
							answer.setText("There was a problem with your input.");
							spontaneous.setText("");
						}
					}
				});
		box.add(calculate);
		
		answer = new JLabel();
		box.add(answer);
		spontaneous = new JLabel();
		box.add(spontaneous);
		
		panel = new JPanel();
		panel.add(box);
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return dG;
	}
	
	public void useSavedNumber(double num)
	{
		String s = (String)JOptionPane.showInputDialog(panel, "Choose where to use it.", "Choose Field", JOptionPane.PLAIN_MESSAGE, null, FIELDS, FIELDS[0]);
		if(s.equals(FIELDS[0])) fields[0].setText("" + num);
		else if(s.equals(FIELDS[1])) fields[1].setText("" + num);
		else fields[2].setText("" + num);
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}