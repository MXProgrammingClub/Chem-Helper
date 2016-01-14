package functions;

import java.util.ArrayList;

import evaluator.Node;
import evaluator.Operand;
import evaluator.Number;

public class Ln extends Operand{

	public int getOrderOps() {
		return 1;
	}

	public int getNumVals() {
		return 1;
	}

	public double eval(ArrayList<Double> vals) {
		return Math.log(vals.get(0));
	}

	public Node derive(ArrayList<Node> children, Character key) {
		Node child = children.get(0);
		return new Node(new Mul(), new Node(new Div(), new Node(new Number(1)), child), child.derive(key));
	}

	public void simplify(Node node) {
		return;
	}

	public String toString(){
		return "ln";
	}
}
