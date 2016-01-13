/*
 * File: Nuclear.java
 * Author: Luke Giacalone
 * Version: 01/11/2015
 * ----------------------
 * Nuclear stuff that chemistry learned.
 */

package Functions;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class Nuclear extends Function {
	
	private ArrayList<String> SYMBOLS =  new ArrayList<String>();
	private JPanel panel;
	private JPanel input;
	private JPanel steps;
	private JTextField mass;
	private JTextField number;
	private JTextField symbol;
	private JButton alpha;
	private JButton beta;
	private JButton electron;
	private JButton positron;
	private JLabel equation;
	private String latext;
	private boolean somethingDisplayed;
	
	public Nuclear() {
		super("Nuclear Chemistry");
		
		for(String s: PeriodicTable.SYMBOLS)
			SYMBOLS.add(s);
		somethingDisplayed = false;
		latext = "";
		
		panel = new JPanel();
		
		input = new JPanel();
		input.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		mass = new JTextField();
		mass.setPreferredSize(new Dimension(40, 35));
		mass.setHorizontalAlignment(JTextField.CENTER);
		input.add(mass, c);
		
		c.gridy = 1;
		number = new JTextField();
		number.setPreferredSize(new Dimension(40, 35));
		number.setHorizontalAlignment(JTextField.CENTER);
		input.add(number, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;
		symbol = new JTextField();
		symbol.setPreferredSize(new Dimension(70, 70));
		symbol.setFont(new Font(symbol.getFont().getName(), 0, 24));
		symbol.setHorizontalAlignment(JTextField.CENTER);
		input.add(symbol, c);
		
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		alpha = new JButton("Alpha Particle");
		input.add(alpha, c);
		
		c.gridx = 3;
		c.gridy = 1;
		beta = new JButton("Beta Particle");
		input.add(beta, c);
		
		c.gridx = 4;
		c.gridy = 0;
		electron = new JButton("Electron Capture");
		input.add(electron, c);
		
		c.gridx = 4;
		c.gridy = 1;
		positron = new JButton("Positron");
		input.add(positron, c);
		
		steps = new JPanel();
		equation = new JLabel();
		steps.add(equation, c);
		
		panel.add(input);
		panel.add(steps);
		
		//decays the element using an alpha particle
		alpha.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!somethingDisplayed) latex2(null, false);
				decayAlpha();
				latex2("\\alpha+", false);
			}
		});
		
		//decays the element using an beta particle
		beta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!somethingDisplayed) latex2(null, false);
				decayBeta();
				latex2("\\beta+", false);
			}
		});
		
		//decays the element using electron capture
		electron.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!somethingDisplayed) latex2(null, false);
				captureElectron();
				latex2("+e^-", true);
			}
		});
		
		//decays the element using an positron
		positron.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!somethingDisplayed) latex2(null, false);
				decayPositron();
				latex2("\\beta^-+", false);
			}
		});
	}

	public JPanel getPanel() {
		return panel;
	}
	
	private void decayAlpha() {
		try {
			mass.setText("" + (Integer.parseInt(mass.getText()) - 4));
			number.setText("" + (Integer.parseInt(number.getText()) - 2));
			symbol.setText(SYMBOLS.get(SYMBOLS.indexOf(symbol.getText()) - 2));
		}
		catch(Throwable e) {
			JOptionPane.showMessageDialog(input, "The mass and number of protons need to be an integers!");
		}
	}
	
	private void captureElectron() {
		try {
			number.setText("" + (Integer.parseInt(number.getText()) - 1));
			symbol.setText(SYMBOLS.get(SYMBOLS.indexOf(symbol.getText()) - 1));
		}
		catch(Throwable e) {
			JOptionPane.showMessageDialog(input, "The mass and number of protons need to be an integers!");
		}
	}
	
	private void decayBeta() {
		try {
			number.setText("" + (Integer.parseInt(number.getText()) + 1));
			symbol.setText(SYMBOLS.get(SYMBOLS.indexOf(symbol.getText()) + 1));
		}
		catch(Throwable e) {
			JOptionPane.showMessageDialog(input, "The mass and number of protons need to be an integers!");
		}
	}
	
	private void decayPositron() {
		try {
			number.setText("" + (Integer.parseInt(number.getText()) - 1));
			symbol.setText(SYMBOLS.get(SYMBOLS.indexOf(symbol.getText()) - 1));
		}
		catch(Throwable e) {
			JOptionPane.showMessageDialog(input, "The mass and number of protons need to be an integers!");
		}
	}
	
	private void latex2(String particle, boolean isFirst) {
		if(somethingDisplayed) 
			if(isFirst)
				latext += particle + "\\rightarrow";
			else
				latext += "\\rightarrow " + particle;
		else
			somethingDisplayed = true;
		
		latext += "\\substack{" + mass.getText() + " \\\\ " + number.getText() + "}" + symbol.getText();
		
		TeXFormula formula = new TeXFormula (latext);
		TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
				
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(new JLabel(), image.getGraphics(), 0, 0);
		
		equation.setIcon(icon);
	}
	
}
