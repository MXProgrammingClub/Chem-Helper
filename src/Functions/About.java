/*
 * File: About.java
 * Author: Luke Giacalone
 * Date: 01/13/2016
 * ----------------------
 * This panel is the description of the authors
 */

package Functions;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class About extends Function {
	
	public JPanel panel;
	
	public About() {
		super("About ChemHelper");
		panel = new JPanel();
		Box box = Box.createVerticalBox();
		
		JLabel title = new JLabel();
		title.setFont(new Font(title.getFont().getName(), Font.BOLD, 28));
		title.setText("ChemHelper beta 0.5.0");
		box.add(title);
		
		box.add(Box.createVerticalStrut(15));
		
		box.add(new JLabel("Created by Julia McClellan, Luke Giacalone, Ted Pyne, and Hyun Choi as a part of Middlesex School"
				+ " Programming Club 2015-17. The program was designed"));
		box.add(new JLabel("to help chemistry students check their work when working on homework and preparing for tests."));
		
		box.add(Box.createVerticalStrut(15));
		
		box.add(new JLabel("<html><b>DISTRIBUTION OF THIS VERSION OF CHEMHELPER IS STRICTLY PROHIBITED!</b> Any distribution is breaking"
				+ " a Major School Rule and will result in disciplinary action.</html>"));
		
		box.add(Box.createVerticalStrut(15));
		
		box.add(new JLabel("ChemHelper is released under the BSD 3 License."));
		
		box.add(Box.createVerticalStrut(15));
		
		box.add(new JLabel("Copyright \u00A9 2016 Programming Club. All rights reserved."));
		
		panel.add(box);
	}

	public JPanel getPanel() {
		return panel;
	}
	
	public boolean help()
	{
		return false;
	}
	
	public String getHelp()
	{
		return null;
	}
}
