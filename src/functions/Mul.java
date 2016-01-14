package functions;
import java.util.ArrayList;

import evaluator.Node;
import evaluator.Operand;
import evaluator.Number;

public class Mul extends Operand {

	public int getNumVals() {
		return 2;
	}
	
	public int getOrderOps(){return 3;}

	public double eval(ArrayList<Double> vals) {
		return vals.get(0)*vals.get(1);
	}

	public String toString(){return "*";}

	public Node derive(ArrayList<Node> children, Character key) 
	{
		Node fpg = new Node(new Mul(), children.get(0).derive(key), children.get(1));
		Node gpf = new Node(new Mul(), children.get(0), children.get(1).derive(key));
		return new Node(new Add(), fpg, gpf); 
		//Product rule: f'(x)*g(x)+f(x)*g'(x)
	}

	@Override
	public void simplify(Node node) {
		ArrayList<Node> children = node.getChildren();
		for(int i = 0; i < 2; i++){
			try {
				double val = children.get(i).eval();
				if(Math.abs(val-1) < DOUBLE_TOL){
					node.absorbChild(children.get((i+1)%2));
					return;
				}
				if(Math.abs(val) < DOUBLE_TOL){
					node.replaceWithNum(0);
					return;
				}
			} catch (Exception e) {
			}
		}
		
	}
}
