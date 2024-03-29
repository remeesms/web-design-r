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

package org.zkoss.poi.ss.formula;

import java.util.*;

import org.zkoss.poi.ss.formula.ptg.Area3DPtg;
import org.zkoss.poi.ss.formula.ptg.AreaErrPtg;
import org.zkoss.poi.ss.formula.ptg.AreaPtg;
import org.zkoss.poi.ss.formula.ptg.ArrayPtg;
import org.zkoss.poi.ss.formula.ptg.AttrPtg;
import org.zkoss.poi.ss.formula.ptg.BoolPtg;
import org.zkoss.poi.ss.formula.ptg.ControlPtg;
import org.zkoss.poi.ss.formula.ptg.DeletedArea3DPtg;
import org.zkoss.poi.ss.formula.ptg.DeletedRef3DPtg;
import org.zkoss.poi.ss.formula.ptg.ErrPtg;
import org.zkoss.poi.ss.formula.ptg.ExpPtg;
import org.zkoss.poi.ss.formula.ptg.FuncVarPtg;
import org.zkoss.poi.ss.formula.ptg.IntPtg;
import org.zkoss.poi.ss.formula.ptg.MemAreaPtg;
import org.zkoss.poi.ss.formula.ptg.MemErrPtg;
import org.zkoss.poi.ss.formula.ptg.MemFuncPtg;
import org.zkoss.poi.ss.formula.ptg.MissingArgPtg;
import org.zkoss.poi.ss.formula.ptg.NamePtg;
import org.zkoss.poi.ss.formula.ptg.NameXPtg;
import org.zkoss.poi.ss.formula.ptg.NumberPtg;
import org.zkoss.poi.ss.formula.ptg.OperationPtg;
import org.zkoss.poi.ss.formula.ptg.Ptg;
import org.zkoss.poi.ss.formula.ptg.Ref3DPtg;
import org.zkoss.poi.ss.formula.ptg.RefErrorPtg;
import org.zkoss.poi.ss.formula.ptg.RefPtg;
import org.zkoss.poi.ss.formula.ptg.StringPtg;
import org.zkoss.poi.ss.formula.ptg.UnionPtg;
import org.zkoss.poi.ss.formula.ptg.UnknownPtg;
import org.zkoss.poi.ss.formula.atp.AnalysisToolPak;
import org.zkoss.poi.ss.formula.eval.AreaEval;
import org.zkoss.poi.ss.formula.eval.ArrayEval;
import org.zkoss.poi.ss.formula.eval.BlankEval;
import org.zkoss.poi.ss.formula.eval.BoolEval;
import org.zkoss.poi.ss.formula.eval.ErrorEval;
import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.FunctionEval;
import org.zkoss.poi.ss.formula.eval.MissingArgEval;
import org.zkoss.poi.ss.formula.eval.NameEval;
import org.zkoss.poi.ss.formula.eval.NameXEval;
import org.zkoss.poi.ss.formula.eval.NumberEval;
import org.zkoss.poi.ss.formula.eval.OperandResolver;
import org.zkoss.poi.ss.formula.eval.RefEval;
import org.zkoss.poi.ss.formula.eval.StringEval;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.eval.ValuesEval;
import org.zkoss.poi.ss.formula.functions.Choose;
import org.zkoss.poi.ss.formula.functions.FreeRefFunction;
import org.zkoss.poi.ss.formula.functions.Function;
import org.zkoss.poi.ss.formula.functions.IfFunc;
import org.zkoss.poi.ss.formula.udf.AggregatingUDFFinder;
import org.zkoss.poi.ss.formula.udf.UDFFinder;
import org.zkoss.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.zkoss.poi.hssf.util.CellReference;
import org.zkoss.poi.ss.formula.CollaboratingWorkbooksEnvironment.WorkbookNotFoundException;
import org.zkoss.poi.ss.formula.eval.NotImplementedException;
import org.zkoss.poi.ss.usermodel.Cell;
import org.zkoss.poi.ss.usermodel.Sheet;
import org.zkoss.poi.util.POILogFactory;
import org.zkoss.poi.util.POILogger;

/**
 * Evaluates formula cells.<p/>
 *
 * For performance reasons, this class keeps a cache of all previously calculated intermediate
 * cell values.  Be sure to call {@link #clearAllCachedResultValues()} if any workbook cells are changed between
 * calls to evaluate~ methods on this class.<br/>
 *
 * For POI internal use only
 *
 * @author Josh Micich
 * @author Henri Chen (henrichen at zkoss dot org) - Sheet1:Sheet3!xxx 3d reference, dependency tracking
 */
