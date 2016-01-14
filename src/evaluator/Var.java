package evaluator;
import java.util.ArrayList;
import java.util.Map;


public class Var extends Operand{
	private Character key;
	
	public Var(Character key){
		this.key = key;
	}

	public String toString(){return key + "";}

	public int getNumVals() {
		return 1;
	}
	
	public Character getKey(){return key;}

	public double eval(ArrayList<Double> vals) {
		return vals.get(0);
	}
	
	public double eval(Map<Character, MutableDouble> vals){
		return vals.get(key).getVal();
	}

	public int getOrderOps() {
		return 0;
	}

	public Node derive(ArrayList<Node> children, Character key) {
		if(key.equals(this.key)) return new Node(new Number(1),null,null);
		return new Node(new Number(0),null,null);
	}

	@Override
	public void simplify(Node node) {
		return;
	}
}
