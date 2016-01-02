package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ChemHelper.Compound;
import ChemHelper.InvalidInputException;
import ChemHelper.Ions;
import Elements.Carbon;
import Elements.Element;
import Elements.Hydrogen;
import Elements.Oxygen;
import Equation.Equation;

public class Combustion extends Function
{
	private JPanel panel;
	private JTextField compound;
	private JButton combust;
	private JLabel result;
	private Equation equation;

	public Combustion()
	{
		super("Combustion");
		equation = null;
		
		compound = new JTextField(5);
		combust = new JButton("Combust");
		combust.addActionListener(new Combust());
		result = new JLabel();
		
		Box box = Box.createVerticalBox();
		box.add(new JLabel("Enter a compound to combust."));
		box.add(compound);
		box.add(combust);
		box.add(result);
		
		panel = new JPanel();
		panel.add(box);
	}
	
	private class Combust implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				Element carbon = new Carbon(), hydrogen = new Hydrogen(), oxygen = new Oxygen();
				Compound c = Compound.parseCompound(compound.getText());
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
							return;
						}
						coefficients[index] = 0;
					}
				}
				if(num < ions.length)
				{
					result.setText("The compound can only contain Hydrogen, Oxygen, and Carbon");
					return;
				}
				
				ArrayList<Compound> left = new ArrayList<Compound>(), right = new ArrayList<Compound>();
				Ions[] o2 = {new Ions(oxygen, 2)}, h2o = {new Ions(hydrogen, 2), new Ions(oxygen)}, co2 = {new Ions(carbon), new Ions(oxygen, 2)};
				left.add(new Compound(o2));
				left.add(c);
				right.add(new Compound(h2o));
				right.add(new Compound(co2));
				
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
		}
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
}