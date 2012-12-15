package org.zkoss.poi.xssf.usermodel;

import org.zkoss.poi.ss.usermodel.ZssTableX;
import org.zkoss.poi.ss.util.AreaReference;

public class XSSFTableX implements ZssTableX{

	private XSSFTable _table;
	
	public XSSFTableX (XSSFTable table) {
		this._table = table;
	}
	
    public AreaReference getPreferredSize() {
    	// TODO
    	// 根据XSSFTable得到table的位置和尺寸
    	// 到底使用AreaReference还是ClientAnchor，酌情而定，都行。使用后者的话，改改接口。
    	return null;
    }
    

	public String getTableId() {
		// TODO
		return "1";
	}
	
	public XSSFTable getTable() {
		return _table;
	}
	
	public void setClientAnchor(AreaReference anchor) {
		
	}
	
}
