/*
 * Takes a compound and creates a combustion equation with other reactant O.2 and products H.2/O and C/O.2.
 * equation() returns true- can save latest produced equation, but can't use a saved equation.
 * 
 * Author: Julia McClellan
 * Version: 2/18/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ChemHelper.InvalidInputException;
import Elements.Carbon;
import Elements.Element;
import Elements.Hydrogen;
import Elements.Oxygen;
import Equation.Equation;
import Equation.Compound;
import Equation.Ions;
import Equation.Monatomic;
import HelperClasses.TextField;

public class Combustion extends Function
{
	private JPanel panel;
	private TextField compound;
	private JButton combust;
	private JLabel result;
	private Equation equation;

	public Combustion()
	{
		super("Combustion");
		equation = null;
		
		compound = new TextField(TextField.COMPOUND);
		combust = new JButton("Combust");
		combust.addActionListener(new Combust());
		JButton help = new JButton("Help");
		help.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					JOptionPane.showMessageDialog(panel, "<html>" + TextField.getHelp() + "</html>", "Help", JOptionPane.QUESTION_MESSAGE);
				}
			});
		JPanel buttons = new JPanel();
		buttons.add(help);
		buttons.add(combust);
		result = new JLabel();
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		subpanel.add(new JLabel("Enter a compound to combust."), c);
		c.gridy++;
		subpanel.add(compound, c);
		c.gridy++;
		subpanel.add(buttons, c);
		c.gridy++;
		subpanel.add(result, c);
		
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	private class Combust implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				Element carbon = new Carbon(), hydrogen = new Hydrogen(), oxygen = new Oxygen();
				Compound c = Compound.parseCompound(compound.getText());
				boolean state = c.getState().equals(" ") ? false : true;
				Ions[] ions = c.getIons();
				int[] coefficients = new int[3], indices = {c.indexOf(hydrogen), c.indexOf(carbon), c.indexOf(oxygen)};
				int num = 0;
				for(int index = 0; index < indices.length; index++)
				{
					if(indices[index] != -1)
					{
						num++;
						coefficients[index] = ions[indices[index]].getNum();
					}
					else
					{
						if(index != 2)
						{
							result.setText("The compound must contain both Hydrogen and Carbon.");
							compound.grabFocus();
							return;
						}
						coefficients[index] = 0;
					}
				}
				if(num < ions.length)
				{
					result.setText("The compound can only contain Hydrogen, Oxygen, and Carbon");
					compound.grabFocus();
					return;
				}
				
				ArrayList<Compound> left = new ArrayList<Compound>(), right = new ArrayList<Compound>();
				Ions[] o2 = {new Monatomic(oxygen, 2)}, h2o = {new Monatomic(hydrogen, 2), new Monatomic(oxygen)}, 
						co2 = {new Monatomic(carbon), new Monatomic(oxygen, 2)};
				left.add(new Compound(o2, state ? "g" : ""));
				left.add(c);
				right.add(new Compound(h2o, state ? "l" : ""));
				right.add(new Compound(co2, state ? "g" : ""));
				
				//Balancing hydrogens
				if(coefficients[0] % 2 != 0)
				{
					c.setNum(2);
					right.get(0).setNum(coefficients[0]);
				}
				else right.get(0).setNum(coefficients[0] / 2);
				
				//Balancing carbons
				right.get(1).setNum(coefficients[1] * c.getNum());
				
				//Balancing oxygens
				int oxygens = right.get(0).getNum() + right.get(1).getNum() * 2 - coefficients[2] * left.get(1).getNum();
				
				if(oxygens <= 0)
				{
					result.setText("Too much oxygen in compound.");
					compound.grabFocus();
					return;
				}
				
				if(oxygens % 2 != 0)
				{
					left.get(0).setNum(oxygens);
					left.get(1).setNum(left.get(1).getNum() * 2);
					right.get(0).setNum(right.get(0).getNum() * 2);
					right.get(1).setNum(right.get(1).getNum() * 2);
				}
				else left.get(0).setNum(oxygens / 2);
				
				equation = new Equation(left, right);
				result.setIcon(Function.latex(equation).getIcon());
			}
			catch(InvalidInputException e)
			{
				result.setText(e.getMessage());
			}
			compound.grabFocus();
		}
	}
	
	public void resetFocus()
	{
		compound.grabFocus();
	}
	
	public JPanel getPanel()
	{	
		return panel;
	}
	
	public boolean equation()
	{
		return true;
	}
	
	public Equation saveEquation()
	{
		return equation;
	}
	
	public String getHelp()
	{
		return "<html>Enter a compound and click the \"Combust\"<br>"
				+ "button to create a combustion reaction.</html>";
	}
}