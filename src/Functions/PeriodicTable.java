/*
 * Displays the periodic table and information about the currently selected element. Has static methods to search for elements in the table.
 * 
 * Authors: Luke Giacalone, Julia McClellan
 * Version: 1/28/2016
 */

package Functions; 

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import Elements.*;

public class PeriodicTable extends Function
{
	private ElementPanel[][] panels, alPanels; //Panels is all elements but actinides and lanthandies, which are in alPanels
	private JPanel panel, alPanel, table, bottomPanel, strut;
	private JLabel info;
	
	public PeriodicTable(int type) //0 = blank, 1 = colors
	{
		super((type == 1) ? "Color Coded" : "Blank");
		panels = new ElementPanel[7][18];
		alPanels = new ElementPanel[2][14];
		for(int row = 0; row < panels.length; row++)
		{
			for(int col = 0; col < panels[0].length; col++)
			{
				panels[row][col] = new ElementPanel(null); //To avoid null pointer exceptions later
			}
		}
		
		int laCol = 0;
		for(Element e: TABLE)
		{
			ElementPanel panel = new ElementPanel(e, type);
			if(e.getGroup() < 0)
			{
				alPanels[(e.getGroup() * -1) - 1][laCol] = panel;
				laCol++;
				if(laCol >= 14)
				{
					laCol = 0;
				}
			}
			else
			{
				panels[e.getPeriod() - 1][e.getGroup() - 1] = panel;
			}
		}
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(7, 18));
		alPanel = new JPanel();
		alPanel.setLayout(new GridLayout(2, 14));
		for(int row = 0; row < panels.length; row++)
		{
			for(int col = 0; col < panels[0].length; col++)
			{
				panel.add(panels[row][col]);
				panels[row][col].addMouseListener(new EListener());
			}
		}
		for(int row = 0; row < alPanels.length; row++)
		{
			for(int col = 0; col < alPanels[0].length; col++)
			{
				alPanel.add(alPanels[row][col]);
				alPanels[row][col].addMouseListener(new EListener());
			}
		}
		info = new JLabel("Click an element to find out about it.");
		info.setPreferredSize(new Dimension(245, 125));
		info.setForeground(Color.black);
		strut = new JPanel();
		strut.setPreferredSize(new Dimension(15, 125));
		
		bottomPanel = new JPanel();
		bottomPanel.add(alPanel);
		bottomPanel.add(strut);
		bottomPanel.add(info);
		
		Box box = Box.createVerticalBox();
		box.add(panel);
		box.add(Box.createVerticalStrut(20));
		box.add(bottomPanel);

