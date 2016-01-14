package evaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Node {


	private Operand mainFunc;		//Getters are setters for simplification

	public Operand getMainFunc() {
		return mainFunc;
	}

	private ArrayList<Node> children;

	public ArrayList<Node> getChildren() {
		return children;
	}

	public void absorbChild(Node child){
		mainFunc = child.getMainFunc();
		children = child.getChildren();
	}

	public void replaceWithNum(double num){
		mainFunc = new Number(num);
		children.clear();
	}

	public boolean hasVar(Character key){
		if(mainFunc instanceof Number) return false;
		if(mainFunc instanceof Var) return ((Var) mainFunc).getKey().equals(key);
		for(Node child: children){
			if(child.hasVar(key))
				return true;
		}
		return false;
	}

	public Node(List<Func> function){
		if(function==null) return;
		while(stripLeadTail(function));
		if(function.size()==1){
			mainFunc = (Operand) function.get(0);
		}
		else{
			children = new ArrayList<Node>();
			int index = findHighestOp(function);
			mainFunc = (Operand)function.get(index);
			if(mainFunc.getNumVals()==1) {
				function.remove(0);
				children.add(new Node(function));
			}
			else if (mainFunc.getNumVals()==2) {
				children.add(new Node(subList(0, index, function)));
				children.add(new Node(subList(index+1, function.size(), function)));
			}
		}
	}

	public Node(Func func) {
		mainFunc = (Operand) func;
	}

	private static ArrayList<Func> subList(int start, int end, List<Func> function) {
		ArrayList<Func> subList = new ArrayList<Func>();
		for(int i = start; i < end; i++){
			subList.add(function.get(i));
		}
		return subList;
	}

	private boolean stripLeadTail(List<Func> function) {
		int count = 1;
		if(!(function.get(0) instanceof Paren && function.get(function.size() - 1) instanceof Paren)) return false; 	//If the func does not start+end with ( and ), exit

		ListIterator<Func> it = function.listIterator(1);
		while(it.nextIndex()< function.size()-1){
			Func func = it.next();
			if(func instanceof OpenParen) count++;
			else if(func instanceof CloseParen) count--;

			if(count==0)return false;
		}
		function.remove(function.size()-1);
		function.remove(0);
		return true;
	}

	private int findHighestOp(List<Func> function) {
		int highestOp = -1;
		int index = -1;
		int parenNum=0;
		for(int i = 0; i < function.size(); i++){
			Func temp = function.get(i);
			if(temp instanceof Paren){
				if(temp instanceof OpenParen) parenNum++;
				else parenNum--;
			}
			if(parenNum == 0 && temp.getOrderOps()>highestOp){
				highestOp = temp.getOrderOps();
				index = i;
			}
		}
		return index;
	}

	public Node(Operand op, Node child1, Node child2){
		mainFunc = op;
		children = new ArrayList<Node>();
		if(child1!=null){
			children.add(child1);
			if(child2!=null) 
				children.add(child2);
		}
	}

	public double eval() throws Exception {
		return eval(null);
	}

	public double eval(Map<Character, MutableDouble> varSet) throws Exception{
		if(mainFunc instanceof Var){
			return ((Var)mainFunc).eval(varSet);
		}
		if(mainFunc instanceof Number) return mainFunc.eval(null);

		ArrayList<Double> subEval = new ArrayList<Double>();
		for(Node child: children){
			subEval.add(child.eval(varSet));
		}
		return mainFunc.eval(subEval);
	}


	public void addKeys(Map<Character, MutableDouble> map) {
		if(children == null || children.size() == 0){
			if(mainFunc instanceof Var){
				map.put(((Var)mainFunc).getKey(), new MutableDouble());
			}
			return;
		}
		for(Node child: children){
			child.addKeys(map);
		}
		return;
	}

	public void simplify() {			//Attempt to solve as much of the equation tree as possible without variables
		if(mainFunc instanceof Var || mainFunc instanceof Number) return;

		for(Node child: children) child.simplify();
		ArrayList<Double> vals = new ArrayList<Double>();
		boolean allNum = true;
		for(Node child: children){
			try{
				vals.add(child.eval());
			}
			catch (Exception ex){
				allNum = false;
			}
		}
		if(allNum){
			replaceWithNum(mainFunc.eval(vals));			//Replace the function with a constant, and remove children
		}
		else{
			mainFunc.simplify(this);
		}
	}

	public String toString(){
		if(children==null || children.size()==0) return mainFunc.toString();
		if(children.size()==1) return mainFunc.toString() + "(" + children.get(0) +")";
		return "(" + children.get(0) + mainFunc.toString() + children.get(1) +")";
	}

	public Node derive(Character key) {
		return mainFunc.derive(children, key);
	}
}
