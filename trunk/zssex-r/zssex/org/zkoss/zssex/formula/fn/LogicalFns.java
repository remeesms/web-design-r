package org.zkoss.zssex.formula.fn;

import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.Function;

public class LogicalFns
{
  public static final ValueEval iferror(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
  {
    return LogicalFunctionImpl.IFERROR.evaluate(args, srcRowIndex, srcColumnIndex);
  }
}