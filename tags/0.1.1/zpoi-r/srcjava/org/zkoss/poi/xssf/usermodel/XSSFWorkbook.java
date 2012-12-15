package org.zkoss.poi.xssf.usermodel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedNames;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDialogsheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCaches;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheets;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetState;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorkbookDocument;
import org.zkoss.poi.POIXMLDocument;
import org.zkoss.poi.POIXMLDocumentPart;
import org.zkoss.poi.POIXMLException;
import org.zkoss.poi.POIXMLProperties;
import org.zkoss.poi.openxml4j.exceptions.OpenXML4JException;
import org.zkoss.poi.openxml4j.opc.OPCPackage;
import org.zkoss.poi.openxml4j.opc.PackagePart;
import org.zkoss.poi.openxml4j.opc.PackagePartName;
import org.zkoss.poi.openxml4j.opc.PackageRelationship;
import org.zkoss.poi.openxml4j.opc.PackagingURIHelper;
import org.zkoss.poi.openxml4j.opc.TargetMode;
import org.zkoss.poi.ss.formula.SheetNameFormatter;
import org.zkoss.poi.ss.formula.udf.UDFFinder;
import org.zkoss.poi.ss.usermodel.PivotCache;
import org.zkoss.poi.ss.usermodel.Row;
import org.zkoss.poi.ss.usermodel.Sheet;
import org.zkoss.poi.ss.usermodel.Workbook;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.poi.ss.util.WorkbookUtil;
import org.zkoss.poi.util.IOUtils;
import org.zkoss.poi.util.Internal;
import org.zkoss.poi.util.POILogFactory;
import org.zkoss.poi.util.POILogger;
import org.zkoss.poi.util.PackageHelper;
import org.zkoss.poi.xssf.model.CalculationChain;
import org.zkoss.poi.xssf.model.ExternalLink;
import org.zkoss.poi.xssf.model.IndexedUDFFinder;
import org.zkoss.poi.xssf.model.MapInfo;
import org.zkoss.poi.xssf.model.SharedStringsTable;
import org.zkoss.poi.xssf.model.StylesTable;
import org.zkoss.poi.xssf.model.ThemesTable;
import org.zkoss.poi.xssf.usermodel.helpers.XSSFPivotTableHelpers;

