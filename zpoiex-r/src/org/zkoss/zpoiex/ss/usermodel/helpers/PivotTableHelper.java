package org.zkoss.zpoiex.ss.usermodel.helpers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCache;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCaches;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableDefinition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.zkoss.lang.Classes;
import org.zkoss.poi.POIXMLDocumentPart;
import org.zkoss.poi.POIXMLException;
import org.zkoss.poi.openxml4j.exceptions.InvalidFormatException;
import org.zkoss.poi.openxml4j.opc.PackagePart;
import org.zkoss.poi.openxml4j.opc.PackageRelationship;
import org.zkoss.poi.openxml4j.opc.PackageRelationshipCollection;
import org.zkoss.poi.ss.usermodel.PivotCache;
import org.zkoss.poi.ss.usermodel.PivotCache.CacheField;
import org.zkoss.poi.ss.usermodel.PivotTable;
import org.zkoss.poi.ss.usermodel.PivotTableHelper;
import org.zkoss.poi.ss.usermodel.Sheet;
import org.zkoss.poi.ss.usermodel.Workbook;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.poi.xssf.usermodel.XSSFFactory;
import org.zkoss.poi.xssf.usermodel.XSSFPivotCache;
import org.zkoss.poi.xssf.usermodel.XSSFPivotCacheRecords;
import org.zkoss.poi.xssf.usermodel.XSSFPivotTable;
import org.zkoss.poi.xssf.usermodel.XSSFRelation;
import org.zkoss.poi.xssf.usermodel.XSSFSheet;
import org.zkoss.poi.xssf.usermodel.XSSFWorkbook;

