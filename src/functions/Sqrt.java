package functions;

import java.util.ArrayList;

import evaluator.Node;
import evaluator.Operand;
import evaluator.Number;

public class Sqrt extends Operand {

	public int getNumVals() {
		return 1;
	}
	
	public int getOrderOps(){return 1;}

	public double eval(ArrayList<Double> vals) {
		return Math.sqrt(vals.get(0));
	}

	public String toString(){return "sqrt";}

	@Override
	public Node derive(ArrayList<Node> children, Character key) {
		Node xn = new Node(new Exp(), children.get(0), new Node(new Number(-.5)));
		return new Node(new Mul(), new Node(new Number(.5)), xn);
	}

	@Override
	public void simplify(Node node) {
		return;
	}

}
