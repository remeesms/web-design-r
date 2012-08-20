package org.zkoss.poi.ss.formula.functions;

import org.zkoss.poi.ss.formula.eval.EvaluationException;

public class NumericFunctionHelper
{
  public static final double checkValue(double result)
    throws EvaluationException
  {
    NumericFunction.checkValue(result);
    return result;
  }
}