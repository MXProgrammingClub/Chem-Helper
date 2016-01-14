package functions;
import java.util.ArrayList;

import evaluator.*;
import evaluator.Number;


public class Add extends Operand {

	public int getNumVals() {
		return 2;
	}
	
	public int getOrderOps(){return 5;}

	public double eval(ArrayList<Double> vals) {
		return vals.get(0)+vals.get(1);
	}

	public String toString(){return "+";}

	@Override
	public Node derive(ArrayList<Node> children, Character key) {
		return new Node(new Add(), children.get(0).derive(key), children.get(1).derive(key));
	}

	@Override
	public void simplify(Node node) {
		ArrayList<Node> children = node.getChildren();
		for(int i = 0; i < 2; i++){
			try {
				if(children.get(i).eval() == 0){
					node.absorbChild(children.get((i+1)%2));
					return;
				}
			} catch (Exception e) {
			}
		}
	}

}
