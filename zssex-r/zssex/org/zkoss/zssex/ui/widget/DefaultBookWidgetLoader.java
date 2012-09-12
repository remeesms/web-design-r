package org.zkoss.zssex.ui.widget;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.image.AImage;
import org.zkoss.poi.ss.usermodel.Chart;
import org.zkoss.poi.ss.usermodel.ClientAnchor;
import org.zkoss.poi.ss.usermodel.Picture;
import org.zkoss.poi.ss.usermodel.PictureData;
import org.zkoss.poi.ss.usermodel.PivotTable;
import org.zkoss.poi.ss.usermodel.ZssChartX;
import org.zkoss.poi.xssf.usermodel.XSSFSheet;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zss.model.impl.DrawingManager;
import org.zkoss.zss.model.impl.SheetCtrl;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zss.ui.Widget;
import org.zkoss.zss.ui.impl.HeaderPositionHelper;
import org.zkoss.zss.ui.impl.Utils;
import org.zkoss.zss.ui.sys.SpreadsheetCtrl;
import org.zkoss.zss.ui.sys.WidgetLoader;
import org.zkoss.zssex.util.SpreadsheetHelper;

public class DefaultBookWidgetLoader implements WidgetLoader, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
		prepareTableWidgets(sheet, list);
		if ((list != null) && (list.size() > 0))
			this._widgetMap.put(key, list);
	}

	private void preparePictureWidgets(Worksheet sheet, DrawingManager dm, Map<String, Widget> list) {
		List<Picture> pictures = dm.getPictures();
		if ((pictures == null) || (pictures.size() == 0)) {
			return;
		}

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
		if ((charts == null) || (charts.size() == 0)) {
			return;
		}

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
	private void prepareTableWidgets(Worksheet sheet, Map<String, Widget> list) {
		// TODO 得到model
		List<PivotTable> pTableList = sheet.getPivotTables();
//		((XSSFSheet)sheet).getTables();
		// FIXME mock
		pTableList= new ArrayList<PivotTable>();
//		pTableList.add(null);
		
		if (pTableList == null || pTableList.size() == 0) {
			return;
		}

		int zindex = 200 + list.size();
		for (PivotTable tableP : pTableList) {
			try {
				TableWidget tablewgt = newTableWidget(sheet, tableP, zindex++);
				if (tablewgt != null) {
					list.put(tablewgt.getId(), tablewgt);
					((SpreadsheetCtrl) this._spreadsheet.getExtraCtrl()).addWidget(tablewgt);
				}
			} catch (IOException e) {
				throw UiException.Aide.wrap(e);
			}
		}
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
			int height = SpreadsheetHelper.getHeightInPx(sheet, anchor);
			int width = SpreadsheetHelper.getWidthInPx(sheet, anchor);
			int left = SpreadsheetHelper.getLeftFraction(sheet, anchor);
			int top = SpreadsheetHelper.getTopFraction(sheet, anchor);
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
			int height = SpreadsheetHelper.getHeightInPx(sheet, anchor);
			int width = SpreadsheetHelper.getWidthInPx(sheet, anchor);
			int left = SpreadsheetHelper.getLeftFraction(sheet, anchor);
			int top = SpreadsheetHelper.getTopFraction(sheet, anchor);
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

	protected TableWidget newTableWidget(Worksheet sheet, PivotTable tableP, int zindex) throws IOException {
		return new TableWidget(sheet, tableP, zindex);
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