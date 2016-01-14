package functions;
import java.util.ArrayList;

import evaluator.Node;
import evaluator.Operand;
import evaluator.Number;

public class Tangent extends Operand {

	public int getNumVals() {
		return 1;
	}
	
	public int getOrderOps(){return 1;}

	public double eval(ArrayList<Double> vals) {
		return Math.tan(vals.get(0));
	}

	public String toString(){return "tan";}

	public Node derive(ArrayList<Node> children, Character key) 
	{
		return new Node(new Mul(), new Node(new Div(), new Node(new Number(1)), new Node(new Exp(), new Node(new Cosine(), children.get(0), null), 
				new Node(new Number(2)))), children.get(0).derive(key));
		//Power rule: d/dx(tan(g(x)) = (1/((cos(g(x)))^2)) * g'(x)
	}

	@Override
	public void simplify(Node node) {
		return;
	}
}
