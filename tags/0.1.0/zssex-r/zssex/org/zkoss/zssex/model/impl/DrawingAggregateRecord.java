package org.zkoss.zssex.model.impl;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.zkoss.poi.ddf.EscherChildAnchorRecord;
import org.zkoss.poi.ddf.EscherClientAnchorRecord;
import org.zkoss.poi.ddf.EscherClientDataRecord;
import org.zkoss.poi.ddf.EscherComplexProperty;
import org.zkoss.poi.ddf.EscherContainerRecord;
import org.zkoss.poi.ddf.EscherOptRecord;
import org.zkoss.poi.ddf.EscherProperty;
import org.zkoss.poi.ddf.EscherRecord;
import org.zkoss.poi.ddf.EscherRecordFactory;
import org.zkoss.poi.ddf.EscherSimpleProperty;
import org.zkoss.poi.ddf.EscherSpRecord;
import org.zkoss.poi.ddf.EscherSpgrRecord;
import org.zkoss.poi.hssf.record.DrawingRecord;
import org.zkoss.poi.hssf.record.EscherAggregate;
import org.zkoss.poi.hssf.record.LabelSSTRecord;
import org.zkoss.poi.hssf.record.ObjRecord;
import org.zkoss.poi.hssf.record.Record;
import org.zkoss.poi.hssf.record.RecordBase;
import org.zkoss.poi.hssf.record.chart.Chart3DRecord;
import org.zkoss.poi.hssf.record.chart.ChartRecord;
import org.zkoss.poi.hssf.record.chart.ChartTitleFormatRecord;
import org.zkoss.poi.hssf.record.chart.LegendRecord;
import org.zkoss.poi.hssf.record.chart.LinkedDataRecord;
import org.zkoss.poi.hssf.record.chart.SeriesIndexRecord;
import org.zkoss.poi.hssf.record.chart.SeriesTextRecord;
import org.zkoss.poi.hssf.record.chart.TextRecord;
import org.zkoss.poi.hssf.record.chart.ValueRangeRecord;
import org.zkoss.poi.hssf.usermodel.HSSFAnchor;
import org.zkoss.poi.hssf.usermodel.HSSFChart;
import org.zkoss.poi.hssf.usermodel.HSSFChartX;
import org.zkoss.poi.hssf.usermodel.HSSFChildAnchor;
import org.zkoss.poi.hssf.usermodel.HSSFClientAnchor;
import org.zkoss.poi.hssf.usermodel.HSSFPatriarch;
import org.zkoss.poi.hssf.usermodel.HSSFPatriarchHelper;
import org.zkoss.poi.hssf.usermodel.HSSFPicture;
import org.zkoss.poi.hssf.usermodel.HSSFShape;
import org.zkoss.poi.hssf.usermodel.HSSFShapeGroup;
import org.zkoss.poi.hssf.usermodel.HSSFSheet;

public class DrawingAggregateRecord extends Record
{
  public static final short sid = 9877;
  private EscherAggregate _agg;
  private Record _drawing;
  private Record _obj;
  private Record[] _chartStream;
  private HSSFShape _shape;

  public DrawingAggregateRecord(EscherAggregate agg, List<RecordBase> records, int drawingLoc, int objLoc, int chartStartLoc, int chartEndLoc)
  {
    this._agg = agg;
    this._drawing = ((Record)records.get(drawingLoc));
    if (objLoc >= 0)
      this._obj = ((Record)records.get(objLoc));

    if ((chartStartLoc >= 0) && (chartStartLoc < chartEndLoc)) {
      int sz = chartEndLoc - chartStartLoc + 1;
      this._chartStream = new Record[sz];
      for (int j = 0; j < sz; ++j)
        this._chartStream[j] = ((Record)records.get(j + chartStartLoc));
    }
  }

  public short getSid()
  {
    return 9877;
  }

  public int getRecordSize()
  {
    return (this._drawing.getRecordSize() + ((this._obj != null) ? this._obj.getRecordSize() : 0) + ((this._chartStream != null) ? getRecordSize(this._chartStream) : 0));
  }

  public int serialize(int offset, byte[] data)
  {
    int total = 0;
    int sz = this._drawing.serialize(offset, data);
    total += sz;
    offset += sz;
    if (this._obj != null) {
      sz = this._obj.serialize(offset, data);
      total += sz;
      offset += sz;
      if (this._chartStream != null) {
        sz = serialize(this._chartStream, offset, data);
        total += sz;
      }
    }
    return total;
  }

  public DrawingRecord getDrawingRecord() {
    return ((DrawingRecord)this._drawing);
  }

  public ObjRecord getObjRecord() {
    return ((ObjRecord)this._obj);
  }

  public ChartRecord[] getChartStream() {
    return ((ChartRecord[])(ChartRecord[])this._chartStream);
  }

  public HSSFShape getShape() {
    if (this._shape == null)
      decodeShape();

    return this._shape;
  }

