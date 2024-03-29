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

import org.zkoss.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.zkoss.poi.ss.formula.IStabilityClassifier;
import org.zkoss.poi.ss.formula.WorkbookEvaluator;
import org.zkoss.poi.ss.formula.eval.ArrayEval;
import org.zkoss.poi.ss.formula.eval.BoolEval;
import org.zkoss.poi.ss.formula.eval.ErrorEval;
import org.zkoss.poi.ss.formula.eval.NumberEval;
import org.zkoss.poi.ss.formula.eval.StringEval;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.udf.UDFFinder;
import org.zkoss.poi.ss.usermodel.Cell;
import org.zkoss.poi.ss.usermodel.CellValue;
import org.zkoss.poi.ss.usermodel.FormulaEvaluator;
import org.zkoss.poi.ss.usermodel.Row;
import org.zkoss.poi.ss.usermodel.Sheet;
import org.zkoss.poi.ss.usermodel.Workbook;

import org.zkoss.poi.ss.formula.eval.HyperlinkEval;
/**
 * Evaluates formula cells.<p/>
 *
 * For performance reasons, this class keeps a cache of all previously calculated intermediate
 * cell values.  Be sure to call {@link #clearAllCachedResultValues()} if any workbook cells are changed between
 * calls to evaluate~ methods on this class.
 *
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 * @author Josh Micich
 * @author henrichen@zkoss.org: handle HYPERLINK function
 */
public class HSSFFormulaEvaluator implements FormulaEvaluator  {

	private WorkbookEvaluator _bookEvaluator;
	private HSSFWorkbook _book;

	/**
	 * @deprecated (Sep 2008) HSSFSheet parameter is ignored
	 */
	public HSSFFormulaEvaluator(HSSFSheet sheet, HSSFWorkbook workbook) {
		this(workbook);
		if (false) {
			sheet.toString(); // suppress unused parameter compiler warning
		}
		this._book = workbook;
	}
	public HSSFFormulaEvaluator(HSSFWorkbook workbook) {
		this(workbook, null);
      this._book = workbook;
	}
	/**
	 * @param stabilityClassifier used to optimise caching performance. Pass <code>null</code>
	 * for the (conservative) assumption that any cell may have its definition changed after
	 * evaluation begins.
	 */
	public HSSFFormulaEvaluator(HSSFWorkbook workbook, IStabilityClassifier stabilityClassifier) {
		this(workbook, stabilityClassifier, null);
	}

	/**
	 * @param udfFinder pass <code>null</code> for default (AnalysisToolPak only)
	 */
	private HSSFFormulaEvaluator(HSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
		_bookEvaluator = new WorkbookEvaluator(HSSFEvaluationWorkbook.create(workbook), stabilityClassifier, udfFinder);
	}

	/**
	 * @param stabilityClassifier used to optimise caching performance. Pass <code>null</code>
	 * for the (conservative) assumption that any cell may have its definition changed after
	 * evaluation begins.
	 * @param udfFinder pass <code>null</code> for default (AnalysisToolPak only)
	 */
	public static HSSFFormulaEvaluator create(HSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
		return new HSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
	}


	/**
	 * Coordinates several formula evaluators together so that formulas that involve external
	 * references can be evaluated.
	 * @param workbookNames the simple file names used to identify the workbooks in formulas
	 * with external links (for example "MyData.xls" as used in a formula "[MyData.xls]Sheet1!A1")
	 * @param evaluators all evaluators for the full set of workbooks required by the formulas.
	 */
	public static void setupEnvironment(String[] workbookNames, HSSFFormulaEvaluator[] evaluators) {
		WorkbookEvaluator[] wbEvals = new WorkbookEvaluator[evaluators.length];
		for (int i = 0; i < wbEvals.length; i++) {
			wbEvals[i] = evaluators[i]._bookEvaluator;
		}
		CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
	}

	/**
	 * Does nothing
	 * @deprecated (Aug 2008) - not needed, since the current row can be derived from the cell
	 */
	public void setCurrentRow(HSSFRow row) {
		// do nothing
		if (false) {
			row.getClass(); // suppress unused parameter compiler warning
		}
	}

