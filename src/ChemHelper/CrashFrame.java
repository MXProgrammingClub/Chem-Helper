/*
 * File: CrashFrame.java
 * 
 * If the program receives an exception that is not handled in a function, this fram will show and ask the user
 * to send a bug report to the ChemHelper staff.
 * 
 * Author: Luke Giacalone
 * Version: 02/13/2016
 */

package ChemHelper;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CrashFrame extends JFrame {
	
	private JPanel panel;
	private JButton send, cancel;
	private JTextArea explainArea;
	
	public CrashFrame(final Throwable exception, final StackTraceElement[] stack) {
		this.setTitle("ChemHelper");
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("<html><h1>Uh Oh!</h1></html>"), c);
		c.gridy = 1;
		panel.add(new JLabel("<html>ChemHelper has crashed! Please send a report so we can fix the problem:"
				+ "<br>https://goo.gl/forms/F0yj4206Jrx2NkT23</html>"), c);
		
		/*c.gridy++;
		panel.add(new JLabel("<html><br>Please explain exactly what you were doing at the time of the crash.<br>"
				+ "Include any data you were using.</html>"), c);
		
		explainArea = new JTextArea();
		JScrollPane explainPane = new JScrollPane();
		explainPane.setPreferredSize(new Dimension(400, 100));
		explainPane.setViewportView(explainArea);
		explainPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		explainArea.setLineWrap(true);
		c.gridy++;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(explainPane, c);*/
		
		c.gridy++;
		c.anchor = GridBagConstraints.WEST;
		panel.add(new JLabel("<html><br>Please include this in your report:	</html>"), c);
		
		JTextArea exceptionTextArea = new JTextArea();
		String exceptionText = "";
		exceptionText += "" + exception;
		for(StackTraceElement e: stack)
			exceptionText += "\n    " + e;
		JScrollPane exceptionPane = new JScrollPane();
		exceptionPane.setPreferredSize(new Dimension(400, 100));
		exceptionTextArea.setText(exceptionText);
		exceptionTextArea.setEditable(false);
		exceptionPane.setViewportView(exceptionTextArea);
		c.anchor = GridBagConstraints.CENTER;
		c.gridy++;
		panel.add(exceptionPane, c);
		
		send = new JButton(/*"Send Report"*/ "Open Form");
		cancel = new JButton("Cancel");
		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		panel.add(send, c);
		/*c.anchor = GridBagConstraints.WEST;
		panel.add(cancel, c);*/
		
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*Desktop desktop = Desktop.getDesktop();
				String message = "mailto:jmcclellan@mxschool.edu,lrgiacalone@mxschool.edu?subject=ChemHelper%20Error&body=";
				message += "EXCEPTION:%0A%0A";
				message += removeSpace(exception.toString());
				for(StackTraceElement s: stack)
					message += "%0A%09" + removeSpace(s.toString());
				message += "%0A%0ADESCRIPTION:%0A%0A";
				message += removeSpace(explainArea.getText());
				URI uri = URI.create(message);
				try {
					desktop.mail(uri);
				} catch (IOException e1) {}
				dispose();
				System.exit(0);*/
				try {
					Desktop.getDesktop().browse(new URI("https://goo.gl/forms/F0yj4206Jrx2NkT23"));
				} catch (IOException | URISyntaxException e1) {}
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		
		this.add(panel);
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	private static String removeSpace(String str) {
		while(str.indexOf(' ') >= 0)
			str = str.substring(0, str.indexOf(' ')) + "%20" + str.substring(str.indexOf(' ') + 1);
		return str;
	}
	
}
