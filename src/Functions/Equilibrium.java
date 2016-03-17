/*
 * Performs various calculations for functions at equilibrium.
 * 
 * Author: Julia McClellan
 * Version: 3/15/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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
	private JPanel panel, enterPanel, fields;
	private EquationReader reader;
	private JLabel expression;
	private EnterField[] compounds;
	private EnterField k;
	private JButton calculate, reset;
	private Box steps, results;
	private JRadioButton before;
	private ArrayList<Double> saved;
	private ArrayList<Compound> relevant;
	private int i;
	
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
					steps.add(Box.createVerticalStrut(5));
					
					if(before.isSelected())
					{
						if(values[compounds.length] == Units.UNKNOWN_VALUE)
						{
							results.add(new JLabel("Insufficient information- K needs to be specified if reaction is not at equilibrium."));
							results.setVisible(true);
							return;
						}
						else
						{
							
						}
					}
					else
					{
						if(values[compounds.length] == Units.UNKNOWN_VALUE)
						{
							
						}
						else
						{
							
						}
					}
					
					steps.setVisible(true);
					results.setVisible(true);
				}
			});
		enterPanel.add(calculate, c);
		
		c.gridy++;
		results = Box.createVerticalBox();
		enterPanel.add(results, c);
		
		c.gridy++;
		reset = new JButton("Reset");
		reset.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					setPanel();
				}
			});
		enterPanel.add(reset, c);
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
		results.removeAll();
		steps.removeAll();
		enterPanel.setVisible(false);
		panel.add(reader.getPanel());
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
		panel.remove(reader.getPanel());
		relevant = new ArrayList<Compound>();
		ArrayList<Compound> irrelevant = new ArrayList<Compound>();
		int[] switchIndex = new int[1];
		String ex = equation.getEquilibrium(relevant, irrelevant, switchIndex);
		i = switchIndex[0];
		
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
			EnterField field = new EnterField("<html>" + relevant.get(index).withoutNum() + "</html>", "Amount", "Volume");
			compounds[index] = field;
			fields.add(field, c);
			c.gridy++;
		}
		k = new EnterField("K");
		fields.add(k, c);
		enterPanel.setVisible(true);
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
		String[] list = new String[compounds.length + 1];
		for(int index = 0; index < compounds.length; index++)
		{
			list[index] = compounds[index].getName();
		}
		list[compounds.length] = "K";
		
		Object obj = JOptionPane.showInputDialog(panel, "Choose where to use the number.", "Use Saved", JOptionPane.QUESTION_MESSAGE, null, list, list[0]);
		if(obj == null) return;
		for(int index = 0; index < list.length; index++)
		{
			if(list[index].equals(obj))
			{
				compounds[index].setAmount(num);
				return;
			}
		}
		k.setAmount(num);
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}