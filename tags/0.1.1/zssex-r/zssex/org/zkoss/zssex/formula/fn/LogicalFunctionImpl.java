package org.zkoss.zssex.formula.fn;

import org.zkoss.poi.ss.formula.eval.ErrorEval;
import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.OperandResolver;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.Function;
import org.zkoss.poi.ss.formula.functions.TextFunction;

public class LogicalFunctionImpl
{
  public static final Function IFERROR = new TextFunction()
  {
    protected ValueEval evaluateFunc(ValueEval[] args, int srcCellRow, int srcCellCol)
      throws EvaluationException
    {
      ValueEval ve = args[0];

      if (ve instanceof ErrorEval)
        return OperandResolver.getSingleValue(args[1], srcCellRow, srcCellCol);

      return ve;
    }
  };
}