package functions;
import java.util.ArrayList;

import evaluator.Node;
import evaluator.Operand;
import evaluator.Number;

public class UnaryMinus extends Operand {

	public int getNumVals() {
		return 1;
	}
	
	public int getOrderOps(){return 1;}

	public double eval(ArrayList<Double> vals) {
		return -1 * vals.get(0);
	}

	public String toString(){return "-";}

	@Override
	public Node derive(ArrayList<Node> children, Character key) {
		return new Node(new Mul(), new Node(new Number(-1), null,null), children.get(0).derive(key));
	}

	@Override
	public void simplify(Node node) {
		return;
	}

}