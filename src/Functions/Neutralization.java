/*
 * Calculates the molarity or volume of the acidic or basic solution needed to neutralize another.
 * 
 * Author: Julia McClellan
 * Version: 3/2/16
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

import HelperClasses.EnterField;
import HelperClasses.Units;

public class Neutralization extends Function 
{
	private JPanel panel;
	private EnterPanel acid, base;
	private JLabel result;
	private JButton calculate;
	private Box steps;
	private double number;
	private String unit;
	
	public Neutralization()
	{
		super("Neutralization");
		
		acid = new EnterPanel("Acid");
		base = new EnterPanel("Base");
		calculate = new JButton("Calculate");
		calculate.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					steps.setVisible(false);
					steps.removeAll();
					double[] nums = {acid.getMolarity(), acid.getVolume(), base.getMolarity(), base.getVolume()};
					int sigFigs = Math.min(acid.getSigFigs(), base.getSigFigs());
					steps.add(Box.createVerticalStrut(5));
					
					if(nums[0] == Units.ERROR_VALUE)
					{
						result.setText("Error in value for acid molarity.");
						return;
					}
					if(nums[1] == Units.ERROR_VALUE)
					{
						result.setText("Error in value for acid volume.");
						return;
					}
					if(nums[2] == Units.ERROR_VALUE)
					{
						result.setText("Error in value for base molarity.");
						return;
					}
					if(nums[3] == Units.ERROR_VALUE)
					{
						result.setText("Error in value for base volume.");
						return;
					}
					
					if(nums[0] == Units.UNKNOWN_VALUE) number = calculateMolarity(acid, base, new double[]{nums[1], nums[2], nums[3]});
					else if(nums[1] == Units.UNKNOWN_VALUE) number = calculateVolume(acid, base, new double[]{nums[0], nums[2], nums[3]});
					else if(nums[2] == Units.UNKNOWN_VALUE) number = calculateMolarity(base, acid, new double[]{nums[3], nums[0], nums[1]});
					else if(nums[3] == Units.UNKNOWN_VALUE) number = calculateVolume(base, acid, new double[]{nums[2], nums[0], nums[1]});
					else
					{
						result.setText("Leave a value blank");
						return;
					}
					
					if(number == Units.UNKNOWN_VALUE)
					{
						result.setText("Leave only one value blank.");
						return;
					}
					
					result.setText(Function.withSigFigs(number, sigFigs) + " " + unit);
					steps.setVisible(true);
				}
				
				private double calculateVolume(EnterPanel main, EnterPanel other, double[] values)
				{
					steps.add(new JLabel(main.getName() + " volume = " + other.getName() + " molarity * " + other.getName() + " volume / " + main.getName()
							+ " molarity"));
					double result = values[1] * values[2] / values[0];
					steps.add(new JLabel(main.getName() + " volume = " + values[1] + " * " + values[2] + " / " + values[0] + " = " + result + " L"));
					result = main.getVolume(result);
					return result;
				}
				
				private double calculateMolarity(EnterPanel main, EnterPanel other, double[] values)
				{
					steps.add(new JLabel(main.getName() + " molarity = " + other.getName() + " molarity * " + other.getName() + " volume / " + main.getName()
						+ " volume"));
					double result = values[1] * values[2] / values[0];
					steps.add(new JLabel(main.getName() + " molarity = " + values[1] + " * " + values[2] + " / " + values[0] + " = " + result + " mol / L"));
					result = main.getMolarity(result);
					return result;
				}
			});
		result = new JLabel();
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		subpanel.add(new JLabel("Enter all known information:"), c);
		c.gridy++;
		subpanel.add(acid, c);
		c.gridy++;
		subpanel.add(base, c);
		c.gridy++;
		subpanel.add(calculate, c);
		c.gridy++;
		subpanel.add(result, c);
		
		JPanel subpanel2 = new JPanel(new GridBagLayout());
		c.gridy = c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		subpanel2.add(subpanel, c);
		c.gridx++;
		subpanel2.add(Box.createHorizontalStrut(10), c);
		c.gridx++;
		steps = Box.createVerticalBox();
		subpanel2.add(steps);
		
		panel = new JPanel();
		panel.add(subpanel2);
	}
	
	private class EnterPanel extends JPanel
	{
		private EnterField molarity, volume;
		private String name;
		
		public EnterPanel(String name)
		{
			this.name = name;
			molarity = new EnterField("Molarity", "Amount", "Volume");
			volume = new EnterField("Volume", "Volume");
			add(new JLabel(name + ": "));
			add(molarity);
			add(Box.createHorizontalStrut(10));
			add(volume);
		}
		
		public String getName()
		{
			return name;
		}
		
		public double getMolarity()
		{
			double amount = molarity.getAmount();
			if(amount == Units.UNKNOWN_VALUE) steps.add(new JLabel(name + " molarity =  ?"));
			else steps.add(new JLabel(name + " molarity = " + amount + " mol / L"));
			return amount;
		}
		
		public double getVolume()
		{
			double amount = volume.getAmount();
			if(amount == Units.UNKNOWN_VALUE) steps.add(new JLabel(name + " volume =  ?"));
			else steps.add(new JLabel(name + " volume = " + amount + " L"));
			return amount;
		}
		
		public void setMolarity(double amount)
		{
			molarity.setAmount(amount);
		}
		
		public void setVolume(double amount)
		{
			volume.setAmount(amount);
		}
		
		public double getVolume(double before)
		{
			double after = volume.getBlankAmount(before);
			unit = volume.getUnitName();
			if(after != before) steps.add(new JLabel(before + " L = " + after + " " + unit));
			return after;
		}
		
		public double getMolarity(double before)
		{
			double after = molarity.getBlankAmount(before);
			unit = molarity.getUnitName() + " / " + molarity.getUnit2Name();
			if(after != before) steps.add(new JLabel(before + " mol / L = " + after + " " + unit));
			return after;
		}
		
		public int getSigFigs()
		{
			int sigFigs1 = volume.getSigFigs(), sigFigs2 = molarity.getSigFigs();
			return sigFigs1 == -1 ? sigFigs2 : sigFigs2 == -1 ? sigFigs1 : Math.min(sigFigs2, sigFigs1);
		}
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
		String option = (String)JOptionPane.showInputDialog(panel, "Choose where to use the number.", "Choose number", JOptionPane.QUESTION_MESSAGE, null,
				new String[]{"Acid- molarity", "Acid- volume", "Base- molarity", "Base- volume"}, "Acid- molarity");
		if(option == null) return;
		if(option.equals("Acid- molarity")) acid.setMolarity(num);
		else if(option.equals("Acid- volume")) acid.setVolume(num);
		else if(option.equals("Before- molarity")) base.setMolarity(num);
		else base.setVolume(num);
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}