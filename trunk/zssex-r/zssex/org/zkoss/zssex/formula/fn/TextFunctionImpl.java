package org.zkoss.zssex.formula.fn;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.OperandResolver;
import org.zkoss.poi.ss.formula.eval.StringEval;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.Function;
import org.zkoss.poi.ss.formula.functions.NumericFunction;
import org.zkoss.poi.ss.formula.functions.TextFunction;
import org.zkoss.poi.ss.formula.functions.TextFunctionHelper;
import org.zkoss.xel.fn.CommonFns;

public class TextFunctionImpl
{
  public static final Function CHAR = new TextFunction()
  {
    protected ValueEval evaluateFunc(ValueEval[] args, int srcRowIndex, int srcColumnIndex) throws EvaluationException
    {
      int arg = TextFunctionHelper.evaluateIntArg(args[0], srcRowIndex, srcColumnIndex);
      return new StringEval(Character.toString((char)arg));
    }

  };
  public static final Function CODE = new NumericFunction()
  {
    protected double eval(ValueEval[] args, int srcCellRow, int srcCellCol)
      throws EvaluationException
    {
      String arg = TextFunctionHelper.evaluateStringArg(args[0], srcCellRow, srcCellCol);
      return arg.charAt(0);
    }

  };
  public static final Function FIXED = new TextFunction()
  {
    protected ValueEval evaluateFunc(ValueEval[] args, int srcRowIndex, int srcColumnIndex) throws EvaluationException
    {
      BigDecimal number = new BigDecimal(TextFunctionHelper.evaluateDoubleArg(args[0], srcRowIndex, srcColumnIndex));
      int scale = 2;
      String s = "#,##0";
      if (args.length == 2) {
        scale = TextFunctionHelper.evaluateIntArg(args[1], srcRowIndex, srcColumnIndex);
      } else if (args.length == 3) {
        scale = TextFunctionHelper.evaluateIntArg(args[1], srcRowIndex, srcColumnIndex);
        ValueEval ve = OperandResolver.getSingleValue(args[2], srcRowIndex, srcColumnIndex);
        Boolean noCommas = OperandResolver.coerceValueToBoolean(ve, false);
        if (noCommas.booleanValue())
          s = "###";
      }

      StringBuffer formatText = new StringBuffer(s);
      if (scale > 0)
        formatText.append(".");
      for (int i = 1; i < scale + 1; ++i)
        formatText.append("0");

      return new StringEval(new DecimalFormat(formatText.toString()).format(number.setScale(scale, 4)));
    }

  };
  public static final Function PROPER = new TextFunction()
  {
    protected ValueEval evaluateFunc(ValueEval[] args, int srcRowIndex, int srcColumnIndex) throws EvaluationException
    {
      StringBuffer text = new StringBuffer(TextFunctionHelper.evaluateStringArg(args[0], srcRowIndex, srcColumnIndex));
      text.setCharAt(0, Character.toUpperCase(text.charAt(0)));
      for (int i = 1; i < text.length(); ++i)
        if (Character.isLetter(text.charAt(i)))
          if (!(Character.isLetter(text.charAt(i - 1))))
            text.setCharAt(i, Character.toUpperCase(text.charAt(i)));
          else if (Character.isUpperCase(text.charAt(i)))
            text.setCharAt(i, Character.toLowerCase(text.charAt(i)));



      return new StringEval(text.toString());
    }

  };
  public static final Function REPLACE = new TextFunction()
  {
    protected ValueEval evaluateFunc(ValueEval[] args, int srcRowIndex, int srcColumnIndex) throws EvaluationException
    {
      StringBuffer text = new StringBuffer(TextFunctionHelper.evaluateStringArg(args[0], srcRowIndex, srcColumnIndex));
      int start = TextFunctionHelper.evaluateIntArg(args[1], srcRowIndex, srcColumnIndex) - 1;
      int end = TextFunctionHelper.evaluateIntArg(args[2], srcRowIndex, srcColumnIndex) + start;
      String newChar = TextFunctionHelper.evaluateStringArg(args[0], srcRowIndex, srcColumnIndex);
      return new StringEval(text.replace(start, end, newChar).toString());
    }

  };
  public static final Function REPT = new TextFunction()
  {
    protected ValueEval evaluateFunc(ValueEval[] args, int srcRowIndex, int srcColumnIndex) throws EvaluationException
    {
      String text = TextFunctionHelper.evaluateStringArg(args[0], srcRowIndex, srcColumnIndex);
      String result = "";
      int ntimes = TextFunctionHelper.evaluateIntArg(args[1], srcRowIndex, srcColumnIndex);
      for (int i = 0; i < ntimes; ++i)
        result = result.concat(text);

      return new StringEval(result);
    }

  };
  public static final Function SUBSTITUTE = new TextFunction()
  {
    protected ValueEval evaluateFunc(ValueEval[] args, int srcRowIndex, int srcColumnIndex) throws EvaluationException
    {
      StringBuffer text = new StringBuffer(TextFunctionHelper.evaluateStringArg(args[0], srcRowIndex, srcColumnIndex));
      String oldText = TextFunctionHelper.evaluateStringArg(args[1], srcRowIndex, srcColumnIndex);
      String newText = TextFunctionHelper.evaluateStringArg(args[2], srcRowIndex, srcColumnIndex);
      int start = text.indexOf(oldText);
      String result = text.toString();
      if (start != -1) {
        int i;
        int end = start + oldText.length();
        if (args.length == 4)
          for (i = 0; i < CommonFns.toInt(args[3]) - 1; ++i) {
            start = text.indexOf(oldText, end);
            end = start + oldText.length();
          }

        result = text.replace(start, end, newText).toString();
      }
      return new StringEval(result);
    }
  };
}