public final class WorkbookEvaluator {
	
	private static final POILogger LOG = POILogFactory.getLogger(WorkbookEvaluator.class);

    private final EvaluationWorkbook _workbook;
	private EvaluationCache _cache;
	/** part of cache entry key (useful when evaluating multiple workbooks) */
	private int _workbookIx;

	private final IEvaluationListener _evaluationListener;
	private final Map<EvaluationSheet, Integer> _sheetIndexesBySheet;
	private final Map<String, Integer> _sheetIndexesByName;
	private CollaboratingWorkbooksEnvironment _collaboratingWorkbookEnvironment;
	private final IStabilityClassifier _stabilityClassifier;
	private final AggregatingUDFFinder _udfFinder;
	
	private DependencyTracker _dependencyTracker;

    private boolean _ignoreMissingWorkbooks = false;

	/**
	 * @param udfFinder pass <code>null</code> for default (AnalysisToolPak only)
	 */
	public WorkbookEvaluator(EvaluationWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
		this (workbook, null, stabilityClassifier, udfFinder);
	}
	/* package */ WorkbookEvaluator(EvaluationWorkbook workbook, IEvaluationListener evaluationListener,
			IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
		_workbook = workbook;
		_evaluationListener = evaluationListener;
		_cache = new EvaluationCache(evaluationListener);
		_sheetIndexesBySheet = new IdentityHashMap<EvaluationSheet, Integer>();
		_sheetIndexesByName = new IdentityHashMap<String, Integer>();
		_collaboratingWorkbookEnvironment = CollaboratingWorkbooksEnvironment.EMPTY;
		_workbookIx = 0;
		_stabilityClassifier = stabilityClassifier;

        AggregatingUDFFinder defaultToolkit = // workbook can be null in unit tests
                workbook == null ? null : (AggregatingUDFFinder)workbook.getUDFFinder();
        if(defaultToolkit != null && udfFinder != null) {
            defaultToolkit.add(udfFinder);
        }
        _udfFinder = defaultToolkit;
	}
	
	public void setDependencyTracker(DependencyTracker tracker) {
		_dependencyTracker = tracker;
	}
	
	/**
	 * also for debug use. Used in toString methods
	 */
	/* package */ String getSheetName(int sheetIndex) {
		return _workbook.getSheetName(sheetIndex);
	}

	/* package */ EvaluationSheet getSheet(int sheetIndex) {
		return _workbook.getSheet(sheetIndex);
	}
	
	/* package */ EvaluationWorkbook getWorkbook() {
		return _workbook;
	}

	/* package */ EvaluationName getName(String name, int sheetIndex) {
        NamePtg namePtg = _workbook.getName(name, sheetIndex).createPtg();

		if(namePtg == null) {
			return null;
		} else {
			return _workbook.getName(namePtg);
		}
	}

	private static boolean isDebugLogEnabled() {
		return LOG.check(POILogger.DEBUG);
	}
	private static boolean isInfoLogEnabled() {
		return LOG.check(POILogger.INFO);
	}
	private static void logDebug(String s) {
		if (isDebugLogEnabled()) {
			LOG.log(POILogger.DEBUG, s);
		}
	}
	private static void logInfo(String s) {
		if (isInfoLogEnabled()) {
			LOG.log(POILogger.INFO, s);
		}
	}
	/* package */ void attachToEnvironment(CollaboratingWorkbooksEnvironment collaboratingWorkbooksEnvironment, EvaluationCache cache, int workbookIx) {
		_collaboratingWorkbookEnvironment = collaboratingWorkbooksEnvironment;
		_cache = cache;
		_workbookIx = workbookIx;
	}
	/* package */ CollaboratingWorkbooksEnvironment getEnvironment() {
		return _collaboratingWorkbookEnvironment;
	}

	/**
	 * Discards the current workbook environment and attaches to the default 'empty' environment.
	 * Also resets evaluation cache.
	 */
	/* package */ void detachFromEnvironment() {
		_collaboratingWorkbookEnvironment = CollaboratingWorkbooksEnvironment.EMPTY;
		_cache = new EvaluationCache(_evaluationListener);
		_workbookIx = 0;
	}
	/**
	 * @return the evaluator for another workbook which is part of the same {@link CollaboratingWorkbooksEnvironment}
	 */
	/* package */ WorkbookEvaluator getOtherWorkbookEvaluator(String workbookName) throws WorkbookNotFoundException {
		return _collaboratingWorkbookEnvironment.getWorkbookEvaluator(workbookName);
	}

	/* package */ IEvaluationListener getEvaluationListener() {
		return _evaluationListener;
	}

