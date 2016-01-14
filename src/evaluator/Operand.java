package evaluator;
import java.util.ArrayList;


public abstract class Operand implements Func{
	public static final Double DOUBLE_TOL = .00001;
	public abstract int getNumVals();
	public abstract double eval(ArrayList<Double> vals);
	public abstract Node derive(ArrayList<Node> children, Character key);
	public abstract void simplify(Node node);
}
