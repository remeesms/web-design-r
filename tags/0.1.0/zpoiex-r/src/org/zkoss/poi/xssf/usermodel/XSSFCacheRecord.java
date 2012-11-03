package org.zkoss.poi.xssf.usermodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBoolean;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDateTime;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIndex;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumber;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRecord;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSharedItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTString;
import org.w3c.dom.Node;
import org.zkoss.poi.ss.usermodel.PivotCache.CacheRecord;

public class XSSFCacheRecord implements CacheRecord
{
  private CTRecord _record;
  private List<CTCacheField> _fields;
  private List<Object> _data;

  XSSFCacheRecord(CTRecord record, List<CTCacheField> fields)
  {
    this._record = record;
    this._fields = fields;
  }

  public List<Object> getData() {
    if (this._data == null)
      initData();

    return this._data;
  }

  private void initData() {
    this._data = new ArrayList();

    HashMap dataMap = new HashMap();
    IndexMapper indexMapper = new IndexMapper(this._record.getDomNode());

    List<CTIndex> xList = this._record.getXList();
    if (xList != null)
      for (CTIndex e : xList) {
        int index = ((Integer)indexMapper.get(e.getDomNode())).intValue();
        CTSharedItems sharedItems = ((CTCacheField)this._fields.get(index)).getSharedItems();

        List sList = sharedItems.getSList();
        String s = ((CTString)sList.get((int)e.getV())).getV();
        dataMap.put(Integer.valueOf(index), s);
      }


    List<CTNumber> nList = this._record.getNList();
    if (nList != null)
      for (CTNumber n : nList) {
        int index = ((Integer)indexMapper.get(n.getDomNode())).intValue();
        dataMap.put(Integer.valueOf(index), Double.valueOf(n.getV()));
      }


    List<CTString> sList = this._record.getSList();
    if (sList != null)
      for (CTString s : sList) {
        int index = ((Integer)indexMapper.get(s.getDomNode())).intValue();
        dataMap.put(Integer.valueOf(index), s.getV());
      }


    List<CTDateTime> dList = this._record.getDList();
    if (dList != null)
      for (CTDateTime d : dList) {
        int index = ((Integer)indexMapper.get(d.getDomNode())).intValue();
        dataMap.put(Integer.valueOf(index), d.getV());
      }


    List<CTBoolean> bList = this._record.getBList();
    if (bList != null)
      for (CTBoolean b : bList) {
        int index = ((Integer)indexMapper.get(b.getDomNode())).intValue();
        dataMap.put(Integer.valueOf(index), Boolean.valueOf(b.getV()));
      }


    for (int i = 0; i < dataMap.size(); ++i)
      this._data.add(dataMap.get(Integer.valueOf(i)));
  }

  private class IndexMapper extends HashMap<Node, Integer>
  {
    IndexMapper(Node parent)
    {
      int i = 0;
      for (Node chd = parent.getFirstChild(); chd != null; chd = chd.getNextSibling())
        put(chd, Integer.valueOf(i++));
    }
  }
}