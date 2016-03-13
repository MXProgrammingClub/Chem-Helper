/*
 * Finds the oxidation numbers for the ions in a compound.
 * 
 * Author: Ted Pyne, Julia McClellan
 * Version: 3/13/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ChemHelper.InvalidInputException;
import Elements.Element;
import Equation.Compound;
import Equation.Ions;
import Equation.Monatomic;
import Equation.Polyatomic;
import HelperClasses.TextField;

public class OxidationNumber extends Function{	//Oxidation number in compound
	private JPanel panel;
	private TextField field;
	private JButton calculate;
	private Box result;
	
	public OxidationNumber() {
		super("Oxidation Number");
		setPanel();
	}


	private void setPanel() {
		panel = new JPanel();
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		subpanel.add(new JLabel("Enter the compound to calulate Oxidation numbers for: "), c);
		c.gridy++;
		field = new TextField(TextField.EQUATION);
		calculate = new JButton("Calulate");
		calculate.addActionListener(new ButtonListener());
		subpanel.add(field, c);
		c.gridy++;
		subpanel.add(calculate, c);
		c.gridy++;
		result = Box.createVerticalBox();
		subpanel.add(result, c);
		panel.add(subpanel);
	}


	public JPanel getPanel() {
		return panel;
	}
	
	private class ButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			result.removeAll();
			result.setVisible(false);
			try {
				Compound compound = Compound.parseCompound(field.getText());
				Ions[] ions = compound.getIons();
				
				int charge = 0;
				for(Ions ion: ions) charge += ion.getCharge() * ion.getNum();
				if(ions.length != 1 && ions[ions.length - 1].getCharge() * ions[ions.length - 1].getNum() == charge) 
				{
					//If the charge applies to the whole compound, it will just be attached to the last ion
					charge = ions[ions.length - 1].getCharge();
				}
				
				int[][] numbers = findNumbers(ions, charge);

				for(int index = 0; index < ions.length; index++)
				{
					if(ions[index] instanceof Polyatomic)
					{
						Monatomic[] elements = ions[index].getElements();
						for(int i = 0; i < elements.length; i++)
						{
							result.add(new JLabel("<html>" + elements[i].getElement().getSymbol() + ": " + (numbers[index][i] > 0 ? '+' : "")
									+ numbers[index][i] + "</html>"));
						}
					}
					else result.add(new JLabel("<html>" + ((Monatomic)ions[index]).getElement().getSymbol() + ": " + (numbers[index][0] > 0 ? '+' : "") 
							+ numbers[index][0] + "</html>"));
				}
				result.setVisible(true);
			} 
			catch (InvalidInputException e) {
				result.add(new JLabel("There was an error with your input"));
				result.setVisible(true);
			}
		}
		
		private int[][] findNumbers(Ions[] ions, int charge) {
			
			int[][] numbers = new int[ions.length][1];
			int total = 0;
			//Rule: the oxidation number of an element in its free state  is 0; a monatomic ion's is the charge of the ion
			if(ions.length == 1) numbers[0][0] = ions[0].getCharge();
			else
			{
				boolean fluorine = false;
				for(int index = 0; index < ions.length; index++)
				{
					if(ions[index] instanceof Polyatomic)
					{
						int charged = Polyatomic.findCharge((Polyatomic)ions[index]);
						int[][] values = findNumbers(ions[index].getElements(false), charged);
						numbers[index] = new int[values.length];
						for(int i = 0; i < values.length; i ++) numbers[index][i] = values[i][0];
						total += charged * ions[index].getNum();
					}
					else
					{
						Element e = ((Monatomic)ions[index]).getElement();
						//Rule: the oxidation number of fluorine is always -1
						if(e.getNum() == 9)
						{
							numbers[index][0] = -1;
							fluorine = true;
							total += -ions[index].getNum();
						}
						
						//Rule: groups 1 and 2 have oxidation numbers of 1 and 2 respectively
						if(e.getNum() != 1 && e.getGroup() <= 2 && e.getGroup() > 0)
						{
							numbers[index][0] = e.getGroup();
							total += e.getGroup() * ions[index].getNum();
						}
					}
				}

				//Search for oxygen has to be after, because if there's fluorine the number changes
				for(int index = 0; index < ions.length; index++)
				{
					if(numbers[index][0] == 0)
					{
						int num = ((Monatomic)ions[index]).getElement().getNum();
						if(num == 8)
						{
							//Rule: in the presence of fluorine, oxygen's number is +2
							if(fluorine)
							{
								numbers[index][0] = 2;
								total += 2 * ions[index].getNum();
							}
							//Rule: in peroxides, oxygen's number is -1
							else if(ions[index].getNum() == 2 && ions.length == 2 && ions[-index + 1] instanceof Monatomic && 
									((Monatomic)ions[-index + 1]).getElement().getNum() == 1)
							{
								numbers[index][0] = -1;
								total -= 2;
							}
							//Rule: in all other cases, oxygen is -2
							else 
							{
								numbers[index][0] = -2;
								total += -2 * ions[index].getNum();
							}
						}
						else if(num == 1)
						{
							//Rule: unless it's part of hydride, hydrogen's number is +1
							if(numbers.length == 2 && total == 1)
							{
								numbers[index][0] = -1;
								total += -ions[index].getNum();
							}
							else
							{
								numbers[index][0] = 1;
								total += ions[index].getNum();
							}
						}
					}
				}
				
				//If the total is not equal to the desired charge, the remaining ion's number will account for the remaining
				if(total != charge)
				{
					for(int index = 0; index < ions.length; index++)
					{
						if(numbers[index][0] == 0) numbers[index][0] = (charge - total) / ions[index].getNum();
					}
				}
			}
			return numbers;
		}
	}
}