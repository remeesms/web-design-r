package org.zkoss.zssex.util;

import org.zkoss.zk.ui.UiException;

public class HTMLUtil {
	
	public static int[] colorDecode(String color, int[] rgb) {
		if (color == null) {
			return null;
		}
		if (rgb == null) {
			rgb = new int[3];
		}
		
		if (color.length() != 7 || color.charAt(0) != '#') {
			throw new UiException("Incorrect color format (#RRGGBB) : " + color);
		}
		
		rgb[0] = Integer.parseInt(color.substring(1, 3), 16);
		rgb[1] = Integer.parseInt(color.substring(3, 5), 16);
		rgb[2] = Integer.parseInt(color.substring(5, 7), 16);
		
		return rgb;
	}

	public static int measureStringToInt(String str) {
		int j = str.lastIndexOf("px");
		if (j > 0) {
			final String num = str.substring(0, j);
			return Integer.parseInt(num);
		}

		j = str.lastIndexOf("pt");
		if (j > 0) {
			final String num = str.substring(0, j);
			return (int) (Integer.parseInt(num) * 1.3333);
		}

		j = str.lastIndexOf("em");
		if (j > 0) {
			final String num = str.substring(0, j);
			return (int) (Integer.parseInt(num) * 13.3333);
		}
		return Integer.parseInt(str);
	}
}