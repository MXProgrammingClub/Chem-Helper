package Functions;
import javax.swing.*;
import javax.swing.border.*;

import Elements.Element;

import java.awt.*;

public class ElementPanel extends JPanel 
{
	private Element element;
	
	public ElementPanel(Element element)
	{
		this.element = element;
		if(element != null)
		{
			String name = element.getSymbol();
			if(element.getName().equals("Lanthanum"))
			{
				name = "*" + name;
			}
			if(element.getName().equals("Barium"))
			{
				name += "*";
			}
			if(element.getName().equals("Actinium"))
			{
				name = "**" + name;
			}
			if(element.getName().equals("Radium"))
			{
				name += "**";
			}
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.ipady = -5;
			c.gridy = 0;
			add(new JLabel("" + element.getNum()), c);

			c.gridy = 1;
			add(new JLabel("<html><h3>" + name + "</h3></html>"), c);
			
			c.gridy = 2;
			c.ipady = 0;
			add(new JLabel("<html><font size=\"1\">" + element.getName() + "</font></html>"), c);
			
			setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 1), new EmptyBorder(3, 3, 3, 3)));
			
			/*add(new JLabel("<html>" + element.getNum() + "<br><center><h1>" + name + "</h1><br>" + element.getName() + "</center></html>"));
			setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 1), new EmptyBorder(6, 6, 6, 6)));
			setSize(50, 50);*/
		}
	}
	
	public Element getElement()
	{
		return element;
	}
}
