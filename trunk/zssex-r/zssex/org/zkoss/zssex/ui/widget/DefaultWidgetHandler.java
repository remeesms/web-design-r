package org.zkoss.zssex.ui.widget;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.DeferredValue;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zss.ui.Rect;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zss.ui.Widget;
import org.zkoss.zss.ui.impl.Utils;
import org.zkoss.zss.ui.sys.SpreadsheetCtrl;
import org.zkoss.zss.ui.sys.WidgetHandler;
import org.zkoss.zssex.ui.au.out.AuRedrawWidget;

public class DefaultWidgetHandler
  implements WidgetHandler, Serializable
{
  private Spreadsheet _spreadsheet;
  private List<WidgetCtrl> _clientWidgets;
  private List<WidgetCtrl> _nonClientWidgets;
  private Ghost _ghost;
  private static String WIDGET_RES_PREFIX = "zsw_";

  public DefaultWidgetHandler()
  {
    this._clientWidgets = new ArrayList();
    this._nonClientWidgets = new ArrayList();
  }

  public boolean addWidget(Widget widget)
  {
    if (!(widget instanceof BaseWidget))
    {
      return false;
    }

    if (this._ghost == null) {
      this._ghost = newGhost();
      this._spreadsheet.appendChild(this._ghost);
    }
    WidgetCtrl ctrl = ((BaseWidget)widget).getCtrl();

    boolean r = this._ghost.appendChild(ctrl);

    if (r)
    {
      this._ghost.invalidate();

      ((BaseWidget)widget).setHandler0(this);

      this._nonClientWidgets.add(ctrl);

      if (!(this._spreadsheet.isInvalidated())) {
        Worksheet sheet = this._spreadsheet.getSelectedSheet();
        Rect rect = ((SpreadsheetCtrl)this._spreadsheet.getExtraCtrl()).getVisibleRect();
        onLoadWidgetOnDemand((BaseWidget)widget, sheet, rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom());
      }
    }

    return r;
  }

  public void updateWidgets(Worksheet sheet, int left, int top, int right, int bottom) {
    for (WidgetCtrl ctrl : this._clientWidgets) {
      Widget widget = ctrl.getWidget();
      if (widget instanceof ChartWidget)
        updateChartWidget((ChartWidget)widget, sheet, left, top, right, bottom);
    }
  }

  private void updateChartWidget(ChartWidget widget, Worksheet sheet, int left, int top, int right, int bottom)
  {
    AreaReference rng = widget.getUpdateAreaReference();
    CellReference c1 = rng.getFirstCell();
    CellReference c2 = rng.getLastCell();
    int col1 = c1.getCol();
    int row1 = c1.getRow();
    int col2 = c2.getCol();
    int row2 = c2.getRow();

    if ((row2 >= top) && (row1 <= bottom) && (col2 >= left) && (col1 <= right))
      widget.invalidate();
  }

  public boolean redrawWidget(Widget widget)
  {
    if (!(widget instanceof BaseWidget))
    {
      return false;
    }

    if (this._ghost == null)
      return false;

    Worksheet sheet = this._spreadsheet.getSelectedSheet();
    Rect rect = ((SpreadsheetCtrl)this._spreadsheet.getExtraCtrl()).getVisibleRect();
    boolean r = onLoadWidgetOnDemand((BaseWidget)widget, sheet, rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom());
    if (r)
      ((BaseWidget)widget).getCtrl().invalidate();

    return r;
  }

  public Spreadsheet getSpreadsheet() {
    return this._spreadsheet;
  }

  public boolean removeWidget(Widget widget) {
    if (!(widget instanceof BaseWidget))
      return false;

    if (((BaseWidget)widget).getHandler() != this)
      return false;

    WidgetCtrl ctrl = ((BaseWidget)widget).getCtrl();
    if (this._ghost == null) {
      this._ghost = newGhost();
      this._spreadsheet.appendChild(this._ghost);
    }
    boolean r = this._ghost.removeChild(ctrl);
    if (r)
    {
      ((BaseWidget)widget).setHandler0(null);
      ((BaseWidget)widget).setInClient(false);
      this._clientWidgets.remove(ctrl);
      this._nonClientWidgets.remove(ctrl);
    }

    return r;
  }

  public void init(Spreadsheet spreadsheet) {
    this._spreadsheet = spreadsheet;
  }

  public void onLoadOnDemand(Worksheet sheet, int left, int top, int right, int bottom) {
    if (this._ghost == null)
      return;

    int size = this._nonClientWidgets.size();
    for (int i = size - 1; i >= 0; --i) {
      WidgetCtrl ctrl = (WidgetCtrl)this._nonClientWidgets.get(i);
      BaseWidget widget = ctrl.getWidget();
      onLoadWidgetOnDemand(widget, sheet, left, top, right, bottom);
    }
  }

  private boolean onLoadWidgetOnDemand(BaseWidget widget, Worksheet sheet, int left, int top, int right, int bottom) {
    int r = widget.getRow();
    int c = widget.getColumn();
    int r2 = widget.getRow2();
    int c2 = widget.getColumn2();
    WidgetCtrl ctrl = widget.getCtrl();

    if ((r2 >= top) && (r <= bottom) && (c2 >= left) && (c <= right)) {
      responseWidgetPosition(Utils.getSheetUuid(sheet), widget);
      if (!(widget.isInClient())) {
        widget.setInClient(true);
        this._clientWidgets.add(ctrl);
        this._nonClientWidgets.remove(ctrl);
      }
      return true;
    }
    return false;
  }

  private void responseWidgetPosition(String sheetid, BaseWidget widget) {
    WidgetCtrl ctrl = widget.getCtrl();
    String uuid = ctrl.getUuid();
    this._spreadsheet.response(WIDGET_RES_PREFIX + uuid, new AuRedrawWidget(this._spreadsheet, sheetid, uuid));
  }

  public void invaliate()
  {
    Iterator iter = this._clientWidgets.iterator();

    while (iter.hasNext()) {
      WidgetCtrl ctrl = (WidgetCtrl)iter.next();
      ctrl.getWidget().setInClient(false);
      this._nonClientWidgets.add(ctrl);
    }
    this._clientWidgets.clear(); }

  private Ghost newGhost() {
    Ghost ghost = new Ghost();
    ghost.setAttribute("zsschildren", "");
    return ghost;
  }

  class DefferedRender
    implements DeferredValue
  {
    WidgetCtrl _ctrl;

    DefferedRender(WidgetCtrl paramWidgetCtrl)
    {
      this._ctrl = paramWidgetCtrl;
    }

    public String getValue() {
      StringWriter sw = new StringWriter();
      try {
        this._ctrl.redraw(sw);
      } catch (IOException e) {
        throw new UiException(e);
      }
      return sw.toString();
    }
  }
}