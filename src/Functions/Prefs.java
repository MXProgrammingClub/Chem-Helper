/*
 * File: Prefs.java (I wanted Preferences.java but eclipse didn't let me)
 * Author: Luke Giacalone
 * Version: 01/29/2016
 * ----------------------------------------------------------------------
 * Holds the preferences for the ChemHelper program
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ChemHelper.ChemHelper;

public class Prefs extends Function {
	
	private ChemHelper chelper;
	private JPanel panel;
	private JComboBox<String> tableStyles;
	private JCheckBox stateColors;
	
	private static final String[] TABLE_STYLES = {"Blank", "Color coded", "Color coded version 2"};
	
	public Prefs(ChemHelper ch) {
		super("Preferences");
		chelper = ch;
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		subpanel.add(new JLabel("<html><h3><u>Periodic Table Settings</u></h3></html>"), c);
		
		c.gridy = 1;
		subpanel.add(new JLabel("Table Style: "), c);
		c.gridx = 1;
		tableStyles = new JComboBox<String>(TABLE_STYLES);
		tableStyles.setSelectedIndex(chelper.getIntPreference("Table_Style"));
		subpanel.add(tableStyles, c);
		c.gridx = 0;
		c.gridy = 2;
		subpanel.add(new JLabel("Element Labels Colored by State: "), c);
		c.gridx = 1;
		stateColors = new JCheckBox();
		stateColors.setSelected(chelper.getBooleanPreference("State_Colors"));
		subpanel.add(stateColors, c);
		
		tableStyles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					chelper.changePreference("Table_Style", tableStyles.getSelectedIndex());
					chelper.refreshTable();
				}
				catch(Throwable e2) {}
			}
		});
		
		stateColors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					chelper.changePreference("State_Colors", stateColors.isSelected());
					chelper.refreshTable();
				}
				catch(Throwable e2) {}
			}
		});
		
		panel = new JPanel();
		panel.add(subpanel);
	}

	public JPanel getPanel() {
		return panel;
	}
	
}
