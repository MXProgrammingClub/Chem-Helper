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

/**
 * File: Equilibrium.java
 * Package: Functions
 * Version: 08/07/2016
 * Author: Julia McClellan
 * --------------------------------------------------
 * Performs various calculations for functions at equilibrium.
 */
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
	
	/**
	 * Constructs the function.
	 */
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
						else if(values[index] == Units.UNKNOWN_VALUE) steps.add(Function.latex("[" + Function.latex(relevant.get(index), false) + 
								"] =\\text{ ? M}"));
						else
						{
							steps.add(Function.latex("[" + Function.latex(relevant.get(index), false) + "] = " + values[index] + "\\text{ M}"));
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
					else if(values[compounds.length] == Units.UNKNOWN_VALUE) steps.add(Function.latex("\\text{K }= ?"));
					else
					{
						steps.add(Function.latex("\\text{K} = " + values[compounds.length]));
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
								steps.add(Function.latex((index == 0 ? precipitate[index].getName() : ((index % 2 == 0 ? "[" : "\\text{Volume }") + 
										Function.latex(reactants[(index - 1) / 2], false) + (index % 2 == 0 ? "]" : ""))) + " = " + extra[index] + 
										(index > 0 ? "\\text{ " + (index % 2 == 0 ? "M" : "L") + "}" : "")));
								sigFigs = Math.min(sigFigs, precipitate[index].getSigFigs());
							}
							else steps.add(Function.latex((index == 0 ? precipitate[index].getName() : ((index % 2 == 0 ? "[" : "\\text{Volume }") + 
									Function.latex(reactants[(index - 1) / 2], false) + (index % 2 == 0 ? "]" : ""))) + " = ?"));
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
							steps.add(Function.latex("\\text{Solubility}= " + extra[5] + " \\frac{mol}{L}"));
							sigFigs = Math.min(sigFigs, solubility.getSigFigs());
						}
						else steps.add(Function.latex("\\text{Solubility = ?}"));
					}
					
					steps.add(Box.createVerticalStrut(5));
					steps.add(new JLabel(expression.getIcon()));
					
					if(precipitate != null)
					{
						if(values[0] == Units.UNKNOWN_VALUE && extra[0] == Units.UNKNOWN_VALUE) //Fist calculates concentrations of each ion if necessary
						{
							if(extra[1] == Units.UNKNOWN_VALUE || extra[2] == Units.UNKNOWN_VALUE || extra[3] == Units.UNKNOWN_VALUE || extra[4] ==
									Units.UNKNOWN_VALUE)
							{
								results.add(new JLabel("Insufficient information for calculations."));
								results.setVisible(true);
								return;
							}
							LinkedList<String> stepList = new LinkedList<String>();
							double[] concentrations = calculateConcentrations(extra, relevant, reactants, stepList);
							for(String step: stepList) steps.add(Function.latex(step));
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
						
						double q;
						if(extra[0] == Units.UNKNOWN_VALUE)
						{
							//Finds Qsp and compares it to Ksp to find if there is a precipitate
							LinkedList<String> stepList = new LinkedList<String>();
							q = calculateK(values, stepList, powers, false);
							for(String step: stepList) steps.add(Function.latex(step));
							results.add(new JLabel("Q = " + Function.withSigFigs(q, sigFigs)));
						}
						else q = extra[0];
						steps.add(Function.latex(q + (q > values[values.length - 1] ? " > " : " < ") + values[values.length - 1]));
						steps.add(Function.latex("\\text{Q}" + (q > values[values.length - 1] ? " > " : " < ") + "\\text{K}"));
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
							
							String equation = values[values.length - 1] + " = ";
							String[] expressions = new String[values.length - 1];
							int col = 1;
							for(int index = 0; index < relevant.size(); index++)
							{
								if(values[index] == Units.UNKNOWN_VALUE)
								{
									results.add(new JLabel("<html>Enter initial concentration for " + relevant.get(index).withoutNumState()));
									results.setVisible(true);
									return;
								}
								labels[0][col] = new JLabel("<html>[" + relevant.get(index).withoutNumState() + "]");
								labels[1][col] = new JLabel(values[index] + "");
								labels[2][col] = new JLabel((powers.get(index) == 1 ? "" : powers.get(index)) + "x");
								labels[3][col] = new JLabel((values[index] == 0 ? "" : values[index] + " + ") + (powers.get(index) == 1 ? "" : 
									(powers.get(index) == -1 ? "-" : powers.get(index))) + "x");
								expressions[index] = Function.latex(relevant.get(index), false) + " = " + labels[3][col].getText();
								equation += "(" + labels[3][col].getText() + ")^{" + powers.get(index) + "} * ";
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
							steps.add(Function.latex(equation.substring(0, equation.length() - 3)));
							
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
									for(String step: stepList) steps.add(Function.latex(step));
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
								for(String step: stepList) steps.add(Function.latex(step));
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
							for(String step: stepList) steps.add(Function.latex(step));
							
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
						else
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

							if(values[compounds.length] == Units.UNKNOWN_VALUE) //K is unknown
							{
								if(unknown != -1)
								{
									results.add(new JLabel("To calculate K, enter the concentrations of all compounds."));
									results.setVisible(true);
									return;
								}
								
								LinkedList<String> stepList = new LinkedList<String>();
								double result = calculateK(values, stepList, powers, true);
								
								for(String step: stepList) steps.add(Function.latex(step));
								saved.add(result);
								results.add(new JLabel("K = " + Function.withSigFigs(result, sigFigs)));
							}
							else //K is known
							{
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
									for(String step: stepList) steps.add(Function.latex(step));
									
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
										String unit1 = solubility.getUnitName(), unit2 = solubility.getUnit2Name();
										steps.add(Function.latex("\\text{Solubility} = x = " + result + " \\frac{" + unit1 + "}{" + unit2 + "}"));
										saved.add(result);
										results.add(new JLabel("Solubility = " + Function.withSigFigs(result, sigFigs) + " " + unit1 + " / " + unit2));
										extra[5] = result; //So it won't accidentally be recalculated later
									}
								}
								else
								{
									LinkedList<String> stepList = new LinkedList<String>();
									double result = calculateConcentration(values, stepList, powers, unknown);
									
									for(String step: stepList) steps.add(Function.latex(step));
									saved.add(compounds[unknown].getBlankAmount(result));
									if(result != saved.get(0)) steps.add(Function.latex("\\text{x} = " + saved.get(0) + "\\frac{" + 
											compounds[unknown].getUnitName() + "}{" + compounds[unknown].getUnit2Name() + "}"));
									results.add(new JLabel("<html>[" + relevant.get(unknown).withoutNumState() + "] = " + 
											Function.withSigFigs(saved.get(0), sigFigs)));
									values[unknown] = result; //In case it it needed to calculate solubility later
								}
							}
						}
					}
					
					if(solubility != null && extra[5] == Units.UNKNOWN_VALUE)
					{
						steps.add(Function.latex("\\text{Solubility} * " + relevant.get(0).getNum() + " = " + Function.latex(relevant.get(0), false)));
						double s = values[0] / relevant.get(0).getNum();
						steps.add(Function.latex("\\text{Solubility} = \\frac{" + values[0] + "}{" + relevant.get(0).getNum() + "} = " + s 
								+ "\\frac{mol}{L}"));
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
	
	/**
	 * Returns the panel to its original state after the reset button has been pressed.
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
	
	/**
	 * Calculates the value of K given the concentrations of the relevant compounds.
	 * @param values An array containing the concentrations of relevant compounds (should be of length relevant.size() + 1, last index doesn't matter).
	 * @param steps An empty LinkedList that the method will fill with steps.
	 * @param powers An array with the power each concentration should be raised to.
	 * @param k Whether the calculations are solving for K or Q.
	 * @return The value of k.
	 */
	private static double calculateK(double[] values, LinkedList<String> steps, ArrayList<Integer> powers, boolean k)
	{
		double value = 1;
		String step1 = "\\text{" + (k ? "K" : "Q") + "} = ", step2 = "\\text{" + (k ? "K" : "Q") + "} = "; //Step 1 shows multiplication before raising to 
			//powers, step 2 shows after
		for(int index = 0; index < values.length - 1; index++)
		{
			step1 += "" + values[index] + "^{" + powers.get(index) + "} * ";
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
	
	/**
	 * Calculates one concentration given all the others and K.
	 * @param values The concentrations (with the unknown index left empty) and the last index containing K.
	 * @param steps An empty LinkedList that the method will fill with steps.
	 * @param powers An array with the power each concentration should be raised to.
	 * @param unknown The index of the unknown concentration.
	 * @return The concentration of the compound in the unknown index.
	 */
	public static double calculateConcentration(double[] values, LinkedList<String> steps, ArrayList<Integer> powers, int unknown)
	{
		double value = 1;
		String step1 = values[values.length - 1] + " = ", step2 = values[values.length - 1] + " = ";
		for(int index = 0; index < values.length - 1; index++)
		{
			if(index != unknown)
			{
				step1 += values[index] + "^{" + powers.get(index) + "} * ";
				double num = Math.pow(values[index], powers.get(index));
				step2 += num + " * ";
				value *= num;
			}
			else
			{
				step1 += "x^{" + powers.get(index) + "} * ";
				step2 += "x^{" + powers.get(index) + "} * ";
			}
		}
		step1 = step1.substring(0, step1.length() - 3);
		step2 = step2.substring(0, step2.length() - 3);
		steps.add(step1);
		steps.add(step2);
		value = values[values.length - 1] / value;
		steps.add("x^{" + powers.get(unknown) + "} = " + value);
		value = Math.pow(value, 1.0 / powers.get(unknown));
		steps.add("x = " + value + "\\text{ M}");
		return value;
	}
	
	/**
	 * Calculates all unknown concentrations given the others and K and returns the indices in values which have been changed, or null if there is 
	 * insufficient information for the calculations.
	 * @param values The concentrations and K value, with unknown indices left blank.
	 * @param steps An empty LinkedList that the method will fill with steps.
	 * @param powers An array with the power each concentration should be raised to.
	 * @param compounds The compounds used in the calculations
	 * @param storeX An array of length 1 to store the value of x.
	 * @return The indices in values which have been changed, or null if there is insufficient information for the calculations.
	 */
	public static LinkedList<Integer> calculateValues(double[] values, LinkedList<String> steps, ArrayList<Integer> powers, ArrayList<Compound> compounds,
			double[] storeX)
	{
		double newK = values[values.length - 1]; //Will divide K by all known concentrations
		String stepK = newK + " * ";
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
					steps.add("[" + Function.latex(compounds.get(index), false) + "] = " + (powers.get(index) == 1 ? "" : powers.get(index)) + "x");
					changed.add(index);
				}
			}
			else
			{
				newK /= Math.pow(values[index], powers.get(index));
				stepK += "\\frac{1}{" + values[index] + "^{" + powers.get(index) + "}} * ";
			}
		}
		if(newK != values[values.length - 1])
		{
			steps.add("\\text{Divide K by known concentrations:}");
			steps.add(stepK.substring(0, stepK.length() - 3) + " = " + newK); //Gets rid of last * before adding equals
		}
		
		String step = newK + " = ";
		int value = 1, sum = 0;
		for(int index = 0; index < values.length - 1; index++)
		{
			int power = powers.get(index);
			value *= Math.pow(power, power);
			sum += power;
			step += (power == 1 ? "" : power) + "x^{" + power + "} * ";
		}
		steps.add(step.substring(0, step.length() - 3)); //Removes last *
		if(value != 1)
		{
			steps.add(newK + " = " + (value == 1 ? "" : value + " * ") + "x^{" + sum + "}");
			newK /= value;
		}
		steps.add(newK + " = x^{" + sum + "}");
		
		double x = Math.pow(newK, (1.0 / sum));
		steps.add("x = " + x);
		storeX[0] = x;
		
		for(Integer index: changed)
		{
			int power = powers.get(index);
			values[index] = x * power;
			steps.add("[" + Function.latex(compounds.get(index), false) + "] = " + (power == 1 ? "" : power) + "x = " + values[index] + " \\frac{mol}{L}");
		}
		
		return changed;
	}
	
	/**
	 * Solves the ICE table approximately by assuming that in the denominator x does not matter.
	 * @param values
	 * @param product The product of x^x for each positive power.
	 * @param sum The sum of the positive powers.
	 * @param index The first index of powers with a negative value.
	 * @param powers An array with the power each concentration should be raised to.
	 * @param steps An empty LinkedList that the method will fill with steps.
	 * @param ex The expressions describing each index of values.
	 * @return The solved concentrations.
	 */
	public static double[] solveApprox(double[] values, double product, int sum, int index, ArrayList<Integer> powers, LinkedList<String> steps, String[] ex)
	{
		double denom = 1;
		String fraction = values[values.length - 1] + " = \\frac{" + product + "x^{" + sum + "}}{";
		for(; index < powers.size(); index++)
		{
			steps.add("\\text{Assume }" + values[index] + " - " + (powers.get(index) == -1 ? "" : -powers.get(index)) + "x \u2248 " + values[index]);
			fraction += values[index] + "^{" + -powers.get(index) + "} * ";
			denom *= Math.pow(values[index], -powers.get(index));
		}
		steps.add(fraction.substring(0, fraction.length() - 3));
		double x = values[values.length - 1] * denom / product;
		steps.add(x + " = x^{" + sum + "}");
		x = Math.pow(x, 1.0 / sum);
		steps.add("x = " + x);
		
		double[] results = new double[values.length - 1];
		for(int i = 0; i < powers.size(); i++)
		{
			results[i] = values[i] + powers.get(i) * x;
			steps.add(ex[i] + " = " + results[i] + " \\text{M}");
		}
		return results;
	}
	
	/**
	 * Solves the ICE table with quadratic equations. Returns null if it cannot be solved.
	 * @param original The concentrations and k values before the reaction.
	 * @param powers An array with the power each concentration should be raised to.
	 * @param steps An empty LinkedList that the method will fill with steps.
	 * @param expressions The expressions describing each index of values.
	 * @return The concentrations after the reaction, or null if it cannot be solved.
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
		steps.add(original[original.length - 1] + " = \\frac{" + displayQuadratic(top) + "}{" + displayQuadratic(bottom) + "}");
		
		for(int i = 0; i < bottom.length; i++)
		{
			bottom[i] *= original[original.length - 1];
		}
		steps.add(displayQuadratic(bottom) + " = " + displayQuadratic(top));
		
		for(int i = 0; i < bottom.length; i++)
		{
			top[i] -= bottom[i];
		}
		steps.add("0 = " + displayQuadratic(top));
		
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
	
	/**
	 * Multiplies the two binomials.
	 * @param factors A 2d array with the two binomials.
	 * @return The resulting trinomial in the form of an array.
	 */
	private static double[] foil(double[][] factors)
	{
		double[] expression = new double[3];
		expression[0] = factors[0][0] * factors[1][0];
		expression[1] = factors[0][0] * factors[1][1] + factors[1][0] * factors[0][1];
		expression[2] = factors[0][1] * factors[1][1];
		return expression;
	}
	
	/**
	 * Formats the trinomial stored in an array as a quadratic.
	 * @param expression The trinomial in the form of an array.
	 * @return The quadratic as a string.
	 */
	private static String displayQuadratic(double[] expression)
	{
		String str = "";
		if(expression[0] == -1) str += "-";
		else if(expression[0] != 1 && expression[0] != 0) str += expression[0];
		if(expression[0] != 0) str += "x^{2}";
		
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
	
	/**
	 * Calculates the concentrations of each compound and K from the solubility of the solid.
	 * @param s The solubility of the precipitate.
	 * @param steps An empty LinkedList that the method will fill with steps.
	 * @param relevant The relevant compounds.
	 * @param powers An array with the power each concentration should be raised to.
	 * @return The concentrations of each compound.
	 */
	public static double[] calculateFromSolubility(double s, LinkedList<String> steps, ArrayList<Compound> relevant, ArrayList<Integer> powers)
	{
		double[] values = new double[relevant.size() + 1];
		String step1 = "\\text{k} = ", step2 = "\\text{k} = "; //Calculates k as it goes
		values[values.length - 1] = 1; 
		for(int index = 0; index < relevant.size(); index++)
		{
			String compound = "[" + Function.latex(relevant.get(index), false) + "]";
			int power = powers.get(index);
			values[index] = s * power;
			steps.add(compound + " = " + s + " * " + power + " = " + values[index] + " \\frac{mol}{L}");
			step1 += compound + "^{" + power + "} * ";
			step2 += "(" + values[index] + ")^{" + power + "} * ";
			values[values.length - 1] *= Math.pow(values[index], power);
		}
		steps.add(step1.substring(0, step1.length() - 3));
		steps.add(step2.substring(0, step2.length() - 3));
		steps.add("\\text{k} = " + values[values.length - 1]);
		return values;
	}
	
	/**
	 * Calculates the concentrations of the compounds.
	 * @param values The extra values about the precipitate.
	 * @param compounds The relevant compounds.
	 * @param original The reactants.
	 * @param steps An empty LinkedList that the method will fill with steps.
	 * @return The solved concentrations.
	 */
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
		
		String compound0 = "[" + Function.latex(compounds.get(0), false) + "]", compound1 = "[" + Function.latex(compounds.get(1), false) + "]";
		steps.add(compound0 + " = \\frac{" + values[1] + " * " + values[2] + " * " + coefficients[0] + "}{" + values[1] +
				" + " + values[3] + "}");
		concentrations[0] = values[1] * values[2] * coefficients[0] / (values[1] + values[3]);
		steps.add(compound0 + " = " + concentrations[0] + " \\frac{mol}{L}");
		
		steps.add(compound1 + " = \\frac{" + values[3] + " * " + values[4] + " * " + coefficients[1] + "}{" + values[1] +
				" + " + values[3] + "}");
		concentrations[1] = values[3] * values[4] * coefficients[1] / (values[1] + values[3]);
		steps.add(compound1 + " = " + concentrations[1] + " \\frac{mol}{L}");
		return concentrations;
	}
	
	/**
	 * Returns true as the equilibrium function does deal with equations.
	 * @return true
	 */
	@Override
	public boolean equation()
	{
		return true;
	}
	
	/**
	 * Returns the most recently used equation so that ChemHelper can save it.
	 * @return The equation to save.
	 */
	@Override
	public Equation saveEquation()
	{
		return reader.saveEquation();
	}
	
	/**
	 * Takes equation and sets up EnterFields to perform calculations with the compounds in the equation.
	 * @param equation The equation to be used.
	 */
	@Override
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
			final Double kValue = KSP.get(irrelevant.get(0).withoutNumState());
			if(kValue != null)
			{
				final JCheckBox stored = new JCheckBox("Use stored value for K");
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
	
	/**
	 * Prompts the user to choose a state for the compound. If they do, returns true, returns false otherwise.
	 * @param c The compound without a state.
	 * @return Whether the user enters a state for the compound.
	 */
	private boolean getState(Compound c)
	{
		Object obj = JOptionPane.showInputDialog(panel, "<html>Please enter the state of matter of " + c + "</html>", "State", JOptionPane.QUESTION_MESSAGE, 
				null, Compound.getValidstates().toArray(), "aq");
		if(obj == null) return false;
		c.setState((String)obj);
		return true;
	}
	
	/**
	 * Returns true as this function can save and use saved numbers.
	 * @return true
	 */
	@Override
	public boolean number()
	{
		return true;
	}
	
	/**
	 * Allows the user to chooses a recently calculated number to save.
	 * @return The number the user chooses, or 0 if one is not chosen.
	 */
	@Override
	public double saveNumber()
	{
		if(saved.size() == 0) return 0;
		if(saved.size() == 1) return saved.get(0);
		Object obj = JOptionPane.showInputDialog(panel, "Choose a number to save.", "Save Number", JOptionPane.QUESTION_MESSAGE, null, saved.toArray(), 
				saved.get(0));
		if(obj != null) return (Double) obj;
		return 0;
	}
	
	/**
	 * Uses a number previously saved by ChemHelper in this function.
	 * @param num The saved number.
	 */
	@Override
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
	
	/**
	 * Creates the static TreeMap from compound name to solubility.
	 * @return The TreeMap<String, Double> from compound formula to solubility.
	 */
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
	
	/**
	 * Returns the instructions for this function.
	 * @return The help string.
	 */
	@Override
	public String getHelp()
	{
		return "<html>First enter the reaction at equilibrium, being<br>"
				+ " sure to include states of matter. For double<br>"
				+ "displacement reactions, ChemHelper will remove<br>"
				+ "spectator ions, and ask whether the question<br>"
				+ "requires finding if there is a precipitate.<br>"
				+ "Select whether the given concentrations are from<br>"
				+ "before the reaction occurred or at equilibrium,<br>"
				+ "and enter all known information. Click the<br>"
				+ "calculate button for ChemHelper to find any<br>"
				+ "remaining values.</html>";
	}
	
	/**
	 * Returns the panel containing the equilibrium GUI components.
	 * @return The JPanel for this function.
	 */
	@Override
	public JPanel getPanel()
	{
		return panel;
	}
}