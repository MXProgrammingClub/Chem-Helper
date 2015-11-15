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
			
			add(new JLabel(name));
			setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 1), new EmptyBorder(6, 6, 6, 6)));
		}
	}
	
	public Element getElement()
	{
		return element;
	}
}