public class XSSFWorkbook extends POIXMLDocument
  implements Workbook, Iterable<XSSFSheet>
{
  private static final Pattern COMMA_PATTERN = Pattern.compile(",");
  public static final float DEFAULT_CHARACTER_WIDTH = 7.0017F;
  private static final int MAX_SENSITIVE_SHEET_NAME_LEN = 31;
  public static final int PICTURE_TYPE_GIF = 8;
  public static final int PICTURE_TYPE_TIFF = 9;
  public static final int PICTURE_TYPE_EPS = 10;
  public static final int PICTURE_TYPE_BMP = 11;
  public static final int PICTURE_TYPE_WPG = 12;
  private CTWorkbook workbook;
  private List<XSSFSheet> sheets;
  private List<XSSFName> namedRanges;
  private SharedStringsTable sharedStringSource;
  private StylesTable stylesSource;
  private ThemesTable theme;
  private IndexedUDFFinder _udfFinder = new IndexedUDFFinder(new UDFFinder[] { UDFFinder.DEFAULT });
  private CalculationChain calcChain;
  private MapInfo mapInfo;
  private XSSFDataFormat formatter;
  private Row.MissingCellPolicy _missingCellPolicy = Row.RETURN_NULL_AND_BLANK;
  private List<XSSFPictureData> pictures;
  private static POILogger logger = POILogFactory.getLogger(XSSFWorkbook.class);
  private XSSFCreationHelper _creationHelper;
  private List<String[]> _externalSheetRefs = new ArrayList(4);

  private Map<String, String> linkIndexToBookName = new HashMap(4);

  private Map<String, String> bookNameToLinkIndex = new HashMap(4);
  private List<PivotCache> _pivotCaches;

  int getOrCreateExternalSheetIndex(String bookName, String sheetName1, String sheetName2)
  {
    synchronized (this._externalSheetRefs) {
      int len = this._externalSheetRefs.size();
      for (int j = 0; j < len; j++) {
        String jbookName = ((String[])this._externalSheetRefs.get(j))[0];
        if (((bookName == jbookName) || ((bookName != null) && (bookName.equalsIgnoreCase(jbookName)))) && (((String[])this._externalSheetRefs.get(j))[1].equalsIgnoreCase(sheetName1)) && (((String[])this._externalSheetRefs.get(j))[2].equalsIgnoreCase(sheetName2)))
        {
          return j;
        }
      }
      this._externalSheetRefs.add(new String[] { bookName, sheetName1, sheetName2 });
      return len;
    }
  }

  String[] convertFromExternSheetIndex(int externSheetIndex) {
    if (this._externalSheetRefs.size() <= externSheetIndex) {
      return null;
    }
    return (String[])this._externalSheetRefs.get(externSheetIndex);
  }

  public XSSFWorkbook()
  {
    super(newPackage());
    onWorkbookCreate();
  }

  public XSSFWorkbook(OPCPackage pkg)
    throws IOException
  {
    super(pkg);

    load(XSSFFactory.getInstance());
  }

  public XSSFWorkbook(InputStream is) throws IOException {
    super(PackageHelper.open(is));

    load(XSSFFactory.getInstance());
  }

  @Deprecated
  public XSSFWorkbook(String path)
    throws IOException
  {
    this(openPackage(path));
  }

  protected void onDocumentRead() throws IOException
  {
    try
    {
      WorkbookDocument doc = WorkbookDocument.Factory.parse(getPackagePart().getInputStream());
      this.workbook = doc.getWorkbook();

      Map shIdMap = new HashMap();
      for (POIXMLDocumentPart p : getRelations()) {
        if ((p instanceof SharedStringsTable)) { this.sharedStringSource = ((SharedStringsTable)p);
        } else if ((p instanceof StylesTable)) { this.stylesSource = ((StylesTable)p);
        } else if ((p instanceof ThemesTable)) { this.theme = ((ThemesTable)p);
        } else if ((p instanceof CalculationChain)) { this.calcChain = ((CalculationChain)p);
        } else if ((p instanceof MapInfo)) { this.mapInfo = ((MapInfo)p);
        } else if ((p instanceof XSSFSheet)) {
          shIdMap.put(p.getPackageRelationship().getId(), (XSSFSheet)p);
        } else if ((p instanceof ExternalLink)) {
          ExternalLink el = (ExternalLink)p;
          this.linkIndexToBookName.put(el.getLinkIndex(), el.getBookName());
          this.bookNameToLinkIndex.put(el.getBookName(), el.getLinkIndex());
        }
      }
      this.stylesSource.setTheme(this.theme);

      if (this.sharedStringSource == null)
      {
        this.sharedStringSource = ((SharedStringsTable)createRelationship(XSSFRelation.SHARED_STRINGS, XSSFFactory.getInstance()));
      }

      this.sheets = new ArrayList(shIdMap.size());
      for (CTSheet ctSheet : this.workbook.getSheets().getSheetArray()) {
        XSSFSheet sh = (XSSFSheet)shIdMap.get(ctSheet.getId());
        if (sh == null) {
          logger.log(POILogger.WARN, "Sheet with name " + ctSheet.getName() + " and r:id " + ctSheet.getId() + " was defined, but didn't exist in package, skipping");
        }
        else {
          sh.sheet = ctSheet;
          sh.onDocumentRead();
          this.sheets.add(sh);
        }
      }

      this.namedRanges = new ArrayList();
      if (this.workbook.isSetDefinedNames())
        for (CTDefinedName ctName : this.workbook.getDefinedNames().getDefinedNameArray())
          this.namedRanges.add(new XSSFName(ctName, this));
    }
    catch (XmlException e)
    {
      throw new POIXMLException(e);
    }
  }

  public CTPivotCaches getCTPivotCaches() {
    return this.workbook.getPivotCaches();
  }

  private void onWorkbookCreate()
  {
    this.workbook = CTWorkbook.Factory.newInstance();

    CTWorkbookPr workbookPr = this.workbook.addNewWorkbookPr();
    workbookPr.setDate1904(false);

    CTBookViews bvs = this.workbook.addNewBookViews();
    CTBookView bv = bvs.addNewWorkbookView();
    bv.setActiveTab(0L);
    this.workbook.addNewSheets();

    POIXMLProperties.ExtendedProperties expProps = getProperties().getExtendedProperties();
    expProps.getUnderlyingProperties().setApplication("Apache POI");

    this.sharedStringSource = ((SharedStringsTable)createRelationship(XSSFRelation.SHARED_STRINGS, XSSFFactory.getInstance()));
    this.stylesSource = ((StylesTable)createRelationship(XSSFRelation.STYLES, XSSFFactory.getInstance()));

    this.namedRanges = new ArrayList();
    this.sheets = new ArrayList();
  }

  protected static OPCPackage newPackage()
  {
    try
    {
      OPCPackage pkg = OPCPackage.create(new ByteArrayOutputStream());

      PackagePartName corePartName = PackagingURIHelper.createPartName(XSSFRelation.WORKBOOK.getDefaultFileName());

      pkg.addRelationship(corePartName, TargetMode.INTERNAL, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument");

      pkg.createPart(corePartName, XSSFRelation.WORKBOOK.getContentType());

      pkg.getPackageProperties().setCreatorProperty("Apache POI");

      return pkg; } catch (Exception e) {
    	  throw new POIXMLException(e);
    }
    
  }

  @Internal
  public CTWorkbook getCTWorkbook()
  {
    return this.workbook;
  }

  public int addPicture(byte[] pictureData, int format)
  {
    int imageNumber = getAllPictures().size() + 1;
    XSSFPictureData img = (XSSFPictureData)createRelationship(XSSFPictureData.RELATIONS[format], XSSFFactory.getInstance(), imageNumber, true);
    try {
      OutputStream out = img.getPackagePart().getOutputStream();
      out.write(pictureData);
      out.close();
    } catch (IOException e) {
      throw new POIXMLException(e);
    }
    this.pictures.add(img);
    return imageNumber - 1;
  }

  public int addPicture(InputStream is, int format)
    throws IOException
  {
    int imageNumber = getAllPictures().size() + 1;
    XSSFPictureData img = (XSSFPictureData)createRelationship(XSSFPictureData.RELATIONS[format], XSSFFactory.getInstance(), imageNumber, true);
    OutputStream out = img.getPackagePart().getOutputStream();
    IOUtils.copy(is, out);
    out.close();
    this.pictures.add(img);
    return imageNumber - 1;
  }

  public XSSFSheet cloneSheet(int sheetNum)
  {
    validateSheetIndex(sheetNum);

    XSSFSheet srcSheet = (XSSFSheet)this.sheets.get(sheetNum);
    String srcName = srcSheet.getSheetName();
    String clonedName = getUniqueSheetName(srcName);

    XSSFSheet clonedSheet = createSheet(clonedName);
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      srcSheet.write(out);
      clonedSheet.read(new ByteArrayInputStream(out.toByteArray()));
    } catch (IOException e) {
      throw new POIXMLException("Failed to clone sheet", e);
    }
    CTWorksheet ct = clonedSheet.getCTWorksheet();
    if (ct.isSetLegacyDrawing()) {
      logger.log(POILogger.WARN, "Cloning sheets with comments is not yet supported.");
      ct.unsetLegacyDrawing();
    }
    if (ct.isSetPageSetup()) {
      logger.log(POILogger.WARN, "Cloning sheets with page setup is not yet supported.");
      ct.unsetPageSetup();
    }

    clonedSheet.setSelected(false);

    List<POIXMLDocumentPart> rels = srcSheet.getRelations();

    XSSFDrawing dg = null;
    for (POIXMLDocumentPart r : rels)
    {
      if ((r instanceof XSSFDrawing)) {
        dg = (XSSFDrawing)r;
        continue;
      }

      PackageRelationship rel = r.getPackageRelationship();
      clonedSheet.getPackagePart().addRelationship(rel.getTargetURI(), rel.getTargetMode(), rel.getRelationshipType());

      clonedSheet.addRelation(rel.getId(), r);
    }

    if (dg != null) {
      if (ct.isSetDrawing())
      {
        ct.unsetDrawing();
      }
      XSSFDrawing clonedDg = clonedSheet.createDrawingPatriarch();

      clonedDg.getCTDrawing().set(dg.getCTDrawing());

      List<POIXMLDocumentPart> srcRels = srcSheet.createDrawingPatriarch().getRelations();
      for (POIXMLDocumentPart rel : srcRels) {
        PackageRelationship relation = rel.getPackageRelationship();
        clonedSheet.createDrawingPatriarch().getPackagePart().addRelationship(relation.getTargetURI(), relation.getTargetMode(), relation.getRelationshipType(), relation.getId());
      }

    }

    return clonedSheet;
  }

  private String getUniqueSheetName(String srcName)
  {
    int uniqueIndex = 2;
    String baseName = srcName;
    int bracketPos = srcName.lastIndexOf('(');
    if ((bracketPos > 0) && (srcName.endsWith(")"))) {
      String suffix = srcName.substring(bracketPos + 1, srcName.length() - ")".length());
      try {
        uniqueIndex = Integer.parseInt(suffix.trim());
        uniqueIndex++;
        baseName = srcName.substring(0, bracketPos).trim();
      }
      catch (NumberFormatException e)
      {
      }
    }
    while (true) {
      String index = Integer.toString(uniqueIndex++);
      String name;
      if (baseName.length() + index.length() + 2 < 31)
        name = baseName + " (" + index + ")";
      else {
        name = baseName.substring(0, 31 - index.length() - 2) + "(" + index + ")";
      }

      if (getSheetIndex(name) == -1)
        return name;
    }
  }

  public XSSFCellStyle createCellStyle()
  {
    return this.stylesSource.createCellStyle();
  }

  public XSSFDataFormat createDataFormat()
  {
    if (this.formatter == null)
      this.formatter = new XSSFDataFormat(this.stylesSource);
    return this.formatter;
  }

  public XSSFFont createFont()
  {
    XSSFFont font = new XSSFFont();
    font.registerTo(this.stylesSource);
    return font;
  }

  public XSSFName createName() {
    CTDefinedName ctName = CTDefinedName.Factory.newInstance();
    ctName.setName("");
    XSSFName name = new XSSFName(ctName, this);
    this.namedRanges.add(name);
    return name;
  }

  public XSSFSheet createSheet()
  {
    String sheetname = "Sheet" + this.sheets.size();
    int idx = 0;
    while (getSheet(sheetname) != null) {
      sheetname = "Sheet" + idx;
      idx++;
    }
    return createSheet(sheetname);
  }

  public XSSFSheet createSheet(String sheetname)
  {
    if (sheetname == null) {
      throw new IllegalArgumentException("sheetName must not be null");
    }

    if (containsSheet(sheetname, this.sheets.size())) {
      throw new IllegalArgumentException("The workbook already contains a sheet of this name");
    }

    if (sheetname.length() > 31) sheetname = sheetname.substring(0, 31);
    WorkbookUtil.validateSheetName(sheetname);

    CTSheet sheet = addSheet(sheetname);

    int sheetNumber = 1;
    XSSFSheet sh;
    for (Iterator i$ = this.sheets.iterator(); i$.hasNext(); sheetNumber = (int)Math.max(sh.sheet.getSheetId() + 1L, sheetNumber)) sh = (XSSFSheet)i$.next();

    XSSFSheet wrapper = (XSSFSheet)createRelationship(XSSFRelation.WORKSHEET, XSSFFactory.getInstance(), sheetNumber);
    wrapper.sheet = sheet;
    sheet.setId(wrapper.getPackageRelationship().getId());
    sheet.setSheetId(sheetNumber);
    if (this.sheets.size() == 0) wrapper.setSelected(true);
    this.sheets.add(wrapper);
    return wrapper;
  }

  protected XSSFDialogsheet createDialogsheet(String sheetname, CTDialogsheet dialogsheet) {
    XSSFSheet sheet = createSheet(sheetname);
    return new XSSFDialogsheet(sheet);
  }

  private CTSheet addSheet(String sheetname) {
    CTSheet sheet = this.workbook.getSheets().addNewSheet();
    sheet.setName(sheetname);
    return sheet;
  }

  public XSSFFont findFont(short boldWeight, short color, short fontHeight, String name, boolean italic, boolean strikeout, short typeOffset, byte underline)
  {
    return this.stylesSource.findFont(boldWeight, color, fontHeight, name, italic, strikeout, typeOffset, underline);
  }

  public int getActiveSheetIndex()
  {
    return (int)this.workbook.getBookViews().getWorkbookViewArray(0).getActiveTab();
  }

  public List<XSSFPictureData> getAllPictures()
  {
    if (this.pictures == null)
    {
      this.pictures = new ArrayList();
      for (XSSFSheet sh : this.sheets) {
        for (POIXMLDocumentPart dr : sh.getRelations()) {
          if ((dr instanceof XSSFDrawing)) {
            for (POIXMLDocumentPart img : dr.getRelations()) {
              if ((img instanceof XSSFPictureData)) {
                this.pictures.add((XSSFPictureData)img);
              }
            }
          }
        }
      }
    }
    return this.pictures;
  }

  public XSSFCellStyle getCellStyleAt(short idx)
  {
    return this.stylesSource.getStyleAt(idx);
  }

  public XSSFFont getFontAt(short idx)
  {
    return this.stylesSource.getFontAt(idx);
  }

  public XSSFName getName(String name) {
    int nameIndex = getNameIndex(name);
    if (nameIndex < 0) {
      return null;
    }
    return (XSSFName)this.namedRanges.get(nameIndex);
  }

  public XSSFName getNameAt(int nameIndex) {
    int nNames = this.namedRanges.size();
    if (nNames < 1) {
      throw new IllegalStateException("There are no defined names in this workbook");
    }
    if ((nameIndex < 0) || (nameIndex > nNames)) {
      throw new IllegalArgumentException("Specified name index " + nameIndex + " is outside the allowable range (0.." + (nNames - 1) + ").");
    }

    return (XSSFName)this.namedRanges.get(nameIndex);
  }

  public int getNameIndex(String name)
  {
    int i = 0;
    for (XSSFName nr : this.namedRanges) {
      if (nr.getNameName().equals(name)) {
        return i;
      }
      i++;
    }
    return -1;
  }

  public short getNumCellStyles()
  {
    return (short)this.stylesSource.getNumCellStyles();
  }

  public short getNumberOfFonts()
  {
    return (short)this.stylesSource.getFonts().size();
  }

  public int getNumberOfNames()
  {
    return this.namedRanges.size();
  }

  public int getNumberOfSheets()
  {
    return this.sheets.size();
  }

  public String getPrintArea(int sheetIndex)
  {
    XSSFName name = getBuiltInName("_xlnm.Print_Area", sheetIndex);
    if (name == null) return null;

    return name.getRefersToFormula();
  }

  public XSSFSheet getSheet(String name)
  {
    for (XSSFSheet sheet : this.sheets) {
      if (name.equalsIgnoreCase(sheet.getSheetName())) {
        return sheet;
      }
    }
    return null;
  }

  public XSSFSheet getSheetAt(int index)
  {
    validateSheetIndex(index);
    return (XSSFSheet)this.sheets.get(index);
  }

  public int getSheetIndex(String name)
  {
    for (int i = 0; i < this.sheets.size(); i++) {
      XSSFSheet sheet = (XSSFSheet)this.sheets.get(i);
      if (name.equalsIgnoreCase(sheet.getSheetName())) {
        return i;
      }
    }
    return -1;
  }

  public int getSheetIndex(Sheet sheet)
  {
    int idx = 0;
    for (XSSFSheet sh : this.sheets) {
      if (sh == sheet) return idx;
      idx++;
    }
    return -1;
  }

  public String getSheetName(int sheetIx)
  {
    validateSheetIndex(sheetIx);
    return ((XSSFSheet)this.sheets.get(sheetIx)).getSheetName();
  }

  public Iterator<XSSFSheet> iterator()
  {
    return this.sheets.iterator();
  }

  public boolean isMacroEnabled()
  {
    return getPackagePart().getContentType().equals(XSSFRelation.MACROS_WORKBOOK.getContentType());
  }

  public void removeName(int nameIndex) {
    this.namedRanges.remove(nameIndex);
  }

  public void removeName(String name) {
    for (int i = 0; i < this.namedRanges.size(); i++) {
      XSSFName nm = (XSSFName)this.namedRanges.get(i);
      if (nm.getNameName().equalsIgnoreCase(name)) {
        removeName(i);
        return;
      }
    }
    throw new IllegalArgumentException("Named range was not found: " + name);
  }

  public void removePrintArea(int sheetIndex)
  {
    int cont = 0;
    for (XSSFName name : this.namedRanges) {
      if ((name.getNameName().equals("_xlnm.Print_Area")) && (name.getSheetIndex() == sheetIndex)) {
        this.namedRanges.remove(cont);
        break;
      }
      cont++;
    }
  }

  public void removeSheetAt(int index)
  {
    validateSheetIndex(index);

    onSheetDelete(index);

    XSSFSheet sheet = getSheetAt(index);
    removeRelation(sheet);
    this.sheets.remove(index);
  }

  private void onSheetDelete(int index)
  {
    this.workbook.getSheets().removeSheet(index);

    if (this.calcChain != null) {
      removeRelation(this.calcChain);
      this.calcChain = null;
    }

    for (Iterator it = this.namedRanges.iterator(); it.hasNext(); ) {
      XSSFName nm = (XSSFName)it.next();
      CTDefinedName ct = nm.getCTName();
      if (ct.isSetLocalSheetId())
        if (ct.getLocalSheetId() == index)
          it.remove();
        else if (ct.getLocalSheetId() > index)
        {
          ct.setLocalSheetId(ct.getLocalSheetId() - 1L);
        }
    }
  }

  public Row.MissingCellPolicy getMissingCellPolicy()
  {
    return this._missingCellPolicy;
  }

  public void setMissingCellPolicy(Row.MissingCellPolicy missingCellPolicy)
  {
    this._missingCellPolicy = missingCellPolicy;
  }

  public void setActiveSheet(int index)
  {
    validateSheetIndex(index);

    for (CTBookView arrayBook : this.workbook.getBookViews().getWorkbookViewArray())
      arrayBook.setActiveTab(index);
  }

  private void validateSheetIndex(int index)
  {
    int lastSheetIx = this.sheets.size() - 1;
    if ((index < 0) || (index > lastSheetIx))
      throw new IllegalArgumentException("Sheet index (" + index + ") is out of range (0.." + lastSheetIx + ")");
  }

  public int getFirstVisibleTab()
  {
    CTBookViews bookViews = this.workbook.getBookViews();
    CTBookView bookView = bookViews.getWorkbookViewArray(0);
    return (short)(int)bookView.getActiveTab();
  }

  public void setFirstVisibleTab(int index)
  {
    CTBookViews bookViews = this.workbook.getBookViews();
    CTBookView bookView = bookViews.getWorkbookViewArray(0);
    bookView.setActiveTab(index);
  }

  public void setPrintArea(int sheetIndex, String reference)
  {
    XSSFName name = getBuiltInName("_xlnm.Print_Area", sheetIndex);
    if (name == null) {
      name = createBuiltInName("_xlnm.Print_Area", sheetIndex);
    }

    String[] parts = COMMA_PATTERN.split(reference);
    StringBuffer sb = new StringBuffer(32);
    for (int i = 0; i < parts.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      SheetNameFormatter.appendFormat(sb, getSheetName(sheetIndex));
      sb.append("!");
      sb.append(parts[i]);
    }
    name.setRefersToFormula(sb.toString());
  }

  public void setPrintArea(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow)
  {
    String reference = getReferencePrintArea(getSheetName(sheetIndex), startColumn, endColumn, startRow, endRow);
    setPrintArea(sheetIndex, reference);
  }

  public void setRepeatingRowsAndColumns(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow)
  {
    if (((startColumn == -1) && (endColumn != -1)) || (startColumn < -1) || (endColumn < -1) || (startColumn > endColumn))
      throw new IllegalArgumentException("Invalid column range specification");
    if (((startRow == -1) && (endRow != -1)) || (startRow < -1) || (endRow < -1) || (startRow > endRow)) {
      throw new IllegalArgumentException("Invalid row range specification");
    }
    XSSFSheet sheet = getSheetAt(sheetIndex);
    boolean removingRange = (startColumn == -1) && (endColumn == -1) && (startRow == -1) && (endRow == -1);

    XSSFName name = getBuiltInName("_xlnm.Print_Titles", sheetIndex);
    if (removingRange) {
      if (name != null) this.namedRanges.remove(name);
      return;
    }
    if (name == null) {
      name = createBuiltInName("_xlnm.Print_Titles", sheetIndex);
    }

    String reference = getReferenceBuiltInRecord(name.getSheetName(), startColumn, endColumn, startRow, endRow);
    name.setRefersToFormula(reference);

    CTWorksheet ctSheet = sheet.getCTWorksheet();
    if ((!ctSheet.isSetPageSetup()) || (!ctSheet.isSetPageMargins()))
    {
      XSSFPrintSetup printSetup = sheet.getPrintSetup();
      printSetup.setValidSettings(false);
    }
  }

  private static String getReferenceBuiltInRecord(String sheetName, int startC, int endC, int startR, int endR)
  {
    CellReference colRef = new CellReference(sheetName, 0, startC, true, true);
    CellReference colRef2 = new CellReference(sheetName, 0, endC, true, true);

    String escapedName = SheetNameFormatter.format(sheetName);
    String c;
    if ((startC == -1) && (endC == -1)) c = ""; else {
      c = escapedName + "!$" + colRef.getCellRefParts()[2] + ":$" + colRef2.getCellRefParts()[2];
    }
    CellReference rowRef = new CellReference(sheetName, startR, 0, true, true);
    CellReference rowRef2 = new CellReference(sheetName, endR, 0, true, true);

    String r = "";
    if ((startR == -1) && (endR == -1)) r = "";
    else if ((!rowRef.getCellRefParts()[1].equals("0")) && (!rowRef2.getCellRefParts()[1].equals("0"))) {
      r = escapedName + "!$" + rowRef.getCellRefParts()[1] + ":$" + rowRef2.getCellRefParts()[1];
    }

    StringBuffer rng = new StringBuffer();
    rng.append(c);
    if ((rng.length() > 0) && (r.length() > 0)) rng.append(',');
    rng.append(r);
    return rng.toString();
  }

  private static String getReferencePrintArea(String sheetName, int startC, int endC, int startR, int endR)
  {
    CellReference colRef = new CellReference(sheetName, startR, startC, true, true);
    CellReference colRef2 = new CellReference(sheetName, endR, endC, true, true);

    return "$" + colRef.getCellRefParts()[2] + "$" + colRef.getCellRefParts()[1] + ":$" + colRef2.getCellRefParts()[2] + "$" + colRef2.getCellRefParts()[1];
  }

  public XSSFName getBuiltInName(String builtInCode, int sheetNumber) {
    for (XSSFName name : this.namedRanges) {
      if ((name.getNameName().equalsIgnoreCase(builtInCode)) && (name.getSheetIndex() == sheetNumber)) {
        return name;
      }
    }
    return null;
  }

  XSSFName createBuiltInName(String builtInName, int sheetNumber)
  {
    validateSheetIndex(sheetNumber);

    CTDefinedNames names = this.workbook.getDefinedNames() == null ? this.workbook.addNewDefinedNames() : this.workbook.getDefinedNames();
    CTDefinedName nameRecord = names.addNewDefinedName();
    nameRecord.setName(builtInName);
    nameRecord.setLocalSheetId(sheetNumber);

    XSSFName name = new XSSFName(nameRecord, this);
    for (XSSFName nr : this.namedRanges) {
      if (nr.equals(name)) {
        throw new POIXMLException("Builtin (" + builtInName + ") already exists for sheet (" + sheetNumber + ")");
      }
    }

    this.namedRanges.add(name);
    return name;
  }

  public void setSelectedTab(int index)
  {
    for (int i = 0; i < this.sheets.size(); i++) {
      XSSFSheet sheet = (XSSFSheet)this.sheets.get(i);
      sheet.setSelected(i == index);
    }
  }

  public void setSheetName(int sheetIndex, String sheetname)
  {
    validateSheetIndex(sheetIndex);

    if ((sheetname != null) && (sheetname.length() > 31)) sheetname = sheetname.substring(0, 31);
    WorkbookUtil.validateSheetName(sheetname);

    if (containsSheet(sheetname, sheetIndex)) {
      throw new IllegalArgumentException("The workbook already contains a sheet of this name");
    }

    Sheet wsheet = getSheetAt(sheetIndex);
    String o;
    String n;
    if (wsheet != null) {
      String oldname = wsheet.getSheetName();
      for (String[] names : this._externalSheetRefs) {
        String sheetname1 = names[1];
        String sheetname2 = names[2];
        if (oldname.equals(sheetname1)) {
          names[1] = sheetname;
        }
        if (oldname.equals(sheetname2)) {
          names[2] = sheetname;
        }
      }

      o = SheetNameFormatter.format(oldname);
      n = SheetNameFormatter.format(sheetname);
      for (XSSFName nm : this.namedRanges) {
        CTDefinedName ct = nm.getCTName();
        if ((ct.isSetLocalSheetId()) && 
          (ct.getLocalSheetId() == sheetIndex)) {
          String ref = ct.getStringValue();
          String newref = ref.replaceAll(o + "!", n + "!");
          ct.setStringValue(newref);
        }
      }
    }

    this.workbook.getSheets().getSheetArray(sheetIndex).setName(sheetname);
  }

  public void setSheetOrder(String sheetname, int pos)
  {
    int idx = getSheetIndex(sheetname);
    this.sheets.add(pos, this.sheets.remove(idx));

    CTSheets ct = this.workbook.getSheets();
    XmlObject cts = ct.getSheetArray(idx).copy();
    this.workbook.getSheets().removeSheet(idx);
    CTSheet newcts = ct.insertNewSheet(pos);
    newcts.set(cts);

    for (int i = 0; i < this.sheets.size(); i++)
      ((XSSFSheet)this.sheets.get(i)).sheet = ct.getSheetArray(i);
  }

  private void saveNamedRanges()
  {
    if (this.namedRanges.size() > 0) {
      CTDefinedNames names = CTDefinedNames.Factory.newInstance();
      CTDefinedName[] nr = new CTDefinedName[this.namedRanges.size()];
      int i = 0;
      for (XSSFName name : this.namedRanges) {
        nr[i] = name.getCTName();
        i++;
      }
      names.setDefinedNameArray(nr);
      this.workbook.setDefinedNames(names);

      syncNamedRange();
    }
    else if (this.workbook.isSetDefinedNames()) {
      this.workbook.unsetDefinedNames();
    }
  }

  private void saveCalculationChain()
  {
    if (this.calcChain != null) {
      int count = this.calcChain.getCTCalcChain().sizeOfCArray();
      if (count == 0) {
        removeRelation(this.calcChain);
        this.calcChain = null;
      }
    }
  }

  protected void commit() throws IOException
  {
    saveNamedRanges();
    saveCalculationChain();

    XmlOptions xmlOptions = new XmlOptions(DEFAULT_XML_OPTIONS);
    xmlOptions.setSaveSyntheticDocumentElement(new QName(CTWorkbook.type.getName().getNamespaceURI(), "workbook"));
    Map map = new HashMap();
    map.put(STRelationshipId.type.getName().getNamespaceURI(), "r");
    xmlOptions.setSaveSuggestedPrefixes(map);

    PackagePart part = getPackagePart();
    OutputStream out = part.getOutputStream();
    this.workbook.save(out, xmlOptions);
    out.close();
  }

  @Internal
  public SharedStringsTable getSharedStringSource()
  {
    return this.sharedStringSource;
  }

  public StylesTable getStylesSource()
  {
    return this.stylesSource;
  }

  public ThemesTable getTheme()
  {
    return this.theme;
  }

  public XSSFCreationHelper getCreationHelper()
  {
    if (this._creationHelper == null) this._creationHelper = new XSSFCreationHelper(this);
    return this._creationHelper;
  }

  private boolean containsSheet(String name, int excludeSheetIdx)
  {
    CTSheet[] ctSheetArray = this.workbook.getSheets().getSheetArray();

    if (name.length() > 31) {
      name = name.substring(0, 31);
    }

    for (int i = 0; i < ctSheetArray.length; i++) {
      String ctName = ctSheetArray[i].getName();
      if (ctName.length() > 31) {
        ctName = ctName.substring(0, 31);
      }

      if ((excludeSheetIdx != i) && (name.equalsIgnoreCase(ctName)))
        return true;
    }
    return false;
  }

  protected boolean isDate1904()
  {
    CTWorkbookPr workbookPr = this.workbook.getWorkbookPr();
    return (workbookPr != null) && (workbookPr.getDate1904());
  }

  public List<PackagePart> getAllEmbedds()
    throws OpenXML4JException
  {
    List embedds = new LinkedList();

    XSSFSheet sheet;
    for (Iterator i$ = this.sheets.iterator(); i$.hasNext(); ) {
    	sheet = (XSSFSheet)i$.next();

      for (PackageRelationship rel : sheet.getPackagePart().getRelationshipsByType(XSSFRelation.OLEEMBEDDINGS.getRelation())) {
        embedds.add(sheet.getPackagePart().getRelatedPart(rel));
      }

      for (PackageRelationship rel : sheet.getPackagePart().getRelationshipsByType(XSSFRelation.PACKEMBEDDINGS.getRelation()))
        embedds.add(sheet.getPackagePart().getRelatedPart(rel));
    }
    
    return embedds;
  }

  public boolean isHidden() {
    throw new RuntimeException("Not implemented yet");
  }

  public void setHidden(boolean hiddenFlag) {
    throw new RuntimeException("Not implemented yet");
  }

  public boolean isSheetHidden(int sheetIx)
  {
    validateSheetIndex(sheetIx);
    CTSheet ctSheet = ((XSSFSheet)this.sheets.get(sheetIx)).sheet;
    return ctSheet.getState() == STSheetState.HIDDEN;
  }

  public boolean isSheetVeryHidden(int sheetIx)
  {
    validateSheetIndex(sheetIx);
    CTSheet ctSheet = ((XSSFSheet)this.sheets.get(sheetIx)).sheet;
    return ctSheet.getState() == STSheetState.VERY_HIDDEN;
  }

  public void setSheetHidden(int sheetIx, boolean hidden)
  {
    setSheetHidden(sheetIx, hidden ? 1 : 0);
  }

  public void setSheetHidden(int sheetIx, int state)
  {
    validateSheetIndex(sheetIx);
    WorkbookUtil.validateSheetState(state);
    CTSheet ctSheet = ((XSSFSheet)this.sheets.get(sheetIx)).sheet;
    ctSheet.setState(STSheetState.Enum.forInt(state + 1));
  }

  protected void onDeleteFormula(XSSFCell cell)
  {
    if (this.calcChain != null) {
      int sheetId = (int)cell.getSheet().sheet.getSheetId();
      this.calcChain.removeItem(sheetId, cell.getReference());
    }
  }

  @Internal
  public CalculationChain getCalculationChain()
  {
    return this.calcChain;
  }

  public Collection<XSSFMap> getCustomXMLMappings()
  {
    return this.mapInfo == null ? new ArrayList() : this.mapInfo.getAllXSSFMaps();
  }

  @Internal
  public MapInfo getMapInfo()
  {
    return this.mapInfo;
  }

  public boolean isStructureLocked()
  {
    return (workbookProtectionPresent()) && (this.workbook.getWorkbookProtection().getLockStructure());
  }

  public boolean isWindowsLocked()
  {
    return (workbookProtectionPresent()) && (this.workbook.getWorkbookProtection().getLockWindows());
  }

  public boolean isRevisionLocked()
  {
    return (workbookProtectionPresent()) && (this.workbook.getWorkbookProtection().getLockRevision());
  }

  public void lockStructure()
  {
    createProtectionFieldIfNotPresent();
    this.workbook.getWorkbookProtection().setLockStructure(true);
  }

  public void unLockStructure()
  {
    createProtectionFieldIfNotPresent();
    this.workbook.getWorkbookProtection().setLockStructure(false);
  }

  public void lockWindows()
  {
    createProtectionFieldIfNotPresent();
    this.workbook.getWorkbookProtection().setLockWindows(true);
  }

  public void unLockWindows()
  {
    createProtectionFieldIfNotPresent();
    this.workbook.getWorkbookProtection().setLockWindows(false);
  }

  public void lockRevision()
  {
    createProtectionFieldIfNotPresent();
    this.workbook.getWorkbookProtection().setLockRevision(true);
  }

  public void unLockRevision()
  {
    createProtectionFieldIfNotPresent();
    this.workbook.getWorkbookProtection().setLockRevision(false);
  }

  private boolean workbookProtectionPresent() {
    return this.workbook.getWorkbookProtection() != null;
  }

  private void createProtectionFieldIfNotPresent() {
    if (this.workbook.getWorkbookProtection() == null)
      this.workbook.setWorkbookProtection(CTWorkbookProtection.Factory.newInstance());
  }

  UDFFinder getUDFFinder()
  {
    return this._udfFinder;
  }

  public void addToolPack(UDFFinder toopack)
  {
    this._udfFinder.add(toopack);
  }

  public void setForceFormulaRecalculation(boolean value)
  {
    CTWorkbook ctWorkbook = getCTWorkbook();
    CTCalcPr calcPr = ctWorkbook.isSetCalcPr() ? ctWorkbook.getCalcPr() : ctWorkbook.addNewCalcPr();

    calcPr.setCalcId(0L);
  }

  public boolean getForceFormulaRecalculation()
  {
    CTWorkbook ctWorkbook = getCTWorkbook();
    CTCalcPr calcPr = ctWorkbook.getCalcPr();
    return (calcPr != null) && (calcPr.getCalcId() != 0L);
  }

  String getBookNameFromExternalLinkIndex(String externalLinkIndex)
  {
    return (String)this.linkIndexToBookName.get(externalLinkIndex);
  }

  private void syncNamedRange()
  {
    this.namedRanges = new ArrayList();
    if (this.workbook.isSetDefinedNames())
      for (CTDefinedName ctName : this.workbook.getDefinedNames().getDefinedNameArray())
        this.namedRanges.add(new XSSFName(ctName, this));
  }

  void setPictureData(int pictureIndex, XSSFPictureData img)
  {
    this.pictures.set(pictureIndex, img);
  }

  String getExternalLinkIndexFromBookName(String bookname)
  {
    return (String)this.bookNameToLinkIndex.get(bookname);
  }

  public List<PivotCache> getPivotCaches()
  {
    if (this._pivotCaches == null) {
      this._pivotCaches = XSSFPivotTableHelpers.instance.getHelper().initPivotCaches(this);
    }
    return this._pivotCaches;
  }

  public PivotCache createPivotCache(AreaReference sourceRef)
  {
    return XSSFPivotTableHelpers.instance.getHelper().createPivotCache(sourceRef, this);
  }
}