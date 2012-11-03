package org.zkoss.poi.ss.formula.functions;

import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.ValueEval;

public class TextFunctionHelper
{
  public static final int evaluateIntArg(ValueEval eval, int srcCellRow, int srcCellCol)
    throws EvaluationException
  {
    return TextFunction.evaluateIntArg(eval, srcCellRow, srcCellCol); }

  public static final String evaluateStringArg(ValueEval eval, int srcCellRow, int srcCellCol) throws EvaluationException {
    return TextFunction.evaluateStringArg(eval, srcCellRow, srcCellCol); }

  public static final double evaluateDoubleArg(ValueEval eval, int srcCellRow, int srcCellCol) throws EvaluationException {
    return TextFunction.evaluateDoubleArg(eval, srcCellRow, srcCellCol);
  }
}