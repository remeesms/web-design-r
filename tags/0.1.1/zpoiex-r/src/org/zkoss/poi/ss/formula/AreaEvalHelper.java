package org.zkoss.poi.ss.formula;

import java.util.LinkedList;
import java.util.List;
import org.zkoss.lang.Objects;
import org.zkoss.poi.ss.formula.eval.AreaEval;
import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.NumberEval;
import org.zkoss.poi.ss.formula.eval.StringEval;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.TextFunctionHelper;
import org.zkoss.poi.ss.formula.functions.UtilFns;

public class AreaEvalHelper
{
  public static ValueEval getRelativeValue(ValueEval valueEval, int relativeRowIndex, int relativeColumnIndex)
  {
    return ((AreaEval)valueEval).getRelativeValue(relativeRowIndex, relativeColumnIndex);
  }

  public static double[][] toDoubleMatrix(ValueEval valueEval, int relativeRowIndex, int relativeColumnIndex) throws EvaluationException {
    AreaEval areaEval;
    int row;
    int i;
    double[][] d = (double[][])null;
    if (valueEval instanceof AreaEval) {
      areaEval = (AreaEval)valueEval;
      int col = areaEval.getLastColumn() - areaEval.getFirstColumn();
      row = areaEval.getLastRow() - areaEval.getFirstRow();
      d = new double[col + 1][row + 1];
      for (i = 0; i <= col; ++i)
        for (int j = 0; j <= row; ++j)
          d[i][j] = TextFunctionHelper.evaluateDoubleArg(areaEval.getRelativeValue(j, i), relativeRowIndex, relativeColumnIndex);

    }

    return d;
  }

  public static List<Double> toDoubleList(ValueEval valueEval, int srcRowIndex, int srcColumnIndex) throws EvaluationException
  {
    AreaEval areaEval;
    int row;
    int i;
    List d = new LinkedList();
    if (valueEval instanceof AreaEval) {
      areaEval = (AreaEval)valueEval;
      int col = areaEval.getLastColumn() - areaEval.getFirstColumn();
      row = areaEval.getLastRow() - areaEval.getFirstRow();
      for (i = 0; i <= col; ++i)
        for (int j = 0; j <= row; ++j) {
          ValueEval val = areaEval.getRelativeValue(j, i);
          if (val instanceof StringEval) {
            Double a = null;
            Double dval = ((a = UtilFns.stringToDouble(((StringEval)val).getStringValue(), true)) == null) ? Objects.ZERO_DOUBLE : a;
            d.add(dval);
          } else if (val instanceof NumberEval) {
            d.add(new Double(TextFunctionHelper.evaluateDoubleArg(val, srcRowIndex, srcColumnIndex)));
          }
        }
    }

    return d;
  }
}