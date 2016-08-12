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

/**
 * File: Neutralization.java
 * Package: Functions
 * Version: 08/12/2016
 * Author: Julia McClellan
 * -----------------------------------------------
 * Calculates the molarity or volume of the acidic or basic solution needed to neutralize another.
 */
public class Neutralization extends Function 
{
	private JPanel panel;
	private EnterPanel acid, base;
	private JLabel result;
	private JButton calculate;
	private Box steps;
	private double number;
	private String unit;
	
	/**
	 * Constructs the function.
	 */
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
				
				/**
				 * Calculates the missing volume.
				 * @param main The EnterPanel with the missing value.
				 * @param other The other EnterPanel.
				 * @param values An array containing the known values.
				 * @return The unknown volume.
				 */
				private double calculateVolume(EnterPanel main, EnterPanel other, double[] values)
				{
					steps.add(Function.latex("\\text{" + main.getName() + " volume} = \\frac{\\text{" + other.getName() + " molarity} * \\text{" + 
							other.getName() + " volume}}{\\text{" + main.getName() + " molarity}}"));
					double result = values[1] * values[2] / values[0];
					steps.add(Function.latex("\\text{" + main.getName() + " volume} = \\frac{" + values[1] + "\\frac{mol}{L} * " + values[2] + "L}{" 
							+ values[0] + "\\frac{mol}{L}} = " + result + " L"));
					result = main.getVolume(result);
					return result;
				}
				
				/**
				 * Calculates the missing molarity.
				 * @param main The EnterPanel with the missing value.
				 * @param other The other EnterPanel.
				 * @param values An array containing the known values.
				 * @return The unknown molarity.
				 */
				private double calculateMolarity(EnterPanel main, EnterPanel other, double[] values)
				{
					steps.add(Function.latex("\\text{" + main.getName() + " molarity} = \\frac{\\text{" + other.getName() + " molarity} * \\text{" + 
							other.getName() + " volume}}{\\text{" + main.getName() + " volume}}"));
					double result = values[1] * values[2] / values[0];
					steps.add(Function.latex("\\text{" + main.getName() + " molarity} = \\frac{" + values[1] + "\\frac{mol}{L} * " + values[2] + "L}{"
							+ values[0] + "L = " + result + "\\frac{mol}{L}"));
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
	
	/**
	 * Contains fields for molarity and volume of a solution.
	 */
	private class EnterPanel extends JPanel
	{
		private EnterField molarity, volume;
		private String name;
		
		/**
		 * Creates the panel for the given value.
		 * @param name The name of the solution.
		 */
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
		
		/**
		 * Returns the name of the solution used here.
		 * @return The name of the solution for this EnterPanel.
		 */
		public String getName()
		{
			return name;
		}
		
		/**
		 * Returns the value in the molarity field, or Units.UNKNOWN_VALUE if blank.
		 * @return The entered molarity.
		 */
		public double getMolarity()
		{
			double amount = molarity.getAmount();
			if(amount == Units.UNKNOWN_VALUE) steps.add(Function.latex("\\text{" + name + " molarity} =  ?"));
			else steps.add(Function.latex("\\text{" + name + " molarity} = " + amount + "\\frac{mol}{L}"));
			return amount;
		}
		
		/**
		 * Returns the value in the volume field, or Units.UNKNOWN_VALUE if blank.
		 * @return The entered volume.
		 */
		public double getVolume()
		{
			double amount = volume.getAmount();
			if(amount == Units.UNKNOWN_VALUE) steps.add(Function.latex("\\text{" + name + " volume} =  ?"));
			else steps.add(Function.latex("\\text{" + name + " volume} = " + amount + " L"));
			return amount;
		}
		
		/**
		 * Sets the value of the molarity enter field.
		 * @param amount The value to set.
		 */
		public void setMolarity(double amount)
		{
			molarity.setAmount(amount);
		}
		
		/**
		 * Sets the value of the volume enter field.
		 * @param amount The value to set.
		 */
		public void setVolume(double amount)
		{
			volume.setAmount(amount);
		}
		
		/**
		 * Converts the volume to the desired unit.
		 * @param before The volume in L
		 * @return The volume in the desired unit.
		 */
		public double getVolume(double before)
		{
			double after = volume.getBlankAmount(before);
			unit = volume.getUnitName();
			if(after != before) steps.add(Function.latex(before + " L = " + after + " " + unit));
			return after;
		}
		
		/**
		 * Converts the molarity to the desired unit.
		 * @param before The molarity in mol/L
		 * @return The molarity in the desired unit.
		 */
		public double getMolarity(double before)
		{
			double after = molarity.getBlankAmount(before);
			if(after != before) steps.add(Function.latex(before + " \\{mol}{L} = " + after + "\\frac{" + molarity.getUnitName() + "}{" + 
					molarity.getUnit2Name() + "}"));
			return after;
		}
		
		/**
		 * Returns the smallest number of sig figs in these fields.
		 * @return The sig fig number to use.
		 */
		public int getSigFigs()
		{
			int sigFigs1 = volume.getSigFigs(), sigFigs2 = molarity.getSigFigs();
			return sigFigs1 == -1 ? sigFigs2 : sigFigs2 == -1 ? sigFigs1 : Math.min(sigFigs2, sigFigs1);
		}
	}
	
	/**
	 * Returns true as this function can save numbers.
	 * @return true
	 */
	@Override
	public boolean number()
	{
		return true;
	}
	
	/**
	 * Saves the most recently calculated number.
	 * @return The most recently calculated number.
	 */
	@Override
	public double saveNumber()
	{
		return number;
	}
	
	/**
	 * Allows the user to select where to use the selected number.
	 * @param num The saved number to use.
	 */
	@Override
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
	
	/**
	 * Returns a string with instructions.
	 * @return The help message.
	 */
	@Override
	public String getHelp()
	{
		return "<html>Enter all known quantities from the acid-base<br>"
				+ "neutralization reaction and press calculate to<br>"
				+ "find the remaining value.</html>";
	}
	
	/**
	 * Returns the panel with components for this function.
	 * @return The panel for this function.
	 */
	@Override
	public JPanel getPanel()
	{
		return panel;
	}
}