package org.zkoss.zssex.util;

import org.zkoss.poi.hssf.usermodel.HSSFSheet;
import org.zkoss.poi.ss.usermodel.ClientAnchor;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.zss.model.Book;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zss.ui.impl.Utils;

public class SpreadsheetHelper {

	public static int getTopFraction(Worksheet sheet, ClientAnchor anchor) {
		if (sheet instanceof HSSFSheet)
			return getHSSFTopFraction(sheet, anchor);

		return getXSSFTopFraction(sheet, anchor);
	}

	private static int getHSSFTopFraction(Worksheet sheet, ClientAnchor anchor) {
		int t = anchor.getRow1();
		int tfrc = anchor.getDy1();

		int th = Utils.getHeightAny(sheet, t);
		return ((tfrc >= 256) ? th : (int) Math.round(th * tfrc / 256.0D));
	}

	private static int getXSSFTopFraction(Worksheet sheet, ClientAnchor anchor) {
		int tfrc = anchor.getDy1();

		return ChartHelper.emuToPx(tfrc);
	}

	public static int getLeftFraction(Worksheet sheet, ClientAnchor anchor) {
		if (sheet instanceof HSSFSheet)
			return getHSSFLeftFraction(sheet, anchor);

		return getXSSFLeftFraction(sheet, anchor);
	}

	private static int getHSSFLeftFraction(Worksheet sheet, ClientAnchor anchor) {
		Book book = (Book) sheet.getWorkbook();
		int charWidth = book.getDefaultCharWidth();
		int l = anchor.getCol1();
		int lfrc = anchor.getDx1();

		int lw = Utils.getWidthAny(sheet, l, charWidth);
		return ((lfrc >= 1024) ? lw : (int) Math.round(lw * lfrc / 1024.0D));
	}

	private static int getXSSFLeftFraction(Worksheet sheet, ClientAnchor anchor) {
		int lfrc = anchor.getDx1();

		return ChartHelper.emuToPx(lfrc);
	}

	public static int getWidthInPx(Worksheet sheet, ClientAnchor anchor) {
		if (sheet instanceof HSSFSheet)
			return getHSSFWidthInPx(sheet, anchor);

		return getXSSFWidthInPx(sheet, anchor);
	}

	private static int getHSSFWidthInPx(Worksheet sheet, ClientAnchor anchor) {
		Book book = (Book) sheet.getWorkbook();
		int charWidth = book.getDefaultCharWidth();
		int l = anchor.getCol1();
		int lfrc = anchor.getDx1();

		int lw = Utils.getWidthAny(sheet, l, charWidth);

		int wFirst = (lfrc >= 1024) ? 0 : lw - (int) Math.round(lw * lfrc / 1024.0D);

		int r = anchor.getCol2();
		int wLast = 0;
		if (l != r) {
			int rfrc = anchor.getDx2();
			int rw = Utils.getWidthAny(sheet, r, charWidth);
			wLast = (int) Math.round(rw * rfrc / 1024.0D);
		}

		int width = wFirst + wLast;
		for (int j = l + 1; j < r; ++j) {
			width += Utils.getWidthAny(sheet, j, charWidth);
		}

		return width;
	}

	private static int getXSSFWidthInPx(Worksheet sheet, ClientAnchor anchor) {
		Book book = (Book) sheet.getWorkbook();
		int charWidth = book.getDefaultCharWidth();
		int l = anchor.getCol1();
		int lfrc = anchor.getDx1();

		int lw = Utils.getWidthAny(sheet, l, charWidth);

		int wFirst = lw - ChartHelper.emuToPx(lfrc);

		int r = anchor.getCol2();
		int wLast = 0;
		if (l != r) {
			int rfrc = anchor.getDx2();
			wLast = ChartHelper.emuToPx(rfrc);
		}

		int width = wFirst + wLast;
		for (int j = l + 1; j < r; ++j) {
			width += Utils.getWidthAny(sheet, j, charWidth);
		}

		return width;
	}

