package org.zkoss.zssex.ui.widget;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zssex.model.TableModel;
import org.zkoss.zssex.model.impl.SimpleTableModel;
import org.zkoss.zssex.util.HTMLUtil;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

public class SimpleTable extends Div implements org.zkoss.zssex.ui.widget.Table {

	private static final long serialVersionUID = -3738264439202916632L;

	public static final String TYPE_GRID = "grid";
	public static final String TYPE_PIVOT = "pivot";

	// grid table
	private Grid _gridTable;

	// control variable
	private boolean _smartDrawTable; // whether post the smartDraw event
	private SmartDrawListener _smartDrawTableListener;

	private String _type = TYPE_GRID; // table type

	// chart related attributes
	private String _title; // chart title
	private int _intWidth = 400; // default to 400
	private int _intHeight = 200; // default to 200

	private String _paneColor; // pane's color
	private int[] _paneRGB = new int[] { 0xEE, 0xEE, 0xEE }; // pane red, green,
																// blue (0 ~
																// 255, 0 ~ 255,
																// 0 ~ 255)
	private int _paneAlpha = 255; // pane alpha transparency (0 ~ 255, default
									// to 255)

	// plot related attributes
	private int _fgAlpha = 255; // foreground alpha transparency (0 ~ 255,
								// default to 255)
	private String _bgColor;
	private int[] _bgRGB = new int[] { 0xFF, 0xFF, 0xFF }; // background red,
															// green, blue (0 ~
															// 255, 0 ~ 255, 0 ~
															// 255)
	private int _bgAlpha = 255; // background alpha transparency (0 ~ 255,
								// default to 255)

	// Font
	private Font _titleFont; // title font

	public SimpleTable() {
		init();
	}

	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);

