package org.zkoss.poi.xssf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataConsolidateFunction;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataConsolidateFunction.Enum;
import org.zkoss.poi.ss.usermodel.Calculation;
import org.zkoss.poi.ss.usermodel.DataField;
import org.zkoss.poi.ss.usermodel.PivotField;

public class XSSFDataField
  implements DataField
{
  private final CTDataField _dataField;
  private final PivotField _pivotField;
  private static HashMap<STDataConsolidateFunction.Enum, Calculation> subtotalMap;
  private static HashMap<Calculation, STDataConsolidateFunction.Enum> subtotalMap2;

  XSSFDataField(CTDataField dataField, PivotField pivotField)
  {
    this._dataField = dataField;
    this._pivotField = pivotField;
  }

  public PivotField getPivotField() {
    return this._pivotField;
  }

  public String getName() {
    return this._dataField.getName();
  }

  public Calculation getSubtotal() {
    STDataConsolidateFunction.Enum type = this._dataField.getSubtotal();
    if (subtotalMap == null)
      initSubtotalMap();

    return ((Calculation)subtotalMap.get(type));
  }

  private static void initSubtotalMap()
  {
    subtotalMap = new HashMap();
    subtotalMap.put(STDataConsolidateFunction.AVERAGE, Calculation.AVERAGE);
    subtotalMap.put(STDataConsolidateFunction.COUNT, Calculation.COUNT);
    subtotalMap.put(STDataConsolidateFunction.COUNT_NUMS, Calculation.COUNT_NUMS);
    subtotalMap.put(STDataConsolidateFunction.MAX, Calculation.MAX);
    subtotalMap.put(STDataConsolidateFunction.MIN, Calculation.MIN);
    subtotalMap.put(STDataConsolidateFunction.PRODUCT, Calculation.PRODUCT);
    subtotalMap.put(STDataConsolidateFunction.STD_DEV, Calculation.STD_DEV);
    subtotalMap.put(STDataConsolidateFunction.STD_DEVP, Calculation.STD_DEV_P);
    subtotalMap.put(STDataConsolidateFunction.SUM, Calculation.SUM);
    subtotalMap.put(STDataConsolidateFunction.VAR, Calculation.VARIANCE);
    subtotalMap.put(STDataConsolidateFunction.VARP, Calculation.VARIANCE_P);

    subtotalMap2 = new HashMap();
    subtotalMap2.put(Calculation.AVERAGE, STDataConsolidateFunction.AVERAGE);
    subtotalMap2.put(Calculation.COUNT, STDataConsolidateFunction.COUNT);
    subtotalMap2.put(Calculation.COUNT_NUMS, STDataConsolidateFunction.COUNT_NUMS);
    subtotalMap2.put(Calculation.MAX, STDataConsolidateFunction.MAX);
    subtotalMap2.put(Calculation.MIN, STDataConsolidateFunction.MIN);
    subtotalMap2.put(Calculation.PRODUCT, STDataConsolidateFunction.PRODUCT);
    subtotalMap2.put(Calculation.STD_DEV, STDataConsolidateFunction.STD_DEV);
    subtotalMap2.put(Calculation.STD_DEV_P, STDataConsolidateFunction.STD_DEVP);
    subtotalMap2.put(Calculation.SUM, STDataConsolidateFunction.SUM);
    subtotalMap2.put(Calculation.VARIANCE, STDataConsolidateFunction.VAR);
    subtotalMap2.put(Calculation.VARIANCE_P, STDataConsolidateFunction.VARP);
  }

  static STDataConsolidateFunction.Enum getSubtotalType(Calculation subtotal) {
    if (subtotalMap2 == null)
      initSubtotalMap();

    return ((STDataConsolidateFunction.Enum)subtotalMap2.get(subtotal));
  }
}