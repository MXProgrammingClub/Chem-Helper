package Functions; 
import javax.swing.*;

import Elements.*;

import java.awt.*;

public class PeriodicTable extends JPanel
{
	private ElementPanel[][] panels, alPanels; //Panels is all elements but actinides and lanthandies, which are in alPanels
	
	public PeriodicTable()
	{
		panels = new ElementPanel[7][18];
		alPanels = new ElementPanel[2][14];
		for(int row = 0; row < panels.length; row++)
		{
			for(int col = 0; col < panels[0].length; col++)
			{
				panels[row][col] = new ElementPanel(null); //To avoid null pointer exceptions later
			}
		}
		
		for(Element e: TABLE)
		{
			ElementPanel panel = new ElementPanel(e);
			if(e.getPeriod() > 0)
			{
				
			}
			else
			{
				panels[e.getPeriod()][e.getGroup()] = panel;
			}
		}
		
		setLayout(new GridLayout(7, 18));
		for(int row = 0; row < panels.length; row++)
		{
			for(int col = 0; col < panels[0].length; col++)
			{
				add(panels[row][col]);
			}
		}
		
	}
	
	public static void main(String[] args)
	{
		PeriodicTable table = new PeriodicTable();
		JFrame frame = new JFrame();
		frame.getContentPane().add(table);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.pack();
	    frame.setVisible(true);
		
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
			new Zirconium()
			/*,
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
			*/
		};
}