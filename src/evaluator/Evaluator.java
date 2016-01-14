package evaluator;
import java.util.HashMap;
import java.util.Map;


public class Evaluator {
	Node mainNode;
	public Map<Character, MutableDouble> getKeys(){
		Map<Character, MutableDouble> map = new HashMap<Character, MutableDouble>();
		mainNode.addKeys(map);
		return map;
	}
	public void parse(String function){
		 mainNode = new Node(Lexer.lex(function));
		 mainNode.simplify();
	}
	
	public double evaluate(Map<Character, MutableDouble> varSet) throws Exception{
		return mainNode.eval(varSet);
	}
	public double evaluate() throws Exception{
		return mainNode.eval();
	}
	
	public String toString(){
		return mainNode.toString();
	}
	
	public void derive(Character key){
		mainNode = mainNode.derive(key);
		mainNode.simplify();
	}
}