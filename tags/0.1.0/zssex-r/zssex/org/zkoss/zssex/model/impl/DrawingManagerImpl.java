package org.zkoss.zssex.model.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrameNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.zkoss.poi.hssf.record.EscherAggregate;
import org.zkoss.poi.hssf.record.NameRecord;
import org.zkoss.poi.hssf.usermodel.HSSFChartX;
import org.zkoss.poi.hssf.usermodel.HSSFCombo;
import org.zkoss.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.zkoss.poi.hssf.usermodel.HSSFPatriarch;
import org.zkoss.poi.hssf.usermodel.HSSFPatriarchHelper;
import org.zkoss.poi.hssf.usermodel.HSSFPicture;
import org.zkoss.poi.hssf.usermodel.HSSFShape;
import org.zkoss.poi.hssf.usermodel.HSSFSheet;
import org.zkoss.poi.hssf.usermodel.HSSFSimpleShape;
import org.zkoss.poi.hssf.usermodel.HSSFWorkbook;
import org.zkoss.poi.ss.formula.ptg.Area3DPtg;
import org.zkoss.poi.ss.formula.ptg.Ptg;
import org.zkoss.poi.ss.usermodel.Chart;
import org.zkoss.poi.ss.usermodel.ClientAnchor;
import org.zkoss.poi.ss.usermodel.Combo;
import org.zkoss.poi.ss.usermodel.Drawing;
import org.zkoss.poi.ss.usermodel.Picture;
import org.zkoss.poi.ss.usermodel.PictureData;
import org.zkoss.poi.ss.usermodel.Sheet;
import org.zkoss.poi.ss.usermodel.Workbook;
import org.zkoss.poi.ss.usermodel.ZssChartX;
import org.zkoss.poi.ss.usermodel.charts.AxisCrosses;
import org.zkoss.poi.ss.usermodel.charts.AxisPosition;
import org.zkoss.poi.ss.usermodel.charts.ChartAxis;
import org.zkoss.poi.ss.usermodel.charts.ChartAxisFactory;
import org.zkoss.poi.ss.usermodel.charts.ChartData;
import org.zkoss.poi.ss.usermodel.charts.ChartGrouping;
import org.zkoss.poi.ss.usermodel.charts.ChartLegend;
import org.zkoss.poi.ss.usermodel.charts.ChartType;
import org.zkoss.poi.ss.usermodel.charts.LegendPosition;
import org.zkoss.poi.ss.usermodel.charts.ValueAxis;
import org.zkoss.poi.xssf.usermodel.XSSFChart;
import org.zkoss.poi.xssf.usermodel.XSSFChartX;
import org.zkoss.poi.xssf.usermodel.XSSFClientAnchor;
import org.zkoss.poi.xssf.usermodel.XSSFClientAnchorHelper;
import org.zkoss.poi.xssf.usermodel.XSSFCombo;
import org.zkoss.poi.xssf.usermodel.XSSFDrawing;
import org.zkoss.poi.xssf.usermodel.XSSFGraphicFrame;
import org.zkoss.poi.xssf.usermodel.XSSFName;
import org.zkoss.poi.xssf.usermodel.XSSFPicture;
import org.zkoss.poi.xssf.usermodel.XSSFPictureHelper;
import org.zkoss.poi.xssf.usermodel.XSSFSheet;
import org.zkoss.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.poi.xssf.usermodel.charts.XSSFBar3DChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFBarChartData;
import org.zkoss.zss.model.Book;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zss.model.impl.DrawingManager;
import org.zkoss.zss.model.impl.XSSFSheetImpl;

