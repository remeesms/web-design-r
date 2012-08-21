package org.zkoss.zssex.ui.widget;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.image.AImage;
import org.zkoss.poi.hssf.usermodel.HSSFSheet;
import org.zkoss.poi.ss.usermodel.Chart;
import org.zkoss.poi.ss.usermodel.ClientAnchor;
import org.zkoss.poi.ss.usermodel.Picture;
import org.zkoss.poi.ss.usermodel.PictureData;
import org.zkoss.poi.ss.usermodel.ZssChartX;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.UiException.Aide;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zss.model.Book;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zss.model.impl.DrawingManager;
import org.zkoss.zss.model.impl.SheetCtrl;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zss.ui.Widget;
import org.zkoss.zss.ui.impl.HeaderPositionHelper;
import org.zkoss.zss.ui.impl.Utils;
import org.zkoss.zss.ui.sys.SpreadsheetCtrl;
import org.zkoss.zss.ui.sys.WidgetLoader;
import org.zkoss.zssex.util.ChartHelper;

public class DefaultBookWidgetLoader implements WidgetLoader {
	private static final Log log = Log.lookup(DefaultBookWidgetLoader.class);
	private Spreadsheet _spreadsheet;
	private HeaderPositionHelper _rowSizeHelper;
	private HeaderPositionHelper _colSizeHelper;
	static final int ZINDEX_START = 200;
	Map<String, Map<String, Widget>> _widgetMap;

	public DefaultBookWidgetLoader() {
		this._widgetMap = new HashMap<String, Map<String, Widget>>();
	}

	public void init(Spreadsheet spreadsheet) {
		this._spreadsheet = spreadsheet;
	}

	public void invalidate() {
	}

