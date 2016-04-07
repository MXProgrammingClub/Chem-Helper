/*
 * Displays with latex an element's electron shell configuration given its atomic number, name, or symbol.
 * 
 * Authors: Julia McClellan, Luke Giacalone, Hyun Choi, Ted Pyne
 * Version: 4/6/2016
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
	private JLabel info, results, nobleGas;
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
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		subpanel.add(buttons, c);
		c.gridx = 1;
		subpanel.add(enterPanel, c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 3;
		subpanel.add(info, c);
		c.gridx = 0;
		c.gridy++;
		
		subpanel.add(results, c);
		c.gridy++;
		nobleGas = new JLabel();
		subpanel.add(nobleGas, c);
		
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
			nobleGas.setIcon(null);
			results.setIcon(null);
			results.setText("");
			info.setText("");
			String input = enter.getText(), output = "";
			Element[] table = PeriodicTable.TABLE; 
			boolean done = false;
			int index = 0;
			
			if(num.isSelected())
			{
				try
				{
					index = Integer.parseInt(input);
					if(index < 1 || table.length < index)
					{
						results.setText(index + " is not a valid atomic number.");
					}
					else
					{
						Element e = table[index - 1];
						info.setText(e.getName());
						output += e.getEShell();
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
						index = element;
						output += table[element].getEShell();
						done = true;
						//results.setText(table[element].getEShell());
						found = true;
					}
				}
				if(!found)
				{
					results.setText(input + " is not a valid symbol.");
				}
			}
			if(name.isSelected())
			{
				boolean found = false;
				for(int element = 0; element < table.length && !found; element++)
				{
					if(table[element].getName().equals(input))
					{
						index = element;
						output += table[element].getEShell();
						done = true;
						//results.setText(table[element].getEShell());
						found = true;
					}
				}
				if(!found)
				{
					results.setText(input + " is not a valid element.");
				}
			}
			
			if (done) {
				TeXFormula formula = new TeXFormula (output);
				TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
						
				BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				icon.paintIcon(new JLabel(), image.getGraphics(), 0, 0);
				
				results.setIcon(icon);
				
				//Noble gas form:
				String form;
				if(index <= 2) return;
				else if(index < 10) form = "[He]";
				else if(index < 18) form = "[Ne]";
				else if(index < 36) form = "[Ar]";
				else if(index < 54) form = "[Kr]";
				else if(index < 96) form = "[Xe]";
				else form = "[Rn]";
				form += output.substring(output.lastIndexOf("s") - 1);
				
				TeXFormula formula2 = new TeXFormula (form);
				TeXIcon icon2 = formula2.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
						
				BufferedImage image2 = new BufferedImage(icon2.getIconWidth(), icon2.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				icon2.paintIcon(new JLabel(), image2.getGraphics(), 0, 0);
				
				nobleGas.setIcon(icon2);
			}
		}
	}
	
	public String getHelp()
	{
		return "<html>With the buttons on the side, select whether you are entering<br>" 
				+ "the atomic number, symbol, or name of the element. Then enter the<br>"
				+ "chosen information and press the button to see the electron shell<br>"
				+ "configuration for that element.</html>";
	}
}