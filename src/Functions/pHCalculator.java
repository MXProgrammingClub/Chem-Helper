/*
 * Calculates the pH/pOH/concentration H+/concentration OH.
 * 
 * Author: Julia McClellan
 * Version: 2/26/2016
 */

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

public class pHCalculator extends Function
{
	private JPanel panel;
	private EnterField ph, poh, h, oh;
	private JLabel result;
	private JButton calculate;
	private Box steps;
	private ArrayList<Double> nums;
	
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
					
				}
			});
		result = new JLabel();
		
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
		c.gridy++;
		subpanel.add(calculate, c);
		c.gridy++;
		subpanel.add(result, c);
		
		panel = new JPanel();
		panel.add(subpanel);
		steps = Box.createVerticalBox();
		panel.add(Box.createHorizontalStrut(10));
		panel.add(steps);
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}