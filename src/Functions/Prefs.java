/*
 * File: Prefs.java (I wanted Preferences.java but eclipse didn't let me)
 * Author: Luke Giacalone
 * Edited: Julia McClellan - added significant figures preference
 * Version: 2/20/2016
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
	private JComboBox<String> tableStyles, sigFigFormat;
	private JCheckBox stateColors;
	
	private static final String[] TABLE_STYLES = {"Blank", "Color Coded 1", "Color Coded 2"}, SIG_FIGS = {"None", "Standard", "Scientific Notation"};
	
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
		
		c.gridy++;
		c.gridx = 0;
		subpanel.add(new JLabel("Significant Figures Format:"), c);
		c.gridx++;
		sigFigFormat = new JComboBox<String>(SIG_FIGS);
		sigFigFormat.setSelectedIndex(chelper.getIntPreference("Sig_Figs"));
		subpanel.add(sigFigFormat, c);
		
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
		
		sigFigFormat.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					int pref = sigFigFormat.getSelectedIndex();
					chelper.changePreference("Sig_Figs", pref);
					Function.setSigFigPref(pref);
				}
			});
		
		panel = new JPanel();
		panel.add(subpanel);
	}

	public JPanel getPanel() {
		return panel;
	}
	
}
