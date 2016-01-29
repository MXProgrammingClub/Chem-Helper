/*
 * File: Prefs.java (I wanted Preferences.java but eclipse didn't let me)
 * Author: Luke Giacalone
 * Version: 01/29/2016
 * ----------------------------------------------------------------------
 * Holds the preferences for the ChemHelper program
 */

package Functions;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Prefs extends Function {
	
	private JPanel panel;
	private JCheckBox allowLukesTable;
	private JLabel tableMessage;
	
	public Prefs() {
		super("Preferences");
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		JLabel tablelabel = new JLabel("Enable Luke's Table");
		tablelabel.setPreferredSize(new Dimension(150, 10));
		subpanel.add(tablelabel, c);
		c.gridx = 1;
		allowLukesTable = new JCheckBox();
		allowLukesTable.setSelected(ChemHelper.ChemHelper.getPreference("allowLukesTable"));
		subpanel.add(allowLukesTable, c);
		c.gridx = 2;
		tableMessage = new JLabel("Your change will appear on restart of ChemHelper.");
		tableMessage.setVisible(false);
		subpanel.add(tableMessage, c);
		
		panel = new JPanel();
		panel.add(subpanel);
		
		allowLukesTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(allowLukesTable.isSelected())
						ChemHelper.ChemHelper.changePreference("allowLukesTable", true);
					else
						ChemHelper.ChemHelper.changePreference("allowLukesTable", false);
					tableMessage.setVisible(true);
				}
				catch(Throwable e2) {
					
				}
			}
		});
	}

	public JPanel getPanel() {
		return panel;
	}
	
}
