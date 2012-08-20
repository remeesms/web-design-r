package org.zkoss.zssex.formula;

import java.util.Date;
import org.zkoss.poi.ss.formula.OperationEvaluationContext;
import org.zkoss.poi.ss.formula.eval.BoolEval;
import org.zkoss.poi.ss.formula.eval.ErrorEval;
import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.NumberEval;
import org.zkoss.poi.ss.formula.eval.OperandResolver;
import org.zkoss.poi.ss.formula.eval.StringEval;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.FreeRefFunction;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.XelContext;
import org.zkoss.zss.model.impl.XelContextHolder;

public class ELEval
  implements FreeRefFunction
{
  public static final FreeRefFunction instance = new ELEval();
  public static final String NAME = "ELEVAL";

  public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec)
  {
    if (args.length != 1)
      return ErrorEval.NAME_INVALID;
    try
    {
      ValueEval ve = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
      String expression = OperandResolver.coerceValueToString(ve);
      XelContext ctx = XelContextHolder.getXelContext();
      Object obj = Expressions.evaluate(ctx, expression, (Class)ctx.getAttribute("zkoss.zss.CellType"));
      if (obj == null)
        return ErrorEval.NAME_INVALID;

      if (obj instanceof String)
        return new StringEval((String)obj);
      if (obj instanceof Number)
        return new NumberEval(((Number)obj).doubleValue());
      if (obj instanceof Boolean)
        return BoolEval.valueOf(((Boolean)obj).booleanValue());
      if (obj instanceof Date)
        return new NumberEval(javaMillSecondToExcelDate(((Date)obj).getTime()));
    }
    catch (EvaluationException e) {
      return e.getErrorEval();
    }

    return null;
  }

  private double javaMillSecondToExcelDate(long date) {
    return (date / 86400000L);
  }
}