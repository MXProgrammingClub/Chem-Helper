package functions;

import java.util.ArrayList;

import evaluator.Number;

public class E extends Number{

	public E(double val) {
		super(val);
	}
	
	public E() {
		super(-1);
	}

	public double eval(ArrayList<Double> vals) {
		return Math.E;
	}
	
	public String toString(){return "e";}
}
