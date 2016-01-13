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
		title.setFont(new Font(title.getFont().getName(), Font.BOLD, 20));
		title.setText("ChemHelper");
		box.add(title);
		
		box.add(Box.createVerticalStrut(15));
		
		box.add(new JLabel("Created by Julia McClellan, Luke Giacalone, Hyun Choi, and Ted Pyne as a part of Middlesex School"
				+ " Computer Science Club 2015-16. The program was designed"));
		box.add(new JLabel("to help chemistry students check their work when working on homework and preparing for tests."));
		
		box.add(Box.createVerticalStrut(15));
		
		box.add(new JLabel("Copyright \u00A9 2016 MXCS Club. All rights reserved."));
		
		panel.add(box);
	}

	public JPanel getPanel() {
		return panel;
	}
	
}
