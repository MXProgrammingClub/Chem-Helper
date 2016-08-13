package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import HelperClasses.EnterField;
import HelperClasses.Units;

/**
 * File: pHCalculator.java
 * Package: Functions
 * Version: 08/12/2016
 * Authors: Julia McClellan
 * -----------------------------------------------
 * Calculates the pH/pOH/concentration H+/concentration OH.
 */
public class pHCalculator extends Function
{
	private JPanel panel;
	private EnterField ph, poh, h, oh;
	private JButton calculate;
	private Box result, steps;
	private ArrayList<Double> nums;
	
	/**
	 * Constructs the function.
	 */
	public pHCalculator()
	{
		super("pH Calculator");
		
		h = new EnterField("<html>H<sup>+</sup></html>", "Amount", "Volume");
		oh = new EnterField("<html>OH<sup>-</sup></html>", "Amount", "Volume");
		ph = new EnterField("pH");
		poh = new EnterField("pOH");
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					steps.removeAll();
					steps.setVisible(false);
					result.removeAll();
					result.setVisible(false);
					if(!h.isEmpty())
					{
						double H = h.getAmount();
						if(H == Units.ERROR_VALUE)
						{
							result.add(new JLabel("<html>Invalid input for H<sub>+</sub></html>"));
							result.setVisible(true);
							return;
						}
						int sigFigs = h.getSigFigs();
						double pH = getP(false, H), pOH = betweenP(false, pH), OH = fromP(true, pOH), temp = oh.getBlankAmount(OH);
						if(temp != OH)
						{
							steps.add(Function.latex(OH + "\\frac{mol}{L} = " + temp + "\\frac{" + oh.getUnitName() + "}{" + oh.getUnit2Name() + "}"));
							OH = temp;
						}
						
						nums = new ArrayList<Double>();
						nums.add(pH);
						nums.add(pOH);
						nums.add(OH);
						
						result.add(new JLabel("pH = " + Function.withSigFigs(pH, sigFigs)));
						result.add(new JLabel("pOH = " + Function.withSigFigs(pOH, sigFigs)));
						result.add(new JLabel("<html>OH<sup>-</sup> = " + Function.withSigFigs(OH, sigFigs) + " " + oh.getUnitName() + " / " + 
								oh.getUnit2Name()));
					}
					else if(!oh.isEmpty())
					{
						double OH = oh.getAmount();
						if(OH == Units.ERROR_VALUE)
						{
							result.add(new JLabel("<html>Invalid input for OH<sub>-</sub></html>"));
							result.setVisible(true);
							return;
						}
						int sigFigs = oh.getSigFigs();
						double pOH = getP(true, OH), pH = betweenP(true, pOH), H = fromP(false, pH), temp = h.getBlankAmount(H);
						if(temp != H)
						{
							steps.add(Function.latex(H + "\\frac{mol}{L} = " + temp + "\\frac{" + h.getUnitName() + "}{" + h.getUnit2Name() + "}"));
							H = temp;
						}
						
						nums = new ArrayList<Double>();
						nums.add(pOH);
						nums.add(pH);
						nums.add(H);
						
						result.add(new JLabel("pOH = " + Function.withSigFigs(pOH, sigFigs)));
						result.add(new JLabel("pH = " + Function.withSigFigs(pH, sigFigs)));
						result.add(new JLabel("<html>H<sup>+</sup> = " + Function.withSigFigs(H, sigFigs) + " " + h.getUnitName() + " / " + 
								h.getUnit2Name()));
					}
					else if(!ph.isEmpty())
					{
						double pH = ph.getAmount();
						if(pH == Units.ERROR_VALUE)
						{
							result.add(new JLabel("Invalid input for pH"));
							result.setVisible(true);
							return;
						}
						int sigFigs = ph.getSigFigs();
						double H = fromP(false, pH), temp = h.getBlankAmount(H);
						if(temp != H)
						{
							steps.add(Function.latex(H + "\\frac{mol}{L} = " + temp + "\\frac{" + h.getUnitName() + "}{" + h.getUnit2Name() + "}"));
						}
						double pOH = betweenP(false, pH), OH = fromP(true, pOH), temp2 = oh.getBlankAmount(OH);
						if(temp2 != OH)
						{
							steps.add(Function.latex(OH + "\\frac{mol}{L} = " + temp2 + "\\frac{" + oh.getUnitName() + "}{" + oh.getUnit2Name() + "}"));
							OH = temp2;
						}
						
						nums = new ArrayList<Double>();
						nums.add(H);
						nums.add(pOH);
						nums.add(OH);
						
						result.add(new JLabel("<html>H<sup>+</sup> = " + Function.withSigFigs(H, sigFigs) + " " + h.getUnitName() + " / " + 
								h.getUnit2Name()));
						result.add(new JLabel("pOH = " + Function.withSigFigs(pOH, sigFigs)));
						result.add(new JLabel("<html>OH<sup>-</sup> = " + Function.withSigFigs(OH, sigFigs) + " " + oh.getUnitName() + " / " + 
								oh.getUnit2Name()));
					}
					else if(!poh.isEmpty())
					{
						double pOH = poh.getAmount();
						if(pOH == Units.ERROR_VALUE)
						{
							result.add(new JLabel("Invalid input for pOH"));
							result.setVisible(true);
							return;
						}
						int sigFigs = poh.getSigFigs();
						double OH = fromP(true, pOH), temp = oh.getBlankAmount(OH);
						if(temp != OH)
						{
							steps.add(Function.latex(OH + "\\frac{mol}{L} = " + temp + "\\frac{" + oh.getUnitName() + "}{" + oh.getUnit2Name() + "}"));
							OH = temp;
						}
						double pH = betweenP(true, pOH), H = fromP(false, pH), temp2 = h.getBlankAmount(H);
						if(temp2 != H)
						{
							steps.add(Function.latex(H + "\\frac{mol}{L} = " + temp2 + "\\frac{" + h.getUnitName() + "}{" + h.getUnit2Name() + "}"));
						}
						
						nums = new ArrayList<Double>();
						nums.add(OH);
						nums.add(pH);
						nums.add(H);
						
						result.add(new JLabel("<html>OH<sup>-</sup> = " + Function.withSigFigs(OH, sigFigs) + " " + oh.getUnitName() + " / " + 
								oh.getUnit2Name()));
						result.add(new JLabel("pOH = " + Function.withSigFigs(pOH, sigFigs)));
						result.add(new JLabel("<html>H<sup>+</sup> = " + Function.withSigFigs(H, sigFigs) + " " + h.getUnitName() + " / " + 
								h.getUnit2Name()));
					}
					else
					{
						result.add(new JLabel("Do not leave all values blank."));
						result.setVisible(true);
						return;
					}
					steps.setVisible(true);
					result.setVisible(true);
				}
				