//		renderer.render("tableModel", buildModel());
	}

	/**
	 * 构建给前端显示的model
	 * 
	 * @return
	 */
	protected Map<String, Object> buildModel() {

		// FIXME 如果没有invalidate则用缓存的table model
		return null;
	}
	

	private void init() {

		if (_smartDrawTableListener == null) {
			_smartDrawTableListener = new SmartDrawListener();
			addEventListener("onSmartDrawTable", _smartDrawTableListener);
		}

		initGridTable();

		setWidth(getIntWidth() + "px");
		setHeight(getIntHeight() + "px");
	}

	private void initGridTable() {
		try {
			_gridTable = new Grid();
			
			// [mock] //
			_gridTable.setWidth("100%");
			// _gridTable.setVflex(true); // ??

			// prepare model
			LinkedHashMap<String, Object> m;
			List<LinkedHashMap<String, Object>> datasource = new ArrayList<LinkedHashMap<String, Object>>();
			m = new LinkedHashMap<String, Object>();
			m.put("aaa", 112344);
			m.put("bbb", 5566777);
			m.put("ccc", 5566777);
			m.put("ddd", 5566777);
			datasource.add(m);
			m = new LinkedHashMap<String, Object>();
			m.put("aaa", 112344);
			m.put("bbb", 5566777);
			m.put("ccc", 5566777);
			m.put("ddd", 5566777);
			datasource.add(m);
			m = new LinkedHashMap<String, Object>();
			m.put("aaa", 112344);
			m.put("bbb", 5566777);
			m.put("ccc", 5566777);
			m.put("ddd", 5566777);
			datasource.add(m);
			m = new LinkedHashMap<String, Object>();
			m.put("aaa", 112344);
			m.put("bbb", 5566777);
			m.put("ccc", 5566777);
			m.put("ddd", 5566777);
			datasource.add(m);
			m = new LinkedHashMap<String, Object>();
			m.put("aaa", 112344);
			m.put("bbb", 5566777);
			m.put("ccc", 5566777);
			m.put("ddd", 5566777);
			datasource.add(m);
			
			ListModel gridModel = new ListModelList(datasource);
			// _gridTable.setModel(gridModel);
			

			// prepare columns
			Columns columns = new Columns();
			Column column;
			column = new Column();
			column.setLabel("label1");
			column.setSort("auto");
			columns.appendChild(column);
			column = new Column();
			column.setLabel("label2");
			column.setSort("auto");
			columns.appendChild(column);
			column = new Column();
			column.setLabel("label3");
			 column.setSort("auto");
			columns.appendChild(column);
			_gridTable.appendChild(columns);

			// prepare rows
			// Rows rows = _gridTable.getRows();
			Rows rows = new Rows();
			Row row;
			Label label;
			// row 1
			row = new Row();
			label = new Label();
			label.setValue("qewqefasdfsadf");
			row.appendChild(label);
			label = new Label();
			label.setValue("abtynysdfsadf");
			row.appendChild(label);
			label = new Label();
			label.setValue("asdf3243adf");
			row.appendChild(label);
			rows.appendChild(row);
			// row 2
			row = new Row();
			label = new Label();
			label.setValue("32423asdfsadf");
			row.appendChild(label);
			label = new Label();
			label.setValue("24asdfsadf");
			row.appendChild(label);
			label = new Label();
			label.setValue("1221sdfsadf");
			row.appendChild(label);
			rows.appendChild(row);

			_gridTable.appendChild(rows);

			_gridTable.setParent(this);

			_gridTable.renderAll();
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class SmartDrawListener implements SerializableEventListener {

		private static final long serialVersionUID = -6798556501673897827L;

		public void onEvent(Event event) throws Exception {
			doSmartDraw();
		}
	}

	private void doSmartDraw() {
		if (Strings.isBlank(getType()))
			throw new UiException("table must specify type (grid, pivot, ...)");

		if (Strings.isBlank(getWidth()))
			throw new UiException("table must specify width");

		if (Strings.isBlank(getHeight()))
			throw new UiException("table must specify height");

		try {
			this.smartUpdate("tableModel", buildModel());

		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			_smartDrawTable = false;
		}
	}

	public void setType(String type) {
		if (Objects.equals(_type, type)) {
			return;
		}
		_type = type;
		smartDrawTable();
	}

	public String getType() {
		return _type;
	}

	public void setTitle(String title) {
		if (Objects.equals(_title, title)) {
			return;
		}
		_title = title;
		smartDrawTable();
	}

	public String getTitle() {
		return _title;
	}

	@Override
	public void setWidth(String w) {
		if (Objects.equals(w, getWidth())) {
			return;
		}
		_intWidth = HTMLUtil.measureStringToInt(w);
		super.setWidth(w);
		smartDrawTable();
	}

	public int getIntWidth() {
		return _intWidth;
	}

	@Override
	public void setHeight(String h) {
		if (Objects.equals(h, getHeight())) {
			return;
		}
		_intHeight = HTMLUtil.measureStringToInt(h);
		super.setHeight(h);
		smartDrawTable();
	}

	public int getIntHeight() {
		return _intHeight;
	}

	/**
	 * @param alpha
	 *            (0 ~ 255)
	 */
	public void setPaneAlpha(int alpha) {
		if (alpha == _paneAlpha) {
			return;
		}
		if (alpha > 255 || alpha < 0) {
			alpha = 255;
		}
		_paneAlpha = alpha;
		smartDrawTable();
	}

	/**
	 * transparency, 0 ~ 255
	 */
	public int getPaneAlpha() {
		return _paneAlpha;
	}

	/**
	 * @param color
	 *            #RRGGBB format (hexdecimal).
	 */
	public void setPaneColor(String color) {
		if (Objects.equals(color, _paneColor)) {
			return;
		}
		_paneColor = color;
		if (_paneColor == null) {
			_paneRGB = null;
		} else {
			_paneRGB = new int[3];
			HTMLUtil.colorDecode(_paneColor, _paneRGB);
		}
		smartDrawTable();
	}

	/**
	 * string as #RRGGBB). default null
	 */
	public String getPaneColor() {
		return _paneColor;
	}

	/**
	 * Get the pane color in int array (0: red, 1: green, 2:blue). null means
	 * default.
	 */
	public int[] getPaneRGB() {
		return _paneRGB;
	}

	/**
	 * Set the foreground alpha (transparency, 0 ~ 255).
	 * 
	 * @param alpha
	 *            transparency, 0 ~ 255
	 */
	public void setFgAlpha(int alpha) {
		if (alpha == _fgAlpha) {
			return;
		}

		if (alpha > 255 || alpha < 0) {
			alpha = 255;
		}
		_fgAlpha = alpha;
		smartDrawTable();
	}

	/**
	 * Get the foreground alpha (transparency, 0 ~ 255, opacue).
	 */
	public int getFgAlpha() {
		return _fgAlpha;
	}

	/**
	 * Set the background alpha (transparency, 0 ~ 255).
	 * 
	 * @param alpha
	 *            the transparency of background color (0 ~ 255, default to 255
	 *            opaque).
	 */
	public void setBgAlpha(int alpha) {
		if (alpha == _bgAlpha) {
			return;
		}
		if (alpha > 255 || alpha < 0) {
			alpha = 255;
		}
		_bgAlpha = alpha;
		smartDrawTable();
	}

	/**
	 * Get the background alpha (transparency, 0 ~ 255, opacue).
	 */
	public int getBgAlpha() {
		return _bgAlpha;
	}

	/**
	 * Set the background color of the chart.
	 * 
	 * @param color
	 *            in #RRGGBB format (hexdecimal).
	 */
	public void setBgColor(String color) {
		if (Objects.equals(color, _bgColor)) {
			return;
		}
		_bgColor = color;
		if (_bgColor == null) {
			_bgRGB = null;
		} else {
			_bgRGB = new int[3];
			HTMLUtil.colorDecode(_bgColor, _bgRGB);
		}
		smartDrawTable();
	}

	/**
	 * Get the background color of the chart (in string as #RRGGBB). null means
	 * default.
	 */
	public String getBgColor() {
		return _bgColor;
	}

	/**
	 * Get the background color in int array (0: red, 1: green, 2:blue). null
	 * means default.
	 */
	public int[] getBgRGB() {
		return _bgRGB;
	}

	/**
	 * Returns the title font of this chart. If you saw squares rather than
	 * correct words in title, check whether the default title font supports
	 * your characters (e.g. Chinese). You probably have to set this font
	 * accordingly.
	 * 
	 * @return the title font
	 */
	public Font getTitleFont() {
		return _titleFont;
	}

	/**
	 * Sets the title font of this chart. If you saw squares rather than correct
	 * words in title, check whether the default title font supports your
	 * characters (e.g. Chinese). You probably have to set this font
	 * accordingly.
	 * 
	 * @param font
	 *            the title font of this chart
	 */
	public void setTitleFont(Font font) {
		if (Objects.equals(font, _titleFont)) {
			return;
		}
		_titleFont = font;
		smartDrawTable();
	}

	/**
	 * mark a draw flag to inform that this Chart needs update.
	 */
	protected void smartDrawTable() {
		if (_smartDrawTable) { // already mark smart draw
			return;
		}
		_smartDrawTable = true;
		Events.postEvent("onSmartDrawTable", this, null);
	}

	public boolean addEventListener(String evtnm, EventListener listener) {
		final boolean ret = super.addEventListener(evtnm, listener);
		if (Events.ON_CLICK.equals(evtnm) && ret)
			smartDrawTable(); // since Area has to generate
		return ret;
	}

	// Cloneable//
	public Object clone() {
		final SimpleTable clone = (SimpleTable) super.clone();

		// Due to the not unique ID of the area component creating in
		// JFreeChartEngine, we have to clear
		// all its children first.
		clone.getChildren().clear();
		clone._smartDrawTableListener = null;
		clone._smartDrawTable = false;
		clone.init();
		clone.doSmartDraw();

		return clone;
	}

	public void setModel(TableModel model) {
		if (getModel() != model) {
//			if (_model != null) {
//				_model.removeTableDataListener(_dataListener);
//			}
			this._gridTable.setModel((SimpleTableModel)model);
//			initDataListener();
		}
		// Always redraw
		smartDrawTable();
	}

	public TableModel getModel() {
		return (TableModel)this._gridTable.getModel();
	}

}