/*
 * File: StateChangeTemp.java
 * 
 * This function calculates the change in temperature in a state change based on the number of particles in solution
 * (boiling point elevation and freezing point depression)
 * 
 * Author: Luke Giacalone
 * Version: 02/02/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import HelperClasses.EnterField;

public class StateChangeTemp extends Function {
	
	private static final String[] TEMPERATURE_UNITS = {"K", "\u2103", "\u2109"};
	private static final String[] MOLE_UNITS = {"mol"};
	private static final String[] MASS_UNITS = {"pg", "ng", "\u00B5g", "mg", "cg", "dg", "g", "dag", "hg", "kg", "Mg", "Tg", "Gg"};
	private static final int[] MASS_POWERS = {-12, -9, -6, -3, -2, -1, 0, 1, 2, 3, 6, 9, 12};
	
	private JPanel panel, boilingPanel, freezingPanel;
	private JRadioButton boilingButton, freezingButton, togetherI, seperatedI;
	private EnterField deltaTb, i_b, molIon_b, molSolute_b, kb, mb, deltaTf, i_f, molIon_f, molSolute_f, kf, mf;
	private JButton calculate;
	
	public StateChangeTemp() {
		super("State Change Temperature");
		
		Box buttonBox = Box.createHorizontalBox();
		boilingButton = new JRadioButton("Boiling Point Elevation");
		freezingButton = new JRadioButton("Freezing Point Depression");
		boilingButton.setSelected(true);
		freezingButton.setSelected(false);
		buttonBox.add(boilingButton);
		buttonBox.add(freezingButton);
		
		Box iBox = Box.createHorizontalBox();
		togetherI = new JRadioButton("<html><i>i</i> as One Value</html>");
		seperatedI = new JRadioButton("<html><i>i</i> as Seperated Values</html>");
		togetherI.setSelected(true);
		seperatedI.setSelected(false);
		iBox.add(togetherI);
		iBox.add(seperatedI);
		
		boilingPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		deltaTb = new EnterField("\u0394t", TEMPERATURE_UNITS);
		i_b = new EnterField("<html><i>i</i></html>");
		molIon_b = new EnterField("Ions", MOLE_UNITS);
		molSolute_b = new EnterField("Solute", MOLE_UNITS);
		kb = new EnterField("<html>k<sub>b</sub></html>");
		mb = new EnterField("molality", MOLE_UNITS, MASS_UNITS);
		c.gridx = 0;
		c.gridy = 0;
		boilingPanel.add(deltaTb, c);
		c.gridy = 1;
		boilingPanel.add(i_b, c);
		c.gridy = 2;
		boilingPanel.add(molIon_b, c);
		c.gridy = 3;
		boilingPanel.add(molSolute_b, c);
		c.gridy = 4;
		boilingPanel.add(kb, c);
		c.gridy = 5;
		boilingPanel.add(mb, c);
		boilingPanel.setVisible(true);
		i_b.setVisible(true);
		molIon_b.setVisible(false);
		molSolute_b.setVisible(false);
		mb.setUnit2(6);
		
		freezingPanel = new JPanel(new GridBagLayout());
		deltaTf = new EnterField("\u0394t", TEMPERATURE_UNITS);
		i_f = new EnterField("<html><i>i</i></html>");
		molIon_f = new EnterField("Ions", MOLE_UNITS);
		molSolute_f = new EnterField("Solute", MOLE_UNITS);
		kf = new EnterField("<html>k<sub>f</sub></html>");
		mf = new EnterField("molality", MOLE_UNITS, MASS_UNITS);
		c.gridx = 0;
		c.gridy = 0;
		freezingPanel.add(deltaTf, c);
		c.gridy = 1;
		freezingPanel.add(i_f, c);
		c.gridy = 2;
		freezingPanel.add(molIon_f, c);
		c.gridy = 3;
		freezingPanel.add(molSolute_f, c);
		c.gridy = 4;
		freezingPanel.add(kf, c);
		c.gridy = 5;
		freezingPanel.add(mf, c);
		freezingPanel.setVisible(false);
		i_f.setVisible(true);
		molIon_f.setVisible(false);
		molSolute_f.setVisible(false);
		mf.setUnit2(6);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		subpanel.add(buttonBox, c);
		c.gridy = 1;
		subpanel.add(iBox, c);
		c.gridy = 2;
		subpanel.add(boilingPanel, c);
		subpanel.add(freezingPanel, c);
		c.gridy = 3;
		subpanel.add(calculate, c);
		
		panel = new JPanel();
		panel.add(subpanel);
		
		boilingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(freezingButton.isSelected()) {
					freezingButton.setSelected(false);
					freezingPanel.setVisible(false);
					boilingPanel.setVisible(true);
				}
				else
					boilingButton.setSelected(true);
			}
		});
		freezingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(boilingButton.isSelected()) {
					boilingButton.setSelected(false);
					boilingPanel.setVisible(false);
					freezingPanel.setVisible(true);
				}
				else
					freezingButton.setSelected(true);
			}
		});
		togetherI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(seperatedI.isSelected()) {
					seperatedI.setSelected(false);
					i_b.setVisible(true);
					molIon_b.setVisible(false);
					molSolute_b.setVisible(false);
					i_f.setVisible(true);
					molIon_f.setVisible(false);
					molSolute_f.setVisible(false);
				}
				else
					togetherI.setSelected(true);
			}
		});
		seperatedI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(togetherI.isSelected()) {
					togetherI.setSelected(false);
					i_b.setVisible(false);
					molIon_b.setVisible(true);
					molSolute_b.setVisible(true);
					i_f.setVisible(false);
					molIon_f.setVisible(true);
					molSolute_f.setVisible(true);
				}
				else
					seperatedI.setSelected(true);
			}
		});
	}
	
	private class Calculate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
}
