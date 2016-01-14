package functions;
import java.util.ArrayList;

import evaluator.Node;
import evaluator.Operand;
import evaluator.Number;

public class Exp extends Operand {

	public int getNumVals() {
		return 2;
	}
	
	public int getOrderOps(){return 2;}

	public double eval(ArrayList<Double> vals) {
		return Math.pow(vals.get(0), vals.get(1));
	}

	public String toString(){return "^";}

	@Override
	public Node derive(ArrayList<Node> children, Character key) {
		Node base = children.get(0);
		Node exp = children.get(1);
		if(base.hasVar(key)&&exp.hasVar(key)){
							// d/dx( f(x)^g(x) ) = f(x)^g(x) * d/dx( g(x) ) * ln( f(x) )
                				// + f(x)^( g(x)-1 ) * g(x) * d/dx( f(x) )
			Node fg = new Node(new Exp(), base, exp);
			Node dglnf = new Node(new Mul(), exp.derive(key), new Node(new Ln(), base, null));
			Node top = new Node(new Mul(), fg, dglnf);
			
			Node gm	= new Node(new Sub(), exp, new Node(new Number(1)));
			Node fgm = new Node(new Exp(), base, gm);
			Node gdf = new Node(new Mul(), base, exp.derive(key));
			Node bot = new Node(new Mul(), fgm, gdf);
			
			return new Node(new Add(), top, bot);
		}
		if(base.hasVar(key)){			//Power rule
			Node ndx = new Node(new Mul(), exp, base.derive(key));	//ndX
			Node nm	= new Node(new Sub(), exp, new Node(new Number(1)));			//n-1
			Node xn	= new Node(new Exp(), base, nm);			//X^nm
			
			return new Node(new Mul(), ndx, xn);
		}
		Node ndx = new Node(new Mul(), exp.derive(key), new Node(new Ln(), base, null));
		Node nx = new Node(new Exp(), base, exp);
		return new Node(new Mul(), ndx, nx);
	}

	@Override
	public void simplify(Node node) {
		try {
			double baseVal = node.getChildren().get(0).eval();
			if(Math.abs(baseVal)<DOUBLE_TOL) node.replaceWithNum(0);
		} catch (Exception e) {
		}
		try {
			double expVal = node.getChildren().get(1).eval();
			if(Math.abs(expVal-1)<DOUBLE_TOL) node.absorbChild(node.getChildren().get(0));
		} catch (Exception e) {
		}
	}

}
