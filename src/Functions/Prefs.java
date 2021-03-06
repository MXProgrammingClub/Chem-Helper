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
	
	private static final String[] TABLE_STYLES = {"Blank", "Color Coded 1", "Color Coded 2"};
	private static final String[] SIG_FIGS = {"None", "Standard", "Scientific Notation"};
	
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
		stateColors.setSelected(chelper.getBooleanPreference("Table_State_Colors"));
		subpanel.add(stateColors, c);
		
		c.gridy++;
		c.gridx = 0;
		subpanel.add(new JLabel("<html><h3><br><u>Significant Figure Settings</u></h3></html>"), c);
		
		c.gridy++;
		c.gridx = 0;
		subpanel.add(new JLabel("Format: "), c);
		c.gridx++;
		sigFigFormat = new JComboBox<String>(SIG_FIGS);
		sigFigFormat.setSelectedIndex(chelper.getIntPreference("SigFigs_Format"));
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
					chelper.changePreference("Table_State_Colors", stateColors.isSelected());
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
					chelper.changePreference("SigFigs_Format", pref);
					Function.setSigFigPref(pref);
				}
			});
		
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	public String getHelp()
	{
		return "<html>Periodic table settings: Choose blank backgrounds for<br>"
				+ "elements or from two color coded options. Additionally,<br>"
				+ "choose whether elements symbols should be colored<br>"
				+ "based on state.<br>"
				+ "Significant figures settings: \"None\" will display all<br>"
				+ "numbers without rounding, \"Scientific Notation\" will<br>"
				+ "display all numbers rounded in scientific notation, and<br>"
				+ "\"Standard\" will display rounded numbers, using<br>"
				+ "scientific notation if necessary to maintain the correct<br>"
				+ "number of significant figures.</html>";
	}

	public JPanel getPanel() {
		return panel;
	}	
}