package org.zkoss.poi.xssf.usermodel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheRecords;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheRecords.Factory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRecord;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.PivotCacheRecordsDocument;
import org.zkoss.poi.POIXMLDocumentPart;
import org.zkoss.poi.POIXMLException;
import org.zkoss.poi.openxml4j.opc.PackagePart;
import org.zkoss.poi.openxml4j.opc.PackageRelationship;
import org.zkoss.poi.openxml4j.opc.internal.MemoryPackagePart;

public class XSSFPivotCacheRecords extends POIXMLDocumentPart
{
  private CTPivotCacheRecords _pivotCacheRecords;

  XSSFPivotCacheRecords()
  {
    onDocumentCreate();
  }

  protected void onDocumentCreate()
  {
    this._pivotCacheRecords = CTPivotCacheRecords.Factory.newInstance();
  }

  XSSFPivotCacheRecords(PackagePart part, PackageRelationship rel) throws IOException {
    super(part, rel);
    onDocumentRead();
  }

  protected void onDocumentRead() throws IOException
  {
    try {
      this._pivotCacheRecords = PivotCacheRecordsDocument.Factory.parse(getPackagePart().getInputStream()).getPivotCacheRecords();
    } catch (XmlException e) {
      throw new POIXMLException(e);
    }
  }

  protected void commit() throws IOException
  {
    XmlOptions xmlOptions = new XmlOptions(DEFAULT_XML_OPTIONS);
    xmlOptions.setSaveSyntheticDocumentElement(new QName(CTPivotCacheRecords.type.getName().getNamespaceURI(), "pivotCacheRecords"));
    Map map = new HashMap();
    map.put(STRelationshipId.type.getName().getNamespaceURI(), "r");
    xmlOptions.setSaveSuggestedPrefixes(map);

    PackagePart part = getPackagePart();
    clearMemoryPackagePart(part);

    OutputStream out = part.getOutputStream();
    this._pivotCacheRecords.save(out, xmlOptions);
    out.close();
  }

  private void clearMemoryPackagePart(PackagePart part) {
    if (part instanceof MemoryPackagePart)
      ((MemoryPackagePart)part).clear();
  }

  CTRecord addNewRow()
  {
    return this._pivotCacheRecords.addNewR();
  }

  List<CTRecord> getRows() {
    return this._pivotCacheRecords.getRList();
  }

  public String toString()
  {
    return this._pivotCacheRecords.toString();
  }

  void setCount(int size) {
    this._pivotCacheRecords.setCount(size);
  }

  int getCount() {
    return (int)this._pivotCacheRecords.getCount();
  }
}