	/**
	 * Should be called whenever there are changes to input cells in the evaluated workbook.
	 * Failure to call this method after changing cell values will cause incorrect behaviour
	 * of the evaluate~ methods of this class
	 */
	public void clearAllCachedResultValues() {
		_cache.clear();
		_sheetIndexesBySheet.clear();
	}

	/**
	 * Should be called to tell the cell value cache that the specified (value or formula) cell
	 * has changed.
	 */
	public void notifyUpdateCell(EvaluationCell cell) {
		int sheetIndex = getSheetIndex(cell.getSheet());
		_cache.notifyUpdateCell(_workbookIx, sheetIndex, cell);
	}
	/**
	 * Should be called to tell the cell value cache that the specified cell has just been
	 * deleted.
	 */
	public void notifyDeleteCell(EvaluationCell cell) {
		int sheetIndex = getSheetIndex(cell.getSheet());
		_cache.notifyDeleteCell(_workbookIx, sheetIndex, cell);
	}
	
	private int getSheetIndex(EvaluationSheet sheet) {
		Integer result = _sheetIndexesBySheet.get(sheet);
		if (result == null) {
			int sheetIndex = _workbook.getSheetIndex(sheet);
			if (sheetIndex < 0) {
				throw new RuntimeException("Specified sheet from a different book");
			}
			result = Integer.valueOf(sheetIndex);
			_sheetIndexesBySheet.put(sheet, result);
		}
		return result.intValue();
	}

	public ValueEval evaluate(EvaluationCell srcCell) {
		int sheetIndex = getSheetIndex(srcCell.getSheet());
		return evaluateAny(srcCell, sheetIndex, srcCell.getRowIndex(), srcCell.getColumnIndex(), new EvaluationTracker(_cache));
	}

	/**
	 * Case-insensitive.
	 * @return -1 if sheet with specified name does not exist
	 */
	/* package */ int getSheetIndex(String sheetName) {
		Integer result = _sheetIndexesByName.get(sheetName);
		if (result == null) {
			int sheetIndex = _workbook.getSheetIndex(sheetName);
			if (sheetIndex < 0) {
				return -1;
			}
			result = Integer.valueOf(sheetIndex);
			_sheetIndexesByName.put(sheetName, result);
		}
		return result.intValue();
	}
	
	/* package */ int getSheetIndexByExternIndex(int externSheetIndex) {
	   return _workbook.convertFromExternSheetIndex(externSheetIndex);
	}

	/* package */ int getLastSheetIndexByExternIndex(int externSheetIndex) {
		   return _workbook.convertLastIndexFromExternSheetIndex(externSheetIndex);
		}