  void decodeShape() {
    if (this._shape == null) {
      int pos = 0;
      EscherRecordFactory recordFactory = this._agg.getRecordFactory();
      byte[] buffer = getDrawingRecord().getData();
      EscherRecord r = recordFactory.createRecord(buffer, 0);
      int bytesRead = r.fillFields(buffer, 0, recordFactory);
      // if ((!($assertionsDisabled)) && (bytesRead != buffer.length)) throw new AssertionError();
      this._shape = decodeEscherRecord(r, null);
    }
  }

  private HSSFShape decodeEscherRecord(EscherRecord escherRecord, HSSFShapeGroup parent) {
    List recordList = escherRecord.getChildRecords();
    Iterator recordIter = recordList.iterator();
    EscherChildAnchorRecord childAnchorRecord = null;
    EscherClientAnchorRecord clientAnchorRecord = null;
    EscherOptRecord optRecord = null;
    EscherSpRecord spRecord = null;
    EscherClientDataRecord dataRecord = null;
    EscherSpgrRecord spgrRecord = null;
    List<EscherContainerRecord> containers = new ArrayList(2);
    HSSFShape shape = null;

    while (recordIter.hasNext()) {
      EscherRecord childRecord = (EscherRecord)recordIter.next();

      switch (childRecord.getRecordId())
      {
      case -4087:
        spgrRecord = (EscherSpgrRecord)childRecord;
        break;
      case -4086:
        spRecord = (EscherSpRecord)childRecord;
        break;
      case -4085:
        optRecord = (EscherOptRecord)childRecord;
        break;
      case -4081:
        childAnchorRecord = (EscherChildAnchorRecord)childRecord;
        break;
      case -4080:
        clientAnchorRecord = (EscherClientAnchorRecord)childRecord;
        break;
      case -4079:
        dataRecord = (EscherClientDataRecord)childRecord;
        break;
      case -4084:
      case -4083:
      case -4082:
      default:
        if ((childRecord instanceof EscherContainerRecord) && (childRecord.getChildRecords().size() > 0))
          containers.add((EscherContainerRecord)childRecord);
      }

    }

    if (spRecord != null)
    {
      if (spRecord.isPatriarch()) {
        if (spgrRecord != null)
          this._agg.getPatriarch().setCoordinates(spgrRecord.getRectX1(), spgrRecord.getRectX2(), spgrRecord.getRectX2(), spgrRecord.getRectY2());
      }
      else {
        int dx1;
        int dy1;
        int dx2;
        int dy2;
        HSSFAnchor anchor = null;
        if (childAnchorRecord != null) {
          dx1 = Math.min(1023, childAnchorRecord.getDx1());
          dy1 = Math.min(255, childAnchorRecord.getDy1());
          dx2 = Math.min(1023, childAnchorRecord.getDx2());
          dy2 = Math.min(255, childAnchorRecord.getDy2());
          anchor = new HSSFChildAnchor(dx1, dy1, dx2, dy2);
        } else if (clientAnchorRecord != null)
        {
          dx1 = Math.min(1023, clientAnchorRecord.getDx1());
          dy1 = Math.min(255, clientAnchorRecord.getDy1());
          dx2 = Math.min(1023, clientAnchorRecord.getDx2());
          dy2 = Math.min(255, clientAnchorRecord.getDy2());
          short col1 = clientAnchorRecord.getCol1();
          int row1 = clientAnchorRecord.getRow1();
          short col2 = clientAnchorRecord.getCol2();
          int row2 = clientAnchorRecord.getRow2();
          anchor = new HSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1, col2, row2);
        }
        if ((spRecord.isHaveAnchor()) && (anchor != null)) {
          if (spRecord.isGroup())
            return (parent = createGroup(parent, anchor));
          if (optRecord != null)
          {
            int shapeType = spRecord.getOptions() >>> 4;

            switch (shapeType)
            {
            case 75:
              shape = createPicture(parent, optRecord, anchor);
              break;
            case 201:
              shape = createChart(parent, optRecord, anchor);
              break;
            default:
              System.out.println("unknown shapeType:" + Integer.toHexString(shapeType));
            }
          }
        }
      }

    }

    for (EscherContainerRecord containerRecord : containers) {
      HSSFShape shape0 = decodeEscherRecord(containerRecord, parent);
      if ((shape == null) && (shape0 != null))
        shape = shape0;

    }

