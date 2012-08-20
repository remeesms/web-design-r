package org.zkoss.zssex.formula.fn;

import org.zkoss.poi.ss.formula.eval.AreaEval;
import org.zkoss.poi.ss.formula.eval.BoolEval;
import org.zkoss.poi.ss.formula.eval.ErrorEval;
import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.NumberEval;
import org.zkoss.poi.ss.formula.eval.OperandResolver;
import org.zkoss.poi.ss.formula.eval.StringEval;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.Function;
import org.zkoss.poi.ss.formula.functions.NumericFunction;
import org.zkoss.poi.ss.formula.functions.TextFunction;

public class InfoFunctionImpl
{
  public static final Function ISERR = new TextFunction()
  {
    protected ValueEval evaluateFunc(ValueEval[] args, int srcCellRow, int srcCellCol)
      throws EvaluationException
    {
      Object arg = args[0];
      return BoolEval.valueOf((arg instanceof ErrorEval) && (arg != ErrorEval.NA));
    }

  };
  public static final Function N = new TextFunction()
  {
    protected ValueEval evaluateFunc(ValueEval[] args, int srcCellRow, int srcCellCol)
      throws EvaluationException
    {
      ValueEval ve = OperandResolver.getSingleValue(args[0], srcCellRow, srcCellCol);

      if (ve instanceof NumberEval)
        return args[0];
      if (ve instanceof StringEval)
        return new NumberEval(0.0D);
      if (ve instanceof BoolEval)
        return new NumberEval(((BoolEval)ve).getNumberValue());

      return new NumberEval(0.0D);
    }

  };
  public static final Function TYPE = new NumericFunction()
  {
    protected double eval(ValueEval[] args, int srcCellRow, int srcCellCol)
      throws EvaluationException
    {
      ValueEval ve = args[0];
      if (!(ve instanceof ErrorEval)) {
        ve = OperandResolver.getSingleValue(args[0], srcCellRow, srcCellCol);
      }

      if (ve instanceof NumberEval)
        return 1.0D;
      if (ve instanceof StringEval)
        return 2.0D;
      if (ve instanceof BoolEval)
        return 4.0D;
      if (ve instanceof ErrorEval)
        return 16.0D;
      if (ve instanceof AreaEval)
        return 64.0D;

      return 0.0D;
    }
  };
}