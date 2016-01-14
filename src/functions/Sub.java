package functions;
import java.util.ArrayList;

import evaluator.Node;
import evaluator.Operand;

public class Sub extends Operand {

	public int getNumVals() {
		return 2;
	}
	
	public int getOrderOps(){return 5;}

	public double eval(ArrayList<Double> vals) {
		return vals.get(0)-vals.get(1);
	}

	public String toString(){return "-";}

	public Node derive(ArrayList<Node> children, Character key)
	{
		return new Node(new Sub(), children.get(0).derive(key), children.get(1).derive(key));
	}

	@Override
	public void simplify(Node node) {
		ArrayList<Node> children = node.getChildren();
		try {
			if(Math.abs(children.get(0).eval()) <DOUBLE_TOL) node.absorbChild(children.get(0));
		} catch (Exception e) {
		}
		
	}
}
