package functions;

import java.util.ArrayList;

import evaluator.Number;

public class Pi extends Number{

	public Pi(double val) {
		super(val);
	}
	
	public Pi() {
		super(-1);
	}

	public double eval(ArrayList<Double> vals) {
		return Math.PI;
	}
	
	public String toString(){return "pi";}
}
