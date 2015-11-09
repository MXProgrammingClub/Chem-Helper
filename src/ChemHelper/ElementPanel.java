/*
 * A panel which creates the box for an element in the periodic table.
 * 
 * Author: Julia McClellan
 * Version: 11/04/2015
 */

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ElementPanel extends JPanel 
{
	private Element element;
	
	public ElementPanel(Element element)
	{
		this.element = element;
		setLayout(new BorderLayout());
		add(new JLabel("" + element.getNum()), BorderLayout.NORTH);
		add(new JLabel(element.getSymbol()), BorderLayout.CENTER);
		add(new JLabel(element.getName()), BorderLayout.SOUTH);
		setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 1), new EmptyBorder(6, 6, 6, 6)));
	}
	
	public Element getElement()
	{
		return element;
	}
}