public class PivotTableHelper
  implements PivotTableHelper
{
  public PivotCache createPivotCache(AreaReference sourceRef, Workbook book)
  {
    if (book instanceof XSSFWorkbook)
      return createXSSFPivotCache(sourceRef, (XSSFWorkbook)book);

    return null;
  }

  private PivotCache createXSSFPivotCache(AreaReference sourceRef, XSSFWorkbook book) {
    List pivotCacheList = book.getPivotCaches();
    CTWorkbook ctWorkbook = book.getCTWorkbook();
    CTPivotCaches pivotCaches = ctWorkbook.getPivotCaches();

    if (pivotCaches == null) {
      pivotCaches = ctWorkbook.addNewPivotCaches();
    }

    CTPivotCache ctPivotCache = pivotCaches.addNewPivotCache();

    int cacheId = 1;
    for (CTPivotCache c : pivotCaches.getPivotCacheList()) {
      cacheId = (int)Math.max(c.getCacheId() + 1L, cacheId);
    }

    XSSFPivotCache pivotCache = (XSSFPivotCache)book.createRelationship(XSSFRelation.PIVOT_CACHE_DEFINITION, XSSFFactory.getInstance(), cacheId);
    ctPivotCache.setId(pivotCache.getPackageRelationship().getId());
    ctPivotCache.setCacheId(cacheId);

    pivotCache.setCacheId(cacheId);
    pivotCache.setSheetSource(sourceRef);

    pivotCacheList.add(pivotCache);
    return pivotCache;
  }

  public List<PivotTable> initPivotTables(Sheet sheet) {
    if (sheet instanceof XSSFSheet)
      return initXSSFPivotTables((XSSFSheet)sheet);

    return Collections.emptyList(); }

  private List<PivotTable> initXSSFPivotTables(XSSFSheet sheet) {
    XSSFWorkbook book = sheet.getWorkbook();
    List pivotCaches = book.getPivotCaches();

    List pivotTableList = new ArrayList();
    PackagePart part = sheet.getPackagePart();
    try {
      PackageRelationshipCollection rels = part.getRelationshipsByType(XSSFRelation.PIVOT_TABLE.getRelation());
      for (PackageRelationship rel : rels) {
        PackagePart p = part.getRelatedPart(rel);
        XSSFPivotTable pivotTable = new XSSFPivotTable(p, rel, pivotCaches);
        pivotTableList.add(pivotTable);
      }
      return pivotTableList;
    } catch (InvalidFormatException e) {
      throw new POIXMLException(e);
    } catch (XmlException e) {
      throw new POIXMLException(e);
    } catch (IOException e) {
      throw new POIXMLException(e);
    }
  }

  public List<PivotCache> initPivotCaches(Workbook book) {
    if (book instanceof XSSFWorkbook)
      return initXSSFPivotCaches((XSSFWorkbook)book);

    return Collections.emptyList();
  }

  private List<PivotCache> initXSSFPivotCaches(XSSFWorkbook book) {
    try {
      List pivotCacheList = new ArrayList();
      CTPivotCaches pivotCaches = book.getCTPivotCaches();
      if (pivotCaches == null) {
        return pivotCacheList;
      }

      for (CTPivotCache ctPivotCache : pivotCaches.getPivotCacheList()) {
        POIXMLDocumentPart p = book.getRelationById(ctPivotCache.getId());
        pivotCacheList.add(new XSSFPivotCache(ctPivotCache.getCacheId(), book, p.getPackagePart(), p.getPackageRelationship()));
      }
      return pivotCacheList;
    } catch (IOException e) {
      throw new POIXMLException(e);
    }
  }

  public PivotTable createPivotTable(CellReference destination, String name, PivotCache pivotCache, Sheet sheet) {
    List pivotTables = sheet.getPivotTables();
    if (sheet instanceof XSSFSheet) {
      if (containsPivotTable(name, pivotTables)) {
        throw new IllegalArgumentException("Already contains a pivot tabel of this name");
      }

      XSSFSheet sheet0 = (XSSFSheet)sheet;
      XSSFPivotCache cache = (XSSFPivotCache)pivotCache;

      int pvIdx = pivotTables.size() + 1;
      XSSFPivotTable pt = (XSSFPivotTable)sheet0.createRelationship(XSSFRelation.PIVOT_TABLE, XSSFFactory.getInstance(), pvIdx);
      pt.setPivotCache(cache);
      pt.setName(name);

      pivotTables.add(pt);

      CTPivotTableDefinition pivotTableDefinition = pt.getPivotTableDefinition();
      AreaReference ref = new AreaReference(destination, new CellReference(destination.getRow() + 17, destination.getCol() + 2));
      pt.setFirstHeaderRow(1);
      pt.setFirstData(1, 0);
      pt.setLocationRef(ref);

      CTPivotFields pivotFields = pivotTableDefinition.addNewPivotFields();
      for (PivotCache.CacheField cf : pivotCache.getFields()) {
        CTPivotField pivotField = pivotFields.addNewPivotField();
        pivotField.setShowAll(false);
        long formatId = cf.getNumberFormatId();
        if (formatId > 0L)
          pivotField.setNumFmtId(formatId);
      }

      pivotFields.setCount(pivotFields.getPivotFieldList().size());

      CTPivotTableStyle pivotTableStyleInfo = pivotTableDefinition.addNewPivotTableStyleInfo();
      pivotTableStyleInfo.setName("PivotStyleLight16");
      pivotTableStyleInfo.setShowRowHeaders(true);
      pivotTableStyleInfo.setShowColHeaders(true);
      pivotTableStyleInfo.setShowRowStripes(false);
      pivotTableStyleInfo.setShowColStripes(false);
      pivotTableStyleInfo.setShowLastColumn(true);
      return pt;
    }
    return null; }

  private boolean containsPivotTable(String name, List<PivotTable> pivotTables) {
    for (PivotTable pt : pivotTables)
      if (pt.getName().equalsIgnoreCase(name))
        return true;


    return false;
  }

  static
  {
    Field fd = null;
    try {
      fd = Classes.getAnyField(XSSFRelation.class, "_cls");
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    boolean old = fd.isAccessible();
    try {
      fd.setAccessible(true);
      fd.set(XSSFRelation.PIVOT_TABLE, XSSFPivotTable.class);
      fd.set(XSSFRelation.PIVOT_CACHE_DEFINITION, XSSFPivotCache.class);
      fd.set(XSSFRelation.PIVOT_CACHE_RECORDS, XSSFPivotCacheRecords.class);
    } catch (IllegalAccessException e) {
    }
    finally {
      fd.setAccessible(old);
    }
  }
}