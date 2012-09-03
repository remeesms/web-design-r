package org.zkoss.zssex.ui.widget;

import java.util.Date;

import org.zkoss.poi.ss.usermodel.PivotTable;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.zk.ui.Component;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zssex.util.SpreadsheetHelper;
import org.zkoss.zssex.util.TableHelper;

public class TableWidget extends BaseWidget {

	private Worksheet _sheet;
	private int _zindex;
	private PivotTable _tableP;
	private Table _table;
	private int _outcol1;
	private int _outcol2;
	private int _outrow1;
	private int _outrow2;

	private Table inner() {
		return this._table;
	}

	public TableWidget(Worksheet sheet, PivotTable tableP, int zindex) {
		this._sheet = sheet;
		this._zindex = zindex;
		this._tableP = tableP;

		this.createTable();

		//setId(String.valueOf(this._tableP.getCacheId())); // FIXME 检查正误
		setId(String.valueOf(new Date().getTime())); // FIXME 不是这么做的
		setSizable(false);
		setMovable(false);
		setFocusable(false);
		setCtrlKeys("#del"); // FIXME 检查意义
	}

	protected Component getInnerComponent() {
		return (Component) inner();
	}

	public String getType() {
		return inner().getType();
	}

	public void setType(String type) {
		inner().setType(type);
	}

	public String getTitle() {
		return inner().getTitle();
	}

	public void setTitle(String title) {
		inner().setTitle(title);
	}

	public void setWidth(String w) {
		inner().setWidth(w);
	}

	public String getWidth() {
		return inner().getWidth();
	}

	public void setHeight(String h) {
		inner().setHeight(h);
	}

	public String getHeight() {
		return inner().getHeight();
	}

	private void initUpdateAreaReference() {
		this._outcol1 = Integer.MAX_VALUE;
		this._outrow1 = Integer.MAX_VALUE;
		this._outcol2 = Integer.MIN_VALUE;
		this._outrow2 = Integer.MIN_VALUE;
		;
	}

	private void createTable() {
		this._table = newTable();
		if (this._table != null) {
			initTable();
		}
	}

	private Table newTable() {
		Table table = TableHelper.createTable(this._tableP);
		table.setParent(getCtrl());
		return table;
	}
	
	public void invalidate() {
		if (this._table != null) {
			initTable();
		}
	}

	public AreaReference getUpdateAreaReference() {
		CellReference c1 = new CellReference(this._outrow1, this._outcol1, true, true);
		CellReference c2 = new CellReference(this._outrow2, this._outcol2, true, true);
		return new AreaReference(c1, c2);
	}

	private void prepareAnchorPosition() {
//		AreaReference areaReference = this._tableP.getLocationRef();
//		CellReference firstCell = areaReference.getFirstCell();
//		CellReference lastCell = areaReference.getLastCell();
		
		// FIXME mock ////////////////////////////////////
		CellReference firstCell = new CellReference(3,3);
		CellReference lastCell = new CellReference(7,7);
		AreaReference areaReference = new AreaReference(firstCell, lastCell);
		
		if (areaReference != null) {
			int row = firstCell.getRow();
			int col = firstCell.getCol();
			int row2 = lastCell.getRow();
			int col2 = lastCell.getCol();
			int height = SpreadsheetHelper.getHeightInPx(this._sheet, areaReference);
			int width = SpreadsheetHelper.getWidthInPx(this._sheet, areaReference);
			int left = SpreadsheetHelper.getLeftFraction(this._sheet, areaReference);
			int top = SpreadsheetHelper.getTopFraction(this._sheet, areaReference);
			
			setRow(row);
			setColumn(col);
			setRow2(row2);
			setColumn2(col2);
			setZindex(this._zindex);
			setWidth((width - 1) + "px"); // FIXME -1 ?
			setHeight((height - 1) + "px"); // FIXME -1 ?
			setLeft(left - 1);
			setTop(top - 1);
		}
	}

	private void initTable() {
		initUpdateAreaReference();
		prepareAnchorPosition();
		TableHelper.drawTable(this._table, this._sheet, this._tableP);
	}

	public String getWidgetType() {
		return "table";
	}
}