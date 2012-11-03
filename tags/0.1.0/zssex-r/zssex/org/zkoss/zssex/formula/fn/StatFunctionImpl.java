package org.zkoss.zssex.formula.fn;

import java.util.List;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.BinomialDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.DistributionFactory;
import org.apache.commons.math.distribution.ExponentialDistribution;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.GammaDistribution;
import org.apache.commons.math.distribution.HypergeometricDistribution;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.PoissonDistribution;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.WeibullDistribution;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.zkoss.poi.ss.formula.AreaEvalHelper;
import org.zkoss.poi.ss.formula.eval.ErrorEval;
import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.OperandResolver;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.Function;
import org.zkoss.poi.ss.formula.functions.NumericFunction;

public class StatFunctionImpl
{
  public static final Function AVERAGEA = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      List ls = UtilFns.toList(args, srcRowIndex, srcColumnIndex);
      if (ls.isEmpty()) {
        throw new EvaluationException(ErrorEval.DIV_ZERO);
      }

      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(UtilFns.getStats(UtilFns.toDoubleArray(ls)).getMean());
    }

  };
  public static final Function BINOMDIST = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double number = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      int trails = (int)NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      double p_s = NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
      ValueEval ve = OperandResolver.getSingleValue(args[3], srcRowIndex, srcColumnIndex);
      java.lang.Boolean isCumulative = OperandResolver.coerceValueToBoolean(ve, false);
      if ((number > trails) || (number < 0.0D))
        throw new EvaluationException(ErrorEval.NUM_ERROR);

      DistributionFactory factory = DistributionFactory.newInstance();
      BinomialDistribution bd = factory.createBinomialDistribution(trails, p_s);
      if (isCumulative.booleanValue())
        try {
          return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(bd.cumulativeProbability(number));
        } catch (MathException e) {
          throw new EvaluationException(ErrorEval.NUM_ERROR);
        }

      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(bd.probability(number));
    }

  };
  public static final Function CHIDIST = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double x = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double df = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      if (x < 0.0D)
        throw new EvaluationException(ErrorEval.NUM_ERROR);

      DistributionFactory factory = DistributionFactory.newInstance();
      try {
        return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(1.0D - factory.createChiSquareDistribution(df).cumulativeProbability(x));
      } catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }

    }

  };
  public static final Function CHIINV = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double p = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double df = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      if ((p < 0.0D) || (p > 1.0D))
        throw new EvaluationException(ErrorEval.NUM_ERROR);

      DistributionFactory factory = DistributionFactory.newInstance();
      try {
        return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(factory.createChiSquareDistribution(df).inverseCumulativeProbability(1.0D - p));
      } catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }

    }

  };
  public static final Function EXPONDIST = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex) throws EvaluationException
    {
      double x = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double lambda = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      double mean = 1.0D / lambda;
      ValueEval ve = OperandResolver.getSingleValue(args[2], srcRowIndex, srcColumnIndex);
      java.lang.Boolean isCumulative = OperandResolver.coerceValueToBoolean(ve, false);
      DistributionFactory factory = DistributionFactory.newInstance();
      try {
        if (isCumulative.booleanValue())
          return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(factory.createExponentialDistribution(mean).cumulativeProbability(x));

        return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue((1.0D - factory.createExponentialDistribution(mean).cumulativeProbability(x)) / mean);
      }
      catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
    }

  };
  public static final Function FDIST = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double result;
      double x = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double df = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      double ddf = NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
      DistributionFactory factory = DistributionFactory.newInstance();
      try
      {
        result = factory.createFDistribution(df, ddf).cumulativeProbability(x);
      } catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(result);
    }

  };
  public static final Function FINV = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double result;
      double p = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double df = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      double ddf = NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
      DistributionFactory factory = DistributionFactory.newInstance();
      try
      {
        result = factory.createFDistribution(df, ddf).inverseCumulativeProbability(p);
      } catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(result);
    }

  };
  public static final Function GAMMADIST = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double x = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double alpha = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      double beta = NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
      ValueEval ve = OperandResolver.getSingleValue(args[3], srcRowIndex, srcColumnIndex);
      java.lang.Boolean isCumulative = OperandResolver.coerceValueToBoolean(ve, false);
      DistributionFactory factory = DistributionFactory.newInstance();
      try {
        if (isCumulative.booleanValue())
          return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(factory.createGammaDistribution(alpha, beta).cumulativeProbability(x));

        return new Integer(-1).intValue();
      }
      catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
    }

  };
  public static final Function GAMMAINV = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double result;
      double p = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double alpha = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      double beta = NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
      DistributionFactory factory = DistributionFactory.newInstance();
      try
      {
        result = factory.createGammaDistribution(alpha, beta).inverseCumulativeProbability(p);
      } catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(result);
    }

  };
  public static final Function GAMMALN = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double x = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(Gamma.logGamma(x));
    }

  };
  public static final Function GEOMEAN = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(UtilFns.getStats(UtilFns.toDoubleArray(UtilFns.toList(args, srcRowIndex, srcColumnIndex))).getGeometricMean());
    }

  };
  public static final Function HARMEAN = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex) throws EvaluationException
    {
      double g = UtilFns.getStats(UtilFns.toDoubleArray(UtilFns.toList(args, srcRowIndex, srcColumnIndex))).getGeometricMean();
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(Math.sqrt(g) / UtilFns.getStats(UtilFns.toDoubleArray(UtilFns.toList(args, srcRowIndex, srcColumnIndex))).getMean());
    }

  };
  public static final Function HYPGEOMDIST = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      int s = (int)NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      int ns = (int)NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      int p = (int)NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
      int np = (int)NumericFunction.singleOperandEvaluate(args[3], srcRowIndex, srcColumnIndex);
      DistributionFactory factory = DistributionFactory.newInstance();
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(factory.createHypergeometricDistribution(np, p, ns).probability(s));
    }

  };
  public static final Function INTERCEPT = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      throw new EvaluationException(ErrorEval.NUM_ERROR);
    }

  };
  public static final Function KURT = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex) throws EvaluationException
    {
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(UtilFns.getStats(UtilFns.toDoubleArray(UtilFns.toList(args, srcRowIndex, srcColumnIndex))).getKurtosis());
    }

  };
  public static final Function NORMDIST = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double result;
      double x = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double mean = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      double sDev = NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
      DistributionFactory factory = DistributionFactory.newInstance();
      try
      {
        result = factory.createNormalDistribution(mean, sDev).cumulativeProbability(x);
      } catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(result);
    }

  };
  public static final Function POISSON = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double x = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double mean = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      ValueEval ve = OperandResolver.getSingleValue(args[2], srcRowIndex, srcColumnIndex);
      java.lang.Boolean isCumulative = OperandResolver.coerceValueToBoolean(ve, false);
      DistributionFactory factory = DistributionFactory.newInstance();
      PoissonDistribution pd = factory.createPoissonDistribution(mean);
      try {
        if (isCumulative.booleanValue())
          return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(pd.cumulativeProbability(x));

        return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(pd.probability(x));
      }
      catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
    }

  };
  public static final Function SKEW = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(UtilFns.getStats(UtilFns.toDoubleArray(UtilFns.toList(args, srcRowIndex, srcColumnIndex))).getSkewness());
    }

  };
  public static final Function SLOPE = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      List l0 = AreaEvalHelper.toDoubleList(args[0], srcRowIndex, srcColumnIndex);
      List l1 = AreaEvalHelper.toDoubleList(args[1], srcRowIndex, srcColumnIndex);
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(UtilFns.getRegre(UtilFns.toDoubleArray(l1), UtilFns.toDoubleArray(l0)).getSlope());
    }

  };
  public static final Function STDEV = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(UtilFns.getStats(UtilFns.toDoubleArray(UtilFns.toList(args, srcRowIndex, srcColumnIndex))).getStandardDeviation());
    }

  };
  public static final Function STDEVP = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      throw new EvaluationException(ErrorEval.NUM_ERROR);
    }

  };
  public static final Function TDIST = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double result;
      double x = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double degree_freedom = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      int tails = (int)NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
      DistributionFactory factory = DistributionFactory.newInstance();
      TDistribution td = factory.createTDistribution(degree_freedom);
      try
      {
        result = 1.0D - td.cumulativeProbability(x);
      } catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(result);
    }

  };
  public static final Function TINV = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double result;
      double p = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double degree_freedom = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      DistributionFactory factory = DistributionFactory.newInstance();
      TDistribution td = factory.createTDistribution(degree_freedom);
      try
      {
        result = td.inverseCumulativeProbability(1.0D - p);
      } catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(result);
    }

  };
  public static final Function VAR = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(UtilFns.getStats(UtilFns.toDoubleArray(UtilFns.toList(args, srcRowIndex, srcColumnIndex))).getVariance());
    }

  };
  public static final Function VARP = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      throw new EvaluationException(ErrorEval.NUM_ERROR);
    }

  };
  public static final Function WEIBULL = new NumericFunction()
  {
    public double eval(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
      throws EvaluationException
    {
      double result;
      double x = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
      double alpha = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
      double beta = NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
      DistributionFactory factory = DistributionFactory.newInstance();
      WeibullDistribution wb = factory.createWeibullDistribution(alpha, beta);
      try
      {
        result = wb.cumulativeProbability(x);
      } catch (MathException e) {
        throw new EvaluationException(ErrorEval.NUM_ERROR);
      }
      return org.zkoss.poi.ss.formula.functions.NumericFunctionHelper.checkValue(result);
    }
  };
}