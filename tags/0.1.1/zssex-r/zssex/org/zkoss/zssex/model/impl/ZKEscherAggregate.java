package org.zkoss.zssex.model.impl;

import java.util.ArrayList;
import java.util.List;
import org.zkoss.poi.ddf.DefaultEscherRecordFactory;
import org.zkoss.poi.ddf.EscherRecordFactory;
import org.zkoss.poi.hssf.model.DrawingManager2;
import org.zkoss.poi.hssf.record.BOFRecord;
import org.zkoss.poi.hssf.record.EscherAggregate;
import org.zkoss.poi.hssf.record.Record;
import org.zkoss.poi.hssf.record.RecordBase;
import org.zkoss.zk.ui.Executions;
//import org.zkoss.zssex.rt.Runtime;

public class ZKEscherAggregate extends EscherAggregate
{
  private List<DrawingAggregateRecord> _drawingAggregates = new ArrayList();
  private EscherRecordFactory _recordFactory;

  public ZKEscherAggregate(DrawingManager2 paramDrawingManager2)
  {
    super(paramDrawingManager2);
  }

  public List<DrawingAggregateRecord> getAggregateRecords()
  {
    return this._drawingAggregates;
  }

  public boolean mergeRecordsIntoEscherAggregate(List<RecordBase> paramList, int paramInt, DrawingManager2 paramDrawingManager2)
  {
//    if (!(Runtime.token(Executions.getCurrent())))
      return false;
//    RecordBase localRecordBase1 = paramInt;
//    RecordBase localRecordBase2 = -1;
//    int i = paramInt;
//    int j = -1;
//    int k = -1;
//    int l = -1;
//    int i1 = -1;
//    int i2 = paramList.size();
//    while (true)
//    {
//      while (true)
//      {
//        if (i >= i2)
//          break label285;
//        localRecordBase3 = (RecordBase)paramList.get(i);
//        if (localRecordBase3 instanceof Record)
//          break;
//        ++i;
//      }
//      Record localRecord = (Record)localRecordBase3;
//      int i3 = localRecord.getSid();
//      switch (i3)
//      {
//      case 236:
//        if (j >= 0)
//        {
//          this._drawingAggregates.add(new DrawingAggregateRecord(this, paramList, j, k, l, i1));
//          localRecordBase2 = getDeleteEndLoc(j, k, i1);
//          j = k = l = i1 = -1;
//        }
//        j = i;
//        break;
//      case 93:
//      case 438:
//        k = i;
//        break;
//      case 2057:
//        BOFRecord localBOFRecord = (BOFRecord)localRecord;
//        int i4 = i;
//        i = locateEOF(i4, paramList);
//        if (i < 0)
//          throw new RuntimeException("BOF without corresponding EOF record:\n" + paramList.get(l));
//        if (localBOFRecord.getType() == 32)
//        {
//          l = i4;
//          i1 = i;
//        }
//      }
//      ++i;
//    }
//    if (j >= 0)
//    {
//      label285: this._drawingAggregates.add(new DrawingAggregateRecord(this, paramList, j, k, l, i1));
//      localRecordBase2 = getDeleteEndLoc(j, k, i1);
//    }
//    for (RecordBase localRecordBase3 = localRecordBase2; localRecordBase3 >= localRecordBase1; --localRecordBase3)
//      paramList.remove(localRecordBase3);
//    paramList.add(paramInt, this);
//    return true;
  }

  public EscherRecordFactory getRecordFactory()
  {
    if (this._recordFactory == null)
      this._recordFactory = new DefaultEscherRecordFactory();
    return this._recordFactory;
  }

  private int getDeleteEndLoc(int paramInt1, int paramInt2, int paramInt3)
  {
    return ((paramInt2 >= 0) ? paramInt2 : (paramInt3 >= 0) ? paramInt3 : paramInt1);
  }

  private int locateEOF(int paramInt, List<RecordBase> paramList)
  {
    int i = 1;
    int j = paramInt + 1;
    int k = paramList.size();
    while (j < k)
    {
      Record localRecord = (Record)paramList.get(j);
      int l = localRecord.getSid();
      switch (l)
      {
      case 2057:
        ++i;
        break;
      case 10:
        if (--i == 0)
          return j;
      }
      ++j;
    }
    return -1;
  }
}