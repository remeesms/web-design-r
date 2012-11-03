package org.zkoss.zssex.formula.fn;

import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.Function;

public class TextFns
{
  public static final ValueEval textChar(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
  {
    return TextFunctionImpl.CHAR.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval code(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return TextFunctionImpl.CODE.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval fixed(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return TextFunctionImpl.FIXED.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval proper(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return TextFunctionImpl.PROPER.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval replace(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return TextFunctionImpl.REPLACE.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval rept(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return TextFunctionImpl.REPT.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval substitute(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return TextFunctionImpl.SUBSTITUTE.evaluate(args, srcRowIndex, srcColumnIndex);
  }
}