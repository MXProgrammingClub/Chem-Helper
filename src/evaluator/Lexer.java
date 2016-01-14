package evaluator;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import functions.Add;
import functions.Cosine;
import functions.Div;
import functions.E;
import functions.Exp;
import functions.Ln;
import functions.Mul;
import functions.Pi;
import functions.Sine;
import functions.Sqrt;
import functions.Sub;
import functions.Tangent;
import functions.UnaryMinus;


public class Lexer {
	private static int pointer;
	public static ArrayList<Func> lex(String function) {
		if(!validSyntax(function)) return null;
		function = function.replaceAll("\\s", "");
		ArrayList<Func> lexed = new ArrayList<Func>();
		pointer = 0;
		Map<String, Func> funcs = buildMap();
		while(pointer < function.length()){
			if(isNumber(function, pointer)){ 	
				lexed.add(getNumber(function));
			}
			else if(isParen(function, pointer)) lexed.add(getParen(function));
			else {
				Func posFunc = getFunc(function, lexed, funcs);
				if(posFunc!=null) lexed.add(posFunc);
				else{		//Add a var with the given name
					lexed.add(new Var(function.charAt(pointer)));
					pointer++;
				}
			}
		}
		handleImplcitMult(lexed);
		return lexed;
	}
	
	private static boolean validSyntax(String function) {
		int paren = 0, len = function.length();
		for(int i = 0; i < len; i++){
			if(function.charAt(i)=='(') paren++;
			else if(function.charAt(i)==')') paren--;
			
			if(paren < 0) return false;
		}
		return paren ==0;
	}

	private static void handleImplcitMult(ArrayList<Func> lexed){
		int i = 0;
		while(i < lexed.size() -1){
			Func func = lexed.get(i), next = lexed.get(i+1);
			if((func instanceof Number || func instanceof Var) &&
				(next instanceof Number || next instanceof Var || next instanceof OpenParen || (next instanceof Operand && ((Operand) next).getNumVals()==1))) lexed.add(i+1, new Mul());
			else
			if(func instanceof CloseParen && 
				(next instanceof OpenParen || next instanceof Number || next instanceof Var|| (next instanceof Operand && ((Operand) next).getNumVals()==1))) lexed.add(i+1, new Mul());	//If )(, add a *
			i++;
		}
	}

	private static Map<String, Func> buildMap(){
		Map<String, Func> funcs = new TreeMap<String, Func>();
		
		Func add = new Add();
		Func sub = new Sub();
		Func mul = new Mul();
		Func div = new Div();
		Func exp = new Exp();
		Func sqrt = new Sqrt();
		Func sin = new Sine();
		Func cos = new Cosine();
		Func tan = new Tangent();
		Func ln = new Ln();
		
		funcs.put("+", add);funcs.put("-", sub);funcs.put("*", mul);funcs.put("/", div);funcs.put("^", exp);funcs.put("sqrt", sqrt);
		funcs.put("sin", sin);funcs.put("Sin", sin);funcs.put("cos", cos);funcs.put("Cos", cos);funcs.put("tan", tan);funcs.put("Tan", tan);
		funcs.put("ln", ln); funcs.put("Ln", ln);
		funcs.put("e", new E()); funcs.put("pi", new Pi());
		return funcs;
	}
	
	private static Func getFunc(String function, ArrayList<Func> lexed, Map<String, Func> funcs) {		
		if(pointer < function.length() && function.charAt(pointer)=='-' && (lexed.size()==0 || !(lexed.get(lexed.size()-1) instanceof Number || lexed.get(lexed.size()-1) instanceof Var))){ 
			pointer++; return new UnaryMinus();
		}
		
		
		Set<String> funcNames = funcs.keySet();
		for(String name: funcNames){
			if(pointer + name.length() < function.length() && function.substring(pointer, pointer+ name.length()).equals(name)){
				pointer += name.length();
				Func func = funcs.get(name);
				return func;
			}
		}
		return null;
	}

	private static Func getParen(String function){
		Paren paren;
		if(function.charAt(pointer)=='(' || function.charAt(pointer)=='['){
			paren = new OpenParen();
		}
		else{ 
			paren = new CloseParen();
		}	
		pointer++;
		return paren;
	}
	
	private static Func getNumber(String function){
		String temp = "";
		
		while(pointer < function.length() && isNumber(function, pointer)){
			temp += function.charAt(pointer);
			pointer++;
		}
			
		double val = (new Double(temp)).doubleValue();
		return new Number(val);
	}
	
	
	private static boolean isNumber(String function, int target){
		char val = function.charAt(target);
		return (val == '.' ||val == '0' ||val == '1' ||val == '2' ||val == '3' ||val == '4' ||val == '5' ||val == '6' ||val == '7' ||val == '8' || val == '9');
	}
	
	private static boolean isParen(String function, int target) {
		char val = function.charAt(target);
		return val == '(' || val ==')' || val == '[' || val ==']';	
		
	}

}