public class DrawingManagerImpl
  implements DrawingManager
{
  private Sheet _sheet;
  private List<Picture> _pictures;
  private List<ZssChartX> _charts;
  private List<Combo> _combos;
  private Map<Chart, ZssChartX> _chartXMap;

  public DrawingManagerImpl(Sheet sheet)
  {
    this._sheet = sheet;
  }

  public List<Combo> getCombos() {
    if (this._combos == null)
      initDrawings(this._sheet);

    return this._combos;
  }

  public List<Picture> getPictures() {
    if (this._pictures == null)
      initDrawings(this._sheet);

    return this._pictures;
  }

  public List<ZssChartX> getChartXs() {
    if (this._charts == null)
      initDrawings(this._sheet);

    return this._charts;
  }

  public List<Chart> getCharts() {
    if (this._chartXMap == null)
      return new ArrayList();

    return new ArrayList(this._chartXMap.keySet());
  }

  private void initDrawings(Sheet sheet) {
    if (this._combos == null)
      this._combos = new ArrayList();

    if (this._pictures == null)
      this._pictures = new ArrayList();

    if (this._charts == null) {
      this._charts = new ArrayList();
      this._chartXMap = new HashMap();
    }

    if (sheet instanceof HSSFSheet)
      initHSSFDrawings((HSSFSheet)sheet);
    else
      initXSSFDrawings((XSSFSheet)sheet);
  }

  private void initXSSFDrawings(XSSFSheet sheet)
  {
    XSSFDrawing patriarch = ((XSSFSheetImpl)sheet).getDrawingPatriarch();
    if (patriarch != null) {
      CTDrawing ctdrawing = patriarch.getCTDrawing();
      for (CTTwoCellAnchor anchor : ctdrawing.getTwoCellAnchorArray()) {
        CTMarker from = anchor.getFrom();
        CTMarker to = anchor.getTo();
        XSSFClientAnchor xanchor = ((from != null) && (to != null)) ? XSSFClientAnchorHelper.newXSSFClientAnchor(from, to) : null;
        CTPicture pic = anchor.getPic();
        if (pic != null) {
          XSSFPicture xpicture = XSSFPictureHelper.newXSSFPicture(patriarch, xanchor, pic);
          this._pictures.add(xpicture);
        } else {
          CTGraphicalObjectFrame gfrm = anchor.getGraphicFrame();
          XSSFChartX chartX = createXSSFChartX(patriarch, gfrm, xanchor);
          this._charts.add(chartX);
          this._chartXMap.put(chartX.getChart(), chartX);
        }
      }

    }

    XSSFWorkbook wb = sheet.getWorkbook();
    int sheetIndex = wb.getSheetIndex(sheet);
    XSSFName name = wb.getBuiltInName("_xlnm._FilterDatabase", sheetIndex);
    if (name != null) {
      String fmla = name.getRefersToFormula();
      addComboFromFormula(fmla);
    }
  }

  private XSSFChartX createXSSFChartX(XSSFDrawing patriarch, CTGraphicalObjectFrame gfrm, XSSFClientAnchor xanchor)
  {
    String name = gfrm.getNvGraphicFramePr().getCNvPr().getName();
    CTGraphicalObject gobj = gfrm.getGraphic();
    CTGraphicalObjectData gdata = gobj.getGraphicData();
    String chartId = gdata.getDomNode().getFirstChild().getAttributes().getNamedItemNS("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id").getNodeValue();
    String uri = gdata.getUri();
    XSSFChartX chartX = new XSSFChartX(patriarch, xanchor, name, chartId);
    return chartX;
  }

  private ZssChartX getChartXByChart(Chart chart) {
    return ((ZssChartX)this._chartXMap.get(chart));
  }

  private void addCombo(int colFirst, int colSecond, int row) {
    for (int i = colFirst; i <= colSecond; ++i) {
      XSSFCombo combo = new XSSFCombo(i, row);
      this._combos.add(combo);
    }
  }

  private void addComboFromFormula(String fmla) {
    int colFirst = getColFirstFromFormula(fmla);
    int colSecond = getColSecondFromFormula(fmla);
    int row = getRowFromFormula(fmla);
    for (int i = colFirst; i <= colSecond; ++i) {
      XSSFCombo combo = new XSSFCombo(i, row);
      this._combos.add(combo);
    }
  }

  private int getColFirstFromFormula(String fmla)
  {
    int first = fmla.indexOf("$");
    int second = fmla.indexOf("$", first + 1);
    String col = fmla.substring(first + 1, second);

    return convertColNumberFromChars(col);
  }

  private int getColSecondFromFormula(String fmla)
  {
    int first = fmla.indexOf(":$");
    int second = fmla.indexOf("$", first + 2);
    String col = fmla.substring(first + 2, second);

    return convertColNumberFromChars(col);
  }

  private int convertColNumberFromChars(String col) {
    col = col.toUpperCase();
    char[] charArray = col.toCharArray();
    int colNumber = 0;
    for (int i = 0; i < charArray.length; ++i) {
      colNumber = colNumber * 26 + charArray[i] - 65 + 1;
    }

    return (colNumber - 1);
  }

  private int getRowFromFormula(String fmla) {
    int first = fmla.indexOf("$");
    int second = fmla.indexOf("$", first + 1);
    int sep = fmla.indexOf(":");
    String row = fmla.substring(second + 1, sep);

    return (Integer.parseInt(row) - 1);
  }

  private void initHSSFDrawings(HSSFSheet sheet)
  {
    Ptg[] ptgs;
    int i;
    HSSFPatriarch patriarch = sheet.getDrawingPatriarch();

    if (patriarch != null) {
      EscherAggregate drawingAggregate = new HSSFPatriarchHelper(patriarch).getBoundAggregate();
      List<DrawingAggregateRecord> recordList = (List<DrawingAggregateRecord>)drawingAggregate.getAggregateRecords();
      for (DrawingAggregateRecord r : recordList) {
        r.decodeShape();
      }

      for (HSSFShape shape : patriarch.getChildren()) {
        if (shape instanceof HSSFPicture) {
          this._pictures.add((HSSFPicture)shape);
        } else if (shape instanceof HSSFChartX) {
          HSSFSimpleShape simpleShape = (HSSFSimpleShape)shape;
          if (simpleShape.getShapeType() == 20) {
            HSSFCombo combo = new HSSFCombo(null, simpleShape.getAnchor());
            this._combos.add(combo);
          } else {
            this._charts.add((HSSFChartX)shape);
            this._chartXMap.put(((HSSFChartX)shape).getChart(), (ZssChartX)shape);
          }
        } else {
          Class clazz = shape.getClass();
          System.out.println(clazz.getName());
        }

      }

    }

    HSSFWorkbook wb = sheet.getWorkbook();
    List autoFilters = sheet.getWorkbook().getAllAutofilters();
    Iterator iterator = autoFilters.iterator();
    int sheetIndex = wb.getSheetIndex(sheet);
    while (iterator.hasNext()) {
      NameRecord nr = (NameRecord)iterator.next();
      ptgs = nr.getNameDefinition();
      for (i = 0; i < ptgs.length; ++i)
      {
        HSSFEvaluationWorkbook ewb = HSSFEvaluationWorkbook.create(wb);
        if (ptgs[i] instanceof Area3DPtg) {
          Area3DPtg ptg = (Area3DPtg)ptgs[i];
          if (sheetIndex == ewb.convertFromExternSheetIndex(ptg.getExternSheetIndex()))
            addCombo(ptg.getFirstColumn(), ptg.getLastColumn(), ptg.getFirstRow());
        }
      }
    }
  }

  public ZssChartX addChartX(Worksheet sheet, ClientAnchor anchor, ChartData data, ChartType type, ChartGrouping grouping, LegendPosition pos)
  {
    Drawing drawing = sheet.createDrawingPatriarch();
    Chart chart = drawing.createChart(anchor);
    switch (type.ordinal() + 1)
    {
    case 1:
    case 2:
    case 3:
    case 4:
      chart.getOrCreateView3D();
      break;
    case 6:
    case 7:
      chart.getOrCreateView3D();
      ((XSSFBar3DChartData)data).setGrouping(grouping);
      break;
    case 5:
    case 8:
      ((XSSFBarChartData)data).setGrouping(grouping);
    }

    ChartAxis bottomAxis = createChartAxis(chart, type, AxisPosition.BOTTOM);
    if (bottomAxis != null) {
      ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
      bottomAxis.setCrosses(AxisCrosses.AUTO_ZERO);
      leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
      chart.plot(data, new ChartAxis[] { bottomAxis, leftAxis });
    } else {
      chart.plot(data, new ChartAxis[0]);
    }
    if (pos != null) {
      ChartLegend legend = chart.getOrCreateLegend();
      legend.setPosition(pos);
    }
    XSSFClientAnchor xanchor = (XSSFClientAnchor)anchor;
    XSSFGraphicFrame frame = ((XSSFChart)chart).getGraphicFrame();
    CTGraphicalObjectFrame gfrm = frame.getCTGraphicalObjectFrame();
    ZssChartX chartX = createXSSFChartX((XSSFDrawing)drawing, gfrm, xanchor);
    if (this._charts == null) {
      this._charts = new ArrayList();
      this._chartXMap = new HashMap();
    }
    this._charts.add(chartX);
    this._chartXMap.put(chartX.getChart(), chartX);
    return chartX;
  }

  private ChartAxis createChartAxis(Chart chart, ChartType type, AxisPosition pos) {
    switch (type.ordinal() + 1)
    {
    case 4:
    case 10:
    case 9:
      return null;
    case 1:
    case 2:
    case 5:
    case 6:
    case 7:
    case 8:
    case 11:
    case 13:
    	return chart.getChartAxisFactory().createValueAxis(pos);
    case 12:
    	return chart.getChartAxisFactory().createCategoryAxis(pos);
    case 3:
    case 14:
    case 15:
    case 16:
    case 17:
    case 18:
    }
    throw new UnsupportedOperationException("Chart type:" + type);
  }

  public Picture addPicture(Worksheet sheet, ClientAnchor anchor, byte[] data, int format)
  {
    Workbook book = sheet.getWorkbook();
    int pictureIndex = book.addPicture(data, format);
    Drawing drawing = sheet.createDrawingPatriarch();
    Picture pic = drawing.createPicture(anchor, pictureIndex);
    if (this._pictures == null)
      this._pictures = new ArrayList();

    this._pictures.add(pic);
    return pic;
  }

  public void deletePicture(Worksheet sheet, Picture pic)
  {
    Book book = (Book)sheet.getWorkbook();
    Drawing drawing = sheet.createDrawingPatriarch();
    PictureData pd = pic.getPictureData();
    drawing.deletePicture(pic);
    if ((pd != null) && (pd.getRelationCounter() == 0))
      book.deletePictureData(pd);

    if (this._pictures != null)
      this._pictures.remove(pic);
  }

  public void movePicture(Worksheet sheet, Picture picture, ClientAnchor anchor)
  {
    Workbook book = sheet.getWorkbook();
    Drawing drawing = sheet.createDrawingPatriarch();
    drawing.movePicture(picture, anchor);
  }

  public void moveChart(Worksheet sheet, Chart chart, ClientAnchor anchor)
  {
    Workbook book = sheet.getWorkbook();
    Drawing drawing = sheet.createDrawingPatriarch();
    ZssChartX chartX = getChartXByChart(chart);
    if (chartX != null)
      drawing.moveChart(chartX, anchor);
  }

  public void deleteChart(Worksheet sheet, Chart chart)
  {
    Drawing drawing = sheet.createDrawingPatriarch();
    ZssChartX chartX = getChartXByChart(chart);
    drawing.deleteChart(chartX);
    if ((chartX != null) && (this._charts != null)) {
      this._charts.remove(chartX);
      this._chartXMap.remove(chart);
    }
  }
}