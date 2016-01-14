package functions;
import java.util.ArrayList;

import evaluator.Node;
import evaluator.Operand;

public class Sine extends Operand {

	public int getNumVals() {
		return 1;
	}
	
	public int getOrderOps(){return 1;}

	public double eval(ArrayList<Double> vals) {
		return Math.sin(vals.get(0));
	}

	public String toString(){return "sin";}

	public Node derive(ArrayList<Node> children, Character key) {
		return new Node(new Mul(), new Node(new Cosine(), children.get(0), null), children.get(0).derive(key));		//Chain rule
	}

	public void simplify(Node node) {
		return;
	}

}