		table = new JPanel();
		table.add(box);
		table.setSize(900, 590);
	}
	
	public JPanel getPanel()
	{
		return table;
	}

	public static Element[] getTable()
	{
		return TABLE;
	}
	
	public static Element find(String name)
	{
		if(name.length() <= 3)
		{
			for(int index = 0; index < TABLE.length; index++)
			{
				if(TABLE[index].getSymbol().equals(name)) return TABLE[index];
			}
		}
		else
		{
			for(int index = 0; index < TABLE.length; index++)
			{
				if(TABLE[index].getName().equals(name)) return TABLE[index];
			}
		}
		return null;
	}
	
	private class EListener implements MouseListener
	{
		public void mouseClicked(MouseEvent arg0) 
		{
			if(arg0.getSource() instanceof ElementPanel)
			{
				Element e = ((ElementPanel) arg0.getSource()).getElement();
				if(e != null)
				{
					String text = "<html>Element: " + e.getName() + "<br>Atomic Mass: " + e.getMolarMass() + " amu" + 
							"<br>Family: " + e.getGroupName() + "<br>Type: " + e.getMetal() + "<br>State at room temperature: " + e.getState() + 
							"<br>Boiling Point: ";
					if(e.getBoil() == Double.MAX_VALUE || e.getBoil() == 0)
					{
						text += "Unknown";
					}
					else
					{
						text += e.getBoil() + " K";
					}
					text += "<br>Freezing Point: ";
					if(e.getFreeze() == Double.MAX_VALUE || e.getFreeze() == 0)
					{
						text += "Unknown";
					}
					else
					{
						text += e.getFreeze() + " K";
					}
					text += "<br>Density: ";
					if(e.getDense() == Double.MAX_VALUE || e.getDense() == 0)
					{
						text += "Unknown";
					}
					else
					{
						text += e.getDense() + " g/mL";
					}
					text += "</html";
					info.setText(text);
				}
			}
		}
		public void mouseEntered(MouseEvent arg0){}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}
	}
	
	private class ElementPanel extends JPanel 
	{
		private Element element;
		
		public ElementPanel(Element element, int type) //0 = blank, 1 = julia, 2 = luke
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
				add(new JLabel("<html><font color=\"black\">" + element.getNum()), c);

				c.gridy = 1;
				JLabel symbol = new JLabel("<html><h3>" + name + "</h3></html>");
				symbol.setForeground(Color.black);
				if(type != 0)
				{
					if(element.getState().equals("Liquid"))
						symbol.setForeground(Color.BLUE);
					else if(element.getState().equals("Gas"))
						symbol.setForeground(Color.RED);
				}
				add(symbol, c);
				
				c.gridy = 2;
				c.ipady = 0;
				add(new JLabel("<html><font size=\"1\" color =\"black\">" + element.getName() + "</font></html>"), c);
				
				setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 1), new EmptyBorder(3, 3, 3, 3)));
				
				if(type == 1) {
					if(element.getGroupName().equals("Transition Metal")) setBackground(new Color(223, 255, 204));
					else if(element.getGroupName().equals("Actinide") || element.getGroupName().equals("Lanthanide")) setBackground(new Color(255, 202, 191));
					else if(element.getMetal().equals("Metal")) setBackground(new Color(255, 255, 185));
					else if(element.getMetal().equals("Non-metal")) setBackground(new Color(238, 203, 255));
					else setBackground(new Color(207, 243, 243));
				}
			}
		}
		
		public ElementPanel(Element element) {
			this(element, 0);
		}
		
		public Element getElement()
		{
			return element;
		}
	}
	
	public static final Element[] TABLE = {
			new Hydrogen(),
			new Helium(),
			new Lithium(),
			new Beryllium(),
			new Boron(),
			new Carbon(), 
			new Nitrogen(),
			new Oxygen(),
			new Fluorine(),
			new Neon(),
			new Sodium(),
			new Magnesium(),
			new Aluminum(),
			new Silicon(),
			new Phosphorus(),
			new Sulfur(),
			new Chlorine(),
			new Argon(),
			new Potassium(),
			new Calcium(),
			new Scandium(),
			new Titanium(),
			new Vanadium(),
			new Chromium(),
			new Manganese(),
			new Iron(),
			new Cobalt(),
			new Nickel(),
			new Copper(),
			new Zinc(),
			new Gallium(),
			new Germanium(),
			new Arsenic(),
			new Selenium(),
			new Bromine(),
			new Krypton(),
			new Rubidium(),
			new Strontium(),
			new Yttrium(),
			new Zirconium(),
			new Niobium(),
			new Molybdenum(),
			new Technetium(),
			new Ruthenium(),
			new Rhodium(),
			new Palladium(),
			new Silver(),
			new Cadmium(),
			new Indium(),
			new Tin(),
			new Antimony(),
			new Tellurium(),
			new Iodine(),
			new Xenon(),
			new Cesium(),
			new Barium(),
			new Lanthanum(),
			new Cerium(),
			new Praseodymium(),
			new Neodymium(),
			new Promethium(),
			new Samarium(),
			new Europium(),
			new Gadolinium(),
			new Terbium(),
			new Dysprosium(),
			new Holmium(),
			new Erbium(),
			new Thulium(),
			new Ytterbium(),
			new Lutetium(),
			new Hafnium(),
			new Tantalum(),
			new Tungsten(),
			new Rhenium(),
			new Osmium(),
			new Iridium(),
			new Platnium(),
			new Gold(),
			new Mercury(),
			new Thallium(),
			new Lead(),
			new Bismuth(),
			new Polonium(),
			new Astatine(),
			new Radon(),
			new Francium(),
			new Radium(),
			new Actinium(),
			new Thorium(),
			new Protactinium(),
			new Uranium(),
			new Neptunium(),
			new Plutonium(),
			new Americium(),
			new Curium(),
			new Berkelium(),
			new Californium(),
			new Einsteinium(),
			new Fermium(),
			new Mendelevium(),
			new Nobelium(),
			new Lawrencium(),
			new Rutherfordium(),
			new Dubnium(),
			new Seaborgium(),
			new Bohrium(),
			new Hassium(), 
			new Meitnerium(),
			new Darmstadtium(),
			new Roentgenium(),
			new Copernicium(),
			new Ununtrium(),
			new Flerovium(),
			new Ununpentium(),
			new Livermorium(), 
			new Ununseptium(),
			new Ununoctium()
		};
	
	public static final String[] SYMBOLS = {"H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne", "Na", "Mg", "Al", "Si", "P",
			"S", "Cl", "Ar", "K", "Ca", "Sc", "Ti", "Va", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn", "Ga", "Ge", "As", "Se", "Br",
			"Kr", "Rb", "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "In", "Sn", "Sb", "Te", "I", "Xe", "Cs",
			"Ba", "La", "Ce", "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb", "Lu", "Hf", "Ta", "W",
			"Re", "Os", "Ir", "Pt", "Au", "Hg", "Tl", "Pb", "Bi", "Po", "At", "Rn", "Fr", "Ra", "Ac", "Th", "Pa", "U", "Np",
			"Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr", "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg", "Cn",
			"Uut", "Fl", "Uup", "Lv", "Uus", "Uuo"};

}