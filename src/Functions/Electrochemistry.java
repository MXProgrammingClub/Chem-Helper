package Functions;

import java.util.TreeMap;

public class Electrochemistry
{
	private static final TreeMap<String, Double> REACTIONS = createMap();
	
	private static TreeMap<String, Double> createMap()
	{
		TreeMap<String, Double> map = new TreeMap<String, Double>();
		map.put("F<sub>2</sub>(g) + 2e<sub>-</sub> \u2192 2F<sup>-</sup>", 2.87);
		map.put("H<sub>2</sub>O<sub>2</sub>(aq) + 2H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 2H<sub>2</su>O(l)", 1.78);
		map.put("PbO<sub>2</sub>(s) + 4H<sup>+</sup>(aq) + SO<sub>4</sub><sup>2-</sup>(aq) + 2e<sup>-</sup> \u2192 PbSO<sub>4</sub>(s) + 2H<sub>2</su>O(l)",
				1.69);
		
		return map;
	}
}