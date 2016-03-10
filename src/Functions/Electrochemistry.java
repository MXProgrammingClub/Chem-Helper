package Functions;

import java.util.TreeMap;

public class Electrochemistry
{
	private static final TreeMap<String, Double> REACTIONS = createMap();
	
	private static TreeMap<String, Double> createMap()
	{
		TreeMap<String, Double> map = new TreeMap<String, Double>();
		map.put("F<sub>2</sub>(g) + 2e<sub>-</sub> \u2192 2F<sup>-</sup>", 2.87);
		map.put("H<sub>2</sub>O<sub>2</sub>(aq) + 2H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 2H<sub>2</sub>O(l)", 1.78);
		map.put("PbO<sub>2</sub>(s) + 4H<sup>+</sup>(aq) + SO<sub>4</sub><sup>2-</sup>(aq) + 2e<sup>-</sup> \u2192 PbSO<sub>4</sub>(s) + 2H<sub>2</sub>O(l)",
				1.69);
		map.put("MnO<sub>4</sub><sup>-</sup>(aq) + 4H<sup>+</sup>(aq) + 3e<sup>-</sup> \u2192 MnO<sub>2</sub>(s) + 2H<sub>2</sub>O(l)", 1.68);
		map.put("MnO<sub>4</sub><sup>-</sup>(aq) + 8H<sup>+</sup>(aq) + 5e<sup>-</sup> \u2192 Mn<sup>2+</sup>(aq) + 4H<sub>2</sub>O(1)", 1.51);
		map.put("Au<sup>3+</sup>(aq) + 3e<sup>-</sup> \u2192 Au(s)", 1.5);
		map.put("PbO<sub>2</sub>(s) + 4H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 Pb<sup>2+</sup>(aq) + 2H<sub>2</sub>O(l)", 1.46);
		map.put("Cl<sub>2</sub> + 2e<sup>-</sup> /u2192 2Cl<sup>-</sup>", 1.36);
		map.put("Cr<sub>2</sub>O<sub>7</sub><sup>2-</sup>(aq) + 14H<sup>+</sup>(aq) + 6e<sup>-</sup> \u2192 2Cr<sup>3+</sup>(aq) + 7H<sub>2</sub>O(l)", 1.33);
		map.put("O<sub>2</sub>(g) + 4H<sup>+</sup>(aq) + 4e<sup>-</sup> \u2192 2H<sub>2</sub>O(l)", 1.23);
		map.put("MnO<sub>2</sub>(g) + 4H<sup>+</sup>(aq) + 2e<sup>-</sup> \u2192 Mn<sup>2+</sup>(aq) + 2H<sub>2</sub>O(l)", 1.21);
		return map;
	}
}