	public static int getHeightInPx(Worksheet sheet, ClientAnchor anchor) {
		if (sheet instanceof HSSFSheet)
			return getHSSFHeightInPx(sheet, anchor);

		return getXSSFHeightInPx(sheet, anchor);
	}

	private static int getHSSFHeightInPx(Worksheet zkSheet, ClientAnchor anchor) {
		int t = anchor.getRow1();
		int tfrc = anchor.getDy1();

		int th = Utils.getHeightAny(zkSheet, t);
		int hFirst = (tfrc >= 256) ? 0 : th - (int) Math.round(th * tfrc / 256.0D);

		int b = anchor.getRow2();
		int hLast = 0;
		if (t != b) {
			int bfrc = anchor.getDy2();
			int bh = Utils.getHeightAny(zkSheet, b);
			hLast = (int) Math.round(bh * bfrc / 256.0D);
		}

		int height = hFirst + hLast;
		for (int j = t + 1; j < b; ++j) {
			height += Utils.getHeightAny(zkSheet, j);
		}

		return height;
	}

	private static int getXSSFHeightInPx(Worksheet zkSheet, ClientAnchor anchor) {
		int t = anchor.getRow1();
		int tfrc = anchor.getDy1();

		int th = Utils.getHeightAny(zkSheet, t);
		int hFirst = th - ChartHelper.emuToPx(tfrc);

		int b = anchor.getRow2();
		int hLast = 0;
		if (t != b) {
			int bfrc = anchor.getDy2();
			hLast = ChartHelper.emuToPx(bfrc);
		}

		int height = hFirst + hLast;
		for (int j = t + 1; j < b; ++j) {
			height += Utils.getHeightAny(zkSheet, j);
		}

		return height;
	}

	public static int getHeightInPx(Worksheet sheet, AreaReference areaReference) {
		// TODO 这种方式是否准确？chart是使用ClientAnchor算的，里面有些单位变化
		CellReference firstCell = areaReference.getFirstCell();
		CellReference lastCell = areaReference.getLastCell();

		int heightPx = 0;
		for (int i = firstCell.getRow(); i <= lastCell.getRow(); i++) {
			heightPx += Utils.getHeightAny(sheet, i);
		}

		return heightPx;
	}

	public static int getWidthInPx(Worksheet sheet, AreaReference areaReference) {
		// TODO 这种方式是否准确？chart是使用ClientAnchor算的，里面有些单位变化
		Book book = sheet.getBook();
		int charWidth = book.getDefaultCharWidth();
		CellReference firstCell = areaReference.getFirstCell();
		CellReference lastCell = areaReference.getLastCell();

		int widthPx = 0;
		for (int i = firstCell.getCol(); i <= lastCell.getCol(); i++) {
			widthPx += Utils.getWidthAny(sheet, i, charWidth);
		}

		return widthPx;
	}

	public static int getTopFraction(Worksheet sheet, AreaReference areaReference) {
		// TODO 这种方式是否准确？chart是使用ClientAnchor算的，里面有些单位变化
		CellReference firstCell = areaReference.getFirstCell();

		int heightPx = 0;
		for (int i = 1; i <= firstCell.getRow(); i++) { // FIXME 从1开始？
			heightPx += Utils.getHeightAny(sheet, i);
		}

		return heightPx;
	}

	public static int getLeftFraction(Worksheet sheet, AreaReference areaReference) {
		// TODO 这种方式是否准确？chart是使用ClientAnchor算的，里面有些单位变化
		Book book = sheet.getBook();
		int charWidth = book.getDefaultCharWidth();
		CellReference firstCell = areaReference.getFirstCell();

		int widthPx = 0;
		for (int i = 1; i <= firstCell.getCol(); i++) {
			widthPx += Utils.getWidthAny(sheet, i, charWidth);
		}

		return widthPx;
	}

}
