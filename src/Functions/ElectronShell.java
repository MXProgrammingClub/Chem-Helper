/*
 * Displays with latex an element's electron shell configuration given its atomic number, name, or symbol.
 * 
 * Authors: Julia McClellan, Luke Giacalone, Hyun Choi, Ted Pyne
 * Version: 1/2/2016
 */

package Functions;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import Elements.Element;

public class ElectronShell extends Function
{
	private JRadioButton num, sym, name;
	private JPanel buttons, panel, enterPanel;
	private JLabel info, results;
	private JButton calc;
	private JTextField enter;
	private ButtonGroup options;
	
	public ElectronShell()
	{
		super("Electron Shell Configuration");
		options = new ButtonGroup();
		num = new JRadioButton("Atomic Number", true);
		sym = new JRadioButton("Symbol");
		name = new JRadioButton("Element Name"); 
		options.add(num);
		options.add(sym);
		options.add(name); 
		buttons = new JPanel();
		buttons.setLayout(new GridLayout(3, 1));
		buttons.add(num);
		buttons.add(sym);
		buttons.add(name);
		info = new JLabel("");
		results = new JLabel();
		enter = new JTextField(20);
		calc = new JButton("Find Configuration");
		calc.addActionListener(new ButtonListener());
		enterPanel = new JPanel();
		enterPanel.add(enter);
		enterPanel.add(calc);
		panel = new JPanel();
		
		//adds all the elements to a panel with GridBagLayout
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		subpanel.add(info, c);
		c.gridwidth = 1;
		c.gridy = 1;
		subpanel.add(buttons, c);
		c.gridx = 1;
		subpanel.add(enterPanel, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		subpanel.add(results, c);
		
		//adds the subpanel to the real panel so that the contents will be at the top, not the center, of the screen
		panel.setLayout(new BorderLayout());
		panel.add(subpanel, BorderLayout.NORTH);
	}

	public JPanel getPanel() 
	{
		return panel;
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			String input = enter.getText();
			Element[] table = PeriodicTable.TABLE;
			String output = "";
			boolean done = false;
			if(num.isSelected())
			{
				try
				{
					int atomicNum = Integer.parseInt(input);
					if(atomicNum < 1 || table.length < atomicNum)
					{
						results.setText(atomicNum + " is not a valid atomic number.");
					}
					else
					{
						output += table[atomicNum - 1].getEShell();
						done = true;
						//results.setText(table[atomicNum - 1].getEShell());
					}
				}
				catch(NumberFormatException e)
				{
					results.setText("Please enter a number.");
				}
				
			}
			if(sym.isSelected())
			{
				boolean found = false;
				for(int element = 0; element < table.length && !found; element++)
				{
					if(table[element].getSymbol().equals(input))
					{
						output += table[element].getEShell();
						done = true;
						//results.setText(table[element].getEShell());
						found = true;
					}
					if(!found)
					{
						results.setText(input + " is not a valid symbol.");
					}
				}
			}
			if(name.isSelected())
			{
				boolean found = false;
				for(int element = 0; element < table.length && !found; element++)
				{
					if(table[element].getName().equals(input))
					{
						output += table[element].getEShell();
						done = true;
						//results.setText(table[element].getEShell());
						found = true;
					}
					if(!found)
					{
						results.setText(input + " is not a valid element.");
					}
				}
			}
			
			if (done) {
				TeXFormula formula = new TeXFormula (output);
				TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
						
				BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				icon.paintIcon(new JLabel(), image.getGraphics(), 0, 0);
				
				results.setIcon(icon);
			}
			
		}
	}
}