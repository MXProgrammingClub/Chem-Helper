/*
 * Performs various calculations for functions at equilibrium.
 * 
 * Author: Julia McClellan
 * Version: 3/27/2016
 */

package Functions;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import Equation.Compound;
import Equation.Equation;
import HelperClasses.EnterField;
import HelperClasses.Units;

public class Equilibrium extends Function
{
	public static TreeMap<String, Double> KSP = createMap();
	private JPanel panel, enterPanel, fields;
	private EquationReader reader;
	private JLabel expression;
	private EnterField[] compounds, precipitate;
	private EnterField k, solubility;
	private JButton calculate, reset;
	private Box steps, results;
	private JRadioButton before;
	private ArrayList<Double> saved;
	private ArrayList<Compound> relevant;
	private ArrayList<Integer> powers;
	private Compound[] reactants;
	
	public Equilibrium()
	{
		super("Equilibrium");
		saved = new ArrayList<Double>();
		reader = new EquationReader(this);
		
		enterPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		
		expression = new JLabel();
		enterPanel.add(expression, c);
		c.gridy++;
		
		before = new JRadioButton("Before reaction");
		JRadioButton after = new JRadioButton("At equilibrium", true);
		ButtonGroup g = new ButtonGroup();
		g.add(before);
		g.add(after);
		JPanel buttons = new JPanel();
		buttons.add(after);
		buttons.add(before);
		enterPanel.add(buttons, c);
		c.gridy++;
		
		fields = new JPanel(new GridBagLayout());
		enterPanel.add(fields, c);
		c.gridy++;
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					results.removeAll();
					results.setVisible(false);
					steps.removeAll();
					steps.setVisible(false);
					saved = new ArrayList<Double>();
					
					double[] values = new double[compounds.length + 1];
					int sigFigs = Integer.MAX_VALUE;
					for(int index = 0; index < compounds.length; index++)
					{
						values[index] = compounds[index].getAmount();
						String name = compounds[index].getName();
						name = name.substring(6, name.length() - 7); //Getting rid of the html tags
						if(values[index] == Units.ERROR_VALUE)
						{
							results.add(new JLabel("<html>Error in value for " + name + "</html>"));
							results.setVisible(true);
							return;
						}
						else if(values[index] == Units.UNKNOWN_VALUE) steps.add(new JLabel("<html> " + name + " = ? M</html>"));
						else
						{
							steps.add(new JLabel("<html> " + name + " = " + values[index] + " M</html>"));
							sigFigs = Math.min(sigFigs, compounds[index].getSigFigs());
						}
					}
					values[compounds.length] = k.getAmount();
					if(values[compounds.length] == Units.ERROR_VALUE)
					{
						results.add(new JLabel("Error in value for K"));
						results.setVisible(true);
						return;
					}
					else if(values[compounds.length] == Units.UNKNOWN_VALUE) steps.add(new JLabel("K = ?"));
					else
					{
						steps.add(new JLabel("K = " + values[compounds.length]));
						sigFigs = Math.min(sigFigs, k.getSigFigs());
					}
					
					double[] extra = new double[6];
					if(precipitate != null)
					{
						for(int index = 0; index < precipitate.length; index++)
						{
							extra[index] = precipitate[index].getAmount();
							if(extra[index] == Units.ERROR_VALUE)
							{
								results.add(new JLabel("<html>Error in value for " + precipitate[index].getName().substring(7)));
								results.setVisible(true);
								return;
							}
							else if(extra[index] != Units.UNKNOWN_VALUE)
							{
								steps.add(new JLabel(precipitate[index].getName() + " = " + extra[index] + (index > 0 ? "L" : "")));
								sigFigs = Math.min(sigFigs, precipitate[index].getSigFigs());
							}
							else steps.add(new JLabel(precipitate[index].getName() + " = ?"));
						}
					}
					if(solubility != null)
					{
						extra[5] = solubility.getAmount();
						if(extra[5] == Units.ERROR_VALUE)
						{
							results.add(new JLabel("Error in value for solubility."));
							results.setVisible(true);
							return;
						}
						else if(extra[5] != Units.UNKNOWN_VALUE)
						{
							steps.add(new JLabel("Solubility = " + extra[5] + " mol / L"));
							sigFigs = Math.min(sigFigs, solubility.getSigFigs());
						}
						else steps.add(new JLabel("Solubility = ?"));
					}
					
					steps.add(Box.createVerticalStrut(5));
					steps.add(new JLabel(expression.getIcon()));
					
					if(precipitate != null)
					{
						if(values[0] == Units.UNKNOWN_VALUE) //Fist calculates concentrations of each ion if necessary
						{
							LinkedList<String> stepList = new LinkedList<String>();
							double[] concentrations = calculateConcentrations(extra, relevant, reactants, stepList);
							for(String step: stepList) steps.add(new JLabel(step));
							for(int index = 0; index < concentrations.length; index++)
							{
								values[index] = compounds[index].getBlankAmount(concentrations[index]);
								saved.add(values[index]);
								results.add(new JLabel("<html>" + relevant.get(index).withoutNumState() + " = " + Function.withSigFigs(values[index], sigFigs)
									+ " " + compounds[index].getUnitName() + " / " + compounds[index].getUnit2Name()));
							}
						}
						else if(values[1] == Units.UNKNOWN_VALUE)
						{
							results.add(new JLabel("Insufficient information to calculate."));
							results.setVisible(true);
							return;
						}
						
						//Finds Qsp and compares it to Ksp to find if there is a precipitate
						LinkedList<String> stepList = new LinkedList<String>();
						double q = calculateK(values, stepList, powers, false);
						for(String step: stepList) steps.add(new JLabel(step));
						results.add(new JLabel("Q = " + Function.withSigFigs(q, sigFigs)));
						steps.add(new JLabel(q + (q > values[values.length - 1] ? " > " : " < ") + values[values.length - 1]));
						steps.add(new JLabel("Q" + (q > values[values.length - 1] ? " > " : " < ") + "K"));
						if(q > values[values.length - 1]) results.add(new JLabel("Yes, there is a precipitate."));
						else results.add(new JLabel("No, there is not a precipitate."));
					}
					else if(before.isSelected())
					{
						if(values[compounds.length] == Units.UNKNOWN_VALUE) //K is unknown
						{
							results.add(new JLabel("Insufficient information- K needs to be specified if reaction is not at equilibrium."));
							results.setVisible(true);
							return;
						}
						else //K is known
						{
							//Creates the ICE table
							JLabel[][] labels = new JLabel[4][values.length];
							labels[0][0] = new JLabel();
							labels[1][0] = new JLabel("Initial");
							labels[2][0] = new JLabel("Change");
							labels[3][0] = new JLabel("Equilibrium");
							
							String equation = "<html>" + values[values.length - 1] + " = ";
							String[] expressions = new String[values.length - 1];
							int col = 1;
							for(int index = 0; index < relevant.size(); index++)
							{
								if(values[index] == Units.UNKNOWN_VALUE) values[index] = 0;
								labels[0][col] = new JLabel("<html>[" + relevant.get(index).withoutNumState() + "]");
								labels[1][col] = new JLabel(values[index] + "");
								labels[2][col] = new JLabel((powers.get(index) == 1 ? "" : powers.get(index)) + "x");
								labels[3][col] = new JLabel((values[index] == 0 ? "" : values[index] + " + ") + (powers.get(index) == 1 ? "" : 
									(powers.get(index) == -1 ? "-" : powers.get(index))) + "x");
								expressions[index] = labels[0][col].getText() + " = " + labels[3][col].getText();
								equation += "(" + labels[3][col].getText() + ")<sup>" + powers.get(index) + "</sup> * ";
								col++;
							}
							JPanel table = new JPanel(new GridLayout(4, values.length));
							for(JLabel[] row: labels)
							{
								for(JLabel label: row)
								{
									label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
									table.add(label);
								}
							}
							steps.add(table);
							steps.add(new JLabel(equation.substring(0, equation.length() - 3) + "</html>"));
							
							//If powers are too high, solves approximately
							int sum = 0, index = 0;
							double product = 1;
							boolean zero = true;
							for(; index < powers.size() && powers.get(index) > 0; sum += powers.get(index), index++)
							{
								if(values[index] != 0) zero = false;
								else product *= Math.pow(powers.get(index), powers.get(index));
							}
							if(sum > 2) //and thus can't be solved as a quadratic
							{
								if(zero)
								{
									LinkedList<String> stepList = new LinkedList<String>();
									double[] concentrations = solveApprox(values, product, sum, index, powers, stepList, expressions);
									for(String step: stepList) steps.add(new JLabel(step));
									for(int i = 0; i < concentrations.length; i++)
									{
										double val = compounds[i].getBlankAmount(concentrations[i]);
										saved.add(val);
										results.add(new JLabel("<html>[" + relevant.get(i).withoutNumState() + "] = " + Function.withSigFigs(val, sigFigs)));
									}
								}
								else 
								{
									results.add(new JLabel("ChemHelper cannot solve this equation."));
								}
							}
							else
							{
								LinkedList<String> stepList = new LinkedList<String>();
								double[] concentrations = solveICE(values, powers, stepList, expressions);
								for(String step: stepList) steps.add(new JLabel(step));
								for(int i = 0; i < concentrations.length; i++)
								{
									double val = compounds[i].getBlankAmount(concentrations[i]);
									saved.add(val);
									results.add(new JLabel("<html>[" + relevant.get(i).withoutNumState() + "] = " + Function.withSigFigs(val, sigFigs)));
								}
							}
							
							//Things were aligning really weirdly
							for(Component c: steps.getComponents()) ((JComponent)c).setAlignmentX(Component.LEFT_ALIGNMENT);
						}
					}
					else //if after is selected
					{
						if(solubility != null && extra[5] != Units.UNKNOWN_VALUE)
						{
							LinkedList<String> stepList = new LinkedList<String>();
							values = calculateFromSolubility(extra[5], stepList, relevant, powers);
							for(String step: stepList) steps.add(new JLabel(step));
							
							for(int index = 0; index < values.length - 1; index++)
							{
								values[index] = compounds[index].getBlankAmount(values[index]);
								saved.add(values[index]);
								results.add(new JLabel("<html>["+ relevant.get(index).withoutNumState() + "] = " + Function.withSigFigs(values[index], sigFigs)
								+ " " + compounds[index].getUnitName() + " / " + compounds[index].getUnit2Name()));
							}
							saved.add(values[values.length - 1]);
							results.add(new JLabel("K = " + Function.withSigFigs(values[values.length - 1], sigFigs)));
						}
						else if(values[compounds.length] == Units.UNKNOWN_VALUE) //K is unknown
						{
							LinkedList<String> stepList = new LinkedList<String>();
							double result = calculateK(values, stepList, powers, true);
							
							for(String step: stepList) steps.add(new JLabel(step));
							saved.add(result);
							results.add(new JLabel("K = " + Function.withSigFigs(result, sigFigs)));
						}
						else //K is known
						{
							int unknown = -1;
							for(int index = 0; index < compounds.length; index++)
							{
								if(values[index] == Units.UNKNOWN_VALUE)
								{
									if(unknown == -1) unknown = index;
									else
									{
										unknown = Integer.MAX_VALUE;
										break;
									}
								}
							}
							
							if(unknown == -1)
							{
								results.add(new JLabel("Leave a value blank."));
								results.setVisible(true);
								return;
							}
							else if(unknown == Integer.MAX_VALUE)
							{
								LinkedList<String> stepList = new LinkedList<String>();
								double[] x = new double[1];
								LinkedList<Integer> changed = calculateValues(values, stepList, powers, relevant, x);
								if(changed == null)
								{
									results.add(new JLabel(stepList.getLast()));
									results.setVisible(false);
									return;
								}
								for(String step: stepList) steps.add(new JLabel(step));
								
								for(Integer index: changed)
								{
									double val = compounds[index].getBlankAmount(values[index]);
									saved.add(val);
									results.add(new JLabel("<html>[" + relevant.get(index).withoutNumState() + "] = " + Function.withSigFigs(val, sigFigs)
											+ " " + compounds[index].getUnitName() + " / " + compounds[index].getUnit2Name()));
								}
								
								if(solubility != null)
								{
									double result = solubility.getBlankAmount(x[0]);
									String unit = solubility.getUnitName() + " / " + solubility.getUnit2Name();
									steps.add(new JLabel("Solubility = x = " + result + " " + unit));
									saved.add(result);
									results.add(new JLabel("Solubility = " + Function.withSigFigs(result, sigFigs) + " " + unit));
									extra[5] = result; //So it won't accidentally be recalculated later
								}
							}
							else
							{
								LinkedList<String> stepList = new LinkedList<String>();
								double result = calculateConcentration(values, stepList, powers, unknown);
								
								for(String step: stepList) steps.add(new JLabel(step));
								saved.add(compounds[unknown].getBlankAmount(result));
								if(result != saved.get(0)) steps.add(new JLabel("x = " + saved.get(0) + " " + compounds[unknown].getUnitName() + " / "  
										+ compounds[unknown].getUnit2Name()));
								results.add(new JLabel("<html>[" + relevant.get(unknown).withoutNumState() + "] = " + 
										Function.withSigFigs(saved.get(0), sigFigs)));
							}
						}
					}
					
					if(solubility != null && extra[5] == Units.UNKNOWN_VALUE)
					{
						steps.add(new JLabel("Solubility * " + relevant.get(0).getNum() + " = " + compounds[0].getName().substring(6)));
						double s = values[0] / relevant.get(0).getNum();
						steps.add(new JLabel("Solubility = " + values[0] + " / " + relevant.get(0).getNum() + " = " + s + " mol / L"));
						s = solubility.getBlankAmount(s);
						saved.add(s);
						results.add(new JLabel("Solubility = " + s + " " + solubility.getUnitName() + " / " + solubility.getUnit2Name()));
					}
					
					steps.setVisible(true);
					results.setVisible(true);
				}
			});
		
		reset = new JButton("Reset");
		reset.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					setPanel();
				}
			});
		JPanel buttons2 = new JPanel();
		buttons2.add(calculate);
		buttons2.add(reset);
		enterPanel.add(buttons2, c);
		
		c.gridy++;
		results = Box.createVerticalBox();
		enterPanel.add(results, c);
		enterPanel.setVisible(false);
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		subpanel.add(enterPanel, c);
		c.gridx++;
		subpanel.add(Box.createHorizontalStrut(10), c);
		c.gridx++;
		steps = Box.createVerticalBox();
		subpanel.add(steps);
		
		panel = new JPanel();
		panel.add(reader.getPanel());
		panel.add(subpanel);
	}
	
	/*
	 * Returns the panel to its original state after the rest button has been pressed.
	 */
	private void setPanel()
	{
		expression.setIcon(null);
		fields.removeAll();
		compounds = null;
		k = null;
		precipitate = null;
		solubility = null;
		results.removeAll();
		steps.removeAll();
		enterPanel.setVisible(false);
		panel.add(reader.getPanel());
	}
	
	/*
	 * Calculates the value of K given the concentrations of the relevant compounds.
	 */
	private static double calculateK(double[] values, LinkedList<String> steps, ArrayList<Integer> powers, boolean k)
	{
		double value = 1;
		String step1 = "<html>" + (k ? "K" : "Q") + " = ", step2 = "<html>" + (k ? "K" : "Q") + " = "; //Step 1 shows multiplication before raising to powers, 
			//step 2 shows after
		for(int index = 0; index < values.length - 1; index++)
		{
			step1 += "(" + values[index] + ")<sup>" + powers.get(index) + "</sup> * ";
			double num = Math.pow(values[index], powers.get(index));
			step2 += num + " * ";
			value *= num;
		}
		step1 = step1.substring(0, step1.length() - 3);
		step2 = step2.substring(0, step2.length() - 3);
		steps.add(step1);
		steps.add(step2);
		steps.add((k ? "K" : "Q") + " = " + value);
		return value;
	}
	
	/*
	 * Calculates one concentration given all the others and K.
	 */
	public static double calculateConcentration(double[] values, LinkedList<String> steps, ArrayList<Integer> powers, int unknown)
	{
		double value = 1;
		String step1 = "<html>" + values[values.length - 1] + " = ", step2 = "<html>" + values[values.length - 1] + " = ";
		for(int index = 0; index < values.length - 1; index++)
		{
			if(index != unknown)
			{
				step1 += "(" + values[index] + ")<sup>" + powers.get(index) + "</sup> * ";
				double num = Math.pow(values[index], powers.get(index));
				step2 += num + " * ";
				value *= num;
			}
			else
			{
				step1 += "x<sup>" + powers.get(index) + "</sup> * ";
				step2 += "x<sup>" + powers.get(index) + "</sup> * ";
			}
		}
		step1 = step1.substring(0, step1.length() - 3);
		step2 = step2.substring(0, step2.length() - 3);
		steps.add(step1);
		steps.add(step2);
		value = values[values.length - 1] / value;
		steps.add("<html>x<sup>" + powers.get(unknown) + "</sup> = " + value);
		value = Math.pow(value, 1 / powers.get(unknown));
		steps.add("x = " + value + " mol / L");
		return value;
	}
	
	/*
	 * Calculates all unknown concentrations given the others and K and returns the indices in values which have been changed, or null if there is insufficient
	 * information for the calculations.
	 */
	public static LinkedList<Integer> calculateValues(double[] values, LinkedList<String> steps, ArrayList<Integer> powers, ArrayList<Compound> compounds,
			double[] storeX)
	{
		double newK = values[values.length - 1]; //Will divide K by all known concentrations
		String stepK = "<html>" + newK + " / ";
		LinkedList<Integer> changed = new LinkedList<Integer>(); //To put in relevant indices
		for(int index = 0; index < compounds.size(); index++)
		{
			if(values[index] == Units.UNKNOWN_VALUE)
			{
				if(powers.get(index) < 0) //If K is dependent on compounds on the left, the ratios can't be used for concentrations
				{
					steps.add("Insufficient information for calculations.");
					return null;
				}
				else
				{
					steps.add("<html>[" + compounds.get(index).withoutNumState() + "] = " + (powers.get(index) == 1 ? "" : powers.get(index)) + "x");
					changed.add(index);
				}
			}
			else
			{
				newK /= Math.pow(values[index], powers.get(index));
				stepK += "(" + values[index] + ")<sup>" + powers.get(index) + "</sup> / ";
			}
		}
		if(newK != values[values.length - 1])
		{
			steps.add("Divide K by known concentrations:");
			steps.add(stepK.substring(0, stepK.length() - 3) + " = " + newK); //Gets rid of last / before adding equals
		}
		
		String step = "<html>" + newK + " = ";
		int value = 1, sum = 0;
		for(int index = 0; index < values.length - 1; index++)
		{
			int power = powers.get(index);
			value *= Math.pow(power, power);
			sum += power;
			step += "(" + (power == 1 ? "" : power) + "x)<sup>" + power + "</sup> * ";
		}
		steps.add(step.substring(0, step.length() - 3)); //Removes last *
		if(value != 1)
		{
			steps.add("<html>" + newK + " = " + (value == 1 ? "" : value + " * ") + "x<sup>" + sum + "</sup><html>");
			newK /= value;
		}
		steps.add("<html>" + newK + " = x<sup>" + sum + "</sup><html>");
		
		double x = Math.pow(newK, (1 / (double)sum));
		steps.add("x = " + x);
		storeX[0] = x;
		
		for(Integer index: changed)
		{
			int power = powers.get(index);
			values[index] = x * power;
			steps.add("<html>[" + compounds.get(index).withoutNumState() + "] = " + (power == 1 ? "" : power) + "x = " + values[index] + " mol / L</html>");
		}
		
		return changed;
	}
	
	/*
	 * Solves the ICE table approximately by assuming that in the denominator x does not matter.
	 */
	public static double[] solveApprox(double[] original, double product, int sum, int index, ArrayList<Integer> powers, LinkedList<String> steps, String[] ex)
	{
		double denom = 1;
		String fraction = "<html>" + original[original.length - 1] + " = " + product + "x<sup>" + sum + "</sup> / (";
		for(; index < powers.size(); index++)
		{
			steps.add("Assume " + original[index] + " - " + (powers.get(index) == -1 ? "" : -powers.get(index)) + "x \u2245 " + original[index]);
			fraction += original[index] + "<sup>" + -powers.get(index) + "</sup> * ";
			denom *= Math.pow(original[index], -powers.get(index));
		}
		steps.add(fraction.substring(0, fraction.length() - 3) + ")</html>");
		double x = original[original.length - 1] * denom / product;
		steps.add("<html>" + x + " = x<sup>" + sum + "</sup></html>");
		x = Math.pow(x, 1.0 / sum);
		steps.add("x = " + x);
		
		double[] results = new double[original.length - 1];
		for(int i = 0; i < powers.size(); i++)
		{
			results[i] = original[i] + powers.get(i) * x;
			steps.add(ex[i] + " = " + results[i] + " M");
		}
		return results;
	}
	
	/*
	 * Solves the ICE table with quadratic equations. Returns null if it cannot be solved.
	 */
	public static double[] solveICE(double[] original, ArrayList<Integer> powers, LinkedList<String> steps, String[] expressions)
	{
		double[][] num = new double[2][2], denom = new double[2][2]; //Will hold the linear expressions to be multiplied
		int index = 0;
		num[0][0] = powers.get(index);
		num[0][1] = original[index];
		if(powers.get(index) == 2)
		{
			num[1][0] = powers.get(index);
			num[1][1] = original[index];
			index++;
		}
		else
		{
			index++;
			num[1][0] = powers.get(index);
			num[1][1] = original[index];
			index++;
		}
		
		denom[0][0] = powers.get(index);
		denom[0][1] = original[index];
		if(powers.get(index) == 2)
		{
			denom[1][0] = powers.get(index);
			denom[1][1] = original[index];
			index++;
		}
		else
		{
			index++;
			denom[1][0] = powers.get(index);
			denom[1][1] = original[index];
			index++;
		}
		
		double[] top = foil(num), bottom = foil(denom);
		steps.add("<html>" + original[original.length - 1] + " = (" + displayQuadratic(top) + ") / (" + displayQuadratic(bottom) + ")</html>");
		
		for(int i = 0; i < bottom.length; i++)
		{
			bottom[i] *= original[original.length - 1];
		}
		steps.add("<html>" + displayQuadratic(bottom) + " = " + displayQuadratic(top) + "</html>");
		
		for(int i = 0; i < bottom.length; i++)
		{
			top[i] -= bottom[i];
		}
		steps.add("<html>0 = " + displayQuadratic(top) + "</html>");
		
		double x = (-top[1] + Math.sqrt(top[1] * top[1] - 4 * top[0] * top[2])) / (2 * top[0]); //Uses higher root only
		steps.add("x = " + x);
		
		double[] results = new double[original.length - 1];
		for(int i = 0; i < powers.size(); i++)
		{
			results[i] = original[i] + powers.get(i) * x;
			steps.add(expressions[i] + " = " + results[i] + " M");
		}
		return results;
	}
	
	private static double[] foil(double[][] factors)
	{
		double[] expression = new double[3];
		expression[0] = factors[0][0] * factors[1][0];
		expression[1] = factors[0][0] * factors[1][1] + factors[1][0] * factors[0][1];
		expression[2] = factors[0][1] * factors[1][1];
		return expression;
	}
	
	private static String displayQuadratic(double[] expression)
	{
		String str = "";
		if(expression[0] == -1) str += "-";
		else if(expression[0] != 1 && expression[0] != 0) str += expression[0];
		if(expression[0] != 0) str += "x<sup>2</sup>";
		
		if(expression[1] != 0)
		{
			if(str.length() != 0) str += expression[1] > 0 ? " + " : " - ";
			if(expression[1] != 1 && expression[1] != -1) str += Math.abs(expression[1]);
			str += "x";
		}
		
		if(expression[2] != 0)
		{
			if(str.length() != 0) str += expression[2] > 0 ? " + " : " - ";
			if(expression[2] != 1 && expression[2] != -1) str += Math.abs(expression[2]);
		}
		
		return str;
	}
	
	/*
	 * Calculates the concentrations of each compound and K from the solubility of the solid.
	 */
	public static double[] calculateFromSolubility(double s, LinkedList<String> steps, ArrayList<Compound> relevant, ArrayList<Integer> powers)
	{
		double[] values = new double[relevant.size() + 1];
		String step1 = "<html>k = ", step2 = "<html>k = "; //Calculates k as it goes
		values[values.length - 1] = 1; 
		for(int index = 0; index < relevant.size(); index++)
		{
			String compound = "[" + relevant.get(index).withoutNumState() + "]";
			int power = powers.get(index);
			values[index] = s * power;
			steps.add("<html>" + compound + " = " + s + " * " + power + " = " + values[index] + " mol / L</html>");
			step1 += compound + "<sup>" + power + "</sup> * ";
			step2 += "(" + values[index] + ")<sup>" + power + "</sup> * ";
			values[values.length - 1] *= Math.pow(values[index], power);
		}
		steps.add(step1.substring(0, step1.length() - 3) + "<html>");
		steps.add(step2.substring(0, step2.length() - 3) + "<html>");
		steps.add("k = " + values[values.length - 1]);
		return values;
	}
	
	public static double[] calculateConcentrations(double[] values, ArrayList<Compound> compounds, Compound[] original, LinkedList<String> steps)
	{
		double[] concentrations = new double[2];
		int[] coefficients = new int[2]; //Finds the coefficients of each ion in the reactants
		coefficients[0] = original[0].numberOf((compounds.get(0).getIons()[0].getElements()[0].getElement()));
		if(coefficients[0] == 0) 
		{
			coefficients[0] = original[1].numberOf((compounds.get(0).getIons()[0].getElements()[0].getElement()));
			coefficients[1] = original[0].numberOf((compounds.get(1).getIons()[0].getElements()[0].getElement()));
			//The values will be in the wrong order for the calculations
			double temp = values[1];
			values[1] = values[3];
			values[3] = temp;
			temp = values[2];
			values[2] = values[4];
			values[4] = temp;
		}
		else coefficients[1] = original[1].numberOf((compounds.get(1).getIons()[0].getElements()[0].getElement()));
		
		steps.add("<html>[" + compounds.get(0).withoutNumState() + "] = " + values[1] + " * " + values[2] + " * " + coefficients[0] + " / (" + values[1] +
				" + " + values[3] + ")</html>");
		concentrations[0] = values[1] * values[2] * coefficients[0] / (values[1] + values[3]);
		steps.add("<html>[" + compounds.get(0).withoutNumState() + "] = " + concentrations[0] + " mol / L</html>)");
		
		steps.add("<html>[" + compounds.get(1).withoutNumState() + "] = " + values[3] + " * " + values[4] + " * " + coefficients[1] + " / (" + values[1] +
				" + " + values[3] + ")</html>");
		concentrations[1] = values[3] * values[4] * coefficients[1] / (values[1] + values[3]);
		steps.add("<html>[" + compounds.get(1).withoutNumState() + "] = " + concentrations[1] + " mol / L</html>)");
		return concentrations;
	}
	
	public boolean equation()
	{
		return true;
	}
	
	public Equation saveEquation()
	{
		return reader.saveEquation();
	}
	
	/*
	 * Takes equation and sets up EnterFields to perform calculations with the compounds in the equation.
	 */
	public void useSaved(Equation equation)
	{
		//States of matter are necessary to make an equilibrium expression
		{
			for(Compound c: equation.getLeft())
			{
				c.checkForPoly();
				if(c.getState().equals(" ") && !getState(c)) return;
			}
			for(Compound c: equation.getRight())
			{
				c.checkForPoly();
				if(c.getState().equals(" ") && !getState(c)) return;
			}
		}
		
		int type = equation.isDoubleDisplacement();
		reactants = new Compound[2];
		if(type == 1)
		{
			ArrayList<Compound> left = equation.getLeft();
			reactants[0] = left.get(0);
			reactants[1] = left.get(1);
			equation.removeSpectators();
		}
		
		panel.remove(reader.getPanel());
		relevant = new ArrayList<Compound>();
		ArrayList<Compound> irrelevant = new ArrayList<Compound>();
		powers = new ArrayList<Integer>();
		String ex = equation.getEquilibrium(relevant, irrelevant, powers);
		
		TeXFormula formula = new TeXFormula(ex);
		TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(new JLabel(), image.getGraphics(), 0, 0);
		expression.setIcon(icon);
		
		compounds = new EnterField[relevant.size()];
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		for(int index = 0; index < compounds.length; index++)
		{
			EnterField field = new EnterField("<html>[" + relevant.get(index).withoutNumState() + "]</html>", "Amount", "Volume");
			compounds[index] = field;
			fields.add(field, c);
			c.gridy++;
		}
		k = new EnterField("K");
		fields.add(k, c);
		
		if(type != -1)
		{
			Double kValue = KSP.get(irrelevant.get(0).withoutNumState());
			if(kValue != null)
			{
				JCheckBox stored = new JCheckBox("Use stored value for K");
				stored.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent arg0)
						{
							if(stored.isSelected()) k.setAmount(kValue);
						}
					});
				c.gridy++;
				fields.add(stored, c);
			}
			
			int pre = 0;
			if(type == 1) 
			{
				pre = JOptionPane.showConfirmDialog(panel, "Does the question involve finding whether there is a precipitate?", "Choose Type",
						JOptionPane.YES_NO_OPTION);
			}
			if(type == 0 || pre != 0)
			{
				solubility = new EnterField("Solubility", "Amount", "Volume");
				c.gridy++;
				fields.add(solubility, c);
			}
			else
			{
				precipitate = new EnterField[5];
				precipitate[0] = new EnterField("Q");
				c.gridy++;
				fields.add(precipitate[0], c);
				for(int index = 1; index < precipitate.length; index += 2)
				{
					precipitate[index] = new EnterField("<html>Volume " + reactants[index / 2].withoutNumState(), "Volume");
					c.gridy++;
					fields.add(precipitate[index], c);
					precipitate[index + 1] = new EnterField("<html>[" + reactants[index / 2].withoutNumState() + "]", "Amount", "Volume");
					c.gridy++;
					fields.add(precipitate[index + 1], c);
				}
			}
		}
		
		enterPanel.setVisible(true);
	}
	
	/*
	 * Prompts the user to choose a state for the compound. If they do, returns true, returns false otherwise.
	 */
	private boolean getState(Compound c)
	{
		Object obj = JOptionPane.showInputDialog(panel, "<html>Please enter the state of matter of " + c + "</html>", "State", JOptionPane.QUESTION_MESSAGE, 
				null, Compound.getValidstates().toArray(), "aq");
		if(obj == null) return false;
		c.setState((String)obj);
		return true;
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		if(saved.size() == 0) return 0;
		if(saved.size() == 1) return saved.get(0);
		Object obj = JOptionPane.showInputDialog(panel, "Choose a number to save.", "Save Number", JOptionPane.QUESTION_MESSAGE, null, saved.toArray(), 
				saved.get(0));
		if(obj != null) return (Double) obj;
		return 0;
	}
	
	public void useSavedNumber(double num)
	{
		if(k == null) return;
		String[] list = new String[compounds.length + (solubility != null ? 2 : precipitate != null ? 6 : 1)];
		for(int index = 0; index < compounds.length; index++)
		{
			list[index] = compounds[index].getName();
		}
		list[compounds.length] = "K";
		if(solubility != null) list[list.length - 1] = "Solubility";
		else if(precipitate != null)
		{
			for(int index = compounds.length + 1; index < list.length; index++)
			{
				list[index] = precipitate[index - compounds.length - 1].getName();
			}
		}
		
		Object obj = JOptionPane.showInputDialog(panel, "Choose where to use the number.", "Use Saved", JOptionPane.QUESTION_MESSAGE, null, list, list[0]);
		if(obj == null) return;
		for(int index = 0; index < compounds.length; index++)
		{
			if(list[index].equals(obj))
			{
				compounds[index].setAmount(num);
				return;
			}
		}
		if(obj.equals("K"))
		{
			k.setAmount(num);
			return;
		}
		if(obj.equals("Solubility"))
		{
			solubility.setAmount(num);
			return;
		}
		for(int index = compounds.length + 1; index < list.length; index++)
		{
			if(obj.equals(precipitate[index - compounds.length - 1].getName()))
			{
				precipitate[index - compounds.length - 1].setAmount(num);
				return;
			}
		}
	}
	
	private static TreeMap<String, Double> createMap()
	{
		TreeMap<String, Double> map = new TreeMap<String, Double>();
		map.put("Al(OH)<sub>3</sub>", 1.8E-33);
		map.put("BaCO<sub>3</sub>", 8.1E-9);
		map.put("BaF<sub>2</sub>", 1.7E-6);
		map.put("BaSO<sub>4</sub>", 1.1E-10);
		map.put("Bi<sub>2</sub>S<sub>3</sub>", 1.6E-72);
		map.put("CdS", 8E-28);
		map.put("CaCO<sub>3</sub>", 8.7E-9);
		map.put("CaF<sub>2</sub>", 3.9E-11);
		map.put("Ca(OH)<sub>2</sub>", 8E-6);
		map.put("Ca<sub>3</sub>(PO<sub>4</sub>)<sub>2</sub>", 1.2E-26);
		map.put("Cr(OH)<sub>3</sub>", 3E-29);
		map.put("CoS", 4E-21);
		map.put("CuBr", 4.2E-8);
		map.put("CuI", 5.1E-12);
		map.put("Cu(OH)<sub>2</sub>", 2.2E-20);
		map.put("CuS", 6E-37);
		map.put("Fe(OH)<sub>2</sub>", 1.6E-14);
		map.put("Fe(OH)<sub>3</sub>", 1.1E-36);
		map.put("FeS", 6E-19);
		map.put("PbCO<sub>3</sub>", 3.3E-14);
		map.put("PbCl<sub>2</sub>", 2.4E-4);
		map.put("PbCrO<sub>4</sub>", 2E-14);
		map.put("PbF<sub>2</sub>", 4.1E-8);
		map.put("PbI<sub>2</sub>", 1.4E-8);
		map.put("PbS", 3.4E-28);
		map.put("MgCO<sub>3</sub>", 4E-3);
		map.put("Mg(OH)<sub>2</sub>", 1.2E-11);
		map.put("MnS", 3E-14);
		map.put("Hg<sub>2</sub>Cl<sub>2</sub>", 3.5E-18);
		map.put("Hg(OH)<sub>2</sub>", 3.1E-26);
		map.put("HgS", 4E-54);
		map.put("Ni(OH)<sub>2</sub>", 5.5E-16);
		map.put("NiS", 1.4E-24);
		map.put("AgBr", 7.7E-13);
		map.put("Ag<sub>2</sub>CO<sub>3</sub>", 8.1E-12);
		map.put("AgCl", 1.6E-10);
		map.put("AgI", 8.3E-17);
		map.put("Ag<sub>2</sub>SO<sub>4</sub>", 1.4E-5);
		map.put("Ag<sub>2</sub>S", 6E-51);
		map.put("SrCO<sub>3</sub>", 1.6E-9);
		map.put("SrSO<sub>4</sub>", 3.8E-7);
		map.put("Sn(OH)<sub>2</sub>", 5.4E-27);
		map.put("SnS", 1E-26);
		map.put("Zn(OH)<sub>2</sub>", 1.8E-14);
		map.put("ZnS", 3E-23);
		return map;
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}