/*
 * Balances redox reactions and if possible finds information about the electrochemical cell it would create.
 * 
 * Author: Julia McClellan
 * Version: 3/10/2016
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

import Elements.Nitrogen;
import Elements.Sodium;
import Equation.Compound;
import Equation.Equation;

public class Redox extends Function
{
	private static final TreeMap<String, Double> REACTIONS = createMap();
	private JPanel panel;
	private EquationReader reader;
	private ArrayList<Equation> halves = new ArrayList<Equation>();
	private double potential;
	private JPanel cell;
	
	public Redox()
	{
		super("Redox Reactions");
		
		reader = new EquationReader(this, true);
		cell = new JPanel(new GridBagLayout());
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		subpanel.add(reader.getPanel(), c);
		c.gridy++;
		subpanel.add(cell, c);
		
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	public ArrayList<Equation> getArrays()
	{
		return halves;
	}
	
	public boolean equation()
	{
		return true;
	}
	
	public Equation saveEquation()
	{
		return reader.getEquation();
	}
	
	public void useSaved(Equation equation)
	{
		JPanel redox = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		redox.add(new JLabel("<html>Oxidation: " + halves.get(0) + "</html>"), c);
		c.gridx++;
		redox.add(Box.createHorizontalStrut(20), c);
		c.gridx++;
		redox.add(new JLabel("<html>Reduction: " + halves.get(1) + "</html>"), c);
		c.gridy++;
		c.gridx = 0;
		redox.add(new JLabel("<html>Anode: " + halves.get(0).getLeft().get(0) + "</html>"), c);
		c.gridx += 2;
		redox.add(new JLabel("<html>Cathode: " + halves.get(1).getRight().get(0) + "</html>"), c);
		c.gridy++;
		c.gridx = 0;
		redox.add(new JLabel("<html>In solution: " + halves.get(0).getRight().get(0) + "</html>"), c);
		c.gridx += 2;
		redox.add(new JLabel("<html>In solution: " + halves.get(1).getLeft().get(0) + "</html>"), c);
		
		cell.setVisible(false);
		cell.removeAll();
		c.gridx = 0;
		c.gridy = 0;
		cell.add(redox);
		c.gridy++;
		
		Compound c1 = halves.get(1).getLeft().get(1), c2 = halves.get(0).getRight().get(0);
		String salt = "";
		if(c1.contains(new Sodium()) || c2.contains(new Sodium())) salt += "K";
		else salt += "Na";
		if(c1.contains(new Nitrogen()) || c2.contains(new Nitrogen())) salt += "ClO<sub>3</sub></html>";
		else salt += "NO<sub>3</sub></html>";
		//Add more possible salt bridges?
		cell.add(new JLabel("<html>Possible salt bridge: " + salt), c);
		c.gridy++;
		
		String oxidation = halves.get(0).reverse().toString(), reduction = halves.get(1).toString();
		Double red = REACTIONS.get(reduction), ox = REACTIONS.get(oxidation);
		if(red == null)
		{
			int enter = JOptionPane.showConfirmDialog(panel, "<html>No value found for " + reduction + 
					"<br>Do you have one to enter?</html>", "Enter Value", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(enter == JOptionPane.YES_OPTION)
			{
				while(red == null)
				{
					Object entered = JOptionPane.showInputDialog(panel, "<html>Enter value for " + reduction + "</html>", "Enter Value", 
							JOptionPane.QUESTION_MESSAGE, null, null, null);
					if(entered == null || ((String)entered).equals("")) break;
					try
					{
						red = Double.parseDouble((String)entered);
					}
					catch(Throwable e){}
				}
			}
		}
		if(ox == null)
		{
			int enter = JOptionPane.showConfirmDialog(panel, "<html>No value found for " + oxidation + 
					"<br>Do you have one to enter?</html>", "Enter Value", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(enter == JOptionPane.YES_OPTION)
			{
				while(ox == null)
				{
					Object entered = JOptionPane.showInputDialog(panel, "<html>Enter the value for " + oxidation + "</html>", "Enter Value", 
							JOptionPane.QUESTION_MESSAGE, null, null, null);
					if(entered == null || ((String)entered).equals("")) break;
					try
					{
						ox = Double.parseDouble((String)entered);
					}
					catch(Throwable e){}
				}
			}
		}
		
		if(red != null && ox != null)
		{
			cell.add(new JLabel("<html>Cell potential: E<sub>cell</sub> = E<sub>red</sub> - E<sub>ox</sub>"), c);
			c.gridy++;
			cell.add(new JLabel("<html>E<sub>red</sub> = " + red), c);
			c.gridy++;
			cell.add(new JLabel("<html>E<sub>ox</sub> = " + ox), c);
			c.gridy++;
			potential = red - ox;
			cell.add(new JLabel("<html>E<sub>cell</sub> = " + red + " - " + ox + " = " + potential), c);
			c.gridy++;
			if(potential > 0) cell.add(new JLabel("Voltaic"), c);
			else cell.add(new JLabel("Electrolytic"), c);
		}
		cell.setVisible(true);
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return potential;
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
	
	private static TreeMap<String, Double> createMap()
	{
		TreeMap<String, Double> map = new TreeMap<String, Double>();
		map.put("F<sub>2</sub>(g) + 2e<sub>-</sub> \u2192 2F<sup>-</sup>", 2.87);
		map.put("H<sub>2</sub>O<sub>2</sub>(aq) + 2H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 2H<sub>2</sub>O(l)", 1.78);
		map.put("PbO<sub>2</sub>(s) + 4H<sup>+</sup>(aq) + SO<sub>4</sub><sup>2-</sup>(aq) + 2e<sup>-</sup> \u2192 PbSO<sub>4</sub>(s) + 2H<sub>2</sub>O(l)",
				1.69);
		map.put("MnO<sub>4</sub><sup>-</sup>(aq) + 4H<sup>+</sup>(aq) + 3e<sup>-</sup> \u2192 MnO<sub>2</sub>(s) + 2H<sub>2</sub>O(l)", 1.68);
		map.put("MnO<sub>4</sub><sup>-</sup>(aq) + 8H<sup>+</sup>(aq) + 5e<sup>-</sup> \u2192 Mn<sup>2+</sup>(aq) + 4H<sub>2</sub>O(1)", 1.51);
		map.put("Au<sup>3+</sup>(aq) + 3e<sup>-</sup> \u2192 Au(s)", 1.5);
		map.put("PbO<sub>2</sub>(s) + 4H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 Pb<sup>2+</sup>(aq) + 2H<sub>2</sub>O(l)", 1.46);
		map.put("Cl<sub>2</sub> + 2e<sup>-</sup> /u2192 2Cl<sup>-</sup>", 1.36);
		map.put("Cr<sub>2</sub>O<sub>7</sub><sup>2-</sup>(aq) + 14H<sup>+</sup>(aq) + 6e<sup>-</sup> \u2192 2Cr<sup>3+</sup>(aq) + 7H<sub>2</sub>O(l)", 1.33);
		map.put("O<sub>2</sub>(g) + 4H<sup>+</sup>(aq) + 4e<sup>-</sup> \u2192 2H<sub>2</sub>O(l)", 1.23);
		map.put("MnO<sub>2</sub>(g) + 4H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 Mn<sup>2+</sup>(aq) + 2H<sub>2</sub>O(l)", 1.21);
		//fractional coefficient?
		map.put("Br<sub>2</sub>(l) + 2e<sup>-</sup> \u2192 2Br<sup>-</sup>(aq)", 1.09);
		map.put("VO<sub>2</sub><sup>+</sup>(aq) + 2H<sup>+</sup>(aq) + e<sup>-</sup> \u2192 VO<sup>2+</sup>(aq) + H<sub>2</sub>(l)", 1.0);
		map.put("NO<sub>3</sub><sup>-</sup>(aq) + 4H<sup>+</sup>(aq) + 3e<sup>-</sup> \u2192 NO(s) + 2H<sub>2</sub>(l)", .96);
		map.put("ClO<sub>2</sub>(g) + e<sup>-</sup> \u2192 ClO<sub>2</sub><sup>-</sup>(aq)", .95);
		map.put("Ag<sup>+</sup>(aq) + e<sup>-</sup> \u2192 Ag(s)", .8);
		map.put("Fe<sup>3+</sup>(aq) + e<sup>-</sup> \u2192 Fe<sup>2+</sup>(aq)", .77);
		map.put("O<sub>2</sub>(g) + 2H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 H<sub>2</sub>O<sub>2</sub>(aq)", .7);
		map.put("MnO<sub>4</sub><sup>-</sup>(aq) + e<sup>-</sup> \u2192 MnO<sub>4</sub><sup>2-</sup>(aq)", .56);
		map.put("I<sub>2</sub>(s) + 2e<sup>-</sup> \u2192 2I<sup>-</sup>(aq)", .54);
		map.put("Cu<sup>+</sup>(aq) + e<sup>-</sup> \u2192 Cu(s)", .52);
		map.put("O<sub>2</sub>(g) + 2H<sub>2</sub>O(l) + 4e<sup>-</sup> \u2192 4OH<sup>-</sup>", .4);
		map.put("Cu<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Cu(s)", .34);
		map.put("SO<sub>4</sub><sup>2-</sup>(aq) + 4H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 H<sub>2</sub>SO<sub>3</sub>(aq) + H<sub>2</sub>O(l)", .2);
		map.put("Cu<sup>2+</sup>(aq) + e<sup>-</sup> \u2192 Cu<sup>+</sup>(aq)", .16);
		map.put("Sn<sup>4+</sup>(aq) + 2e<sup>-</sup> \u2192 Sn<sup>2+</sup>(aq)", .15);
		map.put("2H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 H<sub>2</sub>(g)", 0.0);
		map.put("Fe<sup>3+</sup>(aq) + 3e<sup>-</sup> \u2192 Fe(s)", -.036);
		map.put("Pb<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Pb(s)", -.13);
		map.put("Sn<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Sn(s)", -.14);
		map.put("Ni<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Ni(s)", -.23);
		map.put("Cd<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Cd(s)", -.4);
		map.put("Fe<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Fe(s)", -.45);
		map.put("Cr<sup>3+</sup>(aq) + e<sup>-</sup> \u2192 Cr<sup>2+</sup>(aq)", -.5);
		map.put("Cr<sup>3+</sup>(aq) + 3e<sup>-</sup> \u2192 Cr(s)", -.73);
		map.put("Zn<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Zn(s)", -.76);
		map.put("2H<sub>2</sub>O(l) + 2e<sup>-</sup> \u2192 H<sub>2</sub>(g) + 2OH<sup>-</sup>(aq)", -.83);
		map.put("Mn<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Mn(s)", -1.18);
		map.put("Al<sup>3+</sup>(aq) + 3e<sup>-</sup> \u2192 Al(s)", -1.66);
		map.put("Mg<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Mg(s)", -2.37);
		map.put("Na<sup>+</sup>(aq) + e<sup>-</sup> \u2192 Na(s)", -2.71);
		map.put("Ca<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Ca(s)", -2.76);
		map.put("Ba<sup>2+</sup>(aq) + 2e<sup>-</sup> \u2192 Ba(s)", -2.9);
		map.put("K<sup>+</sup>(aq) + e<sup>-</sup> \u2192 K(s)", -2.92);
		map.put("Li<sup>+</sup>(aq) + e<sup>-</sup> \u2192 Li(s)", -3.04);
		return map;
	}
}