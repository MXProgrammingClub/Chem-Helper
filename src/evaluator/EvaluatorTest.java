package evaluator;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class EvaluatorTest {

	private static final double DOUBLE__TOL = .0001;

	@Test
	public void testGetKeys() {
		Evaluator eval = new Evaluator();
		eval.parse("5X+Sin(Y^(2-X))2");
		Map<Character, MutableDouble> keys = eval.getKeys();
		
		assertEquals(2, keys.size());
		assertNull(keys.get('A'));
		assertNotNull(keys.get('X'));
	}

	@Test
	public void testParse() throws Exception {
		Evaluator eval = new Evaluator();
		eval.parse("5+Sin(0^2)");
		
		assertEquals(5.0, eval.evaluate(),DOUBLE__TOL);
		
	}

	@Test
	public void testEvaluateMapOfStringMutableDouble() throws Exception {
		Evaluator eval = new Evaluator();
		eval.parse("5X+Cos(Y)");
		
		Map<Character, MutableDouble> vars = eval.getKeys();
		assertNotNull(vars);
		
		vars.get('X').setVal(3);
		vars.get('Y').setVal(Math.PI);
		
		assertEquals(14, eval.evaluate(vars), DOUBLE__TOL);
	}
	
	@Test
	public void testImplicitMult() throws Exception{
		Evaluator eval = new Evaluator();
		Map<Character, MutableDouble> vars;
		
		eval.parse("5X");
		vars = eval.getKeys();
		vars.get('X').setVal(2);
		assertEquals(10, eval.evaluate(vars), DOUBLE__TOL);
		
		eval.parse("5(7)");
		assertEquals(35, eval.evaluate(), DOUBLE__TOL);
		
		eval.parse("(2)X");
		vars = eval.getKeys();
		vars.get('X').setVal(2);
		assertEquals(4, eval.evaluate(vars), DOUBLE__TOL);
		
		eval.parse("(1)(7)");
		assertEquals(7, eval.evaluate(), DOUBLE__TOL);
	}
	
	@Test
	public void testDerive() throws Exception{
		Evaluator eval = new Evaluator();
		eval.parse("XSin(X)");
		eval.derive('X');
		Map<Character, MutableDouble> vars = eval.getKeys();
		vars.get('X').setVal(Math.PI);
		assertEquals(-Math.PI, eval.evaluate(vars), DOUBLE__TOL);
	}
}