	/**
	 * Should be called whenever there are major changes (e.g. moving sheets) to input cells
	 * in the evaluated workbook.  If performance is not critical, a single call to this method
	 * may be used instead of many specific calls to the notify~ methods.
	 *
	 * Failure to call this method after changing cell values will cause incorrect behaviour
	 * of the evaluate~ methods of this class
	 */
	public void clearAllCachedResultValues() {
		_bookEvaluator.clearAllCachedResultValues();
	}
	/**
	 * Should be called to tell the cell value cache that the specified (value or formula) cell
	 * has changed.
	 * Failure to call this method after changing cell values will cause incorrect behaviour
	 * of the evaluate~ methods of this class
	 */
	public void notifyUpdateCell(HSSFCell cell) {
		_bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell(cell));
	}
    public void notifyUpdateCell(Cell cell) {
        _bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((HSSFCell)cell));
    }
	/**
	 * Should be called to tell the cell value cache that the specified cell has just been
	 * deleted.
	 * Failure to call this method after changing cell values will cause incorrect behaviour
	 * of the evaluate~ methods of this class
	 */
	public void notifyDeleteCell(HSSFCell cell) {
		_bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell(cell));
	}
	public void notifyDeleteCell(Cell cell) {
		_bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell((HSSFCell)cell));
	}

	/**
	 * Should be called to tell the cell value cache that the specified (value or formula) cell
	 * has changed.
	 * Failure to call this method after changing cell values will cause incorrect behaviour
	 * of the evaluate~ methods of this class
	 */
	public void notifySetFormula(Cell cell) {
		_bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((HSSFCell)cell));
	}

	/**
	 * If cell contains a formula, the formula is evaluated and returned,
	 * else the CellValue simply copies the appropriate cell value from
	 * the cell and also its cell type. This method should be preferred over
	 * evaluateInCell() when the call should not modify the contents of the
	 * original cell.
	 *
	 * @param cell may be <code>null</code> signifying that the cell is not present (or blank)
	 * @return <code>null</code> if the supplied cell is <code>null</code> or blank
	 */
	public CellValue evaluate(Cell cell) {
		if (cell == null) {
			return null;
		}

		switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_BOOLEAN:
				return CellValue.valueOf(cell.getBooleanCellValue());
			case HSSFCell.CELL_TYPE_ERROR:
				return CellValue.getError(cell.getErrorCellValue());
			case HSSFCell.CELL_TYPE_FORMULA:
				return evaluateFormulaCellValue(cell);
			case HSSFCell.CELL_TYPE_NUMERIC:
				return new CellValue(cell.getNumericCellValue());
			case HSSFCell.CELL_TYPE_STRING:
				return new CellValue(cell.getRichStringCellValue().getString());
			case HSSFCell.CELL_TYPE_BLANK:
				return null;
		}
		throw new IllegalStateException("Bad cell type (" + cell.getCellType() + ")");
	}


	/**
	 * If cell contains formula, it evaluates the formula, and saves the result of the formula. The
	 * cell remains as a formula cell. If the cell does not contain formula, this method returns -1
	 * and leaves the cell unchanged.
	 *
	 * Note that the type of the <em>formula result</em> is returned, so you know what kind of
	 * cached formula result is also stored with  the formula.
	 * <pre>
	 * int evaluatedCellType = evaluator.evaluateFormulaCell(cell);
	 * </pre>
	 * Be aware that your cell will hold both the formula, and the result. If you want the cell
	 * replaced with the result of the formula, use {@link #evaluateInCell(org.zkoss.poi.ss.usermodel.Cell)}
	 * @param cell The cell to evaluate
	 * @return -1 for non-formula cells, or the type of the <em>formula result</em>
	 */
	public int evaluateFormulaCell(Cell cell) {
		if (cell == null || cell.getCellType() != HSSFCell.CELL_TYPE_FORMULA) {
			return -1;
		}
		CellValue cv = evaluateFormulaCellValue(cell);
		// cell remains a formula cell, but the cached value is changed
		setCellValue(cell, cv);
		return cv.getCellType();
	}

	/**
	 * If cell contains formula, it evaluates the formula, and
	 *  puts the formula result back into the cell, in place
	 *  of the old formula.
	 * Else if cell does not contain formula, this method leaves
	 *  the cell unchanged.
	 * Note that the same instance of HSSFCell is returned to
	 * allow chained calls like:
	 * <pre>
	 * int evaluatedCellType = evaluator.evaluateInCell(cell).getCellType();
	 * </pre>
	 * Be aware that your cell value will be changed to hold the
	 *  result of the formula. If you simply want the formula
	 *  value computed for you, use {@link #evaluateFormulaCell(Cell)}}
	 */
	public HSSFCell evaluateInCell(Cell cell) {
		if (cell == null) {
			return null;
		}
		HSSFCell result = (HSSFCell) cell;
		if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
			CellValue cv = evaluateFormulaCellValue(cell);
			setCellValue(cell, cv);
			setCellType(cell, cv); // cell will no longer be a formula cell
		}
		return result;
	}
	private static void setCellType(Cell cell, CellValue cv) {
		int cellType = cv.getCellType();
		switch (cellType) {
			case HSSFCell.CELL_TYPE_BOOLEAN:
			case HSSFCell.CELL_TYPE_ERROR:
			case HSSFCell.CELL_TYPE_NUMERIC:
			case HSSFCell.CELL_TYPE_STRING:
				cell.setCellType(cellType);
				return;
			case HSSFCell.CELL_TYPE_BLANK:
				// never happens - blanks eventually get translated to zero
			case HSSFCell.CELL_TYPE_FORMULA:
				// this will never happen, we have already evaluated the formula
		}
		throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
	}

	private static void setCellValue(Cell cell, CellValue cv) {
		int cellType = cv.getCellType();
		switch (cellType) {
			case HSSFCell.CELL_TYPE_BOOLEAN:
				cell.setCellValue(cv.getBooleanValue());
				break;
			case HSSFCell.CELL_TYPE_ERROR:
				cell.setCellErrorValue(cv.getErrorValue());
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				cell.setCellValue(cv.getNumberValue());
				break;
			case HSSFCell.CELL_TYPE_STRING:
				cell.setCellValue(new HSSFRichTextString(cv.getStringValue()));
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				// never happens - blanks eventually get translated to zero
			case HSSFCell.CELL_TYPE_FORMULA:
				// this will never happen, we have already evaluated the formula
			default:
				throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
		}
	}

	/**
	 * Loops over all cells in all sheets of the supplied
	 *  workbook.
	 * For cells that contain formulas, their formulas are
	 *  evaluated, and the results are saved. These cells
	 *  remain as formula cells.
	 * For cells that do not contain formulas, no changes
	 *  are made.
	 * This is a helpful wrapper around looping over all
	 *  cells, and calling evaluateFormulaCell on each one.
	 */
	public static void evaluateAllFormulaCells(HSSFWorkbook wb) {
	   evaluateAllFormulaCells(wb, new HSSFFormulaEvaluator(wb));
	}
	
   /**
    * Loops over all cells in all sheets of the supplied
    *  workbook.
    * For cells that contain formulas, their formulas are
    *  evaluated, and the results are saved. These cells
    *  remain as formula cells.
    * For cells that do not contain formulas, no changes
    *  are made.
    * This is a helpful wrapper around looping over all
    *  cells, and calling evaluateFormulaCell on each one.
    */
	public static void evaluateAllFormulaCells(Workbook wb) {
      FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
      evaluateAllFormulaCells(wb, evaluator);
	}
	private static void evaluateAllFormulaCells(Workbook wb, FormulaEvaluator evaluator) {
      for(int i=0; i<wb.getNumberOfSheets(); i++) {
         Sheet sheet = wb.getSheetAt(i);

         for(Row r : sheet) {
            for (Cell c : r) {
               if (c.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
                  evaluator.evaluateFormulaCell(c);
               }
            }
         }
      }
	}
	
   /**
    * Loops over all cells in all sheets of the supplied
    *  workbook.
    * For cells that contain formulas, their formulas are
    *  evaluated, and the results are saved. These cells
    *  remain as formula cells.
    * For cells that do not contain formulas, no changes
    *  are made.
    * This is a helpful wrapper around looping over all
    *  cells, and calling evaluateFormulaCell on each one.
    */
   public void evaluateAll() {
      evaluateAllFormulaCells(_book, this);
   }

	/**
	 * Returns a CellValue wrapper around the supplied ValueEval instance.
	 * @param eval
	 */
	private CellValue evaluateFormulaCellValue(Cell cell) {
		ValueEval eval = _bookEvaluator.evaluate(new HSSFEvaluationCell((HSSFCell)cell));
		return getCellValueByValueEval(eval);
	}
	
	@Override
	public WorkbookEvaluator getWorkbookEvaluator() {
		return _bookEvaluator;
	}

	//20111124, henrichen@zkoss.org: give ValueEval, evaluate to CellValue
	@Override
	public CellValue getCellValueByValueEval(ValueEval eval) {
		//20100720, henrichen@zkoss.org: handle HYPERLINK function 
		CellValue cv = null;
		if (eval instanceof ArrayEval) {
			return getCellValueByValueEval(((ArrayEval)eval).getValue(0, 0)); //recursive and get the 1st cell
		}
		if (eval instanceof NumberEval) {
			NumberEval ne = (NumberEval) eval;
			cv = new CellValue(ne.getNumberValue());
		}
		if (eval instanceof BoolEval) {
			BoolEval be = (BoolEval) eval;
			cv = CellValue.valueOf(be.getBooleanValue());
		}
		if (eval instanceof StringEval) {
			StringEval ne = (StringEval) eval;
			cv = new CellValue(ne.getStringValue());
		}
		if (eval instanceof ErrorEval) {
			//20110407, henrichne@zkoss.org: degenerate CIRCULAR_REF_ERROR to REF_INVALID
			cv = CellValue.getError(((ErrorEval)eval).getErrorCode() == ErrorEval.CIRCULAR_REF_ERROR.getErrorCode() ?
					ErrorEval.REF_INVALID.getErrorCode() : ((ErrorEval)eval).getErrorCode());
		}
		if (cv != null) {
			if (eval instanceof HyperlinkEval) {
				cv.setHyperlink(((HyperlinkEval)eval).getHyperlink());
			}
			return cv;
		}
		throw new RuntimeException("Unexpected eval class (" + eval.getClass().getName() + ")");
	}

    /**
     * Whether to ignore missing references to external workbooks and
     * use cached formula results in the main workbook instead.
     * <p>
     * In some cases exetrnal workbooks referenced by formulas in the main workbook are not avaiable.
     * With this method you can control how POI handles such missing references:
     * <ul>
     *     <li>by default ignoreMissingWorkbooks=false and POI throws {@link org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment.WorkbookNotFoundException}
     *     if an external reference cannot be resolved</li>
     *     <li>if ignoreMissingWorkbooks=true then POI uses cached formula result
     *     that already exists in the main workbook</li>
     * </ul>
     *
     * @param ignore whether to ignore missing references to external workbooks
     */
    public void setIgnoreMissingWorkbooks(boolean ignore){
        _bookEvaluator.setIgnoreMissingWorkbooks(ignore);
    }

	
	//20111124, henrichen@zkoss.org: evaluate with sheet and formula text only.
	@Override
	public CellValue evaluateFormula(int sheetIndex, String formula) {
		ValueEval eval = _bookEvaluator.evaluate(sheetIndex, formula, false);
		return getCellValueByValueEval(eval);
	}
	//20111128, henrichen@zkoss.org: evaluate with sheet and formula text and return ValueEval.
	@Override
	public ValueEval evaluateFormulaValueEval(int sheetIndex, String formula, boolean ignoreDereference) {
		return _bookEvaluator.evaluate(sheetIndex, formula, ignoreDereference);
	}
}
