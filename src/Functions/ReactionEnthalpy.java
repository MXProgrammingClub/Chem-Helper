/*
 * Calculates the enthalpy of reaction for the given equation.
 * 
 * Author: Julia McClellan
 * Version: 2/24/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Equation.Compound;
import Equation.Equation;

public class ReactionEnthalpy extends Function
{
	private static final TreeMap<String, Double> HEAT = generateMap();
	private JPanel panel;
	private EquationReader reader;
	private JLabel result;
	private Box steps;
	private double num;
	
	public ReactionEnthalpy()
	{
		super("Enthalpy of Reaction");
		
		reader = new EquationReader(this);
		result = new JLabel();
		steps = Box.createVerticalBox();
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		subpanel.add(reader.getPanel(), c);
		c.gridy++;
		subpanel.add(result, c);
		
		panel = new JPanel();
		panel.add(subpanel);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(steps);
	}
	
	public boolean equation()
	{
		return true;
	}
	
	public Equation saveEquation()
	{
		return reader.saveEquation();
	}
	
	public void useSaved(Equation equation)
	{
		steps.setVisible(false);
		steps.removeAll();
		steps.add(new JLabel("<html>" + equation + "</html>"));
		
		ArrayList<Compound> left = equation.getLeft(), right = equation.getRight();
		StringBuffer step = new StringBuffer("(");
		double amount1 = 0, amount2 = 0;
		
		for(Compound c: right)
		{
			try
			{
				amount2 = processCompound(c, amount2, step);
			}
			catch(NullPointerException e)
			{
				result.setText("The enthalpy of reaction could not be calculated");
				return;
			}
			step.append(" + ");
		}
		
		step.delete(step.length() - 3, step.length());
		step.append(") - (");
		
		for(Compound c: left)
		{
			try
			{
				amount1 = processCompound(c, amount1, step);
			}
			catch(NullPointerException e)
			{
				result.setText("The enthalpy of reaction could not be calculated");
				return;
			}
			step.append(" + ");
		}
		
		step.delete(step.length() - 3, step.length());
		num = amount2 - amount1;
		step.append(") = " + num + " kJ / mol");
		steps.add(new JLabel(step.toString()));
		result.setText(num + " kJ / mol");
		steps.setVisible(true);
	}
	
	private double processCompound(Compound c, double amount, StringBuffer step)
	{
		Double value = HEAT.get(c.withoutNum());
		int num = c.getNum();
		if(value == null)
		{
			int enter = JOptionPane.showConfirmDialog(panel, "<html>No heat of formation value found for " + c.withoutNum() + 
					"<br>Do you have one to enter?</html>", "Enter Value", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(enter == JOptionPane.YES_OPTION)
			{
				while(value == null)
				{
					Object entered = JOptionPane.showInputDialog(panel, "<html>Enter the heat of formation value for " + c + "</html>", "Enter Value", 
							JOptionPane.QUESTION_MESSAGE, null, null, null);
					if(entered == null || ((String)entered).equals("")) throw new NullPointerException();
					try
					{
						value = Double.parseDouble((String)entered);
					}
					catch(Throwable e){}
				}
			}
			else throw new NullPointerException();
		}
		steps.add(new JLabel("<html>" + c.withoutNum() + " = " + value));
		step.append("(" + num + " * " + value + ")");
		amount += num * value;
		return amount;
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return num;
	}
	
	public String getHelp()
	{
		return "<html>Enter an equation to calculate its enthalpy of formation.<br>"
				+ "Please include states of matter. ChemHelper will check its<br>"
				+ "database for each element's heat of formation. If a given<br>"
				+ "element cannot be found, you will be prompted to manally enter<br>"
				+ "that element's value. ChemHelper will then calculate the enthalpy<br>"
				+ "for the reaction using these values.</html>";
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
	
	private static TreeMap<String, Double> generateMap()
	{
		TreeMap<String, Double> map = new TreeMap<String, Double>();
		map.put("Ag(s)", 0.0);
		map.put("AgCl(s)", -127.0);
		map.put("AgCN(s)", 146.0);
		map.put("Al<sub>2</sub>O<sub>3</sub>", -1675.7);
		map.put("BaCl<sub>2</sub>(aq)", -855.0);
		map.put("BaSO<sub>4</sub>", -1473.2);
		map.put("BeO(s)", -609.4);
		map.put("BiCl<sub>3</sub>(s)", -379.1);
		map.put("Bi<sub>2</sub>Cl<sub>3</sub>(s)", -143.1);
		map.put("Br<sub>2</sub>", 0.0);
		map.put("CCl<sub>4</sub>(l)", -128.2);
		map.put("CH<sub>4</sub>(g)", -74.6);
		map.put("C<sub>2</sub>H<sub>2</sub>(g)", 227.4);
		map.put("C<sub>2</sub>H<sub>4</sub>(g)", 52.4);
		map.put("C<sub>2</sub>H<sub>6</sub>(g)", -84.0);
		map.put("CO(g)", -110.5);
		map.put("CO<sub>2</sub>(g)", -393.5);
		map.put("CS<sub>2</sub>(l)", 89.0);
		map.put("Ca(s)", 0.0);
		map.put("CaCO<sub>3</sub>(s)", -1206.9);
		map.put("CaO(s)", -634.9);
		map.put("Ca(OH)<sub>2</sub>(s)", -985.2);
		map.put("Cl<sub>2</sub>(g)", 0.0);
		map.put("Co<sub>3</sub>O<sub>4</sub>(s)", -891.0);
		map.put("CoO(s)", -237.9);
		map.put("Cr<sub>2</sub>O<sub>3</sub>(s)", -1139.7);
		map.put("CsCl(s)", -443.0);
		map.put("Cs<sub>2</sub>SO<sub>4</sub>(s)", -1443.0);
		map.put("CuI(s)", -67.8);
		map.put("CuS(s)", -53.1);
		map.put("Cu<sub>2</sub>S(s)", -79.5);
		map.put("CuSO<sub>4</sub>(s)", -771.4);
		map.put("F<sub>2</sub>(g)", 0.0);
		map.put("FeCl<sub>3</sub>(s)", -399.49);
		map.put("FeO(s)", -272.0);
		map.put("FeS(s)", -100.0);
		map.put("Fe<sub>2</sub>O<sub>3</sub>(s)", -824.2);
		map.put("Fe<sub>3</sub>O<sub>4</sub>(s)", -1118.4);
		map.put("H(g)", 218.0);
		map.put("H<sub>2</sub>(g)", 0.0);
		map.put("HBr(g)", -36.3);
		map.put("HCl(g)", -92.3);
		map.put("HCl(aq)", -167.159);
		map.put("HCN(aq)", 108.9);
		map.put("HCHO", -108.6);
		map.put("HCOOH", -425.0);
		map.put("HF(g)", -273.3);
		map.put("HI(g)", 26.5);
		map.put("H<sub>2</sub>O(l)", -285.8);
		map.put("H<sub>2</sub>O(g)", -241.8);
		map.put("H<sub>2</sub>O<sub>2</sub>(l)", -187.8);
		map.put("H<sub>3</sub>PO<sub>2<sub>(l)", -595.4);
		map.put("H<sub>3</sub>PO<sub>4<sub>(aq)", -1271.7);
		map.put("H<sub>2</sub>S(g)", -20.6);
		map.put("H<sub>2</sub>SO<sub>3</sub>(aq)", -608.8);
		map.put("H<sub>2</sub>SO<sub>4</sub>(aq)", -814.0);
		map.put("HgCl<sub>2</sub>(s)", -224.3);
		map.put("Hg<sub>2</sub>Cl<sub>2</sub>(s)", -265.4);
		map.put("Hg<sub>2</sub>SO<sub>4</sub>(aq)", -743.1);
		map.put("I<sub>2</sub>(s)", 0.0);
		map.put("K(s)", 0.0);
		map.put("KBr(s)", -393.8);
		map.put("KMnO<sub>4</sub>(s)", -837.2);
		map.put("KOH", -424.6);
		map.put("LiBr(s)", -351.2);
		map.put("LiOH(s)", -487.5);
		map.put("Mn(s)", 0.0);
		map.put("MnCl<sub>2</sub>(aq)", -555.0);
		map.put("Mn(NO<sub>3</sub>)<sub>2</sub>(aq)", -635.5);
		map.put("MnO<sub>2</sub>(s)", -520.0);
		map.put("MnS(s)", -214.2);
		map.put("N<sub>2</sub>(g)", 0.0);
		map.put("NH<sub>3</sub>(g)", -45.9);
		map.put("NH<sub>4</sub>Br(s)", -270.8);
		map.put("NO(g)", 91.3);
		map.put("NO<sub>2</sub>(g)", 33.2);
		map.put("N<sub>2</sub>O(g)", 81.6);
		map.put("Na(s)", 0.0);
		map.put("NaBr(s)", -361.1);
		map.put("NaCl(s)", -411.2);
		map.put("NaHCO<sub>3</sub>(s)", -950.8);
		map.put("NaNO<sub>3</sub>(s)", -467.9);
		map.put("NaOH(s)", -425.8);
		map.put("Na<sub>2</sub>CO<sub>3</sub>(s)", -1130.7);
		map.put("Na<sub>2</sub>S(s)", -364.8);
		map.put("Na<sub>2</sub>SO<sub>4</sub>(s)", -1387.1);
		map.put("NH<sub>4</sub>Cl(s)", -314.4);
		map.put("O<sub>2</sub>(g)", 0.0);
		map.put("P<sub>4</sub>O<sub>6</sub>(s)", -1640.1);
		map.put("P<sub>4</sub>O<sub>10</sub>(s)", -2984.0);
		map.put("PbBr<sub>2</sub>(s)", -278.7);
		map.put("PbCl<sub>2</sub>(s)", -359.4);
		map.put("SF<sub>6</sub>(g)", -1220.5);
		map.put("SO<sub>2</sub>(g)", -296.8);
		map.put("SO<sub>3</sub>(g)", -454.5);
		map.put("SrO(s)", -592.0);
		map.put("TiO<sub>2</sub>(s)", -944.0);
		map.put("TiI(s)", -123.8);
		map.put("UCl<sub>4</sub>(s)", -1019.2);
		map.put("UCl<sub>6</sub>(s)", -1092.0);
		map.put("Zn(s)", 0.0);
		map.put("ZnCl<sub>2</sub>(aq)", -415.1);
		map.put("ZnO(s)", -350.5);
		map.put("ZnSO<sub>4</sub>(s)", -982.8);
		return map;
	}
}