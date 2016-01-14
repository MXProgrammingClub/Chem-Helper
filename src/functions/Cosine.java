package functions;
import java.util.ArrayList;

import evaluator.Node;
import evaluator.Operand;
import evaluator.Number;

public class Cosine extends Operand {

	public int getNumVals() {
		return 1;
	}
	
	public int getOrderOps(){return 1;}

	public double eval(ArrayList<Double> vals) {
		return Math.cos(vals.get(0));
	}

	public String toString(){return "cos";}

	public Node derive(ArrayList<Node> children, Character key)
	{
		return new Node(new Mul(), new Node(new Mul(), new Node(new Sine(), children.get(0), null), new Node(new Number(-1))), children.get(0).derive(key)); 
		//Chain rule
	}

	public void simplify(Node node) {
		return;
	}

}
