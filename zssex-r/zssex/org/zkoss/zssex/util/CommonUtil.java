package org.zkoss.zssex.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommonUtil {
	
	public static List<Map<String, Object>> wrapCollection(Collection<?> list, String label) {
		List<Map<String, Object>> categoriesWrap = new ArrayList<Map<String, Object>>();
		Iterator<?> it = list.iterator();
		while(it.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("label", it.next());
			categoriesWrap.add(map);
		}
		return categoriesWrap;
	}
	
}