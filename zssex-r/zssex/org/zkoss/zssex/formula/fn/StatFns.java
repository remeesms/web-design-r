package org.zkoss.zssex.formula.fn;

import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.Function;

public class StatFns
{
  public static final ValueEval averagea(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
  {
    return StatFunctionImpl.AVERAGEA.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval binomdist(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.BINOMDIST.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval chidist(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.CHIDIST.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval chiinv(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.CHIINV.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval expondist(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.EXPONDIST.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval fdist(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.FDIST.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval finv(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.FINV.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval gammadist(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.GAMMADIST.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval gammainv(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.GAMMAINV.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval gammaln(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.GAMMALN.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval geomean(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.GEOMEAN.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval harmean(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.HARMEAN.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval hypgeomdist(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.HYPGEOMDIST.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval intercept(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.INTERCEPT.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval kurt(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.KURT.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval normdist(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.NORMDIST.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval poisson(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.POISSON.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval skew(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.SKEW.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval slope(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.SLOPE.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval stdev(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.STDEV.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval stdevp(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.STDEVP.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval tdist(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.TDIST.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval tinv(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.TINV.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval var(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.VAR.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval varp(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.VARP.evaluate(args, srcRowIndex, srcColumnIndex); }

  public static final ValueEval weibull(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
    return StatFunctionImpl.WEIBULL.evaluate(args, srcRowIndex, srcColumnIndex);
  }
}