    return shape;
  }

  private HSSFShapeGroup createGroup(HSSFShapeGroup parent, HSSFAnchor anchor) {
    if (parent == null)
      return this._agg.getPatriarch().createGroup((HSSFClientAnchor)anchor);

    return parent.createGroup((HSSFChildAnchor)anchor);
  }

  private HSSFPicture createPicture(HSSFShapeGroup parent, EscherOptRecord optRecord, HSSFAnchor anchor)
  {
    int pictureId = -1;
    String pictureName = null;
    String alt = null;
    try {
      for (EscherProperty pro : optRecord.getEscherProperties())
        switch (pro.getPropertyNumber())
        {
        case 260:
          pictureId = ((EscherSimpleProperty)pro).getPropertyValue();
          break;
        case 896:
          pictureName = new String(((EscherComplexProperty)pro).getComplexData(), "UTF-16LE").trim();
          break;
        case 897:
          alt = new String(((EscherComplexProperty)pro).getComplexData(), "UTF-16LE").trim();
        }
    }
    catch (UnsupportedEncodingException e)
    {
    }

    HSSFPicture picture = (parent == null) ? this._agg.getPatriarch().createPicture((HSSFClientAnchor)anchor, pictureId) : parent.createPicture((HSSFChildAnchor)anchor, pictureId);

    picture.setName(pictureName);
    picture.setAlt(alt);
    return picture;
  }

  private HSSFChartX createChart(HSSFShapeGroup parent, EscherOptRecord optRecord, HSSFAnchor anchor) {
    String chartName = null;
    try {
      for (EscherProperty pro : optRecord.getEscherProperties())
        switch (pro.getPropertyNumber())
        {
        case 896:
          chartName = new String(((EscherComplexProperty)pro).getComplexData(), "UTF-16LE").trim();
        }
    }
    catch (UnsupportedEncodingException e)
    {
    }

    HSSFSheet sheet = new HSSFPatriarchHelper(this._agg.getPatriarch()).getSheet();
    HSSFChart chart = decodeChartStream(sheet);
    HSSFChartX chartX = (parent == null) ? this._agg.getPatriarch().createChart((HSSFClientAnchor)anchor, chart) : parent.createChart((HSSFChildAnchor)anchor, chart);

    chartX.setName(chartName);
    return chartX;
  }

  private HSSFChart decodeChartStream(HSSFSheet sheet)
  {
    if (this._chartStream != null)
      return decodeChartStream0(sheet);

    return null;
  }

  private HSSFChart decodeChartStream0(HSSFSheet sheet)
  {
    Chart3DRecord chart3d = null;
    ChartRecord chart = null;
    TextRecord titleTextRecord = null;
    List seriesList = new ArrayList();
    Object[] lastSeries = null;
    LegendRecord legend = null;
    ChartTitleFormatRecord chartTitleFormat = null;
    SeriesTextRecord chartTitleText = null;
    List valueRanges = new ArrayList();
    Stack stack = new Stack();
    Record chartType = null;
    Record preR = null;
    SeriesIndexRecord siIndex = null;

    for (Record r : this._chartStream) {
      switch (r.getSid())
      {
      case 4147:
        stack.push(preR);
        break;
      case 4148:
        Record popR = (Record)stack.pop();
        if (popR.getSid() == 4099)
          lastSeries = null; break;
      case 4098:
        chart = (ChartRecord)r;
        break;
      case 4154:
        chart3d = (Chart3DRecord)r;
        break;
      case 4117:
        legend = (LegendRecord)r;
        break;
      case 4099:
        lastSeries = new Object[] { r, new ArrayList(), null, null };
        seriesList.add(lastSeries);
        break;
      case 4176:
        chartTitleFormat = (ChartTitleFormatRecord)r;
        break;
      case 4109:
        SeriesTextRecord str = (SeriesTextRecord)r;
        if ((legend == null) && (lastSeries != null))
          lastSeries[2] = str;
        else
          chartTitleText = str;

        break;
      case 4133:
        Record peekR = (Record)stack.peek();
        if ((peekR instanceof ChartRecord) && (preR.getSid() == 2215))
          titleTextRecord = (TextRecord)r; break;
      case 4177:
        LinkedDataRecord linkedDataRecord = (LinkedDataRecord)r;
        peekR = (Record)stack.peek();
        switch (peekR.getSid())
        {
        case 4099:
          if (lastSeries != null) {
            ((List)lastSeries[1]).add(linkedDataRecord);
          }

        }

        break;
      case 4127:
        valueRanges.add((ValueRangeRecord)r);
        break;
      case 4119:
      case 4120:
      case 4121:
      case 4122:
      case 4123:
        chartType = r;
        break;
      case 4197:
        siIndex = (SeriesIndexRecord)r;
        break;
      case 253:
        if ((siIndex != null) && (siIndex.getIndex() == 2))
        {
          Object[] ser = ((lastSeries == null) && (seriesList.size() > 0)) ? (Object[])seriesList.get(seriesList.size() - 1) : lastSeries;

          if (ser != null) {
            List cats = (List)ser[3];
            if (cats == null)
              ser[3] = (cats = new ArrayList());

            cats.add((LabelSSTRecord)r);
          }
        }
      }

      preR = r;
    }
    return new HSSFChart(sheet, chart, legend, chartTitleFormat, chartTitleText, seriesList, valueRanges, chartType, chart3d, titleTextRecord);
  }

  private int getRecordSize(Record[] records) {
    int total = 0;
    for (int j = records.length - 1; j >= 0; --j)
      total += records[j].getRecordSize();

    return total;
  }

  private int serialize(Record[] records, int offset, byte[] data) {
    int total = 0;
    int j = 0; for (int len = records.length; j < len; ++j) {
      Record r = records[j];
      int sz = r.serialize(offset, data);
      offset += sz;
      total += sz;
    }
    return total;
  }
}