	/**
	 * @return never <code>null</code>, never {@link BlankEval}
	 */
	private ValueEval evaluateAny(EvaluationCell srcCell, int sheetIndex,
				int rowIndex, int columnIndex, EvaluationTracker tracker) {

		// avoid tracking dependencies to cells that have constant definition
		boolean shouldCellDependencyBeRecorded = _stabilityClassifier == null ? true
					: !_stabilityClassifier.isCellFinal(sheetIndex, rowIndex, columnIndex);
		if (srcCell == null || srcCell.getCellType() != Cell.CELL_TYPE_FORMULA) {
			ValueEval result = getValueFromNonFormulaCell(srcCell);
			if (shouldCellDependencyBeRecorded) {
				tracker.acceptPlainValueDependency(_workbookIx, sheetIndex, rowIndex, columnIndex, result);
			}
			return result;
		}

		FormulaCellCacheEntry cce = _cache.getOrCreateFormulaCellEntry(srcCell);
		if (shouldCellDependencyBeRecorded || cce.isInputSensitive()) {
			tracker.acceptFormulaDependency(cce);
		}
		IEvaluationListener evalListener = _evaluationListener;
		ValueEval result;
		if (cce.getValue() == null) {
			if (!tracker.startEvaluate(cce)) {
				return ErrorEval.CIRCULAR_REF_ERROR;
			}
			OperationEvaluationContext ec = new OperationEvaluationContext(this, _workbook, sheetIndex, rowIndex, columnIndex, tracker);

			try {

				Ptg[] ptgs = _workbook.getFormulaTokens(srcCell);
				if (evalListener == null) {
					result = evaluateFormula(ec, ptgs, false, false);
				} else {
					evalListener.onStartEvaluate(srcCell, cce);
					result = evaluateFormula(ec, ptgs, false, false);
					evalListener.onEndEvaluate(cce, result);
				}

				tracker.updateCacheResult(result);
			}
			 catch (NotImplementedException e) {
				throw addExceptionInfo(e, sheetIndex, rowIndex, columnIndex);
			 } catch (RuntimeException re) {
				 if (re.getCause() instanceof WorkbookNotFoundException && _ignoreMissingWorkbooks) {
 					logInfo(re.getCause().getMessage() + " - Continuing with cached value!");
 					switch(srcCell.getCachedFormulaResultType()) {
	 					case Cell.CELL_TYPE_NUMERIC:
	 						result = new NumberEval(srcCell.getNumericCellValue());
	 						break;
	 					case Cell.CELL_TYPE_STRING:
	 						result =  new StringEval(srcCell.getStringCellValue());
	 						break;
	 					case Cell.CELL_TYPE_BLANK:
	 						result = BlankEval.instance;
	 						break;
	 					case Cell.CELL_TYPE_BOOLEAN:
	 						result =  BoolEval.valueOf(srcCell.getBooleanCellValue());
	 						break;
	 					case Cell.CELL_TYPE_ERROR:
							result =  ErrorEval.valueOf(srcCell.getErrorCellValue());
							break;
	 					case Cell.CELL_TYPE_FORMULA:
						default:
							throw new RuntimeException("Unexpected cell type '" + srcCell.getCellType()+"' found!");
 					}
				 } else {
					 throw re;
				 }
			 } finally {
				tracker.endEvaluate(cce);
			}
		} else {
			if(evalListener != null) {
				evalListener.onCacheHit(sheetIndex, rowIndex, columnIndex, cce.getValue());
			}
			return cce.getValue();
		}
		if (isDebugLogEnabled()) {
			String sheetName = getSheetName(sheetIndex);
			CellReference cr = new CellReference(rowIndex, columnIndex);
			logDebug("Evaluated " + sheetName + "!" + cr.formatAsString() + " to " + result.toString());
		}
		// Usually (result === cce.getValue())
		// But sometimes: (result==ErrorEval.CIRCULAR_REF_ERROR, cce.getValue()==null)
		// When circular references are detected, the cache entry is only updated for
		// the top evaluation frame
		return result;
	}

	/**
	 * Adds the current cell reference to the exception for easier debugging.
	 * Would be nice to get the formula text as well, but that seems to require
	 * too much digging around and casting to get the FormulaRenderingWorkbook.
	 */
	private NotImplementedException addExceptionInfo(NotImplementedException inner, int sheetIndex, int rowIndex, int columnIndex) {

		try {
			String sheetName = _workbook.getSheetName(sheetIndex);
			CellReference cr = new CellReference(sheetName, rowIndex, columnIndex, false, false);
			String msg =  "Error evaluating cell " + cr.formatAsString();
			return new NotImplementedException(msg, inner);
		} catch (Exception e) {
			// avoid bombing out during exception handling
			e.printStackTrace();
			return inner; // preserve original exception
		}
	}
	/**
	 * Gets the value from a non-formula cell.
	 * @param cell may be <code>null</code>
	 * @return {@link BlankEval} if cell is <code>null</code> or blank, never <code>null</code>
	 */
	/* package */ static ValueEval getValueFromNonFormulaCell(EvaluationCell cell) {
		if (cell == null) {
			return BlankEval.instance;
		}
		int cellType = cell.getCellType();
		switch (cellType) {
			case Cell.CELL_TYPE_NUMERIC:
				return new NumberEval(cell.getNumericCellValue());
			case Cell.CELL_TYPE_STRING:
				return new StringEval(cell.getStringCellValue());
			case Cell.CELL_TYPE_BOOLEAN:
				return BoolEval.valueOf(cell.getBooleanCellValue());
			case Cell.CELL_TYPE_BLANK:
				return BlankEval.instance;
			case Cell.CELL_TYPE_ERROR:
				return ErrorEval.valueOf(cell.getErrorCellValue());
		}
		throw new RuntimeException("Unexpected cell type (" + cellType + ")");
	}
	//20110324, henrichen@zkoss.org: after process the ValueEval 
	private ValueEval postProcessValueEval(OperationEvaluationContext ec, ValueEval opResult, boolean eval) {
		if (_dependencyTracker != null) {
			opResult = _dependencyTracker.postProcessValueEval(ec, opResult, eval);
		}
		return opResult;
	}

