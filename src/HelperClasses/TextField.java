/*
 * Text field for entering equations and compounds.
 * 
 * Author: Julia McClellan
 * Version: 1/23/2016
 */

package HelperClasses;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TextField extends JPanel
{
	private int index;
	private JLabel label;
	private String current;
	private Button sup, sub;
	private JButton arrow;
	
	public TextField()
	{
		current = "<html>|</html>";
		index = 6;
		label = new JLabel(current);
		label.setPreferredSize(new Dimension(200, 30));
		label.setBorder(BorderFactory.createLineBorder(Color.black));
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
		arrow = new JButton("\u2192");
		final TextField field = this;
		arrow.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						enter("\u2192");
						field.grabFocus();
					}
				});
		sup = new Button(this, "<sup>", "</sup>", "<html>a<sup>b</sup></html>");
		sub = new Button(this, "<sub>", "</sub>", "<html>a<sub>b</sub></html>");
		sup.addOther(sub);
		sub.addOther(sup);
		this.addKeyListener(new Key());
		JPanel buttons = new JPanel();
		buttons.add(arrow);
		buttons.add(sup);
		buttons.add(sub);
		setLayout(new GridLayout(1, 2));
		JPanel panel = new JPanel();
		panel.add(label);
		add(panel);
		add(buttons);
		setFocusable(true);
		addMouseListener(new MouseListener()
				{
					public void mouseClicked(MouseEvent arg0)
					{
						grabFocus();
					}
					public void mouseEntered(MouseEvent arg0){} public void mouseExited(MouseEvent arg0) {}
					public void mousePressed(MouseEvent arg0){} public void mouseReleased(MouseEvent arg0) {}
				});
	}
	
	public String getText()
	{
		return current.substring(0, index) + current.substring(index + 1);
	}
	
	private void enter(String enter)
	{
		current = current.substring(0, index) + enter + current.substring(index);
		index += enter.length();
		label.setText(current);
	}
	
	private class Key implements KeyListener
	{
		public void keyPressed(KeyEvent arg0)
		{
			if(arg0.getKeyCode() == 8) //backspace
			{
				int goBack = checkBack();
				if(goBack == 0) return;
				if(goBack == 5)
				{
					int end = current.indexOf("</", index);
					if(end != -1 && end != current.length() - 7) current = current.substring(0, end) + current.substring(end + 6);
					else if(current.substring(index - goBack, index).equals("<sub>")) sub.turnOff();
					else sup.turnOff();
				}
				else if(goBack != 1 && !current.substring(index - goBack, index).equals("\u2192"))
				{
					int start = current.lastIndexOf(">", index - goBack);
					current = current.substring(0, start - 4) + current.substring(start + 1);
					index -= 5;
				}
				current = current.substring(0, index - goBack) + current.substring(index);
				index = index - goBack;
				label.setText(current);
			}
			else if(arg0.getKeyCode() == 127) //delete
			{
				int goAhead = checkAhead();
				if(goAhead == 0) return;
				if(goAhead == 5)
				{
					int end = current.indexOf("</", index);
					if(end != -1 && end != current.length() - 7) current = current.substring(0, end) + current.substring(end + 6);
					else if(current.substring(index + 1, index + 1 + goAhead).equals("<sub>")) sub.turnOff();
					else sup.turnOff();
				}
				else if(!current.substring(index + 1, index + 1 + goAhead).equals("\u2192"))
				{
					int end = current.lastIndexOf("<s", index);
					if(end != -1)
					{
						current = current.substring(0, end) + current.substring(end + 5);
						index -= 5;
					}
				}
				current = current.substring(0, index + 1) + current.substring(index + 1 + goAhead);
				label.setText(current);
			}
			else if(arg0.getKeyCode() == 39) //right arrow
			{
				int newIndex = index + checkAhead();
				if(newIndex != index)
				{
					current = current.substring(0, index) + current.substring(index + 1, newIndex + 1) + '|' + current.substring(newIndex + 1);
					index = newIndex;
					label.setText(current);
				}
				else if(sub.isOn()) sub.toggle();
				else if(sup.isOn()) sup.toggle();
			}
			else if(arg0.getKeyCode() == 37) //left arrow
			{
				int newIndex = index - checkBack();
				if(newIndex != index)
				{
					current = current.substring(0, newIndex ) + '|' + current.substring(newIndex, index) + current.substring(index + 1);
					index = newIndex;
					label.setText(current);
				}
			}
			else if(arg0.getKeyCode() == 36) //home
			{
				current = "<html>|" + current.substring(6, index) + current.substring(index + 1);
				index = 6;
				label.setText(current);
			}
			else if(arg0.getKeyCode() == 35) //end
			{
				current = current.substring(0, index) + current.substring(index + 1, current.length() - 7) + "|</html>";
				index = current.length() - 8;
				label.setText(current);
			}
		}
		
		public void keyTyped(KeyEvent arg0)
		{
			char ch = arg0.getKeyChar();
			if(ch != (char) 8 && ch != (char) 127 && ch != (char) 27 && ch != (char) 10) //backspace, delete, escape, and enter call this but shouldn't
			{
				if(ch == '^' && !sub.isOn()) sup.toggle();
				else if(ch == '_' && !sup.isOn()) sub.toggle();
				else if(ch == '>' && current.charAt(index - 1) == '-') 
				{
					current = current.substring(0, index - 1) + current.substring(index);
					index--;
					enter("\u2192");
				}
				else if(ch != '^' && ch != '_') enter(ch + "");
			}
		}
		public void keyReleased(KeyEvent arg0){} 
		
		private int checkAhead()
		{
			if(index == current.length() - 8) return 0;
			if(current.substring(index + 1, index + 7).equals("\u2192")) return 6;
			if(current.substring(index + 1, index + 6).equals("<sup>")) return 5;
			if(current.substring(index + 1, index + 6).equals("<sub>")) return 5;
			if(current.substring(index + 1, index + 7).equals("</sup>")) return 6;
			if(current.substring(index + 1, index + 7).equals("</sub>")) return 6;
			return 1;
		}
		
		private int checkBack()
		{
			if(index == 6) return 0;
			if(current.substring(index - 6, index).equals("\u2192")) return 6;
			if(current.substring(index - 5, index).equals("<sup>")) return 5;
			if(current.substring(index - 5, index).equals("<sub>")) return 5;
			if(current.substring(index - 6, index).equals("</sup>")) return 6;
			if(current.substring(index - 6, index).equals("</sub>")) return 6;
			return 1;
		}
	}
	
	private class Button extends JButton
	{
		private String add1, add2;
		private boolean on;
		private TextField field;
		private Button other;
		
		public Button(TextField field, String add1, String add2, String display)
		{
			super(display);
			this.add1 = add1;
			this.add2 = add2;
			on = false;
			this.field = field;
			addActionListener(new ButtonListener());
		}
		
		public void addOther(Button other)
		{
			this.other = other;
		}
		
		public void toggle()
		{
			if(!other.isOn())
			{
				if(!on) field.enter(add1);
				else if(current.substring(index - 5, index).equals(add1))
				{
					current = current.substring(0, index - 5) + current.substring(index);
					index -= 5;
					label.setText(current);
				}
				else field.enter(add2);
				on = !on;
			}
			field.grabFocus();
		}
		
		public void turnOff()
		{
			on = false;
		}
		
		public boolean isOn()
		{
			return on;
		}
		
		private class ButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				toggle();
			}
		}
	}
}