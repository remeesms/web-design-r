/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.zkoss.poi.hssf.usermodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.poi.ddf.EscherComplexProperty;
import org.zkoss.poi.ddf.EscherOptRecord;
import org.zkoss.poi.ddf.EscherProperty;
import org.zkoss.poi.ddf.EscherBSERecord;
import org.zkoss.poi.hssf.record.EscherAggregate;
import org.zkoss.poi.ss.usermodel.Chart;
import org.zkoss.poi.util.StringUtil;
import org.zkoss.poi.util.Internal;
import org.zkoss.poi.ss.usermodel.Drawing;
import org.zkoss.poi.ss.usermodel.ClientAnchor;
import org.zkoss.poi.ss.usermodel.Picture;
import org.zkoss.poi.ss.usermodel.ZssChartX;

/**
 * The patriarch is the toplevel container for shapes in a sheet.  It does
 * little other than act as a container for other shapes and groups.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public final class HSSFPatriarch implements HSSFShapeContainer, Drawing {
    private final List<HSSFShape> _shapes = new ArrayList<HSSFShape>();
    private int _x1 = 0;
    private int _y1  = 0 ;
    private int _x2 = 1023;
    private int _y2 = 255;

    /**
     * The EscherAggregate we have been bound to.
     * (This will handle writing us out into records,
     *  and building up our shapes from the records)
     */
    private EscherAggregate _boundAggregate;
	final HSSFSheet _sheet; // TODO make private

    /**
     * Creates the patriarch.
     *
     * @param sheet the sheet this patriarch is stored in.
     */
    HSSFPatriarch(HSSFSheet sheet, EscherAggregate boundAggregate){
        _sheet = sheet;
		_boundAggregate = boundAggregate;
    }

    /**
     * Creates a new group record stored under this patriarch.
     *
     * @param anchor    the client anchor describes how this group is attached
     *                  to the sheet.
     * @return  the newly created group.
     */
    public HSSFShapeGroup createGroup(HSSFClientAnchor anchor)
    {
        HSSFShapeGroup group = new HSSFShapeGroup(null, anchor);
        group.anchor = anchor;
        addShape(group);
        return group;
    }

    /**
     * Creates a simple shape.  This includes such shapes as lines, rectangles,
     * and ovals.
     *
     * @param anchor    the client anchor describes how this group is attached
     *                  to the sheet.
     * @return  the newly created shape.
     */
    public HSSFSimpleShape createSimpleShape(HSSFClientAnchor anchor)
    {
        HSSFSimpleShape shape = new HSSFSimpleShape(null, anchor);
        shape.anchor = anchor;
        addShape(shape);
        return shape;
    }

    /**
     * Creates a picture.
     *
     * @param anchor    the client anchor describes how this group is attached
     *                  to the sheet.
     * @return  the newly created shape.
     */
    public HSSFPicture createPicture(HSSFClientAnchor anchor, int pictureIndex)
    {
        HSSFPicture shape = new HSSFPicture(null, anchor);
        shape.setPictureIndex( pictureIndex );
        shape.anchor = anchor;
        addShape(shape);

        EscherBSERecord bse = _sheet.getWorkbook().getWorkbook().getBSERecord(pictureIndex);
        bse.setRef(bse.getRef() + 1);
        return shape;
    }

    public HSSFPicture createPicture(ClientAnchor anchor, int pictureIndex)
    {
        return createPicture((HSSFClientAnchor)anchor, pictureIndex);
    }

    /**
     * Creates a polygon
     *
     * @param anchor    the client anchor describes how this group is attached
     *                  to the sheet.
     * @return  the newly created shape.
     */
    public HSSFPolygon createPolygon(HSSFClientAnchor anchor)
    {
        HSSFPolygon shape = new HSSFPolygon(null, anchor);
        shape.anchor = anchor;
        addShape(shape);
        return shape;
    }

    /**
     * Constructs a textbox under the patriarch.
     *
     * @param anchor    the client anchor describes how this group is attached
     *                  to the sheet.
     * @return      the newly created textbox.
     */
    public HSSFTextbox createTextbox(HSSFClientAnchor anchor)
    {
        HSSFTextbox shape = new HSSFTextbox(null, anchor);
        shape.anchor = anchor;
        addShape(shape);
        return shape;
    }

    /**
     * Constructs a cell comment.
     *
     * @param anchor    the client anchor describes how this comment is attached
     *                  to the sheet.
     * @return      the newly created comment.
     */
   public HSSFComment createComment(HSSFAnchor anchor)
    {
        HSSFComment shape = new HSSFComment(null, anchor);
        shape.anchor = anchor;
        addShape(shape);
        return shape;
    }

    /**
     * YK: used to create autofilters
     *
     * @see org.zkoss.poi.hssf.usermodel.HSSFSheet#setAutoFilter(org.zkoss.poi.ss.util.CellRangeAddress)
     */
     HSSFSimpleShape createComboBox(HSSFAnchor anchor)
     {
         HSSFSimpleShape shape = new HSSFSimpleShape(null, anchor);
         shape.setShapeType(HSSFSimpleShape.OBJECT_TYPE_COMBO_BOX);
         shape.anchor = anchor;
         addShape(shape);
         return shape;
     }

    public HSSFComment createCellComment(ClientAnchor anchor) {
        return createComment((HSSFAnchor)anchor);
    }

    /**
     * Returns a list of all shapes contained by the patriarch.
     */
    public List<HSSFShape> getChildren()
    {
        return _shapes;
    }

    /**
     * add a shape to this drawing
     */
    @Internal
    public void addShape(HSSFShape shape){
        shape._patriarch = this;
        _shapes.add(shape);
    }

    /**
     * Total count of all children and their children's children.
     */
    public int countOfAllChildren() {
        int count = _shapes.size();
        for (Iterator<HSSFShape> iterator = _shapes.iterator(); iterator.hasNext();) {
            HSSFShape shape = iterator.next();
            count += shape.countOfAllChildren();
        }
        return count;
    }
    /**
     * Sets the coordinate space of this group.  All children are constrained
     * to these coordinates.
     */
    public void setCoordinates(int x1, int y1, int x2, int y2){
        _x1 = x1;
        _y1 = y1;
        _x2 = x2;
        _y2 = y2;
    }

    /**
     * Does this HSSFPatriarch contain a chart?
     * (Technically a reference to a chart, since they
     *  get stored in a different block of records)
     * FIXME - detect chart in all cases (only seems
     *  to work on some charts so far)
     */
    public boolean containsChart() {
        // TODO - support charts properly in usermodel

        // We're looking for a EscherOptRecord
        EscherOptRecord optRecord = (EscherOptRecord)
            _boundAggregate.findFirstWithId(EscherOptRecord.RECORD_ID);
        if(optRecord == null) {
            // No opt record, can't have chart
            return false;
        }

        for(Iterator<EscherProperty> it = optRecord.getEscherProperties().iterator(); it.hasNext();) {
            EscherProperty prop = it.next();
            if(prop.getPropertyNumber() == 896 && prop.isComplex()) {
                EscherComplexProperty cp = (EscherComplexProperty)prop;
                String str = StringUtil.getFromUnicodeLE(cp.getComplexData());

                if(str.equals("Chart 1\0")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * The top left x coordinate of this group.
     */
    public int getX1()
    {
        return _x1;
    }

    /**
     * The top left y coordinate of this group.
     */
    public int getY1()
    {
        return _y1;
    }

    /**
     * The bottom right x coordinate of this group.
     */
    public int getX2()
    {
        return _x2;
    }

    /**
     * The bottom right y coordinate of this group.
     */
    public int getY2()
    {
        return _y2;
    }

    /**
     * Returns the aggregate escher record we're bound to
     */
    protected EscherAggregate _getBoundAggregate() {
        return _boundAggregate;
    }

    /**
     * Creates a new client anchor and sets the top-left and bottom-right
     * coordinates of the anchor.
     *
     * @param dx1  the x coordinate in EMU within the first cell.
     * @param dy1  the y coordinate in EMU within the first cell.
     * @param dx2  the x coordinate in EMU within the second cell.
     * @param dy2  the y coordinate in EMU within the second cell.
     * @param col1 the column (0 based) of the first cell.
     * @param row1 the row (0 based) of the first cell.
     * @param col2 the column (0 based) of the second cell.
     * @param row2 the row (0 based) of the second cell.
     * @return the newly created client anchor
     */
    public HSSFClientAnchor createAnchor(int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2){
        return new HSSFClientAnchor(dx1, dy1, dx2, dy2, (short)col1, row1, (short)col2, row2);
    }

	public Chart createChart(ClientAnchor anchor) {
		throw new RuntimeException("NotImplemented");
	}

    //20101014, henrichen@zkoss.org: handle chart creation
    public HSSFChartX createChart(HSSFAnchor anchor, HSSFChart chart)
    {
        HSSFChartX shape = new HSSFChartX(null, anchor);
        shape.setChart(chart);
        shape.anchor = anchor;
        shape._patriarch = this;
        _shapes.add(shape);
        return shape;
    }

    //20111109, henrichen@zkoss.org: currently support XSSFPicture only
	@Override
	public void deletePicture(Picture picture) {
		int pictureIndex = ((HSSFPicture)picture).getPictureIndex();
        EscherBSERecord bse = _sheet.getWorkbook().getWorkbook().getBSERecord(pictureIndex);
        bse.setRef(bse.getRef() - 1);
        _shapes.remove(picture);
	}

	//20111110, henrichen@zkoss.org: update picture anchor place
	@Override
	public void movePicture(Picture pic, ClientAnchor anchor) {
		pic.setClientAnchor(anchor);
	}

	//20111111, henrichen@zkoss.org: update chart anchor place
	@Override
	public void moveChart(ZssChartX chart, ClientAnchor anchor) {
		chart.setClientAnchor(anchor);
	}

	@Override
	public void deleteChart(ZssChartX chartX) {
		//TODO: remove chart record from sheet
		//TODO: remove anchor record from sheet
		//TODO: remove chart record from workbook
		_shapes.remove(chartX);
	}
}