	//20110324, henrichen@zkoss.org: constructs the dependency DAG per the given formula
	private void addDependency(OperationEvaluationContext ec, Ptg[] ptgs) {
		if (_dependencyTracker != null) {
			_dependencyTracker.addDependency(ec, ptgs);
		}
	}
	// visibility raised for testing
	/* package */ ValueEval evaluateFormula(OperationEvaluationContext ec, Ptg[] ptgs, boolean ignoreDependency, boolean ignoreDereference) {
		if (!ignoreDependency)
			addDependency(ec, ptgs); //20110324, henrichen@zkoss.org: construct the dependency DAG per this formula (bug#290)
		
		Stack<ValueEval> stack = new Stack<ValueEval>();
		for (int i = 0, iSize = ptgs.length; i < iSize; i++) {

			// since we don't know how to handle these yet :(
			Ptg ptg = ptgs[i];
			if (ptg instanceof AttrPtg) {
				AttrPtg attrPtg = (AttrPtg) ptg;
				if (attrPtg.isSum()) {
					// Excel prefers to encode 'SUM()' as a tAttr token, but this evaluator
					// expects the equivalent function token
					ptg = FuncVarPtg.SUM;
				}
				if (attrPtg.isOptimizedChoose()) {
					ValueEval arg0 = stack.pop();
					int[] jumpTable = attrPtg.getJumpTable();
					int dist;
					int nChoices = jumpTable.length;
					try {
						int switchIndex = Choose.evaluateFirstArg(arg0, ec.getRowIndex(), ec.getColumnIndex());
						if (switchIndex<1 || switchIndex > nChoices) {
							stack.push(ErrorEval.VALUE_INVALID);
							dist = attrPtg.getChooseFuncOffset() + 4; // +4 for tFuncFar(CHOOSE)
						} else {
							dist = jumpTable[switchIndex-1];
						}
					} catch (EvaluationException e) {
						stack.push(e.getErrorEval());
						dist = attrPtg.getChooseFuncOffset() + 4; // +4 for tFuncFar(CHOOSE)
					}
					// Encoded dist for tAttrChoose includes size of jump table, but
					// countTokensToBeSkipped() does not (it counts whole tokens).
					dist -= nChoices*2+2; // subtract jump table size
					i+= countTokensToBeSkipped(ptgs, i, dist);
					continue;
				}
				if (attrPtg.isOptimizedIf()) {
					ValueEval arg0 = stack.pop();
					boolean evaluatedPredicate;
					try {
						evaluatedPredicate = IfFunc.evaluateFirstArg(arg0, ec.getRowIndex(), ec.getColumnIndex());
					} catch (EvaluationException e) {
						stack.push(e.getErrorEval());
						int dist = attrPtg.getData();
						i+= countTokensToBeSkipped(ptgs, i, dist);
						attrPtg = (AttrPtg) ptgs[i];
						dist = attrPtg.getData()+1;
						i+= countTokensToBeSkipped(ptgs, i, dist);
						continue;
					}
					if (evaluatedPredicate) {
						// nothing to skip - true param folows
					} else {
						int dist = attrPtg.getData();
						i+= countTokensToBeSkipped(ptgs, i, dist);
						Ptg nextPtg = ptgs[i+1];
						if (ptgs[i] instanceof AttrPtg && nextPtg instanceof FuncVarPtg) {
							// this is an if statement without a false param (as opposed to MissingArgPtg as the false param)
							i++;
							stack.push(BoolEval.FALSE);
						}
					}
					continue;
				}
				if (attrPtg.isSkip()) {
					int dist = attrPtg.getData()+1;
					i+= countTokensToBeSkipped(ptgs, i, dist);
					if (stack.peek() == MissingArgEval.instance) {
						stack.pop();
						stack.push(BlankEval.instance);
					}
					continue;
				}
			}
			if (ptg instanceof ControlPtg) {
				// skip Parentheses, Attr, etc
				continue;
			}
			if (ptg instanceof MemFuncPtg || ptg instanceof MemAreaPtg) {
				// can ignore, rest of tokens for this expression are in OK RPN order
				continue;
			}
			if (ptg instanceof MemErrPtg) {
				continue;
			}

			ValueEval opResult;
			if (ptg instanceof OperationPtg) {
				OperationPtg optg = (OperationPtg) ptg;

				if (optg instanceof UnionPtg) { continue; }


				int numops = optg.getNumberOfOperands();
				ValueEval[] ops = new ValueEval[numops];

				// storing the ops in reverse order since they are popping
				for (int j = numops - 1; j >= 0; j--) {
					ValueEval p = stack.pop();
					//20101115, henrichen@zkoss.org: add dependency before operation
					//FuncVarPtg, the NamePtg(functionname) should be as is
					p = postProcessValueEval(ec, p, !(optg instanceof FuncVarPtg) || j > 0); 
					ops[j] = p;
				}
//				logDebug("invoke " + operation + " (nAgs=" + numops + ")");
				opResult = OperationEvaluatorFactory.evaluate(optg, ops, ec);
				opResult = postProcessValueEval(ec, opResult, true);
			} else {
				opResult = getEvalForPtg(ptg, ec);
				opResult = postProcessValueEval(ec, opResult, false);
			}
			if (opResult == null) {
				throw new RuntimeException("Evaluation result must not be null");
			}
//			logDebug("push " + opResult);
			stack.push(opResult);
		}

		ValueEval value = stack.pop();
		if (!stack.isEmpty()) {
			throw new IllegalStateException("evaluation stack not empty");
		}
		value = postProcessValueEval(ec, value, true); //20101115, henrichen@zkoss.org: might be simple one operand formula
		return ignoreDereference ? value : dereferenceResult(value, ec.getRowIndex(), ec.getColumnIndex());
	}
	/**
	 * Calculates the number of tokens that the evaluator should skip upon reaching a tAttrSkip.
	 *
	 * @return the number of tokens (starting from <tt>startIndex+1</tt>) that need to be skipped
	 * to achieve the specified <tt>distInBytes</tt> skip distance.
	 */
	private static int countTokensToBeSkipped(Ptg[] ptgs, int startIndex, int distInBytes) {
		int remBytes = distInBytes;
		int index = startIndex;
		while (remBytes != 0) {
			index++;
			remBytes -= ptgs[index].getSize();
			if (remBytes < 0) {
				throw new RuntimeException("Bad skip distance (wrong token size calculation).");
			}
			if (index >= ptgs.length) {
				throw new RuntimeException("Skip distance too far (ran out of formula tokens).");
			}
		}
		return index-startIndex;
	}

