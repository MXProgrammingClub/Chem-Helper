package Functions;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public abstract class Function {
	public static final double C = 300000000, h = 6.626*Math.pow(10, -34);
	private String name;
	
	public Function(String name)
	{
		this.name = name;
	}
	
	public abstract JPanel getPanel();		//Return the frame containing all components for that chem function
	
	public String toString()
	{
		return name;
	}
	
	
	public static JPanel wrapInFlow(Component comp){			//Wrap a component in a FlowLayout
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(comp);
		return panel;
	}
	
	// Written by Lewis/Loftus/Cocking
	public static int gcd (int num1, int num2)
	   {
	      while (num1 != num2)
	         if (num1 > num2)
	            num1 = num1 - num2;
	         else
	            num2 = num2 - num1;

	      return num1;
	   }
}