				/**
				 * Calculates either the pH or pOH.
				 * @param o Whether it is pOH or pH.
				 * @param amount The concentration the calculation is based on.
				 * @return The pH or pOH calculated.
				 */
				public double getP(boolean o, double amount)
				{
					double answer = -Math.log10(amount);
					steps.add(Function.latex("\\text{p" + (o ? "O" : "") + "H} = -log(" + amount + ") = " + answer));
					return answer;
				}
				
				/**
				 * Converts from pH or pOH to the other.
				 * @param o Whether the original is pOH or pH.
				 * @param amount The known value, either pH or pOH.
				 * @return The pH or pOH calculated.
				 */
				public double betweenP(boolean o, double amount)
				{
					double answer = 14 - amount;
					steps.add(Function.latex("\\text{p" + (o ? "" : "O") + "h} = 14 - " + amount + " = " + answer));
					return answer;
				}
				
				/**
				 * Calculates the concentration of OH- or H+.
				 * @param o Whether it is OH- or H+.
				 * @param amount The pH or pOH.
				 * @return The calculated concentration.
				 */
				public double fromP(boolean o, double amount)
				{
					double answer = Math.pow(10, -amount);
					steps.add(Function.latex((o ? "\\text{OH}^{-}" : "\\text{H}^{+}") + " = 10^{-" + amount + "} = " + answer + "\\frac{mol}{L}"));
					return answer;
				}
			});
		result = Box.createVerticalBox();
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		subpanel.add(new JLabel("Enter known quantity:"), c);
		c.gridy++;
		subpanel.add(h, c);
		c.gridy++;
		subpanel.add(oh, c);
		c.gridy++;
		subpanel.add(ph, c);
		c.gridy++;
		subpanel.add(poh, c);
		c.gridy ++;
		subpanel.add(calculate, c);
		c.gridy++;
		subpanel.add(Box.createVerticalStrut(5), c);
		c.gridy++;
		subpanel.add(result, c);
		
		JPanel subpanel2 = new JPanel(new GridBagLayout());
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		subpanel2.add(subpanel, c);
		steps = Box.createVerticalBox();
		c.gridx++;
		subpanel2.add(Box.createHorizontalStrut(10), c);
		c.gridx++;
		subpanel2.add(steps, c);
		
		panel = new JPanel();
		panel.add(subpanel2);
	}
	
	/**
	 * Returns true as numbers can be saved for this function.
	 * @return true
	 */
	@Override
	public boolean number()
	{
		return true;
	}
	
	/**
	 * Has the user select a recently calculated number to save.
	 * @return The selcted number to save.
	 */
	@Override
	public double saveNumber()
	{
		if(nums == null || nums.size() == 0) return 0;
		Object choice = JOptionPane.showInputDialog(panel, "Choose which number to save.", "Choose Number", JOptionPane.QUESTION_MESSAGE, null, 
				nums.toArray(), nums.get(0));
		if(choice == null) return 0;
		return (Double)choice;
	}
	
	/**
	 * Has the user choose where to use the saved number.
	 * @param num The saved number to use.
	 */
	@Override
	public void useSavedNumber(double num)
	{
		Object option = JOptionPane.showInputDialog(panel, "Choose where to use the number.", "Choose Number", JOptionPane.QUESTION_MESSAGE, null, 
				new String[]{"<html>H<sup>+</sup></html>", "<html>OH<sup>-</sup></html>", "pH", "pOH"}, "<html>H<sup>+</sup></html>");
		if(option == null) return;
		switch((String)option)
		{
			case "<html>H<sup>+</sup></html>":
				h.setAmount(num);
				break;
			case "<html>OH<sup>-</sup></html>":
				oh.setAmount(num);
				break;
			case "pH":
				ph.setAmount(num);
				break;
			default:
				poh.setAmount(num);
		}
	}
	
	/**
	 * Returns a help message for this function.
	 * @return The help string.
	 */
	@Override
	public String getHelp()
	{
		return "<html>Enter the known value for the acidic or basic<br>"
				+ "solution: pH, poH, H<sup>+</sup>, or OH<sup>-</sup> and ChemHelper<br>"
				+ "will calculate the rest.</html>";
	}
	
	/**
	 * Returns the panel with components for this function.
	 * @return The function's panel.
	 */
	@Override
	public JPanel getPanel()
	{
		return panel;
	}
}