	/**
	 * Dereferences a single value from any AreaEval or RefEval evaluation
	 * result. If the supplied evaluationResult is just a plain value, it is
	 * returned as-is.
	 *
	 * @return a {@link NumberEval}, {@link StringEval}, {@link BoolEval}, or
	 *         {@link ErrorEval}. Never <code>null</code>. {@link BlankEval} is
	 *         converted to {@link NumberEval#ZERO}
	 */
	public static ValueEval dereferenceResult(ValueEval evaluationResult, int srcRowNum, int srcColNum) {
		ValueEval value;
		try {
			value = OperandResolver.getMultipleValue(evaluationResult, srcRowNum, srcColNum); //20111125, henrichen@zkoss.org: handle array value  
				//OperandResolver.getSingleValue(evaluationResult, srcRowNum, srcColNum);
		} catch (EvaluationException e) {
			return e.getErrorEval();
		}
		if (value == BlankEval.instance) {
			// Note Excel behaviour here. A blank final final value is converted to zero.
			return NumberEval.ZERO;
			// Formulas _never_ evaluate to blank.  If a formula appears to have evaluated to
			// blank, the actual value is empty string. This can be verified with ISBLANK().
		}
		return value;
	}


	/**
	 * returns an appropriate Eval impl instance for the Ptg. The Ptg must be
	 * one of: Area3DPtg, AreaPtg, ReferencePtg, Ref3DPtg, IntPtg, NumberPtg,
	 * StringPtg, BoolPtg <br/>special Note: OperationPtg subtypes cannot be
	 * passed here!
	 */
	/*package*/ ValueEval getEvalForPtg(Ptg ptg, OperationEvaluationContext ec) { //20110324, henrichen@zkoss.org: raise access right
		//  consider converting all these (ptg instanceof XxxPtg) expressions to (ptg.getClass() == XxxPtg.class)

		if (ptg instanceof NamePtg) {
			// named ranges, macro functions
			NamePtg namePtg = (NamePtg) ptg;
			EvaluationName nameRecord = _workbook.getName(namePtg);
			if (nameRecord.isFunctionName()) {
				return new NameEval(nameRecord.getNameText());
			}
			if (nameRecord.hasFormula()) {
				return evaluateNameFormula(nameRecord.getNameDefinition(), ec);
			}

			return new NameEval(nameRecord.getNameText());
			//shall be #NAME? error
			//throw new RuntimeException("Don't now how to evalate name '" + nameRecord.getNameText() + "'");
		}
		if (ptg instanceof NameXPtg) {
		   return ec.getNameXEval(((NameXPtg) ptg));
		}

		if (ptg instanceof IntPtg) {
			return new NumberEval(((IntPtg)ptg).getValue());
		}
		if (ptg instanceof NumberPtg) {
			return new NumberEval(((NumberPtg)ptg).getValue());
		}
		if (ptg instanceof StringPtg) {
			return new StringEval(((StringPtg) ptg).getValue());
		}
		if (ptg instanceof BoolPtg) {
			return BoolEval.valueOf(((BoolPtg) ptg).getValue());
		}
		if (ptg instanceof ErrPtg) {
			return ErrorEval.valueOf(((ErrPtg) ptg).getErrorCode());
		}
		if (ptg instanceof MissingArgPtg) {
			return MissingArgEval.instance;
		}
		if (ptg instanceof AreaErrPtg ||ptg instanceof RefErrorPtg
				|| ptg instanceof DeletedArea3DPtg || ptg instanceof DeletedRef3DPtg) {
				return ErrorEval.REF_INVALID;
		}
		if (ptg instanceof Ref3DPtg) {
			Ref3DPtg rptg = (Ref3DPtg) ptg;
			return ec.getRef3DEval(rptg.getRow(), rptg.getColumn(), rptg.getExternSheetIndex());
		}
		if (ptg instanceof Area3DPtg) {
			Area3DPtg aptg = (Area3DPtg) ptg;
			return ec.getArea3DEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn(), aptg.getExternSheetIndex());
		}
		if (ptg instanceof RefPtg) {
			RefPtg rptg = (RefPtg) ptg;
			return ec.getRefEval(rptg.getRow(), rptg.getColumn());
		}
		if (ptg instanceof AreaPtg) {
			AreaPtg aptg = (AreaPtg) ptg;
			return ec.getAreaEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn());
		}

		if (ptg instanceof UnknownPtg) {
			// POI uses UnknownPtg when the encoded Ptg array seems to be corrupted.
			// This seems to occur in very rare cases (e.g. unused name formulas in bug 44774, attachment 21790)
			// In any case, formulas are re-parsed before execution, so UnknownPtg should not get here
			throw new RuntimeException("UnknownPtg not allowed");
		}
		if (ptg instanceof ExpPtg) {
			// ExpPtg is used for array formulas and shared formulas.
			// it is currently unsupported, and may not even get implemented here
			throw new RuntimeException("ExpPtg currently not supported");
		}
		//20101014, henrichen@zkoss.org. bug #139: Unexpected ptg class RuntimeException when loading Excel file with special formula
		if (ptg instanceof ArrayPtg) {
			return new ArrayEval((ArrayPtg) ptg);
		}
		
		throw new RuntimeException("Unexpected ptg class (" + ptg.getClass().getName() + ")");
	}
    /**
     * YK: Used by OperationEvaluationContext to resolve indirect names.
     */
	/*package*/ ValueEval evaluateNameFormula(Ptg[] ptgs, OperationEvaluationContext ec) {
		if (ptgs.length > 1) {
			throw new RuntimeException("Complex name formulas not supported yet");
		}
		return getEvalForPtg(ptgs[0], ec);
	}

	/**
	 * Used by the lazy ref evals whenever they need to get the value of a contained cell.
	 */
	/* package */ ValueEval evaluateReference(String sheetname1, String sheetname2, int rowIndex,
			int columnIndex, EvaluationTracker tracker) {
		if ("#REF".equals(sheetname1)) { //20101213, henrichen@zkoss.org: handle reference to deleted sheet
			return ErrorEval.REF_INVALID;
		}
		final int i1 = getSheetIndex(sheetname1);
		final int i2 = getSheetIndex(sheetname2);
		final int sheetIndex1 = Math.min(i1, i2);
		final int sheetIndex2 = Math.max(i1, i2);
		final int size = sheetIndex2 - sheetIndex1 + 1;
		ValueEval[] results = new ValueEval[size];
		for(int j = sheetIndex1, k=0; j <= sheetIndex2; ++j, ++k) {
			final EvaluationSheet sheet = getSheet(j);
			EvaluationCell cell = sheet.getCell(rowIndex, columnIndex);
			results[k] = evaluateAny(cell, j, rowIndex, columnIndex, tracker);
		}
		return size > 1 ? new ValuesEval(results) : results[0];
	}
	
	public FreeRefFunction findUserDefinedFunction(String functionName) {
		return _udfFinder.findFunction(functionName);
	}

    /**
     * Whether to ignore missing references to external workbooks and
     * use cached formula results in the main workbook instead.
     * <p>
     * In some cases exetrnal workbooks referenced by formulas in the main workbook are not avaiable.
     * With this method you can control how POI handles such missing references:
     * <ul>
     *     <li>by default ignoreMissingWorkbooks=false and POI throws {@link WorkbookNotFoundException}
     *     if an external reference cannot be resolved</li>
     *     <li>if ignoreMissingWorkbooks=true then POI uses cached formula result
     *     that already exists in the main workbook</li>
     * </ul>
     *
     * @param ignore whether to ignore missing references to external workbooks
     * @see <a href="https://issues.apache.org/bugzilla/show_bug.cgi?id=52575">Bug 52575</a> for details
     */
    public void setIgnoreMissingWorkbooks(boolean ignore){
        _ignoreMissingWorkbooks = ignore;
    }

    /**
     * Return a collection of functions that POI can evaluate
     *
     * @return names of functions supported by POI
     */
    public static Collection<String> getSupportedFunctionNames(){
        Collection<String> lst = new TreeSet<String>();
        lst.addAll(FunctionEval.getSupportedFunctionNames());
        lst.addAll(AnalysisToolPak.getSupportedFunctionNames());
        return lst;
    }

    /**
     * Return a collection of functions that POI does not support
     *
     * @return names of functions NOT supported by POI
     */
    public static Collection<String> getNotSupportedFunctionNames(){
        Collection<String> lst = new TreeSet<String>();
        lst.addAll(FunctionEval.getNotSupportedFunctionNames());
        lst.addAll(AnalysisToolPak.getNotSupportedFunctionNames());
        return lst;
    }

    /**
     * Register a ATP function in runtime.
     *
     * @param name  the function name
     * @param func  the functoin to register
     * @throws IllegalArgumentException if the function is unknown or already  registered.
     * @since 3.8 beta6
     */
    public static void registerFunction(String name, FreeRefFunction func){
        AnalysisToolPak.registerFunction(name, func);
    }

    /**
     * Register a function in runtime.
     *
     * @param name  the function name
     * @param func  the functoin to register
     * @throws IllegalArgumentException if the function is unknown or already  registered.
     * @since 3.8 beta6
     */
    public static void registerFunction(String name, Function func){
        FunctionEval.registerFunction(name, func);
    }

	//20111124, henrichen@zkoss.org: given sheet index, formula text, return evaluated results
	public ValueEval evaluate(int sheetIndex, String formula, boolean ignoreDereference) {
		return evaluateAny(formula, sheetIndex, 0, 0, new EvaluationTracker(_cache), ignoreDereference);
	}

	//20111124, henrichen@zkoss.org: given sheet index
	/**
	 * @return never <code>null</code>, never {@link BlankEval}
	 */
	private ValueEval evaluateAny(String formula, int sheetIndex,
				int rowIndex, int columnIndex, EvaluationTracker tracker, boolean ignoreDeference) {
		final EvaluationCell virtualCell = new EvaluationCell() { //virtual EvaluationCell, it is used as a key only
			@Override
			public Object getIdentityKey() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EvaluationSheet getSheet() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getRowIndex() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getColumnIndex() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getCellType() {
				return Cell.CELL_TYPE_FORMULA;
			}

			@Override
			public double getNumericCellValue() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getStringCellValue() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean getBooleanCellValue() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public int getErrorCellValue() {
				// TODO Auto-generated method stub
				return 0;
			} //virtual cell

			@Override
			public int getCachedFormulaResultType() {
				// TODO Auto-generated method stub
				return Cell.CELL_TYPE_BLANK;
			}
			
		};
		FormulaCellCacheEntry cce = _cache.getOrCreateFormulaCellEntry(virtualCell);
		ValueEval result;
		if (cce.getValue() == null) {
			if (!tracker.startEvaluate(cce)) {
				return ErrorEval.CIRCULAR_REF_ERROR;
			}
			OperationEvaluationContext ec = new OperationEvaluationContext(this, _workbook, sheetIndex, rowIndex, columnIndex, tracker);
			try {
				Ptg[] ptgs = _workbook.getFormulaTokens(sheetIndex, formula);
				result = evaluateFormula(ec, ptgs, true, ignoreDeference);
				tracker.updateCacheResult(result);
			} catch (NotImplementedException e) {
				throw addExceptionInfo(e, sheetIndex, rowIndex, columnIndex);
			} finally {
				tracker.endEvaluate(cce);
				_cache.notifyDeleteCell(0, sheetIndex, virtualCell); //clear the cache since it is for temporary use only
			}
		} else {
			return cce.getValue();
		}
		if (isDebugLogEnabled()) {
			String sheetName = getSheetName(sheetIndex);
			CellReference cr = new CellReference(rowIndex, columnIndex);
			logDebug("Evaluated " + sheetName + "!" + cr.formatAsString() + " to " + result.toString());
		}
		// Usually (result === cce.getValue())
		// But sometimes: (result==ErrorEval.CIRCULAR_REF_ERROR, cce.getValue()==null)
		// When circular references are detected, the cache entry is only updated for
		// the top evaluation frame
		return result;
	}
}