	public void onSheetClean(Worksheet sheet) {
		String key = Utils.getSheetUuid(sheet);
		Map list = (Map) this._widgetMap.get(key);
		if (list != null) {
			Iterator iter = list.values().iterator();
			while (iter.hasNext())
				((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).removeWidget((Widget) iter.next());

			this._widgetMap.remove(key);
		}
		this._rowSizeHelper = null;
		this._colSizeHelper = null;
	}

	public void onSheetSelected(Worksheet sheet) {
		String key = Utils.getSheetUuid(sheet);

		this._rowSizeHelper = ((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).getRowPositionHelper(key);
		this._colSizeHelper = ((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).getColumnPositionHelper(key);

		DrawingManager dm = ((SheetCtrl) sheet).getDrawingManager();
		Map<String, Widget> list = new LinkedHashMap<String, Widget>();
		preparePictureWidgets(sheet, dm, list);
		prepareChartWidgets(sheet, dm, list);
		prepareTableWidgets(sheet, dm, list);
		if ((list != null) && (list.size() > 0))
			this._widgetMap.put(key, list);
	}

	private void preparePictureWidgets(Worksheet sheet, DrawingManager dm, Map<String, Widget> list) {
		List<Picture> pictures = dm.getPictures();
		if ((pictures == null) || (pictures.size() == 0)) { return; }
		
		int zindex = 200 + list.size();
		for (Picture picture : pictures) {
			try {
				ImageWidget imagewgt = newImageWidget(sheet, picture, zindex++);
				if (imagewgt != null) {
					list.put(picture.getPictureId(), imagewgt);
					((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).addWidget(imagewgt);
				}
			} catch (IOException e) {
				throw UiException.Aide.wrap(e);
			}
		}
	}

	private void prepareChartWidgets(Worksheet sheet, DrawingManager dm, Map<String, Widget> list) {
		List<ZssChartX> charts = dm.getChartXs();
		if ((charts == null) || (charts.size() == 0)) { return; }
		
		int zindex = 200 + list.size();
		for (ZssChartX chartX : charts) {
			try {
				ChartWidget chartwgt = newChartWidget(sheet, chartX, zindex++);
				if (chartwgt != null) {
					list.put(chartX.getChartId(), chartwgt);
					((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).addWidget(chartwgt);
				}
			} catch (IOException e) {
				throw UiException.Aide.wrap(e);
			}
		}
	}
	
	/**
	 * pivot table / grid
	 * 
	 * @param sheet
	 * @param dm
	 * @param list
	 */
	private void prepareTableWidgets(Worksheet sheet, DrawingManager dm, Map<String, Widget> list) {
		// TODO
		/*List<ZssChartX> charts = dm.getChartXs();
		if ((charts == null) || (charts.size() == 0)) { return; }
		
		int zindex = 200 + list.size();
		for (ZssChartX chartX : charts) {
			try {
				ChartWidget chartwgt = newChartWidget(sheet, chartX, zindex++);
				if (chartwgt != null) {
					list.put(chartX.getChartId(), chartwgt);
					((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).addWidget(chartwgt);
				}
			} catch (IOException e) {
				throw UiException.Aide.wrap(e);
			}
		}*/
	}	

	public void addChartWidget(Worksheet sheet, ZssChartX chart) {
		String key = Utils.getSheetUuid(sheet);
		Map<String, Widget> list = this._widgetMap.get(key);
		if (list == null)
			list = new LinkedHashMap<String, Widget>();

		int zindex = 200 + list.size();
		try {
			ChartWidget chartwgt = newChartWidget(sheet, chart, zindex);
			if (chartwgt != null) {
				list.put(chart.getChartId(), chartwgt);
				((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).addWidget(chartwgt);
			}
		} catch (IOException e) {
			throw UiException.Aide.wrap(e);
		}
		if ((list != null) && (list.size() > 0))
			this._widgetMap.put(key, list);
	}

	public void addPictureWidget(Worksheet sheet, Picture picture) {
		String key = Utils.getSheetUuid(sheet);
		Map<String, Widget> list = this._widgetMap.get(key);
		if (list == null)
			list = new LinkedHashMap<String, Widget>();

		int zindex = 200 + list.size();
		try {
			ImageWidget imagewgt = newImageWidget(sheet, picture, zindex);
			if (imagewgt != null) {
				list.put(picture.getPictureId(), imagewgt);
				((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).addWidget(imagewgt);
			}
		} catch (IOException e) {
			throw UiException.Aide.wrap(e);
		}
		if ((list != null) && (list.size() > 0))
			this._widgetMap.put(key, list);
	}

	public void deletePictureWidget(Worksheet sheet, Picture picture) {
		String key = Utils.getSheetUuid(sheet);
		Map<String, Widget> list = this._widgetMap.get(key);
		if (list == null)
			return;

		ImageWidget imagewgt = (ImageWidget) list.remove(picture.getPictureId());
		if (imagewgt != null)
			((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).removeWidget(imagewgt);
	}

	public void updatePictureWidget(Worksheet sheet, Picture picture) {
		String key = Utils.getSheetUuid(sheet);
		Map<String, Widget> list = (Map<String, Widget>) this._widgetMap.get(key);
		if (list == null) {
			return;
		}

		ImageWidget imagewgt = (ImageWidget) list.get(picture.getPictureId());
		if (imagewgt == null) {
			return;
		}

		ClientAnchor anchor = picture.getClientAnchor();
		setupImageWigetAnchor(sheet, imagewgt, anchor, imagewgt.getZindex());
	}

	private void setupImageWigetAnchor(Worksheet sheet, ImageWidget imagewgt, ClientAnchor anchor, int zindex) {
		if (anchor != null) {
			int row = anchor.getRow1();
			int col = anchor.getCol1();
			int row2 = anchor.getRow2();
			int col2 = anchor.getCol2();
			int height = getHeightInPx(sheet, anchor);
			int width = getWidthInPx(sheet, anchor);
			int left = getLeftFraction(sheet, anchor);
			int top = getTopFraction(sheet, anchor);
			imagewgt.setRow(row);
			imagewgt.setColumn(col);
			imagewgt.setRow2(row2);
			imagewgt.setColumn2(col2);
			imagewgt.setZindex(zindex);
			imagewgt.setWidth(width + "px");
			imagewgt.setHeight(height + "px");
			imagewgt.setLeft(left - 1);
			imagewgt.setTop(top - 1);
		}
	}

	private void setupChartWigetAnchor(Worksheet sheet, ChartWidget chartwgt, ClientAnchor anchor, int zindex) {
		if (anchor != null) {
			int row = anchor.getRow1();
			int col = anchor.getCol1();
			int row2 = anchor.getRow2();
			int col2 = anchor.getCol2();
			int height = getHeightInPx(sheet, anchor);
			int width = getWidthInPx(sheet, anchor);
			int left = getLeftFraction(sheet, anchor);
			int top = getTopFraction(sheet, anchor);
			chartwgt.setRow(row);
			chartwgt.setColumn(col);
			chartwgt.setRow2(row2);
			chartwgt.setColumn2(col2);
			chartwgt.setZindex(zindex);
			chartwgt.setWidth(width + "px");
			chartwgt.setHeight(height + "px");
			chartwgt.setLeft(left - 1);
			chartwgt.setTop(top - 1);
		}
	}

	protected ImageWidget newImageWidget(Worksheet sheet, Picture picture, int zindex) throws IOException {
		PictureData picdata = picture.getPictureData();
		if (picdata == null)
			return null;

		ImageWidget imagewgt = new ImageWidget(sheet, picture);
		AImage img = getAImage(sheet, picdata, picture.getName());
		imagewgt.setContent(img);

		ClientAnchor anchor = picture.getClientAnchor();
		setupImageWigetAnchor(sheet, imagewgt, anchor, zindex);
		return imagewgt;
	}

	private AImage getAImage(Worksheet sheet, PictureData picdata, String name) {
		try {
			name = name + '.' + picdata.suggestFileExtension();
			return new AImage(name, picdata.getData());
		} catch (IOException e) {
			log.warning(e);
		}

		return null;
	}

	protected ChartWidget newChartWidget(Worksheet sheet, ZssChartX chart, int zindex) throws IOException {
		return new ChartWidget(sheet, chart, zindex);
	}

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

	public void updateChartWidget(Worksheet sheet, Chart chart) {
		String key = Utils.getSheetUuid(sheet);
		Map<String, Widget> list = this._widgetMap.get(key);
		if (list == null) {
			return;
		}

		ChartWidget chartwgt = (ChartWidget) list.get(chart.getChartId());
		if (chartwgt == null) {
			return;
		}

		ClientAnchor anchor = chart.getPreferredSize();
		setupChartWigetAnchor(sheet, chartwgt, anchor, chartwgt.getZindex());
	}

	public void deleteChartWidget(Worksheet sheet, Chart chart) {
		String key = Utils.getSheetUuid(sheet);
		Map<String, Widget> list = this._widgetMap.get(key);
		if (list == null)
			return;

		ChartWidget imagewgt = (ChartWidget) list.remove(chart.getChartId());
		if (imagewgt != null) {
			((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).removeWidget(imagewgt);
		}
	}
}